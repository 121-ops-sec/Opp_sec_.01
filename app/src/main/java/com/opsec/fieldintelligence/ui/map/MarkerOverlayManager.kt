package com.opsec.fieldintelligence.ui.map

import android.content.Context
import com.opsec.fieldintelligence.data.model.MapLayerState
import com.opsec.fieldintelligence.data.model.MarkerType
import com.opsec.fieldintelligence.data.model.Pin
import com.opsec.fieldintelligence.util.MarkerIconFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MarkerOverlayManager(
    private val mapView: MapView,
    private val context: Context,
    private val onMarkerTapped: (Pin) -> Unit
) {
    // Maps pinId → Marker so we can update or remove efficiently
    private val pinMarkers = mutableMapOf<Long, Marker>()

    // Marker for the user's current GPS position
    private var myLocationMarker: Marker? = null

    /**
     * Synchronise the overlay with the current pin list and layer visibility state.
     * Called whenever allPins or layerState changes.
     */
    fun syncPins(pins: List<Pin>, layerState: MapLayerState) {
        val incomingIds = pins.map { it.id }.toSet()

        // Remove markers for deleted pins
        val toRemove = pinMarkers.keys.filterNot { it in incomingIds }
        toRemove.forEach { id ->
            pinMarkers[id]?.let { mapView.overlays.remove(it) }
            pinMarkers.remove(id)
        }

        // Add / update markers
        pins.forEach { pin ->
            val existing = pinMarkers[pin.id]
            if (existing == null) {
                val marker = Marker(mapView).apply {
                    position = GeoPoint(pin.latitude, pin.longitude)
                    title = pin.title
                    icon = MarkerIconFactory.getIcon(context, pin.markerType)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    setOnMarkerClickListener { _, _ ->
                        onMarkerTapped(pin)
                        true
                    }
                }
                pinMarkers[pin.id] = marker
                mapView.overlays.add(marker)
            } else {
                // Update position and title in case it was edited
                existing.position = GeoPoint(pin.latitude, pin.longitude)
                existing.title = pin.title
            }

            // Apply layer visibility
            pinMarkers[pin.id]?.isEnabled = isVisible(pin.markerType, layerState)
        }

        mapView.invalidate()
    }

    /** Updates or creates the "my location" marker. */
    fun updateMyLocation(point: GeoPoint) {
        if (myLocationMarker == null) {
            myLocationMarker = Marker(mapView).apply {
                title = "My Location"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                setOnMarkerClickListener { _, _ -> true } // consume tap, no detail sheet
            }
            mapView.overlays.add(0, myLocationMarker) // put below pin markers
        }
        myLocationMarker?.position = point
        mapView.invalidate()
    }

    /** Removes all pin markers from the map (e.g., on fragment destroy). */
    fun clear() {
        pinMarkers.values.forEach { mapView.overlays.remove(it) }
        pinMarkers.clear()
        myLocationMarker?.let { mapView.overlays.remove(it) }
        myLocationMarker = null
    }

    private fun isVisible(type: MarkerType, state: MapLayerState): Boolean = when (type) {
        MarkerType.NOTE -> state.showNotes
        MarkerType.FREQUENCY -> state.showFrequencies
        MarkerType.HAZARD -> state.showHazards
        MarkerType.INFRASTRUCTURE -> state.showInfrastructure
        MarkerType.GENERIC -> state.showGenericPins
    }
}
