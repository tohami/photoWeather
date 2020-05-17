package com.tohami.photo_weather.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.tohami.photo_weather.R
import com.tohami.photo_weather.ui.base.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.activity_weather.*

class WeatherPhotosActivity : AppCompatActivity(),
    OnFragmentInteractionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        setSupportActionBar(toolbar)
        setupNavigationComponent()
    }


    private fun setupNavigationComponent() {
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
        return true
    }

    override fun setToolbarTitle(title: String) {
        toolbar.title = title
    }

    override fun setToolbarVisibility(visibility: Boolean) {
        toolbar.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        if (navHostFragment != null) {
            val fragment =
                navHostFragment.childFragmentManager.fragments[0]
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}