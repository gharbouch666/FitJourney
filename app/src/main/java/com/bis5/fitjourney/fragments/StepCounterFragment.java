package com.bis5.fitjourney.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bis5.fitjourney.adapters.DailyStepAdapter;
import com.bis5.fitjourney.databinding.FragmentStepCounterBinding;
import com.bis5.fitjourney.models.DailyStep;
import com.bis5.fitjourney.viewmodels.StepCounterViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StepCounterFragment extends Fragment implements SensorEventListener {

    private FragmentStepCounterBinding binding;
    private StepCounterViewModel viewModel;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private DailyStepAdapter dailyStepAdapter;

    private final ActivityResultLauncher<String> requestPermissionLauncher = 
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                startStepCounter();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Step counter will not function.", Toast.LENGTH_SHORT).show();
            }
        });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStepCounterBinding.inflate(inflater, container, false);
        // CORRECTED: Scope ViewModel to this fragment only
        viewModel = new ViewModelProvider(this).get(StepCounterViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();

        viewModel.getStepCount().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getStepGoal().observe(getViewLifecycleOwner(), goal -> {
            binding.progressBarSteps.setMax(goal);
            binding.tvStepGoal.setText(String.format("Goal: %,d", goal));
        });

        // INVINCIBLE SENSOR SETUP
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepCounterSensor == null) {
                Toast.makeText(requireContext(), "Step counter sensor not available on this device.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(requireContext(), "Sensor service not available.", Toast.LENGTH_LONG).show();
        }

        updateWeeklyReport();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Start counter only if sensor exists and permissions are granted
        if (stepCounterSensor != null) {
            checkPermissionAndStart();
        }
    }

    private void checkPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION)
                    == PackageManager.PERMISSION_GRANTED) {
                startStepCounter();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
            }
        } else {
            startStepCounter();
        }
    }

    private void startStepCounter() {
        if (sensorManager != null && stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int steps = (int) event.values[0];
            viewModel.setStepCount(steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void setupRecyclerView() {
        dailyStepAdapter = new DailyStepAdapter();
        binding.rvWeeklySteps.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvWeeklySteps.setAdapter(dailyStepAdapter);
    }

    private void updateUI(int currentSteps) {
        binding.tvStepCount.setText(String.format(Locale.getDefault(), "%,d", currentSteps));
        binding.progressBarSteps.setProgress(currentSteps);

        float distance = currentSteps * 0.000762f; // Approx km per step
        float calories = currentSteps * 0.04f; // Approx kcal per step

        binding.tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", distance));
        binding.tvCalories.setText(String.format(Locale.getDefault(), "%.0f kcal", calories));
    }

    private void updateWeeklyReport() {
        List<DailyStep> weeklySteps = new ArrayList<>();
        weeklySteps.add(new DailyStep("Today", "Oct 27", 7500));
        weeklySteps.add(new DailyStep("Yesterday", "Oct 26", 10234));
        weeklySteps.add(new DailyStep("Friday", "Oct 25", 8123));
        weeklySteps.add(new DailyStep("Thursday", "Oct 24", 9876));
        weeklySteps.add(new DailyStep("Wednesday", "Oct 23", 11567));
        weeklySteps.add(new DailyStep("Tuesday", "Oct 22", 7890));
        weeklySteps.add(new DailyStep("Monday", "Oct 21", 12345));

        dailyStepAdapter.setDailySteps(weeklySteps);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
