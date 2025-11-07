package com.bis5.fitjourney.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["workoutId"])] // Add this line for performance
)
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutId: String, // Foreign key to link to Workout
    val name: String,
    val sets: Int,
    val reps: Int,
    val weight: Double
)