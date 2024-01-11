package com.brtbeacon.map3d.demo.map.route;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map.map3d.layer.BRTVectorTiledMapLayersManager;
import com.brtbeacon.map.map3d.route.BRTMapRouteManager;
import com.brtbeacon.map.map3d.route.BRTRouteResult;
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

public class MultiRouteActivity extends BaseMapActivity {

    private static final String ROUTE_PROGRESS_DIALOG = "route_progress_dialog";

    private BRTPoint startPoint;
    private BRTPoint endPoint;
    private BRTMapRouteManager routeManager = null;
    private CheckBox cboxVehicle;
    private LinkedList<BRTPoint> pointList = new LinkedList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cboxVehicle = findViewById(R.id.cbox_vehicle);
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

        /**
         * 初始化路径规划引擎
         * init Route Manager
         */
        routeManager = new BRTMapRouteManager(this, mapView.getBuilding(), mapBundle.appkey, mapView.getFloorList(), false);
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
                PropertyFactory.symbolAvoidEdges(true),
                PropertyFactory.textField(Expression.get("index_number")),
                PropertyFactory.textAllowOverlap(true),
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
