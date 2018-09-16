package com.irfan.ilham.tugasakhir;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                                Toast.makeText(MainActivity.this, "Log In Berhasil", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            }
                        });

                    }
                }
                if (!admin && !alert) {
                    Toast.makeText(MainActivity.this, "Log In Berhasil", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Terjadi Error : " + databaseError, Toast.LENGTH_LONG).show();
            }
        });
    }
}
