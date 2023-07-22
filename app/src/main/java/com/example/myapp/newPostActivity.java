package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class newPostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mChooseButton, mUploadButton;
    private EditText mEditText;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private FirebaseUser user;
    private String userUid, userName, profilePic;
    private StorageTask mUploadTask;
    private TextView mUploading;
    private Bitmap compressedImgBitmap, mBitmap;
    private byte[] final_img_byte;
    private ByteArrayOutputStream stream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        mUploadButton = findViewById(R.id.upload_btn_newpost);
        mEditText = findViewById(R.id.editText_newpost);
        mImageView = findViewById(R.id.imageView_newpost);
        mProgressBar = findViewById(R.id.progressBar_newpost);
        mUploading = findViewById(R.id.textView_uploading_newPost);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userUid = user.getUid();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("users/" + userUid + "/ImagePosts");
        mStorageRef = FirebaseStorage.getInstance().getReference("users/" + userUid + "/ImagePosts");

        getUserNameAndProfilePic();
        Toast.makeText(this,"Please select image of small size", Toast.LENGTH_SHORT).show();


        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(newPostActivity.this, "Upload is in Progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        setBtnColor();
    }

    private void getUserNameAndProfilePic() {
        DatabaseReference userInfoRef = database.getReference("users/" + userUid + "/info");

        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("name").getValue(String.class);
                profilePic = dataSnapshot.child("profilePic").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setBtnColor() {
        mUploadButton.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.btnBackgroundColor), PorterDuff.Mode.MULTIPLY);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final  String uploadTime = String.valueOf(System.currentTimeMillis());
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);
                            fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String uploadId = mRef.push().getKey();
                                    UploadPost uploadPost = new UploadPost(mEditText.getText().toString().trim(),
                                            task.getResult().toString(), userName, profilePic, uploadTime, userUid, uploadId);

                                    mRef.child(uploadId).setValue(uploadPost);

                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    mUploading.setTextColor(getResources().getColor(R.color.green));
                                    mUploading.setText("Post Uploaded");

                                    Handler delayBackPressed = new Handler();
                                    delayBackPressed.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(newPostActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        }
                                    },1000);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(newPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            mUploading.setVisibility(View.INVISIBLE);
                            mProgressBar.setVisibility(View.INVISIBLE);

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                            mProgressBar.setVisibility(View.VISIBLE);
                            mUploading.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }
}
