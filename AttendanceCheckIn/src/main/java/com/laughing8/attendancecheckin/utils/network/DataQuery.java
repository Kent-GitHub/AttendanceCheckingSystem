package com.laughing8.attendancecheckin.utils.network;

import android.content.Context;

import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.bmobobject.RecordByMouth;
import com.laughing8.attendancecheckin.bmobobject.RecordType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Laughing8 on 2016/5/9.
 */
public class DataQuery {

    private Context mContext;

    private List<RecordType> mRecordTypeList;

    private static List<MUser> mUserList;

    private static Map<String, MUser> mUserMap;

    public DataQuery(Context context) {
        mContext = context;
    }

    public DataQuery(Context context, OnQueryFinishListener listener) {
        mContext = context;
        mOnQueryFinishListener = listener;
        mUserMap = new HashMap<>();
        contactQuery(0);
    }

    private OnQueryFinishListener mOnQueryFinishListener;

    public void recordTypeQuery(final int queryCode) {

        BmobQuery<RecordType> query = new BmobQuery<>();
        query.findObjects(mContext, new FindListener<RecordType>() {
            @Override
            public void onSuccess(List<RecordType> list) {
                mOnQueryFinishListener.queryFinish(list, queryCode);
            }

            @Override
            public void onError(int i, String s) {
                mOnQueryFinishListener.queryFinish(null, queryCode);
            }
        });

    }

    private void convertContactToMap(List<MUser> list) {
        for (MUser user : list) {
            mUserMap.put(user.getUsername(), user);
        }
    }

    public MUser getUser(String userName) {
        return mUserMap.get(userName);
    }

    public void contactQuery(final int queryCode) {
        BmobQuery<MUser> query = new BmobQuery<>();
        query.findObjects(mContext, new FindListener<MUser>() {
            @Override
            public void onSuccess(List<MUser> list) {
                mOnQueryFinishListener.queryFinish(list, queryCode);
                convertContactToMap(list);
            }

            @Override
            public void onError(int i, String s) {
                mOnQueryFinishListener.queryFinish(null, queryCode);
            }
        });
    }

    public void recordQuery(final int queryCode, List<String> where, List values) {
        BmobQuery<RecordByMouth> query = new BmobQuery<>();
        for (int i = 0; i < where.size(); i++) {
            query.addWhereEqualTo(where.get(i), values.get(i));
        }
        query.findObjects(mContext, new FindListener<RecordByMouth>() {
            @Override
            public void onSuccess(List<RecordByMouth> list) {
                mOnQueryFinishListener.queryFinish(list, queryCode);
            }

            @Override
            public void onError(int i, String s) {
                mOnQueryFinishListener.queryFinish(null, queryCode);
            }
        });
    }


    public interface OnQueryFinishListener {
        void queryFinish(List result, int queryCode);
    }

    public void setOnQueryFinishListener(OnQueryFinishListener listener) {
        mOnQueryFinishListener = listener;
    }

}
