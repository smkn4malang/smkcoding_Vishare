package com.irfan.ilham.tugasakhir;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
            protected void populateViewHolder(ViewHolder viewHolder, VideoItems model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setNama(model.getNamaVideo());

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
    }
}
