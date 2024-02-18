package com.baidu.idl.face.face;

import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.baidu.idl.face.model.SearchInfo;
import com.baidu.idl.face.model.TraceInfo;
import com.baidu.idl.face.model.TraceSearchBody;

public class TraceSearchHandler extends Handler {

    /**
     * 人脸边界
     *
     * @param traceSearch
     * @param boundary
     * @param boundaryListener
     */
    public void sendBoundary(TraceSearch traceSearch, RectF boundary, OnFaceBoundaryListener boundaryListener) {
        TraceSearchBody body = new TraceSearchBody();
        body.setTraceSearch(traceSearch);
        body.setBoundary(boundary);
        body.setOnFaceBoundaryListener(boundaryListener);
        Message message = obtainMessage();
        message.what = 1;
        message.obj = body;
        sendMessage(message);
    }

    /**
     * 人脸跟踪
     *
     * @param traceSearch
     * @param traceInfo
     * @param trackListener
     */
    public void sendTrace(TraceSearch traceSearch, TraceInfo traceInfo, OnFaceTrackListener trackListener) {
        TraceSearchBody body = new TraceSearchBody();
        body.setTraceSearch(traceSearch);
        body.setTraceInfo(traceInfo);
        body.setOnFaceTrackListener(trackListener);
        Message message = obtainMessage();
        message.what = 2;
        message.obj = body;
        sendMessage(message);
    }

    /**
     * 人脸搜索
     *
     * @param traceSearch
     * @param info
     * @param searchListener
     */
    public void sendSearch(TraceSearch traceSearch, SearchInfo info, OnFaceSearchListener searchListener) {
        TraceSearchBody body = new TraceSearchBody();
        body.setTraceSearch(traceSearch);
        body.setSearchInfo(info);
        body.setOnFaceSearchListener(searchListener);
        Message message = obtainMessage();
        message.what = 3;
        message.obj = body;
        sendMessage(message);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        TraceSearchBody body = (TraceSearchBody) msg.obj;
        switch (msg.what) {
            case 1:
                OnFaceBoundaryListener boundaryListener = body.getOnFaceBoundaryListener();
                if (boundaryListener != null) {
                    boundaryListener.onFaceBoundary(body.getTraceSearch(), body.getBoundary());
                }
            case 2:
                OnFaceTrackListener trackListener = body.getOnFaceTrackListener();
                if (trackListener != null) {
                    trackListener.onFaceTraceResult(body.getTraceSearch(), body.getTraceInfo());
                }
                break;
            case 3:
                OnFaceSearchListener searchListener = body.getOnFaceSearchListener();
                if (searchListener != null) {
                    searchListener.onFaceSearchResult(body.getTraceSearch(), body.getSearchInfo());
                }
                break;
        }
    }

}
