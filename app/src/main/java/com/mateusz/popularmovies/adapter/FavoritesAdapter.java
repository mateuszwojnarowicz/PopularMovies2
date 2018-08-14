package com.mateusz.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mateusz.popularmovies.MovieFavActivity;
import com.mateusz.popularmovies.R;
import com.mateusz.popularmovies.data.MovieContract.MoviesEntry;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteHolder> {

    private Cursor mCursor;
    private Context mContext;

    public FavoritesAdapter(Context mContext){
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new FavoriteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteHolder holder, int position) {

        int indexId = mCursor.getColumnIndex(MoviesEntry._ID);
        int indexTitle = mCursor.getColumnIndex(MoviesEntry.COLUMN_TITLE);
        int indexPoster = mCursor.getColumnIndex(MoviesEntry.COLUMN_POSTER);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(indexId);
        String title = mCursor.getString(indexTitle);
        byte[] posterArray = mCursor.getBlob(indexPoster);

        holder.itemView.setTag(id);

        holder.textView.setText(title);
        Bitmap bmp = BitmapFactory.decodeByteArray(posterArray, 0, posterArray.length);
        holder.imageView.setImageBitmap(bmp);
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(mContext, MovieFavActivity.class);
                intent.putExtra("id", id);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    class FavoriteHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;
        final TextView textView;
        final LinearLayout itemLayout;

        public FavoriteHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.card_iv);
            textView = itemView.findViewById(R.id.card_tv);
            itemLayout = itemView.findViewById(R.id.item_ll);
        }

        public void setTitle(String title){textView.setText(title);}

    }
}
