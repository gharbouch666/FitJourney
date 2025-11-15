package com.bis5.fitjourney.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.models.WorkoutLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutHistoryViewHolder> {

    public interface OnLogClickListener {
        void onLogClick(WorkoutLog workoutLog);
    }

    private List<WorkoutLog> workoutLogs = new ArrayList<>();
    private final OnLogClickListener onLogClickListener;

    public WorkoutHistoryAdapter(OnLogClickListener onLogClickListener) {
        this.onLogClickListener = onLogClickListener;
    }

    @NonNull
    @Override
    public WorkoutHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_history, parent, false);
        return new WorkoutHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutHistoryViewHolder holder, int position) {
        WorkoutLog log = workoutLogs.get(position);
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        holder.historyDate.setText(sdf.format(new Date(log.getDateStarted())));

        long finishTime = (log.getDateFinished() != null) ? log.getDateFinished() : log.getDateStarted();
        long durationMillis = finishTime - log.getDateStarted();
        long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        holder.historyDuration.setText("Duration: " + durationMinutes + " min");

        holder.itemView.setOnClickListener(v -> onLogClickListener.onLogClick(log));
    }

    @Override
    public int getItemCount() {
        return workoutLogs == null ? 0 : workoutLogs.size();
    }

    public void updateLogs(List<WorkoutLog> newLogs) {
        this.workoutLogs = newLogs;
        notifyDataSetChanged();
    }

    public static class WorkoutHistoryViewHolder extends RecyclerView.ViewHolder {
        final TextView historyDate;
        final TextView historyDuration;

        public WorkoutHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            historyDate = itemView.findViewById(R.id.tvHistoryDate);
            historyDuration = itemView.findViewById(R.id.tvHistoryDuration);
        }
    }
}
