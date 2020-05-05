package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

public class Shelter {

    Vector<Address_GU> SI_GU;               //구 단위로 나눈 들
    Vector<ThreadSender> Shelter_Sender;    //쉘터정보를 패킷으로 나눈 쉘터패킷들

    DatagramSocket  datagramSocket;
    InetAddress inetAddress;


    Shelter(DatagramSocket datagramSocket, InetAddress inetAddress){
        this.datagramSocket = datagramSocket;
        this.inetAddress = inetAddress;

        this.SI_GU = new Vector<Address_GU>();
        //창원시만 제한했을 겨웅 생기는 창원시 쉘터 정보
        this.SI_GU.add(new Address_GU("창원시 성산구"));
        this.SI_GU.add(new Address_GU("창원시 의창구"));
        this.SI_GU.add(new Address_GU("창원시 진해구"));
        this.SI_GU.add(new Address_GU("창원시 마산합포구"));
        this.SI_GU.add(new Address_GU("창원시 마산회원구"));

        Shelter_Sender = new Vector<ThreadSender>();
    }
    //CBE에게서 넘어오는 정보들
    /*
    shelter.address = parseData.shelter_address0;
    shelter.name = parseData.shelter_name0;
    shelter.coordinate
    */

    //쉘터정보 텍스트 읽음
    public void ReadShelterText(){
        File file = new File("./ShelterList.txt");
        String text ;
        String address_new;
        String address_older;
        String name;
        String coordinate;
        try{
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int numberOfCount = 0;
            String line = "";
            String AllData = "";

        while((line = bufferedReader.readLine()) != null){
            text = "";

                address_new = line;
                address_older = bufferedReader.readLine();
                name = bufferedReader.readLine();
                coordinate = bufferedReader.readLine();
                bufferedReader.readLine();
                text = address_new+"," + address_older+"," +name+","+coordinate;
               // System.out.println("line:" + text);
                addShelter(address_older,name,coordinate);
            //distributeShelter(text);
        }
            bufferedReader.close();

        }catch (
            FileNotFoundException e) {
            System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }

    }
    //사용안함
    public void distributeShelter(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(message);

            JSONArray shelterList = (JSONArray) jsonObject.get("shelterList");
            //System.out.println("shelterList:"+ shelterList);
            for(int i = 0 ; i < shelterList.size() ; i++) {
                JSONObject info = (JSONObject) shelterList.get(i);
                String address_older = (String)info.get("address");
                Address_GU gu = Find_GU(address_older);
                //해당 범위지역이다.
                if(gu != null) {
                    //구를 기준으로 주소 분할
                    String splitAdd[] = address_older.split("구");

                    String shelterLastAddress = splitAdd[1];
                    String sheleterName = (String) info.get("name");
                    String coordinate = (String) info.get("coordinate");
                    //해당 구에 쉘터정보 추가
                    gu.add(new ShelterInfo(shelterLastAddress, sheleterName, coordinate));
                }
                //해당 범위지역이 아니다.
                else{

                }
            }


        }catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("<Distribute shelterList is end>");
    }

    public void addShelter(String address_older,String sheleterName,String coordinate){
        Address_GU gu = Find_GU(address_older);
        //해당 범위지역이다.
        if(gu != null) {
            //구를 기준으로 주소 분할
            String splitAdd[] = address_older.split("구");
            String shelterLastAddress = splitAdd[1];

            //해당 구에 쉘터정보 추가
            gu.add(new ShelterInfo(shelterLastAddress, sheleterName, coordinate));
            //System.out.println("sucess");
        }

    }

    //해당 구를 포함하고있으면 구를 리턴
    public Address_GU Find_GU(String address_older){
        for(int i = 0 ; i < this.SI_GU.size() ; i++) {
            Address_GU gu = this.SI_GU.get(i);
            //System.out.println("Find_GU:"+gu.getGu());
            //해당 시의 구 인 경우
            if(address_older.contains(gu.getGu())){
                return gu;
            }
        }
        return null;
    }

    //이전 재난문자정보의 내용을 토대로 편집해서 쉘터정보 생성 및 전송
    public void makePacket(JSONObject WarningObject){
        for(int i = 0 ; i < this.SI_GU.size() ; i++) {
             Vector<ShelterInfo> shelterInfos = this.SI_GU.get(i).getShelterInfos();

             for(int j = 0 ; j < shelterInfos.size() ; j++){
                 JSONObject json = new JSONObject();
                 json.put("shelterFirstAddress",this.SI_GU.get(i).getGu());

                 JSONArray shelterArray = new JSONArray();
                 JSONObject object = new JSONObject();

                 ShelterInfo shelterInfo = shelterInfos.get(j++);
                 object.put("shelterLastAddress",shelterInfo.getShelterLastAddress());
                 object.put("sheleterName",shelterInfo.getSheleterName());
                 object.put("coordinate",shelterInfo.getCoordinate());
                 shelterArray.add(object);
                 if(j < shelterInfos.size()) {
                     shelterInfo = shelterInfos.get(j++);
                     object.put("shelterLastAddress", shelterInfo.getShelterLastAddress());
                     object.put("sheleterName", shelterInfo.getSheleterName());
                     object.put("coordinate", shelterInfo.getCoordinate());
                     shelterArray.add(object);
                 } if(j < shelterInfos.size()) {
                     shelterInfo = shelterInfos.get(j);
                     object.put("shelterLastAddress", shelterInfo.getShelterLastAddress());
                     object.put("sheleterName", shelterInfo.getSheleterName());
                     object.put("coordinate", shelterInfo.getCoordinate());
                     shelterArray.add(object);
                 }
                 json.put("ShelterList",shelterArray);

                 JSONObject result = refactoring(WarningObject , json , j);
                 //System.out.println("Shelter_make_packet:"+result);
                //해당 패킷의 출발지 및 반복주기 결정
                ThreadSender sender = new ThreadSender(this.datagramSocket,result.toJSONString().getBytes(),
                         this.inetAddress, 5000,30,0);
                //쉘터전용 시리얼넘버 세팅
                sender.setSerialNumber((int)result.get("serialNumber"));
                //해당 패킷 목적지 설정(MME로 설정)
                sender.setBroadPacket(this.inetAddress,6000);
                //sender 저장소에 저장됨
                this.Shelter_Sender.add(sender);
                //전송
                 Thread thread = new Thread(sender);
                 thread.setDaemon(true);
                 thread.start();

             }
            System.out.println("AllShelterSend");
        }
    }

    //원본 재난 문자데이터 ,쉘터정보 , 추가할 시리얼넘버메시지코드번호
    public JSONObject refactoring(JSONObject WarningObject, JSONObject ShelterObject,int num){
        JSONObject result = new JSONObject();
        //두개의 json 병합시키기
        result.putAll(WarningObject);
        //result.putAll(ShelterObject);
        //contentmessage 바꾸기
        result.remove("warningContentMessage");
        result.remove("repetitionPeriod");
        result.remove("numberOfBroadcasts");
        /*시리얼넘버 바꾸기*/
        //시리얼넘버
        //업데이트번호:3210
        //메세지코드중 안쓰는부분:543210 7654
        //76543210 76543210
        //num은 1부터 시작
        int serialNumber = (int)result.get("serialNumber") + ((num+1)<<4);
        result.replace("serialNumber",serialNumber);
        result.put("warningContentMessage",ShelterObject);
        return result;
    }

    public void confirm(int SerialNumber) throws IOException {
        ThreadSender sender;
        for (int i = 0; i < this.Shelter_Sender.size()  ; i++){
            sender = this.Shelter_Sender.get(i);
            if(sender.getSerialNumber() == SerialNumber){
                sender.confirm();
            }
        }
    }
}
