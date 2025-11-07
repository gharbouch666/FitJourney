package com.bis5.fitjourney.models

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY date DESC")
    fun getWorkoutsForUser(userId: String): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getWorkoutById(workoutId: String): Flow<Workout>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout)

    @Delete
    suspend fun delete(workout: Workout)

    @Query("UPDATE workouts SET name = :newName WHERE id = :workoutId")
    suspend fun updateWorkoutName(workoutId: String, newName: String)

}