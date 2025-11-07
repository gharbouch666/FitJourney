package com.bis5.fitjourney.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val date: Long = System.currentTimeMillis(),
    val duration: Int = 0, // Duration in minutes
    val userId: String // Foreign key to link to a user
)

data class WorkoutWithExercises(
    val workout: Workout,
    val exercises: List<Exercise>
)
