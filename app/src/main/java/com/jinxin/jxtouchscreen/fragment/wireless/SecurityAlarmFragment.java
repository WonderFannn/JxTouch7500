package com.jinxin.jxtouchscreen.fragment.wireless;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.cmd.CmdBuilder;
import com.jinxin.jxtouchscreen.cmd.CmdType;
import com.jinxin.jxtouchscreen.cmd.OnlineCmdSenderLong;
import com.jinxin.jxtouchscreen.cmd.RemoteJsonResultInfo;
import com.jinxin.jxtouchscreen.cmd.Task;
import com.jinxin.jxtouchscreen.cmd.TaskListener;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.constant.StaticConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductDoorContact;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 灯光
 */
public class SecurityAlarmFragment extends DialogFragment {

    @Bind(R.id.security_alarm_outdoor)
    ImageButton securityAlarmOutdoor;
    @Bind(R.id.security_alarm_living_at_home)
    ImageButton securityAlarmLivingAtHome;
    @Bind(R.id.security_alarm_power_text)
    TextView securityAlarmPowerText;
    @Bind(R.id.security_alarm_polling)
    ImageButton securityAlarmPolling;
    @Bind(R.id.security_alarm_polling_checkbox)
    ImageView securityAlarmPollingCheckbox;
    @Bind(R.id.security_alarm_device_state)
    TextView securityAlarmDeviceState;
    private boolean clickHomeAndOutFlag;
    private boolean clickPollingOutFlag;
    private ProductFun productFun;
    private FunDetail funDetail;
    private ProductDoorContact doorMagnet;
    private boolean isClickable = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_alarm, container, false);
        ButterKnife.bind(this, view);
        initData();
        initView(view);
        return view;
    }

    private void initView(View view) {
        onClick(securityAlarmOutdoor);
        /*显示状态*/
      /*  if (doorMagnet != null && doorMagnet.getIsWarn() == 0)
            rbSecurity.check(R.id.rb_security_right);

        if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_GAS_SENSE)) {
            ivDevice.setBackgroundResource(R.drawable.gas_sense);
        }

        if (doorMagnet != null) {
            if (doorMagnet.getElectric().equals("02")) {
                ivDevice.setBackgroundResource(R.drawable.power_high);
                csbPower.setProgress(90);
            }
            if (doorMagnet.getElectric().equals("03")) {
                ivDevice.setBackgroundResource(R.drawable.power_mid);
                csbPower.setProgress(60);
            }
            if (doorMagnet.getElectric().equals("04")) {
                ivDevice.setBackgroundResource(R.drawable.power_high);
                csbPower.setProgress(20);
            }
        }*/
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.back_btn, R.id.security_alarm_outdoor, R.id.security_alarm_living_at_home, R.id.security_alarm_polling})
    public void onClick(View view) {
        if (!isClickable) {
            ToastUtil.showShort(getContext(), R.string.operate_invalid_work);
            return;
        }
        switch (view.getId()) {
            case R.id.back_btn:
                getDialog().dismiss();
                break;
            case R.id.security_alarm_outdoor:
                clickHomeAndOutFlag=!clickHomeAndOutFlag;
                securityAlarmOutdoor.setSelected(clickHomeAndOutFlag);
                securityAlarmLivingAtHome.setSelected(!clickHomeAndOutFlag);
                clickHomeAndOutFlag=!clickHomeAndOutFlag;
                if (doorMagnet != null) {
                    doorMagnet.setIsWarn(1);
                    DBM.getCurrentOrm().update(doorMagnet);
                }
                break;
            case R.id.security_alarm_living_at_home:
                clickHomeAndOutFlag=!clickHomeAndOutFlag;
                securityAlarmOutdoor.setSelected(!clickHomeAndOutFlag);
                securityAlarmLivingAtHome.setSelected(clickHomeAndOutFlag);
                clickHomeAndOutFlag=!clickHomeAndOutFlag;
                if (doorMagnet != null) {
                    doorMagnet.setIsWarn(0);
                    DBM.getCurrentOrm().update(doorMagnet);
                }
                break;
            case R.id.security_alarm_polling:
                clickPollingOutFlag=!clickPollingOutFlag;
                securityAlarmPolling.setSelected(clickPollingOutFlag);
                doorMagnetCheck();
                break;
        }
    }

    /******************************/

    public void initData() {
        productFun = (ProductFun) getArguments().getSerializable("productFun");
        funDetail = (FunDetail) getArguments().getSerializable("funDetail");
        if (productFun != null) {
            List<ProductDoorContact> doorMagnets = DBM.getCurrentOrm().query(new QueryBuilder<>(ProductDoorContact.class).where("whId = ?",new String[]{productFun.getWhId()}));
            if (doorMagnets != null && doorMagnets.size() > 0) {
                doorMagnet = doorMagnets.get(0);
            }
        }
    }

    TaskListener<Task> listener = new TaskListener<Task>() {

        @Override
        public void onFail(Task task, Object[] arg) {
            isClickable = true;
            if (arg != null && arg.length > 0) {
                RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
                ToastUtil.showShort(BaseApp.getContext(), resultObj.validResultInfo);
            } else {
                ToastUtil.showShort(BaseApp.getContext(), R.string.operate_failed);
            }
        }

        @Override
        public void onSuccess(Task task, Object[] arg) {
            ToastUtil.showShort(getContext(), R.string.device_valid);
            isClickable = true;
        }

    };

    /**
     * 门磁巡检
     */
    protected void doorMagnetCheck() {
        isClickable = false;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("src", "0x01");
        params.put("dst", "0x01");
        String type = StaticConstant.TYPE_DOOR_MAGNET;
        List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, params, type);
        OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(getActivity(),
                NetConstant.hostForZigbee(), CmdType.CMD_ZG, cmdList, true, 1);
        onlineSender.addListener(listener);
        onlineSender.send();
    }


}
