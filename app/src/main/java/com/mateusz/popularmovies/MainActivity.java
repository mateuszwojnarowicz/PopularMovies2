package com.mateusz.popularmovies;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mateusz.popularmovies.adapter.MovieAdapter;
import com.mateusz.popularmovies.model.Movie;
import com.mateusz.popularmovies.utilities.Constants;
import com.mateusz.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;



//This project is created by Mateusz Wojnarowicz
//Android Developer NanoDegree: Project - Popular Movies

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private MovieAdapter mAdapter;
    private SharedPreferences sharedPreferences;
    private Resources resources;
    private LoaderManager loaderManager;
    private Loader<String> loader;
    private RecyclerView.LayoutManager layoutManager;
    private String sortBy;
    private String jsonMovies;
    private Parcelable mLayoutManagerSavedState;

    @BindView(R.id.pb)
    ProgressBar progressBar;
    @BindView(R.id.er)
    TextView errorTextView;
    @BindView(R.id.rv)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, Activity.MODE_PRIVATE);
        resources = getResources();
        setSharedPref();
        sortBy = sharedPreferences.getString(Constants.KEY_SORT, null);
        jsonMovies = sharedPreferences.getString(Constants.SAVE_INSTANCE_KEY_JSON_STRING, "");


        layoutManager = new GridLayoutManager(this, calculateNoOfColumns(this));
        initialize();
        loaderManager = getSupportLoaderManager();
        loader = loaderManager.getLoader(Constants.LOADER_MOVIES_ID);


        if (savedInstanceState != null) {
            mLayoutManagerSavedState = savedInstanceState.getParcelable(Constants.KEY_INSTANCE_STATE_RV_POSITION_1);
        }
        makeOperationLoadMovies(sortBy);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.KEY_INSTANCE_STATE_RV_POSITION_1, layoutManager.onSaveInstanceState());
    }



    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 150);
        return noOfColumns;
    }


    //Set first-launch pref and set title according to pref
    private void setSharedPref(){
        if(!sharedPreferences.contains(Constants.KEY_SORT)) {
            saveData(Constants.VALUE_POP);
            setTitle(resources.getString(R.string.title_pop));
        } else {
            if (Objects.equals(sharedPreferences.getString(Constants.KEY_SORT, null), Constants.VALUE_POP)) {
                setTitle(resources.getString(R.string.title_pop));
            }
            if (Objects.equals(sharedPreferences.getString(Constants.KEY_SORT, null), Constants.VALUE_TOP)) {
                setTitle(resources.getString(R.string.title_top));
            }
        }
    }

    //Set up the RecyclerView
    private void initialize(){
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mAdapter = new MovieAdapter(this, this);
        recyclerView.setAdapter(mAdapter);
    }

    private void makeOperationLoadMovies(String SORT_BY){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LOADER_MOVIES_EXTRA, SORT_BY);
        if(loader==null){
            loaderManager.initLoader(Constants.LOADER_MOVIES_ID, bundle, this);
        }else{
            loaderManager.restartLoader(Constants.LOADER_MOVIES_ID, bundle, this);
        }

    }

    //Update shared pref
    private void saveData(String SORT_VALUE){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_SORT, SORT_VALUE);
        editor.apply();
    }

    private void saveJsonString(String jsonStringValue){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SAVE_INSTANCE_KEY_JSON_STRING, jsonStringValue);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menu_fav:
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                break;
            case R.id.menu_pop:
                saveData(Constants.VALUE_POP);
                saveJsonString("");
                Toast.makeText(this, resources.getString(R.string.message_popularity),Toast.LENGTH_LONG).show();
                recreate();
                break;
            case R.id.menu_top:
                saveData(Constants.VALUE_TOP);
                saveJsonString("");
                Toast.makeText(this, resources.getString(R.string.message_rating),Toast.LENGTH_LONG).show();
                recreate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        saveJsonString("");
        super.onDestroy();
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {


            @Override
            protected void onStartLoading() {

                if(jsonMovies.equals("")){
                    forceLoad();
                    progressBar.setVisibility(View.VISIBLE);
                    errorTextView.setVisibility(View.INVISIBLE);
                    super.onStartLoading();
                } else {
                    deliverResult(jsonMovies);
                }
                
            }

            @Override
            public void deliverResult(String data) {
                jsonMovies = data;
                super.deliverResult(data);
            }

            @Override
            public String loadInBackground() {

                String jsonString = "";
                URL url = NetworkUtils.buildUrl(args.getString(Constants.LOADER_MOVIES_EXTRA));
                try {
                    jsonString += NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return jsonString;
            }


        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        progressBar.setVisibility(View.GONE);
        jsonMovies = data;
        mAdapter.setData(getMoviesFromJson(jsonMovies));
        if (mLayoutManagerSavedState != null) {
            layoutManager.onRestoreInstanceState(mLayoutManagerSavedState);
        }
        saveJsonString(jsonMovies);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    public List<Movie> getMoviesFromJson(String json){
        List<Movie> movies = new ArrayList<Movie>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray(Constants.JSON_KEY_MOVIE_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                //Get 1 movie from JSON
                String mTitle;
                int mId;
                String mPosterUrl;
                String mPlot;
                double mUserRating;
                String mReleaseDate;

                JSONObject Jmovie = (JSONObject) jsonArray.get(i);
                mTitle = Jmovie.getString(Constants.JSON_KEY_MOVIE_TITLE);
                mId = Jmovie.getInt(Constants.JSON_KEY_MOVIE_ID);
                mPosterUrl = NetworkUtils.getPosterString(Jmovie.getString(Constants.JSON_KEY_MOVIE_POSTER_PATH));
                mPlot = Jmovie.getString(Constants.JSON_KEY_MOVIE_OVERVIEW);
                mUserRating = Jmovie.getDouble(Constants.JSON_KEY_MOVIE_VOTE_AVERAGE);
                mReleaseDate = Jmovie.getString(Constants.JSON_KEY_MOVIE_RELEASE_DATE);
                String videosURL = NetworkUtils.buildUrlVideos(String.valueOf(mId));
                String reviewsURL = NetworkUtils.buildUrlReviews(String.valueOf(mId));
                Movie movie = new Movie(mTitle, mId, mPosterUrl, mPlot, mUserRating, mReleaseDate, videosURL, reviewsURL);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }
}




