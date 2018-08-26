package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChooseActivity extends AppCompatActivity {

    private Button masuk, daftar;
    private TextView lewat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        masuk = findViewById(R.id.masuk);
        daftar = findViewById(R.id.daftar);
        lewat = findViewById(R.id.lewatiLogin);

        final Intent intentMasuk = new Intent(ChooseActivity.this, LogInActivity.class);
        final Intent intentDaftar = new Intent(ChooseActivity.this, RegisterActivity.class);
        final Intent intentHome = new Intent(ChooseActivity.this, HomeActivity.class);

        masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentMasuk);
            }
        });

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentDaftar);
            }
        });

        lewat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentHome);
                finish();
            }
        });
    }
}
