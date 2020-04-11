package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

public class TrackingAreaList {
    Vector<TrackingArea> trackingAreaVector;
    DatagramSocket socket;

    public TrackingAreaList(DatagramSocket socket){
        this.trackingAreaVector = new Vector<TrackingArea>();
        this.socket = socket;
    }



    public void setSocket(DatagramSocket socket){
        this.socket = socket;
    }


    //현재 MME에 해당 tai가 있는지 확인하고 있는거는  다음작업 없으면 다음 tai
    public boolean hasTAI(int tai,JSONObject obj){
        for(int i = 0 ; i < (this.trackingAreaVector.size() ) ; i++) {

            TrackingArea TAI = this.trackingAreaVector.get(i);

            //해당 taicode와 같다
            if(TAI.getTrackingAreacode() == tai){
                System.out.println("같은거 찾음:"+i+">>>"+TAI.getPlmnIdentity()+","+TAI.getTrackingAreacode()+">>>"+TAI.getInetAddress());
                send(obj, TAI.getInetAddress());
                return true;
            }
        }
        return false;
    }


    public void sendTAI(JSONArray TAIArray,JSONObject msg){

        if (TAIArray.size() == 0) {

        } else {
            for (int i = 0; i < TAIArray.size(); i++) {
                JSONObject TAIObject = (JSONObject) TAIArray.get(i);

                hasTAI(safeLongToInt((long)TAIObject.get("trackingAreacode")),msg);
            }
        }
    }

   //TAI 하나에게 전송
    public void send(JSONObject obj, InetAddress inetAddress){
        //eNB의 포트는 7000으로 고정
        ThreadSender sender = new ThreadSender(this.socket,obj,inetAddress,7000);
        sender.start();
    }

    //모든 TAI에게 전송
    public void sendALL(JSONObject obj){

        for(int i = 0 ; i < this.trackingAreaVector.capacity() ;i++){
            send(obj, this.trackingAreaVector.get(i).getInetAddress());
        }

    }

    public Vector<TrackingArea> getTrackingAreaVector() {
        return trackingAreaVector;
    }

    // long 값을 int로 변환
    public static int safeLongToInt(long l) {
        int i = (int)l;
        if ((long)i != l) {
            throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
        }
        return i;
    }

}
