package com.example.interviewcoach;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

/**
 * ViewHolder for video.
 *
 * @author Emmy
 */

public class VideoViewHolder extends RecyclerView.ViewHolder {
    private PlayerView playerView;
    private TextView titleView;
    Button deleteVideo;
    private static final String VIDEO_TAG = "Video";
    private static final String EXOPLAYER_TAG = "Exoplayer";

    public VideoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    //Method for displaying Exoplayer with video title.
    public void prepareExoplayer(String videoUrl, String videoTitle) {
        titleView = itemView.findViewById(R.id.title_video);
        playerView = itemView.findViewById(R.id.exoplayer_view);
        deleteVideo = itemView.findViewById(R.id.btn_deleteVideo);

        titleView.setText(videoTitle);

        try {
            Uri uri = Uri.parse(videoUrl);
            ExoPlayer exoPlayer = new ExoPlayer.Builder(itemView.getContext()).build();
            playerView.setPlayer(exoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(uri);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            exoPlayer.setPlayWhenReady(false);
            Log.i(VIDEO_TAG, "VideoUrl: " + videoUrl + "VideoTitle: " + videoTitle);

        } catch (Exception e) {
            Log.d(EXOPLAYER_TAG, e.getMessage());
        }
    }
}

