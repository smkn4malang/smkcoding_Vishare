package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManageUserActivity extends AppCompatActivity {

    private RecyclerView listUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        listUser = findViewById(R.id.manageUserRecycleView);
        listUser.setHasFixedSize(true);
        listUser.setLayoutManager(new LinearLayoutManager(ManageUserActivity.this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseRecyclerAdapter<UserIItems, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserIItems, ViewHolder>(UserIItems.class, R.layout.item_user_manager, ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, UserIItems model, int position) {
                viewHolder.setNama(model.getNama());
                viewHolder.setAsal(model.getAsal());
                viewHolder.setImg(model.getImgUrl());

            }
        };
        listUser.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNama(String nama) {
            TextView nama_view = mView.findViewById(R.id.usernameItemUser);
            nama_view.setText(nama);
        }

        public void setAsal(String asal) {
            TextView asal_view = mView.findViewById(R.id.asalItemUser);
            asal_view.setText(asal);
        }

        public void setImg(String img) {
            ImageView img_view = mView.findViewById(R.id.profileItemUser);
            Glide.with(img_view.getContext()).load(img).override(100, 100).into(img_view);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AdminHomeActivity.class));
        finish();
    }
}