package com.irfan.ilham.tugasakhir;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoViewActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView nama, tonton, komentar, uploader, deskripsi, dislikeCount, likeCount, tanggal, subscribe, subscribeCount, komentarBtn, KategoriView;
    private RatingBar rating;
    private int tontonVideo, like, dislike;
    private String urlVideo, urlImage;
    private LinearLayout EditGroup;
    private ImageView btnEdit, btnDelete;
    private RadioGroup radioGroup;
    private RadioButton dislikeRadio, likeRadio;
    private String urlDatabase, UploaderUID, UUID;
    private RecyclerView listkomentar;
    private CircleImageView uploaderImg;
    private DatabaseReference mDatabase;
    private boolean rat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        videoView = findViewById(R.id.VideoView);
        nama = findViewById(R.id.namaVideoView);
        tonton = findViewById(R.id.tontonVideoView);
        rating = findViewById(R.id.ratingVideoView);
        komentar = findViewById(R.id.tambahKomentarVideoView);
        listkomentar = findViewById(R.id.KomentarRecycleView);
        uploader = findViewById(R.id.namaUploaderVideoView);
        deskripsi = findViewById(R.id.DescVideoView);
        dislikeCount = findViewById(R.id.DislikeCountVideoView);
        likeCount = findViewById(R.id.LikeCountVideoView);
        radioGroup = findViewById(R.id.RadioGroupVideoView);
        dislikeRadio = findViewById(R.id.DislikeVideoView);
        likeRadio = findViewById(R.id.LikeVideoView);
        tanggal = findViewById(R.id.TanggalVideoView);
        subscribe = findViewById(R.id.SubscribeVideoView);
        subscribeCount = findViewById(R.id.SubscribeCountVideoView);
        komentarBtn = findViewById(R.id.KomenBtn);
        KategoriView = findViewById(R.id.KategoriVideoView);
        EditGroup = findViewById(R.id.EditVideoViewGroup);
        btnEdit = findViewById(R.id.EditVideoView);
        btnDelete = findViewById(R.id.DeleteVideoView);
        uploaderImg = findViewById(R.id.UploaderImgVideoView);

        listkomentar.setHasFixedSize(true);
        listkomentar.setLayoutManager(new LinearLayoutManager(VideoViewActivity.this));
        urlDatabase = getIntent().getExtras().getString("id_video");

        View bottomSheet = findViewById(R.id.bottomsheet_komentarVideoView);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);

        komentarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        FirebaseDatabase.getInstance().getReference("Video").child(urlDatabase).child("likedislike").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                like = 0;
                dislike = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String value = ds.child("value").getValue().toString();
                    String key = ds.getKey();
                    if (value.equals("like")) {
                        like += 1;
                    } else if (value.equals("dislike")) {
                        dislike += 1;
                    }

                    if (key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        if (value.equals("like")) {
                            likeRadio.setChecked(true);
                        } else if (value.equals("dislike")) {
                            dislikeRadio.setChecked(true);
                        }
                    }
                }

                int total = like + dislike;
                double rating = 0;

                for (int i = 0; i <= 100; i += 5) {
                    float batas = total * i / 100;
                    if (like <= batas) {
                        rating = i / 20.0;
                        break;
                    }
                }

                final double finalRating = rating;

                if (rat) {
                    FirebaseDatabase.getInstance().getReference("Video").child(urlDatabase).child("rating").setValue(finalRating);
                }

                likeCount.setText(like + " like");
                dislikeCount.setText(dislike + " dislike");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (likeRadio.isChecked()) {
                        FirebaseDatabase.getInstance().getReference("Video").child(urlDatabase).child("likedislike").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("value").setValue("like");
                    } else if (dislikeRadio.isChecked()) {
                        FirebaseDatabase.getInstance().getReference("Video").child(urlDatabase).child("likedislike").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("value").setValue("dislike");
                    }
                }
            });
        }

        komentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(VideoViewActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                    LayoutInflater layoutInflater = LayoutInflater.from(VideoViewActivity.this);
                    final View myView = layoutInflater.inflate(R.layout.dialog_add_komentar, null);

                    builder.setView(myView);
                    builder.setPositiveButton("Unggah", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String komentar;
                            EditText komInput = myView.findViewById(R.id.addKomentarDialog);
                            komentar = komInput.getText().toString().trim();
                            DatabaseReference komen = FirebaseDatabase.getInstance().getReference("Video").child(urlDatabase).child("komentar").push();
                            komen.child("komentar").setValue(komentar);
                            komen.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }
                    });
                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    startActivity(new Intent(VideoViewActivity.this, ChooseActivity.class));
                }
            }
        });

        MediaController mediaController = new MediaController(VideoViewActivity.this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.requestFocus();


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Video").child(urlDatabase);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                databaseReference.child("tonton").setValue(tontonVideo + 1);
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("namaVideo")) {
                    rat = true;
                    String namavideo = dataSnapshot.child("namaVideo").getValue().toString();
                    urlVideo = dataSnapshot.child("videoUrl").getValue().toString();
                    urlImage = dataSnapshot.child("thumbnailUrl").getValue().toString();
                    tontonVideo = Integer.parseInt(dataSnapshot.child("tonton").getValue().toString());
                    float ratingVideo = Float.parseFloat(dataSnapshot.child("rating").getValue().toString());
                    String desc = dataSnapshot.child("deskripsi").getValue().toString();
                    UUID = dataSnapshot.child("UID").getValue().toString();
                    String Tanggal = dataSnapshot.child("tanggal").getValue().toString();
                    String Kategori = dataSnapshot.child("kategori").getValue().toString();
                    FirebaseDatabase.getInstance().getReference("Users").child(UUID).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String nama = dataSnapshot.child("nama").getValue().toString();
                            String urlImg = dataSnapshot.child("imgUrl").getValue().toString();
                            Glide.with(uploaderImg.getContext()).load(urlImg).override(100, 100).into(uploaderImg);
                            uploader.setText(nama);

                            if (!UUID.isEmpty()) {
                                if (UUID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    subscribe.setVisibility(View.GONE);
                                    EditGroup.setVisibility(View.VISIBLE);

                                } else {
                                    subscribe.setVisibility(View.VISIBLE);
                                    EditGroup.setVisibility(View.GONE);
                                }
                            }

                            int subs = (int) dataSnapshot.child("Subscriber").getChildrenCount();
                            subscribeCount.setText(subs + " pengikut");


                            if (dataSnapshot.child("Subscriber").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChildren()) {
                                if (dataSnapshot.child("Subscriber").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("value").getValue().toString().equals("subcribed")) {
                                    subscribe.setText("Diikuti");
                                }
                            } else {
                                subscribe.setText("Ikuti");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    KategoriView.setText(Kategori);
                    nama.setText(namavideo);
                    tonton.setText(tontonVideo + "x ditonton");
                    tanggal.setText(Tanggal);
                    rating.setRating(ratingVideo);
                    deskripsi.setText(desc);
                    videoView.setVideoURI(Uri.parse(urlVideo));
                    videoView.start();
                } else {
                    rat = false;
                }
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
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference("Video").child(urlDatabase).child("komentar");
        FirebaseRecyclerAdapter<KomentarItems, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<KomentarItems, ViewHolder>(KomentarItems.class, R.layout.item_komentar, VideoViewActivity.ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, final KomentarItems model, final int position) {
                viewHolder.setKomentar(model.getKomentar());
                UploaderUID = model.getUID();

                viewHolder.hapus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(VideoViewActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                        LayoutInflater layoutInflater = LayoutInflater.from(VideoViewActivity.this);
                        builder.setTitle("Hapus Komentar!");
                        builder.setMessage("Apakah anda yakin menghapus komentar ini ?");

                        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (getRef(position) != null) {
                                    getRef(position).removeValue();
                                }
                            }
                        });
                        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

                viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(VideoViewActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                        LayoutInflater layoutInflater = LayoutInflater.from(VideoViewActivity.this);
                        final View myView = layoutInflater.inflate(R.layout.dialog_add_komentar, null);
                        TextView tv = myView.findViewById(R.id.addKomentarDialog);
                        tv.setText(model.getKomentar());

                        builder.setView(myView);
                        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String namaKategori;
                                EditText namaInput = myView.findViewById(R.id.addKomentarDialog);
                                namaKategori = namaInput.getText().toString().trim();
                                getRef(position).child("komentar").setValue(namaKategori);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

                if (model.getUID() != null) {

                    if (UploaderUID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        LinearLayout ll = viewHolder.mView.findViewById(R.id.komenDetail);
                        ll.setVisibility(View.VISIBLE);
                    } else {
                        LinearLayout ll = viewHolder.mView.findViewById(R.id.komenDetail);
                        ll.setVisibility(View.GONE);
                    }

                    FirebaseDatabase.getInstance().getReference("Users").child(UploaderUID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String nama = dataSnapshot.child("nama").getValue().toString();
                            String ImgUrl = dataSnapshot.child("imgUrl").getValue().toString();
                            viewHolder.setProfile(ImgUrl);
                            viewHolder.setNama(nama);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        listkomentar.setAdapter(firebaseRecyclerAdapter);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VideoViewActivity.this, EditVideoActivity.class);
                i.putExtra("videoKey", urlDatabase);
                startActivity(i);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VideoViewActivity.this, "Tunggu sebentar...", Toast.LENGTH_SHORT).show();
                StorageReference sr = FirebaseStorage.getInstance().getReferenceFromUrl(urlVideo);
                sr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseStorage.getInstance().getReferenceFromUrl(urlImage).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference("Video").child(urlDatabase).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(VideoViewActivity.this, HomeActivity.class));
                                        Toast.makeText(VideoViewActivity.this, "Video Berhasil di hapus", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VideoViewActivity.this, "Video Gagal Di Hapus!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        subscribe.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (subscribe.getText().toString().equals("Diikuti")) {
                    subscribe.setText("Ikuti");
                    FirebaseDatabase.getInstance().getReference("Users").child(UUID).child("Subscriber").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("value").removeValue();
                } else {
                    subscribe.setText("Diikuti");
                    FirebaseDatabase.getInstance().getReference("Users").child(UUID).child("Subscriber").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("value").setValue("subcribed");
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView hapus, edit;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            hapus = mView.findViewById(R.id.hapusKomen);
            edit = mView.findViewById(R.id.editKomen);

        }

        public void setKomentar(String nama) {
            TextView nama_view = mView.findViewById(R.id.Komentar);
            nama_view.setText(nama);
        }

        public void setNama(String nama) {
            TextView nama_view = mView.findViewById(R.id.namaKomentar);
            nama_view.setText(nama);
        }

        public void setProfile(String image) {
            CircleImageView profile = mView.findViewById(R.id.profileKomentar);
            Glide.with(profile.getContext()).load(image).override(100, 100).into(profile);
        }
    }
}
