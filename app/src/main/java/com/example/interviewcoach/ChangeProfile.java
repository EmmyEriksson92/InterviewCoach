package com.example.interviewcoach;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Program where user can edit profile information
 * and profile picture by taking new picture or selecting from gallery and save data to firebase.
 *
 * @author Emmy
 */
public class ChangeProfile extends AppCompatActivity {
    private ActivityResultLauncher<Intent> activityResultLauncherCaptureImage, activityResultLauncherSelectImage;
    private StorageReference storageReference;
    private ShapeableImageView imageView;
    private EditText et_name, et_email;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String userId;
    private static final String TAG = "Save image";
    private static int IMAGE_PERMISSION_CODE = 100;

    //Start activity.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        imageView = findViewById(R.id.iv_changeProfile);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        //If user  has an profile image display the image in imageView.
        StorageReference profileRef = storageReference.child("profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(imageView));

        //Get user data that is saved in firebase collection and display in profile.
        DocumentReference documentReference = firestore.collection("users").document(userId);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            et_name.setText(documentSnapshot.getString("fName"));
            et_email.setText(documentSnapshot.getString("email"));
        });


        //If result is not null get data from activity result and display new added image in profile.
        activityResultLauncherSelectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent intent = result.getData();
                Uri uri = intent.getData();
                imageView.setImageURI(uri);
                uploadImageToFirebase(uri);

            }
        });

        //If result is not null get data from activity result and display new added image in profile.
        activityResultLauncherCaptureImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent intent = result.getData();
                Bitmap thumbnail = (Bitmap) intent.getExtras().get("data");
                imageView.setImageBitmap(thumbnail);
                uploadImageToFirebaseBitMap(thumbnail);

            }
        });
    }

    //Method for uploading bitmap image to firebase.
    private void uploadImageToFirebaseBitMap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        StorageReference fileRef = storageReference.child("profile.jpg");
        fileRef.putBytes(data).addOnSuccessListener(taskSnapshot -> Log.i(TAG, "image saved")).addOnFailureListener(e -> Log.e(TAG, "Image not saved"));
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


    //Upload image to firebase storage.
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> Log.i(TAG, "image saved")).addOnFailureListener(e -> Log.e(TAG, "Image not saved"));

    }

    //Method for changing image with Intent.
    public void changeImage(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_profilepicture);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.item1_profile:
                    pickImageFromGallery();
                    return true;
                case R.id.item2_profile:
                    takePicture();
                    return true;
                default:
                    return false;
            }
        });
    }

    //Method for saving profile to firebase.
    public void saveProfile(View view) {
        String name = et_name.getText().toString();
        String email = et_email.getText().toString().trim();

        if (!validateName(name) || !validateEmail(email)) {
            return;
        }

        HashMap hashMap = new HashMap();
        hashMap.put("fName", name);
        hashMap.put("email", email);
        final DocumentReference documentReference = firestore.collection("users").document(userId);
        documentReference.update(hashMap).addOnSuccessListener(o -> Toast.makeText(ChangeProfile.this, "Profile saved",
                Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(ChangeProfile.this, "ERROR - saving profile.", Toast.LENGTH_SHORT).show());

    }

    //Method for validating name input.
    private boolean validateName(String fName) {
        if (!ValidateUtils.validateName(fName)) {
            et_name.setError("Name is required");
            return false;
        }
        return true;
    }

    //Method for validating email input.
    private boolean validateEmail(String emailAdress) {
        if (!ValidateUtils.validateEmail(emailAdress)) {
            et_email.setError("Email is invalid.");
            return false;
        } else if (TextUtils.isEmpty(emailAdress)) {
            et_email.setError("Email cannot be empty.");
            return false;
        }
        return true;
    }

    //Create new intent for taking picture with MediaStore.
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (!checkPermissions()) {
            getPermissions();
        }

        activityResultLauncherCaptureImage.launch(intent);
    }

    //Method for checking permissions.
    private boolean checkPermissions() {
        int permission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    //Method for getting permissions.
    private void getPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, IMAGE_PERMISSION_CODE);
    }

    //Create new intent for selecting image & launch activity result.
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncherSelectImage.launch(intent);
    }

}
