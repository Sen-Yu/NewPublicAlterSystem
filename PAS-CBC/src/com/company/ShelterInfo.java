package com.company;

public class ShelterInfo {
    private String shelterLastAddress;
    private String sheleterName;
    private String coordinate;

    public ThreadSender sender;
    public boolean confirm;

    public ShelterInfo(String shelterLastAddress,String sheleterName,String coordinate){
        this.shelterLastAddress = shelterLastAddress;
        this.sheleterName = sheleterName;
        this.coordinate = coordinate;
    }

    public String getCoordinate() {
        return this.coordinate;
    }

    public String getSheleterName() {
        return this.sheleterName;
    }

    public String getShelterLastAddress(){return this.shelterLastAddress;}
}
