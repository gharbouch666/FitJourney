package com.bis5.fitjourney;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.bis5.fitjourney.databinding.ActivityMainBinding;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.viewmodels.NutritionViewModel;
import com.bis5.fitjourney.viewmodels.SharedViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private SharedViewModel sharedViewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startBackgroundAnimation(binding.animatedBackgroundView);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            setupNavigationUI(binding, navController);
        }

        setupViewModels();
        setupNavListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            performLogout();
            return true;
        } else if (item.getItemId() == R.id.action_user_profile) {
            navController.navigate(R.id.action_homeFragment_to_userProfileFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build();
        navController.navigate(R.id.loginFragment, null, navOptions);
    }

    private void startBackgroundAnimation(View view) {
        Drawable background = view.getBackground();
        if (background instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) background;
            animationDrawable.setEnterFadeDuration(10);
            animationDrawable.setExitFadeDuration(5000);
            animationDrawable.start();
        }
    }

    private void setupNavigationUI(ActivityMainBinding binding, NavController navController) {
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.homeFragment);
        topLevelDestinations.add(R.id.workoutListFragment);
        topLevelDestinations.add(R.id.stepCounterFragment);
        topLevelDestinations.add(R.id.nutritionLogFragment);
        topLevelDestinations.add(R.id.socialFeedFragment);

        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
             if (item.getItemId() == navController.getCurrentDestination().getId()) {
                return false; 
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
    }

    private void setupNavListener() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment) {
                binding.toolbar.setVisibility(View.GONE);
                binding.bottomNavigation.setVisibility(View.GONE);
            } else {
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.bottomNavigation.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupViewModels() {
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        new ViewModelProvider(this).get(NutritionViewModel.class);
        AppDatabase database = AppDatabase.getDatabase(this);
        WorkoutViewModelFactory factory = new WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao());
        new ViewModelProvider(this, factory).get(WorkoutViewModel.class);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
