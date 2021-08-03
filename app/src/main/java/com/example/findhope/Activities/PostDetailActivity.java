package com.example.findhope.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.findhope.Adapters.CommentAdapter;
import com.example.findhope.Models.CommentModel;
import com.example.findhope.Models.PostModel;
import com.example.findhope.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

    // Comment
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<CommentModel> listComment;
    static String COMMENT_KEY = "Comment";

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    // edit post
    Dialog popEditPost;
    ImageView popupUserPhoto, popupImage, popupAddBtn;
    EditText popupName, popupDescription, popupNoHp, popupEmail;
    RadioGroup rgStatus;
    RadioButton rbStatusOption, rbMissingPeople, rbFoundPeople;
    String strStatus;
    ProgressBar popupProgressBar;
    // picked image post
    private static final int PReqCode = 2;
    private static final int REQUESTCODE = 2;
    private Uri pickedImgUri = null;

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
        final String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        final String postName = getIntent().getExtras().getString("name");
        txtPostName.setText(postName);

        final String postStatus = getIntent().getExtras().getString("status");
        txtPostStatus.setText(postStatus);
        // merubah warna textview status
        if (txtPostStatus.getText().equals("MISSING PEOPLE")) {
            txtPostStatus.setTextColor(Color.parseColor("#ff0000"));
        } else if (txtPostStatus.getText().equals("FOUND PEOPLE")) {
            txtPostStatus.setTextColor(Color.parseColor("#1db954"));
        }

        final String postDesc = getIntent().getExtras().getString("description");
        txtPostDesc.setText(postDesc);

        final String postNoHp = getIntent().getExtras().getString("nohp");
        txtPostNoHp.setText(postNoHp);
        // menelpon otomatis
        Linkify.addLinks(txtPostNoHp, Linkify.PHONE_NUMBERS);

        final String postEmail = getIntent().getExtras().getString("email");
        txtPostEmail.setText(postEmail);
        // mengirim email otomatis
        Linkify.addLinks(txtPostEmail, Linkify.EMAIL_ADDRESSES);

        final String userPostImage = getIntent().getExtras().getString("userPhoto");
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

        // ini tampilkan list comment
        iniRvComment();


        // KONFIGURASI DELETE DAN UPDATE
        // cek current user login sama dengan user id post
        String myuserid = firebaseUser.getUid();
        final String postUserId = getIntent().getExtras().getString("userId");
        if (myuserid.equals(postUserId)) {
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnEdit.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.INVISIBLE);
        }
        // delete post
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(PostDetailActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure delete this post?")
                        .setPositiveButton("Yes", null)
                        .setNegativeButton("No", null)
                        .show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference deleteReference = firebaseDatabase.getReference("Posts").child(PostKey);
                        DatabaseReference commentReference = firebaseDatabase.getReference("Comment").child(PostKey);
                        deleteReference.removeValue();
                        commentReference.removeValue();
                        Intent homeIntent = new Intent(getApplicationContext(), Home.class);
                        startActivity(homeIntent);
                        finish();
                        showMessage("Delete post successfully");
                    }
                });
            }
        });

        // edit post
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popEditPost = new Dialog(PostDetailActivity.this);
                popEditPost.setContentView(R.layout.popup_add_post);
                popEditPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popEditPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
                popEditPost.getWindow().getAttributes().gravity = Gravity.TOP;
                popEditPost.show();

                // popup widget
                popupUserPhoto = popEditPost.findViewById(R.id.popup_userphoto);
                popupImage =  popEditPost.findViewById(R.id.popup_image);
                popupName =  popEditPost.findViewById(R.id.popup_name);
                rgStatus =  popEditPost.findViewById(R.id.rg_status); // radiogroup
                rbMissingPeople =  popEditPost.findViewById(R.id.rb_missingpeople); // radiobutton
                rbFoundPeople =  popEditPost.findViewById(R.id.rb_foundpeople); // radiobutton
                popupDescription =  popEditPost.findViewById(R.id.popup_description);
                popupNoHp =  popEditPost.findViewById(R.id.popup_nohp);
                popupEmail =  popEditPost.findViewById(R.id.popup_email);
                popupAddBtn =  popEditPost.findViewById(R.id.popup_add_btn);
                popupProgressBar =  popEditPost.findViewById(R.id.popup_progressBar);

                // set data ke inputan edit
                popupName.setText(postName);
                Glide.with(PostDetailActivity.this).load(userPostImage).into(popupUserPhoto);
                Glide.with(PostDetailActivity.this).load(postImage).into(popupImage);
                if (postStatus.equals("MISSING PEOPLE")) {
                    rbMissingPeople.setChecked(true);
                } else if (postStatus.equals("FOUND PEOPLE")) {
                    rbFoundPeople.setChecked(true);
                }
                popupDescription.setText(postDesc);
                popupNoHp.setText(postNoHp);
                popupEmail.setText(postEmail);

                // klik image post jika ingin diubah
                popupImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAndRequestForPermission();
                    }
                });
                // select status from radio button
                rgStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        rbStatusOption = rgStatus.findViewById(checkedId);
                        switch (checkedId) {
                            case  R.id.rb_missingpeople :
                                strStatus = rbStatusOption.getText().toString(); // Missing people
                                break;
                            case  R.id.rb_foundpeople :
                                strStatus = rbStatusOption.getText().toString(); // Found People
                                break;
                            default:
                                strStatus = rbStatusOption.getText().toString(); // Missing people
                        }
                    }
                });

                // klik popup edit btn
                popupAddBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupAddBtn.setVisibility(View.INVISIBLE);
                        popupProgressBar.setVisibility(View.VISIBLE);

                        if (!popupName.getText().toString().isEmpty() && !popupDescription.getText().toString().isEmpty() && !popupNoHp.getText().toString().isEmpty() && !popupEmail.getText().toString().isEmpty()) {
                            // TODO: create post object and add it to firebase database
                            // first we need upload post image access firebase storage
                            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("posts_images");
                            final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String imageDownloadLink = uri.toString();
                                            final String updateName = popupName.getText().toString();
                                            final String updateStatus = strStatus;
                                            final String updateDescription = popupDescription.getText().toString();
                                            final String updateNoHp = popupNoHp.getText().toString();
                                            final String updateEmail = popupEmail.getText().toString();

                                            final String postKey = getIntent().getExtras().getString("postKey");


                                            // delete image post before


                                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://findhope-ac255-default-rtdb.firebaseio.com/");
                                            DatabaseReference myRef = database.getReference("Posts");
                                            Query query = myRef.orderByChild("postKey").equalTo(postKey);
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                                        ds.getRef().child("name").setValue(updateName);
                                                        ds.getRef().child("picture").setValue(imageDownloadLink);
                                                        ds.getRef().child("status").setValue(updateStatus);
                                                        ds.getRef().child("description").setValue(updateDescription);
                                                        ds.getRef().child("nohp").setValue(updateNoHp);
                                                        ds.getRef().child("email").setValue(updateEmail);

                                                        showMessage("Post updated successfully");
                                                        popupAddBtn.setVisibility(View.VISIBLE);
                                                        popupProgressBar.setVisibility(View.INVISIBLE);
                                                        popEditPost.dismiss();
                                                        Intent homeIntent = new Intent(getApplicationContext(), Home.class);
                                                        startActivity(homeIntent);
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // something goes wrong uploading picture
                                            showMessage(e.getMessage());
                                            popupAddBtn.setVisibility(View.VISIBLE);
                                            popupProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });

                                }
                            });
                        } else {
                            showMessage("Please verify all input fields and choose post image");
                            popupAddBtn.setVisibility(View.VISIBLE);
                            popupProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    // KONFIGURASI EDIT IMAGE POST
    // method check and request permission
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(PostDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PostDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(PostDetailActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(PostDetailActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            openGallery(); // AMBIL PHOTO HANYA PADA GALLERY
        }
    }
    // method open gallery
    private void openGallery() {
        // TODO: open gallery intent and wait user to pick an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }
    // method when user picked an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            // the user has successfully picked image we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            popupImage.setImageURI(pickedImgUri);
        }
    }


    // KONFIGURASI COMMENT
    // method tampilkan list comment
    private void iniRvComment() {
        RvComment.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {
                    CommentModel commentModel = snap.getValue(CommentModel.class);
                    listComment.add(commentModel);
                }
                commentAdapter = new CommentAdapter(getApplicationContext(),listComment);
                RvComment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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