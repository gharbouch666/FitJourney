package com.bis5.fitjourney.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.UUID;

@Entity(tableName = "workouts")
public class Workout {

    @PrimaryKey
    @NonNull
    private String id;

    private String name;
    private String workoutType;
    private long date;
    private int duration;
    private String userEmail;

    public Workout(@NonNull String id, String name, String workoutType, long date, int duration, String userEmail) {
        this.id = id;
        this.name = name;
        this.workoutType = workoutType;
        this.date = date;
        this.duration = duration;
        this.userEmail = userEmail;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getName() { return name; }
    public String getWorkoutType() { return workoutType; }
    public long getDate() { return date; }
    public int getDuration() { return duration; }
    public String getUserEmail() { return userEmail; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setWorkoutType(String workoutType) { this.workoutType = workoutType; }
    public void setDate(long date) { this.date = date; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workout workout = (Workout) o;
        return date == workout.date &&
                duration == workout.duration &&
                id.equals(workout.id) &&
                Objects.equals(name, workout.name) &&
                Objects.equals(workoutType, workout.workoutType) &&
                Objects.equals(userEmail, workout.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, workoutType, date, duration, userEmail);
    }
}
