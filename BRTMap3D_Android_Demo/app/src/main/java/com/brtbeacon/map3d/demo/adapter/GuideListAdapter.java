package com.brtbeacon.map3d.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.brtbeacon.map3d.demo.R;
import com.brtbeacon.map3d.demo.entity.GuideItem;

import java.util.List;

public class GuideListAdapter extends BaseExpandableListAdapter {

    private Context context = null;
    private List<GuideItem> guideItemList = null;

    public GuideListAdapter(Context context, List<GuideItem> guideItems) {
        this.context = context;
        this.guideItemList = guideItems;
    }

    @Override
    public int getGroupCount() {
        return guideItemList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return guideItemList.get(i).childs.size();
    }

    @Override
    public Object getGroup(int i) {
        return guideItemList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return guideItemList.get(i).childs.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GuideItem item = (GuideItem) getGroup(i);
        TextView tvName = null;
        if (view != null) {
            tvName = (TextView)view;
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            tvName = (TextView) layoutInflater.inflate(R.layout.item_guide_group, viewGroup, false);
        }
        tvName.setText(item.name);
        return tvName;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        GuideItem item = (GuideItem) getChild(i, i1);
        TextView tvName = null;
        if (view != null) {
            tvName = (TextView)view;
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            tvName = (TextView) layoutInflater.inflate(R.layout.item_guide_child, viewGroup, false);
        }
        tvName.setText(item.name);
        return tvName;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
