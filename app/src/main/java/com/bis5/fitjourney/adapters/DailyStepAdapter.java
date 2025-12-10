package com.bis5.fitjourney.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bis5.fitjourney.databinding.ItemDailyStepBinding;
import com.bis5.fitjourney.models.DailyStep;

import java.util.ArrayList;
import java.util.List;

public class DailyStepAdapter extends RecyclerView.Adapter<DailyStepAdapter.DailyStepViewHolder> {

    private final List<DailyStep> dailySteps = new ArrayList<>();

    public void setDailySteps(List<DailyStep> newSteps) {
        dailySteps.clear();
        dailySteps.addAll(newSteps);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DailyStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemDailyStepBinding binding = ItemDailyStepBinding.inflate(inflater, parent, false);
        return new DailyStepViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyStepViewHolder holder, int position) {
        DailyStep dailyStep = dailySteps.get(position);
        holder.bind(dailyStep);
    }

    @Override
    public int getItemCount() {
        return dailySteps.size();
    }

    static class DailyStepViewHolder extends RecyclerView.ViewHolder {
        private final ItemDailyStepBinding binding;

        public DailyStepViewHolder(ItemDailyStepBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DailyStep dailyStep) {
            binding.tvDayOfWeek.setText(dailyStep.getDayOfWeek());
            binding.tvDate.setText(dailyStep.getDate());
            binding.tvDailyStepCount.setText(String.format("%,d", dailyStep.getSteps()));
        }
    }
}
