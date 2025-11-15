package com.bis5.fitjourney;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bis5.fitjourney.databinding.ActivityMainBinding;
import com.bis5.fitjourney.viewmodels.SharedViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the SharedViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Get the user's email from the Intent and set it in the SharedViewModel
        if (getIntent() != null && getIntent().hasExtra("USER_EMAIL")) {
            String userEmail = getIntent().getStringExtra("USER_EMAIL");
            sharedViewModel.setUserEmail(userEmail);
        }

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }
    }
}
