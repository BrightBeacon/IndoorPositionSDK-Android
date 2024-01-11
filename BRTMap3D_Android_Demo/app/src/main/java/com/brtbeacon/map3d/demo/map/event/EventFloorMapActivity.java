package com.brtbeacon.map3d.demo.map.event;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;

public class EventFloorMapActivity extends BaseMapActivity {
    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        super.mapViewDidLoad(mapView, error);
        if (error != null) {
            showToast(getString(R.string.load_map_error)  + " : " + error.getMessage());
        } else {
            mapView.setFloor(mapView.getFloorList().get(0));
        }
    }

    @Override
    public void onFinishLoadingFloor(BRTMapView mapView, BRTFloorInfo floorInfo) {
        super.onFinishLoadingFloor(mapView, floorInfo);
        showToast(getString(R.string.load_floor_success) + " " + floorInfo.getFloorNumber() + ", " + getString(R.string.floor_name) + " : " + floorInfo.getFloorName());
    }

}
