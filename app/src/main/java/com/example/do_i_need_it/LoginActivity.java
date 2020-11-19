package com.example.do_i_need_it;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText emailField, passwordField;
    ImageView registerRedirect;
    Button loginBtn;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.login_email);
        passwordField = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.login_progress);
        registerRedirect = findViewById(R.id.redirect_register);

        fAuth = FirebaseAuth.getInstance();

        // When login button is clicked
        loginBtn.setOnClickListener(view -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Form validation
            if(TextUtils.isEmpty(email)) {
                emailField.setError("Please enter your email.");
                return;
            }if(TextUtils.isEmpty(password)) {
                passwordField.setError("Please enter your password.");
                return;
            }if(password.length() < 6) {
                passwordField.setError("Password is too short.");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // Firebase authentication action
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Login unsuccessful. "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        });

    }

    public void redirectToRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}