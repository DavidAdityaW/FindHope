package com.example.findhope.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.findhope.Activities.PostDetailActivity;
import com.example.findhope.Models.PostModel;
import com.example.findhope.R;

import java.util.List;

/*
    Dikerjakan pada tanggal : 12 Juli 2021
    Dibuat oleh :
    NIM   : 10118071
    Nama  : David Aditya Winarto
    Kelas : IF-2
*/

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

            // KONFIGURASI POST DETAIL
            // klik to post detail
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("postImage",mData.get(position).getPicture());
                    postDetailActivity.putExtra("name",mData.get(position).getName());
                    postDetailActivity.putExtra("status",mData.get(position).getStatus());
                    postDetailActivity.putExtra("description",mData.get(position).getDescription());
                    postDetailActivity.putExtra("nohp",mData.get(position).getNohp());
                    postDetailActivity.putExtra("email",mData.get(position).getEmail());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate",timestamp);

                    mContext.startActivity(postDetailActivity);
                }
            });

        }
    }
}
