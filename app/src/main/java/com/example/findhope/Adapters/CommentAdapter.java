package com.example.findhope.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.findhope.Models.CommentModel;
import com.example.findhope.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/*
    Dikerjakan pada tanggal : 16 Juli 2021
    Dibuat oleh :
    NIM   : 10118071
    Nama  : David Aditya Winarto
    Kelas : IF-2
*/

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<CommentModel> mData;

    // constructor
    public CommentAdapter(Context mContext, List<CommentModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    // implement method
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment_item,parent,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.tvName.setText(mData.get(position).getUname());
        holder.tvContent.setText(mData.get(position).getContent());
        // format waktu comment timestamp
        holder.tvDate.setText(timestampToString((Long)mData.get(position).getTimestamp()));
        Glide.with(mContext).load(mData.get(position).getUimg()).into(holder.imgUser);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // class commentviewholder
    public class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvContent, tvDate;
        ImageView imgUser;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.comment_username);
            tvContent = itemView.findViewById(R.id.comment_content);
            tvDate = itemView.findViewById(R.id.comment_date);
            imgUser = itemView.findViewById(R.id.comment_user_img);
        }
    }

    // method menampilkan format waktu tanggal comment
    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();
        return date;
    }
}
