package com.kelompok2.petaan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.LOCATION_SERVICE
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.kelompok2.petaan.databinding.FragmentHomepageBinding
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponent
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

class HomepageFragment : Fragment() {

    private var binding: FragmentHomepageBinding? = null
    private lateinit var mapView: MapView
    private var locationComponent: LocationComponent? = null
    private lateinit var mapLibreMap: MapLibreMap
    private val api_key = BuildConfig.API_KEY
    private val styleUrl = "https://api.maptiler.com/maps/streets-v2/style.json?key=$api_key"
//    private val styleUrl = "https://demotiles.maplibre.org/style.json"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomepageBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding!!.mapView

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync{ map ->
            map.setStyle(styleUrl)
            map.cameraPosition = CameraPosition.Builder().target(LatLng(0.0,0.0)).zoom(1.0).build()
        }

        val getActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            mapView.getMapAsync { locationComponent!!.forceLocationUpdate(locationComponent!!.lastKnownLocation) }
        }

        binding!!.mylocationButton.setOnClickListener {
            if (
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(requireActivity(), "You need to enable GPS.", Toast.LENGTH_SHORT).show()
                    getActivityResult.launch(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),  1)
            }
        }

        binding!!.addButton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.addROIFragment)
        }

        binding!!.tolistviewButton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.listViewFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}