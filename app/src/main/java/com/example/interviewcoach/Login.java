package com.example.interviewcoach;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Program for login in user with Firebase.
 *
 * @author Emmy
 */
public class Login extends AppCompatActivity {
    private EditText etEmail, etPass;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    //Start activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_pass);
        progressBar = findViewById(R.id.progress_login);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //When user clicks on button: btn_login login.
    public void login(View view) {
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            etPass.setError("Password is required");
            return;
        }


        //If login is successful redirect user to Main-page. Otherwise show Toast error message.
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.VISIBLE);
            if (task.isSuccessful()) {
                Toast.makeText(Login.this, "login successfull!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(Login.this, "Error - " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //When user clicks on TextView: tv_forgotPass user is redirected to ResetPassword page.
    public void reset(View view) {
        startActivity(new Intent(getApplicationContext(), ResetPassword.class));
    }

    //When user clicks on TextView: tv_register user is redirected to Register-page.
    public void register(View view) {
        startActivity(new Intent(getApplicationContext(), Register.class));
    }
}
