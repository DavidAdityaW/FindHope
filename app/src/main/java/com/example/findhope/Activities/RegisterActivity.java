package com.example.findhope.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.findhope.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

/*
    Dikerjakan pada tanggal : 07 Juli 2021
    Dibuat oleh :
    NIM   : 10118071
    Nama  : David Aditya Winarto
    Kelas : IF-2
*/

public class RegisterActivity extends AppCompatActivity {

    ImageView imgUserPhoto;
    static int PReqCode = 1;
    static int REQUESTCODE = 1;
    Uri pickedImgUri;

    private EditText userName, userEmail, userPassword, userPassword2;
    private Button btnRegister, btnLogin;
    private ProgressBar pbRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgUserPhoto = findViewById(R.id.iv_userphoto);
        userName = findViewById(R.id.et_username);
        userEmail = findViewById(R.id.et_email);
        userPassword = findViewById(R.id.et_password);
        userPassword2 = findViewById(R.id.et_confirmpassword);
        pbRegister = findViewById(R.id.pb_register);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();
        pbRegister.setVisibility(View.INVISIBLE);

        // klik btn register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegister.setVisibility(View.INVISIBLE);
                pbRegister.setVisibility(View.VISIBLE);

                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                    // cek inputan tidak boleh kosong
                    showMessage("Please verify all fields");
                    btnRegister.setVisibility(View.VISIBLE);
                    pbRegister.setVisibility(View.INVISIBLE);
                } else if (!password.equals(password2)) {
                    // cek password dan confirm password tidak sama
                    showMessage("Password doesn't match");
                    btnRegister.setVisibility(View.VISIBLE);
                    pbRegister.setVisibility(View.INVISIBLE);
                }
                else {
                    // start register creating user account
                    createUserAccount(name, email, password);
                }
            }
        });

        // klik imguserphoto memasukan photo profil untuk registrasi
        imgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) { // cek version android minimal lollipop(ndk release/api level 22)
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        // klik btn login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    // KONFIGURASI CREATE ACCOUNT
    // method create user account
    private void createUserAccount(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // user account creating successfully
                    showMessage("Account created successfully");
                    // after creating user account we need to update his profile picture and name
                    updateUserInfo(name, pickedImgUri, mAuth.getCurrentUser());
                } else {
                    // user account creating failed
                    showMessage("Account creation failed " + task.getException().getMessage());
                    btnRegister.setVisibility(View.VISIBLE);
                    pbRegister.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // method update user photo and name
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
        // upload user photo to firebase storage and get url
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded successfully now we can get our image url
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // url contains user image url
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // user info update successfully
                                            showMessage("Register complete");
                                            // change activity to HomeActivity
                                            UpdateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    // method pindah activity ke HomeActivity
    private void UpdateUI() {
        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    // method show toast message
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    // KONFIGURASI PHOTO PROFILE
    // method check and request permission
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }
    }

    // method open gallery
    private void openGallery() {
        // TODO: open gallery intent and wait user to pick an image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE); // memenggil onactivityresult dengan parameternya
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            // user berhasil memilih gambar yang dipilih, we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            imgUserPhoto.setImageURI(pickedImgUri); // set gambar yang dipilih user ke imguserphoto
        }
    }
}