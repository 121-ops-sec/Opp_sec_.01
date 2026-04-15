package com.opsec.fieldintelligence.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.opsec.fieldintelligence.data.db.AppDatabase
import com.opsec.fieldintelligence.data.model.*
import com.opsec.fieldintelligence.data.repository.*
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val pinRepo = PinRepository(db.pinDao())
    private val noteRepo = NoteRepository(db.noteDao())
    private val freqRepo = FrequencyRepository(db.frequencyLogDao())
    private val hazardRepo = HazardRepository(db.hazardDao())

    // Layer visibility state
    private val _layerState = MutableLiveData(MapLayerState())
    val layerState: LiveData<MapLayerState> = _layerState

    // All pins observed from DB
    val allPins: LiveData<List<Pin>> = pinRepo.allPins.asLiveData()

    // Current GPS location
    private val _currentLocation = MutableLiveData<GeoPoint?>()
    val currentLocation: LiveData<GeoPoint?> = _currentLocation

    // GPS accuracy in metres
    private val _locationAccuracy = MutableLiveData<Float>()
    val locationAccuracy: LiveData<Float> = _locationAccuracy

    // Saved map centre / zoom for state restoration across navigation
    private val _mapCenter = MutableLiveData(GeoPoint(0.0, 0.0))
    val mapCenter: LiveData<GeoPoint> = _mapCenter

    private val _mapZoom = MutableLiveData(14.0)
    val mapZoom: LiveData<Double> = _mapZoom

    // One-shot event to signal the map should animate to current location
    private val _centerOnMe = MutableLiveData<Boolean>()
    val centerOnMe: LiveData<Boolean> = _centerOnMe

    fun updateLocation(lat: Double, lon: Double, accuracy: Float) {
        _currentLocation.value = GeoPoint(lat, lon)
        _locationAccuracy.value = accuracy
    }

    fun requestCenterOnMe() {
        _centerOnMe.value = true
    }

    fun centerOnMeHandled() {
        _centerOnMe.value = false
    }

    fun saveMapState(center: GeoPoint, zoom: Double) {
        _mapCenter.value = center
        _mapZoom.value = zoom
    }

    fun toggleLayer(type: MarkerType, visible: Boolean) {
        _layerState.value = when (type) {
            MarkerType.NOTE -> _layerState.value?.copy(showNotes = visible)
            MarkerType.FREQUENCY -> _layerState.value?.copy(showFrequencies = visible)
            MarkerType.HAZARD -> _layerState.value?.copy(showHazards = visible)
            MarkerType.INFRASTRUCTURE -> _layerState.value?.copy(showInfrastructure = visible)
            MarkerType.GENERIC -> _layerState.value?.copy(showGenericPins = visible)
        } ?: _layerState.value
        viewModelScope.launch {
            pinRepo.setLayerVisibility(type, visible)
        }
    }

    fun toggleSatelliteLayer(enabled: Boolean) {
        _layerState.value = _layerState.value?.copy(showSatelliteLayer = enabled)
    }

    // --- Pin CRUD ---

    fun dropPin(lat: Double, lon: Double, title: String, type: MarkerType) {
        viewModelScope.launch {
            pinRepo.addPin(Pin(latitude = lat, longitude = lon, title = title, markerType = type))
        }
    }

    fun deletePin(pin: Pin) = viewModelScope.launch { pinRepo.deletePin(pin) }

    // --- Note CRUD ---

    fun addNote(pinId: Long, title: String, body: String) = viewModelScope.launch {
        noteRepo.addNote(Note(pinId = pinId, title = title, body = body))
    }

    fun notesForPin(pinId: Long) = noteRepo.notesForPin(pinId).asLiveData()

    // --- Frequency CRUD ---

    fun logFrequency(
        pinId: Long,
        mhz: Double,
        modulation: String,
        quality: SignalQuality,
        notes: String
    ) = viewModelScope.launch {
        freqRepo.addLog(
            FrequencyLog(
                pinId = pinId,
                frequencyMhz = mhz,
                modulationType = modulation,
                signalQuality = quality,
                rxNotes = notes
            )
        )
    }

    fun freqsForPin(pinId: Long) = freqRepo.logsForPin(pinId).asLiveData()

    // --- Hazard CRUD ---

    fun addHazard(
        pinId: Long,
        severity: HazardSeverity,
        category: String,
        description: String,
        expiresAt: Long? = null
    ) = viewModelScope.launch {
        hazardRepo.addHazard(
            Hazard(
                pinId = pinId,
                severity = severity,
                category = category,
                description = description,
                expiresAt = expiresAt
            )
        )
    }

    fun hazardsForPin(pinId: Long) = hazardRepo.hazardsForPin(pinId).asLiveData()
}
