package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.InetAddress;
import java.util.Vector;

import static java.lang.Integer.parseInt;

public class TrackingArea {
    //TAI - IP
    long TrackingAreaIE;
    int plmnIdentity;
    int trackingAreacode;
    InetAddress InetAddress;

    public TrackingArea(int TrackingAreaIE,InetAddress InetAddress ){
        this.TrackingAreaIE = TrackingAreaIE;
        this.InetAddress = InetAddress;
    }
    public TrackingArea(int plmnIdentity, int trackingAreacode, InetAddress InetAddress ){
        this.plmnIdentity = plmnIdentity;
        this.trackingAreacode = trackingAreacode;
        this.InetAddress = InetAddress;
       // make();
    }

    public long getTrackingAreaIE() {
        return TrackingAreaIE;
    }

    public InetAddress getInetAddress(){
        return this.InetAddress;
    }

    public int getPlmnIdentity() {
        return plmnIdentity;
    }

    public int getTrackingAreacode() {
        return trackingAreacode;
    }

    //아직 사용안하는 함수
    public void make(){
        String track = "";

        int highestOneBit=Integer.highestOneBit(this.trackingAreacode);
        for(int i = 0 ; i < 15 ; i++){
            highestOneBit = highestOneBit>>1;
            if(highestOneBit >= 0 ){
                track += "0";
            }
            else{
                track += Integer.toBinaryString(this.trackingAreacode);
                break;
            }
        }
        track += Integer.toBinaryString(this.trackingAreacode);
        String plmn = Integer.toBinaryString(this.plmnIdentity);
        System.out.println(this.trackingAreacode +"->"+track+","+this.plmnIdentity+"->"+plmn);
        this.TrackingAreaIE = Long.parseLong(new String(plmn+track), 2);
        System.out.println(Long.toBinaryString(TrackingAreaIE));
    }
}
