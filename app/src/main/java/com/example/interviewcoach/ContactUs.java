package com.example.interviewcoach;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Program for email support where user can send email for support if any questions about the app.
 *
 * @author Emmy
 */
public class ContactUs extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText name, email, phoneNumber, message;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String selected;
    private Spinner dropdown;

    //Start activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        name = findViewById(R.id.et_nameContact);
        email = findViewById(R.id.et_emailContact);
        phoneNumber = findViewById(R.id.et_phoneContact);
        message = findViewById(R.id.et_messageSupport);
        dropdown = findViewById(R.id.spinner1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.holo_orange_dark));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);


        //Show Toast message when email is sent.
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Toast.makeText(this, "Email sent!", Toast.LENGTH_SHORT).show();

        });


    }

    /**
     * this event will enable the back
     * function to the button on press
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Method for validating name input.
    private boolean validateName(String fName) {
        if (!ValidateUtils.validateName(fName)) {
            name.setError("Name is required");
            return false;
        }
        return true;
    }

    //Method for validating email input.
    private boolean validateEmail(String emailAdress) {
        if (!ValidateUtils.validateEmail(emailAdress)) {
            email.setError("Email is invalid.");
            return false;
        } else if (TextUtils.isEmpty(emailAdress)) {
            email.setError("Email cannot be empty.");
            return false;
        }
        return true;
    }

    //Method for validating phone number.
    private boolean validatePhoneNumber(String phoneNb) {
        if (!ValidateUtils.validatePhoneNb(phoneNb)) {
            phoneNumber.setError("Phone number should contain only digits.");
            return false;
        } else if (TextUtils.isEmpty(phoneNb)) {
            phoneNumber.setError("Phone number cannot be empty.");
            return false;
        }
        return true;
    }

    //Method for validating message.
    private boolean validateMessage(String msg) {
        if (TextUtils.isEmpty(msg)) {
            message.setError("Message is required");
            return false;
        }
        return true;
    }

    //When user clicks on button: btn_sendSupport send email of chosen email adress with name, phone number & message.
    public void contactUs(View view) {
        String fName = name.getText().toString();
        String emailAdress = email.getText().toString().trim();
        String phoneNb = phoneNumber.getText().toString();
        String msg = message.getText().toString();

        if(selected == null){
            Toast.makeText(this, "You have to choose type of matter", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateName(fName) || !validateEmail(emailAdress) || !validatePhoneNumber(phoneNb) || !validateMessage(msg)) {
            return;
        }

        String content = "Matter: " + selected + "\n\n" + "Name: " + fName + "\n\n" + "Phone number: " + phoneNb + "\n\n" + msg;
        String[] to = {"emmyveriksson@gmail.com", emailAdress};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Support");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        //Open only email client
        Intent chooser = Intent.createChooser(intent, "Choose an Email client :");
        if (chooser.resolveActivity(getPackageManager()) != null) {
            activityResultLauncher.launch(intent);
        }
    }

    //When user have selected an item in dropdown of Spinner, set String selected to that choice.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getItemAtPosition(position).equals("Choose matter:")){
            return;
        }else{
            selected = parent.getItemAtPosition(position).toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
