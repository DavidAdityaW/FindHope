package com.example.findhope.Activities.ui.recentpost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.findhope.R;

public class RecentPostFragment extends Fragment {

    private RecentPostViewModel recentpostViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recentpostViewModel =
                ViewModelProviders.of(this).get(RecentPostViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recentpost, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        recentpostViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}