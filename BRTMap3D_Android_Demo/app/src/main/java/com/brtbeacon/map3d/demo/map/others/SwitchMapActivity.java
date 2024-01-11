package com.brtbeacon.map3d.demo.map.others;

import android.os.Bundle;
import android.view.View;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;

public class SwitchMapActivity extends BaseMapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btn_map1).setOnClickListener(this);
        findViewById(R.id.btn_map2).setOnClickListener(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_switch_map;
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        if (error != null) {
            showToast(error.getMessage());
            return;
        }
        mapView.setFloor(mapView.getFloorList().get(0));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_map1: {
                mapView.load("00230083", "1b7877da50bc483789c815910d739402", BRTMapView.MAP_LOAD_MODE_ONLINE);
                break;
            }

            case R.id.btn_map2: {
                mapView.load("00230065", "b5eb0f79b8ef4131ab4879d8164d58ad", BRTMapView.MAP_LOAD_MODE_ONLINE);
                break;
            }
        }
    }
}
