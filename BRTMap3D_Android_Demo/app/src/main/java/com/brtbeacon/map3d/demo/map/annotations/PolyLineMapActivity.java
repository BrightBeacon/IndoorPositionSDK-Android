package com.brtbeacon.map3d.demo.map.annotations;

import android.os.Bundle;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.LinkedList;
import java.util.List;

public class PolyLineMapActivity extends BaseMapActivity {

    private List<LatLng> pointList = new LinkedList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);
        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
        pointList.add(latLng);
        if (pointList.size() < 2 )
            return;

        for (Polyline polyline: mapView.getMap().getPolylines()) {
            mapView.getMap().removePolyline(polyline);
        }
        mapView.getMap().addPolyline(new PolylineOptions().color(0xFFFF0000).addAll(pointList));
    }

}
