package com.bis5.fitjourney.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bis5.fitjourney.models.ExerciseDao;
import com.bis5.fitjourney.models.SetLogDao;
import com.bis5.fitjourney.models.WorkoutDao;
import com.bis5.fitjourney.models.WorkoutLogDao;

// This is the standard, boilerplate, correct way to write a ViewModel Factory in Java.
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
        // This generic, reflective approach is safer and more standard than a direct cast.
        if (modelClass.isAssignableFrom(WorkoutViewModel.class)) {
            try {
                return modelClass.getConstructor(WorkoutDao.class, ExerciseDao.class, WorkoutLogDao.class, SetLogDao.class)
                        .newInstance(workoutDao, exerciseDao, workoutLogDao, setLogDao);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
