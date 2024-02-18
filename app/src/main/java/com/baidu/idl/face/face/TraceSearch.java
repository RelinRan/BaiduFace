package com.baidu.idl.face.face;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import com.baidu.idl.face.camera.HardWareInterface;
import com.baidu.idl.face.db.FaceDatabase;
import com.baidu.idl.face.db.FaceTable;
import com.baidu.idl.face.enums.FaceMode;
import com.baidu.idl.face.model.SearchInfo;
import com.baidu.idl.face.model.TraceInfo;
import com.baidu.idl.face.utils.FaceBitmap;
import com.baidu.idl.face.utils.FaceImage;
import com.baidu.idl.face.utils.Hex;
import com.baidu.idl.face.widget.FacePreview;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.Feature;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 人脸处理
 */
public class TraceSearch extends HardWareInterface.ImgCallBack {

    private String TAG = TraceSearch.class.getSimpleName();
    private FaceMode mode;
    private FacePreview display;
    private boolean pause;
    private FaceSDK faceSDK;
    private AtomicInteger isOutofBoundary;
    //抠图
    private BDFaceImageInstance cropImage;
    private Bitmap cropBitmap;
    private FaceInfo faceInfo;
    private byte[] feature;
    private OnFaceTrackListener onFaceTrackListener;
    private OnFaceSearchListener onFaceSearchListener;
    private OnFaceBoundaryListener onFaceBoundaryListener;
    private ExecutorService boundaryService;
    private Future boundaryFuture;
    private ExecutorService featureService;
    private Future featureFuture;
    private ExecutorService searchService;
    private Future searchFuture;
    private TraceSearchHandler handler;
    private FaceDatabase database;
    private boolean traced;
    private boolean searched;
    private long time = 0;

    /**
     * 跟踪搜索
     *
     * @param display 显示器
     * @param mode    模式
     */
    public TraceSearch(FacePreview display, FaceMode mode) {
        this.display = display;
        this.mode = mode;
        feature = new byte[512];
        faceSDK = FaceSDK.getInstance();
        isOutofBoundary = new AtomicInteger();
        boundaryService = Executors.newFixedThreadPool(1);
        featureService = Executors.newFixedThreadPool(1);
        searchService = Executors.newFixedThreadPool(1);
        handler = new TraceSearchHandler();
        database = new FaceDatabase();
    }

    /**
     * 设置模式
     *
     * @param mode
     */
    public void setMode(FaceMode mode) {
        this.mode = mode;
    }

    /**
     * 恢复识别
     */
    public void resume() {
        if (mode == FaceMode.REGISTER) {
            traced = false;
        }
        if (mode == FaceMode.RECOGNIZE) {
            searched = false;
        }
        pause = false;
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mode == FaceMode.REGISTER) {
            traced = true;
        }
        if (mode == FaceMode.RECOGNIZE) {
            searched = true;
        }
        clearBoundary();
        pause = true;
    }

    /**
     * 清除显示的边界
     */
    public void clearBoundary() {
        display.setBoundary(null);
        display.setText("");
    }

    /**
     * 摧毁image
     *
     * @param image
     */
    public void destroyImage(BDFaceImageInstance image) {
        if (image != null) {
            BDFaceImageInstance instance = image.getImage();
            if (instance != null) {
                instance.destory();
            }
            image.destory();
        }
    }

    /**
     * 人脸操作模式
     *
     * @return
     */
    public FaceMode getMode() {
        return mode;
    }

    /**
     * 设置人脸边界监听
     *
     * @param onFaceBoundaryListener
     */
    public void setFaceBoundaryListener(OnFaceBoundaryListener onFaceBoundaryListener) {
        this.onFaceBoundaryListener = onFaceBoundaryListener;
    }

    /**
     * 设置搜索监听
     *
     * @param onFaceSearchListener
     */
    public void setFaceSearchListener(OnFaceSearchListener onFaceSearchListener) {
        this.onFaceSearchListener = onFaceSearchListener;
    }

    /**
     * 设置跟踪监听
     *
     * @param onFaceTrackListener
     */
    public void setFaceTrackListener(OnFaceTrackListener onFaceTrackListener) {
        this.onFaceTrackListener = onFaceTrackListener;
    }

    @Override
    public void onRgbArrive(byte[] imageData, int format, int width, int height, int angle, int isMbyteArrayror) {
        if (pause) {
            clearBoundary();
            return;
        }
        startTrack(imageData, format, width, height, angle, isMbyteArrayror);
    }

    /**
     * 人脸区域
     *
     * @param display  显示器
     * @param faceInfo 人脸信息
     * @param width    图片宽度
     * @param height   图片高度
     * @return
     */
    public RectF getFaceBoundary(FacePreview display, FaceInfo faceInfo, int width, int height) {
        float left = faceInfo.centerX - faceInfo.width / 2F;
        float top = faceInfo.centerY - faceInfo.width / 1.3F;
        float right = faceInfo.centerX + faceInfo.width / 2F;
        float bottom = faceInfo.centerY + faceInfo.width / 1.8F;
        RectF rectF = new RectF(left, top, right, bottom);
        //坐标转换为显示View对应需要的坐标
        int displayWidth = display.getWidth();
        int displayHeight = display.getHeight();
        Matrix matrix = new Matrix();
        if (displayWidth * height > displayHeight * width) {
            int targetHeight = height * displayWidth / width;
            int delta = (targetHeight - displayHeight) / 2;
            float ratio = 1.0f * displayWidth / width;
            matrix.postScale(ratio, ratio);
            matrix.postTranslate(0, -delta);
        } else {
            int targetWith = width * displayHeight / height;
            int delta = (targetWith - displayWidth) / 2;
            float ratio = 1.0f * displayHeight / height;
            matrix.postScale(ratio, ratio);
            matrix.postTranslate(-delta, 0);
        }
        matrix.mapRect(rectF);
        return rectF;
    }

    /**
     * 是否在人脸区域
     *
     * @param boundary
     * @return
     */
    private boolean isBoundary(RectF boundary) {
//        float distanceX = Math.abs(boundary.centerX() - display.getWidth() / 2f);
//        float distanceY = Math.abs(boundary.centerY() - display.getHeight() / 2f);
//        return distanceX <= 100 && distanceY <= 100;
//        return boundary.left >= 0 && boundary.top >= 0 && boundary.right <= display.getWidth() && boundary.bottom <= display.getHeight();
        return true;
    }

    /**
     * 开始人脸跟踪
     *
     * @param imageData
     * @param format
     * @param width
     * @param height
     * @param angle
     * @param isMbyteArrayror
     */
    private void startTrack(byte[] imageData, int format, int width, int height, int angle, int isMbyteArrayror) {
        BDFaceImageInstance faceImage = new BDFaceImageInstance(imageData, height, width, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21, angle, isMbyteArrayror);
        BDFaceImageInstance faceDarkEnhanceImage = faceSDK.faceDarkEnhance(faceImage);
        faceImage.destory();
        //人脸框检测
        FaceInfo[] faces = faceSDK.track(BDFaceSDKCommon.DetectType.DETECT_VIS, BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_FAST, faceDarkEnhanceImage);
        int size = faces == null ? 0 : faces.length;
        if (size == 1) {
            faceInfo = faces[0];
            RectF boundary = getFaceBoundary(display, faceInfo, width, height);//人脸区域
            if ((boundaryFuture != null && boundaryFuture.isDone()) || boundaryFuture == null) {
                boundaryFuture = boundaryService.submit(() -> {
                    handler.sendBoundary(this, boundary, (traceSearch, rectf) -> {
                        display.setBoundary(rectf);
                        display.setText("faceID:" + faceInfo.faceID + ", score:" + faceInfo.score);
                        if (onFaceBoundaryListener != null) {
                            onFaceBoundaryListener.onFaceBoundary(traceSearch, rectf);
                        }
                    });
                });
            }
            long useTime = System.currentTimeMillis() - time;
            if (isBoundary(boundary) && useTime >= 100) {
                Log.i("FaceSDK", "已提取人脸信息 score:" + faceInfo.score);
                if ((featureFuture != null && featureFuture.isDone()) || featureFuture == null) {
                    featureFuture = featureService.submit(() -> {
                        getFeature(faceDarkEnhanceImage, faceInfo, feature);
                    });
                }
            } else {
                destroyImage(faceDarkEnhanceImage);
            }
            time = System.currentTimeMillis();
        } else {
            clearBoundary();
            destroyImage(faceDarkEnhanceImage);
            if (onFaceBoundaryListener != null) {
                onFaceBoundaryListener.onFaceBoundary(this, null);
            }
        }
    }

    /**
     * 获取特征值
     *
     * @param image    暗光处理的image
     * @param faceInfo 人脸信息
     * @param feature  特征值
     */
    private void getFeature(BDFaceImageInstance image, FaceInfo faceInfo, byte[] feature) {
        //239 144 = 95
        //240 122 = 118
        //181 193
        //活体检测
        //float liveScore = faceSDK.silentLive(BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_RGB, image, faceInfo.landmarks);
        //人脸特征提取
        float featureSize = faceSDK.feature(BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO, image, faceInfo.landmarks, feature);
        if (featureSize == 128) {
            Log.i("FaceSDK", "已提取特征值 faceID:" + faceInfo.faceID + ",face size:" + faceSDK.getSize() + ",mode:" + mode+",feature:"+Hex.toHex(feature));
            //注册模式
            if (mode == FaceMode.REGISTER) {
                if (onFaceTrackListener != null && image != null && traced == false) {
                    TraceInfo info = new TraceInfo();
                    Bitmap bitmap = FaceBitmap.getBDFaceImageInstance(image.getImage());
                    info.setImage(FaceImage.toBytes(bitmap.copy(Bitmap.Config.ARGB_8888, true)));
                    info.setFaceInfo(faceInfo);
                    info.setFeature(feature);
                    handler.sendTrace(this, info, onFaceTrackListener);
                    traced = true;
                }
                destroyImage(image);
            }
            //识别模式
            if (mode == FaceMode.RECOGNIZE) {
                destroyImage(image);
                if ((searchFuture != null && searchFuture.isDone()) || searchFuture == null) {
                    searchFuture = searchService.submit(() -> {
                        search(feature);//人脸检索
                    });
                }
            }
        } else {
            destroyImage(image);
        }
    }

    /**
     * 人脸检索
     *
     * @param data 特征值
     */
    private void search(byte[] data) {
        List<? extends Feature> features = faceSDK.search(BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO, SDKConstants.THRESHOLD, 1, data, true);
        int featureSize = features == null ? 0 : features.size();
        if (featureSize > 0) {
            Feature feature = features.get(0);
            float score = feature.getScore();
            int id = feature.getId();
            SearchInfo info = new SearchInfo();
            FaceTable table = database.queryByFaceID(id);
            if (table != null) {
                info.setFaceID(feature.getId());
                info.setUserId(table.getUserId());
                info.setUserName(table.getUserName());
                info.setAvatar(table.getAvatar());
                info.setFaceTime(table.getFaceTime());
            }
            Log.i("FaceSDK", "已搜索到人脸 score:" + score + ",id:" + id + ",userId:" + info.getUserId() + ",userName:" + info.getUserName() + ",featureSize:" + featureSize);
            if (feature.getScore() >= SDKConstants.THRESHOLD && searched == false) {
                searched = true;
                if (onFaceSearchListener != null) {
                    handler.sendSearch(this, info, onFaceSearchListener);
                }
            }
        }
    }

    /**
     * 抠图
     *
     * @param faceInfo
     * @param image
     */
    public Bitmap cropBitmap(FaceInfo faceInfo, BDFaceImageInstance image) {
        cropImage = faceSDK.cropFaceByLandmark(image, faceInfo.landmarks, SDKConstants.ENLARGE_RATIO, false, isOutofBoundary);
        cropBitmap = FaceBitmap.getBDFaceImageInstance(cropImage);
        return cropBitmap;
    }

    /**
     * 销毁资源
     */
    public void destroy() {
        if (boundaryFuture != null && !boundaryFuture.isDone()) {
            boundaryFuture.cancel(true);
        }
        if (featureFuture != null && !featureFuture.isDone()) {
            featureFuture.cancel(true);
        }
        if (searchFuture != null && !searchFuture.isDone()) {
            searchFuture.cancel(true);
        }
    }

}
