package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText nama, email, pass, verpass;
    private Button daftar;
    private RadioGroup JK;
    private TextView back;
    private FirebaseAuth mAuth;
    private LinearLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nama = findViewById(R.id.NamaPengguna);
        email = findViewById(R.id.Email);
        pass = findViewById(R.id.KataSandi);
        verpass = findViewById(R.id.VerifikasiKataSandi);
        daftar = findViewById(R.id.daftar);
        JK = findViewById(R.id.JK);
        daftar = findViewById(R.id.daftarkan);
        back = findViewById(R.id.KembaliRegister);
        progress = findViewById(R.id.ProgresRegister);

        mAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, ChooseActivity.class));
                finish();
            }
        });

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String namaInput = nama.getText().toString().trim();
                String emailInput = email.getText().toString().trim();
                String passwordInput = pass.getText().toString().trim();
                String verPassInput = verpass.getText().toString().trim();

                if (TextUtils.isEmpty(namaInput)) {
                    nama.setError("Masukan nama pengguna!");
                }else if (TextUtils.isEmpty(emailInput)) {
                    email.setError("Masukan alamat email!");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    email.setError("Alamat email tidak valid!");
                } else if (TextUtils.isEmpty(passwordInput)) {
                    pass.setError("Masukan kata sandi!");
                } else if (passwordInput.length() < 8) {
                    pass.setError("Kata sandi minimal 8 karakter");
                } else if (TextUtils.isEmpty(verPassInput)) {
                    verpass.setError("Masukan verifikasi kata sandi");
                } else if (!TextUtils.equals(verPassInput, passwordInput)) {
                    verpass.setError("Verifikasi kata sandi tidak sama!");
                } else {
                    progress.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(emailInput, passwordInput).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Register Berhasil", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                progress.setVisibility(View.INVISIBLE);
                                finish();
                            } else {
                                progress.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterActivity.this, "Regristasi Gagal", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
