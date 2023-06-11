package jt.projects.gbmaps.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import jt.projects.gbmaps.R
import jt.projects.gbmaps.databinding.FragmentMapBinding
import jt.projects.gbmaps.utils.showSnackBar
import java.util.Locale

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    lateinit var map: GoogleMap
    private val markers = mutableListOf<Marker>()

    private val callback = OnMapReadyCallback { googleMap ->
        val location = LatLng(68.9792, 33.0925)
        googleMap.apply {
            uiSettings.isZoomControlsEnabled = true // добавить кнопки zoom[+][-]
       //     isMyLocationEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            addMarker(MarkerOptions().position(location).title("Marker in Murmansk"))
            moveCamera(CameraUpdateFactory.newLatLng(location))

            setOnMapLongClickListener {
//                addMarkerToArray(it)
//                drawLine()
            }

            setOnMarkerClickListener {
                val location = it.position
                true
            }
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

       setButtonSearchListener()
    }

    private fun setButtonSearchListener() {
        binding.buttonSearchAddress.setOnClickListener {
            binding.editTextAddress.text.toString().let {
                val geocoder = Geocoder(requireContext(), Locale("ru_RU"))
                try {
                    val result = geocoder.getFromLocationName(it, 1)

                    if (result != null && result.size > 0) {
                        val location = LatLng(result.first().latitude, result.first().longitude)
                        // setMarker(location, it, R.drawable.ic_map_marker)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 6f))

                    } else {
                        binding.root.showSnackBar("Адрес не найден")
                    }
                } catch (e: Exception) {
                    binding.root.showSnackBar("Ошибка: ".plus(e.message))
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}