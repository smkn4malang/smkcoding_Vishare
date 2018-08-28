package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

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
                    if (mAuth.getCurrentUser() != null) {
                        startActivity(j);
                        finish();
                    } else {
                        startActivity(i);
                        finish();
                    }
                }
            }
        };
        timer.start();

    }
}
