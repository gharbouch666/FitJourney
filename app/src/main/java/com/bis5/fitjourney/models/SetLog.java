package com.bis5.fitjourney.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.UUID;

@Entity(tableName = "set_logs")
public class SetLog {

    @PrimaryKey
    @NonNull
    private String id;

    private String workoutLogId;
    private String exerciseName;
    private int setNumber;
    private int reps;
    private double weight;
    private long timestamp;
    private String status;

    public SetLog(@NonNull String id, String workoutLogId, String exerciseName, int setNumber, int reps, double weight, long timestamp, String status) {
        this.id = id;
        this.workoutLogId = workoutLogId;
        this.exerciseName = exerciseName;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getWorkoutLogId() { return workoutLogId; }
    public String getExerciseName() { return exerciseName; }
    public int getSetNumber() { return setNumber; }
    public int getReps() { return reps; }
    public double getWeight() { return weight; }
    public long getTimestamp() { return timestamp; }
    public String getStatus() { return status; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setWorkoutLogId(String workoutLogId) { this.workoutLogId = workoutLogId; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public void setSetNumber(int setNumber) { this.setNumber = setNumber; }
    public void setReps(int reps) { this.reps = reps; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetLog setLog = (SetLog) o;
        return setNumber == setLog.setNumber &&
                reps == setLog.reps &&
                Double.compare(setLog.weight, weight) == 0 &&
                timestamp == setLog.timestamp &&
                id.equals(setLog.id) &&
                Objects.equals(workoutLogId, setLog.workoutLogId) &&
                Objects.equals(exerciseName, setLog.exerciseName) &&
                Objects.equals(status, setLog.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workoutLogId, exerciseName, setNumber, reps, weight, timestamp, status);
    }
}
