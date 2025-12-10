package com.bis5.fitjourney.other;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.bis5.fitjourney.models.Exercise;
import com.bis5.fitjourney.models.Workout;

import java.util.List;

public class WorkoutWithExercises {
    @Embedded
    public Workout workout;

    @Relation(
            parentColumn = "id",
            entityColumn = "workoutId"
    )
    public List<Exercise> exercises;
}
