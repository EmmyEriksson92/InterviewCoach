package com.example.interviewcoach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
/**
 * ChatAdapter for chatBot.
 * @author Emmy
 */
public class ChatAdapter extends RecyclerView.Adapter {
    private ArrayList<ChatModel> chatModelArrayList;
    private Context context;

    public ChatAdapter(ArrayList<ChatModel> chatModelArrayList, Context context) {
        this.chatModelArrayList = chatModelArrayList;
        this.context = context;
    }

    //Inflate view.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_holder, parent, false);
                return new UserViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_reply, parent, false);
                return new BotViewHolder(view);
        }
        return null;
    }

    //Method sets text for user message & Bot message.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel chatModel = chatModelArrayList.get(position);
        switch (chatModelArrayList.get(position).getSender()) {
            case "user":
                ((UserViewHolder) holder).userMsg.setText(chatModel.getMessage());
                break;
            case "bot":
                ((BotViewHolder) holder).botReply.setText(chatModel.getMessage());
                break;
        }
    }

    //Metod Returns total count for item arrayList.
    @Override
    public int getItemCount() {
        return chatModelArrayList.size();
    }

    //Method gets item view type.
    @Override
    public int getItemViewType(int position) {
        switch (chatModelArrayList.get(position).getSender()) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }
    //ViewHolder for user message.
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userMsg;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userMsg = itemView.findViewById(R.id.userMsg);

        }
    }

    //ViewHolder for Bot message.
    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView botReply;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            botReply = itemView.findViewById(R.id.tv_response);
        }
    }
}
