package com.baidu.idl.face.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.util.List;


/**
 * 红外线相机
 */

public class SingleNirCamera implements HardWareInterface, Camera.PreviewCallback, Camera.ErrorCallback {

    private String TAG = SingleNirCamera.class.getSimpleName();
    // 相机
    private Camera mNirCamera;
    private Camera.Parameters mCameraParam;

    private int mCameraId;
    private int mDisplayWidth = 0;
    private int mDisplayHeight = 0;
    private int degree;

    private SurfaceHolder mSurfaceHolder;
    private Context mContext;
    private NirImgCallBack callBack;
    private int startPreviewCount = 0;

    public void setSurfaceHolder(SurfaceHolder mSurfaceHolder) {
        this.mSurfaceHolder = mSurfaceHolder;
    }

    @Override
    public int initHardWare(Context context) {
        this.mContext = context;
        mDisplayWidth = 480;
        mDisplayHeight = 640;
        return 0;
    }

    @Override
    public int openHardWare() {
        if (mNirCamera == null) {
            try {
                int[] cameraIdArray = new int[1];
                mNirCamera = CameraUtils.open(1);
                mCameraId = cameraIdArray[0];
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return (mNirCamera == null ? -1 : 0);
    }

    @Override
    public int registDataCallBack(ImgCallBack dataCallBack) {
        return 0;
    }


    @Override
    public int registDataCallBack(NirImgCallBack dataCallBack) {
        if (dataCallBack == null) {
            return -1;
        } else {
            this.callBack = dataCallBack;
            return 0;
        }
    }


    @Override
    public int startPreview() {
        if (startPreviewCount > 0) {
            Log.i(TAG, "nir preview have been started ");
            return -2;
        }
        startPreviewCount++;
//        Long rgbStartTime = System.currentTimeMillis();
        if (mNirCamera == null) {
            try {
                openHardWare();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mNirCamera == null) {
            return -1;
        }
        if (mCameraParam == null) {
            mCameraParam = mNirCamera.getParameters();
        }

        mCameraParam.setPictureFormat(PixelFormat.JPEG);
        degree = displayOrientation();
        mNirCamera.setDisplayOrientation(270);
        // 设置后无效，camera.setDisplayOrientation方法有效
        mCameraParam.set("rotation", degree);

//        Point point = CameraPreviewUtils.getBestPreview(mCameraParam, new Point(mDisplayWidth, mDisplayHeight));

        int mPreviewWidth = mDisplayWidth;
        int mPreviewHight = mDisplayHeight;
        if (degree % 90 == 0 || degree % 270 == 0) {
            mPreviewWidth = mDisplayHeight;
            mPreviewHight = mDisplayWidth;
        }

        mCameraParam.setPreviewSize(mPreviewWidth, mPreviewHight);
        List<int[]> fps = mCameraParam.getSupportedPreviewFpsRange();
        int[] fpsRange = fps.get(fps.size()-1);
        mCameraParam.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
        mNirCamera.setParameters(mCameraParam);
        try {
            if (mSurfaceHolder != null) {
                mNirCamera.setPreviewDisplay(mSurfaceHolder);
            }
            mNirCamera.stopPreview();
            mNirCamera.setErrorCallback(this);
            mNirCamera.setPreviewCallback(this);
            mNirCamera.startPreview();
        } catch (RuntimeException e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mNirCamera);
            mNirCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mNirCamera);
            mNirCamera = null;
        }
//        Long rgbStartEndTime = System.currentTimeMillis();
        return 0;
    }

    @Override
    public int stopPreview() {
        startPreviewCount--;
        if (mNirCamera != null) {
            try {
                mNirCamera.setErrorCallback(null);
                mNirCamera.setPreviewCallback(null);
                mNirCamera.stopPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    @Override
    public int closeHardWare() {
        CameraUtils.releaseCamera(mNirCamera);
        mNirCamera = null;
        return 0;
    }

    @Override
    public void destroy() {
        mContext = null;
        mSurfaceHolder = null;
        callBack = null;
    }

    private int displayOrientation() {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
                break;
        }
        int result = (0 - degrees + 360) % 360;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    @Override
    public void onError(int error, Camera camera) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (this.callBack != null) {
            // camera.getParameters() RuntimeException
            Camera.Size size = mCameraParam.getPreviewSize();
            // 返回数据
            this.callBack.onNirArrive(data, 0, size.width, size.height, degree, 0);
        }
    }
}
