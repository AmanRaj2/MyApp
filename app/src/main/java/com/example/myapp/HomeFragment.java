package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener, PostAdapter.OnItemClickListener {
    private View view;
    private ImageView mAll, mFollowing, mRequest, mFollowers;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;
    private List<UploadPost> mUploadPost;
    private PostAdapter mPostAdapter;
    private FirebaseDatabase database;
    private DatabaseReference imagePostRef, followingRef, likeRef;
    private String myUid;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mAll = view.findViewById(R.id.home_imageView_all);
        mRequest = view.findViewById(R.id.home_imageView_request);
        mFollowers = view.findViewById(R.id.home_imageView_followers);
        mFollowing = view.findViewById(R.id.home_imageView_following);
        mProgressCircle = view.findViewById(R.id.progress_circle_home);

        mUploadPost = new ArrayList<>();


        mRecyclerView =(RecyclerView) view.findViewById(R.id.home_recycleView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        database = FirebaseDatabase.getInstance();
        getMyUserUid();
        followingRef = database.getReference("users/" + myUid + "/followers/following");
        //postRef = database.getReference("users/" + myUid + "/ImagePosts");

        setHomePost();

        mAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AllUsersListActivity.class);
                startActivity(intent);
            }
        });

       mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AllRequestsListActivity.class);
                startActivity(intent);
            }
        });

        mFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),FollowersListActivity.class);
                startActivity(intent);
            }
        });

        mFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),FollowingListActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    private void setHomePost() {
        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploadPost.clear();
                //adding mypost to Home #100
                imagePostRef = database.getReference("users/" + myUid + "/ImagePosts");
                imagePostRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot key : dataSnapshot.getChildren()) {
                            UploadPost uploadPost = key.getValue(UploadPost.class);
                            String mKey = key.getKey();
                            uploadPost.setmKey(mKey);
                            mUploadPost.add(uploadPost);
                            //mPostAdapter.notifyDataSetChanged();
                        }
                        mPostAdapter = new PostAdapter(getContext(),mUploadPost);
                        mRecyclerView.setAdapter(mPostAdapter);
                        mPostAdapter.setOnItemClickListener(HomeFragment.this);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                // end #100
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    String uid = user.getValue(String.class);
                    imagePostRef = database.getReference("users/" + uid + "/ImagePosts");
                    imagePostRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot key : dataSnapshot.getChildren()) {
                                UploadPost uploadPost = key.getValue(UploadPost.class);
                                String mKey = key.getKey();
                                uploadPost.setmKey(mKey);
                                mUploadPost.add(uploadPost);
                                //mPostAdapter.notifyDataSetChanged();
                            }
                            mPostAdapter = new PostAdapter(getContext(),mUploadPost);
                            mRecyclerView.setAdapter(mPostAdapter);
                            mPostAdapter.setOnItemClickListener(HomeFragment.this);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                //mPostAdapter.notifyDataSetChanged();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressCircle.setVisibility(View.INVISIBLE);
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
        Toast.makeText(getContext(),"Sorry you can't delete this post",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }
}
