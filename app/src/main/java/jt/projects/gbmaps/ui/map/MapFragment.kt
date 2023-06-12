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
import com.google.android.gms.maps.model.LatLng
import jt.projects.gbmaps.R
import jt.projects.gbmaps.core.MyGeocoder
import jt.projects.gbmaps.databinding.FragmentMapBinding
import jt.projects.gbmaps.utils.showSnackbar
import jt.projects.gbmaps.utils.showSnackbarWithAction
import jt.projects.gbmaps.viewmodel.MapViewModel
import org.koin.android.ext.android.inject

class MapFragment : Fragment() {
    companion object {
        const val ZOOM_VALUE = 3f
    }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val geocoder by inject<MyGeocoder>()

    private lateinit var map: GoogleMap

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MapViewModel::class.java] // переживает создание активити
    }


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.apply {
            if ((ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
            ) {
            //    isMyLocationEnabled = true
            }

            uiSettings.isZoomControlsEnabled = true // добавить кнопки zoom[+][-]
            uiSettings.isMyLocationButtonEnabled = true
            uiSettings.isMapToolbarEnabled = true

            setOnMapLongClickListener { location ->
                val address = geocoder.getCityNameByLocation(location)
                if (address == null) {
                    viewModel.addMarker(location, "no city found", "")
                } else {
                    val name = (address.subAdminArea ?: "-").plus(" (${address.adminArea?:"-"})")
                    viewModel.addMarker(
                        location,
                        name,
                        "$address"
                    )
                }
            }

            setOnMarkerClickListener { marker ->
                //    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, ZOOM_VALUE))
                showSnackbarWithAction(
                    marker.title.toString(), "Удалить маркер"
                ) {
                    viewModel.removeMarker(marker.position)
                }
                true
            }

        }

        map = googleMap
        viewModel.liveDataToObserve.observe(viewLifecycleOwner) { markers ->
            map.clear()
            markers.forEach {
                map.addMarker(it.markerData)
            }
        }
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
                    val result = geocoder.getAddressByName(it, 1)

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


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}