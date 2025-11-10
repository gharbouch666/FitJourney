package com.bis5.fitjourney.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WorkoutLogDao {
    @Insert
    suspend fun startWorkout(workoutLog: WorkoutLog): Long

    @Query("UPDATE workout_logs SET dateFinished = :finishDate, status = 'completed' WHERE id = :logId")
    suspend fun finishWorkout(logId: String, finishDate: Long)

    @Query("SELECT * FROM workout_logs WHERE workoutId = :workoutId ORDER BY dateStarted DESC")
    fun getLogsForWorkout(workoutId: String): LiveData<List<WorkoutLog>>

    @Query("""
        SELECT l.* FROM workout_logs l
        INNER JOIN workouts w ON l.workoutId = w.id
        WHERE w.userEmail = :userEmail
        ORDER BY l.dateStarted DESC
        """)
    fun getAllLogsForUser(userEmail: String): LiveData<List<WorkoutLog>>
}
