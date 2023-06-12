package jt.projects.gbmaps.viewmodel

import android.icu.text.CaseMap.Title
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapViewModel : ViewModel() {

    private val liveData: MutableLiveData<MutableList<MarkerOptions>> = MutableLiveData()
    val liveDataToObserve: LiveData<MutableList<MarkerOptions>>
        get() {
            return liveData
        }

    init {
        liveData.value = mutableListOf(
            MarkerOptions().title("Marker in Murmansk").position(LatLng(68.9792, 33.0925))
        )
    }

    private fun refreshData() {
        liveData.postValue(liveData.value)
    }

    fun addMarker(location: LatLng, title: String) {
        liveData.value?.add(MarkerOptions().position(location).title(title))
        refreshData()
    }

    fun removeMarker(location: LatLng) {
        liveData.value?.removeIf {
            val result =
                (it.position.latitude == location.latitude && it.position.longitude == location.longitude)
            result
        }
        refreshData()
    }

    fun editMarker(marker: MarkerOptions, newTitle: String){
        val index = liveData.value?.indexOf(marker)
        if (index != null) {
            liveData.value?.apply {
                remove(marker)
                add(MarkerOptions().title(newTitle).position(marker.position))
            }
        }
    }
}