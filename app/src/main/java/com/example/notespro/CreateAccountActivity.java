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

public class CreateAccountActivity extends AppCompatActivity {

    EditText tvEmail, tvPassword, tvcnfmPassword;
    Button btnSignUp;
    TextView tvSignIn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary));

        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        tvcnfmPassword = findViewById(R.id.tvCnfmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);
        progressBar = findViewById(R.id.progressBar);

        btnSignUp.setOnClickListener(v -> createAccount());
        tvSignIn.setOnClickListener(v -> {
            startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class));
            finish();
        });

    }

    void createAccount(){
        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();
        String cnfmPassword = tvcnfmPassword.getText().toString();

        boolean isValidated = validateData(email,password,cnfmPassword);
        if(!isValidated)
            return;

        createAccountInFirebase(email,password);

    }

    void createAccountInFirebase(String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                task -> {
                    changeInProgress(false);
                    if(task.isSuccessful()){
                        //Toast.makeText(CreateAccountActivity.this, "Successfully create account,Check email to verify", Toast.LENGTH_SHORT).show();
                        Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification();
                        //firebaseAuth.signOut();
                        startActivity(new Intent(CreateAccountActivity.this,EmailVerifyActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(CreateAccountActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void changeInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String cnfmPassword){
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
        if(!password.equals(cnfmPassword)){
            tvcnfmPassword.setError("Password not matched");
            return false;
        }
        return true;
    }


}