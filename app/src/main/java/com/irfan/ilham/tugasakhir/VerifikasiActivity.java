package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerifikasiActivity extends AppCompatActivity {

    private TextView user, email;
    private Button lanjut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);
        user = findViewById(R.id.RegisterUserIdVerifikasi);
        email = findViewById(R.id.RegisterEmailVerifikasi);
        lanjut = findViewById(R.id.lanjut);

        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nama = dataSnapshot.child("nama").getValue().toString();
                String emailS = dataSnapshot.child("alamat_email").getValue().toString();

                user.setText("Hai, " + nama);
                email.setText("Buka email " + emailS + " untuk verifikasi");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(VerifikasiActivity.this, "Email verifikasi dikirim ke " + user.getEmail(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerifikasiActivity.this, "Gagal mengirim email verifikasi ke " + user.getEmail(), Toast.LENGTH_SHORT).show();
            }
        });

        lanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.isEmailVerified()) {
                    startActivity(new Intent(VerifikasiActivity.this, AddProfileActivity.class));
                    finish();
                } else {
                    Toast.makeText(VerifikasiActivity.this, "Email belum di verifikasi!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
