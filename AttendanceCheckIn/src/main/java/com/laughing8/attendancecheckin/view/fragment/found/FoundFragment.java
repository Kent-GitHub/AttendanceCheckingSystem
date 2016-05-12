package com.laughing8.attendancecheckin.view.fragment.found;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.bmobobject.RecordByMouth;
import com.laughing8.attendancecheckin.bmobobject.RecordPackage;
import com.laughing8.attendancecheckin.bmobobject.RecordType;
import com.laughing8.attendancecheckin.constants.Actions;
import com.laughing8.attendancecheckin.utils.coverview.BetterCoverViewTool;
import com.laughing8.attendancecheckin.view.activity.MainActivity;
import com.laughing8.attendancecheckin.view.activity.SecondActivity;
import com.laughing8.attendancecheckin.view.custom.StrokeTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Laughing8 on 2016/4/9.
 */
public class FoundFragment extends Fragment {

    private MainActivity mActivity;
    private View mView;
    private boolean doRefresh = true;
    private StrokeTextView[] mTvs;
    private TextView mMonth;
    private RecordType recordType;
    private FrameLayout mContainer;
    private RecordPackage recordPackage;
    private List<RecordByMouth>[] mDatas;
    private BetterCoverViewTool mCoverView;
    private String mMonthString;
    //工作、外勤、出差、加班、请假、迟到、早退
    private int[] workStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FoundFragment","onCreate");
        mActivity = (MainActivity) getActivity();
        mTvs = new StrokeTextView[7];
        workStatus = new int[7];
        mCoverView = new BetterCoverViewTool(mActivity);
    }

    private void refresh() {
        Log.d("FoundFragment","refresh");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        mMonthString = new SimpleDateFormat("yyyy年MM月概况").format(date);
        mMonth.setText(mMonthString);
        updateRecordType(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("FoundFragment","onCreateView");
        mView = inflater.inflate(R.layout.fragment_found, null);
        mContainer= (FrameLayout) mView.findViewById(R.id.found_container);
        mView.findViewById(R.id.found_check_month).setOnClickListener(onAtyClick);
        mView.findViewById(R.id.found_statistics).setOnClickListener(onAtyClick);
        mMonth = (TextView) mView.findViewById(R.id.found_month);
        mTvs[0] = (StrokeTextView) mView.findViewById(R.id.found_workDay);
        mTvs[1] = (StrokeTextView) mView.findViewById(R.id.found_goOut);
        mTvs[2] = (StrokeTextView) mView.findViewById(R.id.found_travel);
        mTvs[3] = (StrokeTextView) mView.findViewById(R.id.found_ot);
        mTvs[4] = (StrokeTextView) mView.findViewById(R.id.found_leave);
        mTvs[5] = (StrokeTextView) mView.findViewById(R.id.found_late);
        mTvs[6] = (StrokeTextView) mView.findViewById(R.id.found_lwe);
        if (getArguments() != null) {
            Bundle b = getArguments();
            if (b.getString("mMonthString") != null) {
                mMonthString = b.getString("mMonthString");
                mMonth.setText(mMonthString);
            }
            if (b.getIntArray("workStatus") != null) {
                workStatus = b.getIntArray("workStatus");
                updateWorkStatus();
            }
        }
        if (savedInstanceState!=null){
            if (savedInstanceState.getString("mMonthString") != null) {
                mMonthString = savedInstanceState.getString("mMonthString");
                mMonth.setText(mMonthString);
            }
            if (savedInstanceState.getIntArray("workStatus") != null) {
                workStatus = savedInstanceState.getIntArray("workStatus");
                updateWorkStatus();
            }
        }
        return mView;
    }

    private void createRecord() {
        RecordByMouth record = new RecordByMouth();
        record.setUserName("1212470123");
        record.setDate(new BmobDate(new Date()));
        record.setLocation(new BmobGeoPoint());
        record.setImei("666666666666666666666");
        record.setMac("12:34:56:78:90:SS");
        record.setName("张大伟");
        record.setType(0);
        record.save(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getArguments()!=null){
            Bundle b=getArguments();
            b.putString("mMonthString",mMonthString);
            b.putIntArray("workStatus",workStatus);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("mMonthString", mMonthString);
        outState.putIntArray("workStatus", workStatus);
    }

    @Override
    public void onResume() {
        Log.d("FoundFragment","onResume");
        super.onResume();
        if (doRefresh) {
            doRefresh = false;
            mCoverView.setBackgroundColor(Color.parseColor("#80c0c0c0"));
            mCoverView.fill(mContainer);
            refresh();
        }
    }

    private void updateRecordType(final int year, final int month) {
        //获取RecordType
        BmobQuery<RecordType> queryType = new BmobQuery<>();
        queryType.addWhereEqualTo("version", 1);
        queryType.findObjects(getActivity(), new FindListener<RecordType>() {
            @Override
            public void onSuccess(List<RecordType> list) {
                recordType = list.get(0);
                updateRecord(year, month);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void updateRecord(final int year, final int month) {
        //获取指定月份签到记录
        BmobQuery<RecordByMouth> queryRecord = new BmobQuery<>();
        queryRecord.addWhereEqualTo("month", RecordByMouth.getStaticMonth(year, month));
        queryRecord.addWhereEqualTo("userName", mActivity.getUser().getUsername());
        queryRecord.findObjects(getActivity(), new FindListener<RecordByMouth>() {
            @Override
            public void onSuccess(List<RecordByMouth> list) {
                if (list.size() == 0) {
                    mCoverView.showFinish();
                    return;
                }
                upDateCount(list, year, month);
                mCoverView.showFinish();
            }

            @Override
            public void onError(int i, String s) {
                mCoverView.showNetWork();
                Log.e("FoundFragment", "refresh " + "ErrorCode:" + i + ", message:" + s);
            }
        });
    }

    private void upDateCount(List<RecordByMouth> list, int year, int month) {
        int dayCount[] = new int[31];
        int outCount[] = new int[31];
        int travelCount[] = new int[31];
        recordPackage = new RecordPackage(list, year, month);
        mDatas = recordPackage.getDatas();
        for (int i = 0; i < mDatas.length; i++) {
            List<RecordByMouth> recordList = mDatas[i];
            if (recordList.size() == 0) {
                continue;
            }
            for (RecordByMouth record : recordList) {
                int type = record.getType();
                if (type == 0 || type == 1) {
                    dayCount[i] = 1;
                } else if (type == 2) {
                    dayCount[i] = 1;
                    workStatus[5]++;
                } else if (type == 3) {
                    workStatus[6]++;
                } else if (type == 4 || type == 5) {
                    dayCount[i] = 1;
                } else if (type == 6) {
                    dayCount[i] = 1;
                    outCount[i] = 1;
                } else if (type == 7) {
                    dayCount[i] = 1;
                    travelCount[i] = 1;
                }
            }
        }
        for (int i : dayCount) {
            workStatus[0] += i;
        }
        for (int i : outCount) {
            workStatus[1] += i;
        }
        for (int i : travelCount) {
            workStatus[2] += i;
        }
        updateWorkStatus();
    }

    private void updateWorkStatus() {
        if (workStatus[0] != 0) mTvs[0].setText(workStatus[0] + "天");
        if (workStatus[1] != 0) mTvs[1].setText(workStatus[1] + "天");
        if (workStatus[2] != 0) mTvs[2].setText(workStatus[2] + "天");
        if (workStatus[3] != 0) mTvs[3].setText(workStatus[3] + "次");
        if (workStatus[4] != 0) mTvs[4].setText(workStatus[4] + "天");
        if (workStatus[5] != 0) mTvs[5].setText(workStatus[5] + "次");
        if (workStatus[6] != 0) mTvs[6].setText(workStatus[6] + "次");
    }

    private void testAddRecord() {
        MUser user = mActivity.getUser();
        Calendar calendar = Calendar.getInstance();
        int dayCount = calendar.getActualMaximum(Calendar.DATE);
        for (int i = 0; i < dayCount; i++) {
            calendar.set(Calendar.MONTH, 3);
            calendar.set(Calendar.DATE, i);
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            int minute = (int) (Math.random() * 20);
            calendar.set(Calendar.MINUTE, minute);
            int second = (int) (Math.random() * 60);
            calendar.set(Calendar.SECOND, second);
            Date date = calendar.getTime();
            RecordByMouth record = new RecordByMouth();
            record.setUserName(user.getUsername());
            record.setName(user.getName());
            record.setDate(new BmobDate(date));
            record.setLocation(new BmobGeoPoint());
            record.setImei("888888888888888888");
            record.setMac("12:34:56:78:90:SS");
            record.setType(1);
            record.setMonth(new SimpleDateFormat("yyyy_MM").format(date));
            record.save(mActivity);
        }
    }

    private String formatRecordResult(RecordByMouth record) {
        String name = record.getName();
        String dateString = record.getDate().getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateString.substring(0, 10);
        String time = dateString.substring(11, 19);
        try {
            Date now = new Date();
            Date newDate = sdf.parse(date);
            if (now.getYear() == newDate.getYear() && now.getMonth() == newDate.getMonth()) {
                if (now.getDay() == newDate.getDay()) {
                    date = "今天";
                } else if (now.getDay() - newDate.getDay() == 1) {
                    date = "昨天";
                } else if (now.getDay() - newDate.getDay() == 1) {
                    date = "前天";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return name + " 于 " + date + " " + time + " 打卡了。";
    }

    private View.OnClickListener onAtyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(mActivity, SecondActivity.class);
            switch (v.getId()) {
                case R.id.found_check_month:
                    i.setAction(Actions.FoundCheckMonth);
                    break;
                case R.id.found_statistics:
                    i.setAction(Actions.FoundStatistics);
                    break;
            }
            startActivity(i);
        }
    };

}
