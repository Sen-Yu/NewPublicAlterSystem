package com.company;

public class Messageidentifier {
    int messageIdentifier;

    public int getMessageIdentifier() {
        return messageIdentifier;
    }

    public void setMessageIdentifier(int warningClass) {
        if(warningClass == 0){      //위급재난 문자
            this.messageIdentifier=4370;
        }else if(warningClass == 1){// 긴급재난 문자
            this.messageIdentifier=4371;
        }else if(warningClass == 2){// 안전안내 문자
            this.messageIdentifier=4372;
        }
    }
}
