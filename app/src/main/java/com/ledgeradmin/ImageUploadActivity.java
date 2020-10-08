package com.ledgeradmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageUploadActivity extends AppCompatActivity {

    Button upload, save;

    String type, company, sales, id;

    Uri uri;
    private static final int PICK_IMAGE = 1;

    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        if(type.equals("sales")){
            company = intent.getStringExtra("company");
            id = intent.getStringExtra("id");
        }
        else if (type.equals("dealer")){
            company = intent.getStringExtra("company");
            id = intent.getStringExtra("id");
            sales = intent.getStringExtra("sales");
        }
        else if(type.equals("head")){
            id = intent.getStringExtra("id");
        }
        else if(type.equals("company")){
            id = intent.getStringExtra("id");
        }

        upload = findViewById(R.id.upload);
        save = findViewById(R.id.save);
        circleImageView = findViewById(R.id.profile_image);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(ImageUploadActivity.this);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(uri!=null){

                    File file = new File(uri.getPath());
                    long size = file.length();
                    Log.i("size",String.valueOf(size));

                    if(size > 110000){
                        Toast.makeText(ImageUploadActivity.this, "Please upload a image of size less than 100Kb",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        StorageReference storageReference;
                        storageReference = FirebaseStorage.getInstance().getReference();

                        final StorageReference storageReference1 = storageReference.child(id);
                        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadUrl = uri;
                                        Log.i("Download Url",String.valueOf(downloadUrl));

                                        final HashMap<String, Object> image = new HashMap<>();
                                        image.put("pic",String.valueOf(downloadUrl));

                                        final FirebaseFirestore db;
                                        db = FirebaseFirestore.getInstance();

                                        if(type.equals("sales")){
                                            db.collection("Users").document(id).update(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    db.collection("Companies").document(company).collection("sales")
                                                            .document(id).update(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                        }
                                                    });
                                                }
                                            });
                                        }

                                        else if(type.equals("dealer")){
                                            db.collection("Users").document(id).update(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    db.collection("Companies").document(company).collection("sales")
                                                            .document(sales).collection("dealers").document(id).update(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                        }
                                                    });
                                                }
                                            });
                                        }

                                        else if(type.equals("head")){
                                            db.collection("Heads").document(id).update(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    db.collection("Users").document(id).update(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                        }
                                                    });
                                                }
                                            });
                                        }

                                        else if(type.equals("company")){
                                            db.collection("Companies").document(id).update(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });

                        Toast.makeText(ImageUploadActivity.this, "Your image will be uploaded soon", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else {
                    Toast.makeText(ImageUploadActivity.this, "Please upload a image to continue",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK) {
            Uri imageuri = CropImage.getPickImageResultUri(this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(this,imageuri)){
                uri = imageuri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
            }
            else {
                startCrop(imageuri);
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                uri = result.getUri();

                Picasso.get().load(uri).into(circleImageView);
            }
        }
    }

    private void startCrop(Uri imageuri){
        CropImage.activity(imageuri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1,1)
                .start(this);

    }
}