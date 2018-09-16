package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView videotext;
    private EditText judul;
    private Button submit;
    private Uri VideoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        videoView = findViewById(R.id.uploadVideoContent);
        videotext = findViewById(R.id.uploadtextVideoContent);
        judul = findViewById(R.id.judulUploadVideo);
        submit = findViewById(R.id.btnUploadVideo);

        videotext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UploadActivity.this, "blabla", Toast.LENGTH_LONG).show();
                Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent, "Pilih Video"), 89);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.isPlaying()) {
                    final String namaVideo = judul.getText().toString().trim();
                    final StorageReference VideoRef = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("videos/").child(namaVideo);

                    VideoRef.putFile(VideoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            VideoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Video").push();
                                    postRef.child("namaVideo").setValue(namaVideo);
                                    postRef.child("videoUrl").setValue(uri.toString());
                                    Toast.makeText(UploadActivity.this, "upload success", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(UploadActivity.this, HomeActivity.class));
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 89) {
                Uri select = data.getData();
                VideoUri = select;
                videoView.setVideoURI(select);
                videoView.requestFocus();
                videoView.start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }

}
