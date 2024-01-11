package com.brtbeacon.map3d.demo.map.route;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map.map3d.layer.BRTVectorTiledMapLayersManager;
import com.brtbeacon.map.map3d.route.BRTDirectionalHint;
import com.brtbeacon.map.map3d.route.BRTMapRouteManager;
import com.brtbeacon.map.map3d.route.BRTRoutePart;
import com.brtbeacon.map.map3d.route.BRTRouteResult;
import com.brtbeacon.map.map3d.route.GeometryEngine;
import com.brtbeacon.map.map3d.utils.BRTConvert;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MultiRouteNavActivity extends BaseMapActivity {

    private static final String ROUTE_PROGRESS_DIALOG = "route_progress_dialog";

    private BRTPoint startPoint;
    private BRTPoint endPoint;
    private BRTMapRouteManager routeManager = null;
    private CheckBox cboxVehicle;
    private LinkedList<BRTPoint> pointList = new LinkedList<>();
    private TextView tvHint;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cboxVehicle = findViewById(R.id.cbox_vehicle);
        tvHint = findViewById(R.id.tv_hint);
        findViewById(R.id.btn_route_start).setOnClickListener(this);
        findViewById(R.id.btn_route_stop).setOnClickListener(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_multi_route_v3;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_route_start: {
                    if (pointList.size() < 2) {
                        showToast("点数量小于2，请添加更多的点！");
                        return;
                    }
                    showProgressDialog("路径规划", "正在规划路径，请等待！", ROUTE_PROGRESS_DIALOG);
                    startPoint = pointList.getFirst();
                    endPoint = pointList.getLast();
                    List<BRTPoint> stops = new ArrayList<>();
                    if (pointList.size() > 2) {
                        stops.addAll(pointList.subList(1, pointList.size() - 1));
                    }
                    routeManager.requestRoute(startPoint, endPoint, stops, false, false, null);
                break;
            }

            case R.id.btn_route_stop: {
                mapView.getMap().clear();
                mapView.setMultipleRouteResult(null);
                mapView.setRouteResult(null);
                mapView.showRoutePassed(null);
                pointList.clear();
                clearRoutePoints();
                break;
            }
        }
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);

        if (mapView.getMultipleRouteResult() == null) {
            pointList.add(point);
            addRoutePoint(point);
            return;
        } else {
            //mapView.setLocation(point);
            updateNavLocation(point);
        }
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        //super.mapViewDidLoad(mapView, error);
        if (error != null) {
            return;
        }

        mapView.getFacilityLayer().setProperties(PropertyFactory.iconSize(0.5f));
        mapView.getLabelLayer().setProperties(PropertyFactory.iconSize(0.5f));

        mapView.getMap().getStyle().addImage("route_point", BitmapFactory.decodeResource(getResources(), R.drawable.location_frame_0));
        initLayers(mapView);

        mapView.setMultipleRouteColor(0xFF0000FF);

        /**
         * 初始化路径规划引擎
         * init Route Manager
         */
        routeManager = new BRTMapRouteManager(this, mapView.getBuilding(), mapBundle.appkey, mapView.getFloorList(), true);
        routeManager.addRouteManagerListener(routeManagerListener);

        mapView.setFloor(mapView.getFloorList().get(0));
    }

    @Override
    public void onFinishLoadingFloor(BRTMapView mapView, BRTFloorInfo floorInfo) {
        super.onFinishLoadingFloor(mapView, floorInfo);
        routePointLayer.setFilter(Expression.eq(Expression.get("floor"), floorInfo.getFloorNumber()));
    }

    private BRTMapRouteManager.BRTRouteManagerListener routeManagerListener = new BRTMapRouteManager.BRTRouteManagerListener() {

        @Override
        public void didSolveRouteWithResult(BRTMapRouteManager routeManager, BRTRouteResult routeResult) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeProgressDialog(ROUTE_PROGRESS_DIALOG);
                    showToast(getString(R.string.toast_route_success));
                    mapView.setMultipleRouteResult(routeResult);
                    mapView.setRouteResult(routeResult);

                    BRTRouteResult result = routeResult;
                    double routeLen = 0;
                    while (result != null) {
                        routeLen += result.length;
                        result = result.getNextRouteResult();
                    }
                    System.out.println(routeLen);
                }
            }, 500);
        }

        @Override
        public void didFailSolveRouteWithError(BRTMapRouteManager routeManager, BRTMapRouteManager.BRTRouteException e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeProgressDialog(ROUTE_PROGRESS_DIALOG);
                    showToast(e.getMessage());
                }
            }, 500);
        }
    };

    private void updateNavLocation(BRTPoint point) {
        BRTPoint locationPoint = point;
        BRTRouteResult routeResult = mapView.getRouteResult();

        if (routeResult != null && point != null) {

            if (routeResult.isDeviatingFromRoute(locationPoint, 5)) {
                showToast("路径偏航，请重新规划！");
            } else {
                /**
                 * 如果定位点距离终点小于3米，直接提示到达终点。
                 * If the positioning point is less than 0.5 meters from the end point, it will indicate the destination.
                 */
                if (mapView.getRouteResult().distanceToRouteEnd(locationPoint) < 3) {
                    if (mapView.getRouteResult() != null && mapView.getRouteResult().getNextRouteResult()!=null){
                        mapView.setRouteResult(mapView.getRouteResult().getNextRouteResult());
                        mapView.showRoutePassed(null);
                    } else {
                        showToast(getString(R.string.toast_reach_end));
                    }
                    updateRouteHint(locationPoint);
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
        }
        mapView.setLocation(locationPoint);
        mapView.showRoutePassed(locationPoint);
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
                        + "本段剩余长度：" + String.format("%.2f", getDistanceToHintEnd(hint, lp)) + "\n"
                        + getString(R.string.hint_part_angle) + String.format("%.2f", hint.getCurrentAngle()) + "\n"
                        + getString(R.string.hint_route_left_and_length) + String.format("%.2f", routeResult.distanceToRouteEnd(lp)) + "/" + String.format("%.2f", routeResult.length));
                mapView.lookAt(lp, hint, 500);
            }
        }
    }

    private double getDistanceToHintEnd(BRTDirectionalHint hint, BRTPoint point) {
        return GeometryEngine.distance(Point.fromLngLat(point.getLongitude(), point.getLatitude()), hint.getEndPoint());
    }

    //  多点位图层与数据源
    private GeoJsonSource routePointSource;
    private SymbolLayer routePointLayer;
    private final static String ROUTE_POINT_SOURCE_ID = "24f6ce6f-bef7-4dc5-b351-af357d0b374f";
    private final static String ROUTE_POINT_LAYER_ID = "ec35298c-23c6-464c-80fb-5ccf679b00b4";
    List<Feature> featureList = new ArrayList<>();

    private void initLayers(BRTMapView mapView) {
        routePointSource = new GeoJsonSource(ROUTE_POINT_SOURCE_ID);
        routePointLayer = new SymbolLayer(ROUTE_POINT_LAYER_ID, ROUTE_POINT_SOURCE_ID);
        routePointLayer.setProperties(
                PropertyFactory.textField(Expression.get("index_number")),
                PropertyFactory.iconImage("route_point"),
                PropertyFactory.iconSize(0.75f),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.symbolAvoidEdges(true),
                PropertyFactory.iconIgnorePlacement(true));

        mapView.getMap().getStyle().addSource(routePointSource);
        mapView.getMap().getStyle().addLayerBelow(routePointLayer, BRTVectorTiledMapLayersManager.LAYER_LABEL);
    }

    private void addRoutePoint(BRTPoint point) {
        JsonObject properties = new JsonObject();
        properties.addProperty("index_number", String.valueOf(featureList.size()));
        properties.addProperty("floor", point.getFloorNumber());
        Feature feature = Feature.fromGeometry(Point.fromLngLat(point.getLongitude(), point.getLatitude()), properties);
        featureList.add(feature);
        updateRoutePointLayer();
    }

    private void updateRoutePointLayer() {
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(featureList);
        routePointSource.setGeoJson(featureCollection);
    }

    private void clearRoutePoints() {
        featureList.clear();
        updateRoutePointLayer();
    }


}
