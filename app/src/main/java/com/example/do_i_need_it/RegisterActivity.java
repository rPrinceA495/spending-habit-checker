package com.example.do_i_need_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText fullNameField, phoneNumberField, emailField, passwordField, confirmPasswordField, dobField, countryField;
    String fullName, email, phoneNumber, password, confirmPassword, userID, dob, country;
    Button registerBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Check Login Status
        if(fAuth.getCurrentUser() != null) {
         startActivity(new Intent(getApplicationContext(), MainActivity.class));
         finish();
        }

        // Hooks
        fullNameField = findViewById(R.id.full_name);
        phoneNumberField = findViewById(R.id.phone_number);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        registerBtn = findViewById(R.id.register_btn);
        confirmPasswordField = findViewById(R.id.confirm_password);
        dobField = findViewById(R.id.dob);
        countryField = findViewById(R.id.country);
        progressBar = findViewById(R.id.progress_bar);

        registerBtn.setOnClickListener(view -> {

            // Entered data
            fullName = fullNameField.getText().toString().trim();
            dob = dobField.getText().toString().trim();
            email = emailField.getText().toString().trim();
            country = countryField.getText().toString().trim();
            phoneNumber = phoneNumberField.getText().toString().trim();
            password = passwordField.getText().toString().trim();
            confirmPassword = confirmPasswordField.getText().toString().trim();

            // Form Validation
            if(TextUtils.isEmpty(fullName)) {
                fullNameField.setError("Full name required.");
                return;
            }
            if(TextUtils.isEmpty(dob)) {
                dobField.setError("Please confirm your password.");
                return;
            }
            if(TextUtils.isEmpty(email)) {
                emailField.setError("Email required.");
                return;
            }
            if(TextUtils.isEmpty(country)) {
                countryField.setError("Please confirm your password.");
                return;
            }
            if(TextUtils.isEmpty(phoneNumber)) {
                phoneNumberField.setError("Phone number required.");
                return;
            }if(TextUtils.isEmpty(password)) {
                passwordField.setError("Password required.");
                return;
            }if(TextUtils.isEmpty(confirmPassword)) {
                confirmPasswordField.setError("Please confirm your password.");
                return;
            }
            if(password.length() < 6 || confirmPassword.length() < 6) {
                passwordField.setError("Password should have minimum 6 characters.");
                return;
            }if(!confirmPassword.equals(password)) {
                passwordField.setError("Entered Passwords do not match.");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // Store email and password for authentication
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    userID = fAuth.getCurrentUser().getEmail();
                    // Store user details to firestore:
                    DocumentReference userDocRef = fStore.collection("users").document(userID);
                    final Map<String, Object> user = new HashMap<>();
                    user.put("full_name", fullName);
                    user.put("phone_number", phoneNumber);
                    user.put("dob", dob);
                    user.put("country", country);
                    userDocRef.set(user).addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Account successfully created.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "User details successfully stored.");
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onFailure: database ref not created.");
                    });;
                }else {
                    Toast.makeText(getApplicationContext(), "Error! "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        });

    }

    public void redirectToLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

}