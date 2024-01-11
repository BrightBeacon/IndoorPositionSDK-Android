package com.brtbeacon.map3d.demo.map.event;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;

public class EventClickMapActivity extends BaseMapActivity {

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);
        showToast(getString(R.string.floor_number) + point.getFloorNumber() + "\nLat: " + point.getLatitude() + "\nLng: " + point.getLongitude());
    }
}
