package com.bis5.fitjourney.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bis5.fitjourney.R
import com.bis5.fitjourney.models.SetLog

class LoggedSetAdapter(
    private var loggedSets: List<SetLog> = emptyList()
) : RecyclerView.Adapter<LoggedSetAdapter.LoggedSetViewHolder>() {

    class LoggedSetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.tvLoggedExerciseName)
        val setInfo: TextView = itemView.findViewById(R.id.tvLoggedSetInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoggedSetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_logged_set, parent, false)
        return LoggedSetViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoggedSetViewHolder, position: Int) {
        val setLog = loggedSets[position]
        holder.exerciseName.text = setLog.exerciseName
        holder.setInfo.text = "Set ${setLog.setNumber}: ${setLog.weight} kg x ${setLog.reps}"
    }

    override fun getItemCount() = loggedSets.size

    fun updateLoggedSets(newLoggedSets: List<SetLog>) {
        loggedSets = newLoggedSets
        notifyDataSetChanged()
    }
}
