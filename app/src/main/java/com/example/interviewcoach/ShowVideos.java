package com.example.interviewcoach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Program for displaying recorded videos saved in firebase to user in a recycleView.
 * and profile picture by taking new picture or selecting from gallery and save data to firebase.
 *
 * @author Emmy
 */
public class ShowVideos extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private FirebaseRecyclerAdapter<VideoItem, VideoViewHolder> firebaseRecyclerAdapter;
    private String url;

    //Start activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_videos);
        recyclerView = findViewById(R.id.listVideoRecycler);
        databaseReference = FirebaseDatabase.getInstance("https://interviewcoach-default-rtdb.europe-west1.firebasedatabase.app").getReference("Videos");
        storage = FirebaseStorage.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.holo_orange_dark));

    }

    //When activity is visible for user display Video.
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<VideoItem> options = new FirebaseRecyclerOptions.Builder<VideoItem>().setQuery(databaseReference, VideoItem.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<VideoItem, VideoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VideoViewHolder holder, int position, @NonNull VideoItem model) {
                holder.prepareExoplayer(model.getUrl(), model.getTitle());
                holder.deleteVideo.setOnClickListener((view) -> {
                    url = model.getUrl();
                    showDeleteDialog(url);
                });

            }

            @NonNull
            @Override
            public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
                return new VideoViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    //When activity is not visible for user stop listening to firebase.
    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();

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

    //Show AlertDialog to confirm deletion of video.
    private void showDeleteDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowVideos.this);
        builder.setTitle("Delete video");
        builder.setMessage("Are you sure you want to delete this video?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            StorageReference storageReference = storage.getReferenceFromUrl(url);
            storageReference.delete();
            Query query = databaseReference.orderByChild("url").equalTo(url);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ds.getRef().removeValue();
                    }
                    Toast.makeText(ShowVideos.this, "Video deleted!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }).setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

