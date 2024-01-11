package com.brtbeacon.map3d.demo.map.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTPoiEntity;
import com.brtbeacon.map.map3d.utils.BRTSearchAdapter;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseMapActivity;
import com.brtbeacon.map3d.demo.menu.PoiSearchResultPopupMenu;

import java.util.List;

public class SimpleSearchMapActivity extends BaseMapActivity {

    private BRTSearchAdapter searchAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        super.mapViewDidLoad(mapView, error);
        layoutSearchControl.setVisibility(View.VISIBLE);

        /**
         * 地图加载成功后，初始化搜索引擎；
         * init Search Engine
         */
        searchAdapter = new BRTSearchAdapter(this, mapView.getBuilding().getBuildingID());
    }

    @Override
    protected void onSearchTextChanged(String content) {
        super.onSearchTextChanged(content);
        if (!TextUtils.isEmpty(content)) {
            List<BRTPoiEntity> entityList = searchAdapter.queryPoi(content);
            System.out.println(entityList);
            PoiSearchResultPopupMenu.show(this, layoutSearchControl, entityList, onEntityItemClickListener);
        }
    }

    private PoiSearchResultPopupMenu.OnEntityItemClickListener onEntityItemClickListener = new PoiSearchResultPopupMenu.OnEntityItemClickListener() {
        @Override
        public void onItemClick(BRTPoiEntity entityInfo) {
            showToast(getString(R.string.toast_user_choosed) + entityInfo.getName());
        }
    };
}
