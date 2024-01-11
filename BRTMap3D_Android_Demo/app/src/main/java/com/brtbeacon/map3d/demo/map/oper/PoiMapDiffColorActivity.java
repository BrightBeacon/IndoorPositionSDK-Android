package com.brtbeacon.map3d.demo.map.oper;

import android.os.Bundle;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoi;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PoiMapDiffColorActivity extends BaseMapActivity {

    private Map<BRTPoi, Integer> poiColorMap = new HashMap<>();
    private Set<BRTPoi> poiSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        setFloorControlVisible(true);
    }

    @Override
    public void onPoiSelected(BRTMapView mapView, List<BRTPoi> points) {
        super.onPoiSelected(mapView, points);
        if (points.isEmpty())
            return;

        int color = ((int) (Math.random() * 255.0f + 0.5f) << 24) |
                ((int) (Math.random() * 255.0f + 0.5f) << 16) |
                ((int) (Math.random() * 255.0f + 0.5f) <<  8) |
                (int) (Math.random() * 255.0f + 0.5f);



        BRTPoi poi = points.get(0);
        poiSet.add(poi);
        poiColorMap.put(poi, color);

        List<BRTPoi> poiList = new ArrayList<>(poiSet);
        mapView.highlightPois(poiList, poiColorMap);
    }
}
