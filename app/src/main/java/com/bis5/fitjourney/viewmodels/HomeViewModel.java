package com.bis5.fitjourney.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.models.FoodItemDao;
import com.bis5.fitjourney.models.SocialPostDao;
import com.bis5.fitjourney.other.WorkoutWithExercises;

public class HomeViewModel extends AndroidViewModel {

    private final FoodItemDao foodItemDao;
    private final SocialPostDao socialPostDao;
    private final WorkoutViewModel workoutViewModel;

    private final MutableLiveData<String> userEmailTrigger = new MutableLiveData<>();

    public final LiveData<WorkoutWithExercises> mostRecentWorkout;
    public final LiveData<Integer> todayCalories;
    public final LiveData<String> latestPostContent;
    private final MutableLiveData<Integer> todaySteps = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application, WorkoutViewModel workoutViewModel) {
        super(application);
        this.workoutViewModel = workoutViewModel;
        AppDatabase db = AppDatabase.getDatabase(application);
        this.foodItemDao = db.foodItemDao();
        this.socialPostDao = db.socialPostDao();

        this.mostRecentWorkout = workoutViewModel.mostRecentWorkout;

        this.todayCalories = Transformations.switchMap(userEmailTrigger, email ->
            foodItemDao.getCaloriesForToday()
        );

        this.latestPostContent = Transformations.switchMap(userEmailTrigger, email ->
            socialPostDao.getLatestPostContent()
        );

        // THE STEP ORACLE: Initialize with a placeholder value.
        todaySteps.setValue(8450);
    }

    public LiveData<Integer> getTodaySteps() {
        return todaySteps;
    }

    public void loadDashboardData(String userEmail) {
        if (userEmail != null) {
            workoutViewModel.loadUser(userEmail);
            if (!userEmail.equals(userEmailTrigger.getValue())) {
                userEmailTrigger.setValue(userEmail);
            }
        }
    }
}
