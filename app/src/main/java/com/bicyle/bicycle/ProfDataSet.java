package com.bicyle.bicycle;

import java.io.Serializable;

public class ProfDataSet implements Serializable {
    private String uid = "";
    private String nickname = "";
    private String location = "";
    private String age = "";
    private String gender = "";
    private String deviceToken = "";

    public ProfDataSet() {}

    public ProfDataSet(String uid, String nickname, String location, String age, String gender, String DeviceToken) {
        this.uid = uid;
        this.nickname = nickname;
        this.location = location;
        this.age = age;
        this.gender = gender;
        this.deviceToken = DeviceToken;
    }

    public void setUid(String uid) { this.uid = uid; }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAge(String age) { this.age = age; }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDeviceToken(String DeviceToken) {
        this.deviceToken = DeviceToken;
    }

    public String getUid() { return uid; }

    public String getNickname() {
        return nickname;
    }

    public String getLocation() {
        return location;
    }

    public String getAge() { return age; }

    public String getGender() {
        return gender;
    }

    public String getDeviceToken() {
        return deviceToken;
    }
}
