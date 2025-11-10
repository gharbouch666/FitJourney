package com.bis5.fitjourney.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bis5.fitjourney.R
import com.bis5.fitjourney.databinding.ItemWorkoutBinding
import com.bis5.fitjourney.models.Workout

class WorkoutListAdapter(
    private val onItemClicked: (Workout) -> Unit,
    private val onLongItemClicked: (Workout) -> Unit
) : ListAdapter<Workout, WorkoutListAdapter.WorkoutViewHolder>(WorkoutDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val currentWorkout = getItem(position)
        holder.bind(currentWorkout)
        holder.itemView.setOnClickListener {
            onItemClicked(currentWorkout)
        }
        holder.itemView.setOnLongClickListener {
            onLongItemClicked(currentWorkout)
            true // Consume the long click
        }
    }

    class WorkoutViewHolder(private val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workout: Workout) {
            binding.tvWorkoutName.text = workout.name

            val iconRes = when (workout.name.lowercase()) {
                "push day" -> R.drawable.push
                "pull day" -> R.drawable.pull
                "leg day" -> R.drawable.legs
                "upper body" -> R.drawable.upper
                "lower body" -> R.drawable.lower
                "core" -> R.drawable.core
                "cardio" -> R.drawable.cardio
                else -> R.drawable.dumbell // Default icon
            }
            binding.ivWorkoutIcon.setImageResource(iconRes)
        }
    }

    object WorkoutDiffCallback : DiffUtil.ItemCallback<Workout>() {
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem == newItem
        }
    }
}
