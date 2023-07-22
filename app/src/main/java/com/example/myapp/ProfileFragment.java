package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment implements View.OnClickListener, PostAdapter.OnItemClickListener {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mRef,postRef,profilePicRef,statusRef, likeRef;
    private ValueEventListener mDBListener;
    private FirebaseStorage mStorage;
    private FirebaseUser user;
    private View view;
    private TextView mUsername, mNewPost, mStatus;
    private String userName, userUid, name, pic;
    private String profilePic, status;
    private ImageView mProfilePic;

    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private List<UploadPost> mUploadPost;
    private ProgressBar mProgressCircle;
    private UploadPost uploadPost;
    //private Button likeBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mUsername = view.findViewById(R.id.textView_profileUsername);
        mNewPost = view.findViewById(R.id.textView_upload);
        mProgressCircle = view.findViewById(R.id.progress_circle);
        mProfilePic = view.findViewById(R.id.imageView_profileFragment);
        mStatus = view.findViewById(R.id.textView_status_profileFragment);

        mRecyclerView =(RecyclerView) view.findViewById(R.id.profile_recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUploadPost = new ArrayList<>();

        mPostAdapter = new PostAdapter(getContext(),mUploadPost);
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.setOnItemClickListener(ProfileFragment.this);

        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userUid = user.getUid();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("users/" + userUid + "/info/name");
        postRef = database.getReference("users/" + userUid + "/ImagePosts");
        profilePicRef = database.getReference("users/" + userUid + "/info/profilePic");
        statusRef = database.getReference("users/" + userUid + "/status");


        setUsername();
        setProfilePic();
        setStatus();
        setPosts();

        //
        /*mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        mNewPost.setOnClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(),"Normal click at position : "+ position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCommentClick(int position) {
        Toast.makeText(getContext(),"work is in progress for this",Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onLikeClick(int position) {
        UploadPost selectedItem = mUploadPost.get(position);
        final String selectedKey = selectedItem.getmKey();
        final String uid = selectedItem.getUserUid();
        final boolean[] allow = {true};
        final String[] keyUnlike = new String[1];

        likeRef = database.getReference("users/" + uid + "/ImagePosts/" + selectedKey + "/likes");
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    if (id.getValue().toString().equals(userUid)){
                        allow[0] = false;
                        keyUnlike[0] = id.getKey();
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (allow[0]) {
                    String likeKey = likeRef.push().getKey();
                    likeRef.child(likeKey).setValue(userUid);
                    Toast.makeText(getContext(),"Liked",Toast.LENGTH_SHORT).show();
                }else {
                    likeRef.child(keyUnlike[0]).removeValue();
                    Toast.makeText(getContext(),"UnLiked",Toast.LENGTH_SHORT).show();
                }
            }
        },1000);

    }

    @Override
    public void onLikedByClick(int position) {
        UploadPost selectedItem = mUploadPost.get(position);
        final String selectedKey = selectedItem.getmKey();
        final String uid = selectedItem.getUserUid();
        //Toast.makeText(getContext(),"Some action will be added later for this button",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(),ViewLikesActivity.class);
        intent.putExtra("key",selectedKey);
        intent.putExtra("uid",uid);
        startActivity(intent);

    }

    @Override
    public void onDeleteClick(int position) {
        UploadPost selectedItem = mUploadPost.get(position);
        final String selectedKey = selectedItem.getmKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                postRef.child(selectedKey).removeValue();
                Toast.makeText(getContext(),"Post deleted",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_upload:
                Intent intent = new Intent(getContext(), newPostActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setUsername() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(String.class);
                mUsername.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

     private void setPosts() {
        mDBListener = postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploadPost.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    uploadPost = postSnapshot.getValue(UploadPost.class);
                    String key = postSnapshot.getKey();
                    uploadPost.setmKey(key);
                    mUploadPost.add(uploadPost);
                }
                Collections.reverse(mUploadPost);
                mPostAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void setProfilePic() {
        profilePicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profilePic = dataSnapshot.getValue(String.class);
                if (profilePic.isEmpty()) {
                    //
                } else {
                    Picasso.with(getContext())
                            .load(profilePic)
                            .placeholder(R.mipmap.ic_launcher)
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        postRef.removeEventListener(mDBListener);
    }
}
