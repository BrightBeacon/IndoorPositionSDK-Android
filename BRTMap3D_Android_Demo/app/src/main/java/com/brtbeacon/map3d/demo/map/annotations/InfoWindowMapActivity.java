package com.brtbeacon.map3d.demo.map.annotations;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;

public class InfoWindowMapActivity extends BaseMapActivity {

    /**
     * INFO源唯一标识
     * INFO source unique identification
     */
    private final static String CUSTOM_INFO_SOURCE = "f9e0c973-0e0d-492a-8571-f660fea540dd";

    /**
     * INFO层唯一标识
     * INFO layer unique identification
     */
    private final static String CUSTOM_INFO_LAYER = "cf1253b7-eac4-4597-9592-947726796fd7";

    /**
     * INFO图片资源标识
     * INFO picture resource identification
     */
    private final static String CUSTOM_INFO_NAME = "4f48224e-da52-4f82-b0f6-e36236c96a8c";

    private GeoJsonSource customInfoSource = null;
    private SymbolLayer customInfoLayer = null;

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);

        View infoView = getLayoutInflater().inflate(R.layout.layout_map_info_window, null, false);
        TextView tvName = infoView.findViewById(R.id.tv_name);
        tvName.setText("Floor: " + point.getFloorNumber() + "\nLat: " + point.getLatitude() + "\nLng: " + point.getLongitude());
        Bitmap bitmap = SymbolGenerator.generate(infoView);
        mapView.getMap().getStyle().addImage(CUSTOM_INFO_NAME, bitmap);

        JsonObject properties = new JsonObject();
        properties.addProperty("image-normal", CUSTOM_INFO_NAME);
        properties.addProperty("floor", mapView.getCurrentFloor().getFloorNumber());
        customInfoSource.setGeoJson(
                Feature.fromGeometry(
                        Point.fromLngLat(point.getLongitude(), point.getLatitude()), properties)
        );
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        MapboxMap map = mapView.getMap();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.code);
        map.getStyle().addImage(CUSTOM_INFO_NAME, bitmap);

        customInfoSource = new GeoJsonSource(CUSTOM_INFO_SOURCE);
        map.getStyle().addSource(customInfoSource);
        customInfoLayer = new SymbolLayer(CUSTOM_INFO_LAYER, CUSTOM_INFO_SOURCE);
        customInfoLayer.setProperties(
                PropertyFactory.iconImage(get("image-normal")),
                PropertyFactory.iconIgnorePlacement(true),
                PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                PropertyFactory.textAllowOverlap(true)
        );
        map.getStyle().addLayer(customInfoLayer);
        mapView.setFloor(mapView.getFloorList().get(0));
    }

    @Override
    public void onFinishLoadingFloor(BRTMapView mapView, BRTFloorInfo floorInfo) {
        super.onFinishLoadingFloor(mapView, floorInfo);
        updateLayerFilter();
        setFloorControlVisible(true);
    }

    private void updateLayerFilter() {
        customInfoLayer.setFilter(eq(get("floor"), mapView.getCurrentFloor().getFloorNumber()));
    }

    private void updateInfoWindow(BRTPoint point) {
        View infoView = getLayoutInflater().inflate(R.layout.layout_map_info_window, null, false);
        TextView tvName = infoView.findViewById(R.id.tv_name);
        tvName.setText("我是消息框！");
        mapView.getMap().getStyle().addImage(CUSTOM_INFO_NAME, SymbolGenerator.generate(infoView));
    }


    private static class SymbolGenerator {
        /**
         * Generate a Bitmap from an Android SDK View.
         *
         * @param view the View to be drawn to a Bitmap
         * @return the generated bitmap
         */

        public static Bitmap generate(@NonNull View view) {
            int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(measureSpec, measureSpec);
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();
            view.layout(0, 0, measuredWidth, measuredHeight);
            Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;

        }

    }


}
