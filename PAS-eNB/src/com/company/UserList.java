package com.company;

import java.util.Vector;

public class UserList {
    Vector<User> userVector;



    public UserList(){
        this.userVector = new Vector<User>();
    }

    public Vector<User> getUserVector() {
        return userVector;
    }

    public void setUserVector(Vector<User> userVector) {
        this.userVector = userVector;
    }

    public void add(User user){
        this.userVector.add(user);
    }

    public void find(){
        Vector userVec = this.userVector;
        for(int i = 0 ; i < userVec.size() ; i++){
            userVec.get(i);
        }
    }

    public void Broadcast(){
        Vector userVec = this.userVector;
        for(int i = 0 ; i < userVec.size() ; i++){
            userVec.get(i);
        }
    }
}
