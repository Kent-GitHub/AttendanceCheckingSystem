package com.laughing8.attendancecheckin.view.fragment.request;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.bmobobject.RequestRecord;
import com.laughing8.attendancecheckin.utils.bmobquery.DataQuery;
import com.laughing8.attendancecheckin.utils.network.NetStatus;
import com.laughing8.attendancecheckin.utils.showDialog.ShowDialogTool;
import com.laughing8.attendancecheckin.view.fragment.SecondFragment;

import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Laughing8 on 2016/5/23.
 */
public class RequestDetail extends SecondFragment implements View.OnClickListener, ISimpleDialogListener {

    private RequestRecord mRequest;
    private int requestType;
    private boolean fromMyRequest;
    private TextView requestTypeTv, requestDates, durationTv;
    private TextView requestContent;
    private TextView textLeft1, textMiddle1, textRight1;
    private TextView textLeft2, textMiddle2, textRight2;
    private ShowDialogTool mDialogTool;
    private DataQuery mQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequest = (RequestRecord) getArguments().getSerializable("requestRecord");
            if (mRequest != null) requestType = mRequest.getRequestType();
            fromMyRequest = getArguments().getBoolean("fromMyRequest", true);
        }
        mDialogTool = new ShowDialogTool(mActivity);
        mQuery = new DataQuery(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_detail_temp, null);
        if (mRequest == null) return view;
        requestTypeTv = (TextView) view.findViewById(R.id.requestTypeTv);
        requestDates = (TextView) view.findViewById(R.id.request_date);
        durationTv = (TextView) view.findViewById(R.id.request_duration);
        requestContent = (TextView) view.findViewById(R.id.request_content);
        textLeft1 = (TextView) view.findViewById(R.id.textLeft1);
        textMiddle1 = (TextView) view.findViewById(R.id.textMiddle1);
        textRight1 = (TextView) view.findViewById(R.id.textRight1);
        textLeft2 = (TextView) view.findViewById(R.id.textLeft2);
        textMiddle2 = (TextView) view.findViewById(R.id.textMiddle2);
        textRight2 = (TextView) view.findViewById(R.id.textRight2);

        //请假申请
        String event = null;
        if (requestType == 0) event = "请假";
        else if (requestType == 1) event = "外勤";
        else if (requestType == 2) event = "出差";
        else if (requestType == 3) event = "加班";
        requestTypeTv.setText(event);
        //"2016-5-16"
        String datesString = "";
        String durationString;
        List<String> dates = mRequest.getDates();
        if (dates.size() == 1) {
            int startTime = mRequest.getStartTime();
            int endTime = mRequest.getEndTime();
            int startHour = startTime / 3600;
            int startMinute = (startTime % 3600) / 60;
            int endHour = endTime / 3600;
            int endMinute = (endTime % 3600) / 60;
            datesString = dates.get(0) + " " + startHour + ":" + startMinute +
                    "~" + " " + endHour + ":" + endMinute;
            int hour, minute;
            hour = (endTime - startTime) / 3600;
            minute = ((endTime - startTime) % 3600) / 60;
            durationString = event + (hour == 0 ? "" : hour + "小时") + (minute == 0 ? "" : minute + "分");
        } else {
            for (String date : dates) {
                datesString += date + "、";
            }
            datesString = datesString.substring(0, datesString.length() - 1);
            durationString = event + dates.size() + "天";
        }
        requestDates.setText(datesString);
        durationTv.setText(durationString);
        initLayout(view);
        return view;
    }

    private boolean doRefresh = true;

    @Override
    public void onResume() {
        super.onResume();
        mActivity.setTitle("申请详情");
        if (doRefresh) {
            doRefresh = false;
            refresh();
        }
        if (showResult) showResult();
    }

    private void refresh() {
        textMiddle1.setText("于 " + mRequest.getCreatedAt());
        String executeName = "管理员";
        String requestName = mRequest.getUsername();
        try {
            if (!mUser.getUsername().equals(mRequest.getUsername())) {
                requestName = mQuery.getUser(mRequest.getUsername()).getName();
            }
            executeName = mQuery.getUser(mRequest.getExecuteByUsername()).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestContent.setText(mRequest.getContent());
        textLeft1.setText(requestName);
        if (mRequest.getStatus() == 1) return;
        textLeft2.setText(executeName);
        textMiddle2.setText("于 " + mRequest.getUpdatedAt());
        if (mRequest.getStatus() == 2) textRight2.setText("通过申请");
        if (mRequest.getStatus() == 3) textRight2.setText("撤回申请");
    }

    private void initLayout(View view) {
        Button callBackBtn = (Button) view.findViewById(R.id.btn_callback);
        Button approveBtn = (Button) view.findViewById(R.id.btn_approve);
        if (fromMyRequest) approveBtn.setVisibility(View.GONE);
        if (mRequest.getStatus() == 1) {
            textLeft2.setVisibility(View.GONE);
            textMiddle2.setVisibility(View.GONE);
        } else if (mRequest.getStatus() == 2) {
            callBackBtn.setVisibility(View.GONE);
            approveBtn.setVisibility(View.GONE);
        } else if (mRequest.getStatus() == 3) {
            callBackBtn.setVisibility(View.GONE);
            approveBtn.setVisibility(View.GONE);
        }
        callBackBtn.setOnClickListener(this);
        approveBtn.setOnClickListener(this);
    }

    private final int callbackRequest = 1;
    private final int approveRequest = 2;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_callback:
                mDialogTool.setConfirmTitle("确定要撤回申请吗？").
                        setTargetFragment(this, callbackRequest).
                        setPositionText("确定").setNegativeText("取消").showConfirm();
                break;
            case R.id.btn_approve:
                mDialogTool.setConfirmTitle("确定要通过申请吗？").
                        setTargetFragment(this, approveRequest).
                        setPositionText("确定").setNegativeText("取消").showConfirm();
                break;
        }
    }

    private boolean showResult;
    private boolean updateSucceed;
    private boolean isCallBack;

    private void updateRecord(int status, String executedBy) {
        if (!NetStatus.netWorkAccess(mActivity)) {
            Toast.makeText(mActivity, "无网络连接", Toast.LENGTH_SHORT).show();
            return;
        }
        if (status == 3) isCallBack = true;
        else if (status == 2) isCallBack = false;
        RequestRecord record = new RequestRecord();
        record.setStatus(status);
        record.setExecuteByUsername(executedBy);
        record.update(mActivity, mRequest.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                updateSucceed = true;
                if (isResumed()) showResult();
                else showResult = true;
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d("RequestDetail", "onFailure_i:" + i + "_s:" + s);
                updateSucceed = false;
                if (isResumed()) showResult();
                else showResult = true;
            }
        });
    }

    private void showResult() {
        showResult = false;
        String result;
        if (updateSucceed) {
            if (isCallBack) result = "撤回申请成功";
            else result = "申请通过成功";
            mDialogTool.setPositionText(null);
        } else {
            if (isCallBack) result = "撤回申请失败，请检查网络";
            else result = "申请通过失败，请检查网络";
            mDialogTool.setPositionText("重试");
        }
        mDialogTool.dismissLoading();
        mDialogTool.showResult(result);
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
        if (updateSucceed) {
            if (getTargetFragment() != null) {
                if (getTargetRequestCode() == 1) {
                    ((MyRequest) getTargetFragment()).setDoRefresh(true);
                } else if (getTargetRequestCode() == 2) {
                    ((MyApprove) getTargetFragment()).setDoRefresh(true);
                }
            }
            mActivity.getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onNeutralButtonClicked(int requestCode) {

    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        if (requestCode == callbackRequest) {
            updateRecord(3, mApplication.getUser().getUsername());
            mDialogTool.showLoading();
        } else if (requestCode == approveRequest) {
            updateRecord(2, mApplication.getUser().getUsername());
            mDialogTool.showLoading();
        }
    }
}
