package com.ethernet.app.mainscreen.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DevicePingModel implements Parcelable {

    public String id;
    public String deviceId;
    public String deviceIp;
    public String uuId;
    public String date;
    public double latitude;
    public double longitude;
    public String nowPlaying;
    public int viewContentCount;
    public long isOffline;
    public int viewLoopCount;
    public long offlineTime;


    public DevicePingModel(){

    }

    protected DevicePingModel(Parcel in) {
        id = in.readString();
        deviceId = in.readString();
        deviceIp = in.readString();
        uuId = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        date = in.readString();
        nowPlaying = in.readString();
        viewContentCount = in.readInt();
        isOffline = in.readLong();
        viewLoopCount = in.readInt();
        offlineTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(deviceId);
        dest.writeString(deviceIp);
        dest.writeString(uuId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(date);
        dest.writeString(nowPlaying);
        dest.writeInt(viewContentCount);
        dest.writeLong(isOffline);
        dest.writeInt(viewLoopCount);
        dest.writeLong(offlineTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DevicePingModel> CREATOR = new Creator<DevicePingModel>() {
        @Override
        public DevicePingModel createFromParcel(Parcel in) {
            return new DevicePingModel(in);
        }

        @Override
        public DevicePingModel[] newArray(int size) {
            return new DevicePingModel[size];
        }
    };
}
