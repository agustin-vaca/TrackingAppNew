package com.example.trackingappnew.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class UserRoute implements Parcelable{

    private String userName;
    private Date startTime;
    private ArrayList<GeoPoint> tripCoordinates;

    public UserRoute(String userName, Date startTime, Date endTime, ArrayList<GeoPoint> tripCoordinates) {
        this.userName = userName;
        this.startTime = startTime;
        this.tripCoordinates = tripCoordinates;
    }

    public UserRoute() {
    }

    protected UserRoute(Parcel in) {
        userName = in.readString();
    }

    public static final Creator<UserRoute> CREATOR = new Creator<UserRoute>() {
        @Override
        public UserRoute createFromParcel(Parcel in) {
            return new UserRoute(in);
        }

        @Override
        public UserRoute[] newArray(int size) {
            return new UserRoute[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public ArrayList<GeoPoint> getTripCoordinates() {
        return tripCoordinates;
    }

    public void setTripCoordinates(ArrayList<GeoPoint> tripCoordinates) {
        this.tripCoordinates = tripCoordinates;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
    }
}
