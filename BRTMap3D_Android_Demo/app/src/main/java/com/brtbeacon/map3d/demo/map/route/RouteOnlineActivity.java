package com.brtbeacon.map3d.demo.map.route;

import android.os.Bundle;
import android.util.Log;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoi;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map.map3d.route.BRTDirectionalHint;
import com.brtbeacon.map.map3d.route.BRTMapRouteManager;
import com.brtbeacon.map.map3d.route.BRTRoutePart;
import com.brtbeacon.map.map3d.route.BRTRouteResult;
import com.brtbeacon.map.map3d.route.GeometryEngine;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.mapbox.geojson.Point;

import java.util.List;

public class RouteOnlineActivity extends BaseMapActivity {

    private BRTPoint startPoint;
    private BRTPoint endPoint;
    private BRTMapRouteManager routeManager = null;

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

        if (startPoint != null && endPoint != null) {
            double toEnd = mapView.getRouteResult().distanceToRouteEnd(point);
            Log.e(TAG,"终点距离：" + toEnd);
            if (mapView.getRouteResult().isDeviatingFromRoute(point,8)) {
                startPoint = point;
                mapView.setRouteResult(null);
                showToast("你已偏航，重新规划路线。");
            }else if(toEnd < 8) {
                showToast("已到达终点附近,本次导航结束。");
                startPoint = null;
                endPoint = null;
                return;
            }else {
                mapView.setLocation(point);
                mapView.showRoutePassed(point);
                BRTRoutePart part = mapView.getRouteResult().getNearestRoutePart(point);
                //移动位置超过2米，进行导航提示
                List<BRTDirectionalHint> hints = part.getRouteDirectionalHint(0, 10);
                if (hints.size() > 0) {
                    BRTDirectionalHint hint = part.getDirectionalHintForLocationFromHints(point, hints);
                    //计算当前位置点，距离本段结束点、终点距离
                    double len2End = GeometryEngine.distance(hint.getEndPoint(), point.getPoint());
                    if (hint.getLength() <= 10 || len2End <= 10) {
                        if (hint.getNextHint() != null)
                            showToast("前方" + hint.getNextHint().getDirectionString());
                        else if (part.getNextPart() != null)
                            showToast("前方乘扶梯到" + part.getNextPart().getFloorInfo().getFloorName() + "楼");
                        else showToast("即将到达终点");
                    } else {
                        //当前路段中间，或含小弯道(依据getRouteDirectionalHintsIgnoreDistance:angle:)或直行部分
                        showToast("沿路前行" + String.format("%.1f", len2End) + "米");
                    }
                }
                return;
            }
        } else if (startPoint == null) {
            startPoint = point;
        } else if (endPoint == null) {
            endPoint = point;
        }
        mapView.setRouteStart(startPoint);
        mapView.setRouteEnd(endPoint);

        if (startPoint != null && endPoint != null) {
            showToast(getString(R.string.toast_route_start));
            routeManager.requestRoute(startPoint, endPoint);
        }
    }

    @Override
    public void onPoiSelected(BRTMapView mapView, List<BRTPoi> points) {
        super.onPoiSelected(mapView, points);
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        if (error != null) {
            return;
        }
        /**
         * 初始化路径规划引擎
         * init Route Manager
         */
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
                    startPoint = null;
                    endPoint = null;
                }
            });
        }
    };


}
