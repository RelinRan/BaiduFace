package com.baidu.idl.face.widget;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.idl.face.R;
import com.baidu.idl.face.camera.SingleRgbCamera;
import com.baidu.idl.face.enums.FaceMode;
import com.baidu.idl.face.face.OnFaceSearchListener;
import com.baidu.idl.face.face.OnFaceTrackListener;
import com.baidu.idl.face.face.TraceSearch;

/**
 * 人脸预览
 */
public class FacePreview extends FrameLayout implements SurfaceHolder.Callback {

    private String TAG = FacePreview.class.getSimpleName();
    private SurfaceView display;
    private TextView tvMsg;
    private SingleRgbCamera camera;
    private TraceSearch traceSearch;
    private FaceBoundary faceBoundary;

    public FacePreview(@NonNull Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public FacePreview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public FacePreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.face_preview, this, true);
        display = findViewById(R.id.display);
        tvMsg = findViewById(R.id.tv_msg);
        faceBoundary = findViewById(R.id.boundary);
        display.getHolder().addCallback(this);
        camera = new SingleRgbCamera();
        camera.initHardWare(context);
        camera.openHardWare();
        traceSearch = new TraceSearch(this, FaceMode.REGISTER);
        camera.registDataCallBack(traceSearch);
    }

    /**
     * 设置边界
     *
     * @param boundary
     */
    public void setBoundary(RectF boundary) {
        faceBoundary.setBoundary(boundary);
    }

    /**
     * 设置人脸模式
     *
     * @param mode
     */
    public void setFaceMode(FaceMode mode) {
        traceSearch.setMode(mode);
    }

    /**
     * 恢复人脸解析
     */
    public void resume() {
        traceSearch.resume();
    }

    /**
     * 暂停人脸解析
     */
    public void pause() {
        traceSearch.pause();
    }

    /**
     * 显示文字
     *
     * @param value
     */
    public void setText(String value) {
        tvMsg.setText(value);
    }

    /**
     * 设置人脸搜索监听
     *
     * @param onFaceSearchListener
     */
    public void setFaceSearchListener(OnFaceSearchListener onFaceSearchListener) {
        traceSearch.setFaceSearchListener(onFaceSearchListener);
    }

    /**
     * 设置人脸跟踪监听
     *
     * @param onFaceTrackListener
     */
    public void setFaceTrackListener(OnFaceTrackListener onFaceTrackListener) {
        traceSearch.setFaceTrackListener(onFaceTrackListener);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        camera.setSurfaceHolder(holder);
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        camera.stopPreview();
        camera.closeHardWare();
        traceSearch.destroy();
    }


}
