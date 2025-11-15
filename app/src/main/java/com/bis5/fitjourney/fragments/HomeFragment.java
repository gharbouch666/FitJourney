package com.bis5.fitjourney.fragments;

import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.databinding.FragmentHomeBinding;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.models.SetLog;
import com.bis5.fitjourney.models.Workout;
import com.bis5.fitjourney.models.WorkoutLog;
import com.bis5.fitjourney.viewmodels.SharedViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private WorkoutViewModel workoutViewModel;
    private SharedViewModel sharedViewModel;
    private String userEmail;
    private String selectedChartExercise = "Bench Press"; // Default

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        AppDatabase database = AppDatabase.getDatabase(requireContext());
        WorkoutViewModelFactory viewModelFactory = new WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao());
        workoutViewModel = new ViewModelProvider(this, viewModelFactory).get(WorkoutViewModel.class);

        // Observe the user's email from the SharedViewModel. This is the fix.
        // All UI and data loading depends on a valid email.
        sharedViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            if (email != null && !email.isEmpty()) {
                this.userEmail = email;
                setupUIAndObservers();
            }
        });
    }

    private void setupUIAndObservers() {
        workoutViewModel.createDefaultWorkoutsIfNoneExist(userEmail);

        String[] emailParts = userEmail.split("@");
        String name = emailParts.length > 0 ? emailParts[0] : "User";

        if (!name.isEmpty()) {
            String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
            binding.tvUserName.setText(capitalizedName);
        } else {
            binding.tvUserName.setText("User");
        }

        observeNextWorkout(userEmail);
        observeWorkoutLogs(userEmail);
        observeChartData(userEmail);

        binding.btnGoToWorkout.setOnClickListener(v -> {
            // Navigate without arguments, as the next screen will also use the SharedViewModel
            NavDirections action =
                    HomeFragmentDirections.actionHomeFragmentToWorkoutListFragment();
            NavHostFragment.findNavController(this).navigate(action);
        });

        binding.ivSettings.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment()));

        binding.ivChartOptions.setOnClickListener(v -> showExerciseSelectionDialog());
    }

    private void observeNextWorkout(String email) {
        workoutViewModel.getWorkouts(email).observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null && !workouts.isEmpty()) {
                Workout nextWorkout = workouts.stream().max(Comparator.comparing(Workout::getDate)).orElse(null);
                if (nextWorkout != null) {
                    binding.tvNextWorkoutName.setText(nextWorkout.getName());
                    updateNextWorkoutIcon(nextWorkout);
                    binding.cardNextWorkout.setOnClickListener(v -> {
                        HomeFragmentDirections.ActionHomeFragmentToWorkoutDetailFragment action =
                                HomeFragmentDirections.actionHomeFragmentToWorkoutDetailFragment(nextWorkout.getId());
                        NavHostFragment.findNavController(this).navigate(action);
                    });
                }
            } else {
                binding.tvNextWorkoutName.setText("No workouts yet!");
                binding.ivNextWorkoutIcon.setVisibility(View.GONE);
            }
        });
    }

    private void observeWorkoutLogs(String email) {
        workoutViewModel.getAllLogsForUser(email).observe(getViewLifecycleOwner(), logs -> {
            if (logs != null) {
                List<Long> dates = new ArrayList<>();
                for(WorkoutLog log : logs) {
                    dates.add(log.getDateStarted());
                }
                updateHeatmap(dates);
            }
        });
    }

    private void observeChartData(String email) {
        binding.tvChartTitle.setText("Progress: " + selectedChartExercise);
        workoutViewModel.getSetHistoryForExercise(email, selectedChartExercise).observe(getViewLifecycleOwner(), setLogs -> {
            if (setLogs != null && !setLogs.isEmpty()) {
                setupProgressChart(setLogs);
            }
        });
    }

    private void showExerciseSelectionDialog() {
        if (userEmail != null) {
            workoutViewModel.getUniqueExerciseNames(userEmail).observe(getViewLifecycleOwner(), exerciseNames -> {
                if (exerciseNames != null && !exerciseNames.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Select Exercise for Chart");
                    builder.setItems(exerciseNames.toArray(new String[0]), (dialog, which) -> {
                        selectedChartExercise = exerciseNames.get(which);
                        observeChartData(userEmail);
                    });
                    builder.show();
                }
            });
        }
    }

    private void updateNextWorkoutIcon(Workout workout) {
        int iconRes;
        switch (workout.getName().toLowerCase()) {
            case "push day": iconRes = R.drawable.push; break;
            case "pull day": iconRes = R.drawable.pull; break;
            case "leg day": iconRes = R.drawable.legs; break;
            case "upper body": iconRes = R.drawable.upper; break;
            case "lower body": iconRes = R.drawable.lower; break;
            case "core": iconRes = R.drawable.core; break;
            case "cardio": iconRes = R.drawable.cardio; break;
            default: iconRes = R.drawable.dumbell; break;
        }
        binding.ivNextWorkoutIcon.setImageResource(iconRes);
    }

    private void setupProgressChart(List<SetLog> setLogs) {
        ArrayList<Entry> entries = new ArrayList<>();
        if (setLogs == null || setLogs.isEmpty()) return;

        long referenceDate = setLogs.get(0).getTimestamp();
        for (SetLog log : setLogs) {
            long daysSinceStart = TimeUnit.MILLISECONDS.toDays(log.getTimestamp() - referenceDate);
            entries.add(new Entry(daysSinceStart, (float) log.getWeight()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Weight (kg)");
        LineData lineData = new LineData(dataSet);

        binding.progressChart.setData(lineData);
        binding.progressChart.getDescription().setEnabled(false);
        binding.progressChart.invalidate();
    }

    private void updateHeatmap(List<Long> workoutDates) {
        binding.heatmapContainer.removeAllViews();
        Calendar cal = Calendar.getInstance();

        for (int i = 6; i >= 0; i--) {
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.add(Calendar.DAY_OF_YEAR, -i);

            View dayView = new View(getContext());
            int size = getResources().getDimensionPixelSize(R.dimen.heatmap_day_size);
            int margin = getResources().getDimensionPixelSize(R.dimen.heatmap_day_margin);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            dayView.setLayoutParams(params);

            boolean workoutOnThisDay = false;
            for (Long date : workoutDates) {
                Calendar workoutCal = Calendar.getInstance();
                workoutCal.setTimeInMillis(date);
                if (workoutCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && workoutCal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)) {
                    workoutOnThisDay = true;
                    break;
                }
            }

            GradientDrawable circleDrawable = (GradientDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.heatmap_day_circle).mutate();
            int colorRes = workoutOnThisDay ? R.color.md_theme_primary : R.color.md_theme_surfaceVariant;
            circleDrawable.setColor(ContextCompat.getColor(requireContext(), colorRes));
            dayView.setBackground(circleDrawable);

            binding.heatmapContainer.addView(dayView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}