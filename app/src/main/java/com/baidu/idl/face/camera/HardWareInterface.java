package com.baidu.idl.face.camera;

import android.content.Context;

/**
 * 硬件接口
 */

public interface HardWareInterface {
    /**
     * 初始化硬件
     *
     * @return 成功返回0：失败返回-1
     */
    int initHardWare(Context context);

    /**
     * 打开硬件
     *
     * @return
     */
    int openHardWare();

    /**
     * 返回数据
     */
    public abstract class ImgCallBack {
        public void onRgbArrive(byte[] imageData, int format, int width, int height, int angle, int isMbyteArrayror) {

        }
    }

    public abstract class NirImgCallBack {
        public void onNirArrive(byte[] imageData, int format, int width, int height, int angle, int isMbyteArrayror) {

        }
    }

    /**
     * 注册回调接口
     *
     * @param dataCallBack
     * @return -1：失败 0：成功
     */
    int registDataCallBack(ImgCallBack dataCallBack);

    int registDataCallBack(NirImgCallBack dataCallBack);

    /**
     * 启动硬件
     *
     * @return 成功返回0 失败返回1
     */
    int startPreview();

    /**
     * 停止预览
     *
     * @return 成功返回0，失败返回-1
     */
    int stopPreview();

    /**
     * 关闭硬件
     *
     * @return
     */
    int closeHardWare();

    /**
     * 释放硬件资源
     */
    void destroy();
}

