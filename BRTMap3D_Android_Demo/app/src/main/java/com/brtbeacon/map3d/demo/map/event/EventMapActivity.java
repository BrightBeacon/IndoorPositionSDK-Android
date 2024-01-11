package com.brtbeacon.map3d.demo.map.event;

import android.os.Bundle;
import android.widget.TextView;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.mapbox.android.gestures.RotateGestureDetector;
import com.mapbox.android.gestures.StandardScaleGestureDetector;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class EventMapActivity extends BaseMapActivity {

    private TextView tvScaleLog;
    private TextView tvRotateLog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_map_event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvScaleLog = findViewById(R.id.tv_scale_info);
        tvRotateLog = findViewById(R.id.tv_rotate_info);
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);

        /**
         * 添加地图旋转事件监听
         * add map rotation event monitor
         */
        mapView.getMap().addOnRotateListener(onRotateListener);
        //

        /**
         * 添加地图缩放事件监听
         * add map zoom event monitor
         */
        mapView.getMap().addOnScaleListener(onScaleListener);
    }

    private MapboxMap.OnRotateListener onRotateListener = new MapboxMap.OnRotateListener() {
        @Override
        public void onRotateBegin(RotateGestureDetector rotateGestureDetector) {

        }

        @Override
        public void onRotate(RotateGestureDetector rotateGestureDetector) {
            CameraPosition cameraPosition = mapView.getMap().getCameraPosition();
            tvRotateLog.setText(getString(R.string.rotate_angle) + cameraPosition.bearing);
        }

        @Override
        public void onRotateEnd(RotateGestureDetector rotateGestureDetector) {

        }
    };

    private MapboxMap.OnScaleListener onScaleListener = new MapboxMap.OnScaleListener() {
        @Override
        public void onScaleBegin(StandardScaleGestureDetector standardScaleGestureDetector) {

        }

        @Override
        public void onScale(StandardScaleGestureDetector standardScaleGestureDetector) {
            CameraPosition cameraPosition = mapView.getMap().getCameraPosition();
            tvScaleLog.setText(getString(R.string.zoom_levels) + cameraPosition.zoom);
        }

        @Override
        public void onScaleEnd(StandardScaleGestureDetector standardScaleGestureDetector) {

        }
    };
}
