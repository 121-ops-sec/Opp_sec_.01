package com.opsec.fieldintelligence.ui.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.opsec.fieldintelligence.data.model.MapLayerState
import com.opsec.fieldintelligence.data.model.MarkerType
import com.opsec.fieldintelligence.data.model.Pin
import com.opsec.fieldintelligence.databinding.FragmentMapBinding
import com.opsec.fieldintelligence.location.LocationService
import com.opsec.fieldintelligence.ui.dialogs.AddPinDialog
import com.opsec.fieldintelligence.ui.dialogs.MarkerDetailBottomSheet
import com.opsec.fieldintelligence.util.PermissionHelper
import com.opsec.fieldintelligence.util.TileSourceHelper
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver

@Suppress("DEPRECATION")
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by activityViewModels()
    private lateinit var overlayManager: MarkerOverlayManager

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val lat = intent.getDoubleExtra(LocationService.EXTRA_LAT, 0.0)
            val lon = intent.getDoubleExtra(LocationService.EXTRA_LON, 0.0)
            val acc = intent.getFloatExtra(LocationService.EXTRA_ACCURACY, 0f)
            viewModel.updateLocation(lat, lon, acc)
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            LocationService.startService(requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOsmDroid()
        initMap()
        setupOverlayManager()
        observeViewModel()
        setupLayerChips()
        setupFabs()
        checkLocationPermission()
    }

    private fun initOsmDroid() {
        Configuration.getInstance().apply {
            load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
            userAgentValue = requireContext().packageName
            osmdroidTileCache = TileSourceHelper.getTileCacheDir(requireContext())
        }
    }

    private fun initMap() {
        binding.mapView.apply {
            setTileSource(TileSourceHelper.buildOnlineTileSource())
            setMultiTouchControls(true)
            controller.setZoom(viewModel.mapZoom.value ?: 14.0)
            viewModel.mapCenter.value?.let { controller.setCenter(it) }

            // Long-press to drop a pin at tapped location
            val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?) = false
                override fun longPressHelper(p: GeoPoint?): Boolean {
                    p?.let { showAddPinDialog(it) }
                    return true
                }
            })
            overlays.add(0, eventsOverlay)
        }
    }

    private fun setupOverlayManager() {
        overlayManager = MarkerOverlayManager(
            mapView = binding.mapView,
            context = requireContext(),
            onMarkerTapped = { pin -> showMarkerDetail(pin) }
        )
    }

    private fun observeViewModel() {
        viewModel.allPins.observe(viewLifecycleOwner) { pins ->
            val layerState = viewModel.layerState.value ?: MapLayerState()
            overlayManager.syncPins(pins, layerState)
        }

        viewModel.layerState.observe(viewLifecycleOwner) { state ->
            val pins = viewModel.allPins.value ?: emptyList()
            overlayManager.syncPins(pins, state)
            // Satellite layer toggle
            // Note: satellite TilesOverlay setup would be added here for online use
        }

        viewModel.currentLocation.observe(viewLifecycleOwner) { point ->
            point?.let { overlayManager.updateMyLocation(it) }
        }

        viewModel.centerOnMe.observe(viewLifecycleOwner) { shouldCenter ->
            if (shouldCenter == true) {
                viewModel.currentLocation.value?.let { point ->
                    binding.mapView.controller.animateTo(point)
                }
                viewModel.centerOnMeHandled()
            }
        }
    }

    private fun setupLayerChips() {
        binding.chipNotes.setOnCheckedChangeListener { _, checked ->
            viewModel.toggleLayer(MarkerType.NOTE, checked)
        }
        binding.chipFrequencies.setOnCheckedChangeListener { _, checked ->
            viewModel.toggleLayer(MarkerType.FREQUENCY, checked)
        }
        binding.chipHazards.setOnCheckedChangeListener { _, checked ->
            viewModel.toggleLayer(MarkerType.HAZARD, checked)
        }
        binding.chipInfrastructure.setOnCheckedChangeListener { _, checked ->
            viewModel.toggleLayer(MarkerType.INFRASTRUCTURE, checked)
        }
        binding.btnSatellite.setOnCheckedChangeListener { _, checked ->
            viewModel.toggleSatelliteLayer(checked)
        }
    }

    private fun setupFabs() {
        binding.fabCenterOnMe.setOnClickListener {
            viewModel.requestCenterOnMe()
        }

        binding.fabDropPin.setOnClickListener {
            val center = binding.mapView.mapCenter as? GeoPoint
                ?: GeoPoint(0.0, 0.0)
            showAddPinDialog(center)
        }
    }

    private fun showAddPinDialog(location: GeoPoint) {
        AddPinDialog.newInstance(location.latitude, location.longitude)
            .show(childFragmentManager, AddPinDialog.TAG)
    }

    private fun showMarkerDetail(pin: Pin) {
        MarkerDetailBottomSheet.newInstance(pin.id)
            .show(childFragmentManager, MarkerDetailBottomSheet.TAG)
    }

    private fun checkLocationPermission() {
        if (!PermissionHelper.hasAnyLocation(requireContext())) {
            PermissionHelper.requestLocationPermissions(permissionLauncher)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            locationReceiver,
            IntentFilter(LocationService.ACTION_LOCATION_UPDATE)
        )
    }

    override fun onPause() {
        super.onPause()
        val center = binding.mapView.mapCenter as? GeoPoint ?: GeoPoint(0.0, 0.0)
        viewModel.saveMapState(center, binding.mapView.zoomLevelDouble)
        binding.mapView.onPause()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(locationReceiver)
    }

    override fun onDestroyView() {
        overlayManager.clear()
        _binding = null
        super.onDestroyView()
    }
}
