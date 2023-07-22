package com.example.myapp;

public class userDetails {
    private String name, phone, profilePic, userUid, email, fb, insta, whatsApp;

    public userDetails() {
        //empty constructor

    }

    public userDetails(String userUid, String name, String profilePic, String phone, String email, String fb, String insta, String whatsApp) {
        this.userUid = userUid;
        this.name = name;
        this.profilePic = profilePic;
        this.phone = phone;
        this.email = email;
        this.fb = fb;
        this.insta = insta;
        this.whatsApp = whatsApp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getInsta() {
        return insta;
    }

    public void setInsta(String insta) {
        this.insta = insta;
    }

    public String getWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        this.whatsApp = whatsApp;
    }
}
