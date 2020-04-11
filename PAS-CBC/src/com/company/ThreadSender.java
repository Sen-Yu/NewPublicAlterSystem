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

public class ThreadSender implements Runnable{
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


    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override public void run() {

            //재전송
            if(number == 0 ||count < number) {

                try {
                    socket.send(packet);
                    count++;
                    System.out.println("Resend:"+Thread.currentThread().getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("반복종료!!!!");
                timer.cancel();
            }

        }
    };

    public ThreadSender(){

    }
    public ThreadSender(DatagramSocket socket,byte buffer[], InetAddress sourceInetAddress, int sourcePort, int repetition, int number) {
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

        try {
            this.socket.send(packet);
            System.out.println(Thread.currentThread().getName());
            System.out.println("CBC->MME");
            //반복횟수랑 주기마다 재전송(this.repetition* 1000)
            this.timer.scheduleAtFixedRate(this.task,this.repetition * 1000, this.repetition* 100);

            //MME로부터 confirm올때까지 반복
            /*
            while(true){
                if(this.confirm) {
                    System.out.println("CBC->CBE");

                    this.socket.send(new DatagramPacket(this.confirmPacket.getData(),this.confirmPacket.getLength(),this.sourceInetAddress,2500));
                    System.out.println("재전송취소!!!!");
                    this.timer.cancel();

                    break;
                }
            }
             */

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("쓰레드 종료!!!!");





    }
    //2차방법
    public void confirm() throws IOException {
        this.task.cancel();
        this.timer.cancel();

        System.out.println("CBC->CBE");
        //MME로부터 받았기에 CBC는 CBE에게 confirm보냄
        this.socket.send(new DatagramPacket(this.confirmPacket.getData(),this.confirmPacket.getLength(),this.sourceInetAddress,3000));
        System.out.println("재전송취소!!!!");
        this.task.cancel();
        this.timer.cancel();
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
