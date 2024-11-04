package com.kelompok2.petaan

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.kelompok2.petaan.databinding.ActivityMainBinding
import com.kelompok2.petaan.databinding.FragmentAddroiBinding
import com.kelompok2.petaan.databinding.FragmentHomepageBinding
import org.maplibre.android.MapLibre

class MainActivity : AppCompatActivity() {
//    requestForPermissionLauncher.launch(Manifest.permission.INTERNET)
//    ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(this)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            if (
                destination.id == R.id.addROIFragment ||
                destination.id == R.id.searchFragment
            ) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }
}