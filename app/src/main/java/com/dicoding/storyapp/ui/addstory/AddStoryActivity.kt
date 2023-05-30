package com.dicoding.storyapp.ui.addstory

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.ui.camera.CameraActivity
import com.dicoding.storyapp.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class AddStoryActivity : AppCompatActivity() {
    private var _binding: ActivityAddStoryBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: AddStoryViewModel

    private var getFile: File? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat: RequestBody? = null
    private var lon: RequestBody? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!REQUIRED_PERMISSIONS.all {
                    checkPermission(it)
            }) {
                Toast.makeText(
                    this,
                    getString(R.string.have_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupView()
        setupViewModel()
        playAnimation()

        if (!REQUIRED_PERMISSIONS.all {
                checkPermission(it)
            }) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS,
            )
        }

        binding?.btnCamera?.setOnClickListener { startTakePhoto() }
        binding?.btnGallery?.setOnClickListener { startGallery() }
        binding?.buttonAdd?.setOnClickListener { upload(initDialog()) }
        binding?.btnBack?.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding?.switchLocation?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLocation()
            } else {
                lat = null
                lon = null
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataStore, this)
        )[AddStoryViewModel::class.java]
    }

    private fun getLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude.toString().toRequestBody("text/plain".toMediaType())
                    lon = location.longitude.toString().toRequestBody("text/plain".toMediaType())
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        return builder.create()
    }

    private fun showLoading(isLoading: Boolean, dialog: AlertDialog) {
        if (isLoading) dialog.show() else dialog.dismiss()
    }

    private fun upload(dialog: AlertDialog) {
        val token = intent.getStringExtra(BEARER_TOKEN)
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = binding?.edAddDescription?.text.toString().trim()
                .toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            if (token != null) {
                viewModel.upload(token, imageMultipart, description, lat, lon)
                    .observe(this) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Error -> {
                                    showLoading(false, dialog)
                                    Toast.makeText(
                                        this,
                                        getString(R.string.error_connect),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                Result.Loading -> showLoading(true, dialog)
                                is Result.Success -> {
                                    showLoading(false, dialog)
                                    Toast.makeText(
                                        this,
                                        getString(R.string.upload_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackPressedDispatcher.onBackPressed()
                                }
                            }
                        }
                    }
            }
        } else {
            Toast.makeText(this, getString(R.string.insert_picture), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCamera.launch(intent)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding?.ivPreview?.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            binding?.ivPreview?.setImageURI(selectedImg)
        }
    }

    private fun playAnimation() {
        val ivPreview = ObjectAnimator.ofFloat(binding?.cvPreview, View.ALPHA, 1f).setDuration(500)
        val btnCamera = ObjectAnimator.ofFloat(binding?.btnCamera, View.ALPHA, 1f).setDuration(500)
        val btnGallery =
            ObjectAnimator.ofFloat(binding?.btnGallery, View.ALPHA, 1f).setDuration(500)
        val edDesc = ObjectAnimator.ofFloat(binding?.llDesc, View.ALPHA, 1f).setDuration(500)
        val btnUpload = ObjectAnimator.ofFloat(binding?.buttonAdd, View.ALPHA, 1f).setDuration(500)

        val btnTogether = AnimatorSet().apply {
            playTogether(btnCamera, btnGallery)
        }

        AnimatorSet().apply {
            playSequentially(ivPreview, btnTogether, edDesc, btnUpload)
            start()
        }
    }

    private fun setupView() {
        lightStatusBar(window, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val BEARER_TOKEN = "bearer_token"

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}