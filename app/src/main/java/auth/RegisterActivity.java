package com.bis5.fitjourney.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bis5.fitjourney.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Register button click
        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

            if (validateInputs(fullName, email, password, confirmPassword)) {
                // For now, just show success message
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                // Go back to login
                navigateToLogin();
            }
        });

        // Login text click
        binding.tvLogin.setOnClickListener(v -> navigateToLogin());
    }

    private boolean validateInputs(String fullName, String email, String password, String confirmPassword) {
        if (fullName.isEmpty()) {
            binding.etFullName.setError("Full name is required");
            return false;
        }

        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            return false;
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            return false;
        }

        if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            return false;
        }

        if (!confirmPassword.equals(password)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
