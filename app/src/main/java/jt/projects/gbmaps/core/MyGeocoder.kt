package jt.projects.gbmaps.core

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale


class MyGeocoder(private val context: Context, private val locale: Locale) {

    private val geocoder by lazy { Geocoder(context, locale) }

    fun getCityNameByLocation(location: LatLng): String {
        return try {
            geocoder.getFromLocation(location.latitude, location.longitude, 1)?.get(0)?.locality
                ?: "no city here: (${location.latitude}, ${location.longitude})"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    fun getAddressByLocation(searchAddress: String, maxResults: Int): MutableList<Address>? {
        return geocoder.getFromLocationName(searchAddress, maxResults)
    }

}