package com.laughing8.attendancecheckin.view.fragment.request;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.bmobobject.RequestRecord;
import com.laughing8.attendancecheckin.view.custom.SettingViewFroward;
import com.laughing8.attendancecheckin.view.fragment.SecondFragment;
import com.laughing8.attendancecheckin.utils.showDialog.DatePickerDialog;
import com.laughing8.attendancecheckin.utils.showDialog.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Laughing8 on 2016/5/22.
 */
public class InitiateRequest extends SecondFragment implements ISimpleDialogListener {

    private int requestType;
    private DatePickerDialog mDialog;
    private SettingViewFroward selectTime;
    private List<String> selectedDays;
    private int startTime = -1;
    private int endTime = -1;
    private TextView requestContent;
    private final int submitRequest = 1;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedDays = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_initiate_request, null);
        if (getArguments() != null) {
            requestType = getArguments().getInt("requestType");
        }
        selectTime = (SettingViewFroward) view.findViewById(R.id.initiate_select_time);
        requestContent = (TextView) view.findViewById(R.id.request_content);
        initView();
        selectTime.setOnClickListener(onIntentClick);
        view.findViewById(R.id.initiate_submit).setOnClickListener(onIntentClick);
        return view;
    }

    private View.OnClickListener onIntentClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.initiate_select_time) {
                //DatePickerFragment fragment = new DatePickerFragment();
                //mActivity.jumpToFragment(fragment, true);
                DatePickerDialog dialog = new DatePickerDialog();
                /**
                 * version:1
                 * type:1 String:请假申请
                 * type:2 String:外勤申请
                 * type:3 String:出差申请
                 * type:4 String:加班申请
                 */
                dialog.setTargetFragment(InitiateRequest.this, requestType);
//                Bundle bundle = new Bundle();
//                if (selectedDays.size() > 0) {
//                    bundle.putStringArrayList("selectedDays", (ArrayList<String>) selectedDays);
//                }
//                bundle.putInt("startTime", startTime);
//                bundle.putInt("endTime", endTime);
//                dialog.setArguments(bundle);
                dialog.show(mActivity.getSupportFragmentManager(), "DatePickerFragment");
            } else if (id == R.id.initiate_submit) {
                if (selectedDays.size() == 0) {
                    Toast.makeText(mActivity, "请选择时间", Toast.LENGTH_SHORT).show();
                    return;
                } else if (requestContent.getText().toString().equals("")) {
                    Toast.makeText(mActivity, "请选择理由或说明", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                        setTargetFragment(InitiateRequest.this, submitRequest).
                        setTitle("确定要提交申请吗？").setPositiveButtonText("确定").
                        setNegativeButtonText("取消").show();
            }
        }
    };

    private void initView() {
        if (requestType == 1) selectTime.getTextL().setText("请选择请假时间");
        else if (requestType == 2) selectTime.getTextL().setText("请选择外勤时间");
        else if (requestType == 3) selectTime.getTextL().setText("请选择出差时间");
        else if (requestType == 4) selectTime.getTextL().setText("请选择加班时间");
        requestContent.setText("");
        selectedDays.clear();
    }

    private boolean showSubmitDialog;

    private boolean submitSucceed;

    private void showSubmitDialog(DialogFragment loadingDialog) {
        showSubmitDialog = false;
        loadingDialog.dismiss();
        String title;
        if (submitSucceed) {
            title = "提交申请成功";
            initView();
        } else title = "提交申请失败，请检查网络";
        SimpleDialogFragment.createBuilder(mActivity, mActivity.getSupportFragmentManager()).
                setTitle(title).setNegativeButtonText("关闭").show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = data.getBundleExtra("bundle");
        if (bundle == null) {
            return;
        }
        List<String> selectedDays = bundle.getStringArrayList("selectedDays");
        if (selectedDays == null || selectedDays.size() == 0) {
            return;
        }
        startTime = bundle.getInt("startTime", -1);
        endTime = bundle.getInt("endTime", -1);
        this.selectedDays = selectedDays;
        String selectedString = null;
        if (resultCode == 1) {
            if (selectedDays.size() == 1) {
                selectedString = "请假时间：" + selectedDays.get(0) + "（当天）";
            } else {
                selectedString = "请假时间：";

                for (String selected : selectedDays) {
                    selectedString += selected + "、";
                }
                selectedString = selectedString.substring(0, selectedString.length() - 1);
                selectedString += "  共" + selectedDays.size() + "天";
            }
        } else if (resultCode == 2) {
            if (selectedDays.size() == 1) {
                selectedString = "外勤时间：" + selectedDays.get(0) + "（当天）";
            } else {
                selectedString = "外勤时间：";

                for (String selected : selectedDays) {
                    selectedString += selected + "、";
                }
                selectedString = selectedString.substring(0, selectedString.length() - 1);
                selectedString += "  共" + selectedDays.size() + "天";
            }
        } else if (resultCode == 3) {
            if (selectedDays.size() == 1) {
                selectedString = "出差时间：" + selectedDays.get(0) + "（当天）";
            } else {
                selectedString = "出差时间：";

                for (String selected : selectedDays) {
                    selectedString += selected + "、";
                }
                selectedString = selectedString.substring(0, selectedString.length() - 1);
                selectedString += "  共" + selectedDays.size() + "天";
            }
        } else if (resultCode == 4) {
            selectedString = "加班时间:" + selectedDays.get(0) + "（当天）";
        }
        if (null != selectedString) {
            selectTime.getTextL().setText(selectedString);
        }
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {

    }

    @Override
    public void onNeutralButtonClicked(int requestCode) {

    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        loadingDialog = new LoadingDialog();
        loadingDialog.show(mActivity.getSupportFragmentManager(), "loadingDialog");
        if (requestCode == submitRequest) {
            RequestRecord request = new RequestRecord();
            request.setUsername(mUser.getUsername());
            request.setRequestType(requestType - 1);
            request.setStartTime(startTime);
            request.setEndTime(endTime);
            request.setDates(selectedDays);
            request.setContent(requestContent.getText().toString());
            request.setStatus(1);
            request.save(mActivity, new SaveListener() {
                @Override
                public void onSuccess() {
                    submitSucceed = true;
                    if (isResumed()) showSubmitDialog(loadingDialog);
                    else showSubmitDialog = true;
                }

                @Override
                public void onFailure(int i, String s) {
                    submitSucceed = false;
                    if (isResumed()) showSubmitDialog(loadingDialog);
                    else showSubmitDialog = true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (showSubmitDialog) showSubmitDialog(loadingDialog);
    }
}

