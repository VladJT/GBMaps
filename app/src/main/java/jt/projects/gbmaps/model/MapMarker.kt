package jt.projects.gbmaps.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

data class MapMarker(
    val markerData: MarkerOptions,
    val comment: String = ""
)

val defaultMapMarker = MapMarker(
    markerData = MarkerOptions().title("Marker in Murmansk")
        .position(LatLng(68.9792, 33.0925)),
    comment = "Мурманск — крупнейший в мире город, расположенный за Северным полярным кругом, крупнейший город культурно-этнического региона Лапландия. Город вытянулся более чем на 20 километров вдоль скалистого восточного побережья Кольского залива, в 50 километрах от выхода в открытое море."
)