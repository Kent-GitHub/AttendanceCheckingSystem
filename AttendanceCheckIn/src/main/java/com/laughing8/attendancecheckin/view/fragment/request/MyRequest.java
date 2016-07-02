package com.laughing8.attendancecheckin.view.fragment.request;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.bmobobject.RequestRecord;
import com.laughing8.attendancecheckin.utils.bmobquery.DataQuery;
import com.laughing8.attendancecheckin.view.fragment.SecondFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Laughing8 on 2016/5/21.
 */
public class MyRequest extends SecondFragment implements ViewPager.OnPageChangeListener, DataQuery.OnQueryFinishListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private View indicatorView;
    private List<ListView> mListViews;
    private List<MyBaseAdapter> mAdapters;
    private MyPagerAdapter mPagerAdapter;
    private boolean doRefresh = true;
    private int indicatorWidth;
    private DataQuery mQuery;
    private ViewPager mViewPager;
    private ProgressBar progressBar;
    private boolean[] updateSucceed;

    private Map<Integer, List<RequestRecord>> mData;

    private List<RequestData> undeterminedData;
    private List<RequestData> approvedData;
    private List<RequestData> rejectedData;

    private final int undeterminedQuery = 1;
    private final int approvedQuery = 2;
    private final int rejectedQuery = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQuery = new DataQuery(mActivity, this);

        mData = new HashMap<>();
        updateSucceed = new boolean[3];
        undeterminedData = new ArrayList<>();
        approvedData = new ArrayList<>();
        rejectedData = new ArrayList<>();

        mAdapters = new ArrayList<>();
        mAdapters.add(new MyBaseAdapter(undeterminedData));
        mAdapters.add(new MyBaseAdapter(approvedData));
        mAdapters.add(new MyBaseAdapter(rejectedData));

        mListViews = new ArrayList<>();
        for (MyBaseAdapter adapter : mAdapters) {
            ListView listView = new ListView(mActivity);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            mListViews.add(listView);
        }
        mPagerAdapter = new MyPagerAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_request, null);
        mViewPager = (ViewPager) view.findViewById(R.id.myRequest_viewPager);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mPagerAdapter);
        view.findViewById(R.id.requestText0).setOnClickListener(this);
        view.findViewById(R.id.requestText1).setOnClickListener(this);
        view.findViewById(R.id.requestText2).setOnClickListener(this);
        progressBar = (ProgressBar) view.findViewById(R.id.request_progressBar);
        initLayout(view);
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
        String userName = mUser.getUsername();
        List<Object> values1 = new ArrayList<>();
        values1.add(userName);
        values1.add(1);
        mQuery.requestQuery(undeterminedQuery, new String[]{"username", "status"}, values1);
        List<Object> values2 = new ArrayList<>();
        values2.add(userName);
        values2.add(2);
        mQuery.requestQuery(approvedQuery, new String[]{"username", "status"}, values2);
        List<Object> values3 = new ArrayList<>();
        values3.add(userName);
        values3.add(3);
        mQuery.requestQuery(rejectedQuery, new String[]{"username", "status"}, values3);
        progressBar.setVisibility(View.VISIBLE);
        updateSucceed[0] = updateSucceed[1] = updateSucceed[2] = false;
    }

    private void initLayout(View view) {
        //indicatorContainer
        View container = view.findViewById(R.id.myIndicatorContainer);
        ViewGroup.LayoutParams containerLp = container.getLayoutParams();
        containerLp.height = (int) (mApplication.getScreenHeight() * 0.06);
        container.setLayoutParams(containerLp);
        //indicator
        indicatorView = view.findViewById(R.id.myIndicator);
        ViewGroup.LayoutParams indicatorLp = indicatorView.getLayoutParams();
        indicatorWidth = mApplication.getScreenWidth() / 3;
        indicatorLp.width = indicatorWidth;
        indicatorView.setLayoutParams(indicatorLp);
    }

    //addOnPageChangeListener
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        float x = (position + positionOffset) * indicatorWidth;
        indicatorView.setX(x);
    }

    //addOnPageChangeListener
    @Override
    public void onPageSelected(int position) {

    }

    //addOnPageChangeListener
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onQuerySuccess(List result, int queryCode) {
        if (queryCode == undeterminedQuery) {
            undeterminedData = initData(result);
            mData.put(0, result);
            updateSucceed[0] = true;
            mAdapters.get(0).notifyDataSetChanged(undeterminedData);
        } else if (queryCode == approvedQuery) {
            approvedData = initData(result);
            mData.put(1, result);
            updateSucceed[1] = true;
            mAdapters.get(1).notifyDataSetChanged(approvedData);
        } else if (queryCode == rejectedQuery) {
            rejectedData = initData(result);
            mData.put(2, result);
            updateSucceed[2] = true;
            mAdapters.get(2).notifyDataSetChanged(rejectedData);
        }
        if (updateSucceed[0] && updateSucceed[1] && updateSucceed[2]) {
            progressBar.setVisibility(View.GONE);
            if (mData.get(0).size() > 0) mViewPager.setCurrentItem(0);
            else if (mData.get(1).size() > 0) mViewPager.setCurrentItem(1);
            else if (mData.get(2).size() > 0) mViewPager.setCurrentItem(2);
        }
    }

    @Override
    public void onQueryError(int errorCode, String describe, int queryCode) {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<RequestData> initData(List<RequestRecord> listData) {
        List<RequestData> datas = new ArrayList<>();
        for (RequestRecord record : listData) {
            RequestData data = new RequestData();
            int requestType = record.getRequestType();
            int requestStatus = record.getStatus();
            data.textTop = "";
            if (requestType == 0) {
                data.id = R.drawable.ic_leave_small;
                data.textTop = "请假：";
            } else if (requestType == 1) {
                data.id = R.drawable.ic_go_out_small;
                data.textTop = "外勤：";
            } else if (requestType == 2) {
                data.id = R.drawable.ic_travel_small;
                data.textTop = "出差：";
            } else if (requestType == 3) {
                data.id = R.drawable.ic_overtime_small;
                data.textTop = "加班：";
            }
            List<String> selectedDays = record.getDates();
            if (selectedDays.size() == 1) {
                data.textTop += selectedDays.get(0) + "（当天）";
            } else {
                data.textTop += selectedDays.get(0) + " 等（" + selectedDays.size() + "天）";
            }
//            String start_ = record.getStart().getDate();
//            String end_ = record.getEnd().getDate();
//            Calendar start = Calendar.getInstance();
//            Calendar end = Calendar.getInstance();
//            try {
//                start.setTime(sdf.parse(start_));
//                end.setTime(sdf.parse(end_));
//                create.setTime(sdf.parse(record.getCreatedAt()));
//                int startDay = start.get(Calendar.DAY_OF_YEAR);
//                int endDay = end.get(Calendar.DAY_OF_YEAR);
//                if (startDay == endDay) {
//                    data.textTop += start_.substring(5, 10) + "（当天）";
//                } else {
//                    data.textTop += start_.substring(5, 10) + " ~ " + end_.substring(5, 10)
//                            + "（" + (endDay - startDay + 1) + "天）";
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            Calendar create = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            try {
                create.setTime(sdf.parse(record.getCreatedAt()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (requestStatus == 1) data.textBottom = "待审核";
            else if (requestStatus == 2) data.textBottom = "申请已通过";
            else if (requestStatus == 3) data.textBottom = "申请已驳回";
            long interval = (now.getTimeInMillis() - create.getTimeInMillis()) / 1000;
            if (interval < 60) data.textRight = "刚刚";
            else if (interval < 3600) data.textRight = interval / 60 + "分钟前";
            else if (interval < 24 * 3600) data.textRight = interval / 3600 + "小时前";
            else data.textRight = now.get(Calendar.DAY_OF_YEAR) -
                        create.get(Calendar.DAY_OF_YEAR) + "天前";
            datas.add(data);
        }
        return datas;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        for (int i = 0; i < mListViews.size(); i++) {
            if (mListViews.get(i) == view.getParent()) {
                RequestRecord request = mData.get(i).get(position);
                bundle.putSerializable("requestRecord", request);
                bundle.putBoolean("fromMyRequest", true);
                RequestDetail fragment = new RequestDetail();
                fragment.setArguments(bundle);
                fragment.setTargetFragment(this,1);
                mActivity.jumpToFragment(fragment, true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.requestText0:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.requestText1:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.requestText2:
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    public void setDoRefresh(boolean doRefresh) {
        this.doRefresh = doRefresh;
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position));
            return mListViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

    }

    private class MyBaseAdapter extends BaseAdapter {
        private List<RequestData> mList;

        public MyBaseAdapter(List<RequestData> list) {
            mList = list;
        }

        public void notifyDataSetChanged(List<RequestData> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RequestData data = mList.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mActivity.getLayoutInflater().inflate(R.layout.my_request_list, null);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.request_icon);
                viewHolder.textTop = (TextView) convertView.findViewById(R.id.request_textTop);
                viewHolder.textBottom = (TextView) convertView.findViewById(R.id.request_textBottom);
                viewHolder.textRight = (TextView) convertView.findViewById(R.id.request_textRight);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.icon.setImageResource(data.id);
            viewHolder.textTop.setText(data.textTop);
            viewHolder.textBottom.setText(data.textBottom);
            viewHolder.textRight.setText(data.textRight);
            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView textTop;
            TextView textBottom;
            TextView textRight;
        }
    }

    class RequestData {
        int id;
        String textTop;
        String textBottom;
        String textRight;
    }

}
