package com.bis5.fitjourney.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// A Factory to create the HomeViewModel with its dependencies.
public class HomeViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final WorkoutViewModel workoutViewModel;

    public HomeViewModelFactory(Application application, WorkoutViewModel workoutViewModel) {
        this.application = application;
        this.workoutViewModel = workoutViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(application, workoutViewModel);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
