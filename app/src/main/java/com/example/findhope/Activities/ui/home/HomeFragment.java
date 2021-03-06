package com.example.findhope.Activities.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findhope.Adapters.PostAdapter;
import com.example.findhope.Models.PostModel;
import com.example.findhope.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;


    EditText etSearch;
    TextView tvCountMissingPeople, tvCountFoundPeople;

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<PostModel> postList;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        return root;

        // KONFIGURASI LIST POST - ALL
        // Inflate the layout for this fragment - list post berhasil cuma yang baru dibawah
//        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
//        postRecyclerView = fragmentView.findViewById(R.id.postRV);
//        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        postRecyclerView.setHasFixedSize(true);
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference("Posts");
//        return fragmentView;

        // Inflate the layout for this fragment - list post berhasil dan yang baru diatas
        LinearLayoutManager lin = new LinearLayoutManager(getActivity());
        lin.setStackFromEnd(true);
        lin.setReverseLayout(true);
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        postRecyclerView = fragmentView.findViewById(R.id.postRV);
        postRecyclerView.setLayoutManager(lin);
        postRecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance("https://findhope-ac255-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference("Posts");

        // KONFIGURASI SEARCH
        etSearch = fragmentView.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != null) {
                    loadDataSearch(s.toString());
                } else {
                    loadDataSearch("");
                }
            }
        });

        // KONFIGURASI COUNT MISSING AND FOUND POST
        tvCountMissingPeople = fragmentView.findViewById(R.id.tv_countmissingpeople);
        tvCountFoundPeople = fragmentView.findViewById(R.id.tv_countfoundpeople);
        // missing people
        Query queryCountMissingPeople = databaseReference.orderByChild("status").equalTo("MISSING PEOPLE");
        queryCountMissingPeople.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int countMissing;
                    countMissing = (int) dataSnapshot.getChildrenCount(); // get count missing people

                    tvCountMissingPeople.setText(Integer.toString(countMissing));
                } else {
                    tvCountMissingPeople.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // found people
        Query queryCountFoundPeople = databaseReference.orderByChild("status").equalTo("FOUND PEOPLE");
        queryCountFoundPeople.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int countFound;
                    countFound = (int) dataSnapshot.getChildrenCount(); // get count found people

                    tvCountFoundPeople.setText(Integer.toString(countFound));
                } else {
                    tvCountFoundPeople.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // get list posts from firebase database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                    PostModel postModel = postsnap.getValue(PostModel.class);
                    postList.add(postModel);
                }
                postAdapter = new PostAdapter(getActivity(),postList);
                postRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // searching all data
    private void loadDataSearch(String data) {
        Query query = databaseReference.orderByChild("name").startAt(data).endAt(data+"\uf8ff");
        // get list posts from firebase database
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                    PostModel postModel = postsnap.getValue(PostModel.class);
                    postList.add(postModel);
                }
                postAdapter = new PostAdapter(getActivity(),postList);
                postRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}