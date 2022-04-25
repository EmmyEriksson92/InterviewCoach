package com.example.interviewcoach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Program that displays a chat with chatBot and user,
 * where user can get support from chatBot.
 *
 * @author Emmy
 */
public class Chat extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText editText;
    private ImageView imageView;
    private ArrayList<ChatModel> chatModelArrayList;
    private ChatAdapter chatAdapter;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";

    //Start activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.chat_recycler);
        editText = findViewById(R.id.et_messageChat);
        imageView = findViewById(R.id.message_sendChat);
        chatModelArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatModelArrayList, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        //Change title on actionBar and display home button.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chat with us direct");

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(chatAdapter);

        //When user clicks imageView for sending chat, get response from chatBot, else display error message for user.
        imageView.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                editText.setError("Message cannot be empty.");
                return;
            }
            getResponse(editText.getText().toString());
            editText.setText("");
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
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method gets response from chatBot and recycleView of chat if successful,
     * otherwise display error message in log and for user.
     */
    private void getResponse(String message) {
        chatModelArrayList.add(new ChatModel(message, USER_KEY));
        chatAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=160991&key=l4ETjguQ7i0XFCD4&uid=[uid]&msg=" + message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetroFitApi retroFitApi = retrofit.create(RetroFitApi.class);
        Call<MsgModel> call = retroFitApi.getMessage(url);
        call.enqueue(new Callback<MsgModel>() {
            @Override
            public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                if (response.isSuccessful()) {
                    MsgModel msgModel = response.body();
                    chatModelArrayList.add(new ChatModel(msgModel.getCnt(), BOT_KEY));
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(chatModelArrayList.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<MsgModel> call, Throwable t) {
                Log.e("Error", "Response not successFull: " + t.getMessage());
                chatModelArrayList.add(new ChatModel("No response: " + t.getMessage(), BOT_KEY));
                chatAdapter.notifyDataSetChanged();
            }
        });

    }


}
