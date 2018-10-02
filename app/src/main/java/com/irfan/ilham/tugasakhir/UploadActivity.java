package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UploadActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView videotext;
    private EditText judul, deskripsi;
    private Spinner kategori;
    private Button submit;
    private Uri VideoUri;
    private ArrayList<String> kategoriString = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        videoView = findViewById(R.id.uploadVideoContent);
        videotext = findViewById(R.id.uploadtextVideoContent);
        judul = findViewById(R.id.judulUploadVideo);
        deskripsi = findViewById(R.id.DeskripsiUploadVideo);
        kategori = findViewById(R.id.KategoriUploadVideo);
        submit = findViewById(R.id.btnUploadVideo);

        DatabaseReference kategoriReference = FirebaseDatabase.getInstance().getReference("Kategori");
        kategoriReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    String kat = ds.child("namaKategori").getValue().toString();
                    kategoriString.add(kat);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this, android.R.layout.simple_spinner_dropdown_item, kategoriString);
                kategori.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UploadActivity.this, "Gagal memuat kategori : " + databaseError, Toast.LENGTH_SHORT).show();
            }
        });

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
                    final String descVideo = deskripsi.getText().toString().trim();
                    final String kategoriVideo = kategori.getSelectedItem().toString();
                    final String Tanggal = DateFormat.getDateTimeInstance().format(new Date());
                    final StorageReference VideoRef = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("videos/").child(namaVideo);

                    VideoRef.putFile(VideoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            VideoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Video").push();
                                    postRef.child("namaVideo").setValue(namaVideo);
                                    postRef.child("deskripsi").setValue(descVideo);
                                    postRef.child("tonton").setValue(0);
                                    postRef.child("rating").setValue(0.0);
                                    postRef.child("tanggal").setValue(Tanggal);
                                    postRef.child("kategori").setValue(kategoriVideo);
                                    postRef.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
