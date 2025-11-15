package com.bis5.fitjourney.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SetLogDao {
    @Insert
    void logSet(SetLog setLog);

    @Query("SELECT * FROM set_logs WHERE workoutLogId = :workoutLogId ORDER BY setNumber ASC")
    LiveData<List<SetLog>> getLoggedSetsForWorkout(String workoutLogId);

    @Query("SELECT sl.* FROM set_logs sl INNER JOIN workout_logs wl ON sl.workoutLogId = wl.id WHERE wl.userEmail = :userEmail AND sl.exerciseName = :exerciseName ORDER BY wl.dateStarted ASC")
    LiveData<List<SetLog>> getSetHistoryForExercise(String userEmail, String exerciseName);

    @Query("SELECT DISTINCT sl.exerciseName FROM set_logs sl INNER JOIN workout_logs wl ON sl.workoutLogId = wl.id WHERE wl.userEmail = :userEmail ORDER BY sl.exerciseName ASC")
    LiveData<List<String>> getUniqueLoggedExerciseNames(String userEmail);
}
