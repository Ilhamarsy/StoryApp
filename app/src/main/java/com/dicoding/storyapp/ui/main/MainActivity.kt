package com.dicoding.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.utils.ViewModelFactory
import com.dicoding.storyapp.network.response.LoginResult
import com.dicoding.storyapp.ui.addstory.AddStoryActivity
import com.dicoding.storyapp.ui.maps.MapsActivity
import com.dicoding.storyapp.utils.lightStatusBar
import com.dicoding.storyapp.ui.start.StartActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: MainViewModel

    private lateinit var user: LoginResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataStore, this)
        )[MainViewModel::class.java]

        viewModel.getUser().observe(this) {
            if (it.name.isNotEmpty()) {
                user = it
                binding?.tvName?.text = it.name

                val adapter = ItemAdapter()
                binding?.rvStory?.setHasFixedSize(true)
                binding?.rvStory?.layoutManager =
                    if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) GridLayoutManager(
                        this,
                        2
                    ) else LinearLayoutManager(this)
                binding?.rvStory?.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )
                viewModel.story(it.token).observe(this) { paging ->
                    adapter.submitData(lifecycle, paging)
                }
            }
        }
    }

    private fun setupAction() {
        binding?.btnAdd?.setOnClickListener {
            Intent(this, AddStoryActivity::class.java).also {
                it.putExtra(AddStoryActivity.BEARER_TOKEN, user.token)
                startActivity(it)
            }
        }

        binding?.btnLanguage?.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding?.btnLogout?.setOnClickListener {
            viewModel.logout()
        }

        binding?.btnMaps?.setOnClickListener {
            Intent(this, MapsActivity::class.java).also {
                it.putExtra(MapsActivity.BEARER_TOKEN, user.token)
                startActivity(it)
            }
        }
    }

    private fun setupView() {
        lightStatusBar(window, true)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getUser().observe(this) {
            if (it.name.isEmpty()) {
                Intent(this, StartActivity::class.java).also { intent ->
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }
}