package com.brtbeacon.map3d.demo.map.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ZoomControls;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map.map3d.entity.BRTPoi;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;

import java.util.HashMap;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class IconScaleMapActivity extends BaseMapActivity {

    private SymbolLayer labelLayer = null;
    private SymbolLayer facilityLayer = null;

    @Override
    public void onPoiSelected(BRTMapView mapView, List<BRTPoi> points) {
        super.onPoiSelected(mapView, points);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_icon_scale_map;
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {

        HashMap<String, Bitmap> iconMap = new HashMap<>();
        iconMap.put("010000", BitmapFactory.decodeResource(getResources(), R.drawable.category_food));
        iconMap.put("020000", BitmapFactory.decodeResource(getResources(), R.drawable.category_life));
        iconMap.put("021101", BitmapFactory.decodeResource(getResources(), R.drawable.category_life));
        iconMap.put("020300", BitmapFactory.decodeResource(getResources(), R.drawable.category_digital));
        iconMap.put("021109", BitmapFactory.decodeResource(getResources(), R.drawable.category_leather));
        iconMap.put("021202", BitmapFactory.decodeResource(getResources(), R.drawable.category_ornament));
        iconMap.put("040200", BitmapFactory.decodeResource(getResources(), R.drawable.category_parenting));
        iconMap.put("00000", BitmapFactory.decodeResource(getResources(), R.drawable.category_others));
        mapView.setCatergoryIcons(iconMap);
        mapView.setFloor(mapView.getFloorList().get(0));

        labelLayer = mapView.getLabelLayer();
        facilityLayer = mapView.getFacilityLayer();

        ZoomControls labelIconZoomControls = findViewById(R.id.zoom_label_icon);
        labelIconZoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                labelLayer.setProperties(iconSize(labelLayer.getIconSize().value - 0.05f));
            }
        });
        labelIconZoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                labelLayer.setProperties(iconSize(labelLayer.getIconSize().value + 0.05f));
            }
        });

        ZoomControls facilityIconZoomControls = findViewById(R.id.zoom_facility_icon);
        facilityIconZoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facilityLayer.setProperties(iconSize(facilityLayer.getIconSize().value - 0.05f));
            }
        });
        facilityIconZoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facilityLayer.setProperties(iconSize(facilityLayer.getIconSize().value + 0.05f));
            }
        });

    }

    @Override
    public void onFinishLoadingFloor(BRTMapView mapView, BRTFloorInfo floorInfo) {
        super.onFinishLoadingFloor(mapView, floorInfo);
        setFloorControlVisible(true);
    }
}
