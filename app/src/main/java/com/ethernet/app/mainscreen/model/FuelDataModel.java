package com.ethernet.app.mainscreen.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FuelDataModel implements Parcelable {

    public String currentDispSale;
    public String currentDispVol;
    public String operation;
    public String currentDispense;
    public String fp;
    public String grad;
    public String level;
    public String ppu;

    public FuelDataModel(){

    }


    protected FuelDataModel(Parcel in) {
        currentDispSale = in.readString();
        currentDispVol = in.readString();
        operation = in.readString();
        currentDispense = in.readString();
        fp = in.readString();
        grad = in.readString();
        level = in.readString();
        ppu = in.readString();
    }

    public static final Creator<FuelDataModel> CREATOR = new Creator<FuelDataModel>() {
        @Override
        public FuelDataModel createFromParcel(Parcel in) {
            return new FuelDataModel(in);
        }

        @Override
        public FuelDataModel[] newArray(int size) {
            return new FuelDataModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currentDispSale);
        dest.writeString(currentDispVol);
        dest.writeString(operation);
        dest.writeString(currentDispense);
        dest.writeString(fp);
        dest.writeString(grad);
        dest.writeString(level);
        dest.writeString(ppu);
    }
}
