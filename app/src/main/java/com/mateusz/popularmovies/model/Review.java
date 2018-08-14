package com.mateusz.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    private final String mName;
    private final String mText;

    public Review(String mName, String mText) {
        this.mName = mName;
        this.mText = mText;
    }

    public String getmName() {
        return mName;
    }
    public String getmText() {
        return mText;
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(mName);
        dest.writeString(mText);
    }

    public Review(Parcel parcel){
        mName = parcel.readString();
        mText = parcel.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>(){

        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[0];
        }
    };

    public int describeContents() {
        return hashCode();
    }
}
