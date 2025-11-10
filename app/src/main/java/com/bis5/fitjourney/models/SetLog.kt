package com.bis5.fitjourney.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "set_logs")
data class SetLog(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val workoutLogId: String, // Foreign key to the workout log session
    val exerciseName: String,
    val setNumber: Int,
    val reps: Int,
    val weight: Double,
    val timestamp: Long = System.currentTimeMillis(), // Added this field
    var status: String // e.g., "completed", "skipped"
)
