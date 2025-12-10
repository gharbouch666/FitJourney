package com.bis5.fitjourney.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.models.Exercise;
import com.bis5.fitjourney.models.ExerciseDao;
import com.bis5.fitjourney.models.User;
import com.bis5.fitjourney.models.UserDao;
import com.bis5.fitjourney.models.Workout;
import com.bis5.fitjourney.models.WorkoutDao;

public class AuthViewModel extends AndroidViewModel {

    private final UserDao userDao;
    private final WorkoutDao workoutDao;
    private final ExerciseDao exerciseDao;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        workoutDao = db.workoutDao();
        exerciseDao = db.exerciseDao();
    }

    public LiveData<User> login(String email, String password) {
        return userDao.login(email, password);
    }

    public void register(String name, String email, String password) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Inscribe the Citizen
            User newUser = new User(name, email, password);
            userDao.insert(newUser);

            // Forge the Arsenal with the ONE TRUE PRAYER
            long workoutAId = workoutDao.insert(new Workout("Full Body A", "Strength", System.currentTimeMillis(), 0, email));
            long workoutBId = workoutDao.insert(new Workout("Full Body B", "Strength", System.currentTimeMillis(), 0, email));
            long coreId = workoutDao.insert(new Workout("Core Focus", "Core", System.currentTimeMillis(), 0, email));

            // Stock the Armory
            exerciseDao.insert(new Exercise(workoutAId, "Squat", 3, 8));
            exerciseDao.insert(new Exercise(workoutAId, "Bench Press", 3, 8));
            exerciseDao.insert(new Exercise(workoutAId, "Barbell Row", 3, 8));
            exerciseDao.insert(new Exercise(workoutBId, "Deadlift", 1, 5));
            exerciseDao.insert(new Exercise(workoutBId, "Overhead Press", 3, 8));
            exerciseDao.insert(new Exercise(workoutBId, "Pull Up", 3, 8));
            exerciseDao.insert(new Exercise(coreId, "Plank", 3, 60));
            exerciseDao.insert(new Exercise(coreId, "Leg Raises", 3, 15));
            exerciseDao.insert(new Exercise(coreId, "Bird Dog", 3, 12));
        });
    }

    public LiveData<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
}