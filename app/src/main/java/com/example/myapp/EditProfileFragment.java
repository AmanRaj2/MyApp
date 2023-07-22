package com.example.myapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.List;

public class EditProfileFragment extends Fragment {

    private View view;

    private EditText mName, mStatus, mPhone;
    private Button mSetChanges;
    private ImageView mProfilePic;
    private String name;
    private String status;
    private String phone;
    private String userUid;
    private String profilePic;
    private FirebaseDatabase database;
    private DatabaseReference nameRef, statusRef, phoneRef, profilePicRef, imagePostRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference mStorageRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private StorageTask mUploadTask;
    private TextView mUploading;
    //private List<String> postKeys;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
         mName = view.findViewById(R.id.editText_name_editProfile);
         mStatus = view.findViewById(R.id.editText_status_editProfile);
         mPhone = view.findViewById(R.id.editText_phone_editProfile);
         mSetChanges = view.findViewById(R.id.button_setChanges_editProfile);
         mProfilePic = view.findViewById(R.id.imageView_profilePic_editProfile);
        mProgressBar =view.findViewById(R.id.progressBar_editProfile);
        mUploading = view.findViewById(R.id.textView_uploading_editProfile);

        //postKeys = new ArrayList<>();

        mSetChanges.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.btnBackgroundColor), PorterDuff.Mode.MULTIPLY);

        //getPostKeys();
        setOldDetails();

         mSetChanges.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 setChanges();
             }
         });

         mProfilePic.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 openFileChooser();

             }
         });
        return view;
    }

    /*private void getPostKeys() {

    }*/

    private void setOldDetails() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userUid = user.getUid();
        nameRef = database.getReference("users/" + userUid + "/info/name");
        statusRef = database.getReference("users/" + userUid + "/status");
        phoneRef = database.getReference("users/" + userUid + "/info/phone");
        profilePicRef = database.getReference("users/" + userUid + "/info/profilePic");
        mStorageRef = FirebaseStorage.getInstance().getReference("users/" + userUid + "/profilePic");
        imagePostRef = database.getReference("users/" + userUid + "/ImagePosts");
        setName();
        setStatus();
        setProfilePic();
        setPhone();
    }

    private void setStatus() {
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(String.class);
                mStatus.setText(status);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfilePic() {
        profilePicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profilePic = dataSnapshot.getValue(String.class);
                if (profilePic.isEmpty()) {
                    //can be used to set some pic if user have not set it.
                } else {
                    Picasso.with(getContext())
                            .load(profilePic)
                            .placeholder(R.drawable.ic_profile)
                            .fit()
                            .centerCrop()
                            .into(mProfilePic);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setPhone() {
        phoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                phone = dataSnapshot.getValue(String.class);
                mPhone.setText(phone);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setName() {
                nameRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name = dataSnapshot.getValue(String.class);
                        mName.setText(name);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
        });
    }

    private void setChanges() {
        name = mName.getText().toString();
        status = mStatus.getText().toString();
        phone = mPhone.getText().toString();
        if (name.isEmpty()) {
            mName.setError("Name must be filled");
            mName.requestFocus();
        }else {
            nameRef.setValue(name);
            statusRef.setValue(status);
            phoneRef.setValue(phone);
            updateNameForPosts();
            Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNameForPosts() {
       imagePostRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                   String key = postSnapshot.getKey().toString();
                   imagePostRef.child(key).child("mUserName").setValue(name);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(getContext()).load(mImageUri).into(mProfilePic);
            uploadFile();
        }

    }
    /*
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }*/

    private void uploadFile() {
        if (mImageUri != null ) {
            mUploadTask = mStorageRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                            mStorageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    profilePic = task.getResult().toString();
                                    profilePicRef.setValue(profilePic);
                                    updateProfilePicForPosts(profilePic);
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    mUploading.setTextColor(getResources().getColor(R.color.green));
                                    mUploading.setText("Profile picture Uploaded");
                                    Handler removeUploadingTextView = new Handler();
                                    removeUploadingTextView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mUploading.setVisibility(View.INVISIBLE);
                                            mUploading.setText("Uploading...");
                                            mUploading.setTextColor(getResources().getColor(R.color.red));
                                        }
                                    },3000);
                                }

                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            mUploading.setVisibility(View.INVISIBLE);
                            mProgressBar.setVisibility(View.INVISIBLE);

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            mUploading.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfilePicForPosts(final String profilePic) {
        imagePostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey().toString();
                    imagePostRef.child(key).child("mProfilePic").setValue(profilePic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
