package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Vector;

public class Server {
    DatagramSocket datagramSocket;
    InetAddress inetAddress;

    public Server(int port) {       //port 7000
        try {

            this.datagramSocket = new DatagramSocket(port);
            this.inetAddress = InetAddress.getLocalHost();


            System.out.printf("this.ip="+this.inetAddress);
            while (true) {
                byte buffer[] = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
                //메세지 대기중
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("eNB ready");
                System.out.println("--------------------------------------------------------------------------------");
                //메세지 받음
                this.datagramSocket.receive(datagramPacket);
                //thread

                InetAddress datagramInetAddress = datagramPacket.getAddress();
                int datagramPort = datagramPacket.getPort();
                String message = new String(datagramPacket.getData()).trim();

                System.out.println("server ip : "+ datagramInetAddress + " , server port : "+ datagramPort);
                System.out.println(message);
                //메세지 체크
                String messageType = getMessageType(message);

                if (messageType.equals("Write_Replace_Warning_Request")) {
                    System.out.println("재난문자이다.");
                    //리팩토링
                    JSONObject msg = refactoring(message);
                    //eNB js서버로 문자 전송
                    ThreadSender sender = new ThreadSender(this.datagramSocket,msg,this.inetAddress,7500);
                    sender.run();
                    System.out.println("send");
                } else if (messageType.equals("Write_Replace_Warning_Response")) {
                    //저장소 확인
                } else if (messageType.equals("Shelter_Broadcast_Request")) {
                    //파싱
                } else if(messageType.equals("Signal")){

                } else{

                }

                //thread

            }
        } catch (IOException e) {
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
                System.out.println("TAI("+i+")");
                JSONObject TAIObject = (JSONObject) TAIAraay.get(i);
                System.out.print("TAI'sINFO:"+TAIObject.get("plmnIdentity")+","+TAIObject.get("trackingAreacode"));
            }


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

            //String messageType = (String) jsonObject.get("messageType");
            int messageidentifier = (int)(long)jsonObject.get("messageidentifier");
            int serialNumber = (int)(long)jsonObject.get("serialNumber");
            String warningContentMessage = (String)jsonObject.get("warningContentMessage");
            int dataCodingScheme = (int)(long)jsonObject.get("dataCodingScheme");

            JSONObject obj = new JSONObject();
            //obj.put("messageTypee",messageType);
            obj.put("messageidentifier",messageidentifier);
            obj.put("serialNumber",serialNumber);
            obj.put("warningContentMessage",warningContentMessage);
            obj.put("dataCodingScheme",dataCodingScheme);
            //부가요소임
            //obj.put("warningAreaCoordinates");


            return obj;
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en =
                    NetworkInterface.getNetworkInterfaces();
            while(en.hasMoreElements()) {
                NetworkInterface interf = en.nextElement();
                Enumeration<InetAddress> ips = interf.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress inetAddress = ips.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return null;
    }

}
