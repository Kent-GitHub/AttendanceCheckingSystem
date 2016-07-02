package com.laughing8.attendancecheckin.utils.bmobquery;

import android.content.Context;

import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.bmobobject.RecordByMouth;
import com.laughing8.attendancecheckin.bmobobject.RecordType;
import com.laughing8.attendancecheckin.bmobobject.RequestRecord;
import com.laughing8.attendancecheckin.bmobobject.RequestType;

import java.util.ArrayList;
import java.util.Collections;
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

    private List<RequestType> mRequestTypeList;

    private static List<MUser> mUserList;

    private static Map<String, MUser> mUserMap;

    public DataQuery(Context context) {
        mContext = context;
    }

    public DataQuery(Context context, OnQueryFinishListener listener) {
        mContext = context;
        mOnQueryFinishListener = listener;
        if (mUserMap==null) mUserMap=new HashMap<>();
        if (mUserList == null) contactQuery(0);
    }

    private OnQueryFinishListener mOnQueryFinishListener;

    public void recordTypeQuery(final int queryCode) {

        BmobQuery<RecordType> query = new BmobQuery<>();
        query.findObjects(mContext, new FindListener<RecordType>() {
            @Override
            public void onSuccess(List<RecordType> list) {
                if (list != null && list.size() > 0) mRecordTypeList = list;
                mOnQueryFinishListener.onQuerySuccess(list, queryCode);
            }

            @Override
            public void onError(int i, String s) {
                mOnQueryFinishListener.onQuerySuccess(null, queryCode);
            }
        });

    }

    public void requestTypeQuery(final int queryCode) {
        BmobQuery<RequestType> query = new BmobQuery<>();
        query.findObjects(mContext, new FindListener<RequestType>() {
            @Override
            public void onSuccess(List<RequestType> list) {
                if (list != null && list.size() > 0) mRequestTypeList = list;
                mOnQueryFinishListener.onQuerySuccess(list, queryCode);
            }

            @Override
            public void onError(int i, String s) {
                mOnQueryFinishListener.onQuerySuccess(null, queryCode);
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
                if (list != null && list.size() > 0) mUserList = list;
                mOnQueryFinishListener.onQuerySuccess(list, queryCode);
                convertContactToMap(list);
            }

            @Override
            public void onError(int i, String s) {
                mOnQueryFinishListener.onQuerySuccess(null, queryCode);
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
                mOnQueryFinishListener.onQuerySuccess(list, queryCode);
            }

            @Override
            public void onError(int i, String s) {
                mOnQueryFinishListener.onQuerySuccess(null, queryCode);
            }
        });
    }

    public void requestQuery(final int queryCode, String[] where, String[] values) {
        List<String> valuesList = new ArrayList<>();
        Collections.addAll(valuesList, values);
        requestQuery(queryCode, where, valuesList);
    }

    public void requestQuery(final int queryCode, String[] where, List values) {
        BmobQuery<RequestRecord> query = new BmobQuery<>();
        for (int i = 0; i < where.length; i++) {
            query.addWhereEqualTo(where[i], values.get(i));
        }
        query.order("-updatedAt");
        query.findObjects(mContext, new FindListener<RequestRecord>() {
            @Override
            public void onSuccess(List<RequestRecord> list) {
                mOnQueryFinishListener.onQuerySuccess(list, queryCode);
            }

            @Override
            public void onError(int i, String s) {
                mOnQueryFinishListener.onQueryError(i, s, queryCode);
            }
        });
    }

    public interface OnQueryFinishListener {
        void onQuerySuccess(List result, int queryCode);

        void onQueryError(int errorCode, String describe, int queryCode);
    }

    public void setOnQueryFinishListener(OnQueryFinishListener listener) {
        mOnQueryFinishListener = listener;
    }

}
