package com.ethernet.app.settingscreen.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NozzleModel implements Parcelable {

    public String name;
    public boolean isSelected;

    public NozzleModel(){

    }

    protected NozzleModel(Parcel in) {
        name = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<NozzleModel> CREATOR = new Creator<NozzleModel>() {
        @Override
        public NozzleModel createFromParcel(Parcel in) {
            return new NozzleModel(in);
        }

        @Override
        public NozzleModel[] newArray(int size) {
            return new NozzleModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
