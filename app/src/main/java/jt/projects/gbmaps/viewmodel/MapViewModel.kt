package jt.projects.gbmaps.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapViewModel : ViewModel() {

    private val markers = mutableListOf<Marker>()


//    private fun addMarker(location: LatLng) {
//        val marker = setMarker(location, markers.size.toString(), R.drawable.ic_map_pin)
//        markers.add(marker)
//    }


}