package com.baidu.idl.face.model;

import android.graphics.Bitmap;

import com.baidu.idl.face.utils.FaceImage;
import com.baidu.idl.main.facesdk.FaceInfo;

public class TraceInfo {

    private byte[] image;
    private FaceInfo faceInfo;
    private float liveScore;
    private byte[] feature;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }

    public float getLiveScore() {
        return liveScore;
    }

    public void setLiveScore(float liveScore) {
        this.liveScore = liveScore;
    }

    public byte[] getFeature() {
        return feature;
    }

    public void setFeature(byte[] feature) {
        this.feature = feature;
    }

    public Bitmap getBitmap() {
        return FaceImage.decodeByteArray(image);
    }

}
