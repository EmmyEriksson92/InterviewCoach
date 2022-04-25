package com.example.interviewcoach;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Program which displays a Listview with several interview questions for practice purpose.
 * When user clicks on an List item an new activity starts where user can answer the selected question.
 *
 * @author Emmy
 */
public class QuestionsFragment extends Fragment {
    private ListView listView;
    private ArrayList<String> listItems;
    private EditText editText;
    private CustomBaseAdapterListviewQuestions customBaseAdapterListview;

    //Inflate the fragment layout fragment_questions.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View questions = inflater.inflate(R.layout.fragment_questions, container, false);
        listView = questions.findViewById(R.id.lv_questions);
        listItems = new ArrayList<>();
        listItems.add("What causes teams to fail most often?");
        listItems.add("What do you do when you feel that a task is getting out of hand?");
        listItems.add("Tell me about a situation where you have to handle a delicate personal situation");
        listItems.add("Tell me about a situation where you have to handle a toxic member");
        listItems.add("Have you been in a situation where you didn't have enough work to do?");

        customBaseAdapterListview = new CustomBaseAdapterListviewQuestions(getContext(), listItems);
        listView.setAdapter(customBaseAdapterListview);
        //Start a new activity when user clicks on item.
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), AnswerQuestion.class);
            TextView tv = view.findViewById(R.id.tv_custom_list);
            intent.putExtra("selected", tv.getText().toString());
            startActivity(intent);
        });
        return questions;
    }
}
