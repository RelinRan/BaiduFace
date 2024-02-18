package com.baidu.idl.face.face;

/**
 * 常量配置
 */
public class SDKConstants {

    /**
     * 人脸识别对比阈值
     */
    public static final float THRESHOLD = 0.60f;
    /**
     * 抠图缩放大小
     */
    public static final float ENLARGE_RATIO = 2f;


    //可见光图片检测模型
    public static final String  DETECT_VIS_MODEL = "face-sdk-models/detect/detect_rgb-customized-pa-192.model.float32-0.0.18.1";
    //对齐模型
    public static final String  ALIGN_TRACK_MODEL = "face-sdk-models/align/align_rgb-customized-pa-fast.model.float32-0.7.5.5";
    //模糊检测模型
    public static final String BLUR_MODEL = "face-sdk-models/blur/blur-customized-pa-addcloud_quant_e19.model.float32-3.0.13.3";
    //遮挡检测模型
    public static final String OCCLUSION_MODEL = "face-sdk-models/occlusion/occlusion-customized-pa-paddle.model.float32-2.0.7.3";
    //属性模型
    public static final String ATTRIBUTE_MODEL = "face-sdk-models/attribute/attribute-customized-pa-mobile.model.float32-1.0.9.5";
    //眼睛闭合模型
    public static final String EYE_CLOSE_MODEL = "face-sdk-models/eyes_close/eyes-customized-pa-DMS_eye_rgb_nir_detect.model.float32-1.0.4.2";
    //嘴巴闭合模型
    public static final String MOUTH_CLOSE_MODEL = "face-sdk-models/mouth_close/mouth-customized-pa-DMS_mouth_rgb_nir_detect.model.float32-1.0.4.2";
    //最优人脸模型
    public static final String BEST_MODEL = "face-sdk-models/best_image/best_image-mobilenet-pa-dcqe449_live_e51_relu_128.model.float32-1.0.3.1";
    //可见光图片活体模型
    public static final String LIVE_VIS_MODEL = "face-sdk-models/silent_live/liveness_rgb-customized-pa-DCQsdk80.model.float32-1.1.82.1";
    //2d_mask多因子活体模型
    public static final String LIVE_VIS_2D_MASK_MODEL = "face-sdk-models/silent_live/liveness_rgb-customized-pa-model_freeze_2dmask_20211210_sdk_224_epoch7.model.float32-1.1.80.1";
    //屏幕多因子活体模型
    public static final String LIVE_VIS_HAND_MODEL = "face-sdk-models/silent_live/liveness_rgb-customized-pa-hand_sdk_224.model.float32-1.1.69.1";
    //手部多因子活体模型
    public static final String LIVE_VIS_REFLECTION_MODEL = "face-sdk-models/silent_live/liveness_rgb-customized-pa-reflection.model.float32-1.1.81.1";
    //红外图片活体模型
    public static final String LIVE_NIR_MODEL = "face-sdk-models/silent_live/liveness_nir-customized-pa-DCQ_80.model.float32-1.1.78.1";
    //深度图片活体模型
    public static final String LIVE_DEPTH_MODEL = "face-sdk-models/silent_live/liveness_depth-customized-pa-paddle_60.model.float32-1.1.13.2";
    //证件照图片模型
    public static final String RECOGNIZE_ID_PHOTO_MODEL = "face-sdk-models/feature/feature_live-mnasnet-pa-attention_v4.model.int8-2.0.239.1";
    //可见光图片模型
    public static final String RECOGNIZE_VIS_MODEL = "face-sdk-models/feature/feature_live-mnasnet-pa-attention_v4.model.int8-2.0.239.1";
    //红外图片模型（非必要参数，可以为空）
    public static final String RECOGNIZE_NIR_MODEL = "face-sdk-models/feature/feature_nir-mnasnet-pa-foreign.model.int8-2.0.189.1";
    //RGBD图片模型
    public static final String RECOGNIZE_RGBD_MODEL = "face-sdk-models/feature/feature_live-mnasnet-pa-RGBD_FaceID_5.model.int8-2.0.88.3";
    //注意力模型
    public static final String GAZE_MODEL = "face-sdk-models/gaze/gaze-customized-pa-mobile.model.float32-1.0.3.4";
    //驾驶行为监测能力
    public static final String DRIVE_MONITOR_MODEL = "face-sdk-models/driver_monitor/driver_monitor_nir-customized-pa-DMS_rgb_nir_detect.model.float32-1.0.1.2";
    //口罩
    public static final String MOUTH_MASK = "face-sdk-models/mouth_mask/mouth_mask-customized-pa-faceocc_3classes.model.float32-1.0.9.2";
    //暗光恢复检测
    public static final String DARK_ENHANCE_MODEL = "face-sdk-models/dark_enhance/dark_enhance-customized-pa-zero_depthwise.model.float32-1.0.2.2";
    //安全帽
    public static final String SAFETY_HAT = "face-sdk-models/safetyhat/attribute-customized-pa-anquanmao2023_v1.model.float32-1.0.73.1";

}
