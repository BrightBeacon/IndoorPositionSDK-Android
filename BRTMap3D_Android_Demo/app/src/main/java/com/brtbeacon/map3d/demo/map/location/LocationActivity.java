package com.brtbeacon.map3d.demo.map.location;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.brtbeacon.locationengine.ble.BRTBeacon;
import com.brtbeacon.locationengine.ble.BRTLocationManager;
import com.brtbeacon.locationengine.ble.BRTPublicBeacon;
import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map.map3d.utils.BRTConvert;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.brtbeacon.mapdata.BRTLocalPoint;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Calendar;
import java.util.List;

public class LocationActivity extends BaseMapActivity {

    private BRTLocationManager locationManager;
    private View btnLocation;
    private long lastLocationStartTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnLocation = findViewById(R.id.btn_location);
        btnLocation.setOnClickListener(this);
        btnLocation.setVisibility(View.GONE);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_location;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_location: {
                if (!view.isSelected()) {
                    long now = Calendar.getInstance().getTimeInMillis();
                    long period = now - lastLocationStartTimeMillis;
                    if (period < 12 * 1000) {
                        long wt = 12 - period / 1000;
                        if (wt < 1)
                            wt = 1;
                        Toast.makeText(this, "定位开关太频繁,请等待" + wt + "秒后重试!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                view.setSelected(!view.isSelected());
                if (view.isSelected()) {
                    startLocation();
                    view.setBackground(getResources().getDrawable(R.drawable.btn_locate_on));
                } else {
                    stopLocation();
                    mapView.setLocation(null);
                    view.setBackground(getResources().getDrawable(R.drawable.btn_locate_off));
                }
                break;
            }
        }
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_arrow);
        mapView.setLocationImage(bitmap);
        locationManager = new BRTLocationManager(this, mapBundle.buildingId, mapBundle.appkey);
        locationManager.addLocationEngineListener(locationManagerListener);
        locationManager.setLimitBeaconNumber(true);
        locationManager.setMaxBeaconNumberForProcessing(1);
        btnLocation.setVisibility(View.VISIBLE);

        checkBluetooth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
    }

    private void checkBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            showToast(getString(R.string.error_bluetooth_off));
            return;
        }

        if (!adapter.isEnabled()) {
            adapter.enable();
        }

    }


    private void startLocation() {
        if (locationManager != null) {
            locationManager.startUpdateLocation();
            lastLocationStartTimeMillis = Calendar.getInstance().getTimeInMillis();
        }
    }

    private void stopLocation() {
        if (locationManager != null) {
            locationManager.stopUpdateLocation();
        }
    }

    private BRTLocationManager.BRTLocationManagerListener locationManagerListener = new BRTLocationManager.BRTLocationManagerListener() {

        @Override
        public void didRangedBeacons(BRTLocationManager BRTLocationManager, List<BRTBeacon> list) {
            System.out.println("didRangedBeacons");
        }

        @Override
        public void didRangedLocationBeacons(BRTLocationManager BRTLocationManager, List<BRTPublicBeacon> list) {
            System.out.println("didRangedLocationBeacons");
            mapView.getMap().clear();
            IconFactory iconFactory = IconFactory.getInstance(LocationActivity.this);
            for (int i=0; i< list.size();i++) {
                BRTPublicBeacon pb = list.get(i);
                Icon icon = iconFactory.fromBitmap(creatCodeBitmap(pb.getMinor()+"_"+pb.getRssi()+"("+i+")",LocationActivity.this));
                mapView.getMap().addMarker(new MarkerOptions()
                        .position(BRTConvert.toLatLng(pb.getLocation().getX(),pb.getLocation().getY()))
                        .icon(icon)
                );
            }
        }

        @Override
        public void didFailUpdateLocation(BRTLocationManager BRTLocationManager, final Error error) {
            System.out.println("didFailUpdateLocation");
        }

        @Override
        public void didUpdateDeviceHeading(BRTLocationManager BRTLocationManager, double v) {
            System.out.println("didUpdateDeviceHeading");
            mapView.processDeviceRotation(v);
        }

        @Override
        public void didUpdateImmediateLocation(BRTLocationManager BRTLocationManager, final BRTLocalPoint tyLocalPoint) {
            System.out.println("didUpdateImmediateLocation");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LatLng pointLL = BRTConvert.toLatLng(tyLocalPoint.getX(), tyLocalPoint.getY());
                    BRTPoint point = new BRTPoint(tyLocalPoint.getFloor(), pointLL.getLatitude(), pointLL.getLongitude());
                    mapView.setLocation(point);
                    if (mapView.getCurrentFloor().getFloorNumber() != tyLocalPoint.getFloor()) {
                        mapView.setFloorByNumber(tyLocalPoint.getFloor());
                    }


                    LatLng latLng = BRTConvert.toLatLng(tyLocalPoint.getX(), tyLocalPoint.getY());
                    System.out.println(tyLocalPoint);
                    System.out.println(latLng);
                }
            });
        }

        @Override
        public void didUpdateLocation(BRTLocationManager BRTLocationManager, BRTLocalPoint tyLocalPoint) {
            System.out.println("didUpdateLocation");

        }
    };

    /**
     * 将文字 生成 文字图片 生成显示编码的Bitmap,目前这个方法是可用的
     *
     * @param contents
     * @param context
     * @return
     */
    public  Bitmap creatCodeBitmap(String contents ,Context context) {
        float scale=context.getResources().getDisplayMetrics().scaledDensity;

        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setTextSize(scale*5);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());


        //tv.setBackgroundColor(Color.GREEN);

        tv.buildDrawingCache();
        Bitmap bitmapCode = tv.getDrawingCache();
        return bitmapCode;
    }

    static {
        System.loadLibrary("BRTLocationEngine");
    }
}

