package com.jinxin.jxtouchscreen.fragment.messagelist;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.event.MessegeListEvent;
import com.jinxin.jxtouchscreen.model.MessageTimer;
import com.jinxin.jxtouchscreen.widget.ragebar.RangeBar;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zj on 2016/12/5.
 */
public class MessageListAddFragment extends DialogFragment {
    private RangeBar rangeBar;
    private TextView tvTimeRange;
    private int leftTime = 0;
    private int rightTime = 24;
    //private MessageTimerDaoImpl timerDao;
    private Button button;
    private List<MessageTimer> messageTimers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NoTitleBar);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list_add, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rangeBar = (RangeBar) view.findViewById(R.id.range_bar);
        tvTimeRange = (TextView) view.findViewById(R.id.tv_time_range);
        button = (Button) view.findViewById(R.id.btn_message_increase);
        //timerDao = new MessageTimerDaoImpl(BaseApp.getContext());
        leftTime = rangeBar.getLeftIndex();
        rightTime = rangeBar.getRightIndex();
        tvTimeRange.setText(formatIntToString(leftTime) + "-" + formatIntToString(rightTime));
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {

            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                tvTimeRange.setText(formatIntToString(leftPinIndex) + "-" + formatIntToString(rightPinIndex));
                leftTime = leftPinIndex;
                rightTime = rightPinIndex;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageTimer entity = new MessageTimer();
                entity.setTimeRange(formatIntToString(leftTime) + "-" + formatIntToString(rightTime));

                entity.setId((int) System.currentTimeMillis());
                DBM.getCurrentOrm().save(entity);
                /*try {
                    timerDao.saveOrUpdate(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                EventBus.getDefault().post(new MessegeListEvent(0));
                getDialog().dismiss();
//                CommUtil.hideFragment(getFragmentManager(),"messageListAddFragment");
            }
        });
    }

    private String formatIntToString(int time) {
        String str = "";
        if (time < 10) {
            str = "0" + time + ":00";
        } else {
            str = time + ":00";
        }
        return str;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.back_btn)
    public void onClick() {
        getDialog().dismiss();
    }
}
