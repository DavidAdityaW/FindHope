package com.example.findhope.Activities.ui.recentpost;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecentPostViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RecentPostViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is recent post fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}