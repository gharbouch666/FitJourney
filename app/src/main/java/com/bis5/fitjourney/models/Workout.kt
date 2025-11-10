package com.bis5.fitjourney.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val workoutType: String, // Added this field
    val date: Long,
    val duration: Int, // Duration in minutes
    val userEmail: String
)
