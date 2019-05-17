package com.ethernet.app.settingscreen.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BaudRateModel implements Parcelable {

    public String value;
    public boolean isSelected;

    public BaudRateModel() {

    }

    protected BaudRateModel(Parcel in) {
        value = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<BaudRateModel> CREATOR = new Creator<BaudRateModel>() {
        @Override
        public BaudRateModel createFromParcel(Parcel in) {
            return new BaudRateModel(in);
        }

        @Override
        public BaudRateModel[] newArray(int size) {
            return new BaudRateModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}

