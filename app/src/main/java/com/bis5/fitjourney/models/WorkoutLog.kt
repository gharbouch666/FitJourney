package com.bis5.fitjourney.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val workoutId: String, // Foreign key to the Workout template
    val userEmail: String, // Added this field
    val dateStarted: Long,
    var dateFinished: Long? = null,
    var status: String = "in-progress" // e.g., "in-progress", "completed"
)
