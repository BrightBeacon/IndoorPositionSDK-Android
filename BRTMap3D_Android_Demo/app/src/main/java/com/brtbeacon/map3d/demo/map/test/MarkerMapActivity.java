package com.brtbeacon.map3d.demo.map.test;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MarkerMapActivity extends BaseMapActivity {

    private HashMap<String, Icon> iconMap = new HashMap<>();
    private List<Integer> iconDrawableList = new LinkedList<>();
    private int iconIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iconDrawableList.add(R.drawable.location_frame_0);
        iconDrawableList.add(R.drawable.location_frame_1);
        iconDrawableList.add(R.drawable.location_frame_2);
        iconDrawableList.add(R.drawable.location_frame_3);
        iconDrawableList.add(R.drawable.location_frame_4);
        iconDrawableList.add(R.drawable.location_frame_5);
        iconDrawableList.add(R.drawable.location_frame_6);
        iconDrawableList.add(R.drawable.location_frame_7);
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);
        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
        if (iconIndex >= iconDrawableList.size())
            iconIndex = 0;
        String iconKey = String.valueOf(iconIndex);
        Icon icon = iconMap.get(iconKey);
        if (icon == null) {
            icon = IconFactory.recreate(iconKey, BitmapFactory.decodeResource(getResources(), iconDrawableList.get(iconIndex)));
            ++iconIndex;
        }

        Marker marker = mapView.getMap().addMarker(new MarkerOptions().setPosition(latLng).setTitle("Test").setIcon(icon));
        marker.setIcon(icon);
    }
}
