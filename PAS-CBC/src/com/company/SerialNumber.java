package com.company;

public class SerialNumber {
    //octet1    octet2
    //76543210 76543210
    //00000000 00000000
    int GS;
    int messageCode;
    int moreInfonum;    //쉘터정보에 사용

    int updateNumber;
    int serialNumber;

    void init(){
        this.GS = 0b0000000000000000;//00000000 00000000
        this.messageCode = 0b00000000000000;//0b000000 00000000
        this.updateNumber = 0b0000;//0000
    }



    void update(){
        this.updateNumber++;
        this.serialNumber++;
    }


    public void setGS(int warningClass) {
        if(warningClass == 0){      //위급재난 문자
            //same init
            this.GS = 0b1100000000000000;
        }else if(warningClass == 1){// 긴급재난 문자
            this.GS=0b1100000000000000;
        }else if(warningClass == 2){// 안전안내 문자
            this.GS=0b1100000000000000;
        }
    }

    public void setMessageCode(int warningClass) {

        if(warningClass == 0){      //위급재난 문자
            this.messageCode=0b11000000000000;
        }else if(warningClass == 1){// 긴급재난 문자
            this.messageCode=0b11000000000000;
        }else if(warningClass == 2){// 안전안내 문자
            //same init
            this.messageCode = 0b00000000000000;
        }
    }

    public void setUpdateNumber(int updateNumber) {
        this.updateNumber = updateNumber;
    }

    public void setSerialNumber(int warningClass) {
        this.setGS(warningClass);
        this.setMessageCode(warningClass);
        this.setUpdateNumber(0);
        this.serialNumber = this.GS + this.messageCode + this.updateNumber;
    }

    public int getGS() {
        return this.GS;
    }

    public int getMessageCode() {
        return this.messageCode;
    }

    public int getUpdateNumber() {
        return this.updateNumber;
    }
    public int getSerialNumber() {
        return this.serialNumber;
    }


}
