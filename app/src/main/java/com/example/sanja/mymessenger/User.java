package com.example.sanja.mymessenger;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sanja on 6/21/2017.
 */

public class User implements Serializable{

    String fname;
    String lname;
    String gender;
    String uname;
    String email;
    String password;
    String uid;
    String imageUrl;
    boolean isSelected;

    String status;

    ArrayList<User> friendReq =new ArrayList<>();
    ArrayList<User> myFriends = new ArrayList<>();

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<User> getFriendReq() {
        return friendReq;
    }

    public void setFriendReq(ArrayList<User> friendReq) {
        this.friendReq = friendReq;
    }

    public ArrayList<User> getMyFriends() {
        return myFriends;
    }

    public void setMyFriends(ArrayList<User> myFriends) {
        this.myFriends = myFriends;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "User{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", gender='" + gender + '\'' +
                ", uname='" + uname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", uid='" + uid + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", status='" + status + '\'' +
                ", friendReq=" + friendReq +
                ", myFriends=" + myFriends +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getFname() != null ? !getFname().equals(user.getFname()) : user.getFname() != null)
            return false;
        if (getLname() != null ? !getLname().equals(user.getLname()) : user.getLname() != null)
            return false;
        if (getGender() != null ? !getGender().equals(user.getGender()) : user.getGender() != null)
            return false;
        if (getUname() != null ? !getUname().equals(user.getUname()) : user.getUname() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(user.getEmail()) : user.getEmail() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(user.getPassword()) : user.getPassword() != null)
            return false;
        if (getUid() != null ? !getUid().equals(user.getUid()) : user.getUid() != null)
            return false;
        if (getImageUrl() != null ? !getImageUrl().equals(user.getImageUrl()) : user.getImageUrl() != null)
            return false;
        if (getFriendReq() != null ? !getFriendReq().equals(user.getFriendReq()) : user.getFriendReq() != null)
            return false;
        return getMyFriends() != null ? getMyFriends().equals(user.getMyFriends()) : user.getMyFriends() == null;

    }

    @Override
    public int hashCode() {
        int result = getFname() != null ? getFname().hashCode() : 0;
        result = 31 * result + (getLname() != null ? getLname().hashCode() : 0);
        result = 31 * result + (getGender() != null ? getGender().hashCode() : 0);
        result = 31 * result + (getUname() != null ? getUname().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getUid() != null ? getUid().hashCode() : 0);
        result = 31 * result + (getImageUrl() != null ? getImageUrl().hashCode() : 0);
        result = 31 * result + (getFriendReq() != null ? getFriendReq().hashCode() : 0);
        result = 31 * result + (getMyFriends() != null ? getMyFriends().hashCode() : 0);
        return result;
    }
}
