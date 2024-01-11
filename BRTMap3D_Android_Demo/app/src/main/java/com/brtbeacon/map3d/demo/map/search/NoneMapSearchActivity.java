package com.brtbeacon.map3d.demo.map.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.brtbeacon.map.map3d.entity.BRTPoiEntity;
import com.brtbeacon.map.map3d.utils.BRTSearchAdapter;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.activity.BaseActivity;
import com.brtbeacon.map3d.demo.entity.MapBundle;
import com.brtbeacon.map3d.demo.menu.PoiSearchResultPopupMenu;
import com.brtbeacon.mapdata.BRTBuilding;
import com.brtbeacon.mapsdk.BRTDownloader;

import java.util.List;

public class NoneMapSearchActivity extends BaseActivity {

    public static final String ARG_MAP_BUNDLE = "arg_map_bundle";
    private MapBundle mapBundle;

    protected View layoutSearchControl = null;
    protected EditText editSearch = null;
    protected ImageView ivSearchCtrl = null;

    private BRTSearchAdapter searchAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_none_map);

        layoutSearchControl = findViewById(R.id.layout_search);
        editSearch = findViewById(R.id.edit_search);
        ivSearchCtrl = findViewById(R.id.iv_search_ctrl);
        if (editSearch != null) {
            editSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    onSearchTextChanged(s.toString());
                }
            });
        }

        mapBundle = getIntent().getParcelableExtra(ARG_MAP_BUNDLE);
        /**
         * 安卓6.0以上系统，请添加 android.Manifest.permission.WRITE_EXTERNAL_STORAGE 权限申请
         * 成功后，再执行下面代码；此处为简化了这部分代码；
         *
         * Android 6.0 and Later, Must Request "android.Manifest.permission.WRITE_EXTERNAL_STORAGE"
         * at runtime; Here omit codes;
         */

        /**
         * 下载或者更新地图数据
         * Download or update map data
         */
        BRTDownloader.loadMap(this, mapBundle.buildingId, mapBundle.appkey, onMapDataLoad);
    }

    protected void onSearchTextChanged(String content) {
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

    private BRTDownloader.OnMapDataLoad onMapDataLoad = new BRTDownloader.OnMapDataLoad() {
        @Override
        public void onCompetion(String version, String newVersion, final BRTBuilding building) {
            /**
             * 地图数据加载成功
             * Map data loading success
             */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(getString(R.string.toast_map_load_success));
                    searchAdapter = new BRTSearchAdapter(NoneMapSearchActivity.this, building.getBuildingID());
                    layoutSearchControl.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onError(Error error) {

            /**
             * 地图数据加载失败
             * Failure of map data loading
             */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(error.getMessage());
                }
            });
        }
    };

    static {
        System.loadLibrary("BRTMapSDK");
    }
}
