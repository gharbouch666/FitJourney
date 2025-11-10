package com.bis5.fitjourney.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Query("SELECT * FROM workouts WHERE userEmail = :userEmail ORDER BY date DESC")
    fun getWorkoutsForUser(userEmail: String): LiveData<List<Workout>>

    @Query("SELECT COUNT(*) FROM workouts WHERE userEmail = :userEmail")
    suspend fun getWorkoutCountForUser(userEmail: String): Int

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getWorkout(workoutId: String): LiveData<Workout?>

    @Query("SELECT userEmail FROM workouts WHERE id = :workoutId")
    suspend fun getUserEmailForWorkout(workoutId: String): String?

    @Query("UPDATE workouts SET name = :newName WHERE id = :workoutId")
    suspend fun updateWorkoutName(workoutId: String, newName: String)

    @Delete
    suspend fun deleteWorkout(workout: Workout)
}
