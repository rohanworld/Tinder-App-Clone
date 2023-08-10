package com.rohanmaharaj.owntry.projectdapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private EditText mNameField, mPhoneField;
    private ImageView mProfileImage;
    private DatabaseReference mCustomerDatabase;

    //now two variable to store profile variables
    private String userId, name, phone, profileImageUrl;
    private Uri resultUri;

    //other Video Code as startactovity for result has been deprecated
    ActivityResultLauncher<String> mPickPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String userSex = getIntent().getExtras().getString("userSex");

        mNameField = findViewById(R.id.name);
        mPhoneField = findViewById(R.id.phone);
        Button mBack = findViewById(R.id.back);
        Button mConfirm = findViewById(R.id.confirm);
        mProfileImage = findViewById(R.id.profileImage);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userId);
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


        //now register that activity for result
        mPickPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            //this method will be called when we click on the image
            mProfileImage.setImageURI(result);
        });

        getUserInfo();
        //now if user clicks on image tehn he willbe directed to gallery
        mProfileImage.setOnClickListener(view -> {
//            Intent intent = new Intent(Intent.ACTION_PICK);
//            intent.setType("image/*");
//            startActivityForResult(intent, 1);

            //now laucnh the other startactivity for result method if the above not works
                mPickPhoto.launch("image/*");
        });

        mConfirm.setOnClickListener(view -> saveUserInformation());
        mBack.setOnClickListener(view -> finish());

    }

    private void getUserInfo(){
        //listener to check current user infomation
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    assert map != null;
                    if (map.get("name")!=null){
                        name = Objects.requireNonNull(map.get("name")).toString();
                        mNameField.setText(name);
                    }
                    if (map.get("phone")!=null){
                        phone = Objects.requireNonNull(map.get("phone")).toString();
                        mPhoneField.setText(phone);
                    }
                    if (map.get("profileImageUrl")!=null){
                        profileImageUrl = Objects.requireNonNull(map.get("profileImageUrl")).toString();
                        //now we are going to use glide
                        Glide.with(getApplicationContext()).load(profileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void saveUserInformation() {
        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        mCustomerDatabase.updateChildren(userInfo);

        if (resultUri!=null) {
            //profileImages is a folder to store every image
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;
            //now we are sending image from teh uri to this bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(e -> finish());
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //now we get the url and set it on imageview and i dk why this is not working
//                    Uri downloadUri = taskSnapshot.getUploadSessionUri();
                    Uri downloadUri = Uri.parse(Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().toString());

                    Map userInfo = new HashMap();
                    userInfo.put("profileImageUrl", downloadUri.toString());
                    mCustomerDatabase.updateChildren(userInfo);

                    finish();
                }
            });
        }else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode == Activity.RESULT_OK){
            assert data != null;
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}