package com.example.findhope.Activities.ui.foundpeople;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoundPeopleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoundPeopleFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FoundPeopleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoundPeopleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FoundPeopleFragment newInstance(String param1, String param2) {
        FoundPeopleFragment fragment = new FoundPeopleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    EditText etSearch;

    RecyclerView postRecyclerView;
    PostAdapter postAdapterFoundPeople;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<PostModel> postListFoundPeople;
    Query query;
    String status = "FOUND PEOPLE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_found_people, container, false);

        // KONFIGURASI LIST POST - FILTER YANG STATUS = FOUND PEOPLE
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
        View fragmentView = inflater.inflate(R.layout.fragment_found_people, container, false);
        postRecyclerView = fragmentView.findViewById(R.id.postRVFoundPeople);
        postRecyclerView.setLayoutManager(lin);
        postRecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance("https://findhope-ac255-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference();
        query = databaseReference.child("Posts").orderByChild("status").equalTo(status); // tampilkan post sesuai status = FOUND PEOPLE

//        // KONFIGURASI SEARCH
//        etSearch = fragmentView.findViewById(R.id.et_search);
//        etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.toString() != null) {
//                    loadDataSearch(s.toString());
//                } else {
//                    loadDataSearch("");
//                }
//            }
//        });

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // get list posts from firebase database
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postListFoundPeople = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                    PostModel postModel = postsnap.getValue(PostModel.class);
                    postListFoundPeople.add(postModel);
                }
                postAdapterFoundPeople = new PostAdapter(getActivity(),postListFoundPeople);
                postRecyclerView.setAdapter(postAdapterFoundPeople);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    // searching data
//    private void loadDataSearch(String data) {
//        Query query = databaseReference.orderByChild("name").startAt(data).endAt(data+"\uf8ff");
//        // get list posts from firebase database
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                postListFoundPeople = new ArrayList<>();
//                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
//                    PostModel postModel = postsnap.getValue(PostModel.class);
//                    postListFoundPeople.add(postModel);
//                }
//                postAdapterFoundPeople = new PostAdapter(getActivity(),postListFoundPeople);
//                postRecyclerView.setAdapter(postAdapterFoundPeople);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}