package com.baidu.idl.face.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * 人脸边界
 */
public class FaceBoundary extends View {

    private Paint paint;
    private RectF boundary;

    public FaceBoundary(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public FaceBoundary(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public FaceBoundary(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.CYAN);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 设置边界
     *
     * @param boundary
     */
    public void setBoundary(RectF boundary) {
        this.boundary = boundary;
       invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (boundary != null) {
            canvas.drawRect(boundary, paint);
        }
    }

}
