package com.laughing8.attendancecheckin.view.fragment.statistical;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.bmobobject.RecordByMouth;
import com.laughing8.attendancecheckin.utils.bmobquery.DataQuery;
import com.laughing8.attendancecheckin.utils.network.DataShare;
import com.laughing8.attendancecheckin.view.fragment.SecondFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class LWEFragment extends SecondFragment implements DataQuery.OnQueryFinishListener, View.OnClickListener {

    private ListView mListView;
    private DataQuery mQuery;
    private boolean doRefresh = true;
    private Map<String, LateRecord> mRecordMap;
    private List<LateRecord> mLateRecord;

    private LayoutInflater mInflater;

    private ProgressBar mProgressBar;

    private MyAdapter mAdapter;

    private DataShare mDataShare;

    private final int queryRecord = 1;

    private ImageView sortByTimes;
    private ImageView sortByTimeCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecordMap = new Hashtable<>();
        mLateRecord = new ArrayList<>();
        mQuery = new DataQuery(mActivity, this);
        mDataShare = new DataShare(mActivity);
        mAdapter = new MyAdapter();
        mInflater = mActivity.getLayoutInflater();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lwe, null);
        mListView = (ListView) view.findViewById(R.id.lweListView);
        mListView.setAdapter(mAdapter);
        mProgressBar = (ProgressBar) view.findViewById(R.id.lweProgressBar);
        sortByTimes = (ImageView) view.findViewById(R.id.sort_by_times);
        sortByTimeCount = (ImageView) view.findViewById(R.id.sort_by_timeCount);
        sortByTimes.setOnClickListener(this);
        sortByTimeCount.setOnClickListener(this);
        view.findViewById(R.id.lweTimesTv).setOnClickListener(this);
        view.findViewById(R.id.lweCountTv).setOnClickListener(this);
        mAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (doRefresh) {
            doRefresh = false;
            refresh();
        }
    }

    private void refresh() {
        Calendar c = Calendar.getInstance();
        refresh(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
    }

    private void refresh(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        String monthString = DateFormat.format("yyyy_MM", c).toString();
        List<String> select = new ArrayList<>();
        select.add("month");
        select.add("type");
        List<Object> value = new ArrayList<>();
        value.add(monthString);
        value.add(3);
        mQuery.recordQuery(queryRecord, select, value);
    }

    @Override
    public void onQuerySuccess(List result, int type) {
        if (type == queryRecord) {
            if (result != null && result.size() > 0) {
                initData(result);
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(mActivity, "无数据", Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onQueryError(int errorCode, String describe, int queryCode) {

    }

    private void initData(List result) {
        int owSecond = mDataShare.getOWSecond();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<RecordByMouth> records = result;
        for (RecordByMouth record : records) {
            LateRecord lateRecord = mRecordMap.get(record.getUserName());
            if (lateRecord == null) {
                lateRecord = new LateRecord();
                lateRecord.userName = record.getUserName();
                lateRecord.name = record.getName();
            }
            lateRecord.lateTimes += 1;
            String date = record.getDate().getDate();
            try {
                int hour = Integer.parseInt(date.substring(11, 13));
                int minute = Integer.parseInt(date.substring(14, 16));
                int second = Integer.parseInt(date.substring(17, 19));
                int time = hour * 3600 + minute * 60 + second;
                lateRecord.lateTimeCount += owSecond - time;
            } catch (Exception e) {
                e.printStackTrace();
            }
            mRecordMap.put(record.getUserName(), lateRecord);
        }
        for (LateRecord lateRecord : mRecordMap.values()) {
            mLateRecord.add(lateRecord);
        }
        sortByTimes();
    }

    private boolean timesSorted;
    private boolean timesSortDecrease;

    private void sortByTimes() {
        if (!timesSorted) {
            Collections.sort(mLateRecord, new Comparator<LateRecord>() {
                @Override
                public int compare(LateRecord lhs, LateRecord rhs) {
                    if (lhs.lateTimes > rhs.lateTimes) {
                        return -1;
                    } else if (lhs.lateTimes < rhs.lateTimes) {
                        return 1;
                    }
                    return 0;
                }
            });
            timesSorted = true;
            timesSortDecrease = true;
        } else {
            Collections.reverse(mLateRecord);
            timesSortDecrease = !timesSortDecrease;
        }
        if (timesSortDecrease) sortByTimes.setImageResource(R.drawable.arrow_down);
        else sortByTimes.setImageResource(R.drawable.arrow_up);
        sortByTimeCount.setImageResource(R.drawable.arrow);
        mAdapter.notifyDataSetChanged();
    }

    private boolean timeCountSorted;
    private boolean timeCountDecrease;

    private void sortByTimeCount() {
        if (!timeCountSorted) {
            Collections.sort(mLateRecord, new Comparator<LateRecord>() {
                @Override
                public int compare(LateRecord lhs, LateRecord rhs) {
                    if (lhs.lateTimeCount > rhs.lateTimeCount) {
                        return -1;
                    } else if (lhs.lateTimeCount < rhs.lateTimeCount) {
                        return 1;
                    }
                    return 0;
                }
            });
            timeCountSorted = true;
            timeCountDecrease = true;
        } else {
            Collections.reverse(mLateRecord);
            timeCountDecrease = !timeCountDecrease;
        }
        if (timeCountDecrease) sortByTimeCount.setImageResource(R.drawable.arrow_down);
        else sortByTimeCount.setImageResource(R.drawable.arrow_up);
        sortByTimes.setImageResource(R.drawable.arrow);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sort_by_times:
            case R.id.lweTimesTv:
                sortByTimes();
                break;
            case R.id.sort_by_timeCount:
            case R.id.lweCountTv:
                sortByTimeCount();
                break;
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLateRecord.size();
        }

        @Override
        public Object getItem(int position) {
            return mLateRecord.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LateRecord lateRecord = mLateRecord.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.contact_late_item, null);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.contact_list_icon);
                viewHolder.name = (TextView) convertView.findViewById(R.id.contact_list_name);
                viewHolder.position = (TextView) convertView.findViewById(R.id.contact_list_position);
                viewHolder.lateTimes = (TextView) convertView.findViewById(R.id.lateTimes);
                viewHolder.lateTimeCount = (TextView) convertView.findViewById(R.id.lateTimeCount);
                convertView.setTag(viewHolder);
            } else viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.name.setText(lateRecord.name);
            MUser user = mQuery.getUser(lateRecord.userName);
            if (user == null) {
                mQuery.contactQuery(0);
            }
            viewHolder.position.setText(user != null ? user.getPosition() : "未知");
            viewHolder.lateTimes.setText(lateRecord.lateTimes + "次");
            viewHolder.lateTimeCount.setText(lateRecord.lateTimeCount / 60 + "分钟");
            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView name;
            TextView position;
            TextView lateTimes;
            TextView lateTimeCount;
        }

    }

    class LateRecord {
        String userName;
        String name;
        int lateTimes;
        long lateTimeCount;
    }
}
