package com.baidu.idl.face.model;

import android.graphics.RectF;

import com.baidu.idl.face.face.OnFaceBoundaryListener;
import com.baidu.idl.face.face.OnFaceSearchListener;
import com.baidu.idl.face.face.OnFaceTrackListener;
import com.baidu.idl.face.face.TraceSearch;

public class TraceSearchBody {

    private TraceSearch traceSearch;
    private TraceInfo traceInfo;
    private OnFaceTrackListener onFaceTrackListener;
    private SearchInfo searchInfo;
    private OnFaceSearchListener onFaceSearchListener;

    private RectF boundary;
    private OnFaceBoundaryListener onFaceBoundaryListener;

    public TraceSearch getTraceSearch() {
        return traceSearch;
    }

    public void setTraceSearch(TraceSearch traceSearch) {
        this.traceSearch = traceSearch;
    }

    public TraceInfo getTraceInfo() {
        return traceInfo;
    }

    public void setTraceInfo(TraceInfo traceInfo) {
        this.traceInfo = traceInfo;
    }

    public OnFaceTrackListener getOnFaceTrackListener() {
        return onFaceTrackListener;
    }

    public void setOnFaceTrackListener(OnFaceTrackListener onFaceTrackListener) {
        this.onFaceTrackListener = onFaceTrackListener;
    }

    public SearchInfo getSearchInfo() {
        return searchInfo;
    }

    public void setSearchInfo(SearchInfo searchInfo) {
        this.searchInfo = searchInfo;
    }

    public OnFaceSearchListener getOnFaceSearchListener() {
        return onFaceSearchListener;
    }

    public void setOnFaceSearchListener(OnFaceSearchListener onFaceSearchListener) {
        this.onFaceSearchListener = onFaceSearchListener;
    }

    public RectF getBoundary() {
        return boundary;
    }

    public void setBoundary(RectF boundary) {
        this.boundary = boundary;
    }

    public OnFaceBoundaryListener getOnFaceBoundaryListener() {
        return onFaceBoundaryListener;
    }

    public void setOnFaceBoundaryListener(OnFaceBoundaryListener onFaceBoundaryListener) {
        this.onFaceBoundaryListener = onFaceBoundaryListener;
    }
}
