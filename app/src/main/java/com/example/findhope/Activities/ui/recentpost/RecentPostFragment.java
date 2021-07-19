package com.example.findhope.Activities.ui.recentpost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.findhope.Adapters.PostAdapter;
import com.example.findhope.Models.PostModel;
import com.example.findhope.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecentPostFragment extends Fragment {

    private RecentPostViewModel recentpostViewModel;

    ImageView imgUserPhoto;
    TextView tvName;

    RecyclerView postRecyclerView;
    PostAdapter postAdapterRecent;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<PostModel> postListRecent;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String myuserid;
    Query recent;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        recentpostViewModel =
//                ViewModelProviders.of(this).get(RecentPostViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_recentpost, container, false);
//        final TextView textView = root.findViewById(R.id.text_gallery);
//        recentpostViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        return root;

        // KONFIGURASI LIST POST
        // Inflate the layout for this fragment - list post berhasil cuma yang baru dibawah
//        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
//        postRecyclerView = fragmentView.findViewById(R.id.postRV);
//        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        postRecyclerView.setHasFixedSize(true);
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference("Posts");
//        return fragmentView;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        myuserid = firebaseUser.getUid(); // get uid user pelogin

        // Inflate the layout for this fragment - list post berhasil dan yang baru diatas
        LinearLayoutManager lin = new LinearLayoutManager(getActivity());
        lin.setStackFromEnd(true);
        lin.setReverseLayout(true);
        View fragmentView = inflater.inflate(R.layout.fragment_recentpost, container, false);
        postRecyclerView = fragmentView.findViewById(R.id.postRVRecent);
        postRecyclerView.setLayoutManager(lin);
        postRecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance("https://findhope-ac255-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference();
        //databaseReference = firebaseDatabase.getReference("Posts").child(firebaseUser.getUid());
        recent = databaseReference.child("Posts").orderByChild("userId").equalTo(myuserid); // panggil sesuai uid = uid user pelogin

        imgUserPhoto = fragmentView.findViewById(R.id.iv_userphoto);
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgUserPhoto);
        tvName = fragmentView.findViewById(R.id.tv_name);
        tvName.setText(firebaseUser.getDisplayName());

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // get list posts from firebase database
        recent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postListRecent = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                    PostModel postModel = postsnap.getValue(PostModel.class);
                    postListRecent.add(postModel);
                }
                postAdapterRecent = new PostAdapter(getActivity(),postListRecent);
                postRecyclerView.setAdapter(postAdapterRecent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}