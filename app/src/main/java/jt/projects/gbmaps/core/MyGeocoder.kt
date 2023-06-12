package jt.projects.gbmaps.core

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import jt.projects.gbmaps.utils.LOG_TAG
import java.util.Locale


class MyGeocoder(private val context: Context, private val locale: Locale) {

    private val geocoder by lazy { Geocoder(context, locale) }

    fun getCityNameByLocation(location: LatLng): Address? {
        var address: Address? = null
        try {
            val result = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!result.isNullOrEmpty()) {
                address = result[0]
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, e.message.toString())
        }
        return address
    }

    fun getAddressByName(searchAddress: String, maxResults: Int): MutableList<Address>? {
        return geocoder.getFromLocationName(searchAddress, maxResults)
    }

}