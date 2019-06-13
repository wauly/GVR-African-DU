package com.ethernet.app.mainscreen.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DateTimeModel implements Parcelable {

    public String id;
    public String dateValue;
    public String timeValue;

    public DateTimeModel(){

    }

    protected DateTimeModel(Parcel in) {
        dateValue = in.readString();
        dateValue = in.readString();
        timeValue = in.readString();
    }

    public static final Creator<DateTimeModel> CREATOR = new Creator<DateTimeModel>() {
        @Override
        public DateTimeModel createFromParcel(Parcel in) {
            return new DateTimeModel(in);
        }

        @Override
        public DateTimeModel[] newArray(int size) {
            return new DateTimeModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateValue);
        dest.writeString(timeValue);
    }
}
