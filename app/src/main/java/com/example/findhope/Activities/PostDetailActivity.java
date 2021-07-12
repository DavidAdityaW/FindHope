package com.example.findhope.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.findhope.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
//    static String COMMENT_KEY = "Comment";

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

        String postDesc = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDesc);

        String postNoHp = getIntent().getExtras().getString("nohp");
        txtPostNoHp.setText(postNoHp);

        String postEmail = getIntent().getExtras().getString("email");
        txtPostEmail.setText(postEmail);

        String userPostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(imgUserPost);

        String date = timestampToString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);

        // KONFIGURASI COMMENT
        // set comment user image
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);
        // get post id
        PostKey = getIntent().getExtras().getString("postKey");

//        // ini recyclerview comment
//        iniRvComment();
    }

    // method commetn
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