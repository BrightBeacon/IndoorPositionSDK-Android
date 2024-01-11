package com.brtbeacon.map3d.demo.map.annotations;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.LinkedList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;

public class PolygonComplexMapActivity extends BaseMapActivity {

    /**
     * 多边形源唯一标识
     * polygon source unique identification
     */
    private final static String CUSTOM_POLYGON_SOURCE = "7e4e840a-6ec3-4007-b9d2-a6a21589bc88";

    /**
     * 多边形层唯一标识
     * polygon layer unique identification
     */
    private final static String CUSTOM_POLYGON_LAYER = "968565a9-ec19-45f6-8dda-6383a04b1731";

    private GeoJsonSource polygonSource;
    private FillLayer polygonLayer = null;
    private List<LatLng> pointList = new LinkedList<>();

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        MapboxMap map = mapView.getMap();

        polygonSource = new GeoJsonSource(CUSTOM_POLYGON_SOURCE);
        map.getStyle().addSource(polygonSource);

        polygonLayer = new FillLayer(CUSTOM_POLYGON_LAYER, CUSTOM_POLYGON_SOURCE);
        polygonLayer.setProperties(
                PropertyFactory.fillOutlineColor(0xFFFF0000),
                PropertyFactory.fillColor(0xFFFFFF00),
                PropertyFactory.fillOpacity(0.99f)
        );
        map.getStyle().addLayer(polygonLayer);

        mapView.setFloor(mapView.getFloorList().get(0));
    }

    @Override
    public void onFinishLoadingFloor(BRTMapView mapView, BRTFloorInfo floorInfo) {
        super.onFinishLoadingFloor(mapView, floorInfo);
        updateLayerFilter();
        setFloorControlVisible(true);
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {
        super.onClickAtPoint(mapView, point);

        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
        pointList.add(latLng);

        if (pointList.size() < 3)
            return;

        LinkedList<Point> lngLatList = new LinkedList<>();
        for (LatLng polygonPoint : pointList) {
            lngLatList.add(Point.fromLngLat(polygonPoint.getLongitude(), polygonPoint.getLatitude()));
        }
        lngLatList.add(lngLatList.getFirst());

        List<List<Point>> latLngsList = new LinkedList<>();
        latLngsList.add(lngLatList);

        JsonObject properties = new JsonObject();
        properties.addProperty("floor", mapView.getCurrentFloor().getFloorNumber());
        polygonSource.setGeoJson(Feature.fromGeometry(Polygon.fromLngLats(latLngsList), properties));

    }

    private void updateLayerFilter() {
        polygonLayer.setFilter(eq(get("floor"), mapView.getCurrentFloor().getFloorNumber()));
    }

}
