package com.mateusz.popularmovies;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mateusz.popularmovies.adapter.FavoritesAdapter;
import com.mateusz.popularmovies.data.MovieContract;
import com.mateusz.popularmovies.utilities.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.pbFav)
    ProgressBar progressBar;
    @BindView(R.id.erFav)
    TextView errorTextView;
    @BindView(R.id.rvFav)
    RecyclerView recyclerView;

    private FavoritesAdapter mAdapter;
    private LoaderManager loaderManager;
    private Loader<Cursor> loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, calculateNoOfColumns(this)));
        mAdapter = new FavoritesAdapter(this);
        recyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(Constants.LOADER_FAVORITES_ID,null, this);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(Constants.LOADER_FAVORITES_ID, null, this);
    }*/

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mData = null;

            @Override
            protected void onStartLoading() {
                if(mData!=null){
                    deliverResult(mData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    return getContentResolver().query(MovieContract.MoviesEntry.CONTENT_URI,
                            null, null, null, null);
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mData = data;
                super.deliverResult(data);
            }
        };
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 150);
        return noOfColumns;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
