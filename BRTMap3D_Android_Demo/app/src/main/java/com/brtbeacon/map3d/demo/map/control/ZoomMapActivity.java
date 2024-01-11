package com.brtbeacon.map3d.demo.map.control;

import android.os.Bundle;
import android.view.View;
import android.widget.ZoomControls;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;

public class ZoomMapActivity extends BaseMapActivity {

    private ZoomControls zoomControls = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zoomControls = findViewById(R.id.zoomControls);
        zoomControls.setEnabled(false);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 放大地图
                 * Zoom Map In
                 * 最大缩放值
                 * mapView.getMap().getMaxZoomLevel();
                 * 当前缩放值
                 * mapView.getZoom();
                 */
                mapView.setZoomIn();
            }
        });

        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 缩小地图
                 * Zoom Map Out
                 * 最小缩放值
                 * mapView.getMap().getMinZoomLevel();
                 * 当前缩放值
                 * mapView.getZoom();
                 */
                mapView.setZoomOut();
            }
        });

        findViewById(R.id.btn_reset).setOnClickListener(this);
        mapView.setLogoVisible(View.GONE);
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error != null)
            return;
        zoomControls.setVisibility(View.VISIBLE);
        zoomControls.setEnabled(true);
        setFloorControlVisible(true);

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_zoom_map;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_reset: {
                mapView.resetCamera();
                break;
            }
        }
    }
}
