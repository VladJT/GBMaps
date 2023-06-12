package jt.projects.gbmaps.core

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale


class MyGeocoder(private val context: Context, private val locale: Locale) {

    private val geocoder by lazy { Geocoder(context, locale) }


    fun getCityNameByLocation(location: LatLng): String {
        val result =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)?.get(0)?.locality
        return result ?: "no city here: (${location.latitude}, ${location.longitude})"
    }

    fun getAddressByLocation(searchAddress: String, maxResults: Int): MutableList<Address>? {
        return geocoder.getFromLocationName(searchAddress, maxResults)
    }

}