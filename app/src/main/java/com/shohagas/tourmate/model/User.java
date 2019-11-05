package com.shohagas.tourmate.model;

public class User {
    private String mFullName;
    private String  mPhone;
    private String mEmail;

    public User() {
    }

    public User(String mFullName, String  mPhone, String mEmail) {
        this.mFullName = mFullName;
        this.mPhone = mPhone;
        this.mEmail = mEmail;
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }
}
