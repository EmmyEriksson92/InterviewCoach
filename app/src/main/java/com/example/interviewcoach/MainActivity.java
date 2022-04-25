package com.example.interviewcoach;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * Main program which handles navigation functions with drawer & viewPagers in tabLayout with the apps main functions.
 *
 * @author Emmy
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private DrawerLayout drawer;
    private StorageReference storageReference;
    private ImageView imageView;
    private TextView tvEmail;
    private String userId;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialogSend;
    private EditText mEditTextTo, mEditTextSubject, mEditTextMessage;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private ActivityResultLauncher<Intent> firstActivityResultLauncher, secondActivityResultLauncher;
    private Uri uri;
    private ViewPagerAdapter viewPagerAdapter;
    private static final int PERMISSION_REQUEST_CODE = 100;


    //Start activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        imageView = findViewById(R.id.iv_header);

        //Viewpager2
        viewPager2 = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        FragmentManager fm = getSupportFragmentManager();
        viewPagerAdapter = new ViewPagerAdapter(fm, getLifecycle());
        viewPager2.setAdapter(viewPagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        //NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        imageView = headerLayout.findViewById(R.id.iv_header);
        tvEmail = headerLayout.findViewById(R.id.tv_emailHeader);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();

        //If user  has an profile image display the image in imageView.
        StorageReference profileRef = storageReference.child("profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(imageView));

        //Get user data (email) that is saved in firebase collection and display in drawer.
        DocumentReference documentReference = firestore.collection("users").document(userId);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            tvEmail.setText(documentSnapshot.getString("email"));
        });

        //Show Toast message when email is sent.
        firstActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Toast.makeText(this, "Email sent!", Toast.LENGTH_SHORT).show();

        });

        //If result is not null get data from activity result and show Toast message when attachment is added.
        secondActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                uri = data.getData();
                Toast.makeText(this, "Attachment added!", Toast.LENGTH_SHORT).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        //Rotate the hamburger-icon together with the drawer.
        toggle.syncState();

    }


    //Start and commit transaction of each Fragment when clicked on respective item in Navigation drawer.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch ((item.getItemId())) {
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_contacts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactsFragment()).commit();
                break;
            case R.id.nav_support:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SupportFragment()).commit();
                break;
            case R.id.nav_send:
                showSendPopupWindow();
                break;
            case R.id.nav_home:
                MainActivity.redirectActivity(this, MainActivity.class);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Method for redirect user to other activity.
    private static void redirectActivity(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        activity.startActivity(intent);
    }


    //When user clicks on button: btn_send create a new intent for email and start result launcher.
    public void sendMessage(View view) {
        String recipientList = mEditTextTo.getText().toString();
        String[] recipients = recipientList.split(","); //Split email adresses to single email adress in array.
        String subject = mEditTextSubject.getText().toString();
        String message = mEditTextMessage.getText().toString();

        if (!validateMessage(message)) {
            return;
        }

        for (String recipient : recipients) {
            if (!validateEmail(recipient)) {
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.createChooser(intent, "Choose an email client");
        //Open only email client.
        intent.setType("message/rfc822");
        firstActivityResultLauncher.launch(intent);

    }


    //Method for creating a popup window for sending email.
    private void showSendPopupWindow() {
        dialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        final View sendPopupWindow = getLayoutInflater().inflate(R.layout.activity_send, null);
        mEditTextTo = sendPopupWindow.findViewById(R.id.edit_text_to);
        mEditTextSubject = sendPopupWindow.findViewById(R.id.edit_text_subject);
        mEditTextMessage = sendPopupWindow.findViewById(R.id.edit_text_message);
        dialogBuilder.setView(sendPopupWindow);
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogSend = dialogBuilder.create();
        dialogSend.show();
        Button nButton = dialogSend.getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setTextColor(Color.BLACK);
    }


    //Method for checking permissions.
    private boolean checkPermissions() {
        int first = ActivityCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE);

        int second = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return first == PackageManager.PERMISSION_GRANTED && second == PackageManager.PERMISSION_GRANTED;
    }

    //Create a new intent and set type to any file.
    public void addAttachment(View view) {
        if (checkPermissions() == true) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            secondActivityResultLauncher.launch(intent);
        } else if (checkPermissions() == false) {
            getPermissions();
        } else {
            Toast.makeText(this, "Permission denied for reading file from external storage", Toast.LENGTH_SHORT).show();
        }

    }

    //Request permissions for writing & reading to external storage.
    private void getPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }


    //If the drawer is open close the navigation drawer.
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Logout user.
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }


    //Method for validating email input.
    private boolean validateEmail(String emailAdress) {
        if (!ValidateUtils.validateEmail(emailAdress)) {
            mEditTextTo.setError("Email is invalid.");
            return false;
        } else if (TextUtils.isEmpty(emailAdress)) {
            mEditTextTo.setError("Email cannot be empty.");
            return false;
        }
        return true;
    }

    //Method for validating message.
    private boolean validateMessage(String msg) {
        if (TextUtils.isEmpty(msg)) {
            mEditTextMessage.setError("Message is required");
            return false;
        }
        return true;
    }
}
