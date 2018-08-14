package com.mateusz.popularmovies;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.mateusz.popularmovies.adapter.TrailerAdapter;
import com.mateusz.popularmovies.data.MovieContract;
import com.mateusz.popularmovies.model.Movie;
import com.mateusz.popularmovies.model.Review;
import com.mateusz.popularmovies.model.Video;
import com.mateusz.popularmovies.utilities.Constants;
import com.mateusz.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>>{

    @BindView(R.id.tv_movie_title)
    TextView titleTV;
    @BindView(R.id.ig_movie_poster)
    ImageView imageIG;
    @BindView(R.id.tv_date_value)
    TextView dateTV;
    @BindView(R.id.tv_rating_value)
    TextView ratingTV;
    @BindView(R.id.tv_plot)
    TextView plotTV;
    @BindView(R.id.tv_reviews)
    TextView reviewsTV;
    @BindView(R.id.rv_trailers)
    RecyclerView trailersRV;
    @BindView(R.id.cd_reviews)
    CardView reviewsCD;

    private TrailerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        final Movie movie = intent.getParcelableExtra("Movie");

        //Bind views with data
        setTitle(movie.getmTitle());
        setPoster(movie.getmPosterUrl());
        setDate(movie.getmReleaseDate());
        setRating(movie.getmUserRating());
        setPlot(movie.getmPlot());
        if(!isOnline()){
            trailersRV.setVisibility(View.GONE);
            reviewsCD.setVisibility(View.GONE);
        } else {
            setTrailers(trailersRV);
            makeOperationLoadTrailersReviews(movie.getmVideosUrl(), movie.getmReviewsUrl());
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite()){
                    showDeleteConfirmationDialog(view);

                } else {
                    addToFavorites(movie.getmVideos());
                    Snackbar.make(view, getString(R.string.message_added), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    private void setTitle(String title){
        titleTV.setText(title);
    }

    private void setPoster(String posterUrl){
        Glide.with(this).load(posterUrl).into(imageIG);
    }

    private void setDate(String releaseDate){
        dateTV.setText(releaseDate);
    }

    private void setRating(Double rating){
        int percent = ((int) rating.doubleValue())*10;
        String messageRating = percent+"%";
        ratingTV.setText(messageRating);
    }

    private void setPlot(String plot){
        plotTV.setText(plot);
    }

    private void setTrailers(RecyclerView recyclerView){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        mAdapter = new TrailerAdapter( this, this);
        recyclerView.setAdapter(mAdapter);
    }

    private void setReviews(ArrayList<Review> reviews){
        String reviewsString = "";
        String reviewBy = getResources().getString(R.string.review_by);
        if(reviews.size()>0){
            for(int i = 0; i < reviews.size(); i++){
                Review review = reviews.get(i);
                if(i==0){
                    reviewsString+= "--- "+reviewBy+" "+review.getmName() + " ---";
                } else {
                    reviewsString+= "\n\n--- "+reviewBy+" "+review.getmName() + " ---";
                }
                reviewsString += "\n"+review.getmText();
            }

        } else {
            reviewsString += getResources().getString(R.string.no_reviews);
        }
        reviewsTV.setText(reviewsString);
    }



    private boolean isFavorite(){
        Cursor cursor = getContentResolver().query(
                MovieContract.MoviesEntry.CONTENT_URI,
                null,
                MovieContract.MoviesEntry.COLUMN_TITLE+"=?",
                new String[]{titleTV.getText().toString()},
                null);
        if(cursor.getCount()>0){
            return true;
        } else {
            return false;
        }
    }

    private void addToFavorites(ArrayList<Video> videos){
        ContentValues contentValues = new ContentValues();
        String title = titleTV.getText().toString();
        byte[] poster = getPosterAsByteArray();
        String date = dateTV.getText().toString();
        String rating = ratingTV.getText().toString();
        String plot = plotTV.getText().toString();
        contentValues.put(MovieContract.MoviesEntry.COLUMN_TITLE, title);
        contentValues.put(MovieContract.MoviesEntry.COLUMN_POSTER, poster);
        contentValues.put(MovieContract.MoviesEntry.COLUMN_RELEASE_DATE, date);
        contentValues.put(MovieContract.MoviesEntry.COLUMN_RATING, rating);
        contentValues.put(MovieContract.MoviesEntry.COLUMN_PLOT, plot);
        Uri uri = getContentResolver().insert(MovieContract.MoviesEntry.CONTENT_URI, contentValues);
    }

    private void deleteFromFavorites(){
        Cursor cursor = getContentResolver().query(
                MovieContract.MoviesEntry.CONTENT_URI,
                new String[]{MovieContract.MoviesEntry._ID},
                MovieContract.MoviesEntry.COLUMN_TITLE+"=?",
                new String[]{titleTV.getText().toString()},
                null);
        if(cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex(MovieContract.MoviesEntry._ID));
            Uri uri = MovieContract.MoviesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(Integer.toString(id)).build();
            int deletedRows = getContentResolver().delete(uri, null, null);
        }
        cursor.close();
    }

    private byte[] getPosterAsByteArray(){
        Bitmap image = ((GlideBitmapDrawable) imageIG.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }

    private void showDeleteConfirmationDialog(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message);
        builder.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteFromFavorites();
                dialog.dismiss();
                Snackbar.make(view, getString(R.string.message_deleted), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        builder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<ArrayList<String>>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public ArrayList<String> loadInBackground() {
                ArrayList<String> urls = new ArrayList<String>();
                String videosString = "";
                try {
                    videosString += NetworkUtils.getResponseFromHttpUrl(new URL(args.getString(Constants.BUNDLE_KEY_VIDEOS_URL)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urls.add(videosString);
                String reviewsString = "";
                try {
                    reviewsString += NetworkUtils.getResponseFromHttpUrl(new URL(args.getString(Constants.BUNDLE_KEY_REVIEWS_URL)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urls.add(reviewsString);
                return urls;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<String>> loader, ArrayList<String> data) {
        mAdapter.setData(getVideosFromJSON(data.get(0)));
        setReviews(getReviewsFromJSON(data.get(1)));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<String>> loader) {

    }

    private ArrayList<Video> getVideosFromJSON(String videosString){
        Log.e("videosUrl",videosString);
        ArrayList<Video> mVideos = new ArrayList<Video>();
        try {
            JSONObject jsonObjectVideos = new JSONObject(videosString);
            JSONArray jsonArrayVideos = jsonObjectVideos.getJSONArray(Constants.JSON_KEY_VIDEO_RESULTS);
            if (jsonArrayVideos.length() == 0) {
                mVideos = null;
            } else {
                for (int v = 0; v < jsonArrayVideos.length(); v++) {
                    JSONObject Jvideo = (JSONObject) jsonArrayVideos.get(v);
                    String mVideoName;
                    String mVideoUrlString;
                    mVideoName = Jvideo.getString(Constants.JSON_KEY_VIDEO_NAME);
                    mVideoUrlString = "https://www.youtube.com/watch?v=" + Jvideo.getString(Constants.JSON_KEY_VIDEO_KEY);
                    Video video = new Video(mVideoName, mVideoUrlString);
                    mVideos.add(video);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return mVideos;
    }

    public ArrayList<Review> getReviewsFromJSON(String reviewsString){
        ArrayList<Review> mReviews = new ArrayList<Review>();
        try {
            JSONObject jsonObjectReviews = new JSONObject(reviewsString);
            JSONArray jsonArrayReviews = jsonObjectReviews.getJSONArray(Constants.JSON_KEY_REVIEW_RESULTS);
            if (jsonArrayReviews.length() != 0) {
                for (int r = 0; r < jsonArrayReviews.length(); r++) {
                    JSONObject Jreview = (JSONObject) jsonArrayReviews.get(r);
                    String mReviewName;
                    String mReviewText;
                    mReviewName = Jreview.getString(Constants.JSON_KEY_REVIEW_AUTHOR);
                    mReviewText = Jreview.getString(Constants.JSON_KEY_REVIEW_CONTENT);
                    Review review = new Review(mReviewName, mReviewText);
                    mReviews.add(review);
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return mReviews;
    }

    private void makeOperationLoadTrailersReviews(String urlVideos, String urlReviews){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_KEY_VIDEOS_URL, urlVideos);
        bundle.putString(Constants.BUNDLE_KEY_REVIEWS_URL, urlReviews);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader loader = loaderManager.getLoader(Constants.LOADER_MOVIES_ID);
        if(loader==null){
            loaderManager.initLoader(Constants.LOADER_MOVIE_ID, bundle, this);
        }else{
            loaderManager.restartLoader(Constants.LOADER_MOVIE_ID, bundle, this);
        }

    }
}
