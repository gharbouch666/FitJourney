package com.bis5.fitjourney.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.databinding.FragmentHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);

        // This navigation is not a main tab, so a simple navigate action is correct.
        binding.btnAvatar.setOnClickListener(v -> {
            navController.navigate(R.id.action_homeFragment_to_profileFragment);
        });

        // This listener makes the cards/buttons behave exactly like clicking the "Workouts" tab.
        View.OnClickListener toWorkoutListListener = v -> {
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.workoutListFragment);
            }
        };

        binding.btnStartWorkout.setOnClickListener(toWorkoutListListener);
        binding.cardWorkouts.setOnClickListener(toWorkoutListListener);
        binding.todaysWorkoutCard.setOnClickListener(toWorkoutListListener);

        // Other quick actions that mimic bottom nav clicks
        binding.cardNutrition.setOnClickListener(v -> {
             if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nutritionLogFragment);
            }
        });

        binding.cardProgress.setOnClickListener(v -> {
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.workoutListFragment);
            }
        });

        binding.cardSocial.setOnClickListener(v -> {
             if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.socialFeedFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
