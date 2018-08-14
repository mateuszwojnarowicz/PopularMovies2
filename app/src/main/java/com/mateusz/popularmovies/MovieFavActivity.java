package com.mateusz.popularmovies;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mateusz.popularmovies.adapter.TrailerFavAdapter;
import com.mateusz.popularmovies.data.MovieContract;
import com.mateusz.popularmovies.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieFavActivity extends AppCompatActivity {

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
    @BindView(R.id.rv_trailers)
    RecyclerView trailersRV;
    @BindView(R.id.cd_reviews)
    CardView reviewsCD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        trailersRV.setVisibility(View.GONE);
        reviewsCD.setVisibility(View.GONE);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id",0);

        String[] projection = new String[]{
                MovieContract.MoviesEntry.COLUMN_TITLE,
                MovieContract.MoviesEntry.COLUMN_POSTER,
                MovieContract.MoviesEntry.COLUMN_RELEASE_DATE,
                MovieContract.MoviesEntry.COLUMN_RATING,
                MovieContract.MoviesEntry.COLUMN_PLOT
        };
        String selection = MovieContract.MoviesEntry._ID+"=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        final Cursor cursor = getContentResolver().query(
                MovieContract.MoviesEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        int indexTitle = cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_TITLE);
        int indexPoster = cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_POSTER);
        int indexDate = cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_RELEASE_DATE);
        int indexRating = cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_RATING);
        int indexPlot = cursor.getColumnIndex(MovieContract.MoviesEntry.COLUMN_PLOT);

        cursor.moveToFirst();

        final String title = cursor.getString(indexTitle);
        final byte[] poster = cursor.getBlob(indexPoster);
        final String date = cursor.getString(indexDate);
        final String rating = cursor.getString(indexRating);
        final String plot = cursor.getString(indexPlot);

        setTitle(title);
        setPoster(poster);
        setDate(date);
        setRating(rating);
        setPlot(plot);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite()){
                    showDeleteConfirmationDialog(view);

                } else {
                    addToFavorites(
                            title,
                            poster,
                            date,
                            rating,
                            plot);
                    Snackbar.make(view, getString(R.string.message_added), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FavoritesActivity.class));
            }
        });

    }

    private void setTitle(String title){
        titleTV.setText(title);
    }

    private void setPoster(byte[] posterArray){
        Bitmap bmp = BitmapFactory.decodeByteArray(posterArray, 0, posterArray.length);
        imageIG.setImageBitmap(bmp);
    }

    private void setDate(String date){
        dateTV.setText(date);
    }

    private void setRating(String rating){
        ratingTV.setText(rating);
    }

    private void setPlot(String plot){
        plotTV.setText(plot);
    }

    private void setTrailers(RecyclerView recyclerView, ArrayList<String> videos){
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        TrailerFavAdapter mrAdapter = new TrailerFavAdapter(videos, this, this);
        recyclerView.setAdapter(mrAdapter);
    }

    private ArrayList<String> getListFromString(String jsonObjectString){
        try {
            JSONObject jsonObject = new JSONObject(jsonObjectString);
            JSONArray jsonArray = jsonObject.getJSONArray(Constants.JSON_KEY_MY_VIDEOS);
            ArrayList<String> arrayList = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++){
                String s = jsonArray.getString(i);
                arrayList.add(s);
            }
            return arrayList;
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
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

    private void addToFavorites(String title, byte[] poster, String date, String rating, String plot){
        ContentValues contentValues = new ContentValues();
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

}
