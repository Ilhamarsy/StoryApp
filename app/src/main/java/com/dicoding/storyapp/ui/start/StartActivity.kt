package com.dicoding.storyapp.ui.start

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dicoding.storyapp.databinding.ActivityStartBinding
import com.dicoding.storyapp.utils.lightStatusBar
import com.dicoding.storyapp.ui.login.LoginActivity
import com.dicoding.storyapp.ui.register.RegisterActivity

class StartActivity : AppCompatActivity() {
    private var _binding: ActivityStartBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupView()
        playAnimation()

        binding?.btnLogin?.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        binding?.btnRegister?.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding?.title, View.ALPHA, 1f).setDuration(500)
        val image = ObjectAnimator.ofFloat(binding?.illus, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding?.desc, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegis = ObjectAnimator.ofFloat(binding?.btnRegister, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(btnLogin, btnRegis)
        }

        AnimatorSet().apply {
            startDelay = 250
            playSequentially(title, image, desc, together)
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