package com.baidu.idl.face.face;

import android.graphics.RectF;

public interface OnFaceBoundaryListener {

    /**
     * 人脸边界
     *
     * @param traceSearch 跟踪搜索
     * @param boundary    边界
     */
    void onFaceBoundary(TraceSearch traceSearch, RectF boundary);

}
