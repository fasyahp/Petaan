package com.kelompok2.petaan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.algolia.client.api.SearchClient
import com.algolia.client.extensions.saveObjects
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ReportsViewModel : ViewModel() {
    private val client = SearchClient(appId = BuildConfig.ALGOLIA_APP_ID, apiKey = BuildConfig.ALGOLIA_SEARCH_KEY)
    fun indexData() {
        val reports = mutableListOf<JsonObject>()
        Firebase.firestore.collection("reports").get().addOnSuccessListener {
                documents ->
            documents.forEach { document ->
                val location = document.getGeoPoint("location")
                reports.add(
                    buildJsonObject {
                        put("subject", document.get("subject").toString())
                        put("description", document.get("description").toString())
                        put("severity", document.get("severity").toString())
                        put("location", "${location?.latitude}, ${location?.longitude}")
                    }
                )
            }
            Log.d("Algolia", reports.toString())

            viewModelScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        client.saveObjects(
                            indexName = "reports_index",
                            objects = reports,
                        )
                    }
                    Log.d("Algolia", "Data indexed.")
                } catch (e: Exception) {
                    Log.d("Algolio Error", "${e.message}")
                }
            }
//            ViewModelProvider(this)[ReportsViewModel::class.java].indexData()
        }
    }
    fun search(query: String) {

    }
}
