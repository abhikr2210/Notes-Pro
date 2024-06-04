package com.example.notespro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText tvEmail, tvPassword;
    Button btnLogIn;
    TextView tvLogIn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary));

        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        btnLogIn = findViewById(R.id.btnLogIn);
        tvLogIn = findViewById(R.id.tvLogIn);
        progressBar = findViewById(R.id.progressBar);

        btnLogIn.setOnClickListener(v -> loginUser());
        tvLogIn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
            finish();
        });
    }

    private void loginUser() {
        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();

        boolean isValidated = validateData(email,password);
        if(!isValidated)
            return;

        loginAccountInFirebase(email,password);
    }

    private void loginAccountInFirebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            changeInProgress(false);
            if(task.isSuccessful()){
                if(Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()){
                    Toast.makeText(LoginActivity.this, "Successfully login", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Email not verified, Please verify your email.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            btnLogIn.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            btnLogIn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password){
        // Regular expression pattern for email validation
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";

        if (!email.matches(emailPattern)) {
            tvEmail.setError("Email is Invalid");
            return false;
        }
        if(password.length()<6){
            tvPassword.setError("Password length is invalid");
            return false;
        }
        return true;
    }
}