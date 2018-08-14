package com.mateusz.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mateusz.popularmovies.R;

import java.util.ArrayList;

public class TrailerFavAdapter extends RecyclerView.Adapter<TrailerFavAdapter.TrailerFavHolder> {

    private final ArrayList<String> mVideos;
    private final Activity mActivity;
    private final Context mContext;

    public TrailerFavAdapter(ArrayList<String> videos, Activity activity, Context context) {
        this.mVideos = videos;
        this.mActivity = activity;
        this.mContext = context;
    }

    @NonNull
    @Override
    public TrailerFavAdapter.TrailerFavHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerFavAdapter.TrailerFavHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerFavAdapter.TrailerFavHolder holder, int position) {
        final String videoUrl = mVideos.get(holder.getAdapterPosition());
        holder.setNumber(String.valueOf(position + 1));
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                PackageManager packageManager = mContext.getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mVideos == null) {
            return 0;
        } else {
            return mVideos.size();
        }
    }

    public class TrailerFavHolder extends RecyclerView.ViewHolder {

        final TextView textView;
        final LinearLayout itemLayout;

        public TrailerFavHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_trailer_number);
            itemLayout = itemView.findViewById(R.id.trailer_layout);
        }

        public void setNumber(String number) {
            textView.setText(number);
        }
    }
}