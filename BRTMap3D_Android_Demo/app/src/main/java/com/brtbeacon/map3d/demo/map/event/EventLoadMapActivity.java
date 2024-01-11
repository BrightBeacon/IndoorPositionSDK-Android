package com.brtbeacon.map3d.demo.map.event;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;

public class EventLoadMapActivity extends BaseMapActivity {

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error != null) {
            showToast(getString(R.string.load_map_error) + error.getMessage());
        } else {
            showToast(getString(R.string.load_map_success_floor_count) + mapView.getFloorList().size());
            mapView.setFloor(mapView.getFloorList().get(0));
        }
    }
}
