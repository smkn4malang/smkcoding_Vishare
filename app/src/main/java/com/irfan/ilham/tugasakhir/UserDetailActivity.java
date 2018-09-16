package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapterUser viewPagerAdapter;
    private FloatingActionButton plus, edit, upload, logOut;
    private Animation fabOpen, fabCLose, fabClockwise, fabAntiClockwise;
    private TextView kembali, userName;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseUserReference = firebaseDatabase.getReference("Users");
    private StorageReference mStorageRef;
    private ImageView profileBG;
    private CircleImageView profile;
    private ProgressBar progres;
    private boolean UserTabIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        tabLayout = findViewById(R.id.tab_layoutUser);
        viewPager = findViewById(R.id.viewpagerUser);
        plus = findViewById(R.id.userPlus);
        edit = findViewById(R.id.userEdit);
        upload = findViewById(R.id.userUpload);
        logOut = findViewById(R.id.userLogOut);
        kembali = findViewById(R.id.KembaliUserDetail);
        userName = findViewById(R.id.userNameDetail);
        profileBG = findViewById(R.id.ProfileBG);
        profile = findViewById(R.id.ProfileDetails);
        progres = findViewById(R.id.progresProfile);

        mStorageRef = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabCLose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabAntiClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        StorageReference imgRef = mStorageRef.child("images/profile.jpg");
        final long HALF_MEGABYTE = 1024 * 1024;
        imgRef.getBytes(HALF_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        progres.setVisibility(View.INVISIBLE);
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        profile.setMinimumHeight(dm.heightPixels);
                        profile.setMinimumWidth(dm.widthPixels);
                        profile.setImageBitmap(bm);
                        profileBG.setMinimumHeight(dm.heightPixels);
                        profileBG.setMinimumWidth(dm.widthPixels);
                        profileBG.setImageBitmap(bm);
                    }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progres.setVisibility(View.INVISIBLE);
                Toast.makeText(UserDetailActivity.this, "Gagal memuat data : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        DatabaseReference UserReference = databaseUserReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        UserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("nama").getValue().toString();
                userName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserDetailActivity.this, "Gagal memuat data", Toast.LENGTH_LONG).show();
            }
        });

        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDetailActivity.this, HomeActivity.class));
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDetailActivity.this, EditProfileActivity.class));
                finish();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserDetailActivity.this, UploadActivity.class));
                finish();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserDetailActivity.this, HomeActivity.class));
                finish();
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserTabIsOpen) {
                    logOut.startAnimation(fabCLose);
                    edit.startAnimation(fabCLose);
                    upload.startAnimation(fabCLose);
                    plus.startAnimation(fabAntiClockwise);
                    edit.setClickable(false);
                    logOut.setClickable(false);
                    upload.setClickable(false);
                    UserTabIsOpen = false;
                } else {
                    logOut.startAnimation(fabOpen);
                    edit.startAnimation(fabOpen);
                    upload.startAnimation(fabOpen);
                    plus.startAnimation(fabClockwise);
                    edit.setClickable(true);
                    upload.setClickable(true);
                    logOut.setClickable(true);
                    UserTabIsOpen = true;
                }
            }
        });

        viewPagerAdapter = new ViewPagerAdapterUser(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Profile");
        tabLayout.getTabAt(1).setText("Channel");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }
}
