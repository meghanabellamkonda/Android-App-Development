package com.example.indiantouristry;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.indiantouristry.databinding.ActivityProfileBinding;
import com.example.indiantouristry.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    StorageReference storageReference;
    ActivityProfileBinding binding;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference("Profile");
        storageReference = FirebaseStorage.getInstance().getReference()
                .child("ProfileImages/"+ UUID.randomUUID().toString());
        ActivityResultLauncher<String> getContent =
                registerForActivityResult(new ActivityResultContracts.GetContent(),
                        new ActivityResultCallback<Uri>() {
                            @Override
                            public void onActivityResult(Uri result) {
                                uri = result;
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media
                                            .getBitmap(getContentResolver(),uri);
                                    binding.iv.setImageBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

        binding.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContent.launch("image/*");
            }
        });
    }

    public void save(View view) {
        storageReference.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                String name = binding.sname.getText().toString();
                                String mail = binding.smail.getText().toString();
                                String phone = binding.snumber.getText().toString();
                                MyModel myModel = new MyModel(url,name,mail,phone);
                                Toast.makeText(ProfileActivity.this, "Data Updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    public void fetch(View view) {
        startActivity(new Intent(this,DisplayActivity.class));
    }
}