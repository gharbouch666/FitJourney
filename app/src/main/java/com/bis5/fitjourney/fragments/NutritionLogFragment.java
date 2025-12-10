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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bis5.fitjourney.R;
import com.bis5.fitjourney.adapters.FoodItemAdapter;
import com.bis5.fitjourney.databinding.FragmentNutritionLogBinding;
import com.bis5.fitjourney.models.FoodItem;
import com.bis5.fitjourney.viewmodels.NutritionViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class NutritionLogFragment extends Fragment implements FoodItemAdapter.OnItemLongClickListener {

    private FragmentNutritionLogBinding binding;
    private NutritionViewModel nutritionViewModel;
    private FoodItemAdapter adapter;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNutritionLogBinding.inflate(inflater, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("user_goals", Context.MODE_PRIVATE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nutritionViewModel = new ViewModelProvider(requireActivity()).get(NutritionViewModel.class);

        setupRecyclerView();
        updateGoalsDisplay();

        nutritionViewModel.getAllFoodItems().observe(getViewLifecycleOwner(), foodItems -> {
            if (foodItems == null) return;
            adapter.setFoodItems(foodItems);

            float goalCalories = sharedPreferences.getFloat("goal_calories", 2000);
            float goalProtein = sharedPreferences.getFloat("goal_protein", 150);
            float goalCarbs = sharedPreferences.getFloat("goal_carbs", 250);
            float goalFat = sharedPreferences.getFloat("goal_fat", 70);

            double totalCalories = 0;
            double totalProtein = 0;
            double totalCarbs = 0;
            double totalFat = 0;

            for (FoodItem item : foodItems) {
                totalCalories += item.getCalories();
                totalProtein += item.getProtein();
                totalCarbs += item.getCarbohydrates();
                totalFat += item.getFat();
            }

            binding.tvTotalCalories.setText(String.format(Locale.getDefault(), "%.0f", totalCalories));
            binding.tvCalorieGoal.setText(String.format(Locale.getDefault(), "/ %.0f kcal Goal", goalCalories));

            binding.tvProteinProgress.setText(String.format(Locale.getDefault(), "%.0fg / %.0fg", totalProtein, goalProtein));
            binding.progressProtein.setMax((int) goalProtein);
            binding.progressProtein.setProgress((int) totalProtein, true);

            binding.tvCarbsProgress.setText(String.format(Locale.getDefault(), "%.0fg / %.0fg", totalCarbs, goalCarbs));
            binding.progressCarbs.setMax((int) goalCarbs);
            binding.progressCarbs.setProgress((int) totalCarbs, true);

            binding.tvFatProgress.setText(String.format(Locale.getDefault(), "%.0fg / %.0fg", totalFat, goalFat));
            binding.progressFat.setMax((int) goalFat);
            binding.progressFat.setProgress((int) totalFat, true);
        });

        binding.fabAddFood.setOnClickListener(v -> showAddFoodDialog());

        binding.btnCalculator.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_nutritionLogFragment_to_calorieCalculatorFragment);
        });
    }

    private void updateGoalsDisplay() {
        float goalCalories = sharedPreferences.getFloat("goal_calories", 2000);
        float goalProtein = sharedPreferences.getFloat("goal_protein", 150);
        float goalCarbs = sharedPreferences.getFloat("goal_carbs", 250);
        float goalFat = sharedPreferences.getFloat("goal_fat", 70);

        binding.tvCalorieGoal.setText(String.format(Locale.getDefault(), "/ %.0f kcal Goal", goalCalories));
        binding.progressProtein.setMax((int) goalProtein);
        binding.progressCarbs.setMax((int) goalCarbs);
        binding.progressFat.setMax((int) goalFat);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGoalsDisplay();
    }

    private void setupRecyclerView() {
        adapter = new FoodItemAdapter(this);
        binding.rvFoodItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFoodItems.setAdapter(adapter);
    }

    private void showAddFoodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_food, null);

        final TextInputEditText etFoodName = dialogView.findViewById(R.id.etFoodName);
        final TextInputEditText etCalories = dialogView.findViewById(R.id.etCalories);
        final TextInputEditText etProtein = dialogView.findViewById(R.id.etProtein);
        final TextInputEditText etCarbs = dialogView.findViewById(R.id.etCarbs);
        final TextInputEditText etFat = dialogView.findViewById(R.id.etFat);

        builder.setView(dialogView)
                .setTitle("Log New Food")
                .setPositiveButton("Add", (dialog, id) -> {
                    String name = etFoodName.getText().toString().trim();
                    String caloriesStr = etCalories.getText().toString();
                    String proteinStr = etProtein.getText().toString();
                    String carbsStr = etCarbs.getText().toString();
                    String fatStr = etFat.getText().toString();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(caloriesStr)) {
                        Toast.makeText(getContext(), "Food name and calories are required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double calories = Double.parseDouble(caloriesStr);
                    double protein = proteinStr.isEmpty() ? 0 : Double.parseDouble(proteinStr);
                    double carbs = carbsStr.isEmpty() ? 0 : Double.parseDouble(carbsStr);
                    double fat = fatStr.isEmpty() ? 0 : Double.parseDouble(fatStr);

                    // THE LAW IS ENFORCED: The timestamp is now provided.
                    FoodItem newFood = new FoodItem(name, calories, protein, carbs, fat, System.currentTimeMillis());
                    nutritionViewModel.insert(newFood);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    @Override
    public void onItemLongClick(FoodItem foodItem) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Food Item")
                .setMessage("Are you sure you want to delete '" + foodItem.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    nutritionViewModel.delete(foodItem);
                    Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
