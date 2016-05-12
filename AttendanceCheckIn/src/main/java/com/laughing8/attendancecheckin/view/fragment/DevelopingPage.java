package com.laughing8.attendancecheckin.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laughing8.attendancecheckin.R;

/**
 * Created by Laughing8 on 2016/5/12.
 */
public class DevelopingPage extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_developing,null);
        return view;
    }
}
