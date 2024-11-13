package com.kelompok2.petaan

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kelompok2.petaan.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        val binding = binding ?: return // biar binding ga null

        // Set initial password transformation
        binding.editPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        // Setup password toggle
        binding.passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            // Toggle password visibility
            if (isPasswordVisible) {
                // Show password
                binding.editPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.passwordToggle.setImageResource(R.drawable.ic_eye_open)
            } else {
                // Hide password
                binding.editPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.passwordToggle.setImageResource(R.drawable.ic_eye_close)
            }
            // Maintain cursor position
            binding.editPassword.setSelection(binding.editPassword.text.length)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(
                            Intent(requireContext(), MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }
}