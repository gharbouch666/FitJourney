package com.bis5.fitjourney.adapters;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.models.Exercise;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashSet;
import java.util.Locale;

public class ActiveSetAdapter extends RecyclerView.Adapter<ActiveSetAdapter.ActiveSetViewHolder> {

    public interface OnLogSetListener {
        void onLogSet(int setNumber, double weight, int reps);
    }

    private final Exercise exercise;
    private final OnLogSetListener onLogSetListener;
    private final HashSet<Integer> loggedSets = new HashSet<>();
    private CountDownTimer timer;
    private int restingSetPosition = -1;

    public ActiveSetAdapter(Exercise exercise, OnLogSetListener listener) {
        this.exercise = exercise;
        this.onLogSetListener = listener;
    }

    @NonNull
    @Override
    public ActiveSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_set, parent, false);
        return new ActiveSetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveSetViewHolder holder, int position) {
        int setIndex = position + 1;
        holder.setNumber.setText(String.format(Locale.getDefault(), "Set %d", setIndex));

        if (restingSetPosition == position) {
            holder.weightLayout.setVisibility(View.GONE);
            holder.repsLayout.setVisibility(View.GONE);
            holder.logSetButton.setVisibility(View.GONE);
            holder.restTimer.setVisibility(View.VISIBLE);
            holder.skipRestButton.setVisibility(View.VISIBLE);

            holder.skipRestButton.setOnClickListener(v -> {
                if (timer != null) {
                    timer.cancel();
                }
                restingSetPosition = -1;
                notifyItemChanged(position);
            });
        } else if (loggedSets.contains(setIndex)) {
            holder.weightLayout.setVisibility(View.VISIBLE);
            holder.repsLayout.setVisibility(View.VISIBLE);
            holder.weight.setEnabled(false);
            holder.reps.setEnabled(false);
            holder.logSetButton.setClickable(false);
            holder.logSetButton.setVisibility(View.VISIBLE);
            // BUG FIX: Use setIconTintResource for MaterialButton
            holder.logSetButton.setIconTintResource(android.R.color.holo_green_dark);
            holder.restTimer.setVisibility(View.GONE);
            holder.skipRestButton.setVisibility(View.GONE);
        } else {
            holder.weightLayout.setVisibility(View.VISIBLE);
            holder.repsLayout.setVisibility(View.VISIBLE);
            holder.weight.setEnabled(true);
            holder.reps.setEnabled(true);
            holder.weight.setText(String.valueOf(exercise.getWeight()));
            holder.reps.setText(String.valueOf(exercise.getReps()));
            holder.logSetButton.setClickable(true);
            holder.logSetButton.setVisibility(View.VISIBLE);
            // BUG FIX: Use setIconTint(null) to clear tint for MaterialButton
            holder.logSetButton.setIconTint(null);
            holder.restTimer.setVisibility(View.GONE);
            holder.skipRestButton.setVisibility(View.GONE);

            holder.logSetButton.setOnClickListener(v -> {
                String weightStr = holder.weight.getText().toString();
                String repsStr = holder.reps.getText().toString();
                double weightValue = weightStr.isEmpty() ? 0.0 : Double.parseDouble(weightStr);
                int repsValue = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);
                
                onLogSetListener.onLogSet(setIndex, weightValue, repsValue);
                loggedSets.add(setIndex);
                startRestTimer(holder, position);
            });
        }
    }

    private void startRestTimer(ActiveSetViewHolder holder, int position) {
        restingSetPosition = position;
        notifyItemChanged(position);

        timer = new CountDownTimer(60000, 1000) { // 60-second timer
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                holder.restTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                restingSetPosition = -1;
                notifyItemChanged(position);
            }
        }.start();
    }

    @Override
    public int getItemCount() {
        return exercise.getSets();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (timer != null) {
            timer.cancel(); // Prevent memory leaks
        }
    }

    public static class ActiveSetViewHolder extends RecyclerView.ViewHolder {
        final TextView setNumber;
        final TextInputLayout weightLayout;
        final TextInputLayout repsLayout;
        final EditText weight;
        final EditText reps;
        // BUG FIX: This is a MaterialButton, not an ImageButton
        final MaterialButton logSetButton;
        final TextView restTimer;
        final Button skipRestButton;

        public ActiveSetViewHolder(@NonNull View itemView) {
            super(itemView);
            setNumber = itemView.findViewById(R.id.tvSetNumber);
            weightLayout = itemView.findViewById(R.id.tilWeight);
            repsLayout = itemView.findViewById(R.id.tilReps);
            weight = itemView.findViewById(R.id.etWeight);
            reps = itemView.findViewById(R.id.etReps);
            logSetButton = itemView.findViewById(R.id.btnLogSet);
            restTimer = itemView.findViewById(R.id.tvRestTimer);
            skipRestButton = itemView.findViewById(R.id.btnSkipRest);
        }
    }
}
