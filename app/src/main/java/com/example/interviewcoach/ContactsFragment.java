package com.example.interviewcoach;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Program for displaying users contacts & function for calling contacts when clicking on respective contact.
 *
 * @author Emmy
 */
public class ContactsFragment extends Fragment {
    public static final String[] FIELDS = {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
    };

    private static final int NAME_INDEX = 0;
    private static final int NUMBER_INDEX = 1;
    private static final int REQUEST_CALL = 1;
    private static final int REQUEST_CONTACTS = 0;
    private ListView listView;
    private TextView view_empty, nameCall, numberCall, headerCall;
    private List<Contact> contactsList = new ArrayList<>();
    private List<String> contactNames = new ArrayList<>();
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialogContacts;
    private View callPopupWindow;
    private Button callContact;
    private String phoneNumber;


    //Inflate the fragment layout fragment_contacts.
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contacts = inflater.inflate(R.layout.fragment_contacts, container, false);
        listView = contacts.findViewById(R.id.listView);
        view_empty = contacts.findViewById(R.id.view_empty);
        listView.setEmptyView(view_empty);
        callPopupWindow = getLayoutInflater().inflate(R.layout.layout_call, null);
        nameCall = callPopupWindow.findViewById(R.id.tv_name);
        numberCall = callPopupWindow.findViewById(R.id.tv_number);
        headerCall = callPopupWindow.findViewById(R.id.tv_contacth1);
        callContact = callPopupWindow.findViewById(R.id.btn_callContact);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Contacts");

        if (checkPermissions() == false) {
            getPermissions();
        }
        getContacts();
        callContact();


        if (contactsList != null && contactNames != null) {
            final ArrayAdapter adapterContactNames = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, contactNames);
            listView.setAdapter(adapterContactNames);
            //When user clicks on listView show Dialog for confirming deletion of clicked item.
            listView.setOnItemClickListener((parent, view, position, id) -> {
                showCallPopupWindow();
                String contact = parent.getItemAtPosition(position).toString();
                headerCall.setText("Contact: " + contact);
                for (Contact c : contactsList) {
                    if (c.getName().equals(contact)) {
                        phoneNumber = c.getNumber();
                        nameCall.setText(contact);
                        numberCall.setText(phoneNumber);
                    }
                }
            });

        }
        return contacts;

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

    //Method for calling contact when clicking on button: btn_callContact.
    private void callContact() {
        callContact.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        });
    }

    //Method for getting contacts from users device and adding do ArrayList.
    private void getContacts() {
        ContentResolver contentResolver = getContext().getContentResolver();
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, FIELDS, null, null, sort);

        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "No Data Exists", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                contact.setNumber(cursor.getString(NUMBER_INDEX));
                contact.setName(cursor.getString(NAME_INDEX));
                contactsList.add(contact);
                contactNames.add(cursor.getString(NAME_INDEX));
            }

        }
        cursor.close();
    }

    //Method for checking permissions.
    private boolean checkPermissions() {
        int permission1 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        int permission2 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    //Method for getting permissions.
    private void getPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, REQUEST_CONTACTS);
    }


    //Method for showing popup window for clicked contact in ListView.
    private void showCallPopupWindow() {
        dialogBuilder = new AlertDialog.Builder(getContext());
        if (callPopupWindow.getParent() != null) {
            ((ViewGroup) callPopupWindow.getParent()).removeView(callPopupWindow); //Remove view if already created.
        }

        dialogBuilder.setView(callPopupWindow);
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogContacts = dialogBuilder.create();
        dialogContacts.show();
        Button nButton = dialogContacts.getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setTextColor(Color.BLACK);
    }

}
