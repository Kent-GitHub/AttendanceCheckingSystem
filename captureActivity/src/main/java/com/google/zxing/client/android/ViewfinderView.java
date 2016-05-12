/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;

    /**
     * 距离扫描框下面的字体的大小
     */
    private static final int TEXT_SIZE = 13;
    /**
     * 手机的屏幕密度
     */
    private static float density;

    private static final int TEXT_PADDING_TOP = 30;

    private CameraManager cameraManager;
    private final Paint paint;
    private Bitmap resultBitmap;
    private final int resultColor;
    private final int maskColor;
    private final int resultPointColor;
    private int scannerAlpha;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;
    private boolean showResult;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();

        resultColor = resources.getColor(R.color.result_view);
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;
        density = context.getResources().getDisplayMetrics().density;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    private boolean captureInited;

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        if (!captureInited) {
            captureAtyInit(cameraManager.getFramingRect());
            initAttendanceTvInfo(null);
            captureInited = true;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int frameWidth = frame.right - frame.left;
        int frameHeight = frame.bottom - frame.top;
        // 画扫描框外半透明区域
        // Draw the exterior (i.e. outside the framing rect) darkened
        if (showResult){
            paint.setColor(resultColor);
        }else {
            paint.setColor(maskColor);
        }
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);


        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            //画边框-------------------------------------------------------------------------------------
            paint.setColor(Color.parseColor("#02668e"));
            int rectLength = cameraManager.getScannerWidth() / 15;
            int rectWidth = cameraManager.getScannerWidth() / 80;
            //左上角
            canvas.drawRect(frame.left - rectWidth, frame.top - rectWidth, frame.left + rectLength, frame.top, paint);
            canvas.drawRect(frame.left - rectWidth, frame.top - rectWidth, frame.left, frame.top + rectLength, paint);
            //右上角
            canvas.drawRect(frame.right - rectLength, frame.top - rectWidth, frame.right + rectWidth, frame.top, paint);
            canvas.drawRect(frame.right, frame.top - rectWidth, frame.right + rectWidth, frame.top + rectLength, paint);
            //左下角
            canvas.drawRect(frame.left - rectWidth, frame.bottom, frame.left + rectLength, frame.bottom + rectWidth, paint);
            canvas.drawRect(frame.left - rectWidth, frame.bottom - rectLength, frame.left, frame.bottom + rectWidth, paint);
            //右下角
            canvas.drawRect(frame.right - rectLength, frame.bottom, frame.right + rectWidth, frame.bottom + rectWidth, paint);
            canvas.drawRect(frame.right, frame.bottom - rectLength, frame.right + rectWidth, frame.bottom + rectWidth, paint);

            //画扫描框下面的字----------------------------------------------------------------------------
            paint.setColor(Color.WHITE);
            paint.setTextSize(TEXT_SIZE * density);
            paint.setTypeface(Typeface.create("System", Typeface.BOLD));
            String text = getResources().getString(R.string.scan_text);
            int textWidth = (int) paint.measureText(text);
            int offSet = (cameraManager.getScannerWidth() - textWidth) / 2;
            canvas.drawText(text, frame.left + offSet, (frame.bottom + (float) TEXT_PADDING_TOP * density), paint);

            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                synchronized (currentPossible) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                POINT_SIZE, paint);
                    }
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                synchronized (currentLast) {
                    float radius = POINT_SIZE / 2.0f;
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                radius, paint);
                    }
                }
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    private void initAttendanceTvInfo(String info) {

    }

    private void captureAtyInit(Rect frame) {
        //获取状态栏高度
        Rect viewFrame = new Rect();
        View rootView = getRootView();
        rootView.getWindowVisibleDisplayFrame(viewFrame);
        //初始化扫描线
        ImageView scannerLine = (ImageView) rootView.findViewById(R.id.capture_scan_line);
        ViewGroup.LayoutParams lp1 = scannerLine.getLayoutParams();
        lp1.height = frame.height();
        lp1.width = frame.width();
        scannerLine.setLayoutParams(lp1);
        //初始化签到信息TextView
        Calendar calendar = Calendar.getInstance();
        TextView attendanceTv = (TextView) rootView.findViewById(R.id.capture_attendance_info);
        attendanceTv.setTextColor(Color.WHITE);
        attendanceTv.setTextSize(22);
        attendanceTv.setY(frame.top * 2.7f);
        //初始化ResultView
        View resultView=rootView.findViewById(R.id.capture_result);
        ViewGroup.LayoutParams resultLp = resultView.getLayoutParams();
        resultLp.height = (int) ((frame.bottom - frame.top)*1.1);
        resultLp.width= (int) (resultLp.height*1.2);
        resultView.setLayoutParams(resultLp);
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }

    public void setShowResult(boolean showResult) {
        this.showResult = showResult;
    }
}
