package com.mateusz.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {

    private final String mName;
    private final String mUrlString;

    public Video(String mName, String mUrlString) {
        this.mName = mName;
        this.mUrlString = mUrlString;
    }

    public String getmName() {
        return mName;
    }
    public String getmUrlString() {
        return mUrlString;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(mName);
        dest.writeString(mUrlString);
    }

    //constructor used for parcel
    public Video(Parcel parcel){
        mName = parcel.readString();
        mUrlString = parcel.readString();
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>(){

        @Override
        public Video createFromParcel(Parcel parcel) {
            return new Video(parcel);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[0];
        }


    };

    public int describeContents() {
        return hashCode();
    }
}