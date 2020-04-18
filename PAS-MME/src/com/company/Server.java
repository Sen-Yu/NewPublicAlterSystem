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
    CBSmessageList cbsmessageList;
    public Server(int port) {       //port 6000
        try {
            //
            this.datagramSocket = new DatagramSocket(port);
            this.trackingAreaList = new TrackingAreaList(this.datagramSocket);
            this.cbsmessageList = new CBSmessageList();
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
                //메세지 출처 IP,port,메세지 추출
                InetAddress datagramInetAddress = datagramPacket.getAddress();
                int datagramPort = datagramPacket.getPort();
                String message = new String(datagramPacket.getData()).trim();
                System.out.println("source ip : "+ datagramInetAddress + " , source port : "+ datagramPort);

                //메세지 체크
                String messageType = getMessageType(message);
                //serialNumber & messageidentifier
                JSONObject object = refacoring(message);
                if (messageType.equals("Write_Replace_Warning_Request")) {
                    //TAILIST 추출
                    JSONArray TAIlist = getTAILIST(message);
                    //3.Write-Replace Warning Confirm을 CBC에게 전송

                    ThreadSender confrim  = new  ThreadSender(this.datagramSocket,  makeConfirm(message),datagramInetAddress,datagramPort);
                    confrim.start();
                    //Request 수신시 송신을 위한 재난 문자 임시 저장
                    //addCBSmessage(object,datagramInetAddress,datagramPort);
                    System.out.println("send confirm to CBC");
                    //5.Write-Replace Warning Request를 eNB들에게 전송
                    //재난문자 재 구성
                    JSONObject msg = refactoring_Warning(message);
                    if( TAIlist != null){//TAIlist 매칭후 매칭된 eNB에게 전송
                        this.trackingAreaList.sendTAI(TAIlist,msg);
                    }else{ //가지고 있는 TAI 전부에 전송
                        this.trackingAreaList.sendALL(msg);
                    }

                    //sender.run(this.datagramSocket,warningConfirm.toJSONString().getBytes(), datagramInetAddress, 5000);
                    System.out.println("all send");
                } else if (messageType.equals("Write_Replace_Warning_Response")) {
                   CBSmessage cbsmessage = this.cbsmessageList.findmessage(object);
                   ThreadSender confrim  = new  ThreadSender(this.datagramSocket,  makeConfirm(message),cbsmessage.getSourceIP(),cbsmessage.getSourcePort());
                   confrim.start();
                    //저장소 확인
                } else if (messageType.equals("Shelter_Broadcast_Request")) {
                    //TAILIST 추출
                    JSONArray TAIlist = getTAILIST(message);
                    //3.Write-Replace Warning Confirm을 CBC에게 전송

                    ThreadSender confrim  = new  ThreadSender(this.datagramSocket,  makeConfirm(message),datagramInetAddress,datagramPort);
                    confrim.start();
                    //Request 수신시 송신을 위한 재난 문자 임시 저장
                    addCBSmessage(object,datagramInetAddress,datagramPort);
                    System.out.println("send confirm to CBC");
                    //5.Write-Replace Warning Request를 eNB들에게 전송
                    //재난문자 재 구성
                    JSONObject msg = refactoring_Warning(message);
                    if( TAIlist != null){//TAIlist 매칭후 매칭된 eNB에게 전송
                        this.trackingAreaList.sendTAI(TAIlist,msg);
                    }else{ //가지고 있는 TAI 전부에 전송
                        this.trackingAreaList.sendALL(msg);
                    }

                    //sender.run(this.datagramSocket,warningConfirm.toJSONString().getBytes(), datagramInetAddress, 5000);
                    System.out.println("all send");

                }else if (messageType.equals("Shelter_Broadcast_Response")) {
                    //파싱
                } else {

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public JSONObject refacoring(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);

            int serialNumber = safeLongToInt((long)jsonObject.get("serialNumber"));
            int messageidentifier = safeLongToInt((long)jsonObject.get("messageidentifier"));

            JSONObject obj = new JSONObject();
            obj.put("serialNumber",serialNumber);
            obj.put("messageidentifier",messageidentifier);

            return obj;
        }catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
    public JSONObject makeConfirm(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);

            String messageType = (String) jsonObject.get("messageType");
            int serialNumber = safeLongToInt((long)jsonObject.get("serialNumber"));
            int messageidentifier = safeLongToInt((long)jsonObject.get("messageidentifier"));


            JSONObject obj = new JSONObject();
            if(messageType.equals("Write_Replace_Warning_Request")){
                obj.put("messageType","Write_Replace_Warning_Confirm");
            }else if(messageType.equals("Write_Replace_Warning_Response")){
                obj.put("messageType","Write_Replace_Warning_Indication");
            }else if(messageType.equals("Shelter_Broadcast_Request")){
                obj.put("messageType","Shelter_Broadcast_Confirm");
            }

            obj.put("serialNumber",serialNumber);
            obj.put("messageidentifier",messageidentifier);
            obj.put("cause_E-UTRAN","Message accepted");

            return obj;

        } catch (ParseException e) {
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
    public JSONObject refactoring_Warning(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);

            String messageType = (String) jsonObject.get("messageType");
            int serialNumber = safeLongToInt((long)jsonObject.get("serialNumber"));
            int messageidentifier = safeLongToInt((long)jsonObject.get("messageidentifier"));
            String warningContentMessage = (String)jsonObject.get("warningContentMessage");
            int dataCodingScheme = safeLongToInt((long)jsonObject.get("dataCodingScheme"));
            JSONArray WarninAreaCoordinates = (JSONArray)jsonObject.get("WarninAreaCoordinates");

            JSONObject obj = new JSONObject();
            obj.put("messageType",messageType);
            obj.put("serialNumber",serialNumber);
            obj.put("messageidentifier",messageidentifier);
            obj.put("warningContentMessage",warningContentMessage);
            obj.put("dataCodingScheme",dataCodingScheme);
            obj.put("WarninAreaCoordinates",WarninAreaCoordinates);
            return obj;
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void addCBSmessage(JSONObject object,InetAddress inetAddress,int port){
        int serialNumber = safeLongToInt((long)object.get("serialNumber"));
        int messageidentifier = safeLongToInt((long)object.get("messageidentifier")) ;
        this.cbsmessageList.addCBSmessage(new CBSmessage(serialNumber,messageidentifier,inetAddress,port));
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
