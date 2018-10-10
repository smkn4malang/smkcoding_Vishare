package com.irfan.ilham.tugasakhir;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class KategoriFragment extends Fragment {

    private RecyclerView list;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kategori, container, false);
        list = view.findViewById(R.id.KategoriRecycleView);
        list.setHasFixedSize(true);
        list.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference("Kategori");
        FirebaseRecyclerAdapter<KategoriItems, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<KategoriItems, ViewHolder>(KategoriItems.class, R.layout.item_kategori, ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, final KategoriItems model, int position) {
                viewHolder.setNamaKategori(model.getNamaKategori());
                final String key = model.getNamaKategori();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent search = new Intent(getActivity(), KategoriActivity.class);
                        search.putExtra("kategori_key", key);
                        startActivity(search);
                    }
                });
            }
        };
        list.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNamaKategori(String nama) {
            TextView nama_view = mView.findViewById(R.id.namaKategoriView);
            nama_view.setText(nama);
        }
    }
}