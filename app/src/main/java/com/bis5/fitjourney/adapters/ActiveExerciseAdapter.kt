package com.bis5.fitjourney.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bis5.fitjourney.R
import com.bis5.fitjourney.models.Exercise

class ActiveExerciseAdapter(
    private var exercises: List<Exercise> = emptyList(),
    private val onLogSet: (exerciseName: String, setNumber: Int, weight: Double, reps: Int) -> Unit
) : RecyclerView.Adapter<ActiveExerciseAdapter.ActiveExerciseViewHolder>() {

    inner class ActiveExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        val setsRecyclerView: RecyclerView = itemView.findViewById(R.id.rvSets)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_active_exercise, parent, false)
        return ActiveExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActiveExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name

        val setAdapter = ActiveSetAdapter(exercise) { setNumber, weight, reps ->
            onLogSet(exercise.name, setNumber, weight, reps)
        }
        holder.setsRecyclerView.adapter = setAdapter
    }

    override fun getItemCount() = exercises.size

    fun updateExercises(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }
}
