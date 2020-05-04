package com.company;

import java.util.Vector;

public class Address_GU {

    String gu;
    Vector<ShelterInfo> shelterInfos;

    public Address_GU(String gu){
        this.gu = gu;
        shelterInfos = new Vector<ShelterInfo>();
    }

    public Vector<ShelterInfo> getShelterInfos() {
        return this.shelterInfos;
    }

    public ShelterInfo getInfo(int num){
        return  this.shelterInfos.get(num);
    }

    public String getGu() {
        return this.gu;
    }

    public void add(ShelterInfo info){
        this.shelterInfos.add(info);
    }
}
