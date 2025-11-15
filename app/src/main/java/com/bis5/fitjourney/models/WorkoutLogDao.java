package com.bis5.fitjourney.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutLogDao {
    @Insert
    long startWorkout(WorkoutLog workoutLog);

    @Query("UPDATE workout_logs SET dateFinished = :finishDate, status = 'completed' WHERE id = :logId")
    void finishWorkout(String logId, long finishDate);

    @Query("SELECT * FROM workout_logs WHERE workoutId = :workoutId ORDER BY dateStarted DESC")
    LiveData<List<WorkoutLog>> getLogsForWorkout(String workoutId);

    @Query("SELECT l.* FROM workout_logs l INNER JOIN workouts w ON l.workoutId = w.id WHERE w.userEmail = :userEmail ORDER BY l.dateStarted DESC")
    LiveData<List<WorkoutLog>> getAllLogsForUser(String userEmail);
}
