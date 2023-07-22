package com.example.myapp;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class all_user_adapter extends RecyclerView.Adapter<all_user_adapter.allUserViewHolder> {
    private Context mContext;
    private List<userDetails> mUserDetails;
    private OnItemClickListener mListener;


    public all_user_adapter (Context context, List<userDetails> userDetails_) {
        mContext = context;
        mUserDetails = userDetails_;
    }

    @NonNull
    @Override
    public allUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.all_user_item, parent, false);
        return new allUserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull allUserViewHolder holder, int position) {
        userDetails userDetailsCurrent = mUserDetails.get(position);
        holder.textViewName.setText(userDetailsCurrent.getName());
        String profilePic = userDetailsCurrent.getProfilePic();
        if (profilePic.isEmpty()) {
            Picasso.with(mContext)
                    .load("@drawable/ic_profile")
                    .placeholder(R.drawable.ic_profile)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            Picasso.with(mContext)
                    .load(userDetailsCurrent.getProfilePic())
                    .placeholder(R.drawable.ic_profile)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return mUserDetails.size();
    }

    public  class allUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewName;
        public ImageView imageView;

        public allUserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.all_user_item_textView);
            imageView = itemView.findViewById(R.id.all_user_item_imageView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("select action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "CALL");

            doWhatever.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onWhatEverClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
