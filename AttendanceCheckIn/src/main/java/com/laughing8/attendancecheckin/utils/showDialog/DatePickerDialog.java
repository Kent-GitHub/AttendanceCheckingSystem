package com.laughing8.attendancecheckin.utils.showDialog;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.utils.network.DataShare;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import cn.aigestudio.datepicker.views.MonthView;

/**
 * Created by Laughing8 on 2016/5/22.
 */
public class DatePickerDialog extends DialogFragment implements View.OnClickListener, MonthView.OnPickedDateChangedListener, TimePickerDialog.OnTimeSetListener, DatePicker.OnDatePickedListener {

    private DatePicker mDatePicker;
    private TextView startTimeTv, endTimeTv, timeDuration;
    private View startTimeContainer, endTimeContainer;
    private int startTime;
    private int endTime;
    private DataShare mDataShare;
    private List<String> selectedDays;

    /**
     * version:1
     * type:1 String:请假申请
     * type:2 String:外勤申请
     * type:3 String:出差申请
     * type:4 String:加班申请
     */
    private int requestType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataShare = new DataShare(getActivity());
        selectedDays = new ArrayList<>();
        startTime = mDataShare.getSWSecond();
        endTime = mDataShare.getOWSecond();
        if (getTargetFragment() != null) {
            requestType = getTargetRequestCode();
        }
//        if (getArguments() != null) {
//            Bundle arguments = getArguments();
//            if (arguments.getStringArrayList("selectedDays") != null) {
//                selectedDays = arguments.getStringArrayList("selectedDays");
//            } else {
//                selectedDays = new ArrayList<>();
//            }
//            if (arguments.getInt("startTime") != -1) {
//                startTime = arguments.getInt("startTime");
//            }
//            if (arguments.getInt("endTime") != -1) {
//                endTime = arguments.getInt("endTime");
//            }
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_datepicker_dialog, null);
        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_datePicker);
        mDatePicker.getMonthView().setOnPickedDateChangedListener(this);
        mDatePicker.getMonthView().setScrollEnable(true);
        Calendar c = Calendar.getInstance();
        mDatePicker.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
        mDatePicker.setTvEnsureVisible(false);
        view.findViewById(R.id.startTime).setOnClickListener(onTimePickClick);
        view.findViewById(R.id.endTime).setOnClickListener(onTimePickClick);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(this);
        startTimeTv = (TextView) view.findViewById(R.id.startTimeTv);
        endTimeTv = (TextView) view.findViewById(R.id.endTimeTv);
        timeDuration = (TextView) view.findViewById(R.id.timeDuration);
        startTimeContainer = view.findViewById(R.id.startTimeContainer);
        endTimeContainer = view.findViewById(R.id.endTimeContainer);
//        if (selectedDays.size() > 0) {
//            mDatePicker.getMonthView().setDateSelected(selectedDays);
//        }
        if (requestType == 2 || requestType == 3) {
            startTimeContainer.setVisibility(View.GONE);
            endTimeContainer.setVisibility(View.GONE);
        } else if (requestType == 4) {
            startTime = mDataShare.getOWSecond();
            endTime = startTime + 4 * 3600;
            mDatePicker.setMode(DPMode.SINGLE);
            mDatePicker.setOnDatePickedListener(this);
            computeOverTime();
        }
        return view;
    }

    private final int requestTimeStart = 1;
    private final int requestTimeEnd = 2;
    private int timeRequest;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                this.dismiss();
                break;
            case R.id.dialog_confirm:
                setResult();
                break;
        }
    }

    private View.OnClickListener onTimePickClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            android.app.TimePickerDialog dialog;
            if (v.getId() == R.id.startTime) {
                timeRequest = requestTimeStart;
                dialog = new TimePickerDialog(getActivity(),
                        DatePickerDialog.this, startTime / 3600, (startTime % 3600) / 60, true);
            } else {
                timeRequest = requestTimeEnd;
                dialog = new TimePickerDialog(getActivity(),
                        DatePickerDialog.this, endTime / 3600, (endTime % 3600) / 60, true);
            }
            dialog.show();
        }
    };

    @Override
    public void onPickedDateChanged(List<String> selectedList) {
        if (selectedList.size() == 0) {
            if (requestType==1){
                startTimeContainer.setVisibility(View.VISIBLE);
                endTimeContainer.setVisibility(View.VISIBLE);
            }
            timeDuration.setText("无");
        } else if (selectedList.size() == 1 && requestType == 1) {
            startTimeContainer.setVisibility(View.VISIBLE);
            endTimeContainer.setVisibility(View.VISIBLE);
            computeTime();
        } else {
            startTimeContainer.setVisibility(View.GONE);
            endTimeContainer.setVisibility(View.GONE);
            timeDuration.setText(selectedList.size() + "天");
        }
        selectedDays = selectedList;
    }

    @Override
    public void onDatePicked(String date) {
        selectedDays.clear();
        selectedDays.add(date);
        computeOverTime();
    }

    private void computeOverTime() {
        int hour = (endTime - startTime) / 3600;
        int mMinute = ((endTime - startTime) % 3600) / 60;
        String duration = (hour == 0 ? "" : hour + "小时") + (mMinute == 0 ? "" : mMinute + "分钟");
        timeDuration.setText(duration);
        startTimeTv.setText(formatTime(startTime / 3600, (startTime % 3600) / 60));
        endTimeTv.setText(formatTime(endTime / 3600, (endTime % 3600) / 60));
    }

    private void computeOverTime(int hourOfDay, int minute) {
        int tempStart, tempEnd;
        if (timeRequest == requestTimeStart) {
            tempStart = hourOfDay * 3600 + minute * 60;
            if (tempStart < mDataShare.getOWSecond()) startTime = mDataShare.getOWSecond();
            else startTime = tempStart;
        } else {
            tempEnd = hourOfDay * 3600 + minute * 60;
            if (tempEnd < mDataShare.getOWSecond()) endTime = mDataShare.getOWSecond();
            else endTime = tempEnd;
        }
        int hour = (endTime - startTime) / 3600;
        int mMinute = ((endTime - startTime) % 3600) / 60;
        String duration = (hour == 0 ? "" : hour + "小时") + (mMinute == 0 ? "" : mMinute + "分钟");
        timeDuration.setText(duration);
        startTimeTv.setText(formatTime(startTime / 3600, (startTime % 3600) / 60));
        endTimeTv.setText(formatTime(endTime / 3600, (endTime % 3600) / 60));
    }

    private void computeTime() {
        computeTime(startTime, endTime);
    }

    private void computeTime(int tempStartTime, int tempEndTime) {
        int sws, ows, bns, ans;
        sws = mDataShare.getSWSecond();
        ows = mDataShare.getOWSecond();
        bns = mDataShare.getBNSecond();
        ans = mDataShare.getANSecond();
        startTime = tempStartTime;
        endTime = tempEndTime;
        if (startTime < sws) {
            startTime = sws;
        } else if (startTime > bns && startTime < ans) {
            startTime = ans;
        }
        if (endTime > ows) {
            endTime = ows;
        } else if (endTime > bns && endTime < ans) {
            endTime = bns;
        }
        int timeInterval;
        if (startTime < bns && endTime > ans) {
            timeInterval = endTime - startTime - 3600;
        } else {
            timeInterval = endTime - startTime;
        }
        int hour = timeInterval / 3600;
        int minute = (timeInterval % 3600) / 60;
        String duration = (hour == 0 ? "" : hour + "小时") + (minute == 0 ? "" : minute + "分钟");
        timeDuration.setText(duration);
        startTimeTv.setText(formatTime(startTime / 3600, (startTime % 3600) / 60));
        endTimeTv.setText(formatTime(endTime / 3600, (endTime % 3600) / 60));
    }

    private String formatTime(int hour, int minute) {
        String result = "";
        if (hour < 10) result += "0" + hour;
        else result += hour;
        result += ":";
        if (minute < 10) result += "0" + minute;
        else result += minute;
        return result;
    }

    protected void setResult() {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("selectedDays", (ArrayList<String>) selectedDays);
        bundle.putInt("startTime", startTime);
        bundle.putInt("endTime", endTime);
        intent.putExtra("bundle", bundle);
//        intent.putExtra("selectedDays", selectedDays.toArray(new String[selectedDays.size()]));
//        intent.putExtra("startTime", startTime);
//        intent.putExtra("endTime", endTime);
        getTargetFragment().onActivityResult(requestType, requestType, intent);
        this.dismiss();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (requestType == 4) {
            computeOverTime(hourOfDay, minute);
            return;
        }
        int tempStartTime = startTime, tempEndTime = endTime;
        if (timeRequest == requestTimeStart) {
            int second = hourOfDay * 3600 + minute * 60;
            if (second > endTime) {
                Toast.makeText(getActivity(), "时间选择错误", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tempStartTime = hourOfDay * 3600 + minute * 60;
            }
        } else if (timeRequest == requestTimeEnd) {
            int second = hourOfDay * 3600 + minute * 60;
            if (second < startTime) {
                Toast.makeText(getActivity(), "时间选择错误", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tempEndTime = hourOfDay * 3600 + minute * 60;
            }
        }
        if (tempStartTime > mDataShare.getBNSecond() && tempStartTime < mDataShare.getANSecond()
                && tempEndTime > mDataShare.getBNSecond() && tempEndTime < mDataShare.getANSecond()) {
            Toast.makeText(getActivity(), "时间选择错误", Toast.LENGTH_SHORT).show();
        } else {
            computeTime(tempStartTime, tempEndTime);
        }
    }

}
