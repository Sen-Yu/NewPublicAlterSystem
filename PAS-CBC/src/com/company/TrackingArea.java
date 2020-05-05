package com.company;

import java.net.InetAddress;

public class TrackingArea {

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

    }

    public InetAddress getInetAddress() {
        return this.InetAddress;
    }

    public int getTrackingAreacode() {
        return trackingAreacode;
    }

    public long getTrackingAreaIE() {
        return TrackingAreaIE;
    }
}
