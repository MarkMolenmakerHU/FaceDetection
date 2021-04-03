package com.example.facedetection.Helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

public class RectOverlay extends GraphicOverlay.Graphic {

    private Paint mRectPaint, mPointPaint;
    private GraphicOverlay graphicOverlay;
    private Rect rect;
    private PointF left_eye, right_eye;

    public RectOverlay(GraphicOverlay overlay, Rect rect, PointF left_eye, PointF right_eye) {
        super(overlay);

        mRectPaint = new Paint();
        mRectPaint.setColor(Color.GREEN);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(4.0f);

        mPointPaint = new Paint();
        mPointPaint.setColor(Color.RED);
        mPointPaint.setStyle(Paint.Style.FILL);

        this.graphicOverlay = overlay;
        this.rect = rect;
        this.left_eye = left_eye;
        this.right_eye = right_eye;

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rectF = new RectF(rect);
        rectF.left = translateX(rectF.left);
        rectF.right = translateX(rectF.right);
        rectF.top = translateY(rectF.top);
        rectF.bottom = translateY(rectF.bottom);

        canvas.drawCircle(left_eye.x , left_eye.y, 10, mPointPaint);
        canvas.drawCircle(right_eye.x, right_eye.y, 10, mPointPaint);
        canvas.drawRect(rectF, mRectPaint);
    }

}
