package com.example.interviewcoach;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Program where user can practice on interview by recording video and practice different questions showed in app.
 * Functions included are sharing video & saving video to firebase.
 *
 * @author Emmy
 */
public class PracticeFragment extends Fragment {
    private Slider slider;
    private TextView tvPractice;
    private EditText videoTitle;
    private Button btnVideo, btnShare, btnSave;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private static int CAMERA_PERMISSION_CODE = 100;
    private static int RESULT_OK = -1;
    private ExoPlayer exoPlayer;
    private PlayerView playerView;
    private MediaItem mediaItem;
    private Uri videoURI;
    private static final String TAG_CAMERA = "Video record";
    private static final String TAG_EXOPLAYER = "Exoplayer";
    private DatabaseReference root = FirebaseDatabase.getInstance("https://interviewcoach-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Videos");
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private View practice;

    // Inflate the layout for this fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        practice = inflater.inflate(R.layout.fragment_practice, container, false);
        tvPractice = practice.findViewById(R.id.tv_practice);
        videoTitle = practice.findViewById(R.id.et_titleVideo);
        videoTitle.setVisibility(View.GONE);
        btnSave = practice.findViewById(R.id.btn_saveVideo);
        btnSave.setVisibility(View.GONE);
        btnVideo = practice.findViewById(R.id.btn_video);
        btnShare = practice.findViewById(R.id.btn_share);
        btnShare.setVisibility(View.GONE);
        playerView = practice.findViewById(R.id.exoPlayer_2);
        slider = practice.findViewById(R.id.slider);
        slider.addOnChangeListener((slider, value, fromUser) -> {
            selectedQuestion(value);
        });

        //Get camera permissions if camera is present in phone, otherwise show Toast message for user.
        if (isCameraPresentInPhone()) {
            Log.i(TAG_CAMERA, "Camera Detected");
            if (!checkPermissions()) {
                getCameraPermission();
            }
        } else {
            Toast.makeText(getContext(), "No Camera is Detected", Toast.LENGTH_SHORT).show();
        }

        //When user clicks on button: btn_video record video.
        btnVideo.setOnClickListener(v -> {
            recordVideo();
        });

        //If result is not null get data from activity result and display to user in videoView.
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                videoURI = data.getData();
                try {
                    Uri uri = Uri.parse(videoURI.toString());
                    exoPlayer = new ExoPlayer.Builder(practice.getContext()).build();
                    playerView.setPlayer(exoPlayer);
                    mediaItem = MediaItem.fromUri(uri);
                    exoPlayer.setMediaItem(mediaItem);
                    exoPlayer.prepare();
                    exoPlayer.setPlayWhenReady(true);
                    Log.i("video", "VideoUrl: " + videoURI.toString() + "VideoTitle: " + videoTitle);
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

                    btnShare.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                    videoTitle.setVisibility(View.VISIBLE);
                    btnSave.setOnClickListener(v -> uploadVideo(videoURI));
                    btnShare.setOnClickListener(v -> share());

                } catch (Exception e) {
                    Log.d(TAG_EXOPLAYER, e.getMessage());
                }
            }


        });

        return practice;
    }


    //Method for getting file extension of video.
    private String getFileExt(Uri videoURI) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videoURI));
    }

    //Upload video to firebase storage.
    private void uploadVideo(Uri videoURI) {
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExt(videoURI));
        fileRef.putFile(videoURI).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(getActivity(), "Upload successful!", Toast.LENGTH_LONG).show();
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                VideoItem videoItem = new VideoItem(uri.toString(), videoTitle.getText().toString().trim());
                String videoId = root.push().getKey();//Generate random key.
                root.child(videoId).setValue(videoItem);
            });
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Upload Failed!", Toast.LENGTH_SHORT).show());
    }


    //Method for displaying selected question from slider in TextView.
    private void selectedQuestion(float value) {
        if (value == 20) {
            tvPractice.setText("Tell me something about yourself");
        } else if (value == 40) {
            tvPractice.setText("Why did you apply to this job?");
        } else if (value == 60) {
            tvPractice.setText("What is your weakness and strength?");
        } else if (value == 80) {
            tvPractice.setText("Tell me about a project that your are especially proud of");
        } else if (value == 100) {
            tvPractice.setText("Tell me about a situation where you took a big leader role, in earlier work or study?");

        }
    }

    //Method for sharing video where user can select an app.
    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, videoURI);
        intent.putExtra(Intent.EXTRA_TEXT, videoTitle.getText().toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, "Video");
        intent.setType("video/3gp");
        startActivity(intent.createChooser(intent, "Choose app to share video: "));
    }

    //Check if there is any camera present in phone.
    private boolean isCameraPresentInPhone() {
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        }
        return false;
    }

    //Method for getting camera permissions.
    private void getCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.CAMERA
        }, CAMERA_PERMISSION_CODE);
    }

    //Method for checking camera permissions.
    private boolean checkPermissions() {
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    //Create new intent and launch result of activity.
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        activityResultLauncher.launch(intent);
    }


}
