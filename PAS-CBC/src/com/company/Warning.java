package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

public class Warning {
    //해독 전
    String source;  //발송처
    String calamity;//재난종류
    String impactArea;//발생지
    String waringAreaCoordinates;//발생좌표
    String context;//메시지 내용

    //해독 후
    String messageType;
    Messageidentifier messageidentifier;
    SerialNumber serialNumber;
    String warningContentMessage;
    int dataCodingScheme;
    int repetitionPeriod;
    int numberOfBroadcasts;
    Vector<TrackingArea> trackingAreaVector;

    //전송thread
    ThreadSender sender;

    Shelter shelter;

    DatagramSocket  datagramSocket;
    InetAddress inetAddress;
    int portnum = 6000;

    public Warning(DatagramSocket datagramSocket,InetAddress inetAddress){
        //변환전
        this.source = new String();
        this.calamity = new String();
        this.impactArea = new String();
        //this.waringAreaCoordinates = new Vector<Coordinate>();
        this.waringAreaCoordinates = new String();
        this.context = new String();

        //변환후
        this.messageType = new String();
        this.messageidentifier = new Messageidentifier();
        this.serialNumber = new SerialNumber();
        this.warningContentMessage = new String();
        this.dataCodingScheme = 0b01111010;
        this.repetitionPeriod = 30;
        this.numberOfBroadcasts = 0;
        this.trackingAreaVector = new Vector<TrackingArea>();

        this.shelter = new Shelter(datagramSocket,inetAddress);
    }

    //재난의 클래스를 정하는 메소드
    public int classify(){
        if(this.calamity.contains("지진")||this.calamity.contains("해일")||this.calamity.contains("지진과해일")){
            return 1;
        }else{
            return 2;
        }
    }

    public JSONObject WarningToJson(){
        JSONObject warningMessage = new JSONObject();

        warningMessage.put("messageType","Write_Replace_Warning_Request");
        warningMessage.put("messageidentifier",this.getMessageidentifier());
        warningMessage.put("serialNumber",this.getSerialNumber());
        warningMessage.put("warningContentMessage",this.getWarningContentMessage().trim());
        warningMessage.put("dataCodingScheme",this.getDataCodingScheme());
        warningMessage.put("repetitionPeriod",this.getRepetitionPeriod());
        warningMessage.put("numberOfBroadcasts",this.getNumberOfBroadcasts());

        /* plmn(mcc+mnc)
                *WCDMA	Korea Telecom	 45008
        WCDMA	SK Telecom	45005
        CDMA2000	LG U Plus	45006

        tac (bitstring16)

        */
        //kt통신사에서 0~14 eNodeB
        JSONArray TAIListItem = new JSONArray();
        for(int i = 0 ; i < 5 ; i++) {
            JSONObject TAI = new JSONObject();
            TAI.put("plmnIdentity", 45008);
            TAI.put("trackingAreacode", i);
            TAIListItem.add(TAI);
        }
        warningMessage.put("ListofTAIs",TAIListItem);
        String jsonInfo = warningMessage.toJSONString();
        System.out.println("send:"+jsonInfo);

        return warningMessage;
    }
    public void jsonToWarning(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject messageObj = (JSONObject) jsonParser.parse(message);

            this.messageType = (String) messageObj.get("messageType");
            this.source = (String) messageObj.get("source");
            this.calamity = (String) messageObj.get("calamity");
            this.impactArea = (String) messageObj.get("impactArea");
            this.waringAreaCoordinates = (String) messageObj.get("warningAreaCoordinate");
            this.context = (String) messageObj.get("context");

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void setMessageidentifier(int warningClass) {
        this.messageidentifier.setMessageIdentifier(warningClass);
    }

    public void setSerialNumber(int warningClass) {
        this.serialNumber.setSerialNumber(warningClass);
    }

    public void setWarningContentMessage(String warningContentMessage) {
        this.warningContentMessage = warningContentMessage;
    }

    public void setSender(DatagramSocket socket, byte buffer[], InetAddress sourceInetAddress, int sourcePort, int repetition, int number){
        this.sender = new ThreadSender(socket, buffer, sourceInetAddress, sourcePort, repetition, number);
    }

    public String getSource() {
        return this.source;
    }

    public String getCalamity() {
        return this.calamity;
    }

    public String getImpactArea() {
        return this.impactArea;
    }

    public String getWaringAreaCoordinates() {
        return this.waringAreaCoordinates;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContext() {
        return this.context;
    }

    public int getMessageidentifier() {
        return this.messageidentifier.getMessageIdentifier();
    }

    public int getSerialNumber() {
        return this.serialNumber.getSerialNumber();
    }

    public SerialNumber getSerialnumberO(){return this.serialNumber;}

    public String getWarningContentMessage() {
        return warningContentMessage;
    }


    public void update(){
        this.serialNumber.update();
    }

    public String getMessageType() {
        return this.messageType;
    }

    public int getDataCodingScheme() {
        return this.dataCodingScheme;
    }

    public int getRepetitionPeriod() {
        return this.repetitionPeriod;
    }

    public int getNumberOfBroadcasts() {
        return this.numberOfBroadcasts;
    }

    public ThreadSender getSender() {
        return this.sender;
    }

    public void send(){
        Thread thread = new Thread(this.sender);
        thread.setDaemon(true);
        thread.start();
    }

    public void confirm() throws IOException {
        this.sender.confirm();
    }

    public Vector<TrackingArea> getTrackingAreaVector() {
        return this.trackingAreaVector;
    }

    public Shelter getShelter() {
        return this.shelter;
    }
}
