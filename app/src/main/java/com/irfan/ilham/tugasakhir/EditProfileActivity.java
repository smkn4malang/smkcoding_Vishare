package com.irfan.ilham.tugasakhir;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private EditText Nama, TTL, Kota, Desc;
    private Button Submit;
    private CircleImageView profile;
    private RadioGroup JKGroup;
    private LinearLayout progress;
    private RadioButton JKSelected, JKL, JKP;
    private StorageReference mStorageRef;
    private Uri imgLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Nama = findViewById(R.id.NamaEdit);
        TTL = findViewById(R.id.TTLEdit);
        Kota = findViewById(R.id.KotaEdit);
        Desc = findViewById(R.id.DescEdit);
        Submit = findViewById(R.id.UserDetailEditSubmit);
        profile = findViewById(R.id.profileEdit);
        JKGroup = findViewById(R.id.JKEdit);
        JKL = findViewById(R.id.LEdit);
        JKP = findViewById(R.id.PEdit);
        progress = findViewById(R.id.ProgresEditProfile);
        mStorageRef = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                Submit.setClickable(false);
                int JKID = JKGroup.getCheckedRadioButtonId();
                JKSelected = findViewById(JKID);
                String JKInput = JKSelected.getText().toString().trim();
                String NamaInput = Nama.getText().toString().trim();
                String TTLInput = TTL.getText().toString().trim();
                String KotaInput = Kota.getText().toString().trim();
                String DescInput = Desc.getText().toString().trim();

                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("nama").setValue(NamaInput);
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("deskripsi").setValue(DescInput);
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("asal").setValue(KotaInput);
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("jenis_kelamin").setValue(JKInput);
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("TTL").setValue(TTLInput).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Submit.setClickable(true);

                            StorageReference imgRef = mStorageRef.child("images/profile.jpg");
                            imgRef.putFile(imgLink).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progress.setVisibility(View.INVISIBLE);
                                    Toast.makeText(EditProfileActivity.this, "Berhasil mengupload profile", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progress.setVisibility(View.INVISIBLE);
                                    Toast.makeText(EditProfileActivity.this, "Gagal mengupload profile", Toast.LENGTH_LONG).show();
                                }
                            });

                        } else

                        {
                            progress.setVisibility(View.INVISIBLE);
                            Submit.setClickable(true);
                            Toast.makeText(EditProfileActivity.this, "Gagal membuat data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        StorageReference imgRef = mStorageRef.child("images/profile.jpg");
        final long HALF_MEGABYTE = 1024 * 1024;
        imgRef.getBytes(HALF_MEGABYTE).
                addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        progress.setVisibility(View.INVISIBLE);
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);
                        profile.setMinimumHeight(dm.heightPixels);
                        profile.setMinimumWidth(dm.widthPixels);
                        profile.setImageBitmap(bm);
                    }
                }).

                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Gagal memuat data : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        UserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nameOut = dataSnapshot.child("nama").getValue().toString();
                String TTLOut = dataSnapshot.child("TTL").getValue().toString();
                String KotaOut = dataSnapshot.child("asal").getValue().toString();
                String JKOut = dataSnapshot.child("jenis_kelamin").getValue().toString();
                String DescOut = dataSnapshot.child("deskripsi").getValue().toString();
                Nama.setText(nameOut);
                TTL.setText(TTLOut);
                Kota.setText(KotaOut);
                Desc.setText(DescOut);
                if (JKOut.equals("Laki - laki")) {
                    JKL.setChecked(true);
                } else if (JKOut.equals("Perempuan")) {
                    JKP.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, "Gagal memuat data", Toast.LENGTH_LONG).show();
            }
        });

        profile.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImg = data.getData();
                imgLink = selectedImg;
                profile.setImageURI(selectedImg);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
