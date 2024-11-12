package com.kelompok2.petaan

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import coil3.load
import coil3.request.allowHardware
import coil3.request.crossfade
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.kelompok2.petaan.databinding.FragmentAddroiBinding
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URLConnection

class AddROIFragment : Fragment() {

    private var binding: FragmentAddroiBinding? = null
    private lateinit var appWriteClient: Client
    private lateinit var mimeType: String
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding!!.reportImageView.load(uri) {
                crossfade(true)
                allowHardware(false)
            }
            Log.d("Photo Picker", "Selected URI: $uri")
        } else {
            Log.d("Photo Picker", "No media selected.")
        }
    }

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
        appWriteClient = AppWriteHelper().getClient(requireContext())

        binding!!.addReportImageFab.setOnClickListener{ v ->
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding!!.saveAddReportButton.setOnClickListener { v ->

            val severityDropdown = binding!!.severityDropDown
            val subjectTextField = binding!!.subjectTextField
            val descriptionTextField = binding!!.descriptionTextField
            val locationTextField = binding!!.locationTextField
            val reportImageView = binding!!.reportImageView

            if (
                !severityDropdown.text.toString().trim().isEmpty() &&
                !subjectTextField.text.toString().trim().isEmpty() &&
                !descriptionTextField.text.toString().trim().isEmpty() &&
                !locationTextField.text.toString().trim().isEmpty() &&
                reportImageView.drawable != null
            ) {
                val severity = when (severityDropdown.text.toString()) {
                    "Low" -> 1
                    "Moderate" -> 2
                    "High" -> 3
                    "Critical" -> 4
                    "Emergency" -> 5
                    else -> -1
                }
                val latLng = locationTextField.text?.split(",")

                val imageId = ID.unique()
                val db = Firebase.firestore
                val report = hashMapOf(
                    "subject" to subjectTextField.text.toString(),
                    "description" to descriptionTextField.text.toString(),
                    "severity" to severity,
                    "location" to GeoPoint(latLng!![0].toDouble(), latLng[1].toDouble()),
                    "image" to imageId
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

                val bitmap = binding!!.reportImageView.drawToBitmap()
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val imageBytes = outputStream.toByteArray()

                val byteArrayInputStream = ByteArrayInputStream(imageBytes)
                val mimeType = URLConnection.guessContentTypeFromStream(byteArrayInputStream)
                Log.d("MIME", "$mimeType")
                byteArrayInputStream.close()

                try {
                    lifecycleScope.launch {
                        AppWriteHelper()
                            .getStorage(appWriteClient)
                            .createFile(
                                bucketId = BuildConfig.APP_WRITE_BUCKET_ID,
                                fileId = ID.unique(),
                                file = InputFile.fromBytes(bytes = imageBytes, mimeType = mimeType, filename = subjectTextField.text.toString())
                            )
                        outputStream.close()
                    }
                } catch (e: Error) {
                    Log.d("AppWriteError", "$e")
                }
            } else {
                if (severityDropdown.text.toString().trim().isEmpty()) {
                    severityDropdown.error = "This field is required."
                }
                if (subjectTextField.text.toString().trim().isEmpty()) {
                    subjectTextField.error = "This field is required."
                }
                if (descriptionTextField.text.toString().trim().isEmpty()) {
                    descriptionTextField.error = "This field is required."
                }
                if (locationTextField.text.toString().trim().isEmpty()) {
                    locationTextField.error = "This field is required."
                }
                if (reportImageView.drawable == null) {
                    Toast.makeText(requireContext(), "You have to specify an image.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}