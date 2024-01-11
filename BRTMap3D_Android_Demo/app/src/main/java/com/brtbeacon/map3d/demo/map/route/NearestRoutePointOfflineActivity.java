package com.brtbeacon.map3d.demo.map.route;

import android.os.Bundle;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map.map3d.route.BRTMapRouteManager;
import com.brtbeacon.map.map3d.route.BRTRouteResult;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

public class NearestRoutePointOfflineActivity extends BaseMapActivity {

    private BRTMapRouteManager routeManager = null;
    private Marker marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_route;
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);

        if (routeManager == null)
            return;

        mapView.setLocation(point);
        BRTPoint nearestPoint = routeManager.getNearestRoutePointOffline(point);
        mapView.getMap().deselectMarkers();
        if (marker != null) {
            mapView.getMap().removeMarker(marker);
            marker = null;
        }
        if (nearestPoint != null) {
            marker = mapView.getMap().addMarker(new MarkerOptions().setPosition(nearestPoint.getLatLng()).setTitle("最近路网点"));
        }

    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        routeManager = new BRTMapRouteManager(this, mapView.getBuilding(), mapView.getFloorList());
        routeManager.addRouteManagerListener(routeManagerListener);
    }


    private BRTMapRouteManager.BRTRouteManagerListener routeManagerListener = new BRTMapRouteManager.BRTRouteManagerListener() {
        @Override
        public void didSolveRouteWithResult(BRTMapRouteManager routeManager, BRTRouteResult routeResult) { }

        @Override
        public void didFailSolveRouteWithError(BRTMapRouteManager routeManager, BRTMapRouteManager.BRTRouteException e) { }
    };


}
