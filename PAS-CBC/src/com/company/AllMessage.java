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
        System.out.println(serialNumber+","+messageIdentifier);
        for(int i = 0 ; i < this.warningVector.size() ;  i++) {
            check = this.warningVector.get(i);
            if(check.getMessageidentifier() == messageIdentifier  ){
                //완전히 같은 경우
                /*
                if(check.getSerialNumber() == serialNumber){
                    System.out.println("check:"+check.getSerialNumber()+","+check.getMessageidentifier());
                }
                //MS코드가 다른경우 : 쉘터정보문자
                else if(){

                }
                return i;

                 */
                int mod =   compareSerialNumber(serialNumber, check.getSerialNumber());

                //해당재난문자에 대한 confirm이다.
                if(mod == 0){

                    return i;
                }
                //해당재난문자에 대한 쉘터정보 confirm이다.
                else if(mod > 0){

                    return i;
                }else {
                    System.out.println("이거 나오면 안된다.");
                }
            }

        }
        //해당하는 문자 없음
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


    //confirm serialnumber에 대한 동일여부 비교
    //(받은 메세지의 시리얼넘버.메세지 목록내의 시리얼넘버)
    public int compareSerialNumber(int serialNumber, int WarningSerialNumber){
        int GS              =   0b1100000000000000;
        int messageCode     =   0b0011111111110000;
        int Alert_POP       =   0b0011000000000000;
        int  moreInfonum    =   0b0000111111110000;
        int updateNumber    =   0b0000000000001111;

        int check1;
        int check2;

        check1 = serialNumber & GS;
        check2 = WarningSerialNumber & GS;

        if( check1 == check2 ){
            check1 = serialNumber & Alert_POP;
            check2 = serialNumber  & Alert_POP;
            if(check1 == check2) {
                check1 = serialNumber & updateNumber;
                check2 = serialNumber & updateNumber;
                //해당재난문자에 대한 confirm이다.
                if (check1 == check2) {
                    check1 = serialNumber & moreInfonum;
                    check2 = serialNumber & moreInfonum;

                    //해당 재난문자 confrim이다.
                    //00000000 == 00000000
                    if (check1 == check2){
                        return 0;
                    }
                    //쉘터정보 confirm이다.
                    else{
                        //1~
                        return check2 - check1;
                    }
                }
                //해당 재난 문자보다 최신 confrim 또는 쉘터정보이다.
                else if (check1 > check2) {
                    /*
                    check1 = serialNumber & moreInfonum;
                    check2 = serialNumber & moreInfonum;

                    //해당 재난문자 confrim이다.
                    //00000000 == 00000000
                    if (check1 == check2){
                        return 0;
                    }
                    //쉘터정보 confirm이다.
                    else{
                        //1~
                       return check2 - check1;
                    }
                    */

                }
            }
        }
        //아무것도 안속한는 경우
        return -1;
    }

}
