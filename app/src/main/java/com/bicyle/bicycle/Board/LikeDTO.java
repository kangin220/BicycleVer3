package com.bicyle.bicycle.Board;


public class LikeDTO {

    String uId;

    public LikeDTO(String uId) {
        this.uId = uId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}