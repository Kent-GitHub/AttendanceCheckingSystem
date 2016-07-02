package com.laughing8.attendancecheckin.utils.showDialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;

/**
 * Created by Laughing8 on 2016/5/25.
 */
public class ShowDialogTool {

    private FragmentActivity mContext;
    private SimpleDialogFragment.SimpleDialogBuilder mBuilder;
    private LoadingDialog mLoadingDialog;

    public ShowDialogTool(FragmentActivity context) {
        mContext = context;
        mBuilder = SimpleDialogFragment.createBuilder(context, context.getSupportFragmentManager());
        mLoadingDialog = new LoadingDialog();
    }

    public ShowDialogTool setTargetFragment(Fragment fragment, int requestCode) {
        mBuilder.setTargetFragment(fragment, requestCode);
        return this;
    }

    public ShowDialogTool setConfirmTitle(String title) {
        mBuilder.setTitle(title);
        return this;
    }

    public ShowDialogTool setPositionText(String positionText) {
        mBuilder.setPositiveButtonText(positionText);
        return this;
    }

    public ShowDialogTool setNegativeText(String negativeText) {
        mBuilder.setNegativeButtonText(negativeText);
        return this;
    }

    public void showConfirm() {
        mBuilder.show();
    }

    public void showLoading() {
        mLoadingDialog.show(mContext.getSupportFragmentManager(), "mLoadingDialog");
    }

    public void dismissLoading() {
        mLoadingDialog.dismiss();
    }

    public void showResult(String resultTitle) {
        mBuilder.setTitle(resultTitle).setNegativeButtonText("关闭").show();
    }
}
