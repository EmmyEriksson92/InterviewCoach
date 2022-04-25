package com.example.interviewcoach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Program for register user with Firebase.
 *
 * @author Emmy
 */
public class Register extends AppCompatActivity {
    public static final String TAG_SAVED = "Saved profile";
    public static final String TAG_ERROR = "Error";
    private EditText evName, evEmail, evPass1, evPass2;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private String userId;

    //Start activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        evName = findViewById(R.id.ev_name);
        evEmail = findViewById(R.id.ev_email);
        evPass1 = findViewById(R.id.ev_pass1);
        evPass2 = findViewById(R.id.ev_pass2);
        progressBar = findViewById(R.id.pb_register);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //If user is logged in redirect to MainActivity page.
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }

    /**
     * this event will enable the back
     * function to the button on press
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //When user clicks on tv_login redirect user to Login-page if not currently logged in, otherwise to Main-page.
    public void alreadyRegistered(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    //When user clicks on button: btn_register, register new user in Firebase.
    public void register(View view) {
        String name = evName.getText().toString();
        String email = evEmail.getText().toString().trim();
        String pass1 = evPass1.getText().toString().trim();
        String pass2 = evPass2.getText().toString().trim();

        if (!validateName(name) || !validateEmail(email) || !validatePassword(pass1, pass2)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //Register the user in Firebase.
        firebaseAuth.createUserWithEmailAndPassword(email, pass1).addOnCompleteListener(task -> {
            //If login is successful redirect user to Main-page.
            if (task.isSuccessful()) {
                Toast.makeText(Register.this, "User registered!", Toast.LENGTH_SHORT).show();
                userId = firebaseAuth.getCurrentUser().getUid();
                DocumentReference documentReference = firestore.collection("users").document(userId);
                Map<String, Object> users = new HashMap<>();
                users.put("fName", name);
                users.put("email", email);
                documentReference.set(users).addOnSuccessListener(aVoid -> {
                    Log.d(TAG_SAVED, "User profile is saved in database!");
                });
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                progressBar.setVisibility(View.GONE);
            } else {
                //If login is not successful display error message in Toast message.
                Toast.makeText(Register.this, "Error - " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG_ERROR, task.getException().getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    //Method for validating name input.
    private boolean validateName(String fName) {
        if (!ValidateUtils.validateName(fName)) {
            evName.setError("Name is required");
            return false;
        }
        return true;
    }


    //Method for validating email input.
    private boolean validateEmail(String emailAdress) {
        if (!ValidateUtils.validateEmail(emailAdress)) {
            evEmail.setError("Email is invalid.");
            return false;
        } else if (TextUtils.isEmpty(emailAdress)) {
            evEmail.setError("Email cannot be empty.");
            return false;
        }
        return true;
    }

    //Method for validating password input.
    private boolean validatePassword(String pass1, String pass2) {
        if (TextUtils.isEmpty(pass1)) {
            evPass1.setError("Password is required");
            return false;
        } else if (TextUtils.isEmpty(pass2)) {
            evPass2.setError("Password is required");
            return false;
            //If passwords are not matching display alert dialog.
        } else if (!pass1.equals(pass2)) {
            displayAlertDialog();
            return false;
        }

        if (!ValidateUtils.validatePassword(pass1)) {
            evPass1.setError(getResources().getString(R.string.errorMessage_password));
            return false;
        }

        return true;
    }

    //Method for displaying alert dialog.
    private void displayAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setCancelable(true);
        builder.setTitle("Password is incorrect:");
        builder.setMessage("Passwords are not matching. Please write in matching passwords.").setNegativeButton("Cancel", null).setPositiveButton("OK", null);
        builder.show();
    }

}
