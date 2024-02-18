package com.baidu.idl.face.face;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.idl.face.db.FaceDatabase;
import com.baidu.idl.face.db.FaceTable;
import com.baidu.idl.face.enums.FaceModel;
import com.baidu.idl.face.sqlite.SQLite;
import com.baidu.idl.face.utils.Hex;
import com.baidu.idl.main.facesdk.FaceActionLive;
import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.FaceCrop;
import com.baidu.idl.main.facesdk.FaceDarkEnhance;
import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceDriverMonitor;
import com.baidu.idl.main.facesdk.FaceFeature;
import com.baidu.idl.main.facesdk.FaceGaze;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.FaceLive;
import com.baidu.idl.main.facesdk.FaceMouthMask;
import com.baidu.idl.main.facesdk.FaceSafetyHat;
import com.baidu.idl.main.facesdk.FaceSearch;
import com.baidu.idl.main.facesdk.ImageIllum;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceCropParam;
import com.baidu.idl.main.facesdk.model.BDFaceDetectListConf;
import com.baidu.idl.main.facesdk.model.BDFaceDriverMonitorInfo;
import com.baidu.idl.main.facesdk.model.BDFaceGazeInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceIsOutBoundary;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;
import com.baidu.idl.main.facesdk.model.Feature;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SDK API
 */
public class FaceSDK implements Callback {

    private String TAG = FaceSDK.class.getSimpleName();
    private Context context;
    private FaceAuth faceAuth;
    private SDKConfig sdkConfig;
    public static FaceSDK instance;
    //检测接口
    private FaceDetect faceDetect;
    //活体接口
    private FaceLive faceLive;
    //特征接口
    private FaceFeature faceFeature;
    //注意力检测
    private FaceGaze faceGaze;
    //动作活体
    private FaceActionLive faceActionLive;
    //抠图能力
    private FaceCrop faceCrop;
    //驾驶行为
    private FaceDriverMonitor faceDriverMonitor;
    //口罩检测
    private FaceMouthMask faceMouthMask;
    //图片光照检测
    private ImageIllum imageIllum;
    //安全帽检测
    private FaceSafetyHat faceSafetyHat;
    //暗光恢复检测
    private FaceDarkEnhance faceDarkEnhance;
    //人脸比对检索检测
    private FaceSearch faceSearch;

    private ExecutorService initService;
    private ExecutorService loadService;
    private List<FaceModel> modelList;
    private OnFaceSDKListener onFaceSDKListener;
    private FaceDatabase database;

    public Context getContext() {
        return context;
    }

    /**
     * 初始化
     *
     * @param context  上下文
     * @param config   配置
     * @param listener 监听
     * @return
     */
    public static FaceSDK initialize(Context context, SDKConfig config, OnFaceSDKListener listener) {
        if (instance == null) {
            synchronized (FaceSDK.class) {
                if (instance == null) {
                    instance = new FaceSDK(context, config, listener);
                }
            }
        }
        return instance;
    }

    public static FaceSDK getInstance() {
        return instance;
    }

    /**
     * 获取数据库文件
     *
     * @return
     */
    public File getDatabaseFile() {
        File project = new File(Environment.getExternalStorageDirectory(), sdkConfig.project);
        if (!project.exists()) {
            project.mkdirs();
        }
        File dir = new File(project, "Face");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "face.db");
    }

    /**
     * 构造函数
     *
     * @param context  上下文
     * @param config   配置
     * @param listener 监听
     */
    private FaceSDK(Context context, SDKConfig config, OnFaceSDKListener listener) {
        if (context == null) {
            return;
        }
        this.context = context;
        sdkConfig = config;
        onFaceSDKListener = listener;
        modelList = new ArrayList<>();
        initService = Executors.newFixedThreadPool(1);
        loadService = Executors.newFixedThreadPool(1);
        //初始化人脸数据表
        SQLite.initialize(context, getDatabaseFile().getAbsolutePath(), 20231228).create(FaceTable.class);
        database = new FaceDatabase();
        faceAuth = new FaceAuth();
        //根据开发板类型，设置加速对cpu 核数依赖，调整参数，提高性能 adb shell cat /proc/cpuinfo 查看可用CPU核数
        faceAuth.setCoreConfigure(BDFaceSDKCommon.BDFaceCoreRunMode.BDFACE_LITE_POWER_NO_BIND, 1);
        String licenseKey = config.licenseKey;
        String licenseFileName = config.licenseFileName;
        //在线激活
        if (!TextUtils.isEmpty(licenseKey) && TextUtils.isEmpty(licenseFileName)) {
            faceAuth.initLicenseOnLine(context, licenseKey, this);
        }
        //离线激活 - 拷贝授权文件到内存中自动读取
        if (TextUtils.isEmpty(licenseKey) && TextUtils.isEmpty(licenseFileName)) {
            faceAuth.initLicenseOffLine(context, this);
        }
        //离线激活 - 文件激活
        if (!TextUtils.isEmpty(licenseKey) && !TextUtils.isEmpty(licenseFileName)) {
            faceAuth.initLicense(context, licenseKey, licenseFileName, false, this);
        }
    }

    /**
     * SDK配置
     *
     * @return
     */
    public SDKConfig getSDKConfig() {
        return sdkConfig;
    }

    /**
     * 获取硬件指纹
     *
     * @return
     */
    public String getHardwareFingerprints() {
        return faceAuth.getDeviceId(context);
    }

    /**
     * 激活返回
     *
     * @param code     成功；code 1 加载失败
     * @param response 结果信息
     */
    @Override
    public void onResponse(int code, String response) {
        Log.i(TAG, "initialize code:" + code + ",response:" + response);
        if (code == 0) {
            initService.submit(() -> {
                if (sdkConfig == null) {
                    sdkConfig = new SDKConfig("", "");
                }
                initModel(sdkConfig);
            });
        }
    }


    /**
     * 加载人脸数据库数据
     */
    public void loadFaceDatabase() {
        List<FaceTable> tables = database.queryAll();
        List<Feature> featureList = new ArrayList<>();
        for (int i = 0; i < tables.size(); i++) {
            FaceTable table = tables.get(i);
            Feature feature = new Feature();
            feature.setId(table.getFaceID());
            feature.setUserId(table.getUserId());
            feature.setUserName(table.getUserName());
            feature.setFeature(Hex.toBytes(table.getFeature()));
            feature.setCtime(table.getFaceTime());
            featureList.add(feature);
        }
        int code = pushPersonFeatureList(featureList);
        Log.i(TAG, "load face database size:" + featureList.size() + ",pushPersonFeatureListCode:" + code);
    }

    /**
     * 初始化模型
     */
    private void initModel(SDKConfig faceConfig) {
        //==========================[检测接口]=================================
        faceDetect = new FaceDetect();
        BDFaceSDKConfig config = new BDFaceSDKConfig();
        config.scaleRatio = faceConfig.scaleRatio;
        config.maxDetectNum = faceConfig.maxDetectNum;
        config.minFaceSize = faceConfig.minFaceSize;
        config.notRGBFaceThreshold = faceConfig.notRGBFaceThreshold;
        config.notNIRFaceThreshold = faceConfig.notNIRFaceThreshold;
        config.detectInterval = faceConfig.detectInterval;
        config.trackInterval = faceConfig.trackInterval;
        config.isCheckBlur = faceConfig.isCheckBlur;
        config.isOcclusion = faceConfig.isOcclusion;
        config.isIllumination = faceConfig.isIllumination;
        config.isHeadPose = faceConfig.isHeadPose;
        config.isAttribute = faceConfig.isAttribute;
        config.isEyeClose = faceConfig.isEyeClose;
        config.isMouthClose = faceConfig.isMouthClose;
        config.isBestImage = faceConfig.isBestImage;
        faceDetect.loadConfig(config);
        //==========================[配置信息加载]=================================
        //检测对齐模型
        faceDetect.initModel(context, SDKConstants.DETECT_VIS_MODEL, SDKConstants.ALIGN_TRACK_MODEL, BDFaceSDKCommon.DetectType.DETECT_VIS, BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_FAST, new ModelCallback(FaceModel.FACE_DETECT));
        //质量检测模型
        if (config.isCheckBlur) {
            faceDetect.initQuality(context, SDKConstants.BLUR_MODEL, SDKConstants.OCCLUSION_MODEL, new ModelCallback(FaceModel.QUALITY));
        }
        //属性模型加载
        if (config.isAttribute) {
            faceDetect.initAttrbute(context, SDKConstants.ATTRIBUTE_MODEL, new ModelCallback(FaceModel.ATTRIBUTE));
        }
        //眼睛闭合，嘴巴闭合模型加载
        if (config.isEyeClose && config.isMouthClose) {
            faceDetect.initFaceClose(context, SDKConstants.EYE_CLOSE_MODEL, SDKConstants.MOUTH_CLOSE_MODEL, new ModelCallback(FaceModel.FACE_CLOSE));
        }
        //最优人脸模型加载
        if (config.isBestImage) {
            faceDetect.initBestImage(context, SDKConstants.BEST_MODEL, new ModelCallback(FaceModel.BEST_IMAGE));
        }
        //==========================[暗光恢复检测]=================================
        if (faceConfig.isFaceDarkEnhance) {
            faceDarkEnhance = new FaceDarkEnhance();
            faceDarkEnhance.initFaceDarkEnhance(context, SDKConstants.DARK_ENHANCE_MODEL, new ModelCallback(FaceModel.FACE_DARK_ENHANCE));
        }
        //==========================[人脸比对检索检测]=================================
        if (faceConfig.isFaceSearch) {
            faceSearch = new FaceSearch();
            faceSearch.setNeedJoinDB(false);//是否要入库
            faceSearch.setRegisterCompareThreshold(0.8F);//注册照比对阈值 取值范围：0-1，默认：0.8
            faceSearch.setUpdateCompareThreshold(0.9F);//更新照比对阈值 取值范围：0-1， 默认：0.9
            faceSearch.setInputDBThreshold(0.92F);//入库阈值 取值范围：0-1 默认：0.92
            faceSearch.setMaxUpdateSize(10);// 最大更新数量 默认：10
            faceSearch.setInputDBIntervalTime(24);//入库间隔时长 默认：24小时
            //加载人脸
            loadFaceDatabase();
        }
        //==========================[活体接口]=================================
        if (faceConfig.isFaceLive) {
            faceLive = new FaceLive();
            //活体模型加载,静默活体检测模型初始化，可见光活体模型，深度活体，近红外活体模型初始化
            faceLive.initModel(context, SDKConstants.LIVE_VIS_MODEL, SDKConstants.LIVE_VIS_2D_MASK_MODEL, SDKConstants.LIVE_VIS_HAND_MODEL, SDKConstants.LIVE_VIS_REFLECTION_MODEL, SDKConstants.LIVE_NIR_MODEL, SDKConstants.LIVE_DEPTH_MODEL, new ModelCallback(FaceModel.FACE_LIVE));
        }
        //==========================[特征接口]=================================
        if (faceConfig.isFaceFeature) {
            faceFeature = new FaceFeature();
            //离线特征获取模型加载，目前支持可见光模型，近红外检测模型（非必要参数，可以为空），证件照模型；用户根据自己场景，选择相应场景模型
            faceFeature.initModel(context, SDKConstants.RECOGNIZE_ID_PHOTO_MODEL, SDKConstants.RECOGNIZE_VIS_MODEL, SDKConstants.RECOGNIZE_NIR_MODEL, SDKConstants.RECOGNIZE_RGBD_MODEL, new ModelCallback(FaceModel.FACE_FEATURE));
        }
        //==========================[抠图能力]=================================
        if (faceConfig.isFaceCrop) {
            faceCrop = new FaceCrop();
            faceCrop.initFaceCrop(new ModelCallback(FaceModel.FACE_CROP));
        }
        //==========================[口罩检测]=================================
        if (faceConfig.isFaceMouthMask) {
            faceMouthMask = new FaceMouthMask();
            faceMouthMask.initModel(context, SDKConstants.MOUTH_MASK, new ModelCallback(FaceModel.FACE_MOUTH_MASK));
        }
        //==========================[注意力]=================================
        if (faceConfig.isFaceGaze) {
            faceGaze = new FaceGaze();
            //注意力模型加载,眼睛状态检测，同时判断出左眼，右眼6种状态，分别为向上，向下，向左，向右，向前，闭合
            faceGaze.initModel(context, SDKConstants.GAZE_MODEL, new ModelCallback(FaceModel.GAZE_MODEL));
        }
        //==========================[动作活体]=================================
        if (faceConfig.isFaceActionLive) {
            faceActionLive = new FaceActionLive();
            faceActionLive.initActionLiveModel(context, SDKConstants.EYE_CLOSE_MODEL, SDKConstants.MOUTH_CLOSE_MODEL, new ModelCallback(FaceModel.FACE_ACTION_LIVE));
        }
        //==========================[驾驶行为]=================================
        if (faceConfig.isFaceDriverMonitor) {
            faceDriverMonitor = new FaceDriverMonitor();
            faceDriverMonitor.initDriverMonitor(context, SDKConstants.DRIVE_MONITOR_MODEL, new ModelCallback(FaceModel.FACE_DRIVER_MONITOR));
        }
        //==========================[图片光照检测]=================================
        if (faceConfig.isImageIllum) {
            imageIllum = new ImageIllum();
        }
        //==========================[安全帽检测]=================================
        if (faceConfig.isFaceSafetyHat) {
            faceSafetyHat = new FaceSafetyHat();
            faceSafetyHat.initModel(context, SDKConstants.SAFETY_HAT, new ModelCallback(FaceModel.FACE_SAFETY_HAT));
        }
        //==========================[初始化监听]=================================
        if (onFaceSDKListener != null) {
            boolean succeed = true;
            for (int i = 0; i < modelList.size(); i++) {
                if (modelList.get(i).isInit() == false) {
                    succeed = false;
                }
            }
            onFaceSDKListener.onFaceSDKInitialized(this, succeed);
        }
    }


    /**
     * 模型卸载
     */
    public void destroy() {
        //说明：0表示成功，非0失败
        if (faceDetect != null) {
            faceDetect.uninitModel();
        }
        if (faceLive != null) {
            faceLive.uninitModel();
        }
        if (faceFeature != null) {
            faceFeature.uninitModel();
        }
        if (faceGaze != null) {
            faceGaze.uninitGazeModel();
        }
        if (faceActionLive != null) {
            faceActionLive.uninitActionLiveModel();
        }
        if (faceCrop != null) {
            faceCrop.uninitFaceCrop();
        }
        if (faceDriverMonitor != null) {
            faceDriverMonitor.uninitDriverMonitor();
        }
        if (faceMouthMask != null) {
            faceMouthMask.uninitModel();
        }
        if (faceDarkEnhance != null) {
            faceDarkEnhance.uninitFaceDarkEnhance();
        }
        if (faceSafetyHat != null) {
            faceSafetyHat.uninitModel();
        }
    }

    private class ModelCallback implements Callback {

        private FaceModel model;

        public ModelCallback(FaceModel model) {
            this.model = model;
        }

        @Override
        public void onResponse(int code, String response) {
            Log.i(TAG, model.name() + ",code:" + code + ",msg:" + response);
            model.setInit(code == 0);
            modelList.add(model);
        }
    }

    /**
     * 人脸框检测，每一帧图片都会检测，返回基本人脸信息和72 关键点，可以绘制人脸框，描绘眼耳鼻嘴关键点，也可作用于后续活体，特征抽取入参。
     *
     * @param detectType    检测类型
     * @param imageInstance 图片数据信息
     * @return 返回参数(人脸参数信息)
     */
    public FaceInfo[] detect(BDFaceSDKCommon.DetectType detectType, BDFaceImageInstance imageInstance) {
        return faceDetect.detect(detectType, imageInstance);
    }

    /**
     * 根据传入的人脸框信息进行检测
     * 可灵活配置的人脸检测接口，使用输入的faceInfos通过配置bdFaceDetectListConfig可以控制是否进行一下能力的
     * 预测：人脸检测，关键点提取，头部姿态角，光照，模糊，属性，情绪，闭眼，闭嘴。输出返回值为预测后的faceInfos，
     * 可作用于后续活体，特征抽取入参。
     *
     * @param detectType             检测类型
     * @param alignType              对齐类型
     * @param imageInstance          图片数据信息
     * @param faceInfos              人脸框数据(可以通过人脸追踪能力获取人脸框)
     * @param bdFaceDetectListConfig 功能开关
     * @return 返回参数(人脸参数信息)
     */
    public FaceInfo[] detect(BDFaceSDKCommon.DetectType detectType, BDFaceSDKCommon.AlignType alignType, BDFaceImageInstance imageInstance, FaceInfo[] faceInfos, BDFaceDetectListConf bdFaceDetectListConfig) {
        return faceDetect.detect(detectType, alignType, imageInstance, faceInfos, bdFaceDetectListConfig);
    }

    /**
     * 人脸跟踪-多人脸检测 (接口只支持RGB跟踪)
     * 视频人脸跟踪检测，追踪图片中多个人脸信息，通过参数num 配置，接口包含检测和跟踪功能，返回基本人脸信息
     * 和72 关键点，可以绘制人脸框，描绘眼耳鼻嘴关键点，也可作用于后续活体，特征抽取入参。 该接口只支持RGB检测和对齐；
     *
     * @param detectType    检测类型 （DETECT_VIS ）
     * @param imageInstance 图片数据信息
     * @return 返回参数(人脸参数信息)
     */
    public FaceInfo[] track(BDFaceSDKCommon.DetectType detectType, BDFaceImageInstance imageInstance) {
        return faceDetect.track(detectType, imageInstance);
    }

    /**
     * 人脸跟踪-多人脸检测
     * 视频人脸跟踪检测，追踪图片中多个人脸信息，通过参数num 配置，接口包含检测和跟踪功能，返回基本人脸信息
     * 和72 关键点，可以绘制人脸框，描绘眼耳鼻嘴关键点，也可作用于后续活体，特征抽取入参。
     *
     * @param detectType    检测类型
     * @param alignType     对齐类型
     * @param imageInstance 图片数据信息
     * @return 返回参数(人脸参数信息)
     */
    public FaceInfo[] track(BDFaceSDKCommon.DetectType detectType, BDFaceSDKCommon.AlignType alignType, BDFaceImageInstance imageInstance) {
        return faceDetect.track(detectType, alignType, imageInstance);
    }

    /**
     * 人脸静默活体检测
     *
     * @param type                LIVEID_VIS 可见光图像静默活体检测
     *                            LIVEID_NIR 红外图像静默活体检测
     * @param bdFaceImageInstance 图像对象
     * @param landmarks           检查后landmark
     * @return
     */
    public float silentLive(BDFaceSDKCommon.LiveType type, BDFaceImageInstance bdFaceImageInstance, float[] landmarks) {
        return faceLive.silentLive(type, bdFaceImageInstance, landmarks);
    }

    /**
     * 人脸静默活体检测
     *
     * @param type                LIVEID_VIS 可见光图像静默活体检测
     *                            LIVEID_NIR 红外图像静默活体检测
     * @param bdFaceImageInstance 图像对象
     * @param landmarks           检查后landmark
     * @param liveThreshold       活体阈值，通过该阈值控制多因子阈值，0 ~ 0.6：0.05；0.61 ~ 0.0.8：0.1； 0.81 ~ 1.0：0.5
     * @return
     */
    public float silentLive(BDFaceSDKCommon.LiveType type, BDFaceImageInstance bdFaceImageInstance, float[] landmarks, float liveThreshold) {
        return faceLive.silentLive(type, bdFaceImageInstance, landmarks, liveThreshold);
    }

    /**
     * 人脸静默多帧活体检测
     * 静默活体检测，是否为活体， true： 活体， false： 非活体
     *
     * @param type                LIVEID_VIS 可见光图像静默活体检测
     *                            LIVEID_NIR 红外图像静默活体检测
     * @param bdFaceImageInstance 图像对象
     * @param faceInfo            人脸信息
     * @param strategyCount       多帧次数
     * @param liveThreshold       活体阈值
     * @return
     */
    public boolean strategySilentLive(BDFaceSDKCommon.LiveType type, BDFaceImageInstance bdFaceImageInstance, FaceInfo faceInfo, int strategyCount, float liveThreshold) {
        return faceLive.strategySilentLive(type, bdFaceImageInstance, faceInfo, strategyCount, liveThreshold);
    }


    /**
     * 人脸特征提取 - 无RGBD特征提取
     * 离线特征提取接口，通过featureType 提取不同图片特征数据，函数返回特征个数，特征存储在feature 参数中
     *
     * @param featureType   BDFACE_FEATURE_TYPE_LIVE_PHOTO 生活照
     *                      BDFACE_FEATURE_TYPE_ID_PHOTO 证件照
     *                      BDFACE_FEATURE_TYPE_NIR 红外
     * @param imageInstance 图像信息
     * @param landmarks     检测后产出的数据
     * @param feature       出参：人脸特征 feature 数组，默认初始化512空字节
     * @return 返回128个特征数据
     */
    public float feature(BDFaceSDKCommon.FeatureType featureType, BDFaceImageInstance imageInstance, float[] landmarks, byte[] feature) {
        return faceFeature.feature(featureType, imageInstance, landmarks, feature);
    }

    /**
     * 人脸特征提取 -  有RGBD特征提取
     * 离线特征提取接口，通过featureType 提取不同图片特征数据，函数返回特征个数，特征存储在feature 参数中
     *
     * @param featureType         BDFACE_FEATURE_TYPE_LIVE_PHOTO 生活照
     *                            BDFACE_FEATURE_TYPE_ID_PHOTO 证件照
     *                            BDFACE_FEATURE_TYPE_NIR 红外
     *                            BDFACE_FEATURE_TYPE_RGBD // RGBD特征提取
     * @param imageInstance       图像信息
     * @param imageInstance_depth Depth图像信息
     * @param landmarks           检测后产出的数据
     * @param feature             出参：人脸特征 feature 数组，默认初始化512空字节
     * @return 返回 256个特征数据
     */
    public float featureRGBD(BDFaceSDKCommon.FeatureType featureType, BDFaceImageInstance imageInstance, BDFaceImageInstance imageInstance_depth, float[] landmarks, byte[] feature) {
        return faceFeature.featureRGBD(featureType, imageInstance, imageInstance_depth, landmarks, feature);
    }

    /**
     * 注意力状态获取
     *
     * @param imageInstance 图像信息
     * @param landmarks     检测后产出的关键点数据
     * @return
     */
    public BDFaceGazeInfo gaze(BDFaceImageInstance imageInstance, float[] landmarks) {
        return faceGaze.gaze(imageInstance, landmarks);
    }

    /**
     * 动作活体检测
     * 通过图片和关键点获取眼睛状态信息
     *
     * @param type          动作活体类型
     * @param imageInstance 图像信息
     * @param landmarks     检测后产出的关键点数据
     * @param exist         （出参）是否存在这个动作； 0为不存在该动作 ；1为存在该动作
     * @return
     */
    public int actionLive(BDFaceSDKCommon.BDFaceActionLiveType type, BDFaceImageInstance imageInstance, float[] landmarks, AtomicInteger exist) {
        return faceActionLive.actionLive(type, imageInstance, landmarks, exist);
    }

    /**
     * 清除动作活体历史数据
     * 再进行新一轮的动作活体检测时调用此功能，将之前缓存的人链图片信息清空
     *
     * @return 0表示成功，非0失败
     */
    public int clearActionLiveHistory() {
        return faceActionLive.clearHistory();
    }

    /**
     * 使用人脸框进行人脸扣图
     *
     * @param imageInstance   图片数据信息
     * @param faceinfo        包含人脸框的人脸信息
     * @param enlargeRatio    抠图放大倍数
     * @param isOutofBoundary 是否进行人脸矫正
     * @return
     */
    public BDFaceImageInstance cropFaceByBox(BDFaceImageInstance imageInstance, FaceInfo faceinfo, float enlargeRatio, AtomicInteger isOutofBoundary) {
        return faceCrop.cropFaceByBox(imageInstance, faceinfo, enlargeRatio, isOutofBoundary);
    }

    /**
     * 使用人脸关键点进行人脸扣图
     * ：根据人脸检测结果扣图，扣图结果为矫正之后的人脸信息
     *
     * @param imageInstance   图片数据信息
     * @param landmark        检测后产出数据
     * @param enlargeRatio    抠图放大倍数
     * @param correction      是否进行人脸矫正
     * @param isOutofBoundary 抠图是否：是否超出图像范围（是否有黑边） 0为未超出，1为超出s
     * @return 扣图图像信息，包含宽，高，图片类型，图片数据
     */
    public BDFaceImageInstance cropFaceByLandmark(BDFaceImageInstance imageInstance, float[] landmark, float enlargeRatio, boolean correction, AtomicInteger isOutofBoundary) {
        return faceCrop.cropFaceByLandmark(imageInstance, landmark, enlargeRatio, correction, isOutofBoundary);
    }

    /**
     * 查看人脸是否在边界处
     *
     * @param imageInstance 图片数据信息
     * @param faceinfo      检测后产出的人脸信息
     * @param cropParam     配置参数
     * @return
     */
    public BDFaceIsOutBoundary cropFaceByBoxIsOutofBoundary(BDFaceImageInstance imageInstance, FaceInfo faceinfo, BDFaceCropParam cropParam) {
        return faceCrop.cropFaceByBoxIsOutofBoundary(imageInstance, faceinfo, cropParam);
    }

    /**
     * 根据人脸关键点进行扣图
     *
     * @param imageInstance 图片数据信息
     * @param landmark      关键点信息
     * @param cropParam     配置参数
     * @return
     */
    public BDFaceImageInstance cropFaceByLandmarkParam(BDFaceImageInstance imageInstance, float[] landmark, BDFaceCropParam cropParam) {
        return faceCrop.cropFaceByLandmarkParam(imageInstance, landmark, cropParam);
    }

    /**
     * 驾驶行为监测检测
     *
     * @param imageInstance 图片数据信息
     * @param faceinfo      人脸参数信息（需要包含人脸检测的人脸框数据）
     * @return 驾驶行为监测结果
     */
    public BDFaceDriverMonitorInfo driverMonitor(BDFaceImageInstance imageInstance, FaceInfo faceinfo) {
        return faceDriverMonitor.driverMonitor(imageInstance, faceinfo);
    }

    /**
     * 口罩检测结果获取
     *
     * @param imageInstance 图像信息
     * @param faceInfos     人脸框数据
     * @return 戴口罩置信度
     */
    public float[] checkMask(BDFaceImageInstance imageInstance, FaceInfo[] faceInfos) {
        return faceMouthMask.checkMask(imageInstance, faceInfos);
    }

    /**
     * 图片光照检测接口
     *
     * @param imageInstance 图像信息
     * @param illumScore    图片光照强度
     * @return
     */
    public int imageIllum(BDFaceImageInstance imageInstance, AtomicInteger illumScore) {
        return imageIllum.imageIllum(imageInstance, illumScore);
    }

    /**
     * 安全帽检测
     *
     * @param bdFaceImageInstance 图像信息
     * @param faceInfos           人脸信息
     * @return 安全帽置信度，例如：单个人脸，取值为：float[0]
     */
    public float[] checkHat(BDFaceImageInstance bdFaceImageInstance, FaceInfo[] faceInfos) {
        return faceSafetyHat.checkHat(bdFaceImageInstance, faceInfos);
    }

    /**
     * 暗光检测
     *
     * @param imageInstance 图像信息
     * @return 暗光增强后的图像信息
     */
    public BDFaceImageInstance faceDarkEnhance(BDFaceImageInstance imageInstance) {
        return faceDarkEnhance.faceDarkEnhance(imageInstance);
    }

    /**
     * 1:1 比对
     *
     * @param featureType
     * @param feature1    特征1
     * @param feature2    特征2
     * @param isPercent   控制参数：true返回0~100数值；false 返回0~1
     * @return
     */
    public float compare(BDFaceSDKCommon.FeatureType featureType, byte[] feature1, byte[] feature2, boolean isPercent) {
        return faceSearch.compare(featureType, feature1, feature2, isPercent);
    }

    /**
     * 批量添加数据，主要用于程序启动时
     *
     * @param features
     * @return
     */
    public int pushPersonFeatureList(List<? extends Feature> features) {
        return faceSearch.pushPersonFeatureList(features);
    }

    /**
     * 添加单个数据，主要用于人脸注册以及其他单个人脸添加
     *
     * @param pointID 人员ID
     * @param feature 人员特征
     * @return
     */
    public int pushPersonById(int pointID, byte[] feature) {
        return faceSearch.pushPersonById(pointID, feature);
    }

    /**
     * 根据人员ID删除单个特征数据， 主要用于人脸库删除操作时使用
     *
     * @param pointID 人员ID
     * @return
     */
    public int delPersonById(int pointID) {
        return faceSearch.delPersonById(pointID);
    }

    /**
     * 1:N检索
     * 当前feature和预加载Feature 集合比对，返回预加载Feature集合中命中的id，feature 字段和比对分值score；用户
     * 可以通过id 在数据库中查找全量信息。
     *
     * @param featureType FeatureType.FEATURE_VIS生活照
     *                    FeatureType.FEATURE_ID_PHOTO证件照照
     * @param threshold   比对阈值
     * @param topNum      获取前num 个feature+id映射数组
     * @param feature     当前检查人脸特征值
     * @param isPercent   控制参数：true返回0~100数值；false 返回0~1
     * @return
     */
    public List<? extends Feature> search(BDFaceSDKCommon.FeatureType featureType,
                                          float threshold,
                                          int topNum,
                                          byte[] feature,
                                          boolean isPercent) {
        if (faceSearch == null) {
            return null;
        }
        return faceSearch.search(featureType, threshold, topNum, feature, isPercent);
    }

    /**
     * 1:N检索
     * 当前feature和预加载Feature 集合比对，返回预加载Feature集合中命中的id，feature 字段和比对分值score；用户
     * 可以通过id 在数据库中查找全量信息。
     *
     * @param featureType FeatureType.FEATURE_VIS生活照
     *                    FeatureType.FEATURE_ID_PHOTO证件照照
     * @param threshold   比对阈值
     * @param feature     当前检查人脸特征值
     * @param isPercent   控制参数：true返回0~100数值；false 返回0~1
     * @return
     */
    public List<? extends Feature> search(BDFaceSDKCommon.FeatureType featureType, float threshold,
                                          byte[] feature,
                                          boolean isPercent) {
        if (faceSearch == null) {
            return null;
        }
        return faceSearch.search(featureType, threshold, feature, isPercent);
    }

    /**
     * 1:N检索
     * 当前feature和预加载Feature 集合比对，返回预加载Feature集合中命中的id，feature 字段和比对分值score；用户
     * 可以通过id 在数据库中查找全量信息。
     *
     * @param featureType FeatureType.FEATURE_VIS生活照
     *                    FeatureType.FEATURE_ID_PHOTO证件照照
     * @param topNum      获取前num 个feature+id映射数组
     * @param feature     当前检查人脸特征值
     * @param isPercent   控制参数：true返回0~100数值；false 返回0~1
     * @return
     */
    public List<? extends Feature> search(BDFaceSDKCommon.FeatureType featureType, int topNum,
                                          byte[] feature,
                                          boolean isPercent) {
        if (faceSearch == null) {
            return null;
        }
        return faceSearch.search(featureType, topNum, feature, isPercent);
    }

    /**
     * 获取缓存库内数目
     *
     * @return
     */
    public int getSize() {
        if (faceSearch == null) {
            return 0;
        }
        return faceSearch.getSize();
    }

    /**
     * 获取缓存库内所有人员特征点
     *
     * @return
     */
    public Map<Integer, byte[]> getFeatureMap() {
        if (faceSearch == null) {
            return new HashMap<>();
        }
        return faceSearch.getFeatureMap();
    }

}
