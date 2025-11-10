package com.bis5.fitjourney.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bis5.fitjourney.models.Exercise
import com.bis5.fitjourney.models.ExerciseDao
import com.bis5.fitjourney.models.SetLog
import com.bis5.fitjourney.models.SetLogDao
import com.bis5.fitjourney.models.Workout
import com.bis5.fitjourney.models.WorkoutDao
import com.bis5.fitjourney.models.WorkoutLog
import com.bis5.fitjourney.models.WorkoutLogDao
import kotlinx.coroutines.launch

class WorkoutViewModel(private val workoutDao: WorkoutDao, private val exerciseDao: ExerciseDao, private val workoutLogDao: WorkoutLogDao, private val setLogDao: SetLogDao) : ViewModel() {

    fun getWorkouts(userEmail: String): LiveData<List<Workout>> {
        return workoutDao.getWorkoutsForUser(userEmail)
    }

    fun getWorkout(workoutId: String): LiveData<Workout?> {
        return workoutDao.getWorkout(workoutId)
    }

    fun getExercises(workoutId: String): LiveData<List<Exercise>> {
        return exerciseDao.getExercisesForWorkout(workoutId).asLiveData()
    }

    fun getLoggedSets(workoutLogId: String): LiveData<List<SetLog>> {
        return setLogDao.getLoggedSetsForWorkout(workoutLogId)
    }

    fun createDefaultWorkoutsIfNoneExist(userEmail: String) {
        viewModelScope.launch {
            val workoutCount = workoutDao.getWorkoutCountForUser(userEmail)
            if (workoutCount == 0) {
                val defaultWorkouts = listOf(
                    Workout(name = "Push Day", userEmail = userEmail, date = System.currentTimeMillis(), workoutType = "Push Day", duration = 0),
                    Workout(name = "Pull Day", userEmail = userEmail, date = System.currentTimeMillis(), workoutType = "Pull Day", duration = 0),
                    Workout(name = "Leg Day", userEmail = userEmail, date = System.currentTimeMillis(), workoutType = "Leg Day", duration = 0),
                    Workout(name = "Upper Body", userEmail = userEmail, date = System.currentTimeMillis(), workoutType = "Upper Body", duration = 0),
                    Workout(name = "Lower Body", userEmail = userEmail, date = System.currentTimeMillis(), workoutType = "Lower Body", duration = 0),
                    Workout(name = "Core", userEmail = userEmail, date = System.currentTimeMillis(), workoutType = "Core", duration = 0),
                    Workout(name = "Cardio", userEmail = userEmail, date = System.currentTimeMillis(), workoutType = "Cardio", duration = 0)
                )
                defaultWorkouts.forEach { workoutDao.insertWorkout(it) }
            }
        }
    }

    fun createWorkout(name: String, userEmail: String) {
        viewModelScope.launch {
            val newWorkout = Workout(
                name = name, 
                userEmail = userEmail, 
                date = System.currentTimeMillis(),
                workoutType = name,
                duration = 0
            )
            workoutDao.insertWorkout(newWorkout)
        }
    }

    fun updateWorkoutName(workoutId: String, newName: String) {
        viewModelScope.launch {
            workoutDao.updateWorkoutName(workoutId, newName)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.deleteWorkout(workout)
        }
    }

    fun addExercise(workoutId: String, name: String, sets: Int, reps: Int, weight: Double) {
        viewModelScope.launch {
            val newExercise = Exercise(workoutId = workoutId, name = name, sets = sets, reps = reps, weight = weight)
            exerciseDao.insertExercise(newExercise)
        }
    }

    fun startWorkout(workoutId: String): LiveData<String> = liveData {
        val userEmail = workoutDao.getUserEmailForWorkout(workoutId) ?: ""
        val workoutLog = WorkoutLog(workoutId = workoutId, userEmail = userEmail, dateStarted = System.currentTimeMillis())
        val workoutLogId = workoutLogDao.startWorkout(workoutLog)
        emit(workoutLogId.toString())
    }

    fun getWorkoutHistory(workoutId: String): LiveData<List<WorkoutLog>> {
        return workoutLogDao.getLogsForWorkout(workoutId)
    }

    fun getAllLogsForUser(userEmail: String): LiveData<List<WorkoutLog>> {
        return workoutLogDao.getAllLogsForUser(userEmail)
    }

    fun getSetHistoryForExercise(userEmail: String, exerciseName: String): LiveData<List<SetLog>> {
        return setLogDao.getSetHistoryForExercise(userEmail, exerciseName)
    }

    fun getUniqueExerciseNames(userEmail: String): LiveData<List<String>> {
        return setLogDao.getUniqueLoggedExerciseNames(userEmail)
    }

    fun logSet(workoutLogId: String, exerciseName: String, setNumber: Int, reps: Int, weight: Double) {
        viewModelScope.launch {
            val setLog = SetLog(
                workoutLogId = workoutLogId,
                exerciseName = exerciseName,
                setNumber = setNumber,
                reps = reps,
                weight = weight,
                status = "completed"
            )
            setLogDao.logSet(setLog)
        }
    }

    fun finishWorkout(logId: String) {
        viewModelScope.launch {
            workoutLogDao.finishWorkout(logId, System.currentTimeMillis())
        }
    }
}
