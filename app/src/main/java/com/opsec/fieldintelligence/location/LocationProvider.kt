package com.opsec.fieldintelligence.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

class LocationProvider(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        3000L
    )
        .setMinUpdateDistanceMeters(5f)
        .setWaitForAccurateLocation(false)
        .build()

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startUpdates(onLocation: (Location) -> Unit) {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { onLocation(it) }
            }
        }
        locationCallback = callback
        fusedClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
    }

    fun stopUpdates() {
        locationCallback?.let { fusedClient.removeLocationUpdates(it) }
        locationCallback = null
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(onResult: (Location?) -> Unit) {
        fusedClient.lastLocation.addOnSuccessListener { onResult(it) }
    }
}
