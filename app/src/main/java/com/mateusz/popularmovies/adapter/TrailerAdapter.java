package com.mateusz.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mateusz.popularmovies.R;
import com.mateusz.popularmovies.model.Video;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {

        private final Activity mActivity;
        private final Context mContext;

        public TrailerAdapter(Activity activity, Context context) {

            this.mActivity = activity;
            this.mContext = context;
        }

        @NonNull
        @Override
        public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
            return new TrailerHolder(view);
        }

        private ArrayList<Video> mData;
        public void setData(ArrayList<Video> data){
            mData = data;
            notifyDataSetChanged();
            Log.e("Size of mData", String.valueOf(mData.size()));
        }
        @Override
        public void onBindViewHolder(@NonNull final TrailerHolder holder, int position) {
            final Video video = mData.get(holder.getAdapterPosition());
            holder.setNumber(String.valueOf(holder.getAdapterPosition()+1));
            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getmUrlString()));
                    PackageManager packageManager = mContext.getPackageManager();
                    if (intent.resolveActivity(packageManager) != null) {
                    mContext.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if(mData==null) {
                return 0;
            } else {
                return mData.size();
            }
        }

        public class TrailerHolder extends RecyclerView.ViewHolder{

            final TextView textView;
            final LinearLayout itemLayout;

            public TrailerHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.tv_trailer_number);
                itemLayout = itemView.findViewById(R.id.trailer_layout);
            }

            public void setNumber(String number){textView.setText(number);}
        }


}
