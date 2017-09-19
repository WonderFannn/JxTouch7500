package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.base.QuickAdapter;

import java.util.List;

/**
 * Created by zj on 2016/12/2 0002.
 */
public class GridViewPowerAmplifierAdapter extends QuickAdapter<String> {
    private int selectedPos;

    public GridViewPowerAmplifierAdapter(Context context, int res,List<String> data) {
        super(context, res, data);
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        TextView tvMainTitle=holder.getView(R.id.tv_item_radio_list);
        ImageView imageView=holder.getView(R.id.iv_item_check_radio_list);
        tvMainTitle.setText(data.get(position).toString());
        if (selectedPos == position) {
            imageView.setBackgroundResource(R.drawable.btn_check_on);
        } else {
            imageView.setBackgroundResource(R.drawable.btn_check_off);
        }
        return convertView;
    }
    public void select(int pos) {
        selectedPos = pos;
        notifyDataSetChanged();
    }
}

