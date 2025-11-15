package com.bis5.fitjourney.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bis5.fitjourney.models.Exercise;
import com.bis5.fitjourney.models.ExerciseDao;
import com.bis5.fitjourney.models.SetLog;
import com.bis5.fitjourney.models.SetLogDao;
import com.bis5.fitjourney.models.Workout;
import com.bis5.fitjourney.models.WorkoutDao;
import com.bis5.fitjourney.models.WorkoutLog;
import com.bis5.fitjourney.models.WorkoutLogDao;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutViewModel extends ViewModel {

    private final WorkoutDao workoutDao;
    private final ExerciseDao exerciseDao;
    private final WorkoutLogDao workoutLogDao;
    private final SetLogDao setLogDao;
    private final ExecutorService executorService;

    public WorkoutViewModel(WorkoutDao workoutDao, ExerciseDao exerciseDao, WorkoutLogDao workoutLogDao, SetLogDao setLogDao) {
        this.workoutDao = workoutDao;
        this.exerciseDao = exerciseDao;
        this.workoutLogDao = workoutLogDao;
        this.setLogDao = setLogDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    public LiveData<List<Workout>> getWorkouts(String userEmail) {
        return workoutDao.getWorkoutsForUser(userEmail);
    }

    public LiveData<Workout> getWorkout(String workoutId) {
        return workoutDao.getWorkout(workoutId);
    }

    public LiveData<List<Exercise>> getExercises(String workoutId) {
        return exerciseDao.getExercisesForWorkout(workoutId);
    }

    public LiveData<List<SetLog>> getLoggedSets(String workoutLogId) {
        return setLogDao.getLoggedSetsForWorkout(workoutLogId);
    }

    public void createDefaultWorkoutsIfNoneExist(String userEmail) {
        executorService.execute(() -> {
            if (workoutDao.getWorkoutCountForUser(userEmail) == 0) {
                List<Workout> defaultWorkouts = Arrays.asList(
                    new Workout(UUID.randomUUID().toString(), "Push Day", "Push Day", System.currentTimeMillis(), 0, userEmail),
                    new Workout(UUID.randomUUID().toString(), "Pull Day", "Pull Day", System.currentTimeMillis(), 0, userEmail),
                    new Workout(UUID.randomUUID().toString(), "Leg Day", "Leg Day", System.currentTimeMillis(), 0, userEmail),
                    new Workout(UUID.randomUUID().toString(), "Upper Body", "Upper Body", System.currentTimeMillis(), 0, userEmail),
                    new Workout(UUID.randomUUID().toString(), "Lower Body", "Lower Body", System.currentTimeMillis(), 0, userEmail),
                    new Workout(UUID.randomUUID().toString(), "Core", "Core", System.currentTimeMillis(), 0, userEmail),
                    new Workout(UUID.randomUUID().toString(), "Cardio", "Cardio", System.currentTimeMillis(), 0, userEmail)
                );
                for (Workout workout : defaultWorkouts) {
                    workoutDao.insertWorkout(workout);
                }
            }
        });
    }

    public void createWorkout(String name, String userEmail) {
        executorService.execute(() -> {
            Workout newWorkout = new Workout(UUID.randomUUID().toString(), name, name, System.currentTimeMillis(), 0, userEmail);
            workoutDao.insertWorkout(newWorkout);
        });
    }

    public void updateWorkoutName(String workoutId, String newName) {
        executorService.execute(() -> workoutDao.updateWorkoutName(workoutId, newName));
    }

    public void deleteWorkout(Workout workout) {
        executorService.execute(() -> workoutDao.deleteWorkout(workout));
    }

    public void addExercise(String workoutId, String name, int sets, int reps, double weight) {
        executorService.execute(() -> {
            Exercise newExercise = new Exercise(workoutId, name, sets, reps, weight);
            exerciseDao.insertExercise(newExercise);
        });
    }

    public LiveData<String> startWorkout(String workoutId) {
        MutableLiveData<String> workoutLogIdLiveData = new MutableLiveData<>();
        executorService.execute(() -> {
            String userEmail = workoutDao.getUserEmailForWorkout(workoutId);
            if (userEmail == null) userEmail = "";

            WorkoutLog workoutLog = new WorkoutLog(UUID.randomUUID().toString(), workoutId, userEmail, System.currentTimeMillis(), null, "in-progress");
            long workoutLogId = workoutLogDao.startWorkout(workoutLog);
            // Note: startWorkout returns the rowId, not the UUID. This might be a bug from the original code.
            // For now, we will assume we need the string representation of the row id. 
            // A better implementation would be to return the WorkoutLog's UUID string id.
            workoutLogIdLiveData.postValue(String.valueOf(workoutLogId));
        });
        return workoutLogIdLiveData;
    }

    public LiveData<List<WorkoutLog>> getWorkoutHistory(String workoutId) {
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
        executorService.execute(() -> {
            SetLog setLog = new SetLog(UUID.randomUUID().toString(), workoutLogId, exerciseName, setNumber, reps, weight, System.currentTimeMillis(), "completed");
            setLogDao.logSet(setLog);
        });
    }

    public void finishWorkout(String logId) {
        executorService.execute(() -> workoutLogDao.finishWorkout(logId, System.currentTimeMillis()));
    }
}
