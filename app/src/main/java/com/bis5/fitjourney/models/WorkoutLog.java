package com.bis5.fitjourney.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.UUID;

@Entity(tableName = "workout_logs")
public class WorkoutLog {

    @PrimaryKey
    @NonNull
    private String id;

    private String workoutId;
    private String userEmail;
    private long dateStarted;
    private Long dateFinished;
    private String status;

    public WorkoutLog(@NonNull String id, String workoutId, String userEmail, long dateStarted, Long dateFinished, String status) {
        this.id = id;
        this.workoutId = workoutId;
        this.userEmail = userEmail;
        this.dateStarted = dateStarted;
        this.dateFinished = dateFinished;
        this.status = status;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getWorkoutId() { return workoutId; }
    public String getUserEmail() { return userEmail; }
    public long getDateStarted() { return dateStarted; }
    public Long getDateFinished() { return dateFinished; }
    public String getStatus() { return status; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setWorkoutId(String workoutId) { this.workoutId = workoutId; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setDateStarted(long dateStarted) { this.dateStarted = dateStarted; }
    public void setDateFinished(Long dateFinished) { this.dateFinished = dateFinished; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkoutLog that = (WorkoutLog) o;
        return dateStarted == that.dateStarted &&
                id.equals(that.id) &&
                Objects.equals(workoutId, that.workoutId) &&
                Objects.equals(userEmail, that.userEmail) &&
                Objects.equals(dateFinished, that.dateFinished) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workoutId, userEmail, dateStarted, dateFinished, status);
    }
}
