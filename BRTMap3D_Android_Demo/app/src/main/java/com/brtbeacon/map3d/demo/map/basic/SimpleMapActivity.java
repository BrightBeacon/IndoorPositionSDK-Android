package com.brtbeacon.map3d.demo.map.basic;

import android.os.Bundle;
import android.os.Handler;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;

public class SimpleMapActivity extends BaseMapActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onFinishLoadingFloor(BRTMapView mapView, BRTFloorInfo floorInfo) {
        super.onFinishLoadingFloor(mapView, floorInfo);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mapView.setTilt(50, 1000, true);
            }
        }, 500);
    }

}
