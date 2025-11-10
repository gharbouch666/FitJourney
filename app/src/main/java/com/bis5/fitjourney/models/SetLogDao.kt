package com.bis5.fitjourney.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SetLogDao {
    @Insert
    suspend fun logSet(setLog: SetLog)

    @Query("SELECT * FROM set_logs WHERE workoutLogId = :workoutLogId ORDER BY setNumber ASC")
    fun getLoggedSetsForWorkout(workoutLogId: String): LiveData<List<SetLog>>

    @Query("""
        SELECT sl.* FROM set_logs sl
        INNER JOIN workout_logs wl ON sl.workoutLogId = wl.id
        WHERE wl.userEmail = :userEmail AND sl.exerciseName = :exerciseName
        ORDER BY wl.dateStarted ASC
    """)
    fun getSetHistoryForExercise(userEmail: String, exerciseName: String): LiveData<List<SetLog>>

    @Query("""
        SELECT DISTINCT sl.exerciseName FROM set_logs sl
        INNER JOIN workout_logs wl ON sl.workoutLogId = wl.id
        WHERE wl.userEmail = :userEmail
        ORDER BY sl.exerciseName ASC
    """)
    fun getUniqueLoggedExerciseNames(userEmail: String): LiveData<List<String>>
}
