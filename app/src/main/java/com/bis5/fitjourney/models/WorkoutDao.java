package com.bis5.fitjourney.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import com.bis5.fitjourney.other.WorkoutWithExercises;
import java.util.List;

@Dao
public interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Workout workout); // Must return long

    @Query("SELECT * FROM workouts WHERE userEmail = :userEmail ORDER BY date DESC")
    LiveData<List<Workout>> getWorkoutsForUser(String userEmail);

    @Query("SELECT COUNT(*) FROM workouts WHERE userEmail = :userEmail")
    int getWorkoutCountForUser(String userEmail);

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    LiveData<Workout> getWorkout(long workoutId); // Must take long

    @Transaction
    @Query("SELECT * FROM workouts WHERE id IN (SELECT workoutId FROM workout_logs WHERE userEmail = :userEmail AND dateFinished IS NOT NULL ORDER BY dateFinished DESC LIMIT 1)")
    LiveData<WorkoutWithExercises> getMostRecentWorkoutWithExercises(String userEmail);

    @Query("SELECT userEmail FROM workouts WHERE id = :workoutId")
    String getUserEmailForWorkout(long workoutId); // Must take long

    @Query("UPDATE workouts SET name = :newName WHERE id = :workoutId")
    void updateWorkoutName(long workoutId, String newName); // Must take long

    @Delete
    void delete(Workout workout);
}