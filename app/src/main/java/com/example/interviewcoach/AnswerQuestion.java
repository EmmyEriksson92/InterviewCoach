package com.example.interviewcoach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Program which displays and EditText box where user can answer questions selected in Listview
 * & save answer to external storage.
 *
 * @author Emmy
 */
public class AnswerQuestion extends AppCompatActivity {
    private EditText etAnswer;
    private static final String FILE_NAME = "answer.txt";
    private static int EXTERNAL_STORAGE_PERMISSION_CODE = 100;

    //Start activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);
        etAnswer = findViewById(R.id.et_questions_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.holo_orange_dark));
        if (checkPermissions() == false) {
            getPermissions();
        }

    }

    //Method for adding a new Notification.
    private void addNotification() {
        //If device running the app has Android SDK 26 or upp create Notification channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("save_answer", "Save answer", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "save_answer");
        builder.setContentTitle("Saved to file.");
        builder.setContentText("Answer is now saved to file");
        builder.setSmallIcon(R.drawable.ic_save_black_24dp);
        builder.setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());

    }


    //When user clicks on button: btn_save answer save answer to file.
    public void saveAnswer(View view) {
        String answer = etAnswer.getText().toString();
        if (TextUtils.isEmpty(answer)) {
            etAnswer.setError("Answer is required");
            return;
        }
        if (checkPermissions() == true) {
            writeToFile(answer);
        } else {
            Toast.makeText(this, "Permission for writing file to external storage is denied", Toast.LENGTH_SHORT).show();
        }

    }

    //Method for checking permissions for writing to external storage.
    private boolean checkPermissions() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    //Get permissions for writing to external storage.
    private void getPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, EXTERNAL_STORAGE_PERMISSION_CODE);
    }

    //Method for writing to file.
    public void writeToFile(String dataToWrite) {
        Intent intent = getIntent();
        String selected = intent.getStringExtra("selected");
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), selected + " " + FILE_NAME);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                    file));
            bufferedWriter.write(dataToWrite);
            Toast.makeText(getApplicationContext(), "Saved to " + selected + " " + FILE_NAME, Toast.LENGTH_LONG).show();
            bufferedWriter.close();
            addNotification();
        } catch (IOException e) {
            e.printStackTrace();
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

}
