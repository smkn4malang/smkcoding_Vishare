package com.irfan.ilham.tugasakhir;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private RecyclerView listUser;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        listUser = view.findViewById(R.id.HomeRecycleView);
        listUser.setHasFixedSize(true);
        listUser.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference("Video");
        FirebaseRecyclerAdapter<VideoItems, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<VideoItems, ViewHolder>(VideoItems.class, R.layout.item_video_content, ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, VideoItems model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setNama(model.getNamaVideo());
                viewHolder.setRating(model.getRating());
                viewHolder.setTonton(model.getTonton());

                String thumbnail = model.getThumbnailUrl();
                StorageReference thumb = FirebaseStorage.getInstance().getReferenceFromUrl(thumbnail);
                thumb.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                        viewHolder.setThumbnail(dm.heightPixels, dm.widthPixels, bm);
                    }
                });

                String UID = model.getUID();
                if (!UID.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference("Users").child(UID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String nama = dataSnapshot.child("nama").getValue().toString();
                            String ImgUrl = dataSnapshot.child("imgUrl").getValue().toString();
                            viewHolder.setUploader(nama);
                            viewHolder.setProfile(ImgUrl);
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
        listUser.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder(View itemView) {
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

        public void setProfile(String image) {
            CircleImageView profile = mView.findViewById(R.id.ProfileUploaderVideoContent);
            Glide.with(profile.getContext()).load(image).override(100, 100).into(profile);
        }

        public void setThumbnail(int heigh, int width, Bitmap image) {
            ImageView profile = mView.findViewById(R.id.ThumbnailVideoContent);
            profile.setMinimumHeight(heigh);
            profile.setMinimumWidth(width);
            profile.setImageBitmap(image);
        }
    }
}