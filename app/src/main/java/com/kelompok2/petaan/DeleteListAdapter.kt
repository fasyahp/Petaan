package com.kelompok2.petaan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.fallback
import com.google.firebase.firestore.FirebaseFirestore
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteListAdapter(private val userList: ArrayList<User>, private val db: FirebaseFirestore) :
    RecyclerView.Adapter<DeleteListAdapter.DeleteViewHolder>() {

    private val selectedItems = HashSet<User>()
    private lateinit var client: Client
    private lateinit var context: Context

    class DeleteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescription: TextView = itemView.findViewById(R.id.description)
        val checkBox: CheckBox = itemView.findViewById(R.id.deleteCheckbox)
        val tvLocation: TextView = itemView.findViewById(R.id.location)
        val tvImage: ImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview_item_with_checkbox, parent, false)
        context = itemView.context
        client = AppWriteHelper().getClient(context)
        return DeleteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeleteViewHolder, position: Int) {
        val user = userList[position]
        holder.tvDescription.text = userList[position].subject
        val location = userList[position].location
        if (location != null) {
            val locationText = "${location.latitude}, ${location.longitude}"
            holder.tvLocation.text = locationText
        } else {
            holder.tvLocation.text = "No Location"
        }

        CoroutineScope(Dispatchers.IO).launch {
            holder.tvImage.load(
                try {
                    Storage(client).getFileView(
                        bucketId = BuildConfig.APP_WRITE_BUCKET_ID,
                        fileId = userList[position].objectId
                    )
                } catch (e: Exception) {
                    null
                }
            ) {
                fallback(R.drawable.baseline_broken_image_24)
            }
        }

        holder.checkBox.isChecked = selectedItems.contains(user)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(user)
            } else {
                selectedItems.remove(user)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun getSelectedItems(): List<User> {
        return selectedItems.toList()
    }
}
