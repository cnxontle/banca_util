package com.example.bbva;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> sharedValue = new MutableLiveData<>();

    public void setSharedValue(String value) {
        sharedValue.setValue(value);
    }

    public LiveData<String> getSharedValue() {
        return sharedValue;
    }
}