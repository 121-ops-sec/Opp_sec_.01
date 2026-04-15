package com.opsec.fieldintelligence.data.model

data class MapLayerState(
    val showNotes: Boolean = true,
    val showFrequencies: Boolean = true,
    val showHazards: Boolean = true,
    val showInfrastructure: Boolean = true,
    val showGenericPins: Boolean = true,
    val useOfflineTiles: Boolean = true,
    val showSatelliteLayer: Boolean = false
)
