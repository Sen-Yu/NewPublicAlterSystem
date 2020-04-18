package com.company;

import java.net.InetAddress;

public class CBSmessage {

    int serialNumber;
    int messageidentifier;

    InetAddress sourceIP;
    int sourcePort;

    public CBSmessage(int serialNumber, int messageidentifier, InetAddress sourceIP, int sourcePort){
        this.serialNumber = serialNumber;
        this.messageidentifier = messageidentifier;
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
    }

    public InetAddress getSourceIP() {
        return this.sourceIP;
    }

    public int getSourcePort() {
        return this.sourcePort;
    }

    public int getMessageidentifier() {
        return this.messageidentifier;
    }

    public int getSerialNumber() {
        return this.serialNumber;
    }
}
