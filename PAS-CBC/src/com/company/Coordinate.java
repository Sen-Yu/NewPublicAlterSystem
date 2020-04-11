package com.company;

import java.util.Vector;

public class Coordinate {
    double latitude;
    double longitude;

    public Coordinate(){

    }
    public void init(double latitude,double longitude){
        this.latitude = latitude;
        this.longitude =longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
