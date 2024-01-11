package com.brtbeacon.map3d.demo.map.route;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map.map3d.route.BRTDirectionalHint;
import com.brtbeacon.map.map3d.route.BRTMapRouteManager;
import com.brtbeacon.map.map3d.route.BRTRoutePart;
import com.brtbeacon.map.map3d.route.BRTRouteResult;
import com.brtbeacon.map.map3d.utils.BRTConvert;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.brtbeacon.mapsdk.RoutePart;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.linearref.LengthLocationMap;
import com.vividsolutions.jts.linearref.LinearLocation;

import java.util.Calendar;
import java.util.List;

public class RouteSimNavActivity extends BaseMapActivity {

    private double walkSpeed = 10; //10 Meter Per Second

    private BRTPoint startPoint;
    private BRTPoint endPoint;
    private BRTMapRouteManager routeManager = null;
    private TextView tvHint;
    private Button btnNavSim;
    private Handler handler = new Handler();
    private boolean isNaving = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvHint = findViewById(R.id.tv_hint);
        btnNavSim = findViewById(R.id.btn_nav_sim);
        btnNavSim.setOnClickListener(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_route_hint;
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);

        if (startPoint != null && endPoint != null) {
            if (isNaving)
                return;
            startPoint = null;
            endPoint = null;
            mapView.setRouteResult(null);
            return;
        } else if (startPoint == null) {
            startPoint = point;
        } else if (endPoint == null) {
            endPoint = point;
        }
        mapView.setRouteStart(startPoint);
        mapView.setRouteEnd(endPoint);

        if (startPoint != null && endPoint != null) {
            routeManager.requestRoute(startPoint, endPoint);
            showToast(getString(R.string.toast_route_start));
        }
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error != null) {
            return;
        }
        routeManager = new BRTMapRouteManager(this, mapView.getBuilding(), mapBundle.appkey, mapView.getFloorList(), true);
        routeManager.addRouteManagerListener(routeManagerListener);
    }


    private BRTMapRouteManager.BRTRouteManagerListener routeManagerListener = new BRTMapRouteManager.BRTRouteManagerListener() {
        @Override
        public void didSolveRouteWithResult(BRTMapRouteManager routeManager, BRTRouteResult routeResult) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(getString(R.string.toast_route_success));
                    mapView.setRouteResult(routeResult);
                }
            });
        }

        @Override
        public void didFailSolveRouteWithError(BRTMapRouteManager routeManager, BRTMapRouteManager.BRTRouteException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(e.getMessage());
                }
            });
        }
    };

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_nav_sim: {
                if (mapView.getRouteResult() == null)
                    return;

                if (isNaving) {
                    stopProcessWalk();
                } else {
                    startProcessWalk();
                }
                isNaving = !isNaving;

                //btnNavSim.setEnabled(false);
                break;
            }
        }
    }

    private void startProcessWalk() {
        lastUpdateMillis = Calendar.getInstance().getTimeInMillis();
        walkPart = mapView.getRouteResult().getAllRouteParts().get(0);
        walkPartLength = 0.0;
        btnNavSim.setText("停止导航");
        processNextWalk();
    }

    private void processNextWalk() {
        handler.postDelayed(walkTimeTask, 30);
    }

    private void stopProcessWalk() {
        handler.removeCallbacks(walkTimeTask);
        btnNavSim.setText("模拟导航");
    }

    private BRTRoutePart walkPart = null;
    private double walkPartLength = 0.0;
    private long lastUpdateMillis = 0;
    private Runnable walkTimeTask = new Runnable() {
        @Override
        public void run() {
            if (walkPart == null)
                return;

            if (isFinishing())
                return;

            long currentTimeMillis = Calendar.getInstance().getTimeInMillis();
            long timePeriod = currentTimeMillis - lastUpdateMillis;
            lastUpdateMillis = currentTimeMillis;
            double walkLength = walkSpeed * timePeriod / 1000.0;
            walkPartLength += walkLength;
            RoutePart jtsRoutePart = walkPart.getJtsRoutePart();
            com.vividsolutions.jts.geom.LineString jtsRoute = jtsRoutePart.getRoute();
            double partLength = jtsRoute.getLength();

            while(walkPartLength > partLength) {
                if (walkPart.isLastPart()) {
                    walkPartLength = partLength;
                    break;
                } else {
                    walkPartLength -= partLength;
                    walkPart = walkPart.getNextPart();
                    jtsRoutePart = walkPart.getJtsRoutePart();
                    jtsRoute = jtsRoutePart.getRoute();
                    partLength = jtsRoute.getLength();
                }
            }

            if (walkPart.getFloorInfo().getFloorNumber() != mapView.getCurrentFloor().getFloorNumber()) {
                mapView.setFloorByNumber(walkPart.getFloorInfo().getFloorNumber());
            }


            LengthLocationMap lengthLocationMap = new LengthLocationMap(jtsRoute);
            LinearLocation linearLocation = lengthLocationMap.getLocation(walkPartLength);
            Coordinate coordinate = linearLocation.getCoordinate(jtsRoute);
            LatLng latLng = BRTConvert.toLatLng(coordinate.x, coordinate.y);
            BRTPoint location = new BRTPoint(walkPart.getFloorInfo().getFloorNumber(), latLng);
            mapView.setLocation(location);



            if (walkPartLength < partLength) {
                processNextWalk();
            } else {
                stopProcessWalk();
                showToast(getString(R.string.toast_reach_end));
            }
            showCurrentHint(location);
        }
    };

    private void showCurrentHint(BRTPoint lp) {
        BRTRouteResult routeResult = mapView.getRouteResult();
        BRTRoutePart part = routeResult.getNearestRoutePart(lp);
        if (part != null) {
            List<BRTDirectionalHint> hints = part.getRouteDirectionalHint();
            BRTDirectionalHint hint = part.getDirectionalHintForLocationFromHints(lp, hints);
            if (hint != null) {
                tvHint.setText(getString(R.string.hint_direction) + hint.getDirectionString() + hint.getRelativeDirection() + "\n"
                        + getString(R.string.hint_part_length) + String.format("%.2f", hint.getLength()) + "\n"
                        + getString(R.string.hint_part_angle) + String.format("%.2f", hint.getCurrentAngle()) + "\n"
                        + getString(R.string.hint_route_left_and_length) + String.format("%.2f", routeResult.distanceToRouteEnd(lp)) + "\n"
                        + "/" + String.format("%.2f", routeResult.length));

                mapView.lookAt(lp, hint, 500);
                mapView.processDeviceRotation(90 - hint.getCurrentAngle());
            }
        }
    }

}
