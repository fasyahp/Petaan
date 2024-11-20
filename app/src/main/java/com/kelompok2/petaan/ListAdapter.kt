package com.kelompok2.petaan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.fallback
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListAdapter(private val userList: ArrayList<User>) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private lateinit var context: Context
    private lateinit var client: Client

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescription: TextView = itemView.findViewById(R.id.description)
        val tvLocation: TextView = itemView.findViewById(R.id.location)
        val tvImage: ImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.listview_item, parent, false)
        context = itemView.context
        client = AppWriteHelper().getClient(context)
        return ListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
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
    }
}
