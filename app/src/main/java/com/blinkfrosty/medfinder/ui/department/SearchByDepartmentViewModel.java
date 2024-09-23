package com.blinkfrosty.medfinder.ui.department;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchByDepartmentViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SearchByDepartmentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Search by Department Fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}