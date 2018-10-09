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
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference("Kategori");
        FirebaseRecyclerAdapter<KategoriItems, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<KategoriItems, ViewHolder>(KategoriItems.class, R.layout.item_kategori, ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, KategoriItems model, int position) {
                viewHolder.setNamaKategori(model.getNamaKategori());
                viewHolder.kontenRecycleView.setHasFixedSize(true);
                viewHolder.kontenRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                String kategori = model.getNamaKategori();

                Query dbRef = FirebaseDatabase.getInstance().getReference().child("Video").orderByChild("kategori").startAt(kategori).endAt(kategori + "\uf8ff");
                FirebaseRecyclerAdapter<VideoItems, ViewHolder2> fra = new FirebaseRecyclerAdapter<VideoItems, ViewHolder2>(VideoItems.class, R.layout.item_video_content, ViewHolder2.class, dbRef) {
                    @Override
                    protected void populateViewHolder(final ViewHolder2 viewHolder, VideoItems model, int position) {
                        final String post_key = getRef(position).getKey();
                        viewHolder.setNama(model.getNamaVideo());
                        viewHolder.setRating(model.getRating());
                        viewHolder.setTonton(model.getTonton());

                        String UID = model.getUID();
                        if (!UID.isEmpty()) {
                            FirebaseDatabase.getInstance().getReference("Users").child(UID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String nama = dataSnapshot.child("nama").getValue().toString();
                                    viewHolder.setUploader(nama);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent in = new Intent(getActivity(), VideoViewActivity.class);
                                in.putExtra("id_video", post_key);
                                startActivity(in);
                                getActivity().finish();
                            }
                        });
                    }
                };
                viewHolder.kontenRecycleView.setAdapter(fra);
            }
        };
        list.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        RecyclerView kontenRecycleView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            kontenRecycleView = mView.findViewById(R.id.KategoriNestedRecycleView);
        }

        public void setNamaKategori(String nama) {
            TextView nama_view = mView.findViewById(R.id.namaKategoriView);
            nama_view.setText(nama);
        }
    }

    public static class ViewHolder2 extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder2(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNama(String nama) {
            TextView nama_view = mView.findViewById(R.id.namaVideoContent);
            nama_view.setText(nama);
        }

        public void setTonton(int viewers) {
            TextView tonton_view = mView.findViewById(R.id.TontonVideoContent);
            tonton_view.setText(viewers + "x ditonton");
        }

        public void setRating(float rating) {
            RatingBar rating_view = mView.findViewById(R.id.ratingVideoContent);
            rating_view.setRating(rating);
        }

        public void setUploader(String nama) {
            TextView nama_view = mView.findViewById(R.id.NamaUploaderVideoContent);
            nama_view.setText(nama);
        }
    }
}