package com.company;

import org.json.simple.JSONObject;

import java.util.Vector;

import static com.company.Server.safeLongToInt;

public class CBSmessageList {

    Vector<CBSmessage> cbSmessageVector;

    public CBSmessageList(){
        this.cbSmessageVector = new Vector<CBSmessage>();
    }


    public CBSmessage findmessage(JSONObject object){

        int serialNumber = safeLongToInt((long)object.get("serialNumber"));
        int messageidentifier = safeLongToInt((long)object.get("messageidentifier"));

        Vector<CBSmessage> vector = this.cbSmessageVector;
        CBSmessage message;
        for(int i = 0 ; i < vector.size() ; i++){
            //
            message = vector.get(i);
            if(message.getSerialNumber() == serialNumber && message.getMessageidentifier() == messageidentifier){
                //해당 추출 후 삭제
                vector.remove(i);
                return message;
            }
        }
        //잘못된 경우임
        return null;
    }

    public Vector<CBSmessage> getCbSmessageVector() {
        return this.cbSmessageVector;
    }

    public void addCBSmessage(CBSmessage msg){
        this.cbSmessageVector.add(msg);
    }
}
