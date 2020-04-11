package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;
import java.util.Vector;

public class Server {
    DatagramSocket datagramSocket;
    TrackingAreaList trackingAreaList;
    public Server(int port) {       //port 6000
        try {
            //
            this.datagramSocket = new DatagramSocket(port);
            this.trackingAreaList = new TrackingAreaList(this.datagramSocket);
            //정보 초기화
            //
            InetAddress address = InetAddress.getLocalHost();


            Vector<TrackingArea> vec= this.trackingAreaList.getTrackingAreaVector();
            for(int i = 0 ; i < 20 ; i+=2){
                vec.add(new TrackingArea( 45008,i, address));
            }

            //vec.add(new TrackingArea(int TrackingAreaIE,InetAddress InetAddress ));


            while (true) {
                byte buffer[] = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
                //메세지 대기중
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("MME ready");
                System.out.println("--------------------------------------------------------------------------------");
                //메세지 받음
                this.datagramSocket.receive(datagramPacket);
                //thread

                InetAddress datagramInetAddress = datagramPacket.getAddress();
                int datagramPort = datagramPacket.getPort();
                String message = new String(datagramPacket.getData()).trim();
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(message);
                System.out.println("server ip : "+ datagramInetAddress + " , server port : "+ datagramPort);
                //메세지 체크
                String messageType = getMessageType(message);

                if (messageType.equals("Write_Replace_Warning_Request")) {
                    //문자구성요소 재 구성
                    JSONObject msg = refactoring(message);
                    //TAILIST 비교
                    JSONArray TAIlist = getTAILIST(message);

                    if( TAIlist != null){//TAIlist 매칭후 매칭된 eNB에게 전송
                        this.trackingAreaList.sendTAI(TAIlist,jsonObject);
                    }else{ //가지고 있는 TAI 전부에 전송
                        this.trackingAreaList.sendALL(jsonObject);
                    }


                    //sender.run(this.datagramSocket,warningConfirm.toJSONString().getBytes(), datagramInetAddress, 5000);
                    System.out.println("all send");
                } else if (messageType.equals("Write_Replace_Warning_Response")) { //CBC에게 ㄱㄱ
                    //저장소 확인
                } else if (messageType.equals("Shelter_Broadcast_Request")) {
                    //파싱
                } else {

                }

                //thread

            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void trackingAreaList(DatagramSocket datagramSocket) {
    }

    //MME번호 / 좌표 / ip 체크
    public void checkTAI(){

    }
    public String getMessageType(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);

            System.out.println("recieve:" + jsonObject.toJSONString());
            String messageType = (String) jsonObject.get("messageType");
            return messageType;
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONArray getTAILIST(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);
            JSONArray TAIAraay = (JSONArray) jsonObject.get("ListofTAIs");
            for(int i = 0 ; i < TAIAraay.size()  ; i++) {
                System.out.print("TAI("+i+"):");
                JSONObject TAIObject = (JSONObject) TAIAraay.get(i);
                System.out.println("TAI'sINFO:"+TAIObject.get("plmnIdentity")+","+TAIObject.get("trackingAreacode"));
            }
            System.out.println();

            return TAIAraay;
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONObject refactoring(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);

            String messageType = (String) jsonObject.get("messageType");
            int serialNumber = safeLongToInt((long)jsonObject.get("serialNumber"));
            int messageidentifier = safeLongToInt((long)jsonObject.get("messageidentifier"));
            String warningContentMessage = (String)jsonObject.get("warningContentMessage");
            JSONArray WarninAreaCoordinates = (JSONArray)jsonObject.get("WarninAreaCoordinates");

            JSONObject obj = new JSONObject();
            obj.put("messageTypee",messageType);
            obj.put("serialNumber",serialNumber);
            obj.put("messageidentifier",messageidentifier);
            obj.put("warningContentMessage",warningContentMessage);
            obj.put("WarninAreaCoordinates",WarninAreaCoordinates);
            return obj;
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
