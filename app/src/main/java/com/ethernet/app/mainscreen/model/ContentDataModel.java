package com.ethernet.app.mainscreen.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ContentDataModel implements Parcelable {

    public String clickUrl;
    public String duration;
    public String isDaily;
    public String isSchedule;
    public String loopContent;
    public String loopEndDate;
    public String loopEndTime;
    public String loopStartDate;
    public String loopStartTime;
    public String txtLoopName;
    public String type;
    public String url;
    public String deleteFlag;
    public String screenType;

    public ContentDataModel(){
    }

    protected ContentDataModel(Parcel in) {
        clickUrl = in.readString();
        duration = in.readString();
        isDaily = in.readString();
        isSchedule = in.readString();
        loopContent = in.readString();
        loopEndDate = in.readString();
        loopEndTime = in.readString();
        loopStartDate = in.readString();
        loopStartTime = in.readString();
        txtLoopName = in.readString();
        type = in.readString();
        url = in.readString();
        deleteFlag = in.readString();
        screenType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(clickUrl);
        dest.writeString(duration);
        dest.writeString(isDaily);
        dest.writeString(isSchedule);
        dest.writeString(loopContent);
        dest.writeString(loopEndDate);
        dest.writeString(loopEndTime);
        dest.writeString(loopStartDate);
        dest.writeString(loopStartTime);
        dest.writeString(txtLoopName);
        dest.writeString(type);
        dest.writeString(url);
        dest.writeString(deleteFlag);
        dest.writeString(screenType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContentDataModel> CREATOR = new Creator<ContentDataModel>() {
        @Override
        public ContentDataModel createFromParcel(Parcel in) {
            return new ContentDataModel(in);
        }

        @Override
        public ContentDataModel[] newArray(int size) {
            return new ContentDataModel[size];
        }
    };
}
