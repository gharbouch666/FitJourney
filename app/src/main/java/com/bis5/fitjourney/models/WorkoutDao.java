package com.bis5.fitjourney.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertWorkout(Workout workout);

    @Query("SELECT * FROM workouts WHERE userEmail = :userEmail ORDER BY date DESC")
    LiveData<List<Workout>> getWorkoutsForUser(String userEmail);

    @Query("SELECT COUNT(*) FROM workouts WHERE userEmail = :userEmail")
    int getWorkoutCountForUser(String userEmail);

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    LiveData<Workout> getWorkout(String workoutId);

    @Query("SELECT userEmail FROM workouts WHERE id = :workoutId")
    String getUserEmailForWorkout(String workoutId);

    @Query("UPDATE workouts SET name = :newName WHERE id = :workoutId")
    void updateWorkoutName(String workoutId, String newName);

    @Delete
    void deleteWorkout(Workout workout);
}
