package com.example.findhope.Adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.findhope.Models.PostModel;
import com.example.findhope.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<PostModel> mData;

    // constructor
    public PostAdapter(Context mContext, List<PostModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    // implement method
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvName.setText(mData.get(position).getName());
        holder.tvStatus.setText(mData.get(position).getStatus());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgUserPhoto);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // class myviewholder
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvStatus;
        ImageView imgPost, imgUserPhoto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.row_post_name);
            tvStatus = itemView.findViewById(R.id.row_post_status);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgUserPhoto = itemView.findViewById(R.id.row_post_profile_img);

        }
    }
}
