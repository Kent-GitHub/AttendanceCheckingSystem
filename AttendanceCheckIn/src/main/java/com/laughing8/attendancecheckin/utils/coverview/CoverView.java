package com.laughing8.attendancecheckin.utils.coverview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.laughing8.attendancecheckin.R;


public class CoverView extends RelativeLayout{
	
	public static CoverView mCoverView;
	
	public TextView mTextView;
		
	public ImageView mImageView;
	
	public ProgressBar mProgressBar;
	
	public Button mButton;
	
	public CoverView(Context context) {
		super(context);
	}

	public CoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CoverView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public CoverView showProgressBar(){
			mProgressBar.setVisibility(View.VISIBLE);
		return this;
	}
	public CoverView showImageView(){
		mImageView.setVisibility(View.VISIBLE);
		return this;
	}
	public CoverView showTextView(){
		mTextView.setVisibility(View.VISIBLE);
		return this;
	}
	public CoverView showButton(){
		mButton.setVisibility(View.VISIBLE);
		return this;
	}
	public CoverView hideProgressBar(){
			mProgressBar.setVisibility(View.GONE);
		return this;
	}

	public CoverView hideImageView(){
		mImageView.setVisibility(View.GONE);
		return this;
	}
	public CoverView hideTextView(){
		mTextView.setVisibility(View.GONE);
		return this;
	}
	public CoverView hideButton(){
		mButton.setVisibility(View.GONE);
		return this;
	}
	public CoverView setLoadingText(String text){
		mTextView.setText(text);
		return this;
	}
	
	/**
	 * 生成一个CoverView实例
	 * @param context
	 * @param view
	 * @return
	 */
	public static CoverView build(Context context,ViewGroup view){
		mCoverView = (CoverView) LayoutInflater.from(context).inflate(R.layout.cover_view_layout,view,false);
		return mCoverView;
	}
	
}
