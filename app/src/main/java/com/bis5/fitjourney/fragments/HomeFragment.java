package com.bis5.fitjourney.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.bis5.fitjourney.R;
import com.bis5.fitjourney.databinding.FragmentHomeBinding;
import com.bis5.fitjourney.viewmodels.SharedViewModel;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        setupNavigation();
        setupWelcomeMessage();
        setupQuoteOfTheDay();
        setupBarChart();
    }

    private void setupWelcomeMessage() {
        sharedViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            if (email != null && !email.isEmpty()) {
                binding.tvWelcome.setText(String.format("Welcome, %s!", email.split("@")[0]));
            }
        });
    }

    private void setupQuoteOfTheDay() {
        List<String> quotes = Arrays.asList(
                "'The body achieves what the mind believes.'",
                "'The only bad workout is the one that didn't happen.'",
                "'Success isn't always about greatness. It's about consistency. Consistent hard work gains success. Greatness will come.'",
                "'The clock is ticking. Are you becoming the person you want to be?'",
                "'Your body can stand almost anything. It's your mind that you have to convince.'"
        );
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        binding.tvQuoteOfTheDay.setText(quotes.get(dayOfYear % quotes.size()));
    }

    private void setupBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 4f));
        entries.add(new BarEntry(1, 8f));
        entries.add(new BarEntry(2, 6f));
        entries.add(new BarEntry(3, 2f));
        entries.add(new BarEntry(4, 10f));
        entries.add(new BarEntry(5, 5f));
        entries.add(new BarEntry(6, 7f));

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Activity");
        int chartColor = ContextCompat.getColor(requireContext(), R.color.a_500);
        dataSet.setColor(chartColor);
        dataSet.setValueTextColor(Color.WHITE);

        BarData barData = new BarData(dataSet);
        binding.barChart.setData(barData);

        // Customize the chart
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.getLegend().setEnabled(false);
        binding.barChart.setDrawValueAboveBar(false);
        binding.barChart.setFitBars(true);

        // Customize X-axis
        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));

        // Customize Y-axis
        binding.barChart.getAxisLeft().setTextColor(Color.WHITE);
        binding.barChart.getAxisRight().setEnabled(false);

        binding.barChart.invalidate(); // Refresh the chart
    }

    private void setupNavigation() {
        binding.cardWorkouts.setOnClickListener(v -> 
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_workoutListFragment));

        binding.cardNutrition.setOnClickListener(v -> 
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_nutritionLogFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
