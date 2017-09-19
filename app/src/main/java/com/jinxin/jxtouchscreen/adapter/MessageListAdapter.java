package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.base.QuickAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.event.MessegeListEvent;
import com.jinxin.jxtouchscreen.model.MessageTimer;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.litesuits.orm.db.assit.WhereBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by zj on 2016/12/5.
 */
public class MessageListAdapter extends QuickAdapter<MessageTimer> {

    //private final MessageTimerDaoImpl timerDao;
//    private final List<MessageTimer> messageTimers;

    public MessageListAdapter(Context context, int res, List<MessageTimer> data) {
        super(context, res, data);
        //timerDao = new MessageTimerDaoImpl(context);
//        messageTimers = timerDao.find();
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        MessageTimer messageTimer = data.get(position);
        TextView tvMessageTime = holder.getView(R.id.tv_message_time_name);
        final Button button = holder.getView(R.id.btn_message_delete);
        tvMessageTime.setText(messageTimer.getTimeRange());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort(BaseApp.getContext(),"删除成功");
                //timerDao.delete(timerDao.find().get(position).getId());
                long id = DBM.getCurrentOrm().query(MessageTimer.class).get(position).getId();
                DBM.getCurrentOrm().delete(new WhereBuilder(MessageTimer.class, "_id=?", new String[]{id+""}));
                EventBus.getDefault().post(new MessegeListEvent(0));
            }
        });
        return convertView;
    }


}
