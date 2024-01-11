package com.brtbeacon.map3d.demo.map.route;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoiEntity;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map.map3d.route.BRTDirectionalHint;
import com.brtbeacon.map.map3d.route.BRTMapRouteManager;
import com.brtbeacon.map.map3d.route.BRTRoutePart;
import com.brtbeacon.map.map3d.route.BRTRouteResult;
import com.brtbeacon.map.map3d.route.GeometryEngine;
import com.brtbeacon.map.map3d.utils.BRTConvert;
import com.brtbeacon.map.map3d.utils.BRTSearchAdapter;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.brtbeacon.map3d.demo.menu.PoiSearchResultPopupMenu;
import com.brtbeacon.mapsdk.RouteNodeElement;
import com.brtbeacon.mapsdk.RoutePart;
import com.mapbox.geojson.Point;

import java.util.List;

public class RouteSimLocationNavActivity extends BaseMapActivity {

    private BRTPoint startPoint;
    private BRTPoint endPoint;
    private BRTMapRouteManager routeManager = null;
    private TextView tvHint;
    private Button btnReset;
    private BRTSearchAdapter searchAdapter = null;

    private String endPoiId;
    private BRTPoint endDoorPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvHint = findViewById(R.id.tv_hint);
        btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_route_nav_hint;
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);

        if (startPoint != null && endPoint != null) {
            /**
             * 如果已经选择了起点和终点，开始模拟定位
             * If chosen the starting point and end point, start the simulation positioning.
             */
            updateNavLocation(point);
            return;
        } else if (startPoint == null) {
            //  选择起点
            startPoint = point;
            mapView.setRouteStart(startPoint);
        } else if (endPoint == null) {
            //  选择终点
            endPoint = point;
            mapView.setRouteEnd(endPoint);
        }

        if (startPoint != null && endPoint != null) {
            routeManager.requestRoute(startPoint, endPoint, null, null);
            showToast(getString(R.string.toast_route_start));
        }
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error != null) {
            return;
        }
        routeManager = new BRTMapRouteManager(this, mapView.getBuilding(), mapBundle.appkey, mapView.getFloorList(), false);
        routeManager.addRouteManagerListener(routeManagerListener);
        layoutSearchControl.setVisibility(View.VISIBLE);
        searchAdapter = new BRTSearchAdapter(this, mapView.getBuilding().getBuildingID());
    }

    @Override
    protected void onSearchTextChanged(String content) {
        super.onSearchTextChanged(content);
        if (!TextUtils.isEmpty(content)) {
            List<BRTPoiEntity> entityList = searchAdapter.queryPoi(content);
            System.out.println(entityList);
            PoiSearchResultPopupMenu.show(this, layoutSearchControl, entityList, onEntityItemClickListener);
        }
    }

    private PoiSearchResultPopupMenu.OnEntityItemClickListener onEntityItemClickListener = new PoiSearchResultPopupMenu.OnEntityItemClickListener() {
        @Override
        public void onItemClick(BRTPoiEntity entityInfo) {
            endPoint = new BRTPoint(entityInfo.getFloorNumber(), BRTConvert.toLatLng(entityInfo.getLabelX(), entityInfo.getLabelY()));
            mapView.setRouteEnd(endPoint);
            endPoiId = entityInfo.getPoiId();
            if (startPoint != null && endPoint != null) {
                routeManager.requestRoute(startPoint, endPoint);
            }
        }
    };


    private BRTMapRouteManager.BRTRouteManagerListener routeManagerListener = new BRTMapRouteManager.BRTRouteManagerListener() {
        @Override
        public void didSolveRouteWithResult(BRTMapRouteManager routeManager, BRTRouteResult routeResult) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(getString(R.string.toast_route_success));
                    mapView.setRouteResult(routeResult);
                    endDoorPoint = calcEndDoorPoint(endPoiId, routeResult);
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

    private BRTPoint calcEndDoorPoint(String endPoiId, BRTRouteResult routeResult) {
        if (TextUtils.isEmpty(endPoiId))
            return null;

        List<RoutePart> partList = routeResult.getJtsRouteResult().getAllRouteParts();
        for (int index = partList.size()-1; index >= 0; --index) {
            RoutePart part = partList.get(index);
            for (RouteNodeElement nodeElement: part.getNodeElements()) {
                if ("门".equalsIgnoreCase(nodeElement.getName()) && endPoiId.equalsIgnoreCase(nodeElement.getPoiId())) {
                    //  查找到目标区域 门POI。
                    return new BRTPoint(nodeElement.getFloor(), BRTConvert.toLatLng(nodeElement.getX(), nodeElement.getY()));
                }
            }
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_reset: {
                startPoint = null;
                endPoint = null;
                mapView.setRouteStart(null);
                mapView.setRouteEnd(null);
                mapView.setRouteResult(null);
                break;
            }
        }
    }

    private void updateNavLocation(BRTPoint point) {
        BRTPoint locationPoint = point;
        BRTRouteResult routeResult = mapView.getRouteResult();

        if (routeResult != null && point != null) {
            if (routeResult.isDeviatingFromRoute(locationPoint, 5)) {
                showToast("路径偏航，请重新规划！");
            } else {
                /**
                 * 如果定位点距离终点小于0.5米，直接提示到达终点。
                 * If the positioning point is less than 0.5 meters from the end point, it will indicate the destination.
                 */
                if (mapView.getRouteResult().distanceToRouteEnd(locationPoint) < 3) {
                    showToast(getString(R.string.toast_reach_end));
                } else {
                    BRTPoint nearestPoint = routeResult.getNearestPointOnRoute(locationPoint);
                    if (nearestPoint == null) {
                        /**
                         * 定位点不在路径经过楼层，请重新规划路径
                         * The location is not on the path through the floor. Please re plan the path.
                         */
                        showToast(getString(R.string.toast_nearest_point_error));
                    } else if (GeometryEngine.distance(BRTConvert.toPoint(locationPoint), BRTConvert.toPoint(nearestPoint)) > 8) {
                        /**
                         * 位置偏差大于8米，重新规划路径；
                         * The location and path deviations are too large. Please re plan the path.
                         */
                        showToast(getString(R.string.toast_location_deviation));
                        locationPoint = nearestPoint;
                    } else {
                        //  将定位点设置为路径上最近点，达到路径吸附效果；
                        locationPoint = nearestPoint;
                        updateRouteHint(locationPoint);
                    }
                }
            }

            if (endDoorPoint != null &&
                    locationPoint.getFloorNumber() == endDoorPoint.getFloorNumber() &&
                    locationPoint.getLatLng().distanceTo(endDoorPoint.getLatLng()) < 6) {
                showToast("已经到达目前门口！");
            }

        }
        mapView.setLocation(locationPoint);
    }

    private void updateRouteHint(BRTPoint lp) {
        BRTRouteResult routeResult = mapView.getRouteResult();
        BRTRoutePart part = routeResult.getNearestRoutePart(lp);
        if (part != null) {
            List<BRTDirectionalHint> hints = part.getRouteDirectionalHint();
            BRTDirectionalHint hint = part.getDirectionalHintForLocationFromHints(lp, hints);

            if (hint != null) {
                BRTDirectionalHint nextHint = hint.getNextHint();
                String nextDirection = nextHint != null ? nextHint.getDirectionString() + nextHint.getRelativeDirection() : "";
                tvHint.setText(getString(R.string.hint_direction) + hint.getDirectionString() + hint.getRelativeDirection() + "\n"
                        + getString(R.string.hint_next_direction) + nextDirection + "\n"
                        + getString(R.string.hint_part_length) + String.format("%.2f", hint.getLength()) + "\n"
                        + "本段剩余长度：" + String.format("%.2f", getDistanceToHintEnd(hint, lp))
                        + getString(R.string.hint_part_angle) + String.format("%.2f", hint.getCurrentAngle()) + "\n"
                        + getString(R.string.hint_route_left_and_length) + String.format("%.2f", routeResult.distanceToRouteEnd(lp)) + "/" + String.format("%.2f", routeResult.length));
                mapView.lookAt(lp, hint, 500);
            }
        }
    }

    private double getDistanceToHintEnd(BRTDirectionalHint hint, BRTPoint point) {
        return GeometryEngine.distance(Point.fromLngLat(point.getLongitude(), point.getLatitude()), hint.getEndPoint());
    }

}
