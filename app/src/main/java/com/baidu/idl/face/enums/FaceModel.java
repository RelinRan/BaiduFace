package com.baidu.idl.face.enums;

/**
 * 人脸模型类型
 */
public enum FaceModel {

    /**
     * 检测对齐模型
     */
    FACE_DETECT(false),
    /**
     * 质量检测模型
     */
    QUALITY(false),
    /**
     * 属性模型加载
     */
    ATTRIBUTE(false),
    /**
     * 眼睛闭合，嘴巴闭合模型加载
     */
    FACE_CLOSE(false),
    /**
     * 最优人脸模型加载
     */
    BEST_IMAGE(false),
    /**
     * 活体模型加载
     */
    FACE_LIVE(false),
    /**
     * 特征模型加载
     */
    FACE_FEATURE(false),
    /**
     * 注意力模型
     */
    GAZE_MODEL(false),
    /**
     * 动作活体模型加载
     */
    FACE_ACTION_LIVE(false),
    /**
     * 抠图能力
     */
    FACE_CROP(false),
    /**
     * 驾驶行为监测能力
     */
    FACE_DRIVER_MONITOR(false),
    /**
     * 口罩能力
     */
    FACE_MOUTH_MASK(false),
    /**
     * 暗光恢复检测
     */
    FACE_DARK_ENHANCE(false),
    /**
     * 安全帽
     */
    FACE_SAFETY_HAT(false);

    private boolean init;

    FaceModel(boolean init) {
        this.init = init;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }
}
