package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VideoViewActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView nama;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        videoView = findViewById(R.id.VideoView);
        nama = findViewById(R.id.namaVideoView);
        String url = getIntent().getExtras().getString("id_video");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Video").child(url);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String namavideo = dataSnapshot.child("namaVideo").getValue().toString();
                String urlVideo = dataSnapshot.child("videoUrl").getValue().toString();
                nama.setText(namavideo);
                videoView.setVideoURI(Uri.parse(urlVideo));
                MediaController mediaController = new MediaController(VideoViewActivity.this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.requestFocus();
                videoView.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VideoViewActivity.this, "Gagal memuat data : " + databaseError, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }
}
