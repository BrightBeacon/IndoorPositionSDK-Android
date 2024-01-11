package com.brtbeacon.map3d.demo.map.annotations;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;

public class LabelMapActivity extends BaseMapActivity {

    /**
     * 文字源唯一标识
     * Text source unique identification
     */
    private final static String CUSTOM_LABEL_SOURCE = "8b362259-4d9e-4f2d-8c0d-252df7f00349";

    /**
     * 文字层唯一标识
     * Text layer unique identification
     */
    private final static String CUSTOM_LABEL_LAYER = "ddf3741c-5c46-4413-b28f-ca733cbdf893";

    private GeoJsonSource customLabelSource = null;
    private SymbolLayer customLabelLayer = null;



    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);

        JsonObject properties = new JsonObject();
        properties.addProperty("NAME", getString(R.string.insert_label_content));
        properties.addProperty("floor", mapView.getCurrentFloor().getFloorNumber());
        customLabelSource.setGeoJson(
                Feature.fromGeometry(
                        Point.fromLngLat(point.getLongitude(), point.getLatitude()), properties)
        );
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        MapboxMap map = mapView.getMap();
        customLabelSource = new GeoJsonSource(CUSTOM_LABEL_SOURCE);
        map.getStyle().addSource(customLabelSource);
        customLabelLayer = new SymbolLayer(CUSTOM_LABEL_LAYER, CUSTOM_LABEL_SOURCE);
        customLabelLayer.setProperties(
                PropertyFactory.textField(get("NAME")),
                PropertyFactory.textHaloWidth(1.0f),
                PropertyFactory.textHaloColor(0xFF000000),
                PropertyFactory.textColor(0xFFFF00FF),
                PropertyFactory.textAllowOverlap(true)
        );
        map.getStyle().addLayer(customLabelLayer);
        mapView.setFloor(mapView.getFloorList().get(0));
    }

    @Override
    public void onFinishLoadingFloor(BRTMapView mapView, BRTFloorInfo floorInfo) {
        super.onFinishLoadingFloor(mapView, floorInfo);
        updateLayerFilter();
        setFloorControlVisible(true);
    }

    private void updateLayerFilter() {
        customLabelLayer.setFilter(eq(get("floor"), mapView.getCurrentFloor().getFloorNumber()));
    }
}
