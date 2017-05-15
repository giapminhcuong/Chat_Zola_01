package com.framgia.arutalk.model;

import android.net.Uri;

import com.google.firebase.database.Exclude;

/**
 * Created by Admin on 22/5/2017.
 */

public class User {

    private String mUserId;
    private String mEmail;
    @Exclude
    private String mPassword;
    private String mFirstName;
    private String mLastName;
    private Gender mGender;
    private String mUriPhoto;

    public User() {
    }

    public User(String userId, String email, String firstName, String lastName, Gender gender) {
        mUserId = userId;
        mEmail = email;
        mFirstName = firstName;
        mLastName = lastName;
        mGender = gender;
    }

    public void setMoreInfo(String password, String uriPhoto) {
        mPassword = password;
        mUriPhoto = uriPhoto;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public Gender getGender() {
        return mGender;
    }

    public void setGender(Gender gender) {
        mGender = gender;
    }

    public String getUriPhoto() {
        return mUriPhoto;
    }

    public void setUriPhoto(String uriPhoto) {
        mUriPhoto = uriPhoto;
    }

    @Override
    public String toString() {
        return getUserId() + "\n"
                + getEmail() + "\n"
                + getFirstName() + "\n"
                + getLastName() + "\n"
                + getGender() + "\n";
    }

    public enum Gender {
        MALE, FEMALE
    }
}
