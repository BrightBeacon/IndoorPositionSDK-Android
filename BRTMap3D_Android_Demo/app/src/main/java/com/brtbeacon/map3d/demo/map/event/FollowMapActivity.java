package com.brtbeacon.map3d.demo.map.event;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class FollowMapActivity extends BaseMapActivity {

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);

        MapboxMap map = mapView.getMap();

        /***
         * 设置并显示定位点；
         * set and display location point
         */
        mapView.setLocation(point);

        /**
         * 获取当前摄像机位置
         * get current camera position
         */
        CameraPosition currentPosition = map.getCameraPosition();

        /**
         *
         * 创建目标位置 LatLng
         * create target location latlng
         */
        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());

        /**
         * 创建新的摄像机位置
         * create a new camera position
         */
        CameraPosition newPosition = new CameraPosition.Builder()
                /**
                 * 设置目标位置（定位点）
                 * set the target position (location point)
                 */
                .target(latLng)
                .bearing(currentPosition.bearing)
                .tilt(currentPosition.tilt)
                .zoom(currentPosition.zoom)
                .build();

        map.easeCamera(CameraUpdateFactory.newCameraPosition(newPosition), 500);
    }
}
