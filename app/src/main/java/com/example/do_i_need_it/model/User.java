package com.example.do_i_need_it.model;

public class User {

    private String fullName, phoneNumber, country, dob, profileImageLink;

    public User() {}

    public User(String fullName, String phoneNumber, String country, String profileImageLink) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.profileImageLink = profileImageLink;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getProfileImageLink() {
        return profileImageLink;
    }

    public void setProfileImageLink(String profileImageLink) {
        this.profileImageLink = profileImageLink;
    }
}
