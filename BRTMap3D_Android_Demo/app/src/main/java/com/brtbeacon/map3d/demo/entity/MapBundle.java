package com.brtbeacon.map3d.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class MapBundle implements Parcelable {
    public String buildingId;
    public String appkey;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.buildingId);
        dest.writeString(this.appkey);
    }

    public MapBundle() {
    }

    protected MapBundle(Parcel in) {
        this.buildingId = in.readString();
        this.appkey = in.readString();
    }

    public static final Parcelable.Creator<MapBundle> CREATOR = new Parcelable.Creator<MapBundle>() {
        @Override
        public MapBundle createFromParcel(Parcel source) {
            return new MapBundle(source);
        }

        @Override
        public MapBundle[] newArray(int size) {
            return new MapBundle[size];
        }
    };
}
