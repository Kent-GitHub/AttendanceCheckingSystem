package com.laughing8.attendancecheckin.utils.showDialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.laughing8.attendancecheckin.R;

/**
 * Created by Laughing8 on 2016/4/16.
 */
public class LoadingDialog extends DialogFragment implements DialogInterface.OnKeyListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        this.getDialog().setOnKeyListener(this);
        return inflater.inflate(R.layout.fragment_loading_dialog, container);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
        //if (keyCode == KeyEvent.KEYCODE_BACK) return true;
        //return false;
    }
}
