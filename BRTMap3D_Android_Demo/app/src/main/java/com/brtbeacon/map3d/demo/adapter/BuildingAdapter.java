package com.brtbeacon.map3d.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brtbeacon.map.network.entity.BuildingsResult;
import com.brtbeacon.map3d.demo.R;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingViewHolder> {

    public interface OnItemClickListener {
        void OnItemClick(BuildingViewHolder holder, View view);
    }

    private Context context;
    private List<BuildingsResult.ObjBean> mList;
    private OnItemClickListener onItemClickListener;

    public BuildingAdapter(Context context, List<BuildingsResult.ObjBean> list) {
        this.context = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BuildingViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user_building, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder holder, int position) {
        BuildingsResult.ObjBean item = mList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvAddress.setText(item.getAddress());
        holder.tvBuildingId.setText(item.getBuildingid());
        holder.tvAppkey.setText(item.getAppkey());
        holder.tvType.setText(item.getType());
        holder.tvPType.setText(item.getPtype());
        holder.tvPattern.setText(item.getPattern());
        holder.setBuilding(item);
        holder.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.notifyDataSetChanged();
    }
}
