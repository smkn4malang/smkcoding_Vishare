package com.irfan.ilham.tugasakhir;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    private Button btnMasuk;
    private EditText email, password;
    private TextView forgot, back;
    private FirebaseAuth mAuth;
    private LinearLayout progress;
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
                startActivity(new Intent(LogInActivity.this, RegisterActivity.class));
                finish();
            }
        });

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if (TextUtils.isEmpty(emailInput)) {
                    email.setError("Masukan alamat email!");
                } else if (TextUtils.isEmpty(passwordInput)) {
                    password.setError("Masukan kata sandi!");
                } else {
                    progress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LogInActivity.this, "Log In Berhasil", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LogInActivity.this, HomeActivity.class));
                                progress.setVisibility(View.INVISIBLE);
                                finish();
                            } else {
                                progress.setVisibility(View.INVISIBLE);
                                Toast.makeText(LogInActivity.this, "Tidak Ada Akun Dengan Email Tersebut", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
