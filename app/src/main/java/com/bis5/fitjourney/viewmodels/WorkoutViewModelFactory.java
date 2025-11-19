package com.bis5.fitjourney.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bis5.fitjourney.models.ExerciseDao;
import com.bis5.fitjourney.models.SetLogDao;
import com.bis5.fitjourney.models.WorkoutDao;
import com.bis5.fitjourney.models.WorkoutLogDao;

public class WorkoutViewModelFactory implements ViewModelProvider.Factory {

    private final WorkoutDao workoutDao;
    private final ExerciseDao exerciseDao;
    private final WorkoutLogDao workoutLogDao;
    private final SetLogDao setLogDao;

    public WorkoutViewModelFactory(WorkoutDao workoutDao, ExerciseDao exerciseDao, WorkoutLogDao workoutLogDao, SetLogDao setLogDao) {
        this.workoutDao = workoutDao;
        this.exerciseDao = exerciseDao;
        this.workoutLogDao = workoutLogDao;
        this.setLogDao = setLogDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(WorkoutViewModel.class)) {
            // Use direct instantiation instead of reflection. It is safer and simpler.
            return (T) new WorkoutViewModel(workoutDao, exerciseDao, workoutLogDao, setLogDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
