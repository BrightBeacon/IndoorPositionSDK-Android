package com.brtbeacon.map3d.demo.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseActivity;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.brtbeacon.map3d.demo.adapter.GuideListAdapter;
import com.brtbeacon.map3d.demo.map.event.FollowMapActivity;
import com.brtbeacon.map3d.demo.map.annotations.ImageMapActivity;
import com.brtbeacon.map3d.demo.map.annotations.LabelMapActivity;
import com.brtbeacon.map3d.demo.map.annotations.PolyLineMapActivity;
import com.brtbeacon.map3d.demo.map.annotations.MarkerMapActivity;
import com.brtbeacon.map3d.demo.map.annotations.PolygonMapActivity;
import com.brtbeacon.map3d.demo.map.annotations.InfoWindowMapActivity;
import com.brtbeacon.map3d.demo.map.basic.CoordMapActivity;
import com.brtbeacon.map3d.demo.map.basic.FloorMapActivity;
import com.brtbeacon.map3d.demo.map.basic.SimpleMapActivity;
import com.brtbeacon.map3d.demo.entity.GuideItem;
import com.brtbeacon.map3d.demo.entity.MapBundle;
import com.brtbeacon.map3d.demo.map.control.IconScaleMapActivity;
import com.brtbeacon.map3d.demo.map.control.ZoomMapActivity;
import com.brtbeacon.map3d.demo.map.event.EventClickMapActivity;
import com.brtbeacon.map3d.demo.map.event.EventLoadMapActivity;
import com.brtbeacon.map3d.demo.map.event.EventMapActivity;
import com.brtbeacon.map3d.demo.map.location.LocationActivity;
import com.brtbeacon.map3d.demo.map.oper.PoiMapActivity;
import com.brtbeacon.map3d.demo.map.oper.PoiMapDiffColorActivity;
import com.brtbeacon.map3d.demo.map.others.HeatMapActivity;
import com.brtbeacon.map3d.demo.map.route.MultiRouteActivity;
import com.brtbeacon.map3d.demo.map.route.MultiRouteNavActivity;
import com.brtbeacon.map3d.demo.map.route.NearestRoutePointOfflineActivity;
import com.brtbeacon.map3d.demo.map.route.RouteOfflineActivity;
import com.brtbeacon.map3d.demo.map.route.RouteOnlineActivity;
import com.brtbeacon.map3d.demo.map.route.RouteSimLocationNavActivity;
import com.brtbeacon.map3d.demo.map.route.RouteSimNavActivity;
import com.brtbeacon.map3d.demo.map.search.NoneMapSearchActivity;
import com.brtbeacon.map3d.demo.map.search.SimpleSearchMapActivity;

import java.util.LinkedList;
import java.util.List;

public class MapGuideActivity extends BaseActivity {

    public static final String ARG_BUILDING_ID = "arg_building_id";
    public static final String ARG_APPKEY = "arg_appkey";
    private ExpandableListView listView;
    private GuideListAdapter guideListAdapter = null;
    private List<GuideItem> guideList = new LinkedList<>();

    private String buildingId = "00230100";
    private String appkey = "db99a4a91aa149019ac53abc8d84fdb5";

    public static void startActivity(Context context, String buildingId, String appkey) {
        Intent intent = new Intent(context, MapGuideActivity.class);
        intent.putExtra(ARG_BUILDING_ID, buildingId);
        intent.putExtra(ARG_APPKEY, appkey);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_guide);
        listView = findViewById(R.id.listView);
        initGuideList();
        guideListAdapter = new GuideListAdapter(this, guideList);
        listView.setAdapter(guideListAdapter);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                GuideItem item = (GuideItem) guideListAdapter.getChild(i, i1);
                Intent intent = new Intent(MapGuideActivity.this, item.cls);
                MapBundle mapBundle = new MapBundle();
                mapBundle.buildingId = buildingId;
                mapBundle.appkey = appkey;
                intent.putExtra(BaseMapActivity.ARG_MAP_BUNDLE, mapBundle);
                startActivity(intent);
                return true;
            }
        });
    }

    private void initGuideList() {
        guideList.clear();
        {
            GuideItem groupItem = new GuideItem(getString(R.string.guide_group_map_display));
            groupItem.add(new GuideItem(getString(R.string.guide_basic_map_rendering), SimpleMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_basic_map_floor), FloorMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_basic_map_coord), CoordMapActivity.class));
            guideList.add(groupItem);
        }

        {
            GuideItem groupItem = new GuideItem(getString(R.string.guide_group_map_widget));
            groupItem.add(new GuideItem(getString(R.string.guide_widget_zoom), ZoomMapActivity.class));
            groupItem.add(new GuideItem("* 图标缩放", IconScaleMapActivity.class));
            guideList.add(groupItem);
        }

        {
            GuideItem groupItem = new GuideItem(getString(R.string.guide_group_map_annotations));
            groupItem.add(new GuideItem(getString(R.string.guide_annotation_mark), MarkerMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_annotation_line), PolyLineMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_annotation_polygon), PolygonMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_annotation_label), LabelMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_annotation_image), ImageMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_annotation_info_window), InfoWindowMapActivity.class));
            guideList.add(groupItem);
        }

        {
            GuideItem groupItem = new GuideItem(getString(R.string.guide_group_map_event));
            groupItem.add(new GuideItem(getString(R.string.guide_event_zoom_scale), EventMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_event_click), EventClickMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_event_poi), PoiMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_event_poi_color), PoiMapDiffColorActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_event_map_load), EventLoadMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_event_map_follow), FollowMapActivity.class));
            guideList.add(groupItem);
        }

        {
            GuideItem groupItem = new GuideItem(getString(R.string.guide_group_map_query));
            groupItem.add(new GuideItem(getString(R.string.guide_query_simple), SimpleSearchMapActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_query_simple_none_map), NoneMapSearchActivity.class));
            guideList.add(groupItem);
        }

        {
            GuideItem groupItem = new GuideItem(getString(R.string.guide_group_map_route));
            groupItem.add(new GuideItem(getString(R.string.guide_route_offline), RouteOfflineActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_route_online), RouteOnlineActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_route_sim_nav), RouteSimNavActivity.class));
            groupItem.add(new GuideItem(getString(R.string.guide_route_sim_nav_location), RouteSimLocationNavActivity.class));
            groupItem.add(new GuideItem("* 多路径规划", MultiRouteActivity.class));
            groupItem.add(new GuideItem("* 多路径规划模拟导航", MultiRouteNavActivity.class));
            groupItem.add(new GuideItem("* 最近路网点【离线模式】", NearestRoutePointOfflineActivity.class));
            guideList.add(groupItem);
        }

        {
            GuideItem groupItem = new GuideItem(getString(R.string.guide_group_map_location));
            groupItem.add(new GuideItem(getString(R.string.guide_location_simple), LocationActivity.class));
            guideList.add(groupItem);
        }

        {
            GuideItem groupItem = new GuideItem("其它功能");
            groupItem.add(new GuideItem("* HeatMap 热力图", HeatMapActivity.class));
            guideList.add(groupItem);
        }

        {
            GuideItem groupItem = new GuideItem("ISSUES");
            groupItem.add(new GuideItem("* Add Marker", com.brtbeacon.map3d.demo.map.test.MarkerMapActivity.class));
            guideList.add(groupItem);
        }
    }



}
