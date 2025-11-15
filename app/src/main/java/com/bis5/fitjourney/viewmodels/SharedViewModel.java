package com.bis5.fitjourney.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();

    public void setUserEmail(String email) {
        userEmail.setValue(email);
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }
}
