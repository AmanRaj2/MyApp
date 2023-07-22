
package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

public class AllRequestsListActivity extends AppCompatActivity implements all_request_adapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private all_request_adapter mAll_request_adapter;
    private ProgressBar mProgressBar;
    private DatabaseReference  allowedFollowersRef, RequestsRef, sentRequestsRef, followingRef;
    private List<userDetails> mUserDetails;
    private FirebaseDatabase database;
    private String myUid, requestkey, sentRequestKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_requests_list);

        mProgressBar = findViewById(R.id.all_request_item_progressBar);
        mRecyclerView = findViewById(R.id.all_request_item_recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserDetails = new ArrayList<>();

        mAll_request_adapter = new all_request_adapter(AllRequestsListActivity.this, mUserDetails);
        mRecyclerView.setAdapter(mAll_request_adapter);
        mAll_request_adapter.setOnItemClickListener(AllRequestsListActivity.this);
        getMyUserUid();
        database = FirebaseDatabase.getInstance();
        RequestsRef = database.getReference("users/" + myUid + "/followers/requests");
        allowedFollowersRef = database.getReference("users/" + myUid + "/followers/allowedFollowers");
        Toast.makeText(this,"Long press to accept or deny", Toast.LENGTH_SHORT).show();

        RequestsRef.addValueEventListener(new ValueEventListener() {
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
                            mAll_request_adapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                mAll_request_adapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllRequestsListActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void getMyUserUid() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        myUid = user.getUid();
    }

    @Override
    public void onItemClick(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        final String userUid = selectedItem.getUserUid();
        if (userUid.equals(myUid)) {
            Toast.makeText(AllRequestsListActivity.this, "This is Your account", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(AllRequestsListActivity.this, OthersProfileActivity.class);
            intent.putExtra("uid", userUid);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onAllow(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        final String opUid = selectedItem.getUserUid();
        sentRequestsRef = database.getReference("users/" + opUid + "/followers/sentRequests");

        addToFollowersList(opUid);
        //addToFollowingList
        followingRef = database.getReference("users/" + opUid + "/followers/following");
        followingRef.child(followingRef.push().getKey().toString()).setValue(myUid);//

        getRequestKey(opUid);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestsRef.child(requestkey).removeValue();
                Toast.makeText(AllRequestsListActivity.this, "Done",Toast.LENGTH_SHORT).show();
            }
        },1000);

        getSentSentRequestKey();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sentRequestsRef.child(sentRequestKey).removeValue();
            }
        },1000);
    }

    @Override
    public void onDeny(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        final String opUid = selectedItem.getUserUid();

        sentRequestsRef = database.getReference("users/" + opUid + "/followers/sentRequests");

        getRequestKey(opUid);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestsRef.child(requestkey).removeValue();
                Toast.makeText(AllRequestsListActivity.this, "Done",Toast.LENGTH_SHORT).show();
            }
        },1000);

        getSentSentRequestKey();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sentRequestsRef.child(sentRequestKey).removeValue();
            }
        },1000);


    }

    private void getSentSentRequestKey() {
        sentRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    String currentUid = user.getValue(String.class);
                    if(currentUid.equals(myUid)) {
                        sentRequestKey = user.getKey().toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void getRequestKey(final String opUid) {
        RequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    String currentUid = user.getValue(String.class);
                    if(currentUid.equals(opUid)) {
                        requestkey = user.getKey().toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void addToFollowersList(final String opUid) {
        String uploadId = allowedFollowersRef.push().getKey();
        allowedFollowersRef.child(uploadId).setValue(opUid);
    }
}

