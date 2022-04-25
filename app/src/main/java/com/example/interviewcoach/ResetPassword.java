package com.example.interviewcoach;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Program resetting password for user account in firebase.
 *
 * @author Emmy
 */
public class ResetPassword extends AppCompatActivity {
    private EditText email;

    //Start activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        email = findViewById(R.id.et_email_reset);

    }

    //When user clicks on Button: btn_resetPass start task for resetting password.
    public void resetPassword(View view) {
        String emailString = email.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)) {
            email.setError("Email is required");
            return;
        }

        //If task is successful show Toast message for user, otherwise show Toast message with error message.
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailString).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Email sent successfully to reset password", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
