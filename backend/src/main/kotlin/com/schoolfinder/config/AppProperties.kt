package com.schoolfinder.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val region: Region = Region(),
    val firebase: Firebase = Firebase(),
    val cloudinary: Cloudinary = Cloudinary(),
    val googleMaps: GoogleMaps = GoogleMaps(),
) {
    data class Region(
        val country: String = "Cameroon",
        val currency: String = "XAF",
        // Centred on Yaoundé (the app lists universities in Yaoundé)
        val mapCenterLat: Double = 3.8480,
        val mapCenterLng: Double = 11.5021,
    )

    data class Firebase(
        val enabled: Boolean = false,
        val credentialsPath: String = "",
    )

    data class Cloudinary(
        val url: String = "",
    )

    data class GoogleMaps(
        val apiKey: String = "",
    )
}
