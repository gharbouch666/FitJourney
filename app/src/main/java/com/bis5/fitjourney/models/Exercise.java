package com.bis5.fitjourney.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
    tableName = "exercises",
    foreignKeys = {
        @ForeignKey(
            entity = Workout.class,
            parentColumns = {"id"},
            childColumns = {"workoutId"},
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index(value = {"workoutId"})}
)
public class Exercise {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String workoutId;

    private String name;
    private int sets;
    private int reps;
    private double weight;

    public Exercise(@NonNull String workoutId, String name, int sets, int reps, double weight) {
        this.workoutId = workoutId;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    // Getters
    public int getId() { return id; }
    @NonNull
    public String getWorkoutId() { return workoutId; }
    public String getName() { return name; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public double getWeight() { return weight; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setWorkoutId(@NonNull String workoutId) { this.workoutId = workoutId; }
    public void setName(String name) { this.name = name; }
    public void setSets(int sets) { this.sets = sets; }
    public void setReps(int reps) { this.reps = reps; }
    public void setWeight(double weight) { this.weight = weight; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return id == exercise.id &&
                sets == exercise.sets &&
                reps == exercise.reps &&
                Double.compare(exercise.weight, weight) == 0 &&
                workoutId.equals(exercise.workoutId) &&
                Objects.equals(name, exercise.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workoutId, name, sets, reps, weight);
    }
}
