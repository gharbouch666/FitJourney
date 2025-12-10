package com.bis5.fitjourney.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.models.Exercise;
import com.bis5.fitjourney.models.ExerciseDao;
import com.bis5.fitjourney.models.SetLog;
import com.bis5.fitjourney.models.SetLogDao;
import com.bis5.fitjourney.models.Workout;
import com.bis5.fitjourney.models.WorkoutDao;
import com.bis5.fitjourney.models.WorkoutLog;
import com.bis5.fitjourney.models.WorkoutLogDao;
import com.bis5.fitjourney.other.WorkoutWithExercises;

import java.util.List;
import java.util.UUID;

public class WorkoutViewModel extends ViewModel {

    private final WorkoutDao workoutDao;
    private final ExerciseDao exerciseDao;
    private final WorkoutLogDao workoutLogDao;
    private final SetLogDao setLogDao;

    public final MutableLiveData<String> userEmailTrigger = new MutableLiveData<>();

    public final LiveData<WorkoutWithExercises> mostRecentWorkout;
    public final LiveData<List<Workout>> userWorkouts;

    public WorkoutViewModel(WorkoutDao workoutDao, ExerciseDao exerciseDao, WorkoutLogDao workoutLogDao, SetLogDao setLogDao) {
        this.workoutDao = workoutDao;
        this.exerciseDao = exerciseDao;
        this.workoutLogDao = workoutLogDao;
        this.setLogDao = setLogDao;

        mostRecentWorkout = Transformations.switchMap(userEmailTrigger, workoutDao::getMostRecentWorkoutWithExercises);
        userWorkouts = Transformations.switchMap(userEmailTrigger, workoutDao::getWorkoutsForUser);
    }

    public void loadUser(String userEmail) {
        if (userEmail != null && !userEmail.equals(userEmailTrigger.getValue())) {
            userEmailTrigger.setValue(userEmail);
        }
    }

    public LiveData<Workout> getWorkout(long workoutId) {
        return workoutDao.getWorkout(workoutId);
    }

    public LiveData<List<Exercise>> getExercises(long workoutId) {
        return exerciseDao.getExercisesForWorkout(workoutId);
    }

    public LiveData<List<SetLog>> getLoggedSets(String workoutLogId) {
        return setLogDao.getLoggedSetsForWorkout(workoutLogId);
    }

    public void createWorkout(String name, String userEmail) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Workout newWorkout = new Workout(name, name, System.currentTimeMillis(), 0, userEmail);
            workoutDao.insert(newWorkout);
        });
    }

    public void updateWorkoutName(long workoutId, String newName) {
        AppDatabase.databaseWriteExecutor.execute(() -> workoutDao.updateWorkoutName(workoutId, newName));
    }

    public void deleteWorkout(Workout workout) {
        AppDatabase.databaseWriteExecutor.execute(() -> workoutDao.delete(workout));
    }

    public void addExercise(long workoutId, String name, int sets, int reps) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Exercise newExercise = new Exercise(workoutId, name, sets, reps);
            exerciseDao.insert(newExercise);
        });
    }

    public LiveData<String> startWorkout(long workoutId) {
        MutableLiveData<String> workoutLogIdLiveData = new MutableLiveData<>();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            String userEmail = workoutDao.getUserEmailForWorkout(workoutId);
            if (userEmail == null) userEmail = "";

            WorkoutLog workoutLog = new WorkoutLog(UUID.randomUUID().toString(), workoutId, userEmail, System.currentTimeMillis(), null, "in-progress");
            workoutLogDao.insert(workoutLog);
            workoutLogIdLiveData.postValue(workoutLog.getId());
        });
        return workoutLogIdLiveData;
    }

    public LiveData<List<WorkoutLog>> getWorkoutHistory(long workoutId) {
        return workoutLogDao.getLogsForWorkout(workoutId);
    }

    public LiveData<List<WorkoutLog>> getAllLogsForUser(String userEmail) {
        return workoutLogDao.getAllLogsForUser(userEmail);
    }

    public LiveData<List<SetLog>> getSetHistoryForExercise(String userEmail, String exerciseName) {
        return setLogDao.getSetHistoryForExercise(userEmail, exerciseName);
    }

    public LiveData<List<String>> getUniqueExerciseNames(String userEmail) {
        return setLogDao.getUniqueLoggedExerciseNames(userEmail);
    }

    public void logSet(String workoutLogId, String exerciseName, int setNumber, int reps, double weight) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            SetLog setLog = new SetLog(UUID.randomUUID().toString(), workoutLogId, exerciseName, setNumber, reps, weight, System.currentTimeMillis(), "completed");
            setLogDao.insert(setLog);
        });
    }

    public void finishWorkout(String logId) {
        AppDatabase.databaseWriteExecutor.execute(() -> workoutLogDao.finishWorkout(logId, System.currentTimeMillis()));
    }
}
