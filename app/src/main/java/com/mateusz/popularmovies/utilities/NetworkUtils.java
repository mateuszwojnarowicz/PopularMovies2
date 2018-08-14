package com.mateusz.popularmovies.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by mati on 6/28/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public final class NetworkUtils {

    //The movie database url

    //PUT YOUR API KEY BELOW!!!
    private static final String VALUE_API_KEY = "";

    //Builds an url to get movies
    public static URL buildUrl(String VALUE_SORT_BY){
        Uri builtUri = Uri.parse(Constants.BASE_URL+VALUE_SORT_BY).buildUpon()
                .appendQueryParameter(Constants.KEY_API_KEY, VALUE_API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    //Builds an url to get videos
    public static String buildUrlVideos (String MOVIE_ID){
        Uri builtUri = Uri.parse(Constants.BASE_URL+MOVIE_ID+Constants.PATH_VIDEOS).buildUpon()
                .appendQueryParameter(Constants.KEY_API_KEY, VALUE_API_KEY)
                .build();
        return builtUri.toString();
    }

    //Builds an url to get reviews
    public static String buildUrlReviews (String MOVIE_ID){
        Uri builtUri = Uri.parse(Constants.BASE_URL+MOVIE_ID+Constants.PATH_REVIEWS).buildUpon()
                .appendQueryParameter(Constants.KEY_API_KEY, VALUE_API_KEY)
                .build();
        return builtUri.toString();
    }

    //Build a string representing a url of an image
    public static String getPosterString(String endPath){
        return Constants.IMG_URL+endPath;
    }

    //Get response from the internet
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
