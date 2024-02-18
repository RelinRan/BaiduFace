package com.baidu.idl.face.face;

public class SDKConfig {

    public SDKConfig(String project) {
        this.project = project;
    }

    public SDKConfig(String project,String licenseKey) {
        this.project = project;
        this.licenseKey = licenseKey;
    }

    public SDKConfig(String project,String licenseKey, String licenseFileName) {
        this.project = project;
        this.licenseKey = licenseKey;
        this.licenseFileName = licenseFileName;
    }

    /**
     * 授权key
     */
    public String licenseKey;
    /**
     * 授权文件名
     */
    public String licenseFileName;

    /**
     * 项目名称
     */
    public String project;

    /**
     * 输入图像的缩放系数
     */
    public float scaleRatio = -1.0F;
    /**
     * 需要检测的最大人脸数目
     */
    public int maxDetectNum = 2;
    /**
     * 需要检测的最小人脸大小
     */
    public int minFaceSize = 0;
    /**
     * 人脸置信度阈值（检测分值大于该阈值认为是人脸）RGB
     */
    public float notRGBFaceThreshold = 0.5F;
    /**
     * 人脸置信度阈值（检测分值大于该阈值认为是人脸）NIR
     */
    public float notNIRFaceThreshold = 0.5F;
    /**
     * 未跟踪到人脸前的检测时间间隔
     */
    public float detectInterval = 0;
    /**
     * 已跟踪到人脸后的检测时间间隔
     */
    public float trackInterval = 2.14748365E9F;
    /**
     * 质量检测模糊，默认不做质量检测
     */
    public boolean isCheckBlur = false;
    /**
     * 质量检测遮挡，默认不做质量检测
     */
    public boolean isOcclusion = false;
    /**
     * 质量检测光照，默认不做质量检测
     */
    public boolean isIllumination = false;
    /**
     * 姿态角检测，获取yaw(左右偏转角)，roll(人脸平行平面内的头部旋转角)，pitch(上下偏转角),默认不检测
     */
    public boolean isHeadPose = false;
    /**
     * 属性检查，获取年龄，种族，是否戴眼镜等信息，默认不检测
     */
    public boolean isAttribute = false;
    /**
     * 是否检测眼睛闭合，默认不检测
     */
    public boolean isEyeClose = false;
    /**
     * 是否检测嘴巴闭合，默认不检测
     */
    public boolean isMouthClose = false;
    /**
     * 是否开启最优人脸检测，默认不开启
     */
    public boolean isBestImage = false;


    /**
     * 活体接口
     */
    public boolean isFaceLive = false;
    /**
     * 特征接口
     */
    public boolean isFaceFeature = true;
    /**
     * 注意力
     */
    public boolean isFaceGaze = false;
    /**
     * 动作活体
     */
    public boolean isFaceActionLive = false;
    /**
     * 抠图能力
     */
    public boolean isFaceCrop = false;
    /**
     * 驾驶行为
     */
    public boolean isFaceDriverMonitor = false;
    /**
     * 口罩检测
     */
    public boolean isFaceMouthMask = true;
    /**
     * 图片光照检测
     */
    public boolean isImageIllum = false;
    /**
     * 安全帽检测
     */
    public boolean isFaceSafetyHat = false;
    /**
     * 暗光恢复检测
     */
    public boolean isFaceDarkEnhance = true;
    /**
     * 人脸比对检索检测
     */
    public boolean isFaceSearch = true;
}
