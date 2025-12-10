package com.bis5.fitjourney.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
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

    private long workoutId; // This must be a long.

    private String name;
    private int sets;
    private int reps;
    private double weight;

    public Exercise(long workoutId, String name, int sets, int reps, double weight) {
        this.workoutId = workoutId;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }

    @Ignore
    public Exercise(long workoutId, String name, int sets, int reps) {
        this(workoutId, name, sets, reps, 0.0);
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public long getWorkoutId() { return workoutId; }
    public void setWorkoutId(long workoutId) { this.workoutId = workoutId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }
    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return id == exercise.id && workoutId == exercise.workoutId && sets == exercise.sets && reps == exercise.reps && Double.compare(exercise.weight, weight) == 0 && Objects.equals(name, exercise.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workoutId, name, sets, reps, weight);
    }
}