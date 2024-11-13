package com.kelompok2.petaan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import io.appwrite.services.Storage

class SearchAdapter(private val dataset: MutableList<SearchItem>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subject: TextView
        val location: TextView
        val image: ImageView

        init {
            subject = view.findViewById<TextView>(R.id.search_result_subject)
            location = view.findViewById<TextView>(R.id.search_result_location)
            image = view.findViewById<ImageView>(R.id.search_result_image)
        }
    }

    fun updateData(dataset: MutableList<SearchItem>) {
        this.dataset.clear()
        this.dataset.addAll(dataset)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false)
        context = view.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        holder.subject.text = dataset[position].subject
        holder.location.text = dataset[position].location
        holder.image.load(dataset[position].image)
    }

    override fun getItemCount(): Int = dataset.size

}