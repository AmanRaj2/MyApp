package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowingListActivity extends AppCompatActivity implements following_adapter.OnItemClickListener{

    private RecyclerView mRecyclerView;
    private following_adapter mFollowing_adapter;
    private ProgressBar mProgressBar;
    private DatabaseReference allowedFollowersRef, followingRef;
    private List<userDetails> mUserDetails;
    private FirebaseDatabase database;
    private String myUid, allowedFollowersKey, followingKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);

        mProgressBar = findViewById(R.id.following_item_progressBar);
        mRecyclerView = findViewById(R.id.following_item_recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserDetails = new ArrayList<>();

        mFollowing_adapter = new following_adapter(FollowingListActivity.this, mUserDetails);
        mRecyclerView.setAdapter(mFollowing_adapter);

        mFollowing_adapter.setOnItemClickListener(FollowingListActivity.this);

        getMyUserUid();
        database = FirebaseDatabase.getInstance();
        followingRef = database.getReference("users/" + myUid + "/followers/following");

        setList();
        Toast.makeText(this,"Long press to take action", Toast.LENGTH_SHORT).show();
    }

    private void setList() {
        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserDetails.clear();
                DatabaseReference currentUserRef;
                for(DataSnapshot user : dataSnapshot.getChildren()) {
                    String uid = user.getValue(String.class);
                    currentUserRef = database.getReference("users/" + uid+ "/info");

                    currentUserRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userDetails currentUserDetails = dataSnapshot.getValue(userDetails.class);
                            mUserDetails.add(currentUserDetails);
                            mFollowing_adapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                mFollowing_adapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FollowingListActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        final String userUid = selectedItem.getUserUid();
        if (userUid.equals(myUid)) {
            Toast.makeText(FollowingListActivity.this, "This is Your account", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(FollowingListActivity.this, OthersProfileActivity.class);
            intent.putExtra("uid", userUid);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onCall(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        String name = selectedItem.getName();
        String number = selectedItem.getPhone();
        if(number.equals("")) {
            Toast.makeText(FollowingListActivity.this,name + " have not set his number",Toast.LENGTH_SHORT).show();
        } else {
            String s = "tel:"+number;
            Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse(s));
            startActivity(intent1);

        }
    }

    @Override
    public void onUnfollow(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        final String opUid = selectedItem.getUserUid();
        removeFromAllowedFollowers(opUid);
        removeFromFollowing(opUid);

    }

    private void removeFromAllowedFollowers(String opUid) {
        allowedFollowersRef = database.getReference("users/" + opUid + "/followers/allowedFollowers");
        allowedFollowersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    String uid = key.getValue(String.class);
                    if (uid.equals(myUid)) {
                        allowedFollowersKey = key.getKey().toString();
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
                allowedFollowersRef.child(allowedFollowersKey).removeValue();
            }
        }, 1000);

    }

    private void removeFromFollowing(final String opUid) {

        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    String uid = key.getValue(String.class);
                    if (uid.equals(opUid)) {
                        followingKey = key.getKey().toString();
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
                followingRef.child(followingKey).removeValue();
                Toast.makeText(FollowingListActivity.this,"Done", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

    private void getMyUserUid() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        myUid = user.getUid();
    }
}
