package com.irfan.ilham.tugasakhir;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDetailFragment extends Fragment {

    private TextView nama, desc, kota, ttl, jk, email;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_detail, container, false);
        nama = view.findViewById(R.id.NamaUserDetail);
        desc = view.findViewById(R.id.DeskripUserDetail);
        kota = view.findViewById(R.id.KotaUserDetail);
        ttl = view.findViewById(R.id.TanggalUserDetail);
        jk = view.findViewById(R.id.JKUserDetail);
        email = view.findViewById(R.id.EmailUserDetail);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nameOut = dataSnapshot.child("nama").getValue().toString();
                String descOut = dataSnapshot.child("deskripsi").getValue().toString();
                String kotaOut = dataSnapshot.child("asal").getValue().toString();
                String ttlOut = dataSnapshot.child("TTL").getValue().toString();
                String jkOut = dataSnapshot.child("jenis_kelamin").getValue().toString();
                String emailOut = dataSnapshot.child("alamat_email").getValue().toString();

                nama.setText(nameOut);
                desc.setText(descOut);
                kota.setText(kotaOut);
                ttl.setText(ttlOut);
                jk.setText(jkOut);
                email.setText(emailOut);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}