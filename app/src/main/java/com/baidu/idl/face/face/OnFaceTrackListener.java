package com.baidu.idl.face.face;

import com.baidu.idl.face.model.TraceInfo;

public interface OnFaceTrackListener {

    /**
     * 人脸跟踪
     *
     * @param traceSearch 跟踪搜索
     * @param info        结果信息
     */
    void onFaceTraceResult(TraceSearch traceSearch, TraceInfo info);

}
