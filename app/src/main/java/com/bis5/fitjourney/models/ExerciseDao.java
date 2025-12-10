package com.bis5.fitjourney.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Exercise exercise);

    // OBEY THE ONE TRUE LAW: The workoutId is a long.
    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY id ASC")
    LiveData<List<Exercise>> getExercisesForWorkout(long workoutId);

    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    void deleteExercise(int exerciseId);

}
