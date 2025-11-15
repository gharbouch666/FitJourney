package com.bis5.fitjourney.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bis5.fitjourney.MainActivity;
import com.bis5.fitjourney.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                // This is a placeholder for actual authentication logic
                if ("test@test.com".equals(email) && "123456".equals(password)) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity(email);
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            return false;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            return false;
        }
        return true;
    }

    private void navigateToMainActivity(String userEmail) {
        // Correctly pass the email to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_EMAIL", userEmail);
        startActivity(intent);
        finish();
    }
}
