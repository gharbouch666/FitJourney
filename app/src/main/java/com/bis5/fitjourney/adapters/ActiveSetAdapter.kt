package com.bis5.fitjourney.adapters

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bis5.fitjourney.R
import com.bis5.fitjourney.models.Exercise
import com.google.android.material.textfield.TextInputLayout

class ActiveSetAdapter(
    private val exercise: Exercise,
    private val onLogSet: (setNumber: Int, weight: Double, reps: Int) -> Unit
) : RecyclerView.Adapter<ActiveSetAdapter.ActiveSetViewHolder>() {

    private val loggedSets = mutableSetOf<Int>()
    private var timer: CountDownTimer? = null
    private var restingSetPosition: Int = -1

    inner class ActiveSetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val setNumber: TextView = itemView.findViewById(R.id.tvSetNumber)
        val weightLayout: TextInputLayout = itemView.findViewById(R.id.tilWeight)
        val repsLayout: TextInputLayout = itemView.findViewById(R.id.tilReps)
        val weight: EditText = itemView.findViewById(R.id.etWeight)
        val reps: EditText = itemView.findViewById(R.id.etReps)
        val logSetButton: ImageButton = itemView.findViewById(R.id.btnLogSet)
        val restTimer: TextView = itemView.findViewById(R.id.tvRestTimer)
        val skipRestButton: Button = itemView.findViewById(R.id.btnSkipRest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveSetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_active_set, parent, false)
        return ActiveSetViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActiveSetViewHolder, position: Int) {
        val setIndex = position + 1
        holder.setNumber.text = "Set $setIndex"

        when {
            restingSetPosition == position -> {
                // This set is currently in a rest period
                holder.weightLayout.visibility = View.GONE
                holder.repsLayout.visibility = View.GONE
                holder.logSetButton.visibility = View.GONE
                holder.restTimer.visibility = View.VISIBLE
                holder.skipRestButton.visibility = View.VISIBLE

                holder.skipRestButton.setOnClickListener {
                    timer?.cancel()
                    restingSetPosition = -1
                    notifyItemChanged(position)
                }
            }
            loggedSets.contains(setIndex) -> {
                // This set has been logged
                holder.weightLayout.visibility = View.VISIBLE
                holder.repsLayout.visibility = View.VISIBLE
                holder.weight.isEnabled = false
                holder.reps.isEnabled = false
                holder.logSetButton.isClickable = false
                holder.logSetButton.visibility = View.VISIBLE
                holder.logSetButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark))
                holder.restTimer.visibility = View.GONE
                holder.skipRestButton.visibility = View.GONE
            }
            else -> {
                // This set is ready to be logged
                holder.weightLayout.visibility = View.VISIBLE
                holder.repsLayout.visibility = View.VISIBLE
                holder.weight.isEnabled = true
                holder.reps.isEnabled = true
                holder.weight.setText(exercise.weight.toString())
                holder.reps.setText(exercise.reps.toString())
                holder.logSetButton.isClickable = true
                holder.logSetButton.visibility = View.VISIBLE
                holder.logSetButton.clearColorFilter()
                holder.restTimer.visibility = View.GONE
                holder.skipRestButton.visibility = View.GONE

                holder.logSetButton.setOnClickListener {
                    val weightValue = holder.weight.text.toString().toDoubleOrNull() ?: 0.0
                    val repsValue = holder.reps.text.toString().toIntOrNull() ?: 0
                    onLogSet(setIndex, weightValue, repsValue)

                    loggedSets.add(setIndex)
                    startRestTimer(holder, position)
                }
            }
        }
    }

    private fun startRestTimer(holder: ActiveSetViewHolder, position: Int) {
        restingSetPosition = position
        notifyItemChanged(position) // Update UI to show timer

        timer = object : CountDownTimer(60000, 1000) { // 60-second timer
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                holder.restTimer.text = String.format("%02d:%02d", seconds / 60, seconds % 60)
            }

            override fun onFinish() {
                restingSetPosition = -1
                notifyItemChanged(position) // Update UI to hide timer
            }
        }.start()
    }

    override fun getItemCount(): Int {
        return exercise.sets
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        timer?.cancel() // Prevent memory leaks
    }
}