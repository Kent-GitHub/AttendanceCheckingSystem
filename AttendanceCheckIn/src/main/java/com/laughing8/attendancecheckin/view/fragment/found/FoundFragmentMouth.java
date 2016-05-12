package com.laughing8.attendancecheckin.view.fragment.found;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.application.MyApplication;
import com.laughing8.attendancecheckin.bmobobject.RecordByMouth;
import com.laughing8.attendancecheckin.bmobobject.RecordPackage;
import com.laughing8.attendancecheckin.bmobobject.RecordType;
import com.laughing8.attendancecheckin.utils.coverview.BetterCoverViewTool;
import com.laughing8.attendancecheckin.utils.network.NetStatus;
import com.laughing8.attendancecheckin.view.activity.SecondActivity;
import com.laughing8.attendancecheckin.view.custom.StrokeTextView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.aigestudio.datepicker.bizs.calendars.DPCManager;
import cn.aigestudio.datepicker.bizs.decors.DPDecor;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class FoundFragmentMouth extends Fragment implements DatePicker.OnDatePickedListener {

    private SecondActivity mActivity;
    private MyApplication mApplication;
    private MaterialCalendarView mCalendar;
    private DatePicker mDatePicker;
    private View mContainer;
    private BetterCoverViewTool mCoverView;
    private RecordPackage recordPackage;
    private List<RecordByMouth>[] mDatas;
    private RecordType recordType;
    private TextView today;
    private ListView todayList;
    private boolean doRefresh = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (SecondActivity) getActivity();
        mApplication = (MyApplication) mActivity.getApplication();
        mCoverView = new BetterCoverViewTool(mActivity);
    }

    private void createRecordType() {
        recordType = new RecordType();
        recordType.setVersion(1);
        recordType.addTypeString("上班签到");
        recordType.addTypeString("下班签到");
        recordType.addTypeString("远程签到");
        recordType.addTypeString("早班签到");
        recordType.addTypeString("晚班签到");
        recordType.addTypeString("上班迟到");
        recordType.addTypeString("下班早退");
        recordType.save(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found_mouth, null);
        mContainer = view.findViewById(R.id.mouth_container);
        todayList = (ListView) view.findViewById(R.id.month_listView);
        mCalendar = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        mCalendar.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        mCalendar.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return day.getYear() + "年" + (day.getMonth() + 1) + "月";
            }
        });
        today = (TextView) view.findViewById(R.id.month_today);
        mDatePicker = (DatePicker) view.findViewById(R.id.found_datePicker);
        Calendar calendar = Calendar.getInstance();
        String todayString = new SimpleDateFormat("yyyy年MM月dd日").
                format(calendar.getTime()) + "   " + getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        today.setText(todayString);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        mDatePicker.setHolidayDisplay(false);
        mDatePicker.setTvEnsureVisible(false);
        mDatePicker.setMode(DPMode.SINGLE);
        mDatePicker.setDPDecor(dpDecor);
        mDatePicker.setOnDatePickedListener(this);
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
        DPCManager.getInstance().clearDecorBG();
        Bundle bundle = getArguments();
        if (bundle != null) {
            int year = bundle.getInt("year");
            int month = bundle.getInt("month");
            if (year != 0 && month != 0) {
                refresh(year, month);
                return;
            }
        }
        Calendar calendar = Calendar.getInstance();
        refresh(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }

    private void refresh(final int year, final int month) {
        if (NetStatus.netWorkAccess(getActivity())) {
            //mCoverView.cover(mContainer);
            //mCoverView.fillRootView();

            mCoverView.setBackgroundColor(Color.parseColor("#80c0c0c0"));
            mCoverView.showLoading();
            updateRecordType(year, month);
        } else {
            mCoverView.showNetWork();
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
        String userName;
        if (getArguments() != null && getArguments().getString("userName") != null) {
            userName = getArguments().getString("userName");
        } else {
            userName = mActivity.getUser().getUsername();
        }

        queryRecord.addWhereEqualTo("month", RecordByMouth.getStaticMonth(year, month));
        queryRecord.addWhereEqualTo("userName", userName);
        queryRecord.findObjects(getActivity(), new FindListener<RecordByMouth>() {
            @Override
            public void onSuccess(List<RecordByMouth> list) {
                if (list.size() == 0) {
                    Toast.makeText(mActivity, "当月数据为空", Toast.LENGTH_SHORT).show();
                    //mCoverView.showFinish()
                    //return;
                }
                recordPackage = new RecordPackage(list, year, month);
                mDatas = recordPackage.getDatas();
                updateCalendar();
                mAdapter = new MBaseAdapter();
                todayList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mCoverView.showFinish();
            }

            @Override
            public void onError(int i, String s) {
                mCoverView.showNetWork();
                Log.e("FoundFragmentMouth", "updateRecord " + "ErrorCode:" + i + ", message:" + s);
            }
        });
    }

    private void updateCalendar() {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < mDatas.length; i++) {
            if (mDatas[i].size() > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(recordPackage.getYear(), recordPackage.getMonth() - 1, i + 1);
                mCalendar.setDateSelected(calendar, true);
                Date date = calendar.getTime();
                String dateString = sdf.format(date);
                dates.add(dateString.replace("-0", "-"));
            }
        }
        DPCManager.getInstance().setDecorBG(dates);
        mDatePicker.updateDPCManager();
    }

    private DPDecor dpDecor = new DPDecor() {
        @Override
        public void drawDecorBG(Canvas canvas, Rect rect, Paint paint) {
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;

            Rect bgRect = new Rect((int) (rect.left + width * 0.2), (int) (rect.top + height * 0.2),
                    (int) (rect.right - width * 0.2), (int) (rect.bottom - height * 0.2));
            paint.setDither(true);
            paint.setAntiAlias(true);
            //canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2F, paint);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.single_selected);
            canvas.drawBitmap(bitmap, null, bgRect, paint);
        }
    };

    @Override
    public void onDatePicked(String dateString) {
        int index = dateString.indexOf("-");
        int lastIndex = dateString.lastIndexOf("-");
        int year = Integer.parseInt(dateString.substring(0, index));
        int month = Integer.parseInt(dateString.substring(index + 1, lastIndex));
        selectedDay = Integer.parseInt(dateString.substring(lastIndex + 1, dateString.length()));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, selectedDay);
        dateString = new SimpleDateFormat("yyyy年MM月dd日").
                format(calendar.getTime()) + getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        today.setText(dateString);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DPCManager.getInstance().clearDecorBG();
    }

    private String getDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "星期天";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
        return "星期七";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private int selectedDay;

    private BaseAdapter mAdapter;

    class MBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mDatas[selectedDay - 1].size() == 0) {
                return 1;
            }
            return mDatas[selectedDay - 1].size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas[selectedDay - 1].get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StrokeTextView view = new StrokeTextView(mActivity);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            if (mDatas[selectedDay - 1].size() == 0) {
                view.setText("当天没有记录。");
                return view;
            }
            RecordByMouth record = mDatas[selectedDay - 1].get(position);
            String date = record.getDate().getDate();
            String time = date.substring(11, date.length());
            String describe = recordType.getTypeString(record.getType());
            String text = "第" + (position + 1) + "次签到:" + " 于 " + time + " 进行了 " + describe + "。";
            view.setText(text);
            return view;
        }
    }
}
