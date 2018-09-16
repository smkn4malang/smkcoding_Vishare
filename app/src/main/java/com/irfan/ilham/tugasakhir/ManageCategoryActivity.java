package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManageCategoryActivity extends AppCompatActivity {

    private RecyclerView listUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        listUser = findViewById(R.id.manageKategoriRecycleView);
        listUser.setHasFixedSize(true);
        listUser.setLayoutManager(new LinearLayoutManager(ManageCategoryActivity.this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference("Kategori");
        FirebaseRecyclerAdapter<KategoriItems, ManageCategoryActivity.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<KategoriItems, ViewHolder>(KategoriItems.class, R.layout.item_category_manager, ManageCategoryActivity.ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, KategoriItems model, int position) {
                viewHolder.setNama(model.getNamaKategori());
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
            TextView nama_view = mView.findViewById(R.id.namaKategoriItem);
            nama_view.setText(nama);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, AdminHomeActivity.class));
        finish();
    }
}
