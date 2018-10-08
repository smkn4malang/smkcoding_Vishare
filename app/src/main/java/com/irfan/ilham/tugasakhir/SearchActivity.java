package com.irfan.ilham.tugasakhir;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchActivity extends AppCompatActivity {

    private ImageView back;
    private TextView id;
    private RecyclerView listUser;
    private Query mDatabase;
    private String SearchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        back = findViewById(R.id.backsearch);
        listUser = findViewById(R.id.SearchRecycleView);
        id = findViewById(R.id.idSearch);
        listUser.setHasFixedSize(true);
        listUser.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        SearchKey = getIntent().getExtras().getString("search_key");
        id.setText(SearchKey);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, HomeActivity.class));
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SearchActivity.this, HomeActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference("Video").orderByChild("namaVideo").startAt(SearchKey).endAt(SearchKey + "\uf8ff");
        FirebaseRecyclerAdapter<VideoItems, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<VideoItems, ViewHolder>(VideoItems.class, R.layout.item_video_content, ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, VideoItems model, int position) {
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
                        Intent in = new Intent(SearchActivity.this, VideoViewActivity.class);
                        in.putExtra("id_video", post_key);
                        startActivity(in);
                        finish();
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
    }
}
