package com.brtbeacon.map3d.demo.map.oper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoi;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.brtbeacon.map3d.demo.entity.MapBundle;

import java.util.List;

public class PoiMapActivity extends BaseMapActivity {

    public static void startActivity(Context context, String buildingId, String appkey) {
        Intent intent = new Intent(context, PoiMapActivity.class);
        MapBundle mapBundle = new MapBundle();
        mapBundle.buildingId = buildingId;
        mapBundle.appkey = appkey;
        intent.putExtra(ARG_MAP_BUNDLE, mapBundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        setFloorControlVisible(true);
    }

    @Override
    public void onPoiSelected(BRTMapView mapView, List<BRTPoi> points) {
        super.onPoiSelected(mapView, points);
        if (points.isEmpty())
            return;


        BRTPoi highlistPoi = points.get(0);
        mapView.highlightPoi(highlistPoi);
    }
}
