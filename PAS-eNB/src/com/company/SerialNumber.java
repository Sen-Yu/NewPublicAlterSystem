package com.company;

public class SerialNumber {
    //octet1    octet2
    //76543210 76543210
    //00000000 00000000
    int GS;
    int messageCode;
    int updateNumber;
    int serialNumber;



    public int getGS() {
        return GS;
    }

    public int getMessageCode() {
        return messageCode;
    }

    public int getUpdateNumber() {
        return updateNumber;
    }
    public int getSerialNumber() {
        return serialNumber;
    }


}
