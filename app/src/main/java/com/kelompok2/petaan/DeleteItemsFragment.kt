package com.kelompok2.petaan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import com.kelompok2.petaan.databinding.FragmentDeleteReportsBinding

class DeleteItemsFragment: Fragment() {

    private var binding: FragmentDeleteReportsBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: DeleteListAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeleteReportsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding!!.deleteRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        userList = arrayListOf()

        // Fetch data from Firestore
        db.collection("reports").get().addOnSuccessListener { result ->
            if (!result.isEmpty) {
                for (data in result.documents) {
                    val user = data.toObject(User::class.java)
                    if (user != null) {
                        user.objectId = data.id
                        userList.add(user)
                    }
                }

                lifecycleScope.launch {
                    val storage = Storage(AppWriteHelper().getClient(requireContext()))
                    userList.forEach { user ->
                        try {
                            val file = storage.getFileView(
                                bucketId = BuildConfig.APP_WRITE_BUCKET_ID,
                                fileId = user.objectId
                            )


                            user.image = file
                        } catch (e: Exception) {
                            Log.e("ListViewFragment", "Error fetching image: $e")
                        }
                    }

                    adapter = DeleteListAdapter(userList, db)
                    recyclerView.adapter = adapter
                }
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
        }

        // Delete selected reports
        binding!!.deleteFab.setOnClickListener {
            val selectedItems = adapter.getSelectedItems()

            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            for (item in selectedItems) {
                db.collection("reports").document(item.objectId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("Firestore", "DocumentSnapshot successfully deleted!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error deleting document", e)
                    }
            }

            // Remove items from the list and update the RecyclerView
            userList.removeAll(selectedItems)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
