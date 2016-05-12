package com.laughing8.attendancecheckin.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.laughing8.attendancecheckin.R;

/**
 * Created by Laughing8 on 2016/4/28.
 */
public class StrokeLinearLayout extends LinearLayout{

    private Paint mPain;
    private boolean strokeLeft;
    private boolean strokeTop;
    private boolean strokeRight;
    private boolean strokeBottom;

    public StrokeLinearLayout(Context context) {
        this(context,null);
    }

    public StrokeLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public StrokeLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StrokeLinearLayout, defStyleAttr, 0);
        init(typedArray);
    }

    private void init(TypedArray typedArray) {
        setWillNotDraw(false);
        //初始化Paint
        mPain=new Paint();
        mPain.setColor(Color.parseColor("#d2d2d2"));
        //获取Layout设置
        if (typedArray != null && typedArray.getIndexCount() > 0) {
            for (int i = 0; i < typedArray.getIndexCount(); i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case R.styleable.StrokeLinearLayout_strokeLeft:
                        strokeLeft=typedArray.getBoolean(attr,false);
                        break;
                    case R.styleable.StrokeLinearLayout_strokeTop:
                        strokeTop=typedArray.getBoolean(attr,false);
                        break;
                    case R.styleable.StrokeLinearLayout_strokeRight:
                        strokeRight=typedArray.getBoolean(attr,false);
                        break;
                    case R.styleable.StrokeLinearLayout_strokeBottom:
                        strokeBottom=typedArray.getBoolean(attr,false);
                        break;
                }
            }
        }
    }

    public void setStrokeLeft(boolean strokeLeft) {
        this.strokeLeft = strokeLeft;
    }

    public void setStrokeTop(boolean strokeTop) {
        this.strokeTop = strokeTop;
    }

    public void setStrokeRight(boolean strokeRight) {
        this.strokeRight = strokeRight;
    }

    public void setStrokeBottom(boolean strokeBottom) {
        this.strokeBottom = strokeBottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (strokeLeft){
            canvas.drawLine(0f,0f,0f,getHeight(),mPain);
        }
        if (strokeTop){
            canvas.drawLine(0f,0f,getWidth(),0f,mPain);
        }
        if (strokeRight){
            canvas.drawLine(getWidth()-0.1f,0f,getWidth()-0.1f,getHeight(),mPain);
        }
        if (strokeBottom){
            canvas.drawLine(0f,getHeight()-0.1f,getWidth(),getHeight()-0.1f,mPain);
        }
    }


}
