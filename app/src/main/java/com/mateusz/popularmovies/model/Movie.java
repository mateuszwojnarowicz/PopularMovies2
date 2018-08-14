package com.mateusz.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by mati on 6/28/2018.
 */

@SuppressWarnings("ALL")
public class Movie implements Parcelable {

    private final String mTitle;
    private final int mId;
    private final String mPosterUrl;
    private final String mPlot;
    private final double mUserRating;
    private final String mReleaseDate;
    private final String mVideosUrl;
    private final String mReviewsUrl;
    private ArrayList<Video> mVideos;
    private ArrayList<Review> mReviews;


    public Movie(String mTitle, int mId, String mPosterUrl, String mPlot, double mUserRating, String mReleaseDate, String videosUrl, String reviewsURL) {
        this.mTitle = mTitle;
        this.mId = mId;
        this.mPosterUrl = mPosterUrl;
        this.mPlot = mPlot;
        this.mUserRating = mUserRating;
        this.mReleaseDate = mReleaseDate;
        this.mVideosUrl = videosUrl;
        this.mReviewsUrl = reviewsURL;
    }

    public String getmTitle() {
            return mTitle;
    }
    public int getmId(){
        return mId;
    }
    public String getmPosterUrl() {
        return mPosterUrl;
    }
    public String getmPlot() {
        return mPlot;
    }
    public double getmUserRating() {
        return mUserRating;
    }
    public String getmReleaseDate() {
        return mReleaseDate;
    }
    public String getmVideosUrl() {
        return mVideosUrl;
    }
    public String getmReviewsUrl() {
        return mReviewsUrl;
    }
    public ArrayList<Video> getmVideos() {
        return mVideos;
    }
    public ArrayList<Review> getmReviews() {
        return mReviews;
    }
    public void setmVideos(ArrayList<Video> videos) {
        mVideos = videos;
    }
    public void setmReviews(ArrayList<Review> reviews) {
        mReviews = reviews;
    }
    public ArrayList<Review> setmReviews() {
        return mReviews;
    }

    //Parcelable methods
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(mTitle);
        dest.writeInt(mId);
        dest.writeString(mPosterUrl);
        dest.writeString(mPlot);
        dest.writeDouble(mUserRating);
        dest.writeString(mReleaseDate);
        dest.writeString(mVideosUrl);
        dest.writeString(mReviewsUrl);
    }

    //constructor used for parcel
    public Movie(Parcel parcel){
        mTitle = parcel.readString();
        mId = parcel.readInt();
        mPosterUrl = parcel.readString();
        mPlot = parcel.readString();
        mUserRating = parcel.readDouble();
        mReleaseDate = parcel.readString();
        mVideosUrl = parcel.readString();
        mReviewsUrl = parcel.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };

    public int describeContents() {
        return hashCode();
    }

}
