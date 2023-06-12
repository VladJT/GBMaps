package jt.projects.gbmaps.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import jt.projects.gbmaps.R
import jt.projects.gbmaps.core.MyGeocoder
import jt.projects.gbmaps.databinding.FragmentMapBinding
import jt.projects.gbmaps.utils.showSnackbar
import jt.projects.gbmaps.utils.showSnackbarWithAction
import jt.projects.gbmaps.viewmodel.MapViewModel
import org.koin.android.ext.android.inject

class MapFragment : Fragment() {
    companion object {
        const val ZOOM_VALUE = 6f
    }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val geocoder by inject<MyGeocoder>()

    private lateinit var map: GoogleMap

    private val viewModel by lazy {
        ViewModelProvider(this)[MapViewModel::class.java] // переживает создание активити
    }


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.apply {
            uiSettings.isZoomControlsEnabled = true // добавить кнопки zoom[+][-]

            if ((ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
            ) {
                isMyLocationEnabled = true
            }
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isMapToolbarEnabled = true

            viewModel.liveDataToObserve.observe(viewLifecycleOwner) { markers ->
                googleMap.clear()
                markers.forEach {
                    googleMap.addMarker(it)
                }
            }

            setOnMapLongClickListener { location ->
                viewModel.addMarker(location, geocoder.getCityNameByLocation(location))
            }

            setOnMarkerClickListener { marker ->
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, ZOOM_VALUE))
                showSnackbarWithAction(
                    marker.title.toString(), "Удалить маркер"
                ) {
                    viewModel.removeMarker(marker.position)
                }
                true
            }

        }

        map = googleMap
        firstInit()
    }

    private fun firstInit() {
        val murmansk = LatLng(68.9792, 33.0925)
        viewModel.addMarker(murmansk, "Marker in Murmansk")
        map.moveCamera(CameraUpdateFactory.newLatLng(murmansk))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        setButtonSearchAddressListener()

    }

    private fun setButtonSearchAddressListener() {
        binding.buttonSearchAddress.setOnClickListener {
            binding.editTextAddress.text.toString().let {
                try {
                    val result = geocoder.getAddressByLocation(it, 1)

                    if (result != null && result.size > 0) {
                        val location = LatLng(result.first().latitude, result.first().longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_VALUE))

                    } else {
                        showSnackbar("Адрес не найден")
                    }
                } catch (e: Exception) {
                    showSnackbar("Ошибка: ${e.message}")
                }
            }
        }
    }

    private fun setMarker(location: LatLng, searchText: String, resId: Int): Marker {
        return map.addMarker(
            MarkerOptions().position(location).title(searchText).icon(
                BitmapDescriptorFactory.fromResource(resId)
            )
        )!!
    }


//    private fun drawLine() {
//        val last: Int = markers.size - 1
//        if (last >= 1) {
//            val previous: LatLng = markers[last - 1].position
//            val current: LatLng = markers[last].position
//            map.addPolyline(
//                PolylineOptions()
//                    .add(previous, current)
//                    .color(Color.RED)
//                    .width(15f)
//            )
//        }
//    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}