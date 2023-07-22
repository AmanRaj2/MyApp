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

public class ViewLikesActivity extends AppCompatActivity implements all_user_adapter.OnItemClickListener{
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private all_user_adapter mAll_user_adapter;
    private List<userDetails> mUserDetails;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String myUid;
    private DatabaseReference likeRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_likes);

        mProgressBar = findViewById(R.id.view_likes_progressBar);
        mRecyclerView = findViewById(R.id.view_likes_recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserDetails = new ArrayList<>();

        mAll_user_adapter = new all_user_adapter(ViewLikesActivity.this,mUserDetails);
        mRecyclerView.setAdapter(mAll_user_adapter);
        mAll_user_adapter.setOnItemClickListener(ViewLikesActivity.this);

        getMyUserUid();
        database = FirebaseDatabase.getInstance();
        Bundle bundle = getIntent().getExtras();
        String uid = bundle.getString("uid");
        String selectedKey = bundle.getString("key");
        likeRef = database.getReference("users/" + uid + "/ImagePosts/" + selectedKey + "/likes");

        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            mAll_user_adapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                mAll_user_adapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewLikesActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        userDetails selectedItem = mUserDetails.get(position);
        final String userUid = selectedItem.getUserUid();
        if (userUid.equals(myUid)) {
            Toast.makeText(ViewLikesActivity.this, "This is Your account", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(ViewLikesActivity.this, OthersProfileActivity.class);
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
            Toast.makeText(ViewLikesActivity.this,name + " have not set his number",Toast.LENGTH_SHORT).show();
        } else {
            String s = "tel:"+number;
            Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse(s));
            startActivity(intent1);

        }

    }
    private void getMyUserUid() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myUid = user.getUid();
    }
}
