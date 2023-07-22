
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

public class FollowersListActivity extends AppCompatActivity implements followers_adapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private followers_adapter mFollowers_adapter;
    private ProgressBar mProgressBar;
    private DatabaseReference allowedFollowersRef, followingRef;
    private List<userDetails> mUserDetails;
    private FirebaseDatabase database;
    private String myUid, id, followingKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_list);

        mProgressBar = findViewById(R.id.followers_item_progressBar);
        mRecyclerView = findViewById(R.id.followers_item_recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserDetails = new ArrayList<>();

        mFollowers_adapter = new followers_adapter(FollowersListActivity.this, mUserDetails);
        mRecyclerView.setAdapter(mFollowers_adapter);

        mFollowers_adapter.setOnItemClickListener(FollowersListActivity.this);

        getMyUserUid();
        database = FirebaseDatabase.getInstance();
        allowedFollowersRef = database.getReference("users/" + myUid + "/followers/allowedFollowers");

        setList();
        Toast.makeText(this,"Long press to take action", Toast.LENGTH_SHORT).show();
    }

    private void setList() {
        allowedFollowersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserDetails.clear();
                DatabaseReference currentUser;
                for(DataSnapshot user : dataSnapshot.getChildren()) {
                    String uid = user.getValue(String.class);
                    currentUser = database.getReference("users/" + uid+ "/info");

                    currentUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userDetails currentUserDetails = dataSnapshot.getValue(userDetails.class);
                            mUserDetails.add(currentUserDetails);
                            mFollowers_adapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                mFollowers_adapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FollowersListActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        final String userUid = selectedItem.getUserUid();
        if (userUid.equals(myUid)) {
            Toast.makeText(FollowersListActivity.this, "This is Your account", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(FollowersListActivity.this, OthersProfileActivity.class);
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
            Toast.makeText(FollowersListActivity.this,name + " have not set his number",Toast.LENGTH_SHORT).show();
        } else {
            String s = "tel:"+number;
            Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse(s));
            startActivity(intent1);

        }
    }

    @Override
    public void onRemove(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        final String opUid = selectedItem.getUserUid();

        allowedFollowersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    String uid = key.getValue(String.class);
                    if (uid.equals(opUid)) {
                        id = key.getKey().toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Handler delayProgressbar = new Handler();
        delayProgressbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                allowedFollowersRef.child(id).removeValue();
                Toast.makeText(FollowersListActivity.this,"Removed from followers list", Toast.LENGTH_SHORT).show();
            }
        },1000);

        followingRef = database.getReference("users/" + opUid + "/followers/following");
        getFollowingKey();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                followingRef.child(followingKey).removeValue();
            }
        }, 1000);
    }

    private void getFollowingKey() {
        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    String uid = user.getValue(String.class);
                    if (uid.equals(myUid)) {
                        followingKey = user.getKey().toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getMyUserUid() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        myUid = user.getUid();
    }
}

