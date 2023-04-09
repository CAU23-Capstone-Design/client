package com.lovestory.lovestory.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

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

fun getPolyline(points : List<LatLng>, polylineOptions: PolylineOptions): PolylineOptions {
    for (point in points){
        polylineOptions.add(point)
    }
    return polylineOptions
}

fun averageLatLng(points: List<LatLng>): LatLng? {
    if (points.isEmpty()) return null

    var sumLat = 0.0
    var sumLng = 0.0
    for (point in points) {
        sumLat += point.latitude
        sumLng += point.longitude
    }
    val avgLat = sumLat / points.size
    val avgLng = sumLng / points.size

    return LatLng(avgLat, avgLng)
}