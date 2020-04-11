package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Vector;

public class Shelter {

   /*{"추가정보":[
            {
                "메시지 타입":"",
                "메시지식별자":"",
                "시리얼넘버"
                "정보 타입": "대피소 or 대피요령 or 확진자 경로"

                "대피소":[{"대피소 이름":" ","위도":" ","경도":""},{"대피소 이름":" ","위도":" ","경도":""},........],
                "대피요령":" ",
                "확진자경로":{
                                "확진자번호":" ",
                                "경로":[{"시간":" ","장소":" "},{"시간":" ","장소":" "}, .......]
                            },
            }
        ]
      }
   */
   //해독전
    String infoType;
    String shelterList;
    Vector<ShelterInfo> shelterInfoVector;

    //해독 후
    String messageType;
    Messageidentifier messageidentifier;
    SerialNumber serialNumber;
    String warningContentMessage;
    int sequencePage;
    int dataCodingScheme;
    int repetitionPeriod;
    int numberOfBroadcasts;






    public JSONObject ShelterToJson(){
        JSONObject shelterMessage = new JSONObject();

        shelterMessage.put("messageType","Write_Replace_Warning_Request");
     //   shelterMessage.put("messageidentifier",this.getMessageidentifier());
    //    shelterMessage.put("serialNumber",this.getSerialNumber());
      //  shelterMessage.put("warningContentMessage",this.getWarningContentMessage().trim());
       // shelterMessage.put("dataCodingScheme",this.getDataCodingScheme());
       // shelterMessage.put("repetitionPeriod",this.getRepetitionPeriod());
       // shelterMessage.put("numberOfBroadcasts",this.getNumberOfBroadcasts());

        /* plmn(mcc+mnc)
                *WCDMA	Korea Telecom	 45008
        WCDMA	SK Telecom	45005
        CDMA2000	LG U Plus	45006

*
        tac (bitstring16)

        */
        //kt통신사에서 0~14 eNodeB
        JSONArray TAIListItem = new JSONArray();
        for(int i = 0 ; i < 15 ; i++) {
            JSONObject TAI = new JSONObject();
            TAI.put("plmnIdentity", 45008);
            TAI.put("trackingAreacode", i);
            TAIListItem.add(TAI);
        }
        shelterMessage.put("ListofTAIs",TAIListItem);
        String jsonInfo = shelterMessage.toJSONString();
        System.out.println("send:"+jsonInfo);

        return shelterMessage;
    }
    public void jsonToShelter(String message){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject messageObj = (JSONObject) jsonParser.parse(message);

            this.messageType = (String) messageObj.get("messageType");
            this.infoType = (String) messageObj.get("infoType");


            JSONArray shelterList = (JSONArray) messageObj.get("shelterList");

            this.shelterInfoVector = new Vector<ShelterInfo>();
            //for(int i = 0 ; i<  ; i++){
            //    JSONObject shelterInfo = (JSONObject) messageObj.get("shelterList") ;

           // }



        } catch (ParseException e) {
            e.printStackTrace();
        }

    }






}
