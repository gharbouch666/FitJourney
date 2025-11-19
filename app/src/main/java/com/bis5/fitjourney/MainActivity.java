package com.bis5.fitjourney;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bis5.fitjourney.databinding.ActivityMainBinding;
import com.bis5.fitjourney.viewmodels.SharedViewModel;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the Toolbar as the ActionBar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Initialize the SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Get the user's email from the Intent and set it in the SharedViewModel
        if (getIntent() != null && getIntent().hasExtra("USER_EMAIL")) {
            String userEmail = getIntent().getStringExtra("USER_EMAIL");
            sharedViewModel.setUserEmail(userEmail);
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Define top-level destinations. The Up button will not be shown on these screens.
            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.homeFragment);
            topLevelDestinations.add(R.id.workoutListFragment);
            topLevelDestinations.add(R.id.stepCounterFragment);
            topLevelDestinations.add(R.id.nutritionLogFragment);
            topLevelDestinations.add(R.id.socialFeedFragment);

            appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

            // Connect the NavController to the ActionBar
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Connect the NavController to the BottomNavigationView
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }
    }

    // This is required to handle the Up button press from the ActionBar
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
