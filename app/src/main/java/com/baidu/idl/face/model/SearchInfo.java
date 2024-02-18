package com.baidu.idl.face.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

public class SearchInfo {

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

    /**
     * 获取bitmap
     *
     * @return
     */
    public Bitmap getBitmap() {
        if (TextUtils.isEmpty(avatar)) {
            return null;
        }
        byte[] data = Base64.decode(avatar, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(data,0,data.length);
    }


}
