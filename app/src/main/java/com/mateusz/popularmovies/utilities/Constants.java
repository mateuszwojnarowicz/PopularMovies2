package com.mateusz.popularmovies.utilities;

public class Constants {

    //NetworkUtils - Constants for URL creation
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String IMG_URL = "http://image.tmdb.org/t/p/w500/";
    public static final String KEY_API_KEY = "api_key";
    public static final String PATH_VIDEOS = "/videos";
    public static final String PATH_REVIEWS = "/reviews";

    //MainActivity - Constants for SharedPreferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_SORT = "sortBy";
    public static final String VALUE_POP = "popular";
    public static final String VALUE_TOP = "top_rated";

    //MainActivity - Constants for JSON
    public static final String JSON_KEY_MOVIE_RESULTS = "results";
    public static final String JSON_KEY_MOVIE_TITLE = "title";
    public static final String JSON_KEY_MOVIE_ID = "id";
    public static final String JSON_KEY_MOVIE_POSTER_PATH = "poster_path";
    public static final String JSON_KEY_MOVIE_OVERVIEW = "overview";
    public static final String JSON_KEY_MOVIE_VOTE_AVERAGE = "vote_average";
    public static final String JSON_KEY_MOVIE_RELEASE_DATE = "release_date";

    public static final String JSON_KEY_VIDEO_RESULTS = "results";
    public static final String JSON_KEY_VIDEO_NAME = "name";
    public static final String JSON_KEY_VIDEO_KEY = "key";

    public static final String JSON_KEY_REVIEW_RESULTS = "results";
    public static final String JSON_KEY_REVIEW_AUTHOR = "author";
    public static final String JSON_KEY_REVIEW_CONTENT = "content";

    //MainActivity - Constants for Loader
    public static final int LOADER_MOVIES_ID = 1;
    public static final String LOADER_MOVIES_EXTRA = "sort_by";

    //Other constants
    public static final String JSON_KEY_MY_VIDEOS = "myVideos";
    public static final int LOADER_FAVORITES_ID = 2;
    public static final int LOADER_MOVIE_ID = 3;
    public static final String INTENT_EXTRA_MOVIE_KEY = "Movie";
    public static final String BUNDLE_KEY_VIDEOS_URL = "videosUrlString";
    public static final String BUNDLE_KEY_REVIEWS_URL = "reviewsUrlString";
    public static final String SAVE_INSTANCE_KEY_JSON_STRING = "jsonString";
    public static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    public static final String KEY_INSTANCE_STATE_RV_POSITION_1 = "instanceStateRvPosition1";

}
