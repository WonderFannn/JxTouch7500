package com.jinxin.jxtouchscreen.fragment.messagelist;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.MessageListAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.event.MessegeListEvent;
import com.jinxin.jxtouchscreen.model.MessageTimer;
import com.jinxin.jxtouchscreen.util.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zj on 2016/12/5.
 */
public class MessageListFragment extends DialogFragment {

    @Bind(R.id.back_btn)
    ImageView ivBackbtn;
    private MessageListAdapter messageListAdapter;
    @Bind(R.id.lv_message_inpection)
    ListView lvMessageInpection;
    @Bind(R.id.btn_message_increase)
    Button btnMessageIncrease;
    private MessageListAddFragment messageListAddFragment;
    //private MessageTimerDaoImpl timerDao;
    private List<MessageTimer> messageTimers;
    private Window window;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, android.R.style.Widget_Material_Toolbar_Button_Navigation);
        EventBus.getDefault().register(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        ButterKnife.bind(this, view);
        //timerDao = new MessageTimerDaoImpl(BaseApp.getContext());
        return view;
    }

    private void initView() {
        initAdapter();
        btnMessageIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageListAddFragment == null) {
                    messageListAddFragment = new MessageListAddFragment();

                }
                messageListAddFragment.show(getFragmentManager(),"messageListAddFragment");
//                CommUtil.showFragment(messageListAddFragment,getChildFragmentManager(),R.id.replace_framelayout_messege_list,"messageListAddFragment");
            }
        });
    }

    private void initAdapter() {
        //messageTimers = timerDao.find();
        messageTimers = DBM.getCurrentOrm().query(MessageTimer.class);
        messageListAdapter = new MessageListAdapter(getActivity(), R.layout.item_message_alert, messageTimers);
        lvMessageInpection.setAdapter(messageListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        initView();

    }

    @Subscribe
    public void onEventMainThread(MessegeListEvent event) {
        switch (event.getPos()) {
            case 0:
                L.e("更新数据");
                initAdapter();
                break;

        }
    }

    @OnClick(R.id.back_btn)
    public void onClick() {
//        CommUtil.hideFragment(getFragmentManager(),"messageListFragment");
        getDialog().dismiss();
    }
}
