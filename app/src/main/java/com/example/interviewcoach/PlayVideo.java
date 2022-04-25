package com.example.interviewcoach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

/**
 * Program for playing & displaying selected Youtube video in YoutubePlayerView.
 *
 * @author Emmy
 */
public class PlayVideo extends AppCompatActivity {
    private YouTubePlayerView youTubePlayerView;
    private TextView textView;
    private Intent intent;

    //Start activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        youTubePlayerView = findViewById(R.id.videoView_youtube);
        textView = findViewById(R.id.tv_youtubeView);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.enterFullScreen();
        intent = getIntent();
        String selectedName = intent.getStringExtra("clicked_video_name");
        textView.setText(selectedName);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.holo_orange_dark));

        //Add listener onReady to YoutubePlayerView.
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                String selected = intent.getStringExtra("clicked_video");
                youTubePlayer.loadVideo(selected, 0); //Pass video id & starting point which is set to 0.

            }
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
}
