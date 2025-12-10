package com.bis5.fitjourney.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.bis5.fitjourney.R;
import com.bis5.fitjourney.databinding.FragmentRegisterBinding;
import com.bis5.fitjourney.viewmodels.AuthViewModel;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user already exists
            authViewModel.getUserByEmail(email).observe(getViewLifecycleOwner(), existingUser -> {
                if (existingUser != null) {
                    Toast.makeText(getContext(), "User with this email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Register the new user
                    authViewModel.register(name, email, password);
                    Toast.makeText(getContext(), "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                    // Go back to login screen
                    NavHostFragment.findNavController(this).navigate(R.id.action_registerFragment_to_loginFragment);
                }
            });
        });

        binding.tvGoToLogin.setOnClickListener(v -> 
            NavHostFragment.findNavController(this).navigate(R.id.action_registerFragment_to_loginFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
