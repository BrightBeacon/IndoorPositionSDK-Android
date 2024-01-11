package com.brtbeacon.map3d.demo.map.annotations;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.mapbox.mapboxsdk.annotations.Polygon;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.LinkedList;
import java.util.List;

public class PolygonMapActivity extends BaseMapActivity {

    private List<LatLng> pointList = new LinkedList<>();

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);
        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
        pointList.add(latLng);
        if (pointList.size() < 2 )
            return;

        for (Polygon polygon: mapView.getMap().getPolygons()) {
            mapView.getMap().removePolygon(polygon);
        }
        mapView.getMap().addPolygon(new PolygonOptions().alpha(0.999f).fillColor(0xFF0000FF).addAll(pointList));
    }
}
