package com.kelompok2.petaan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.findNavController
import com.google.firebase.Firebase
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
            val db = Firebase.firestore
            val report = hashMapOf(
                "subject" to binding!!.subjectTextField.text.toString(),
                "description" to binding!!.descriptionTextField.text.toString(),
                "severity" to binding!!.severityDropDown.text.toString(),
                "location" to binding!!.locationTextField.text.toString()
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