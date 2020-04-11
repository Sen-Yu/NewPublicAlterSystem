package com.company;

public class ShelterInfo {
    private String sheleterName;
    private int coordinate;


    public ShelterInfo(String sheleterName,int coordinate){
        this.sheleterName = sheleterName;
        this.coordinate = coordinate;
    }

    public int getCoordinate() {
        return coordinate;
    }

    public String getSheleterName() {
        return sheleterName;
    }

}
