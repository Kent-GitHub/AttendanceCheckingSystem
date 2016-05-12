/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.kent.view;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Like AnalogClock, but digital.  Shows seconds.
 */
@Deprecated
public class CustomDigitalClock extends View {

    private static Calendar mCalendar;
    @SuppressWarnings("FieldCanBeLocal") // We must keep a reference to this observer
    private FormatChangeObserver mFormatChangeObserver;

    private Runnable mTicker;
    private Handler mHandler;

    private int textColor = Color.parseColor("#ff000000");

    private boolean mTickerStopped = false;

    String mFormat;

    private String minute = "18:18";
    private Paint minutePaint;
    private String second = "18";
    private Paint secondPaint;
    private String dayOfWeek = "星期六";
    private String day = "2018年05月18日";
    private Paint dayPaint;

    public CustomDigitalClock(Context context) {
        this(context, null);
    }

    public CustomDigitalClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomDigitalClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initClock();
    }

    private void initClock() {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        initPaint();
        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        setFormat();
    }

    private static long timeInterval;

    public static void setTime(long time) {
        timeInterval = time - System.currentTimeMillis();
    }

    public static Calendar getTime() {
        if (mCalendar == null) mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis() + timeInterval);
        return mCalendar;
    }

    public static long getTimeInterval() {
        return timeInterval;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped) return;
                mCalendar.setTimeInMillis(System.currentTimeMillis() + timeInterval);
                String dateString = sdf.format(mCalendar.getTime());
                //"yyyy年MM月dd日HH:mm:ss"
                minute = dateString.substring(11, 16);
                second = dateString.substring(17, 19);
                dayOfWeek = getDayOfWeek(mCalendar.get(Calendar.DAY_OF_WEEK));
                day = dateString.substring(0, 11);
                //setText(DateFormat.format(mFormat, mCalendar));
                invalidate();
                //long now = SystemClock.uptimeMillis() + timeInterval;
                //long next = now + (1000 - now % 1000);
                //mHandler.postAtTime(mTicker, next - timeInterval);
                long netTime = System.currentTimeMillis() + timeInterval;
                long next = 1000 - netTime % 1000;
                mHandler.postDelayed(mTicker,next);
            }
        };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    private void initPaint() {
        minutePaint = new Paint();
        secondPaint = new Paint();
        dayPaint = new Paint();
        minutePaint.setTextAlign(Paint.Align.CENTER);
        secondPaint.setTextAlign(Paint.Align.CENTER);
        dayPaint.setTextAlign(Paint.Align.CENTER);
        minutePaint.setAntiAlias(true);
        secondPaint.setAntiAlias(true);
        dayPaint.setAntiAlias(true);
        minutePaint.setColor(textColor);
        secondPaint.setColor(textColor);
        dayPaint.setColor(textColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(minute, getWidth() * 0.25f, getHeight() * 0.9f, minutePaint);
        canvas.drawText(second, getWidth() * 0.45f, getHeight() * 0.9f, secondPaint);
        canvas.drawText(dayOfWeek, getWidth() * 0.725f, getHeight() / 2, dayPaint);
        canvas.drawText(day, getWidth() * 0.725f, getHeight() * 0.9f, dayPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent == null) return;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
//            mPaint.setTextSize(mTitleTextSize);
//            mPaint.getTextBounds(mTitle, 0, mTitle.length(), mBounds);
//            float textWidth = mBounds.width();
//            int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
//            width = desired;
            width = parent.getWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
//            mPaint.setTextSize(mTitleTextSize);
//            mPaint.getTextBounds(mTitle, 0, mTitle.length(), mBounds);
//            float textHeight = mBounds.height();
//            int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
//            height = desired;
            height = (int) (width * 0.16);
        }
        minutePaint.setTextSize(height * 0.8f);
        secondPaint.setTextSize(height * 0.3f);
        dayPaint.setTextSize(height * 0.3f);
        setMeasuredDimension(width, height);
    }

    private String getDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "星期天";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
        return "星期七";
    }

    private void setFormat() {
        mFormat = "yyyy年MM月dd日HH:mm:ss";
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        minutePaint.setColor(textColor);
        secondPaint.setColor(textColor);
        dayPaint.setColor(textColor);
    }
}
