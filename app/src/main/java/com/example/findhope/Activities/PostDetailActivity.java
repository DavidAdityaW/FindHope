package com.example.findhope.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.findhope.Models.CommentModel;
import com.example.findhope.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

/*
    Dikerjakan pada tanggal : 12 Juli 2021
    Dibuat oleh :
    NIM   : 10118071
    Nama  : David Aditya Winarto
    Kelas : IF-2
*/

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgPost, imgUserPost, imgCurrentUser;
    TextView txtPostName, txtPostStatus, txtPostDateName, txtPostDesc, txtPostNoHp, txtPostEmail;
    EditText editTextComment;
    Button btnAddComment, btnEdit, btnDelete;
    String PostKey;

    RecyclerView RvComment;
//    CommentAdapter commentAdapter;
//    List<Comment> listComment;
    static String COMMENT_KEY = "Comment";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // set the status bar to transfarent
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getSupportActionBar().hide();

        imgPost = findViewById(R.id.post_detail_img);
        imgUserPost = findViewById(R.id.post_detail_user_img);
        imgCurrentUser = findViewById(R.id.post_detail_comment_user_img);
        txtPostName = findViewById(R.id.post_detail_name);
        txtPostStatus = findViewById(R.id.post_detail_status);
        txtPostDateName = findViewById(R.id.post_detail_date_name);
        txtPostDesc = findViewById(R.id.post_detail_description);
        txtPostNoHp = findViewById(R.id.post_detail_nohp);
        txtPostEmail = findViewById(R.id.post_detail_email);
        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_comment_btn);
        btnEdit = findViewById(R.id.post_detail_edit_btn);
        btnDelete = findViewById(R.id.post_detail_delete_btn);
        RvComment = findViewById(R.id.rv_comment);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://findhope-ac255-default-rtdb.firebaseio.com/");

        // KONFIGURASI BIND DATA
        // get and bind all data dari postadapter
        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postName = getIntent().getExtras().getString("name");
        txtPostName.setText(postName);

        String postStatus = getIntent().getExtras().getString("status");
        txtPostStatus.setText(postStatus);
        // merubah warna textview status
        if (txtPostStatus.getText().equals("MISSING PEOPLE")) {
            txtPostStatus.setTextColor(Color.RED);
        } else if (txtPostStatus.getText().equals("FOUND PEOPLE")) {
            txtPostStatus.setTextColor(Color.parseColor("#1db954"));
        }

        String postDesc = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDesc);

        String postNoHp = getIntent().getExtras().getString("nohp");
        txtPostNoHp.setText(postNoHp);
        // menelpon otomatis
        Linkify.addLinks(txtPostNoHp, Linkify.PHONE_NUMBERS);

        String postEmail = getIntent().getExtras().getString("email");
        txtPostEmail.setText(postEmail);
        // mengirim email otomatis
        Linkify.addLinks(txtPostEmail, Linkify.EMAIL_ADDRESSES);

        String userPostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(imgUserPost);

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);

        // KONFIGURASI COMMENT
        // set comment user image
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);
        // get post id
        PostKey = getIntent().getExtras().getString("postKey");

        // add comment
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey).push();
                String comment_content = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();

                CommentModel commentModel = new CommentModel(comment_content,uid,uimg,uname);
                commentReference.setValue(commentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Comment added");
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Fail to add comment : "+e.getMessage());
                    }
                });
            }
        });

//        // ini recyclerview comment
//        iniRvComment();
    }

    // method comment
//    private void iniRvComment() {
//        RvComment.setLayoutManager(new LinearLayoutManager(this));
//
//        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
//        commentRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                listComment = new ArrayList<>();
//                for (DataSnapshot snap:dataSnapshot.getChildren()) {
//                    Comment comment = snap.getValue(Comment.class);
//                    listComment.add(comment);
//                }
//                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
//                RvComment.setAdapter(commentAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    // method show toast message
    private void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    // method menampilkan format tanggal post
    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd MMMM yyyy",calendar).toString();
        return date;
    }
}