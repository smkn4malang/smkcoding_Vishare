package com.irfan.ilham.tugasakhir;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.irfan.ilham.tugasakhir.R.style.AlertDialogCustom;

public class ManageCategoryActivity extends AppCompatActivity {

    private RecyclerView listUser;
    private DatabaseReference mDatabase;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        listUser = findViewById(R.id.manageKategoriRecycleView);
        add = findViewById(R.id.addKategoriManager);
        listUser.setHasFixedSize(true);
        listUser.setLayoutManager(new LinearLayoutManager(ManageCategoryActivity.this));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference("Kategori");
        FirebaseRecyclerAdapter<KategoriItems, ManageCategoryActivity.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<KategoriItems, ViewHolder>(KategoriItems.class, R.layout.item_category_manager, ManageCategoryActivity.ViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, KategoriItems model, int position) {
                viewHolder.setNama(model.getNamaKategori());
                final String Uid = model.getNamaKategori();

                viewHolder.update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        update(Uid);
                    }
                });

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete(Uid);
                    }
                });
            }
        };
        listUser.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView delete, update;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            update = mView.findViewById(R.id.updateBtnKategoriItem);
            delete = mView.findViewById(R.id.deleteBtnKategoriItem);
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

    private void update(final String Uid) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.child("namaKategori").getValue().toString();
                    final String key2 = ds.getKey().toString();
                    if (key.equals(Uid)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageCategoryActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                        LayoutInflater layoutInflater = LayoutInflater.from(ManageCategoryActivity.this);
                        final View myView = layoutInflater.inflate(R.layout.dialog_add_kategori, null);
                        TextView tv = myView.findViewById(R.id.AddKategoriDialog);
                        tv.setText(key);

                        builder.setView(myView);
                        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String namaKategori;
                                EditText namaInput = myView.findViewById(R.id.AddKategoriDialog);
                                namaKategori = namaInput.getText().toString().trim();
                                FirebaseDatabase.getInstance().getReference("Kategori").child(key2).child("namaKategori").setValue(namaKategori);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void delete(final String Uid) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot  ds : dataSnapshot.getChildren()) {
                    String key = ds.child("namaKategori").getValue().toString();
                    if (key.equals(Uid)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageCategoryActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                        LayoutInflater layoutInflater = LayoutInflater.from(ManageCategoryActivity.this);
                        builder.setTitle("Delete!");
                        builder.setMessage("Are you sure to delete " + Uid + " ?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ds.getRef().removeValue();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void add() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageCategoryActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        LayoutInflater layoutInflater = LayoutInflater.from(ManageCategoryActivity.this);
        final View myView = layoutInflater.inflate(R.layout.dialog_add_kategori, null);

        builder.setView(myView);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String namaKategori;
                EditText namaInput = myView.findViewById(R.id.AddKategoriDialog);
                namaKategori = namaInput.getText().toString().trim();
                FirebaseDatabase.getInstance().getReference("Kategori").push().child("namaKategori").setValue(namaKategori);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
