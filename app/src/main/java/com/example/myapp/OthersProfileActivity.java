package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OthersProfileActivity extends AppCompatActivity implements View.OnClickListener, PostAdapter.OnItemClickListener {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference nameRef,emailRef,postRef,profilePicRef,statusRef, allowedFollowersRef, RequestsRef, sentRequestsRef, followingRef, likeRef;
    private ValueEventListener mDBListener;
    private FirebaseStorage mStorage;
    private FirebaseUser user;
    private TextView mUsername, mFollow, mStatus, mEmail;
    private String userName, myUid, opUid;
    private String profilePic, status, followerKey, requestKey, sentRequestKey, followingKey;
    private ImageView mProfilePic;

    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private List<UploadPost> mUploadPost;
    private ProgressBar mProgressCircle;
    private Boolean sendRequest = true;
    private Boolean cancelRequest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        mUsername = findViewById(R.id.op_textView_profileUsername);
        mFollow = findViewById(R.id.op_textView_friend);
        mProgressCircle = findViewById(R.id.op_progress_circle);
        mProfilePic = findViewById(R.id.op_imageView);
        mStatus = findViewById(R.id.op_textView_status);
        mEmail = findViewById(R.id.textView_op_email);
        mRecyclerView =(RecyclerView) findViewById(R.id.op_recycleView);
        mUploadPost = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(OthersProfileActivity.this));


        mPostAdapter = new PostAdapter(OthersProfileActivity.this,mUploadPost);
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.setOnItemClickListener(OthersProfileActivity.this);

        Bundle bundle = getIntent().getExtras();
        opUid = bundle.getString("uid");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myUid = user.getUid();

        database = FirebaseDatabase.getInstance();
        nameRef = database.getReference("users/" + opUid + "/info/name");
        emailRef = database.getReference("users/" + opUid + "/info/email");
        postRef = database.getReference("users/" + opUid + "/ImagePosts");
        profilePicRef = database.getReference("users/" + opUid + "/info/profilePic");
        statusRef = database.getReference("users/" + opUid + "/status");

        allowedFollowersRef = database.getReference("users/" + opUid + "/followers/allowedFollowers");
        RequestsRef = database.getReference("users/" + opUid + "/followers/requests");
        sentRequestsRef = database.getReference("users/" + myUid + "/followers/sentRequests");
        followingRef = database.getReference("users/" + myUid + "/followers/following");

        mProgressCircle.setVisibility(View.VISIBLE);
        setUsernameAndEmail();
        setProfilePic();
        setStatus();


        allowedFollowersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sendRequest = true;
                cancelRequest = true;
                mUploadPost.clear();
                mPostAdapter.notifyDataSetChanged();
                mFollow.setText("Follow");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String currentUid = child.getValue(String.class);
                    if (currentUid.equals(myUid)) {
                        followerKey = child.getKey().toString();
                        sendRequest = false;
                        cancelRequest = false;
                        mFollow.setText("UnFollow");
                        setPosts();
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if (cancelRequest) {
            RequestsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(cancelRequest)mFollow.setText("Follow");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String currentUid = child.getValue(String.class);
                        if (currentUid.equals(myUid)) {
                            requestKey = child.getKey().toString();
                            sendRequest = false;
                            mFollow.setText("Cancel follow request");
                            mProgressCircle.setVisibility(View.INVISIBLE);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        if (sendRequest) {
            mProgressCircle.setVisibility(View.INVISIBLE);
            mFollow.setText("Follow");
        }

        getSentRequestKey();
        getFollowingKey();

        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFollowButtonAction();
            }
        });
    }

    private void getFollowingKey() {
        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    String uid = user.getValue(String.class);
                    if(uid.equals(opUid)) {
                        followingKey = user.getKey().toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getSentRequestKey() {
        sentRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    String uid = user.getValue(String.class);
                    if (uid.equals(opUid)) {
                        sentRequestKey = user.getKey().toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setFollowButtonAction() {
        String action = mFollow.getText().toString();
        if (action.equals("Follow")) {
            String uploadId = RequestsRef.push().getKey();
            RequestsRef.child(uploadId).setValue(myUid);
            String key = sentRequestsRef.push().getKey();
            sentRequestsRef.child(key).setValue(opUid);
            Toast.makeText(OthersProfileActivity.this,"Follow request sent",Toast.LENGTH_SHORT).show();
            mFollow.setText("Cancel follow request");
        }

        else if (action.equals("UnFollow")) {
            allowedFollowersRef.child(followerKey).removeValue();
            followingRef.child(followingKey).removeValue();
            mUploadPost.clear();
            mPostAdapter.notifyDataSetChanged();
            Toast.makeText(OthersProfileActivity.this,"UnFollowed",Toast.LENGTH_SHORT).show();
            mFollow.setText("Follow");
            sendRequest = true;
        }

        else if (action.equals("Cancel follow request")) {
            RequestsRef.child(requestKey).removeValue();
            sentRequestsRef.child(sentRequestKey).removeValue();
            Toast.makeText(OthersProfileActivity.this,"Canceled",Toast.LENGTH_SHORT).show();
            mFollow.setText("Follow");
            sendRequest = true;
        }

    }

    private void setUsernameAndEmail() {
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(String.class);
                mUsername.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        emailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = dataSnapshot.getValue().toString();
                mEmail.setText(email);
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
                if(profilePic.isEmpty()) {

                } else {
                    Picasso.with(OthersProfileActivity.this)
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

    private void setPosts() {
        mDBListener = postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploadPost.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadPost uploadPost = postSnapshot.getValue(UploadPost.class);
                    uploadPost.setmKey(postSnapshot.getKey());
                    mUploadPost.add(uploadPost);
                }
                Collections.reverse(mUploadPost);

                mPostAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OthersProfileActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(int position) {

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
                    if (id.getValue().toString().equals(myUid)){
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
                    likeRef.child(likeKey).setValue(myUid);
                    Toast.makeText(OthersProfileActivity.this,"Liked",Toast.LENGTH_SHORT).show();
                }else {
                    likeRef.child(keyUnlike[0]).removeValue();
                    Toast.makeText(OthersProfileActivity.this,"UnLiked",Toast.LENGTH_SHORT).show();
                }
            }
        },1000);

    }

    @Override
    public void onCommentClick(int position) {
        Toast.makeText(OthersProfileActivity.this,"work is in progress for this",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLikedByClick(int position) {
        UploadPost selectedItem = mUploadPost.get(position);
        final String selectedKey = selectedItem.getmKey();
        final String uid = selectedItem.getUserUid();

        Intent intent = new Intent(OthersProfileActivity.this,ViewLikesActivity.class);
        intent.putExtra("key",selectedKey);
        intent.putExtra("uid",uid);
        startActivity(intent);

    }

    @Override
    public void onDeleteClick(int position) {

    }
}

