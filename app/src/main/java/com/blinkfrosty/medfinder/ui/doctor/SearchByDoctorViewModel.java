package com.blinkfrosty.medfinder.ui.doctor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchByDoctorViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SearchByDoctorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Search by Doctor Fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}