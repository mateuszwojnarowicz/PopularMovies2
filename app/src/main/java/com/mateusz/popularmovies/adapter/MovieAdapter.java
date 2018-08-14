package com.mateusz.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mateusz.popularmovies.MovieActivity;
import com.mateusz.popularmovies.R;
import com.mateusz.popularmovies.model.Movie;
import com.mateusz.popularmovies.utilities.Constants;

import java.util.List;

/**
 * Created by mati on 6/28/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private final Activity mActivity;
    private final Context mContext;

    public MovieAdapter(Activity activity, Context context) {
        this.mActivity = activity;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MovieHolder(view);
    }

    private List<Movie> mData;

    public void setData(List<Movie> data){
        mData = data;
        notifyDataSetChanged();
        Log.e("Size of mData", String.valueOf(mData.size()));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        final Movie movie = mData.get(holder.getAdapterPosition());
        holder.setTitle(movie.getmTitle());
        Glide.with(mActivity)
                .load(movie.getmPosterUrl())
                .into(holder.imageView);
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(mContext, MovieActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_MOVIE_KEY, movie);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mData==null){
            return 0;
        } else {
            return mData.size();
        }
    }

    public class MovieHolder extends RecyclerView.ViewHolder{

        final ImageView imageView;
        final TextView textView;
        final LinearLayout itemLayout;

        public MovieHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.card_iv);
            textView = itemView.findViewById(R.id.card_tv);
            itemLayout = itemView.findViewById(R.id.item_ll);
        }

        public void setTitle(String title){textView.setText(title);}
    }



}
