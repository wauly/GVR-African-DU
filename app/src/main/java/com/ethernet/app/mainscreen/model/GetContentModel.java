package com.ethernet.app.mainscreen.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GetContentModel implements Parcelable {

    public boolean active;
    public String appPinkCallInterval;
    public ArrayList<ContentDataModel> listOfContent;
    public String deviceId;
    public String orientation;
    public String uuid;

    public GetContentModel(){

    }

    protected GetContentModel(Parcel in) {
        active = in.readByte() != 0;
        appPinkCallInterval = in.readString();
        listOfContent = in.createTypedArrayList(ContentDataModel.CREATOR);
        deviceId = in.readString();
        orientation = in.readString();
        uuid = in.readString();
    }

    public static final Creator<GetContentModel> CREATOR = new Creator<GetContentModel>() {
        @Override
        public GetContentModel createFromParcel(Parcel in) {
            return new GetContentModel(in);
        }

        @Override
        public GetContentModel[] newArray(int size) {
            return new GetContentModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(appPinkCallInterval);
        dest.writeTypedList(listOfContent);
        dest.writeString(deviceId);
        dest.writeString(orientation);
        dest.writeString(uuid);
    }
}
