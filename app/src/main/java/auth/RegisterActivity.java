package auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Since the package is changed, we need to use the full path for the binding class and R class.
import com.bis5.fitjourney.R;
import com.bis5.fitjourney.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> navigateToLogin());
        binding.btnRegister.setOnClickListener(v -> registerUser());
        binding.tvLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void registerUser() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (validateInputs(fullName, email, password, confirmPassword)) {
            // Add your actual registration logic here
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        }
    }

    private boolean validateInputs(String fullName, String email, String password, String confirmPassword) {
        boolean isValid = true;

        if (fullName.isEmpty()) {
            binding.tilFullName.setError("Full name is required");
            isValid = false;
        } else {
            binding.tilFullName.setError(null);
        }

        if (email.isEmpty()) {
            binding.tilEmail.setError("Email is required");
            isValid = false;
        } else {
            binding.tilEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            binding.tilPassword.setError(null);
        }

        if (!confirmPassword.equals(password)) {
            binding.tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        } else {
            binding.tilConfirmPassword.setError(null);
        }

        return isValid;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}