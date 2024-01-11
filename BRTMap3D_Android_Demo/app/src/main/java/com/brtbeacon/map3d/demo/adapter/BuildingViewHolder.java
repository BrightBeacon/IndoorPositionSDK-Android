package com.brtbeacon.map3d.demo.adapter;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.brtbeacon.map.network.entity.BuildingsResult;
import com.brtbeacon.map3d.demo.R;

public class BuildingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    BuildingsResult.ObjBean building;
    BuildingAdapter.OnItemClickListener onItemClickListener;

    TextView tvName;
    TextView tvAddress;
    TextView tvAppkey;
    TextView tvType;
    TextView tvPType;
    TextView tvBuildingId;
    TextView tvPattern;

    public BuildingViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tv_name);
        tvAddress = itemView.findViewById(R.id.tv_address);
        tvAppkey = itemView.findViewById(R.id.tv_appkey);
        tvType = itemView.findViewById(R.id.tv_type);
        tvPType = itemView.findViewById(R.id.tv_ptype);
        tvBuildingId = itemView.findViewById(R.id.tv_building_id);
        tvPattern = itemView.findViewById(R.id.tv_pattern);
        itemView.setOnClickListener(this);
    }

    public BuildingsResult.ObjBean getBuilding() {
        return building;
    }

    public void setBuilding(BuildingsResult.ObjBean building) {
        this.building = building;
    }

    public BuildingAdapter.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(BuildingAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.OnItemClick(this, view);
        }
    }
}
