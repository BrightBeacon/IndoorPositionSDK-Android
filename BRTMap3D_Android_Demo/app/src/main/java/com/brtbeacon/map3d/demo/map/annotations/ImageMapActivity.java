package com.brtbeacon.map3d.demo.map.annotations;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

public class ImageMapActivity extends BaseMapActivity {

    /**
     * 图片源唯一标识
     * Image source unique identification
     */
    private final static String CUSTOM_IMAGE_SOURCE = "0a759f39-94b1-45ec-8db9-42f80322139d";

    /**
     * 图片层唯一标识
     * Image layer unique identification
     */
    private final static String CUSTOM_IMAGE_LAYER = "2ac66adc-8fc7-41b5-a492-f354f4f01736";

    /**
     * 图片资源标识
     * Picture resource identification
     */
    private final static String CUSTOM_IMAGE_NAME = "image_brtbeacon_logo";

    private GeoJsonSource customImageSource = null;
    private SymbolLayer customImageLayer = null;



    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);

        JsonObject properties = new JsonObject();
        properties.addProperty("image-normal", CUSTOM_IMAGE_NAME);
        properties.addProperty("floor", mapView.getCurrentFloor().getFloorNumber());
        customImageSource.setGeoJson(
                Feature.fromGeometry(
                        Point.fromLngLat(point.getLongitude(), point.getLatitude()), properties)
        );
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        MapboxMap map = mapView.getMap();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.code);
        map.getStyle().addImage(CUSTOM_IMAGE_NAME, bitmap);

        customImageSource = new GeoJsonSource(CUSTOM_IMAGE_SOURCE);
        map.getStyle().addSource(customImageSource);
        customImageLayer = new SymbolLayer(CUSTOM_IMAGE_LAYER, CUSTOM_IMAGE_SOURCE);
        customImageLayer.setProperties(
                PropertyFactory.iconImage(get("image-normal")),
                PropertyFactory.textAllowOverlap(true),
                PropertyFactory.iconSize(0.75f)
        );
        map.getStyle().addLayer(customImageLayer);
        mapView.setFloor(mapView.getFloorList().get(0));
    }

    @Override
    public void onFinishLoadingFloor(BRTMapView mapView, BRTFloorInfo floorInfo) {
        super.onFinishLoadingFloor(mapView, floorInfo);
        updateLayerFilter();
        setFloorControlVisible(true);
    }

    private void updateLayerFilter() {
        customImageLayer.setFilter(eq(get("floor"), mapView.getCurrentFloor().getFloorNumber()));
    }

}
