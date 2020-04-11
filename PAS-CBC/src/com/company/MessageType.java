package com.company;

public class MessageType {
    //1.Emergency-Broadcast-Request-36
    //2.Write-Replace-Warning-Request-36
    //3
    //4
    //8
    //10
    //11
    //12
    //13
    int messageType;//messageType = procedureCode or typOfMessage
    int procedureCode;  //WriteReplaceWarning:==36
                        //kill:==43
    int typOfMessage;

    public void init(){
        this.messageType = procedureCode;
    }

    public void whatIsProcedure(String messageType){
        if(messageType.equals("Emergency-Broadcast-Request")) {
            this.procedureCode = 36;
        }
        else if(messageType.equals("Emergency-Broadcast-kill")){
            this.procedureCode = 43;
        }
        else{
            this.procedureCode =0;
        }

    }
}

