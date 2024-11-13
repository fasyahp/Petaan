package com.kelompok2.petaan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.algolia.client.api.SearchClient
import com.algolia.client.extensions.saveObjects
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kelompok2.petaan.databinding.FragmentLandingBinding
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LandingFragment : Fragment() {

    private var binding: FragmentLandingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLandingBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.landingLoginButton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.loginFragment)
        }
        binding!!.landingRegisterButton.setOnClickListener { view ->
            view.findNavController().navigate(R.id.registrationFragment)
        }
    }
}