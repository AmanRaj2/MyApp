
package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class AllUsersListActivity extends AppCompatActivity implements all_user_adapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private all_user_adapter mAll_user_adapter;
    private ProgressBar mProgressBar;
    private DatabaseReference nameRef, profilePicRef, phoneRef, usersRef;
    private List<userDetails> mUserDetails;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_list);

        mProgressBar = findViewById(R.id.all_user_item_progressBar);
        mRecyclerView = findViewById(R.id.all_user_item_recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserDetails = new ArrayList<>();

        mAll_user_adapter = new all_user_adapter(AllUsersListActivity.this,mUserDetails);
        mRecyclerView.setAdapter(mAll_user_adapter);
        mAll_user_adapter.setOnItemClickListener(AllUsersListActivity.this);
        getMyUserUid();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserDetails.clear();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    userDetails currentUserDetails = user.child("info").getValue(userDetails.class);
                    mUserDetails.add(currentUserDetails);
                }
                mAll_user_adapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllUsersListActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });


    }

    private void getMyUserUid() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myUid = user.getUid();
    }

    @Override
    public void onItemClick(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        final String userUid = selectedItem.getUserUid();
        if (userUid.equals(myUid)) {
            Toast.makeText(AllUsersListActivity.this, "This is Your account", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(AllUsersListActivity.this, OthersProfileActivity.class);
            intent.putExtra("uid", userUid);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onWhatEverClick(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        String name = selectedItem.getName();
        String number = selectedItem.getPhone();
        if(number.equals("")) {
            Toast.makeText(AllUsersListActivity.this,name + " have not set his number",Toast.LENGTH_SHORT).show();
        } else {
            String s = "tel:"+number;
            Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse(s));
            startActivity(intent1);

        }

    }
}

