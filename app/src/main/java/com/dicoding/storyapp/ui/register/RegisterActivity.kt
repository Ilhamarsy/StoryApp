package com.dicoding.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.utils.lightStatusBar
import com.dicoding.storyapp.ui.login.LoginActivity
import com.dicoding.storyapp.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        setupViewModel()
        setupAction(initDialog())
        playAnimation()
    }

    private fun initDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        return builder.create()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataStore, this)
        )[RegisterViewModel::class.java]
    }

    private fun setupAction(dialog: AlertDialog) {
        binding?.btnLogin?.setOnClickListener {
            val name = binding?.edRegisterName?.text.toString().trim()
            val email = binding?.edRegisterEmail?.text.toString().trim()
            val password = binding?.edRegisterPassword?.text.toString().trim()
            when {
                name.isEmpty() -> {
                    binding?.edRegisterName?.requestFocus()
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    binding?.edRegisterName?.requestFocus()
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding?.edRegisterName?.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() || password.length < 6 -> {
                    binding?.edRegisterPassword?.requestFocus()
                    return@setOnClickListener
                }
            }
            viewModel.register(name, email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Error -> {
                            showLoading(false, dialog)
                            Toast.makeText(
                                this,
                                getString(R.string.already_registered),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Result.Loading -> showLoading(true, dialog)
                        is Result.Success -> {
                            showLoading(false, dialog)
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.acc_created_success))
                                setMessage(getString(R.string.dialog_message))
                                setPositiveButton(getString(R.string.dialog_positive_btn)) { _, _ ->
                                    Intent(this@RegisterActivity, LoginActivity::class.java).also {
                                        startActivity(it)
                                    }
                                    finish()
                                }
                                setNegativeButton(getString(R.string.dialog_negative_btn)) { _, _ ->
                                    onBackPressedDispatcher.onBackPressed()
                                }
                                create()
                                show()
                            }
                        }
                    }
                }
            }
        }

        binding?.btnBack?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showLoading(isLoading: Boolean, dialog: AlertDialog) {
        if (isLoading) dialog.show() else dialog.dismiss()
    }

    private fun playAnimation() {
        val image = ObjectAnimator.ofFloat(binding?.imageView, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding?.textView, View.ALPHA, 1f).setDuration(500)
        val tvName = ObjectAnimator.ofFloat(binding?.tvName, View.ALPHA, 1f).setDuration(500)
        val edName =
            ObjectAnimator.ofFloat(binding?.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding?.tvEmail, View.ALPHA, 1f).setDuration(500)
        val edEmail =
            ObjectAnimator.ofFloat(binding?.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val tvPass = ObjectAnimator.ofFloat(binding?.tvPassword, View.ALPHA, 1f).setDuration(500)
        val edPass =
            ObjectAnimator.ofFloat(binding?.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val btnRegis = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).setDuration(500)

        val nameTogether = AnimatorSet().apply {
            playTogether(tvName, edName)
        }

        val emailTogether = AnimatorSet().apply {
            playTogether(tvEmail, edEmail)
        }

        val passwordTogether = AnimatorSet().apply {
            playTogether(tvPass, edPass)
        }

        AnimatorSet().apply {
            startDelay = 250
            playSequentially(image, title, nameTogether, emailTogether, passwordTogether, btnRegis)
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
}