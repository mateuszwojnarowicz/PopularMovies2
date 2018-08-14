package com.mateusz.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.mateusz.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_TITLE = "movieTitle";
        public static final String COLUMN_POSTER = "moviePoster";
        public static final String COLUMN_RELEASE_DATE = "movieDate";
        public static final String COLUMN_RATING = "movieRating";
        public static final String COLUMN_PLOT = "moviePlot";

    }

}
