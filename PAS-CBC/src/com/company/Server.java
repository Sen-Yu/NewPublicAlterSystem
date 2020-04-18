package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

public class Server {
    DatagramSocket datagramSocket;

    public Server(int serverPort) {
        try {
            //저장할 메세지들
            AllMessage allMessage = new AllMessage();
            this.datagramSocket = new DatagramSocket(serverPort);
            Vector<ThreadSender> senders = new Vector<ThreadSender>();

            while (true) {
                byte buffer[] = new byte[512];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);

                //메세지 대기중
                System.out.println("--------------------------------------------------------------------------------");
                System.out.println("ready");
                System.out.println("--------------------------------------------------------------------------------");
                //메세지 받음
                this.datagramSocket.receive(datagramPacket);

                String message = new String(datagramPacket.getData()).trim();
                InetAddress datagramInetAddress = datagramPacket.getAddress();
                int datagramPort = datagramPacket.getPort();

                //메세지 체크
                String messageType = getMessageType(message);
                System.out.println("messageType:" + messageType);

                    if (messageType.equals("Emergency_Broadcast_Request")) {
                        //파싱
                            //Warning문자 생성
                            Warning warning = new Warning();
                            warning.jsonToWarning(message);
                            int warningClass = warning.classify();
                            warning.setMessageidentifier(warningClass);
                            warning.setSerialNumber(warningClass);


                            //전에 보낸 메세지가 있는지 확인

                            int same = allMessage.findSameWarning(warning.getSerialNumber(),warning.getSerialNumber());
                            //있는경우 업데이트번호 증가,내용 수정
                            if(same > -1){
                                 allMessage.getWarning(same).update();
                                 allMessage.getWarning(same).setWarningContentMessage(warning.getContext());
                                 //기존의 TAI사용
                            }
                            //없는경우 생성
                            else{
                                warning.setWarningContentMessage(warning.getContext());
                                int repetition = warning.getRepetitionPeriod();
                                int number = warning.getNumberOfBroadcasts();
                                //TAI설정하기
                                allMessage.getWarningVector().add(warning);
                            }


                            warning.setWarningContentMessage(warning.getContext());
                            int repetition = warning.getRepetitionPeriod();
                            int number = warning.getNumberOfBroadcasts();
                            //TAI설정
                            //현재는 임의로 설정
                            //Warning기록
                            allMessage.getWarningVector().add(warning);


                            //MME로 전송(2차방법)// 나중에 MME가 다수 이기때문에  재조정 필요
                            warning.setSender(this.datagramSocket,warning.WarningToJson().toJSONString().getBytes(), datagramInetAddress, datagramPort,repetition,number);
                            warning.getSender().setBroadPacket(datagramInetAddress,6000);
                            warning.send();

                            //MME주소와 포트

                    }

                    else if (messageType.equals("Write_Replace_Warning_Confirm")) {
                        int MessageIdentifier = getMessageIdentifier(message);
                        int SerialNumber = getSerialNumber(message);

                        //검색
                        int num = allMessage.findSameWarning(SerialNumber,MessageIdentifier);
                        System.out.println("num:"+num);
                        if(num != -1) {
                            Warning warning = allMessage.getWarning(num);
                            warning.getSender().setConfirmPacket(warning.getMessageidentifier(), warning.getSerialNumber(), warning.getWaringAreaCoordinates());

                            //재전송중단
                            warning.confirm();
                            //추가정보 문자때문에 바로 삭제 안함(kill받으면 삭제하게 만들예정)
                            allMessage.deleteWarning(num);
                        }
                    } else if (messageType.equals("Shelter_Broadcast_Request")) {
                        int MessageIdentifier = getMessageIdentifier(message);
                        int SerialNumber = getSerialNumber(message);

                        //검색
                        int num = allMessage.findSameWarning(SerialNumber,MessageIdentifier);
                        System.out.println("num:"+num);
                        Warning warning = allMessage.getWarning(num);
                        //이 재난문자의 TAI List를 불러옴
                        warning.getTrackingAreaVector();

                        //재난 정보를 분할



                        //MME로 전송

                    } else if (messageType.equals("Shelter_Broadcast_Confirm")) {
                        int MessageIdentifier = getMessageIdentifier(message);
                        int SerialNumber = getSerialNumber(message);

                        //검색
                        int num = allMessage.findSameWarning(SerialNumber,MessageIdentifier);
                        System.out.println("num:"+num);
                        Warning warning = allMessage.getWarning(num);
                        warning.getSender().setConfirmPacket(warning.getMessageidentifier(), warning.getSerialNumber(), warning.getWaringAreaCoordinates());
                        //재전송중단
                        warning.confirm();
                        //재전송 중단

                        //기록소에서 텀주고 삭제
                    } else {

                    }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public String getMessageType(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);

            System.out.println("recieve:" + jsonObject.toJSONString());
            return (String) jsonObject.get("messageType");
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getMessageIdentifier(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);

            return (int)(long) jsonObject.get("messageidentifier");
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getSerialNumber(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);

            return (int)(long) jsonObject.get("serialNumber");
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void distributeShelter(){

    }


    public static void main(String[] args) throws Exception {
        new Server(5000);
    }
}
