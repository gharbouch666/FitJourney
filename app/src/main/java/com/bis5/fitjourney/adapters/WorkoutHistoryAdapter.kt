package com.bis5.fitjourney.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bis5.fitjourney.R
import com.bis5.fitjourney.models.WorkoutLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class WorkoutHistoryAdapter(
    private var workoutLogs: List<WorkoutLog> = emptyList(),
    private val onLogClick: (WorkoutLog) -> Unit
) : RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutHistoryViewHolder>() {

    class WorkoutHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val historyDate: TextView = itemView.findViewById(R.id.tvHistoryDate)
        val historyDuration: TextView = itemView.findViewById(R.id.tvHistoryDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_history, parent, false)
        return WorkoutHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutHistoryViewHolder, position: Int) {
        val log = workoutLogs[position]
        holder.historyDate.text = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(log.dateStarted))

        val durationMillis = (log.dateFinished ?: log.dateStarted) - log.dateStarted
        val durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
        holder.historyDuration.text = "Duration: $durationMinutes min"

        holder.itemView.setOnClickListener {
            onLogClick(log)
        }
    }

    override fun getItemCount() = workoutLogs.size

    fun updateLogs(newLogs: List<WorkoutLog>) {
        workoutLogs = newLogs
        notifyDataSetChanged()
    }
}