package com.example.findhope.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.findhope.Models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.findhope.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/*
    Dikerjakan pada tanggal : 08 Juli 2021
    Dibuat oleh :
    NIM   : 10118071
    Nama  : David Aditya Winarto
    Kelas : IF-2
*/

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;


    private static final int PReqCode = 2;
    private static final int REQUESTCODE = 2;
    private Uri pickedImgUri = null;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    Dialog popAddPost;
    ImageView popupUserPhoto, popupImage, popupAddBtn;
    EditText popupName, popupDescription, popupNoHp, popupEmail;
    RadioGroup rgStatus;
    RadioButton rbStatusOption, rbMissingPeople, rbFoundPeople;
    String strStatus;
    ProgressBar popupProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // views
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // popup
        iniPopup();
        setupPopupImageClick(); // klik post image


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show(); // klik button floating untuk buka popupaddpost

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow) // nav_home=home, nav_gallery=recent post
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // panggil method updatenavheader untuk menampilkan info user sign in
        updateNavHeader();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // KONFIGURASI SIDEBAR MENU TITIK
    // method item selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // KONFIGURASI POPUP ADD POST
    // method popup
    private void iniPopup() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        // popup widget
        popupUserPhoto = popAddPost.findViewById(R.id.popup_userphoto);
        popupImage =  popAddPost.findViewById(R.id.popup_image);
        popupName =  popAddPost.findViewById(R.id.popup_name);
        rgStatus =  popAddPost.findViewById(R.id.rg_status); // radiogroup
        rbMissingPeople =  popAddPost.findViewById(R.id.rb_missingpeople); // radiobutton
        rbFoundPeople =  popAddPost.findViewById(R.id.rb_foundpeople); // radiobutton
        popupDescription =  popAddPost.findViewById(R.id.popup_description);
        popupNoHp =  popAddPost.findViewById(R.id.popup_nohp);
        popupEmail =  popAddPost.findViewById(R.id.popup_email);
        popupAddBtn =  popAddPost.findViewById(R.id.popup_add_btn);
        popupProgressBar =  popAddPost.findViewById(R.id.popup_progressBar);

        // load current userphoto
        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popupUserPhoto);

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
                }
            }
        });

        // klik popup add btn
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupProgressBar.setVisibility(View.VISIBLE);

                if (!popupName.getText().toString().isEmpty() && !popupDescription.getText().toString().isEmpty() && !popupNoHp.getText().toString().isEmpty() && !popupEmail.getText().toString().isEmpty() && pickedImgUri != null) {
                    // TODO: create post object and add it to firebase database
                    // first we need upload post image access firebase storage
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("posts_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    // create post object
                                    PostModel postModel = new PostModel(popupName.getText().toString(), // name
                                            imageDownloadLink, // image post
                                            strStatus, // status missing people or found people
                                            popupDescription.getText().toString(), // description
                                            popupNoHp.getText().toString(), // nohp
                                            popupEmail.getText().toString(), // email
                                            currentUser.getUid(), // uid
                                            currentUser.getPhotoUrl().toString()); // user photo

                                    // add post to firebase database
                                    addPost(postModel);
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
    // method add post to firebase database
    private void addPost(PostModel postModel) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://findhope-ac255-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("Posts").push();

        // get post unique ID and update post key
        String key = myRef.getKey();
        postModel.setPostKey(key);

        // add post to firebase database final
        myRef.setValue(postModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added Successfully");
                popupAddBtn.setVisibility(View.VISIBLE);
                popupProgressBar.setVisibility(View.INVISIBLE);
                popAddPost.dismiss();
            }
        });
    }
    // method show toast message
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    // KONFIGURASI PILIH POST IMAGE
    // method image post click
    private void setupPopupImageClick() {
        popupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();
            }
        });
    }
    // method check and request permission
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(Home.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            openGallery(); // AMBIL PHOTO HANYA PADA GALLERY
//            starCropActivity(); // COBA IMAGE CROPPER
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
    // COBA IMAGE CROPPER======================
//    private void starCropActivity () {
//        CropImage.activity()
//                .setFixAspectRatio(true)
//                .setAspectRatio(1,1)
//                .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
//                .setActivityTitle("Crop")
//                .setActivityMenuIconColor(R.color.colorPrimary)
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .start(this);
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
//                    popupImage.setImageBitmap(bitmap);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//                // pickedImgUri = result.getUri();
//                popupImage.setImageURI(resultUri);
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//    }

    // KONFIGURASI INFO USER SIGN IN
    // method info user sign in di navigasi header
    public void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_username);
        TextView navUserEmail = headerView.findViewById(R.id.nav_useremail);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_userphoto);

        navUserEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());
        // we will use glide to load image
        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
    }
}