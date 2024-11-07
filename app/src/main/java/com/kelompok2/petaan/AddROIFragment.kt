package com.kelompok2.petaan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.kelompok2.petaan.databinding.FragmentAddroiBinding

class AddROIFragment : Fragment() {

    private var binding: FragmentAddroiBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddroiBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
//        binding!!.locationTextField.text =
/*
        ViewCompat.setOnApplyWindowInsetsListener(binding!!.addROIAppBarContainer) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.saveAddReportButton) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = insets.bottom
            }
            WindowInsetsCompat.CONSUMED
        }
*/

        binding!!.saveAddReportButton.setOnClickListener { v ->
            val severity = when (binding!!.severityDropDown.text.toString()) {
                "Low" -> 1
                "Moderate" -> 2
                "High" -> 3
                "Critical" -> 4
                "Emergency" -> 5
                else -> -1
            }
            val latLng = binding!!.locationTextField.text?.split(",")
            val db = Firebase.firestore
            val report = hashMapOf(
                "subject" to binding!!.subjectTextField.text.toString(),
                "description" to binding!!.descriptionTextField.text.toString(),
                "severity" to severity,
                "location" to GeoPoint(latLng!![0].toDouble(), latLng[1].toDouble())
            )
            db.collection("reports")
                .add(report)
                .addOnSuccessListener { dr ->
                    Log.d("Firebase", "DocumentSnapshot ID: ${dr.id}")
                    Toast.makeText(requireContext(), "Report added!", Toast.LENGTH_SHORT).show()
                    v.findNavController().navigate(R.id.homepageFragment)
                }
                .addOnFailureListener { e ->
                    Log.w("Firebase", "Error adding document", e)
                    Toast.makeText(requireContext(), "Failed to add report: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }
}