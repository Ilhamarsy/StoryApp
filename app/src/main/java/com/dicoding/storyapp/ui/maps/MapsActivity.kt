package com.dicoding.storyapp.ui.maps

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.ui.addstory.AddStoryActivity
import com.dicoding.storyapp.utils.ViewModelFactory
import com.dicoding.storyapp.utils.lightStatusBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: MapsViewModel

    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        setupViewModel()

        binding?.btnBack?.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataStore, this)
        )[MapsViewModel::class.java]
    }

    private fun initDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        return builder.create()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val dialog = initDialog()

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        val token = intent.getStringExtra(AddStoryActivity.BEARER_TOKEN)
        if (token != null) {
            viewModel.getStoryWithLocation(token).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Error -> {
                            showLoading(false, dialog)
                            Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT)
                                .show()
                        }
                        is Result.Loading -> showLoading(true, dialog)
                        is Result.Success -> {
                            showLoading(false, dialog)
                            val listStory = result.data.listStory
                            listStory.forEach {
                                val latLng =
                                    LatLng(it.lat?.toDouble() ?: 0.0, it.lon?.toDouble() ?: 0.0)
                                mMap.addMarker(MarkerOptions().position(latLng).title(it.name))
                                boundsBuilder.include(latLng)
                            }
                            val bounds: LatLngBounds = boundsBuilder.build()
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    300
                                )
                            )

                        }
                    }
                }
            }
        }
        setMapStyle()
    }

    private fun showLoading(isLoading: Boolean, dialog: AlertDialog) {
        if (isLoading) dialog.show() else dialog.dismiss()
    }

    private fun setupView() {
        lightStatusBar(window, true)
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "MapsActivity"

        const val BEARER_TOKEN = "bearer_token"
    }
}