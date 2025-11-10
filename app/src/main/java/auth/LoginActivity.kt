package com.bis5.fitjourney.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bis5.fitjourney.MainActivity
import com.bis5.fitjourney.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                if (email == "test@test.com" && password == "123456") {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    // Super Mode: Pass the email directly to MainActivity.
                    navigateToMainActivity(email)
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }
        return true
    }

    private fun navigateToMainActivity(userEmail: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            // Add the user's email as an extra to the intent.
            putExtra("USER_EMAIL", userEmail)
        }
        startActivity(intent)
        finish()
    }
}