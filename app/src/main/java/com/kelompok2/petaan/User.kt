package com.kelompok2.petaan

import com.google.firebase.firestore.GeoPoint
import kotlinx.serialization.Serializable

@Serializable
data class User (
    val subject: String? = null,
    val location: GeoPoint? = null,
    var objectId: String,
    var image: ByteArray? = null
) {
    // Konstruktor default yang diperlukan oleh Firebase Firestore
    constructor() : this(
        subject = null,
        location = null,
        objectId = "",
        image = null
    )
}