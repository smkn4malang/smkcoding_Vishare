package com.irfan.ilham.tugasakhir;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ImageView user, search, searchClose, searchBtn;
    private EditText searchInput;
    private LinearLayout searchLayout, tobBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);
        user = findViewById(R.id.userDetail);
        search = findViewById(R.id.search);
        searchClose = findViewById(R.id.searchClose);
        searchLayout = findViewById(R.id.searchLayout);
        searchBtn = findViewById(R.id.searchButton);
        searchInput = findViewById(R.id.searchInput);

        tobBar = findViewById(R.id.TopBarHome);
        mAuth = FirebaseAuth.getInstance();

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.icon_home).setText("Beranda");
        tabLayout.getTabAt(1).setIcon(R.drawable.icon_menu).setText("Kategori");

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(HomeActivity.this, UserDetailActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(HomeActivity.this, GuestDetailActivity.class));
                    finish();
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = searchInput.getText().toString();
                Intent search = new Intent(HomeActivity.this, SearchActivity.class);
                search.putExtra("search_key", key);
                startActivity(search);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLayout.setVisibility(View.VISIBLE);
                tobBar.setVisibility(View.INVISIBLE);

            }
        });

        searchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLayout.setVisibility(View.INVISIBLE);
                tobBar.setVisibility(View.VISIBLE);
            }
        });
    }
}