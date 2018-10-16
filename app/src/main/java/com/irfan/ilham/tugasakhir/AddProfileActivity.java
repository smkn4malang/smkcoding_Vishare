package com.irfan.ilham.tugasakhir;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddProfileActivity extends AppCompatActivity {

    private EditText TTL, Kota, Desc;
    private Button Submit;
    private LinearLayout progress;
    private CircleImageView profile;
    private StorageReference mStorageRef;
    private Uri imgLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        TTL = findViewById(R.id.TTLadd);
        Kota = findViewById(R.id.KotaAdd);
        Desc = findViewById(R.id.DescAdd);
        Submit = findViewById(R.id.AddSubmit);
        progress = findViewById(R.id.ProgresAddProfileDetail);
        profile = findViewById(R.id.profileAdd);
        mStorageRef = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddProfileActivity.this);
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                Submit.setClickable(false);
                String TTLInput = TTL.getText().toString().trim();
                String KotaInput = Kota.getText().toString().trim();
                String DescInput = Desc.getText().toString().trim();

                if (!imgLink.toString().isEmpty()) {

                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("deskripsi").setValue(DescInput);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("asal").setValue(KotaInput);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("TTL").setValue(TTLInput).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Submit.setClickable(true);
                                final StorageReference imgRef = mStorageRef.child("images/").child("profile.jpg");

                                imgRef.putFile(imgLink).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imgUrl").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progress.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(AddProfileActivity.this, "Berhasil mengupload profile", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(AddProfileActivity.this, HomeActivity.class));
                                                        finish();
                                                    }
                                                });
                                            }
                                        });
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(AddProfileActivity.this, "Berhasil mengupload profile", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(AddProfileActivity.this, HomeActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(AddProfileActivity.this, "Gagal mengupload profile", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                progress.setVisibility(View.INVISIBLE);
                                Submit.setClickable(true);
                                Toast.makeText(AddProfileActivity.this, "Gagal membuat data", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(AddProfileActivity.this, "Profile belum ditambahkan!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri selectedImg = result.getUri();
                imgLink = selectedImg;
                profile.setImageURI(selectedImg);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(AddProfileActivity.this, "Eroor : " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
