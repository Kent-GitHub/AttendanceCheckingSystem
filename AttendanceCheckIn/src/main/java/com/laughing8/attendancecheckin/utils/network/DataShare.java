package com.laughing8.attendancecheckin.utils.network;

import android.content.Context;

public class DataShare {
    private Context mContext;

    /**
     * 上午上班时间
     */
    private int[] swTime = new int[]{9, 30};
    /**
     * 下午下班时间
     */
    private int[] owTime = new int[]{18, 30};

    /**
     * 中午下班时间
     */
    private int[] beforeNoon = new int[]{12, 0};
    /**
     * 中午上班时间
     */
    private int[] afterNoon = new int[]{13, 0};

    public DataShare(Context context) {
        mContext = context;
    }

    public int[] getSwTime() {
        return swTime;
    }

    public int getSWSecond() {
        return swTime[0] * 3600 + swTime[1] * 60;
    }

    public int getOWSecond() {
        return owTime[0] * 3600 + owTime[1] * 60;
    }

    public void setSwTime(int[] swTime) {
        this.swTime = swTime;
    }

    public int[] getOwTime() {
        return owTime;
    }

    public void setOwTime(int[] owTime) {
        this.owTime = owTime;
    }

    public int[] getBeforeNoon() {
        return beforeNoon;
    }

    public int getBNSecond() {
        return beforeNoon[0] * 3600 + beforeNoon[1] * 60;
    }

    public void setBeforeNoon(int[] beforeNoon) {
        this.beforeNoon = beforeNoon;
    }


    public int[] getAfterNoon() {
        return afterNoon;
    }

    public int getANSecond() {
        return afterNoon[0] * 3600 + afterNoon[1] * 60;
    }

    public void setAfterNoon(int[] afterNoon) {
        this.afterNoon = afterNoon;
    }
}
