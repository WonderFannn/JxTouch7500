package com.jinxin.jxtouchscreen.activity.wirelessairconditionoutlet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jinxin.jxtouchscreen.model.FunDetail;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.jinxin.jxtouchscreen.widget.bl.ArrowDirection;
import com.jinxin.jxtouchscreen.widget.bl.BackGroupDialog;
import com.jinxin.jxtouchscreen.widget.bl.BubbleLayout;
import com.jinxin.jxtouchscreen.widget.bl.BubblePopupHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WirelessAirConditionOutletActivity extends AppCompatActivity implements OnClickListener {

    private TextView textView;
    private List<Integer> listTvId;
    private List<TextView> textViews;
    private LinearLayout linearLayout;
    private List<Integer> listLnId;
    private List<LinearLayout> linearLayouts;
    private TaskListener<Task> listener;
    private ProductFun productFun;
    private FunDetail funDetail;
    private Map<String, Object> params = null;
    private PopupWindow popupWindow;
    private int height;
    private int width;
    private int bubbleLayoutHeight;
    private int bubbleLayoutWidth;
    private BubbleLayout bubbleLayout;
    private StringBuffer stb;
    private TextView on;
    private TextView off;
    private ImageView imageView;
    ImageView back;
    private TextView tvSwitch;
    private TextView powerNumLvTextView;
    private TextView powerStateLvTextView;
    private BackGroupDialog backGroupDialog;
    private Map<String, Object> map;
    //静态功能常量
    public static final String STUDY_INFRARED = "01";
    public static final String SEND_PLATFORM_LIBRARY = "02";
    public static final String SEND_LOCAL_LIBRARY = "03";
    public static final String RESET_LOCAL_LIBRARY = "04";
    public static final String READ_LOCAL_LIBRARY = "05";
    public static final String READ_POWER_STATE = "06";
    public static final String POWER_SETTING = "07";
    public static final String POWER_READ = "08";
    public static final String LED_SETTING = "09";
    public static final String LED_READ = "0A";
    public static final String SEND_INFRARED_DELAYED = "0B";
    public static final String SETTING_INFRARED_DELAYED = "0C";
    private String type = null;
    private int opens;
    /**
     * 上下文
     */
    private Context mContext = null;
    String payload;
    private boolean isClickable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initView();
        initData();

    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
//			stb = new StringBuffer();
            switch (msg.what) {
                case 1: //开关关闭
                    //收起布局
                    for (int i = 0; i < linearLayouts.size(); i++) {
                        linearLayouts.get(i).setVisibility(View.INVISIBLE);
                    }
                    for (int i = 0; i < textViews.size() - 1; i++) {
                        textViews.get(i).setVisibility(View.INVISIBLE);
                        ;
                    }
                    imageView.setBackgroundResource(R.drawable.wireless_outlet_gray);
                    tvSwitch.setText("关闭");
                    ToastUtil.showShort(mContext, "开关关闭");
                    break;
                case 2:   //学习红外
                    backGroupDialog.setImageViewBackground(R.drawable.wireless_infrared_study);
                    backGroupDialog.setTextViewState("红外码学习完毕");
                    bgdTextViewStudy.setClickable(true);
                    backGroupDialog.setTextViewOk(true);
                    handler.sendEmptyMessageDelayed(8, 2000);
                    ToastUtil.showShort(mContext, "学习红外码");
                    break;
                case 3:   //开关开启
                    //展开其余布局
                    for (int i = 0; i < linearLayouts.size(); i++) {
                        linearLayouts.get(i).setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < textViews.size() - 1; i++) {
                        textViews.get(i).setVisibility(View.VISIBLE);
                        ;
                    }
                    imageView.setBackgroundResource(R.drawable.wireless_outlet_blue);
                    tvSwitch.setText("开启");
                    ToastUtil.showShort(mContext, "开关开启");
                    break;
                case 4:   //发送红外
                    backGroupDialog.setImageViewBackground(R.drawable.wireless_infrared_send);
                    backGroupDialog.setTextViewState("红外码发送完毕");
                    bgdTextViewSend.setClickable(true);
                    backGroupDialog.setTextViewOk(true);
                    handler.sendEmptyMessageDelayed(8, 2000);
                    ToastUtil.showShort(mContext, "发送红外码");
                    break;
                case 5:   //更新功率数值
                    if (stb != null) {
                        powerNumLvTextView.setText(stb.toString());
                    }
                    ToastUtil.showShort(mContext, "获取功率数值");
                    break;
                case 6:   //更新功率状态
                    if (stb != null) {
                        powerStateLvTextView.setText(stb.toString());
                    }
                    ToastUtil.showShort(mContext, "获取功率状态");
                    break;
                case 7:   //更新开关状态
                    ToastUtil.showShort(mContext, stb.toString());
                    handler.sendEmptyMessage(1);
                    break;
                case 8:
                    if (backGroupDialog != null)
                        backGroupDialog.dismiss();
                    canSend = !canSend;
                    break;
                default:
                    break;
            }
            for (int i = 0; i < textViews.size(); i++) {
                textViews.get(i).setSelected(false);
            }
            ;
        }

        ;
    };

    private void initData() {
        // TODO Auto-generated method stub
        mContext=BaseApp.getContext();
        Intent intent = getIntent();
        productFun = (ProductFun) intent.getSerializableExtra("productFun");
//		getSupportActionBar().setTitle(productFun.getFunName());
        funDetail = (FunDetail) intent.getSerializableExtra("funDetail");
        listener = new TaskListener<Task>() {
            @Override
            public void onFail(Task task, Object[] arg) {
                if (arg != null) {
                    RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
                    ToastUtil.showShort(mContext, resultObj.validResultInfo);
                }
                BaseApp.getInstance().isClickable = false;
                handler.sendEmptyMessage(8);
            }

            @Override
            public void onSuccess(Task task, Object[] arg) {
                BaseApp.getInstance().isClickable = false;
                RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
                /*if ("-1".equals(resultObj.validResultInfo)){
                    ToastUtil.showShort(mContext, mContext.getString(R.string.mode_contorl_fail));
					handler.sendEmptyMessage(8);
					return;
				}*/
                String resultStr = resultObj.validResultInfo;
                try {
                    payload = resultStr.substring(resultStr.indexOf("[") + 1, resultStr.indexOf("]"));
                } catch (Exception e) {

                }
                if (type.equals(StaticConstant.OPERATE_COMMON_CMD)) {
                    if (map.get("op").equals(STUDY_INFRARED)) {
                        Message msg = new Message();
                        msg.what = 2;
                        handler.sendMessageDelayed(msg, 2000);
                    } else if (map.get("op").equals(SEND_LOCAL_LIBRARY)) {
                        Message msg = new Message();
                        msg.what = 4;
                        handler.sendMessageDelayed(msg, 2000);
                    } else if (map.get("op").equals(READ_POWER_STATE)) {
                        try {
                            getResponseStr(payload);
                        } catch (Exception e) {

                        }
                        handler.sendEmptyMessage(6);
                    }
                } else if (type.equals("on")) {
                    handler.sendEmptyMessage(3);
                } else if (type.equals("off")) {
                    handler.sendEmptyMessage(1);
                } else if (type.equals("readStatus") && opens == 2) {
                    try {
                        getResponseStr(payload);
                    } catch (Exception e) {

                    }
                    handler.sendEmptyMessage(7);//读取开关状态
                } else if (type.equals("read") && opens == 3) {
                    try {
                        getResponseStr(payload);
                    } catch (Exception e) {

                    }
                    handler.sendEmptyMessage(5);//读取功率数值
                }
                System.out.println(mContext.getString(R.string.mode_contorl_success));
            }
        };

        //测量宽高，放置dialog的位置
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textViews.get(5).measure(w, h);
        height = textViews.get(5).getMeasuredHeight();
        width = textViews.get(5).getMeasuredWidth();
        bubbleLayout.measure(w, h);
        bubbleLayoutWidth = bubbleLayout.getMeasuredWidth();
        bubbleLayoutHeight = bubbleLayout.getMeasuredHeight();

    }


    /**
     * 获取状态字符串
     *
     * @param result 应答字符串
     * @return string 状态字符
     */
    protected String getResponseStr(String result) {
        if (result == null)
            return null;
        stb = new StringBuffer();
        int length = result.length();
        String statusCode = result.substring(length - 9, length - 1);
        String[] code = statusCode.split(" ");
        for (int i = 0; i < code.length; i++) {
            System.out.println("code=" + code[i].toString() + "==");
        }
        if (code[0].equals("29")) {//读取功率数值
            stb.append(Integer.valueOf((code[2] + code[1]), 16) + "W");
        } else if (code[1].equals("10")) {//读取开关状态
            if (code[2].equals("00")) {
                stb.append("关");
            } else if (code[2].equals("01")) {
                stb.append("开");
            }
        } else if (code[0].equals("06")) {//获取功率状态
            if (code[2].equals("01")) {
                stb.append("欠功率");
            } else if (code[2].equals("02")) {
                stb.append("正常输出");
            } else if (code[2].equals("03")) {
                stb.append("过功率");
            } else if (code[2].equals("00")) {
                stb.append("继电器关");
            }
            Log.e("=====", "=" + code[0].toString() + "=" + code[1].toString() + "=" + code[2].toString() + "=" + code.length);
        }

        return stb.toString();
    }

    public void initView() {
        // TODO Auto-generated method stub

        setContentView(R.layout.activity_wireless_aircondition_outlet);
        bubbleLayout = (BubbleLayout) LayoutInflater.from(this).inflate(R.layout.layout_sample_popup, null);
        on = (TextView) bubbleLayout.findViewById(R.id.sample_popup_on);
        off = (TextView) bubbleLayout.findViewById(R.id.sample_popup_off);
        imageView = (ImageView) findViewById(R.id.wireless_imageview);
        back = (ImageView) findViewById(R.id.back);
        tvSwitch = (TextView) findViewById(R.id.wireless_switch_state);
        powerNumLvTextView = (TextView) findViewById(R.id.wireless_power_number_ln_tv);
        powerStateLvTextView = (TextView) findViewById(R.id.wireless_power_state_ln_tv);
        ActionBar actionBar = getSupportActionBar();
		/*actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);*/
//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_HOME_AS_UP );
//		actionBar.setCustomView(R.layout.activity_wireless_aircondition_outlet_title);
        //初始化功能按键
        listTvId = new ArrayList<>();
        textViews = new ArrayList<>();
        listTvId.add(R.id.wireless_power_number);
        listTvId.add(R.id.wireless_power_state);
        listTvId.add(R.id.wireless_send_infrared);
        listTvId.add(R.id.wireless_study_infrared);

        listTvId.add(R.id.wireless_switch_on_off_state);
        listTvId.add(R.id.wireless_switch_on_off);
        for (int i = 0; i < listTvId.size(); i++) {
            textView = (TextView) findViewById(listTvId.get(i));
            textViews.add(textView);
            textView.setOnClickListener(this);
        }
        for (int i = 0; i < textViews.size() - 1; i++) {
            textViews.get(i).setVisibility(View.INVISIBLE);
            ;
        }
        //初始化开关布局
        listLnId = new ArrayList<Integer>();
        linearLayouts = new ArrayList<LinearLayout>();
        listLnId.add(R.id.wireless_power_state_ln);
        listLnId.add(R.id.wireless_power_number_ln);
        for (int i = 0; i < listLnId.size(); i++) {
            linearLayout = (LinearLayout) findViewById(listLnId.get(i));
            linearLayouts.add(linearLayout);
        }
        on.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
                operateAircondition(productFun, "", 0);
                textViews.get(5).setSelected(false);
            }
        });
        off.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
                operateAircondition(productFun, "", 1);
                textViews.get(5).setSelected(false);
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        //返回上一个界面
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private TextView bgdTextViewStudy;
    private TextView bgdTextViewSend;

    @Override
    public void onClick(View arg0) {
        if (BaseApp.getInstance().isClickable) {
            ToastUtil.showShort(mContext, mContext.getString(R.string.operate_invalid_work));
            return;
        }
        //重置选中状态
        for (int i = 0; i < textViews.size(); i++) {
            textViews.get(i).setSelected(false);
        }
        //设置点击状态及功能
        if (funDetail == null || productFun == null) {
            return;
        }

        switch (arg0.getId()) {
            case R.id.wireless_power_number://读取功率数值
                textViews.get(0).setSelected(true);
                operateAircondition(productFun, "", 3);
                break;
            case R.id.wireless_power_state://功率状态
                textViews.get(1).setSelected(true);
                operateAircondition(productFun, READ_POWER_STATE, -1);
                break;
            case R.id.wireless_send_infrared://发送红外
                textViews.get(2).setSelected(true);
                backGroupDialog = new BackGroupDialog(this, R.style.ShareDialog,
                        R.drawable.wireless_infrared_send, "发送红外码");
                backGroupDialog.show();
                bgdTextViewSend = backGroupDialog.setTextViewOk(true);
                bgdTextViewSend.setClickable(true);
                bgdTextViewSend.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        if (!canSend) {
                            operateAircondition(productFun, SEND_LOCAL_LIBRARY, -1);
                            canSend = !canSend;
                            bgdTextViewSend.setClickable(false);

                        } else {
//						ToastUtil.showShort(WirelessAirConditionOutletActivity.this, "发送中...");
                        }

                    }
                });

                break;
            case R.id.wireless_study_infrared://学习红外
                textViews.get(3).setSelected(true);
                backGroupDialog = new BackGroupDialog(this, R.style.ShareDialog,
                        R.drawable.wireless_infrared_study, "学习红外码");
                backGroupDialog.show();
                bgdTextViewStudy = backGroupDialog.setTextViewOk(true);
                bgdTextViewStudy.setClickable(true);
                bgdTextViewStudy.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        if (!canSend) {
                            operateAircondition(productFun, STUDY_INFRARED, -1);
                            canSend = !canSend;
                            bgdTextViewStudy.setClickable(false);
                        } else {
//						ToastUtil.showShort(WirelessAirConditionOutletActivity.this, "发送中...");
                        }

                    }
                });

                break;
            case R.id.wireless_switch_on_off://开关
                textViews.get(5).setSelected(true);
                popupWindow = BubblePopupHelper.create(this, bubbleLayout);
                int[] location = new int[2];
                textViews.get(5).getLocationInWindow(location);
                bubbleLayout.setArrowPosition(bubbleLayoutWidth / 2 - 0.5f);
                bubbleLayout.setArrowDirection(ArrowDirection.BOTTOM);
                popupWindow.showAtLocation(textViews.get(5), Gravity.NO_GRAVITY,
                        location[0] - (bubbleLayoutWidth - width) / 2, location[1] - bubbleLayoutHeight);
                break;
            case R.id.wireless_switch_on_off_state://开关状态
                textViews.get(4).setSelected(true);
                operateAircondition(productFun, "", 2);
                break;


            default:
                break;
        }
    }

    private boolean canSend;

    /**
     * 无线智能空调插座指令发送
     *
     * @param productFun
     * @param productState
     */
    private void operateAircondition(ProductFun productFun, String op, int open) {
        L.d(null, "operateAircondition");
        if (productFun == null) return;
        BaseApp.getInstance().isClickable = true;

        if (op.equals(STUDY_INFRARED)) {
            backGroupDialog.setImageViewBackgroundGif(R.drawable.wireless_infrared_study);
            backGroupDialog.setTextViewState("红外码学习中...");
            backGroupDialog.setTextViewOk(false);
        } else if (op.equals(SEND_LOCAL_LIBRARY)) {
            backGroupDialog.setImageViewBackground(R.drawable.wireless_infrared_send);
            backGroupDialog.setTextViewState("红外码发送中...");
            backGroupDialog.setTextViewOk(false);
        }
        map = new HashMap<String, Object>();
        map.put("src", "0x01");
        map.put("dst", "0x01");

        if (open == -1) {
            map.put("text", "");
            map.put("op", op);
            map.put("type", "00 29");
            type = StaticConstant.OPERATE_COMMON_CMD;
        } else if (open == 0) {
            type = "on";
        } else if (open == 1) {
            type = "off";
        } else if (open == 2) {//开关状态
            type = "readStatus";
            opens = open;
        } else if (open == 3) {//功率数值
            type = "read";
            opens = open;
        }

        List<byte[]> cmdAll = new ArrayList<byte[]>();
        /*if (NetworkModeSwitcher.useOfflineMode(this)) {
            String zegbingWhId = AppUtil.getGetwayMACByProductWhId(this, productFun.getWhId());
            String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(this, zegbingWhId);
            if (localHost == null || "".equals(localHost)) {
                Logger.error(null, "localHost is null");
                Toast.makeText(this, "网关离线", Toast.LENGTH_SHORT).show();
                return;
            }
            OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
            List<byte[]> cmdList = cmdGenerator.generateCmd2(this, productFun, funDetail, map, type);
            cmdAll.addAll(cmdList);
            OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(this,
                    localHost + ":3333", cmdAll, true, false);
            offlineSender.addListener(listener);
            offlineSender.send();
        } else {
            OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
            List<byte[]> cmdList = cmdGenerator.generateCmd2(this, productFun, funDetail, map, type);
            cmdAll.addAll(cmdList);
            String Urlhost = DatanAgentConnectResource.DatanAgentConnectResourceZigbee();
            OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(this,
                    Urlhost, true, cmdAll, true, 0, false);
            onlineSender.addListener(listener);
            onlineSender.send();
        }*/
        List<byte[]> cmdList = CmdBuilder.build().generateCmd(productFun, funDetail, map, type);
        cmdAll.addAll(cmdList);
        OnlineCmdSenderLong onlinesender = new OnlineCmdSenderLong(BaseApp.getContext(), NetConstant.hostForZigbee(), CmdType.CMD_ZG, cmdAll, true, 0);
        onlinesender.addListener(listener);
        onlinesender.send();
    }


}
