package com.company;

import java.util.Vector;

public class AllMessage {

    private Vector<Warning> warningVector;
    private Vector<Shelter> shelterVector;

    public AllMessage(){
        this.warningVector = new Vector<Warning>();
        this.shelterVector = new Vector<Shelter>();
    }



    public Vector<Warning> getWarningVector() {
        return this.warningVector;
    }
    //Warning 추가
    public void addWarning(Warning warning){
        this.warningVector.add(warning);
    }
    //시리얼넘버와 메시지식별자같은 Warning 검색
    //Confirm받았을때 재전송 멈추기위해서 또는 새로 업데이트번호를 업데이트하기위해서
    public int findSameWarning(int serialNumber, int messageIdentifier){
        Warning check;

        for(int i = 0 ; i < this.warningVector.size() ;  i++) {
            check = this.warningVector.get(i);
            if(check.getSerialNumber() == serialNumber && check.getMessageidentifier() == messageIdentifier ){

                return i;
            }

        }
        return -1;
    }

    public void deleteWarning(int num){
        this.warningVector.remove(num);
    }

    public Warning getWarning(int num){
        return this.warningVector.get(num);
    }

    public Vector<Shelter> getShelterVector() {
        return this.shelterVector;
    }
}
