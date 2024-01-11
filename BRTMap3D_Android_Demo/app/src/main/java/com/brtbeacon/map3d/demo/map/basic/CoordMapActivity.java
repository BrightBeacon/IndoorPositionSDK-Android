package com.brtbeacon.map3d.demo.map.basic;

import android.graphics.PointF;
import android.os.Bundle;
import android.widget.TextView;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.ProjectedMeters;

import java.text.DecimalFormat;

public class CoordMapActivity extends BaseMapActivity {

    private TextView tvLatLngCoord;
    private TextView tvScreenCoord;
    private TextView tvMetersCoord;

    private DecimalFormat df = new DecimalFormat("0.######");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvLatLngCoord = findViewById(R.id.tv_latlng_coord);
        tvScreenCoord = findViewById(R.id.tv_screen_coord);
        tvMetersCoord = findViewById(R.id.tv_meter_coord);
    }

    protected int getContentViewId() {
        return R.layout.activity_coord_map;
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);
        /**
         *  转换成经纬坐标
         *  Conversion into latitude and longitude coordinates
         */
        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());

        /**
         *  将经纬坐标转换屏幕坐标
         *  Converting the latitude and longitude coordinates to the screen coordinates
         */
        PointF screenCoord = mapView.getMap().getProjection().toScreenLocation(latLng);

        /**
         *  将经纬坐标转换成墨卡托投影坐标
         *  Transform the latitude and longitude coordinates into the Mercator projection coordinates
         */
        ProjectedMeters metersCoord = mapView.getMap().getProjection().getProjectedMetersForLatLng(latLng);

        /**
         * 将墨卡托投影坐标转换成经纬坐标
         * Transform the Mercator projection coordinates into the latitude and longitude coordinates
         */
        LatLng latLng1 = mapView.getMap().getProjection().getLatLngForProjectedMeters(metersCoord);



        tvScreenCoord.setText(String.format("x: %s, y: %s", (int)screenCoord.x, (int)screenCoord.y));
        tvLatLngCoord.setText(String.format("lat: %s, lng: %s", df.format(latLng.getLatitude()), df.format(latLng.getLongitude())));
        tvMetersCoord.setText(String.format("x: %s, y: %s", df.format(metersCoord.getEasting()), df.format(metersCoord.getNorthing())));

    }

}
