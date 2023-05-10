package com.lovestory.lovestory.model

import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlin.math.log2

val points1 = listOf(
    LatLng(37.503735330931136, 126.95615523253305),
    LatLng(37.503960981355855, 126.95652825212924),
    LatLng(37.503960981355855, 126.95678825212924),
    LatLng(37.503860981355855, 126.95678825212924),
    LatLng(37.503630981355855, 126.95705767391241),
    LatLng(37.503800981355855, 126.95742767391241),
    LatLng(37.503909813558551, 126.95758767391241),
    LatLng(37.504312813558557, 126.95763767391241),
    LatLng(37.504412813558553, 126.95763767391241),
    LatLng(37.504512813558558, 126.95733767391241)
)

fun getLatLng(clusterData: ClusterData): MutableList<LatLng> {
    val latLng = mutableListOf<LatLng>()
    clusterData.clusters.forEach{cluster->
        val representativePoint = cluster.representativePoint
        val latitude = representativePoint.latitude
        val longitude = representativePoint.longitude

        latLng.add(LatLng(latitude, longitude))
    }
    return latLng
}

fun getZoomLevelForDistance(distance: Float): Float {
    val zoomLevel = 16f - log2(distance / 500) // Adjust the division factor as needed
    return zoomLevel.coerceIn(8f, 18f) // Ensure the zoom level is within the valid range
}

fun getMaxDistanceBetweenLatLng(average: LatLng, latLngList: List<LatLng>):Float{
    val results = FloatArray(1)
    var maxDistance = 0f

    for (latLng in latLngList) {
        Location.distanceBetween(
            average.latitude,
            average.longitude,
            latLng.latitude,
            latLng.longitude,
            results
        )
        if (results[0] > maxDistance) {
            maxDistance = results[0]
        }
    }
    return maxDistance
}

data class ClusterData(
    val clusters: List<ClusteredPoints>
)

data class ClusteredPoints(
    val representativePoint: Points,
    val count: Int
)

data class Points(
    val latitude: Double,
    val longitude: Double
)

fun getPolyline(points : List<LatLng>, polylineOptions: PolylineOptions): PolylineOptions {
    for (point in points){
        polylineOptions.add(point)
    }
    return polylineOptions
}

fun averageLatLng(points: List<LatLng>): LatLng {
    var averageLatLng by mutableStateOf<LatLng?>(null)

    var sumLat = 0.0
    var sumLng = 0.0
    for (point in points) {
        sumLat += point.latitude
        sumLng += point.longitude
    }
    val avgLat = sumLat / points.size
    val avgLng = sumLng / points.size

    averageLatLng = LatLng(avgLat, avgLng)

    return if(points.isNotEmpty()) {
        LatLng(avgLat, avgLng)
    }else{
        LatLng(37.503735330931136, 126.95615523253305)
    }
}