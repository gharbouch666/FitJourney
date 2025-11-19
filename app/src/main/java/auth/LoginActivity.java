package auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Since the package is changed, we need to use the full path for the binding class and R class.
import com.bis5.fitjourney.R;
import com.bis5.fitjourney.databinding.ActivityLoginBinding;
import com.google.android.material.textfield.TextInputEditText;

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
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });
    }

    private void handleLogin() {
        TextInputEditText emailEditText = (TextInputEditText) binding.etEmail.getEditText();
        TextInputEditText passwordEditText = (TextInputEditText) binding.etPassword.getEditText();

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simple login - any email/password works
        if (isValidLogin(email, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Navigate to main screen
            try {
                Intent intent = new Intent(LoginActivity.this, Class.forName("com.bis5.fitjourney.MainActivity"));
                intent.putExtra("USER_EMAIL", email); // Pass the email to the main activity
                startActivity(intent);
                finish();
            } catch (ClassNotFoundException e) {
                Toast.makeText(this, "Main screen not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValidLogin(String email, String password) {
        return !email.isEmpty() && !password.isEmpty();
    }

    private void handleRegister() {
        // This needs to be implemented correctly, assuming RegisterActivity has the same package issue
        try {
             Intent intent = new Intent(LoginActivity.this, Class.forName("auth.RegisterActivity"));
             startActivity(intent);
        } catch (ClassNotFoundException e) {
             Toast.makeText(this, "Register screen not found", Toast.LENGTH_SHORT).show();
        }
    }
}