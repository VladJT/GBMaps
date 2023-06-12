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
import jt.projects.gbmaps.viewmodel.MapViewModel
import org.koin.android.ext.android.inject

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val geocoder by inject<MyGeocoder>()

    private lateinit var map: GoogleMap

    private val viewModel by lazy {
        ViewModelProvider(this)[MapViewModel::class.java] // переживает создание активити
    }


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        val location = LatLng(68.9792, 33.0925)
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
                markers.forEach {
                    googleMap.addMarker(it)
                }
            }

            viewModel.addMarker(location, "Marker in Murmansk")
            moveCamera(CameraUpdateFactory.newLatLng(location))

            setOnMapLongClickListener { location ->
                viewModel.addMarker(location, geocoder.getCityNameByLocation(location))
            }


//                viewModel.removeMarker(it.position)
//                it.remove()
//                true

        }


        map = googleMap
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
                        // setMarker(location, it, R.drawable.ic_map_marker)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 6f))

                    } else {
                        showSnackbar("Адрес не найден")
                    }
                } catch (e: Exception) {
                    showSnackbar("Ошибка: ".plus(e.message))
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

//    private fun addMarkerToArray(location: LatLng) {
//        val marker = setMarker(location, markers.size.toString(), R.drawable.ic_map_pin)
//        markers.add(marker)
//    }

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