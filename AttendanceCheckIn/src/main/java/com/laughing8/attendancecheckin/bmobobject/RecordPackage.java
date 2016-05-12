package com.laughing8.attendancecheckin.bmobobject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Laughing8 on 2016/4/23.
 */
public class RecordPackage {
    private List<RecordByMouth> mList;
    private List<RecordByMouth>[] mDatas;
    private int year;
    private int month;

    public RecordPackage(List<RecordByMouth> list, int year, int month) {
        mList = list;
        this.year=year;
        this.month=month;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        mDatas = new ArrayList[day];
        for (int i = 0; i < day; i++) {
            mDatas[i] = new ArrayList<>();
        }
        initDatas();
    }

    private void initDatas() {
        if (mList == null) {
            return;
        }
        for (RecordByMouth record : mList) {
            int day = Integer.parseInt(record.getDate().getDate().substring(8, 10));
            mDatas[day-1].add(record);
        }
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public List<RecordByMouth>[] getDatas() {
        return mDatas;
    }

    public List<RecordByMouth> getDatas(int day){
        if (day>mDatas.length){
            return null;
        }
        return mDatas[day];
    }
}
