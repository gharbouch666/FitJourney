package com.bis5.fitjourney.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bis5.fitjourney.models.ExerciseDao
import com.bis5.fitjourney.models.SetLogDao
import com.bis5.fitjourney.models.WorkoutDao
import com.bis5.fitjourney.models.WorkoutLogDao

class WorkoutViewModelFactory(private val workoutDao: WorkoutDao, private val exerciseDao: ExerciseDao, private val workoutLogDao: WorkoutLogDao, private val setLogDao: SetLogDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(workoutDao, exerciseDao, workoutLogDao, setLogDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
