package com.bis5.fitjourney.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.databinding.FragmentCalorieCalculatorBinding;
import java.util.Locale;

public class CalorieCalculatorFragment extends Fragment {

    private FragmentCalorieCalculatorBinding binding;
    private SharedPreferences sharedPreferences;

    private double cutCalories, maintenanceCalories, bulkCalories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCalorieCalculatorBinding.inflate(inflater, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("user_goals", Context.MODE_PRIVATE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCalculate.setOnClickListener(v -> calculateAndShowResults());

        binding.btnSetCutGoal.setOnClickListener(v -> setGoal(cutCalories, "Weight Loss"));
        binding.btnSetMaintainGoal.setOnClickListener(v -> setGoal(maintenanceCalories, "Maintenance"));
        binding.btnSetBulkGoal.setOnClickListener(v -> setGoal(bulkCalories, "Weight Gain"));
    }

    private void calculateAndShowResults() {
        if (!validateInputs()) {
            return;
        }

        double height = Double.parseDouble(binding.etHeight.getText().toString());
        double weight = Double.parseDouble(binding.etWeight.getText().toString());
        int age = Integer.parseInt(binding.etAge.getText().toString());

        double bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;

        double activityMultiplier = 1.2;
        int checkedActivityId = binding.rgActivityLevel.getCheckedRadioButtonId();
        if (checkedActivityId == R.id.rbLightlyActive) activityMultiplier = 1.375;
        else if (checkedActivityId == R.id.rbModeratelyActive) activityMultiplier = 1.55;
        else if (checkedActivityId == R.id.rbVeryActive) activityMultiplier = 1.725;
        else if (checkedActivityId == R.id.rbExtraActive) activityMultiplier = 1.9;

        maintenanceCalories = bmr * activityMultiplier;
        cutCalories = maintenanceCalories - 500;
        bulkCalories = maintenanceCalories + 500;

        binding.tvCutCalories.setText(String.format(Locale.getDefault(), "%.0f kcal", cutCalories));
        binding.tvMaintainCalories.setText(String.format(Locale.getDefault(), "%.0f kcal", maintenanceCalories));
        binding.tvBulkCalories.setText(String.format(Locale.getDefault(), "%.0f kcal", bulkCalories));

        binding.resultsLayout.setVisibility(View.VISIBLE);
    }

    private void setGoal(double targetCalories, String goalName) {
        // Example macro split: 40% protein, 40% carbs, 20% fat
        float proteinGrams = (float) ((targetCalories * 0.40) / 4);
        float carbsGrams = (float) ((targetCalories * 0.40) / 4);
        float fatGrams = (float) ((targetCalories * 0.20) / 9);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("goal_calories", (float) targetCalories);
        editor.putFloat("goal_protein", proteinGrams);
        editor.putFloat("goal_carbs", carbsGrams);
        editor.putFloat("goal_fat", fatGrams);
        editor.apply();

        Toast.makeText(getContext(), "\"" + goalName + "\" goal set!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(binding.etHeight.getText()) ||
            TextUtils.isEmpty(binding.etWeight.getText()) ||
            TextUtils.isEmpty(binding.etAge.getText())) {
            Toast.makeText(getContext(), "Please fill in all your stats.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.rgActivityLevel.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Please select your activity level.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
