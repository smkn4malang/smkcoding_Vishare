package com.irfan.ilham.tugasakhir;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {

    private Button btnMasuk, ya, tidak;
    private EditText email, password;
    private TextView forgot, back;
    private FirebaseAuth mAuth;
    private LinearLayout progress, adminDialog;
    private boolean admin = false, alert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        btnMasuk = findViewById(R.id.masuk);
        email = findViewById(R.id.EmailLogIn);
        password = findViewById(R.id.KataSandiLogIn);
        forgot = findViewById(R.id.lupasandi);
        back = findViewById(R.id.KembaliLogIn);
        progress = findViewById(R.id.ProgresLogin);
        adminDialog = findViewById(R.id.AdminAlerLogIn);
        ya = findViewById(R.id.AdminAlertIyaLogin);
        tidak = findViewById(R.id.AdminAlerTidakLogIn);

        mAuth = FirebaseAuth.getInstance();

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, VerifikasiActivity.class));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, ChooseActivity.class));
                finish();
            }
        });

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailInput = email.getText().toString().trim();
                final String passwordInput = password.getText().toString().trim();

                if (TextUtils.isEmpty(emailInput)) {
                    email.setError("Masukan alamat email!");
                } else if (TextUtils.isEmpty(passwordInput)) {
                    password.setError("Masukan kata sandi!");
                } else {
                    progress.setVisibility(View.VISIBLE);
                    btnMasuk.setClickable(false);
                    mAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                LogIn();
                            } else {
                                progress.setVisibility(View.INVISIBLE);
                                btnMasuk.setClickable(true);
                                Toast.makeText(LogInActivity.this, "Tidak Ada Akun Dengan Email Tersebut", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void LogIn() {
        final DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Admin");

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    if (key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        adminDialog.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.INVISIBLE);
                        alert = true;
                        ya.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                admin = true;
                                startActivity(new Intent(LogInActivity.this, AdminHomeActivity.class));
                                finish();
                            }
                        });
                        tidak.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                admin = false;
                                Toast.makeText(LogInActivity.this, "Log In Berhasil", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LogInActivity.this, HomeActivity.class));
                                finish();
                            }
                        });

                    }
                }
                if (!admin && !alert) {
                    FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                Toast.makeText(LogInActivity.this, "Log In Berhasil", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LogInActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LogInActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                                LayoutInflater layoutInflater = LayoutInflater.from(LogInActivity.this);
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
                                        Toast.makeText(LogInActivity.this, "User berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().getCurrentUser().delete();
                                        startActivity(new Intent(LogInActivity.this, ChooseActivity.class));
                                    }
                                });

                                android.support.v7.app.AlertDialog alertDialog = builder.create();
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
                Toast.makeText(LogInActivity.this, "Terjadi Error : " + databaseError, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,ChooseActivity.class));
        finish();
    }
}
