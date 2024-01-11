package com.brtbeacon.map3d.demo.menu;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brtbeacon.map.map3d.entity.BRTFloorInfo;
import com.brtbeacon.map3d.demo.R;

import java.util.List;

public class FloorListPopupMenu {

    public interface OnFloorItemClickListener {
        void onItemClick(BRTFloorInfo floorInfo);
    }

    public static void show(Context context, View view, BRTFloorInfo currentFloor, List<BRTFloorInfo> floorList, final OnFloorItemClickListener listener) {
        final PopupWindow popupWindow = new PopupWindow(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_floor_list, null, false);
        ListView listView = contentView.findViewById(R.id.listView);
        ArrayAdapter<BRTFloorInfo> adapter = new ArrayAdapter<BRTFloorInfo>(context, R.layout.item_menu_floor, R.id.tv_name, floorList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View rootView = super.getView(position, convertView, parent);
                TextView tvName = rootView.findViewById(R.id.tv_name);
                BRTFloorInfo item = getItem(position);
                tvName.setText(item.getFloorName());
                return rootView;
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BRTFloorInfo floorInfo = (BRTFloorInfo) adapterView.getItemAtPosition(i);
                popupWindow.dismiss();
                listener.onItemClick(floorInfo);
            }
        });
        popupWindow.setContentView(contentView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(view);
    }

}
