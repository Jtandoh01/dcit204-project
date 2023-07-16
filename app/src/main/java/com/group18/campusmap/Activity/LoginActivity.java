package com.group18.campusmap.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.group18.campusmap.Utils.LoadDialog;
import com.group18.campusmap.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding; // View binding object to access UI elements
    private String email, password; // Variables to store user input for email and password
    private LoadDialog loadDialog; // Dialog for showing loading progress during login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize LoadDialog with context
        loadDialog = new LoadDialog(this);

        // Create intents to navigate to SignUpActivity and ForgotPasswdActivity
        Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        Intent forgotPasswdIntent = new Intent(LoginActivity.this, ForgotPasswdActivity.class);

        // Set click listener for "Sign Up" button to navigate to SignUpActivity
        binding.btnSignUp.setOnClickListener(view->{
            startActivity(signUpIntent);
        });

        // Set click listener for "Forgot Password" text to navigate to ForgotPasswdActivity
        binding.txtForgetPassword.setOnClickListener(view->{
            startActivity(forgotPasswdIntent);
        });

        // Set click listener for "Login" button to perform login
        binding.btnLogin.setOnClickListener(view -> {
            if (areFieldReady()) { // Check if all required fields are ready
                login(); // If ready, perform login
            }
        });
    }

    // Method to perform user login with Firebase
    private void login() {
        loadDialog.startLoading(); // Start loading dialog to indicate login process
        FirebaseAuth auth = FirebaseAuth.getInstance(); // Get instance of FirebaseAuth for authentication

        // Sign in with email and password using Firebase Auth
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> login) {
                if (login.isSuccessful()) { // If login is successful
                    if (Objects.requireNonNull(auth.getCurrentUser()).isEmailVerified()) {
                        // Check if the user's email is verified

                        loadDialog.stopLoading(); // Stop the loading dialog
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent); // Start MainActivity
                        finish(); // Finish the LoginActivity to prevent going back to it on back press
                    } else {
                        // If email is not verified, send a verification email to the user
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> email) {
                                if (email.isSuccessful()) { // If email is sent successfully
                                    loadDialog.stopLoading();
                                    // Show a message to the user to verify their email
                                    Toast.makeText(LoginActivity.this, "Please verify email", Toast.LENGTH_SHORT).show();
                                } else {
                                    loadDialog.stopLoading();
                                    // If email sending fails, show an error message
                                    Toast.makeText(LoginActivity.this, "Error: " + email.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    loadDialog.stopLoading();
                    // If login fails, show an error message
                    Toast.makeText(LoginActivity.this, "Error: " + login.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to check if all required fields are filled
    private boolean areFieldReady() {
        email = binding.editEmail.getText().toString().trim();
        password = binding.editPassword.getText().toString().trim();

        boolean flag = false;
        View requestView = null;

        // Check if any of the fields is empty or invalid and set error accordingly
        if (email.isEmpty()) {
            binding.editEmail.setError("Field is required");
            flag = true;
            requestView = binding.editEmail;
        } else if (password.isEmpty()) {
            binding.editPassword.setError("Field is required");
            flag = true;
            requestView = binding.editPassword;
        } else if (password.length() < 8) {
            binding.editPassword.setError("Minimum 8 characters");
            flag = true;
            requestView = binding.editPassword;
        }

        if (flag) {
            requestView.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}
