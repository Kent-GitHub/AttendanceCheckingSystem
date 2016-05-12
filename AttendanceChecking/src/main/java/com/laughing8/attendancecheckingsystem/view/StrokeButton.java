package com.laughing8.attendancecheckingsystem.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

import com.laughing8.attendancecheckingsystem.R;

/**
 * Created by Laughing8 on 2016/4/1.
 */
public class StrokeButton extends Button {
    private boolean strokeLeft, strokeTop, strokeRight, strokeBottom;
    private Paint mPain;
    private String TAG="StrokeButton";

    public StrokeButton(Context context) {
        this(context, null);
    }

    public StrokeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StrokeButton, defStyleAttr, 0);
        init(typedArray,context);
    }

    private void init(TypedArray typedArray, Context context) {
        //初始化Paint
        mPain=new Paint();
        mPain.setColor(Color.parseColor("#d2d2d2"));
        //获取Layout设置
        if (typedArray != null && typedArray.getIndexCount() > 0) {
            for (int i = 0; i < typedArray.getIndexCount(); i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case R.styleable.StrokeButton_strokeLeft:
                        strokeLeft=typedArray.getBoolean(attr,false);
                        break;
                    case R.styleable.StrokeButton_strokeTop:
                        strokeTop=typedArray.getBoolean(attr,false);
                        break;
                    case R.styleable.StrokeButton_strokeRight:
                        strokeRight=typedArray.getBoolean(attr,false);
                        break;
                    case R.styleable.StrokeButton_strokeBottom:
                        strokeBottom=typedArray.getBoolean(attr,false);
                        break;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (strokeLeft){
            canvas.drawLine(0f,0f,0.1f,getHeight(),mPain);
        }
        if (strokeTop){
            canvas.drawLine(0f,0f,getWidth(),0.1f,mPain);
        }
        if (strokeRight){
            canvas.drawLine(getWidth()-0.2f,0f,getWidth()-0.1f,getHeight(),mPain);
        }
        if (strokeBottom){
            canvas.drawLine(0f,getHeight()-0.2f,getWidth(),getHeight()-0.1f,mPain);
        }
    }
}
