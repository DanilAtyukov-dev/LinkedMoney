package com.danilatyukov.linkedmoney.ui.dashboard.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.danilatyukov.linkedmoney.App
import com.danilatyukov.linkedmoney.R
import com.danilatyukov.linkedmoney.appComponent
import com.danilatyukov.linkedmoney.databinding.FragmentWorldMapBinding
import com.danilatyukov.linkedmoney.model.geolocation.GeopointInfoWindow
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay




var currentLocationMarker: Marker? = null

class WorldMapFragment : Fragment() {
    private lateinit var _binding: FragmentWorldMapBinding
    private val binding get() = _binding

    var currentLocation: GeoPoint? = null

    private lateinit var viewModel: WorldMapViewModel

    val markers = ArrayList<Marker>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorldMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initializationMap()

        val worldMapViewModel = ViewModelProvider(this)[WorldMapViewModel::class.java]

        _binding.mapCenter.setOnClickListener{
            App.it().vibratePhone()
            setMapCenter()}
        _binding.zoomOut.setOnClickListener{
            App.it().vibratePhone()
            mapController.zoomOut()}
        _binding.zoomIn.setOnClickListener{
            App.it().vibratePhone()
            mapController.zoomIn()}


        worldMapViewModel.points.observe(viewLifecycleOwner) { it ->
            if (it.isEmpty()) return@observe

            currentLocation = GeoPoint(it.last().latitude, it.last().longitude)
            val currentSpeed = it.last().speed
            val currentAccuracy = it.last().accuracy
            val currentGeopointEntity = it.last()



            setMapCenter()

            it.forEach {
                setWereMarker(it.latitude, it.longitude, it.time, it.speed.toString(), it.accuracy)
            }

            setCurrentMarker(currentLocation!!)

            //it.removeLast()
            _binding.worldMapView.invalidate()

            _binding.clearedMap.setOnClickListener {
                App.it().vibratePhone()
                markers.forEach{
                    it.remove(_binding.worldMapView)

                }
                Thread{
                    App.it().appComponent.geopointDao.deleteAll()
                }.start()
                _binding.worldMapView.invalidate()
            }
        }

        return root
    }

    private fun setMapCenter(){
        mapController.setCenter(currentLocation)
        mapController.setZoom(16.0)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[WorldMapViewModel::class.java]
    }

    private lateinit var mapController: MapController
    private fun initializationMap() {
        try {
            Configuration.getInstance().load(
                requireActivity(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
            )

            mapController = _binding.worldMapView.controller as MapController
            //mapController.setCenter(GeoPoint(UserData.latitude, UserData.longitude))
            _binding.worldMapView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            _binding.worldMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            _binding.worldMapView.setMultiTouchControls(true)
            mapController.setZoom(3.0)
            _binding.worldMapView.maxZoomLevel = 18.2
            _binding.worldMapView.minZoomLevel = 3.0
            _binding.worldMapView.setBuiltInZoomControls(false)

            val mRotationGestureOverlay = RotationGestureOverlay(_binding.worldMapView)
            mRotationGestureOverlay.isEnabled = true
            _binding.worldMapView.setMultiTouchControls(true)
            _binding.worldMapView.overlays.add(mRotationGestureOverlay)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun setWereMarker(latitude: Double, longitude: Double, time: String, speed: String, accuracy: Float) {
        val myMarker = Marker(_binding.worldMapView)
        myMarker.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_locationpointer_violet_24dp)
        //myMarker.icon = (ContextCompat.getDrawable(requireActivity(), org.osmdroid.library.R.drawable.osm_ic_follow_me));

        myMarker.position = GeoPoint(latitude, longitude)
        myMarker.infoWindow = GeopointInfoWindow(
            _binding.worldMapView, time, speed.plus("\n acc $accuracy")
        )
        _binding.worldMapView.overlays.add(myMarker)
        markers.add(myMarker)
    }



    private fun setCurrentMarker(geoPoint: GeoPoint) {
        val myMarker = Marker(_binding.worldMapView)
        mapController.setZoom(16.0)
        markers.add(myMarker)

        myMarker.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_location_pin)
        myMarker.position = GeoPoint(geoPoint.latitude, geoPoint.longitude)
        _binding.worldMapView.overlays.add(myMarker)

        currentLocationMarker?.remove(_binding.worldMapView)
        currentLocationMarker = myMarker
    }
}