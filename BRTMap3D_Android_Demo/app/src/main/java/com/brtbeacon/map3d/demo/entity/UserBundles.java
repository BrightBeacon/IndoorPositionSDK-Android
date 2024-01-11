package com.brtbeacon.map3d.demo.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.brtbeacon.map.network.entity.BuildingLicenseResult;
import com.brtbeacon.map.network.entity.BuildingsResult;
import com.brtbeacon.map.network.entity.LoginResult;

public class UserBundles implements Parcelable {
    private LoginResult loginResult;
    private BuildingsResult.ObjBean building;
    private BuildingLicenseResult buildingLicenseResult;

    public BuildingsResult.ObjBean getBuilding() {
        return building;
    }

    public void setBuilding(BuildingsResult.ObjBean building) {
        this.building = building;
    }

    public LoginResult getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(LoginResult loginResult) {
        this.loginResult = loginResult;
    }

    public BuildingLicenseResult getBuildingLicenseResult() {
        return buildingLicenseResult;
    }

    public void setBuildingLicenseResult(BuildingLicenseResult buildingLicenseResult) {
        this.buildingLicenseResult = buildingLicenseResult;
    }

    public UserBundles() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.loginResult, flags);
        dest.writeParcelable(this.building, flags);
        dest.writeParcelable(this.buildingLicenseResult, flags);
    }

    protected UserBundles(Parcel in) {
        this.loginResult = in.readParcelable(LoginResult.class.getClassLoader());
        this.building = in.readParcelable(BuildingsResult.ObjBean.class.getClassLoader());
        this.buildingLicenseResult = in.readParcelable(BuildingLicenseResult.class.getClassLoader());
    }

    public static final Creator<UserBundles> CREATOR = new Creator<UserBundles>() {
        @Override
        public UserBundles createFromParcel(Parcel source) {
            return new UserBundles(source);
        }

        @Override
        public UserBundles[] newArray(int size) {
            return new UserBundles[size];
        }
    };
}
