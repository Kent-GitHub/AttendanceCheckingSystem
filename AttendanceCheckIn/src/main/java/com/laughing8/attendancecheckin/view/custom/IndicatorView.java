package com.laughing8.attendancecheckin.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.constants.Constants;


/**
 * Created by Laughing8 on 2016/4/4.
 */
public class IndicatorView extends View {

    private int mColor = 0xc0c0c0;
    private Bitmap mIconBitmap;
    private String mText = "Tab";
    private int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
            getResources().getDisplayMetrics());
    //0799fc
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Paint mPaint;
    private Paint mTextPaint;
    private Paint mLinePaint;

    private float mAlpha;

    private Rect mIconRect;
    private Rect mTextBound;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray  a= context.obtainStyledAttributes(attrs,R.styleable.IndicatorView);
        int n=a.getIndexCount();
        for (int i=0;i<n;i++){
            int attr=a.getIndex(i);
            switch (attr){
                case R.styleable.IndicatorView_indicator_icon:
                    mIconBitmap= BitmapFactory.decodeResource(getResources(),
                            a.getResourceId(attr,R.drawable.ic_menu_friendslist));
                    break;
                case R.styleable.IndicatorView_indicator_text:
                    mText=a.getString(attr);
                    break;
                case R.styleable.IndicatorView_indicator_color:
                    mColor=a.getColor(attr,0xc0c0c0);
                    break;
                case R.styleable.IndicatorView_indicator_textSize:
                    mTextSize= (int) a.getDimension(attr,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
                            getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();

        mTextBound=new Rect();
        mTextPaint=new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0x55555);
        mTextPaint.getTextBounds(mText,0,mText.length(),mTextBound);

        mLinePaint=new Paint();
        mLinePaint.setColor(Color.parseColor("#c0c0c0"));

    }

    public void setText(String text){
        mText=text;
        mTextPaint.getTextBounds(mText,0,mText.length(),mTextBound);
        measure(getWidth(),getHeight());
        invalidate();
    }

    public void setImageResource(@DrawableRes int id){
        mIconBitmap=BitmapFactory.decodeResource(getResources(),id);
        measure(getWidth(),getHeight());
        invalidate();
    }

    public void setIconAlpha(float alpha){
        mAlpha=alpha;
        if (Looper.getMainLooper()==Looper.myLooper()){
            invalidate();
        }else {
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int iconWidth=Math.min(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(),
                getMeasuredHeight()-getPaddingTop()-getPaddingBottom()-mTextBound.height());

        int left = getMeasuredWidth()/2-iconWidth/2;
        int top = getMeasuredHeight()/2-(mTextBound.height()+iconWidth)/2;
        mIconRect= new Rect(left,top,left+iconWidth,top+iconWidth);
        //mIconRect.left=left;
        //mIconRect.top=top;
        //mIconRect.right=left+iconWidth;
        //mIconRect.bottom=top+iconWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw图标
        canvas.drawBitmap(mIconBitmap,null,mIconRect,null);
        int alpha= (int) Math.ceil(255*mAlpha);
        //准备纯色图标
        setupTargetBitmap(alpha);
        //draw纯色图标
        canvas.drawBitmap(mBitmap,0,0,null);
        drawSourceText(canvas,alpha);
        drawTargetText(canvas,alpha);
        canvas.drawLine(0f,0f,getWidth(),0f,mLinePaint);
    }

    private void drawTargetText(Canvas canvas, int alpha) {
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        int x=getMeasuredWidth()/2-mTextBound.width()/2;
        int y=mIconRect.bottom+mTextBound.height();
        canvas.drawText(mText,x,y,mTextPaint);
    }

    private void drawSourceText(Canvas canvas, int alpha) {
        mTextPaint.setColor(0x333333);
        mTextPaint.setAlpha(255-alpha);
        int x=getMeasuredWidth()/2-mTextBound.width()/2;
        int y=mIconRect.bottom+mTextBound.height();
        canvas.drawText(mText,x,y,mTextPaint);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle=new Bundle();
        bundle.putParcelable(Constants.InstanceStatus,super.onSaveInstanceState());
        bundle.putFloat(Constants.AlphaStatus,mAlpha);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle= (Bundle) state;
            mAlpha=bundle.getFloat(Constants.AlphaStatus);
            super.onRestoreInstanceState(bundle.getParcelable(Constants.InstanceStatus));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * 在内存中绘制可变色的Icon
     * @param alpha
     */
    private void setupTargetBitmap(int alpha) {
        mBitmap=Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas=new Canvas(mBitmap);

        mPaint=new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);

        mCanvas.drawRect(mIconRect,mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap,null,mIconRect,mPaint);
    }

}
