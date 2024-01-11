package com.brtbeacon.map3d.demo.map.others;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map.map3d.layer.BRTVectorTiledMapLayersManager;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

public class HeatMapActivity extends BaseMapActivity {

    private final static String HEATMAP_LAYER_SOURCE = "a78c109a-b5ff-4df4-9995-e4c07494609f";
    private final static String HEATMAP_LAYER_ID = "219977f8-e74f-49d6-a26b-a0cf18b59439";

    private Button btnClear;

    private GeoJsonSource heatMapSource;
    private HeatmapLayer heatMapLayer;
    private List<Feature> heatFeatureList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnClear = findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);
        btnClear.setVisibility(View.GONE);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_heat_map;
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        if (error != null) {
            showToast(error.getMessage());
            return;
        }
        initHeatMap(mapView);
        mapView.setFloor(mapView.getFloorList().get(0));
        showToast("请点击任意区域添加热力数据!");
        btnClear.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);
        JsonObject properties = new JsonObject();
        properties.addProperty("floor", point.getFloorNumber());
        heatFeatureList.add(Feature.fromGeometry(Point.fromLngLat(point.getLongitude(), point.getLatitude()), properties));
        heatMapSource.setGeoJson(FeatureCollection.fromFeatures(heatFeatureList));
    }

    private void initHeatMap(BRTMapView mapView) {
        heatMapSource = new GeoJsonSource(HEATMAP_LAYER_SOURCE);
        heatMapSource.setGeoJson(FeatureCollection.fromFeatures(heatFeatureList));
        heatMapLayer = new HeatmapLayer(HEATMAP_LAYER_ID, HEATMAP_LAYER_SOURCE);
        mapView.getMap().getStyle().addSource(heatMapSource);
        mapView.getMap().getStyle().addLayerBelow(heatMapLayer, BRTVectorTiledMapLayersManager.LAYER_FACILITY);
    }

    @Override
    public void onFinishLoadingFloor(BRTMapView mapView, BRTFloorInfo floorInfo) {
        super.onFinishLoadingFloor(mapView, floorInfo);
        heatMapLayer.setFilter(Expression.eq(Expression.get("floor"), floorInfo.getFloorNumber()));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_clear: {
                heatFeatureList.clear();
                heatMapSource.setGeoJson(FeatureCollection.fromFeatures(heatFeatureList));
                break;
            }
        }
    }
}
