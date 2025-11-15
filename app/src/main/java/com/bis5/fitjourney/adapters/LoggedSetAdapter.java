package com.bis5.fitjourney.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.models.SetLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoggedSetAdapter extends RecyclerView.Adapter<LoggedSetAdapter.LoggedSetViewHolder> {

    private List<SetLog> loggedSets = new ArrayList<>();

    @NonNull
    @Override
    public LoggedSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_logged_set, parent, false);
        return new LoggedSetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoggedSetViewHolder holder, int position) {
        SetLog setLog = loggedSets.get(position);
        holder.exerciseName.setText(setLog.getExerciseName());
        holder.setInfo.setText(String.format(Locale.getDefault(), "Set %d: %.1f kg x %d", 
            setLog.getSetNumber(), setLog.getWeight(), setLog.getReps()));
    }

    @Override
    public int getItemCount() {
        return loggedSets == null ? 0 : loggedSets.size();
    }

    public void updateLoggedSets(List<SetLog> newLoggedSets) {
        this.loggedSets = newLoggedSets;
        notifyDataSetChanged();
    }

    public static class LoggedSetViewHolder extends RecyclerView.ViewHolder {
        final TextView exerciseName;
        final TextView setInfo;

        public LoggedSetViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.tvLoggedExerciseName);
            setInfo = itemView.findViewById(R.id.tvLoggedSetInfo);
        }
    }
}
