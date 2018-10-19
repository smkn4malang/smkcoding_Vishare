package com.irfan.ilham.tugasakhir;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManageUserActivity extends AppCompatActivity {

    private RecyclerView listUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        listUser = findViewById(R.id.manageUserRecycleView);
        listUser.setHasFixedSize(true);
        listUser.setLayoutManager(new LinearLayoutManager(ManageUserActivity.this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseRecyclerAdapter<UserIItems, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserIItems, ViewHolder>(UserIItems.class, R.layout.item_user_manager, ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, UserIItems model, final int position) {
                viewHolder.setNama(model.getNama());
                viewHolder.setAsal(model.getAsal());
                viewHolder.setImg(model.getImgUrl());
                final String nama = model.getNama();
                final String UID = getRef(position).getKey();
                final int[] subs = new int[1];
                final int[] videoUser = new int[1];

                FirebaseDatabase.getInstance().getReference("Users").child(UID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("Subscriber")) {
                            subs[0] = (int) dataSnapshot.child("Subscriber").getChildrenCount();
                            viewHolder.setFollow(subs[0]);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                FirebaseDatabase.getInstance().getReference("Video").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        videoUser[0] = 0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.hasChild("UID")) {
                                if (ds.child("UID").getValue().toString().equals(UID)) {
                                    videoUser[0] += 1;
                                }
                            }
                        }

                        viewHolder.setVideo(videoUser[0]);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageUserActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                        LayoutInflater layoutInflater = LayoutInflater.from(ManageUserActivity.this);
                        builder.setTitle("Delete!");
                        builder.setMessage("Are you sure to delete " + nama + " ?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference("Users").child(UID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ManageUserActivity.this, "User berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                FirebaseDatabase.getInstance().getReference("Video").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds1: dataSnapshot.getChildren()) {
                                            if (ds1.hasChild("komentar")) {
                                                for (DataSnapshot dsKomen: ds1.child("komentar").getChildren()) {
                                                    if (dsKomen.child("UID").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                        dsKomen.getRef().removeValue();
                                                    }
                                                }
                                            }

                                            if (ds1.hasChild("likedislike")) {
                                                for (DataSnapshot dsLike: ds1.child("likedislike").getChildren()) {
                                                    if (dsLike.getKey().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                        dsLike.getRef().removeValue();
                                                    }
                                                }
                                            }

                                            if (ds1.hasChild("UID")) {
                                                if (ds1.child("UID").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                    ds1.getRef().removeValue();
                                                }
                                            }

                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return false;
                    }
                });

            }
        };
        listUser.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNama(String nama) {
            TextView nama_view = mView.findViewById(R.id.usernameItemUser);
            nama_view.setText(nama);
        }

        public void setAsal(String asal) {
            TextView asal_view = mView.findViewById(R.id.asalItemUser);
            asal_view.setText(asal);
        }

        public void setImg(String img) {
            ImageView img_view = mView.findViewById(R.id.profileItemUser);
            Glide.with(img_view.getContext()).load(img).override(100, 100).into(img_view);
        }

        public void setFollow(int i) {
            TextView view = mView.findViewById(R.id.pengikutItemUser);
            view.setText(i + "");
        }

        public void setVideo(int i) {
            TextView view = mView.findViewById(R.id.videoItemUser);
            view.setText(i + "");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AdminHomeActivity.class));
        finish();
    }
}