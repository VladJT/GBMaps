package jt.projects.gbmaps.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import jt.projects.gbmaps.model.MapMarker
import jt.projects.gbmaps.model.defaultMapMarker

class MapViewModel : ViewModel() {

    private val liveData: MutableLiveData<MutableList<MapMarker>> = MutableLiveData()
    val liveDataToObserve: LiveData<MutableList<MapMarker>>
        get() {
            return liveData
        }

    init {
        liveData.value = mutableListOf(
            defaultMapMarker
        )
    }

    private fun refreshData() {
        liveData.postValue(liveData.value)
    }

    fun addMarker(location: LatLng, title: String, comment: String = "") {
        liveData.value?.add(MapMarker(MarkerOptions().position(location).title(title), comment))
        refreshData()
    }

    fun removeMarker(location: LatLng) {
        liveData.value?.removeIf {
            val result =
                (it.markerData.position.latitude == location.latitude && it.markerData.position.longitude == location.longitude)
            result
        }
        refreshData()
    }

    fun editMarker(marker: MapMarker, newTitle: String, comment: String = "") {
        val index = liveData.value?.indexOf(marker)
        if (index != null) {
            liveData.value?.apply {
                remove(marker)
                add(
                    MapMarker(
                        MarkerOptions().title(newTitle).position(marker.markerData.position),
                        comment = comment
                    )
                )
            }
        }
    }
}