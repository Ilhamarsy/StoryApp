package com.dicoding.storyapp.ui.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.network.response.DetailResponse
import com.dicoding.storyapp.utils.lightStatusBar
import com.dicoding.storyapp.utils.withDateFormat

class DetailActivity : AppCompatActivity() {
    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupView()
        playAnimation()

        val intent = intent.getParcelableExtra<DetailResponse>("DATA")

        if (intent != null) {
            binding?.apply {
                Glide.with(this@DetailActivity)
                    .load(intent.photoUrl)
                    .into(ivDetailPhoto)

                tvDetailName.text = intent.name
                tvItemDate.text = intent.createdAt.withDateFormat()
                tvDetailDescription.text = intent.description
            }
        }

        binding?.btnBack?.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun playAnimation() {
        val desc =
            ObjectAnimator.ofFloat(binding?.tvDetailDescription, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            startDelay = 250
            play(desc)
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