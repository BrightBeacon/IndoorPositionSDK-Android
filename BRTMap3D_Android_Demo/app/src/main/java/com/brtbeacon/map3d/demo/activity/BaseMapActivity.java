package com.brtbeacon.map3d.demo.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.brtbeacon.map.map3d.BRTMapEnvironment;
import com.brtbeacon.map.map3d.BRTMapView;
import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map.map3d.entity.BRTPoi;
import com.brtbeacon.map.map3d.entity.BRTPoint;
import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.entity.MapBundle;
import com.brtbeacon.map3d.demo.menu.FloorListPopupMenu;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class BaseMapActivity extends BaseActivity implements View.OnClickListener, BRTMapView.BRTMapViewListener {
    public static final String TAG = BaseMapActivity.class.getSimpleName();
    private static final int BRTMAP_PERMISSION_CODE = 9999;
    public static final String ARG_MAP_BUNDLE = "arg_map_bundle";
    protected MapBundle mapBundle;
    protected BRTMapView mapView;
    protected BRTFloorInfo currentFloor;

    private View layoutFloorControl = null;
    private TextView tvFloorName = null;

    protected View layoutSearchControl = null;
    protected EditText editSearch = null;
    protected ImageView ivSearchCtrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapBundle = getIntent().getParcelableExtra(ARG_MAP_BUNDLE);
        BRTMapEnvironment.initMapEnvironment(this);

        setContentView(getContentViewId());
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.addMapListener(this);

        layoutFloorControl = findViewById(R.id.layout_floor);
        tvFloorName = findViewById(R.id.tv_floor_name);
        if (layoutFloorControl != null) {
            layoutFloorControl.setOnClickListener(this);
        }

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

        if (!checkNeedPermission()) {
            initMapView();
        }
    }

    private void initMapView() {
        //mapView.init(mapBundle.buildingId, mapBundle.appkey, BRTMapView.MAP_LOAD_MODE_ONLINE);
        mapView.init(mapBundle.buildingId, mapBundle.appkey, BRTMapView.MAP_LOAD_MODE_OFFLINE);
    }

    protected int getContentViewId() {
        return R.layout.activity_simple_map;
    }

    public void setFloorControlVisible(boolean visible) {
        if (layoutFloorControl != null) {
            layoutFloorControl.setVisibility(visible?View.VISIBLE:View.GONE);
        }
    }

    public boolean getFloorControlVisible() {
        if (layoutFloorControl != null) {
            return layoutFloorControl.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    public void updateFloorContrl() {
        if (tvFloorName != null && currentFloor != null) {
            tvFloorName.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_floor: {
                FloorListPopupMenu.show(this, view, null, mapView.getFloorList(), new FloorListPopupMenu.OnFloorItemClickListener() {
                    @Override
                    public void onItemClick(BRTFloorInfo floorInfo) {
                        mapView.setFloor(floorInfo);
                    }
                });
                break;
            }
        }
    }

    @Override
    public void mapViewDidLoad(BRTMapView mapView, Error error) {
        if (error != null) {
            showToast(error.getMessage());
            return;
        }

        mapView.setFloor(mapView.getFloorList().get(0));
    }

    @Override
    public void onFinishLoadingFloor(BRTMapView mapView, BRTFloorInfo floorInfo) {
        if (tvFloorName != null) {
            tvFloorName.setText(floorInfo.getFloorName());
        }
    }

    @Override
    public void onClickAtPoint(BRTMapView mapView, BRTPoint point) {

    }

    @Override
    public void onPoiSelected(BRTMapView mapView, List<BRTPoi> points) {

    }

    protected void onSearchTextChanged(String content) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private boolean checkNeedPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断当前系统的SDK版本是否大于23

            List<String> permissions = new ArrayList<>();

            // 室内地图数据的保存和读取相关权限
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

            /** 室内定位权限请保留以下权限申请代码 **/

            // 安卓6.0及以上系统，需要申请定位权限才能正常扫描蓝牙Beacon定位信标
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            // 如果APP编译targetSdk>=31(即安卓12及以上版本)需要运行时申请以下权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // 蓝牙扫描权限
                permissions.add(Manifest.permission.BLUETOOTH_SCAN);
                // 蓝牙连接权限控制蓝牙开关
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

            List<String> permissionNeedRequest = new LinkedList<>();
            for (String permssion: permissions) {
                if(ActivityCompat.checkSelfPermission(this, permssion) != PackageManager.PERMISSION_GRANTED) {
                    permissionNeedRequest.add(permssion);
                }
            }
            if (!permissionNeedRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionNeedRequest.toArray(new String[0]), BRTMAP_PERMISSION_CODE);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在requestPermissions时传入
            case BRTMAP_PERMISSION_CODE:
                boolean isAllGrant = true;
                for (int grantResult: grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isAllGrant = false;
                        break;
                    }
                }
                if (!isAllGrant) {
                    Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动前往设置开启", Toast.LENGTH_SHORT).show();
                    return;
                }

                initMapView();

                break;
            default:
                break;
        }
    }

    static {
        System.loadLibrary("BRTMapSDK");
    }

}
