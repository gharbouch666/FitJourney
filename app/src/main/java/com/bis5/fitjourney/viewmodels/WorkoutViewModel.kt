package com.bis5.fitjourney.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bis5.fitjourney.models.Exercise
import com.bis5.fitjourney.models.ExerciseDao
import com.bis5.fitjourney.models.Workout
import com.bis5.fitjourney.models.WorkoutDao
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao
) : ViewModel() {

    // --- Workout Functions ---

    fun getWorkouts(userId: String): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsForUser(userId).asLiveData()
    }

    fun getWorkout(workoutId: String): LiveData<Workout> {
        return workoutDao.getWorkoutById(workoutId).asLiveData()
    }

    fun createWorkout(name: String, userId: String) {
        viewModelScope.launch {
            // Create a workout with 0 duration. Duration can be calculated later.
            val newWorkout = Workout(name = name, userId = userId, duration = 0)
            workoutDao.insert(newWorkout)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.delete(workout)
            // Associated exercises will be deleted automatically due to ForeignKey.CASCADE
        }
    }

    fun updateWorkoutName(workoutId: String, newName: String) {
        viewModelScope.launch {
            workoutDao.updateWorkoutName(workoutId, newName)
        }
    }

    // --- Exercise Functions ---

    fun getExercises(workoutId: String): LiveData<List<Exercise>> {
        return exerciseDao.getExercisesForWorkout(workoutId).asLiveData()
    }

    fun addExercise(workoutId: String, name: String, sets: Int, reps: Int, weight: Double) {
        viewModelScope.launch {
            val newExercise = Exercise(
                workoutId = workoutId,
                name = name,
                sets = sets,
                reps = reps,
                weight = weight
            )
            exerciseDao.insertExercise(newExercise)
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.deleteExercise(exercise.id)
        }
    }
}