package com.example.interviewcoach;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Program for support where user can click to chat with chatBot or send email to support.
 * @author Emmy
 */
public class SupportFragment extends Fragment {
    private Button btnChat, btnEmail;
    private TextView tvSupport;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String userId;

    //Inflate the fragment layout fragment_support.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View support = inflater.inflate(R.layout.fragment_support, container, false);
        btnChat = support.findViewById(R.id.btn_chatSupport);
        btnEmail = support.findViewById(R.id.btn_emailSupport);
        tvSupport = support.findViewById(R.id.tv_support);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Support");

        //Get user data that is saved in firebase collection and display profile name.
        DocumentReference documentReference = firestore.collection("users").document(userId);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            tvSupport.setText("What can we help you with, " );
            tvSupport.append(documentSnapshot.getString("fName") + "?");
        });
        //When user clicks on button: btn_chatSupport create a new Intent which redirect user to chat with ChatBot.
        btnChat.setOnClickListener(v -> {
            Intent intentChat = new Intent(getActivity(), Chat.class);
            startActivity(intentChat);
        });
        //When user clicks on button: btn_emailSupport create a new Intent which redirect user to class for handling Email customer support.
        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ContactUs.class);
            startActivity(intent);
        });
        return support;
    }


}
