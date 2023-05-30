package com.dicoding.storyapp.ui.login

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
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.utils.ViewModelFactory
import com.dicoding.storyapp.utils.lightStatusBar
import com.dicoding.storyapp.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
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
        )[LoginViewModel::class.java]
    }

    private fun setupAction(dialog: AlertDialog) {
        binding?.btnLogin?.setOnClickListener {
            val email = binding?.edLoginEmail?.text.toString().trim()
            val password = binding?.edLoginPassword?.text.toString().trim()
            when {
                email.isEmpty() -> {
                    binding?.edLoginEmail?.requestFocus()
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding?.edLoginEmail?.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() || password.length < 6 -> {
                    binding?.edLoginPassword?.requestFocus()
                    return@setOnClickListener
                }
            }
            viewModel.login(email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Error -> {
                            showLoading(false, dialog)
                            if (result.error == "http") Toast.makeText(
                                this,
                                getString(R.string.error_login),
                                Toast.LENGTH_SHORT
                            ).show() else Toast.makeText(
                                this,
                                getString(R.string.error_connect),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Result.Loading -> showLoading(true, dialog)
                        is Result.Success -> {
                            showLoading(false, dialog)
                            Intent(this, MainActivity::class.java).also { intent ->
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
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
        val tvEmail = ObjectAnimator.ofFloat(binding?.tvEmail, View.ALPHA, 1f).setDuration(500)
        val edEmail = ObjectAnimator.ofFloat(binding?.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val tvPass = ObjectAnimator.ofFloat(binding?.tvPassword, View.ALPHA, 1f).setDuration(500)
        val edPass =
            ObjectAnimator.ofFloat(binding?.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).setDuration(500)

        val emailTogether = AnimatorSet().apply {
            playTogether(tvEmail, edEmail)
        }

        val passwordTogether = AnimatorSet().apply {
            playTogether(tvPass, edPass)
        }

        AnimatorSet().apply {
            startDelay = 250
            playSequentially(image, title, emailTogether, passwordTogether, btnLogin)
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