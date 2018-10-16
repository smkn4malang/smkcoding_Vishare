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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UploadActivity extends AppCompatActivity {

    private VideoView videoView;
    private LinearLayout videotext, btnGroup;
    private ImageView imageView;
    private EditText judul, deskripsi;
    private Spinner kategori;
    private ProgressBar myProgress;
    private Button submit, pause, cancel;
    private Uri VideoUri, imgLink;
    private ArrayList<String> kategoriString = new ArrayList<String>();
    private double VideoProgress, ImageProgress, TotalProgress;
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
        imageView = findViewById(R.id.ThumbnailUploadVideo);
        pause = findViewById(R.id.btnPauseVideo);
        cancel = findViewById(R.id.btnCancelVideo);
        myProgress = findViewById(R.id.progresUploadVideo);
        btnGroup = findViewById(R.id.btnGroupUploadVideo);

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
                Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent, "Pilih Video"), 89);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(16,9)
                        .start(UploadActivity.this);
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
                if (videoView.isPlaying() && !imgLink.equals(null)) {
                    final String namaVideo = judul.getText().toString().trim();
                    final String descVideo = deskripsi.getText().toString().trim();
                    final String kategoriVideo = kategori.getSelectedItem().toString();
                    final String Tanggal = DateFormat.getDateTimeInstance().format(new Date());
                    final StorageReference VideoRef = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("videos/");

                    final UploadTask UploadVideo = VideoRef.child(namaVideo).putFile(VideoUri);
                    final UploadTask UploadImage = VideoRef.child(namaVideo + "Thumbnail").putFile(imgLink);

                    submit.setVisibility(View.GONE);
                    btnGroup.setVisibility(View.VISIBLE);
                    myProgress.setVisibility(View.VISIBLE);

                    UploadVideo.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100;
                            VideoProgress = progress;
                            pause.setText("Jeda");
                            TotalProgress = (VideoProgress + ImageProgress) / 2;
                            myProgress.setProgress((int) TotalProgress);

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UploadVideo.cancel();
                                    UploadImage.cancel();
                                    submit.setVisibility(View.VISIBLE);
                                    btnGroup.setVisibility(View.GONE);
                                    myProgress.setVisibility(View.INVISIBLE);
                                }
                            });

                            pause.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UploadVideo.pause();
                                    UploadImage.pause();
                                }
                            });
                        }
                    });

                    UploadVideo.addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            pause.setText("Lanjutkan");
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UploadVideo.cancel();
                                    UploadImage.cancel();
                                    submit.setVisibility(View.VISIBLE);
                                    btnGroup.setVisibility(View.GONE);
                                    myProgress.setVisibility(View.INVISIBLE);
                                }
                            });

                            pause.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UploadVideo.resume();
                                    UploadImage.resume();
                                }
                            });
                        }
                    });

                    UploadImage.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100;
                            ImageProgress = progress;
                            pause.setText("Jeda");
                            TotalProgress = (VideoProgress + ImageProgress) / 2;
                            myProgress.setProgress((int) TotalProgress);

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UploadVideo.cancel();
                                    UploadImage.cancel();
                                }
                            });

                            pause.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UploadVideo.pause();
                                    UploadImage.pause();
                                }
                            });
                        }
                    });

                    UploadImage.addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            pause.setText("Lanjutkan");
                            pause.setText("Lanjutkan");
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UploadVideo.cancel();
                                    UploadImage.cancel();
                                }
                            });

                            pause.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UploadVideo.resume();
                                    UploadImage.resume();
                                }
                            });
                        }
                    });

                    UploadVideo.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            UploadImage.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    VideoRef.child(namaVideo).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri uri) {
                                            VideoRef.child(namaVideo + "Thumbnail").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri2) {
                                                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Video").push();
                                                    postRef.child("namaVideo").setValue(namaVideo);
                                                    postRef.child("deskripsi").setValue(descVideo);
                                                    postRef.child("tonton").setValue(0);
                                                    postRef.child("rating").setValue(0.0);
                                                    postRef.child("tanggal").setValue(Tanggal);
                                                    postRef.child("kategori").setValue(kategoriVideo);
                                                    postRef.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    postRef.child("videoUrl").setValue(uri.toString());
                                                    postRef.child("thumbnailUrl").setValue(uri2.toString());
                                                    Toast.makeText(UploadActivity.this, "upload success", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(UploadActivity.this, HomeActivity.class));
                                                    finish();
                                                }
                                            });
                                        }
                                    });
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
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri selectedImg = result.getUri();
                imgLink = selectedImg;
                imageView.setImageURI(selectedImg);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(UploadActivity.this, "Eroor : " + error.toString(), Toast.LENGTH_SHORT).show();
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
