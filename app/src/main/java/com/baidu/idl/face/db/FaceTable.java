package com.baidu.idl.face.db;

/**
 * 人脸数据表
 */
public class FaceTable {

    private int faceID;
    private String feature;
    private String userId;
    private String userName;
    private String avatar;
    private long faceTime;

    public int getFaceID() {
        return faceID;
    }

    public void setFaceID(int faceID) {
        this.faceID = faceID;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getFaceTime() {
        return faceTime;
    }

    public void setFaceTime(long faceTime) {
        this.faceTime = faceTime;
    }
}
