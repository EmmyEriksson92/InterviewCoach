package com.example.interviewcoach;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Program which shows several tips videos for interview which are displayed when clicking on item in ListView.
 *
 * @author Emmy
 */
public class TipsFragment extends Fragment {
    private ListView listView;
    private ArrayList<String> listString;
    private ArrayList<String> iconsArrayList;
    private String id1, id2, id3, id4, id5;

    //Inflate view fragment_tips.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tips = inflater.inflate(R.layout.fragment_tips, container, false);
        listView = tips.findViewById(R.id.lv_tips);
        listString = new ArrayList<>();
        iconsArrayList = new ArrayList<>();
        populateStringList();

        id1 = "enD8mK9Zvwo";
        id2 = "lKCTS9dY4h4";
        id3 = "7TH1upXDyLE";
        id4 = "8HU2VKH8HJc";
        id5 = "p-enrCZDvD0";

        populateIconsList();

        CustomBaseAdapterListview customBaseAdapterListview = new CustomBaseAdapterListview(getContext(), listString, iconsArrayList);
        listView.setAdapter(customBaseAdapterListview);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = listString.get(position);
            String selectedId = "";
            switch (position) {
                case 0:
                    selectedId = id1;
                    break;
                case 1:
                    selectedId = id2;
                    break;
                case 2:
                    selectedId = id3;
                    break;
                case 3:
                    selectedId = id4;
                    break;
                case 4:
                    selectedId = id5;
                    break;
            }
            Intent intent = new Intent(getActivity(), PlayVideo.class);
            intent.putExtra("clicked_video", selectedId);
            intent.putExtra("clicked_video_name", selectedName);
            startActivity(intent);

        });

        return tips;
    }

    //Method for population ListString with questions.
    private void populateStringList() {
        listString.add("Preparing for a job interview");
        listString.add("Be confident in a job interview");
        listString.add("Etiquette rules during a job interview");
        listString.add("Ask your own questions");
        listString.add("Bring doubles of everything to a job interview");
    }

    //Method for populating iconsArrayList with Youtube links.
    private void populateIconsList() {
        String path1 = "https://img.youtube.com/vi/" + id1 + "/0.jpg";
        String path2 = "https://img.youtube.com/vi/" + id2 + "/0.jpg";
        String path3 = "https://img.youtube.com/vi/" + id3 + "/0.jpg";
        String path4 = "https://img.youtube.com/vi/" + id4 + "/0.jpg";
        String path5 = "https://img.youtube.com/vi/" + id5 + "/0.jpg";
        iconsArrayList.add(path1);
        iconsArrayList.add(path2);
        iconsArrayList.add(path3);
        iconsArrayList.add(path4);
        iconsArrayList.add(path5);
    }
}
