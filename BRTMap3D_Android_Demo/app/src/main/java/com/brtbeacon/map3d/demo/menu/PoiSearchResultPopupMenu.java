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

import com.brtbeacon.map.map3d.entity.BRTPoiEntity;
import com.brtbeacon.map3d.demo.R;

import java.util.List;

public class PoiSearchResultPopupMenu {

    private static PopupWindow popupWindow = null;

    public interface OnEntityItemClickListener {
        void onItemClick(BRTPoiEntity entityInfo);
    }

    public static void show(Context context, View view, List<BRTPoiEntity> entityList, final OnEntityItemClickListener listener) {
        if (entityList.isEmpty())
            return;
        View contentView = null;
        ListView listView = null;
        if (popupWindow == null) {
            popupWindow = new PopupWindow(context);
            contentView = LayoutInflater.from(context).inflate(R.layout.layout_poi_entity_list, null, false);
            popupWindow.setContentView(contentView);
            listView = contentView.findViewById(R.id.listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    BRTPoiEntity entity = (BRTPoiEntity) adapterView.getItemAtPosition(i);
                    popupWindow.dismiss();
                    if (listener != null) {
                        listener.onItemClick(entity);
                    }
                }
            });
        } else {
            contentView = popupWindow.getContentView();
            listView = contentView.findViewById(R.id.listView);
        }
        ArrayAdapter<BRTPoiEntity> adapter = new ArrayAdapter<BRTPoiEntity>(context, R.layout.item_menu_floor, R.id.tv_name, entityList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View rootView = super.getView(position, convertView, parent);
                TextView tvName = rootView.findViewById(R.id.tv_name);
                BRTPoiEntity item = getItem(position);
                tvName.setText(item.getName() + " [" + item.getFloorName() + "]");
                return rootView;
            }
        };
        listView.setAdapter(adapter);


        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(view);
    }

}
