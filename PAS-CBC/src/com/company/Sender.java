package com.company;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.Buffer;
import java.util.EventListener;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class Sender implements Runnable{
    public static int count = 0 ;

    DatagramSocket socket;
    DatagramPacket packet;
    DatagramPacket confirmPacket;
    byte buffer[];

    InetAddress sourceInetAddress;
    InetAddress destInetAddress;
    int sourcePort;
    int destPort;

    int repetition;
    int number;

    boolean confirm;

    //Timer timer = new Timer();
    //MyTask task;

    public Sender(){

    }
    public Sender(DatagramSocket socket,byte buffer[], InetAddress sourceInetAddress, int sourcePort, int repetition, int number) {
        this.socket = socket;
        this.buffer = buffer;
        this.sourceInetAddress = sourceInetAddress;
        this.sourcePort = sourcePort;
        this.repetition = repetition;
        this.number = number;
    }



    @Override
    public void run() {
        System.out.println("dest ip : " + this.destInetAddress + " , dest port : " + this.destPort);
        this.packet = new DatagramPacket(this.buffer,this.buffer.length,this.destInetAddress,this.destPort);
        this.confirm = false;
        int count = 0;
        try {
            do{
                this.socket.send(this.packet);
                count++;
                System.out.println("CBC("+Thread.currentThread().getName()+")->MME");
                sleep(this.repetition * 100);
                //주기마다 쓰레드 휴식 후 실행
            }while(!confirm &&(this.number == 0 || count <= this.number));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("쓰레드 종료!!!!");
    }

    public void confirm() throws IOException {
        this.confirm = true;
        System.out.println("CBC("+Thread.currentThread().getName()+")->CBE");
        //MME로부터 받았기에 CBC는 CBE에게 confirm보냄
        this.socket.send(new DatagramPacket(this.confirmPacket.getData(),this.confirmPacket.getLength(),this.sourceInetAddress,3000));
        System.out.println("재전송취소!!!!");
    }

    public void setBroadPacket(InetAddress destInetAddress, int destPort){
        this.destInetAddress = destInetAddress;
        this.destPort = destPort;
    }

    public void setConfirmPacket(int serialNumber,int messageidentifier, String waringAreaCoordinates){
        JSONObject confirmObj = new JSONObject();
        confirmObj.put("serialNumber",serialNumber);
        confirmObj.put("messageidentifier",messageidentifier);
        confirmObj.put("warningAreaCoordinate",waringAreaCoordinates);
        this.buffer = confirmObj.toJSONString().getBytes();
        this.confirmPacket = new DatagramPacket(buffer, buffer.length);
    }

}
