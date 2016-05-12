package com.laughing8.attendancecheckin.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laughing8.attendancecheckin.R;

/**
 * Created by Laughing8 on 2016/3/28.
 */
public class SettingViewFroward extends RelativeLayout {
    private ImageView imageL;
    private TextView textL;
    private ImageView imageR;
    private TextView textR;
    private boolean drawTopLine;
    private Paint mPain;
    private float topLineFrom;
    private float topLineTo;
    String TAG = "SettingViewFroward";

    public SettingViewFroward(Context context) {
        this(context, null);
    }

    public SettingViewFroward(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingViewFroward(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.setting_item, this);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SettingViewFroward, defStyleAttr, 0);
        init(typedArray);
    }

    public void hasTopLine(boolean hasTopLine) {
        this.drawTopLine = hasTopLine;
    }

    public void hasTopLine(boolean hasTopLine, float from, float to) {
        this.drawTopLine = hasTopLine;
        this.topLineFrom = from;
        this.topLineTo = to;
    }

    private void init(TypedArray typedArray) {
        setClickable(true);
        setBackgroundResource(R.drawable.setting_item_bg);
        //初始化Paint
        mPain = new Paint();
        mPain.setColor(Color.parseColor("#e5e5e5"));
        setWillNotDraw(false);
        //设置子控件垂直居中
        setGravity(Gravity.CENTER_VERTICAL);
        //子控件实例化
        imageL = (ImageView) findViewById(R.id.imageLeft);
        imageL.setVisibility(GONE);
        textL = (TextView) findViewById(R.id.textLeft);
        imageR = (ImageView) findViewById(R.id.imageButton);
        textR = (TextView) findViewById(R.id.textRight);
        //从Layout获取设置
        if (typedArray != null && typedArray.getIndexCount() > 0) {
            //1
            textL.setText(typedArray.getString(R.styleable.SettingViewFroward_textLeft));
            //2
            textR.setText(typedArray.getString(R.styleable.SettingViewFroward_textRight));
            //3
            drawTopLine = typedArray.getBoolean(R.styleable.SettingViewFroward_topLine, false);
            //4
            topLineFrom = typedArray.getFloat(R.styleable.SettingViewFroward_from, 0.05f);
            //5
            topLineTo = typedArray.getFloat(R.styleable.SettingViewFroward_to, 1f);
            //6
            if (typedArray.getInteger(R.styleable.SettingViewFroward_textLeftMargin, -1) != -1) {
                LayoutParams textLLP = (LayoutParams) textL.getLayoutParams();
                textLLP.leftMargin = (int) typedArray.getDimension(R.styleable.SettingViewFroward_textLeftMargin, 0);
                textL.setLayoutParams(textLLP);
            }
            //7
            if (typedArray.getInteger(R.styleable.SettingViewFroward_imageLeftRes, -1) != -1) {
                imageL.setImageResource(typedArray.getInteger(R.styleable.SettingViewFroward_imageLeftRes, -1));
            }
        }
    }

    public void setMarginTop(int marginTop) {
        if (getParent() == null) {
            return;
        }
        try {
            if (getParent() instanceof LinearLayout) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getLayoutParams();
                lp.topMargin = marginTop;
                setLayoutParams(lp);
            } else if (getParent() instanceof RelativeLayout) {
                LayoutParams lp = (LayoutParams) getLayoutParams();
                lp.topMargin = marginTop;
                setLayoutParams(lp);
            } else if (getParent() instanceof FrameLayout) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
                lp.topMargin = marginTop;
                setLayoutParams(lp);
            }
        } catch (Exception e) {
            Log.e("SettingView", "Setting中setMarginTop错误");
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (drawTopLine) {
            canvas.drawLine(getWidth() * topLineFrom, 0f, getWidth() * topLineTo, 0f, mPain);
        }
    }

    public ImageView getImageL() {
        return imageL;
    }

    public void setImageL(ImageView imageL) {
        this.imageL = imageL;
    }

    public TextView getTextL() {
        return textL;
    }

    public void setTextL(TextView textL) {
        this.textL = textL;
    }

    public ImageView getImageR() {
        return imageR;
    }

    public void setImageR(ImageButton imageR) {
        this.imageR = imageR;
    }

    public TextView getTextR() {
        return textR;
    }

    public void setTextR(TextView textR) {
        this.textR = textR;
    }

}
