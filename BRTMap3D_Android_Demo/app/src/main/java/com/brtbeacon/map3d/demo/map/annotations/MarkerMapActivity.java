package com.brtbeacon.map3d.demo.map.annotations;

import android.os.Bundle;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class MarkerMapActivity extends BaseMapActivity {

    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);
        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
        if (marker == null) {
            marker = mapView.getMap().addMarker(new MarkerOptions().setPosition(latLng).setTitle("Test"));
        } else {
            marker.setPosition(latLng);
        }
    }
}
