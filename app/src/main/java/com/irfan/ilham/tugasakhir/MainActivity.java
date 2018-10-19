package com.irfan.ilham.tugasakhir;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private FirebaseAuth mAuth;
    private LinearLayout adminDialog;
    private Button ya, tidak;
    private boolean admin = false, alert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        adminDialog = findViewById(R.id.AdminAlerSplash);
        ya = findViewById(R.id.AdminAlertIyaSplash);
        tidak = findViewById(R.id.AdminAlerTidakSplash);
        iv = findViewById(R.id.logo);
        Animation alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        iv.startAnimation(alphaAnim);
        final Intent i = new Intent(this, ChooseActivity.class);
        final Intent j = new Intent(this, HomeActivity.class);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    ConnectivityManager activeNetworkInfo = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = activeNetworkInfo.getActiveNetworkInfo();
                    boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.splashlayout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });

                    if (isConnected) {
                        if (mAuth.getCurrentUser() != null) {
                            LogIn();
                        } else {
                            startActivity(i);
                            finish();
                        }
                        snackbar.dismiss();
                    } else {
                        snackbar.show();
                    }
                }
            }
        };
        timer.start();

    }

    private void LogIn() {
        final DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Admin");

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    Toast.makeText(MainActivity.this, "Log In Berhasil", Toast.LENGTH_LONG).show();
                    if (key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        adminDialog.setVisibility(View.VISIBLE);
                        alert = true;
                        ya.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                admin = true;
                                startActivity(new Intent(MainActivity.this, AdminHomeActivity.class));
                                finish();
                            }
                        });
                        tidak.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                admin = false;
                            }
                        });

                    }
                }
                if (!admin && !alert) {
                    FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                Toast.makeText(MainActivity.this, "Log In Berhasil", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                                builder.setTitle("Upss!");
                                builder.setMessage("Akun anda di hapus oleh admin!");

                                builder.setPositiveButton("Hapus Akun", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

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
                                        Toast.makeText(MainActivity.this, "User berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().getCurrentUser().delete();
                                        startActivity(new Intent(MainActivity.this, ChooseActivity.class));
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Terjadi Error : " + databaseError, Toast.LENGTH_LONG).show();
            }
        });
    }
}
