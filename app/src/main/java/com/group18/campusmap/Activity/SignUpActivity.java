package com.group18.campusmap.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group18.campusmap.Constant.Constants;
import com.group18.campusmap.Permissions.Permissions;
import com.group18.campusmap.UserModel;
import com.group18.campusmap.Utils.LoadDialog;
import com.group18.campusmap.databinding.ActivityRegisterBinding;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding; // View binding object
    private Uri imageUri; // Stores the selected image URI
    private Permissions permissions; // Helper class to handle permissions
    private LoadDialog loadDialog; // Dialog for showing loading progress
    private String email, username, password; // User input variables
    private StorageReference storageRef; // Reference to Firebase Storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        permissions = new Permissions(); // Initialize Permissions object
        loadDialog = new LoadDialog(this); // Initialize LoadDialog with context
        storageRef = FirebaseStorage.getInstance().getReference(); // Get Firebase Storage reference

        // Set click listener for "Back" button
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });

        // Set click listener for "Login" text to navigate back
        binding.txtLogin.setOnClickListener(view -> {
            onBackPressed();
        });

        // Set click listener for "Sign Up" button
        binding.btnSignUp.setOnClickListener(view -> {
            if (areFieldReady()) { // Check if all required fields are ready
                if (imageUri != null) {
                    register(); // If image is selected, proceed with registration
                } else {//else prompt user to select image
                    Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for image to pick an image
        binding.imgAdd.setOnClickListener(view -> {
            if (permissions.isStorageOk(this)) { // Check if storage permission is granted
                pickImage(); // If granted, open image picker
            } else {
                permissions.requestStoragePermission(this); // If not, request storage permission
            }
        });

    }

    // Method to open the image picker using a 3rd party library
    private void pickImage() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
    }

    // Method to check if all required fields are filled
    private boolean areFieldReady() {
        username = binding.editUsername.getText().toString().trim();
        email = binding.editEmail.getText().toString().trim();
        password = binding.editPassword.getText().toString().trim();

        boolean flag = false;
        View requestView = null;

        // Check if any of the fields is empty or invalid and set error accordingly
        if (username.isEmpty()) {
            binding.editUsername.setError("Field is required");
            flag = true;
            requestView = binding.editUsername;
        } else if (email.isEmpty()) {
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

    // Method to handle user registration with Firebase
    private void register(){
        loadDialog.startLoading(); // Start loading dialog
        FirebaseAuth auth = FirebaseAuth.getInstance(); // Get FirebaseAuth instance
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users"); // Get Firebase Realtime Database reference
        // Create a new user with email and password
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> register) {
                if(register.isSuccessful()){ // If user creation is successful
                    // Upload the selected image to Firebase Storage
                    storageRef.child(auth.getUid() + Constants.IMAGE_PATH).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL of the uploaded image
                            Task<Uri> image = taskSnapshot.getStorage().getDownloadUrl();
                            image.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> imageTask) {
                                    if (imageTask.isSuccessful()){ // If image download URL is obtained successfully
                                        String url = imageTask.getResult().toString(); // Get the image URL
                                        // Create a UserProfileChangeRequest to set user display name and photo URI
                                        UserProfileChangeRequest changeAVI = new UserProfileChangeRequest.Builder().
                                                setDisplayName(username).
                                                setPhotoUri(Uri.parse(url)).
                                                build();
                                        // Update the current user's profile with the new changes
                                        Objects.requireNonNull(auth.getCurrentUser()).updateProfile(changeAVI).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){ // If profile update is successful
                                                            // Create a new UserModel object
                                                            UserModel userModel = new UserModel(email, username, url, true);
                                                            // Save the user data to Firebase Realtime Database
                                                            dbRef.child(Objects.requireNonNull(auth.getUid())).
                                                                    setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            // Send email verification to the user
                                                                            auth.getCurrentUser().sendEmailVerification()
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            // Stop the loading dialog and show a success message
                                                                                            loadDialog.stopLoading();
                                                                                            Toast.makeText(SignUpActivity.this,"Verify email", Toast.LENGTH_SHORT).show();
                                                                                            onBackPressed();
                                                                                        }
                                                                                    });

                                                                        }
                                                                    });
                                                        }else{
                                                            // Profile update failed
                                                        }

                                                    }
                                                });

                                    }else{
                                        // Image upload failed
                                        loadDialog.stopLoading();
                                        Log.d("TAG", "onComplete: Image: Path"+imageTask.getException());
                                        Toast.makeText(SignUpActivity.this, "Image Path"+ imageTask.getException(),Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                    });

                }else{
                    // User creation failed
                    loadDialog.stopLoading();
                    Log.d("TAG", "onComplete: Create user"+register.getException());
                    Toast.makeText(SignUpActivity.this, ""+register.getException(),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Method to handle the result of the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){ // If image cropping is successful
                imageUri = result.getUri(); // Get the cropped image URI
                Glide.with(this).load(imageUri).into(binding.imgAdd); // Load the image into the ImageView using Glide

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE ) {
                // If image cropping fails, get and log the error
                Exception exception = result.getError();
                Log.d("TAG","onActivityResult: "+exception);
            }
        }
    }

    // Method to handle the result of permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage(); // If storage permission is granted, proceed with image picking
            } else {
                // If storage permission is denied, show a message
                Toast.makeText(this, "Access denied to storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
