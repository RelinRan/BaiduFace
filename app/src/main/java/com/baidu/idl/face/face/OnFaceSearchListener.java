package com.baidu.idl.face.face;

import com.baidu.idl.face.model.SearchInfo;

public interface OnFaceSearchListener {

    /**
     * 人脸搜索结果
     *
     * @param traceSearch 跟踪搜索
     * @param info     特征值
     */
    void onFaceSearchResult(TraceSearch traceSearch, SearchInfo info);

}
