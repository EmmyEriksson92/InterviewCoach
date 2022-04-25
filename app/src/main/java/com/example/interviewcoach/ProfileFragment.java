package com.example.interviewcoach;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * Program for displaying user profile with name, email & profile picture saved in firebase.
 *
 * @author Emmy
 */
public class ProfileFragment extends Fragment {
    private TextView name, email, nameTop;
    private Button logout, changeProfile, showVideos;
    private ShapeableImageView imageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String userId;
    private StorageReference storageReference;
    private ButtonClickListener onClickBtnListener;

    //Inflate the fragment layout fragment_profile.
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View profile = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = profile.findViewById(R.id.iv_profile);
        name = profile.findViewById(R.id.profileName);
        email = profile.findViewById(R.id.profileEmail);
        nameTop = profile.findViewById(R.id.profileNameTop);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();
        imageView = profile.findViewById(R.id.iv_profile);
        logout = profile.findViewById(R.id.profile_logout);
        changeProfile = profile.findViewById(R.id.profile_change);
        showVideos = profile.findViewById(R.id.btn_showVideos);
        onClickBtnListener = new ButtonClickListener();

        //Set onClick listener.
        logout.setOnClickListener(onClickBtnListener);
        changeProfile.setOnClickListener(onClickBtnListener);
        showVideos.setOnClickListener(onClickBtnListener);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Profile");

        //If user  has an profile image display the image in imageView.
        StorageReference profileRef = storageReference.child("profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(imageView));

        //Get user data that is saved in firebase collection and display in profile.
        DocumentReference documentReference = firestore.collection("users").document(userId);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            name.setText(documentSnapshot.getString("fName"));
            nameTop.setText(documentSnapshot.getString("fName"));
            email.setText(documentSnapshot.getString("email"));
        });
        return profile;
    }

    /**
     * this event will enable the back
     * function to the button on press
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Logout user when user click on button: profile_logout.
    private void logout() {
        if (firebaseAuth.getCurrentUser() != null)
            FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), Login.class));
    }

    //Redirect to page for changing profile when clicking on button: profile_change.
    private void changeProfile() {
        Intent intent = new Intent(getContext(), ChangeProfile.class);
        startActivity(intent);
    }

    //Redirect to page for showing all saved videos when clicking on button: btn_showVideos.
    private void showVideos() {
        Intent intent = new Intent(getContext(), ShowVideos.class);
        startActivity(intent);
    }

    //When user clicks on logout, Change profile & Show videos activity starts for every selection.
    public class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.profile_logout:
                    logout();
                    break;
                case R.id.profile_change:
                    changeProfile();
                    break;
                case R.id.btn_showVideos:
                    showVideos();
                    break;
            }
        }

    }
}
