package com.example.myapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context mContext;
    private List<UploadPost> mUploadPosts;
    private OnItemClickListener mListener;
    private List<Long> time = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference  likeRef;
    private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


    public PostAdapter(Context context, List<UploadPost> uploads) {
        mContext = context;
        Log.d("size", "constructor "+ String.valueOf(uploads.size()));
        mUploadPosts = sortPostWithTime(uploads);
    }


    private List<UploadPost> sortPostWithTime(List<UploadPost> uploads) {
        for (UploadPost i : uploads) {
            time.add(Long.valueOf(i.getUploadTime()));
        }
        Log.d("size", String.valueOf(uploads.size()));

        int n = time.size();
        for (int i=0 ; i<n-1 ; i++ ) {
            for (int j=i; j<n ; j++) {
                if (time.get(i) < time.get(j)) {

                    long temp = time.get(i);
                    time.set(i,time.get(j));
                    time.set(j,temp);

                    UploadPost tempPost = uploads.get(i);
                    uploads.set(i,uploads.get(j));
                    uploads.set(j,tempPost);
                }
            }
        }
        //Log.d("time after", String.valueOf(time.get(0)));
        return uploads;
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        UploadPost uploadPostCurrent = mUploadPosts.get(position);
        holder.textViewComment.setText(uploadPostCurrent.getmComment());
        holder.userName.setText(uploadPostCurrent.getmUserName());
        //
        String date = new SimpleDateFormat("d-MMMM-yyyy", Locale.getDefault()).format(new Date(Long.parseLong(uploadPostCurrent.getUploadTime())));
        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date(Long.parseLong(uploadPostCurrent.getUploadTime())));
        String dateTime = date + " at " + time;
        holder.uploadTime.setText(dateTime);

        //

        //setButtonColor
            final String selectedKey = uploadPostCurrent.getmKey();
            final String uid = uploadPostCurrent.getUserUid();
            final boolean[] allow = {true};
            final String[] keyUnlike = new String[1];

            likeRef = database.getReference("users/" + uid + "/ImagePosts/" + selectedKey + "/likes");
            likeRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String count = dataSnapshot.getChildrenCount() + "   ";
                    holder.likeBtn.setText(count + "likes");
                    holder.likeBtn.setTextColor(R.color.blackColor);

                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        if (id.getValue().toString().equals(myUid)){
                            allow[0] = false;
                            keyUnlike[0] = id.getKey();
                            holder.likeBtn.setText(count + "likes");
                            holder.likeBtn.setTextColor(R.color.whiteColor);
                        }
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        //

        Picasso.with(mContext)
                .load(uploadPostCurrent.getmImageUrl())
                .placeholder(R.drawable.ic_cloud_download)
                .fit()
                .centerInside()
                .into(holder.imageView);

        String pic = uploadPostCurrent.getmProfilePic();
        if (pic.isEmpty()) {

        } else {
            Picasso.with(mContext)
                    .load(pic)
                    .placeholder(R.drawable.ic_profile)
                    .fit()
                    .centerCrop()
                    .into(holder.profilePic);
        }

    }



    @Override
    public int getItemCount() {
        return mUploadPosts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewComment, userName, uploadTime;
        public ImageView imageView, profilePic;
        public Button likeBtn, commentBtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewComment = itemView.findViewById(R.id.textView_postItem);
            imageView = itemView.findViewById(R.id.imageView_postItem);
            profilePic = itemView.findViewById(R.id.post_item_profilePic);
            userName = itemView.findViewById(R.id.post_item_userName);
            uploadTime = itemView.findViewById(R.id.post_item_time);
            likeBtn = itemView.findViewById(R.id.post_item_like);
            commentBtn = itemView.findViewById(R.id.post_item_comment);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            likeBtn.setOnClickListener(this);
            commentBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null ) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    switch (v.getId()){
                        case R.id.post_item_like:
                            mListener.onLikeClick(position);
                            break;
                        case R.id.post_item_comment:
                            mListener.onCommentClick(position);
                            break;
                        default:
                            mListener.onItemClick(position);
                    }
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Liked By");
            MenuItem delete = menu.add(Menu.NONE,2,2,"Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener != null ) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1 :
                            mListener.onLikedByClick(position);
                            return true;
                        case 2 :
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onLikeClick(int position);

        void onCommentClick(int position);

        void onLikedByClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}


