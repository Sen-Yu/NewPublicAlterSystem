package com.company;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ThreadSender extends Thread {

    DatagramSocket socket;
    JSONObject jsonObject;
    InetAddress destInetAddress;
    int destPort;

    public ThreadSender(DatagramSocket socket, JSONObject jsonObject, InetAddress destInetAddress,int destPort){
        this.socket = socket;
        this.jsonObject = jsonObject ;
        this.destInetAddress = destInetAddress;
        this.destPort = destPort;
    }

    @Override
    public void run() {
        byte buffer[] = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        buffer = jsonObject.toJSONString().getBytes();
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length,this.destInetAddress,this.destPort);

        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}

