package com.example.myapp;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UploadPost {
    private String mComment, mImageUrl, mKey, mUserName, mProfilePic, userUid;
    private String uploadTime;

    public UploadPost() {
        //empty constructor needed

    }

    public UploadPost(String comment, String imageUrl, String mUserName, String mProfilePic, String uploadTime, String userUid, String mKey) {
        mComment = comment;
        mImageUrl = imageUrl;
        this.mUserName = mUserName;
        this.mProfilePic = mProfilePic;
        this.uploadTime = uploadTime;
        this.userUid = userUid;
        this.mKey = mKey;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String comment) {
        mComment = comment;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmProfilePic() {
        return mProfilePic;
    }

    public void setmProfilePic(String mProfilePic) {
        this.mProfilePic = mProfilePic;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
