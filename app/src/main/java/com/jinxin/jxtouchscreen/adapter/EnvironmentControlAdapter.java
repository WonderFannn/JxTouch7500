package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.base.QuickAdapter;
import com.jinxin.jxtouchscreen.model.Environment;

import java.util.List;

/**
 * Created by zj on 2016/12/5.
 */
public class EnvironmentControlAdapter extends QuickAdapter<Environment> {
    Environment environment;
    String name;
    public EnvironmentControlAdapter(Context context, int res, List<Environment> data, String name) {
        super(context, res, data);
        this.name=name;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        ImageView ivEnvironmentWeather=holder.getView(R.id.environment_weather);
        TextView tvLocation=holder.getView(R.id.tv_location);
        TextView tvTemp=holder.getView(R.id.txt_temp);
        TextView tvCloud=holder.getView(R.id.txt_cloud);
        TextView tvNetdegree=holder.getView(R.id.txt_netdegree);
        environment=data.get(position);
        tvTemp.setText(environment.getKq());
        tvLocation.setText(name);
        if (environment.getKq()!=null) {
            if (environment.getKq().contains("优")) {
                ivEnvironmentWeather.setBackgroundResource(R.drawable.ico_you);
            }else if(environment.getKq().contains("良")) {
                ivEnvironmentWeather.setBackgroundResource(R.drawable.ico_liang);
            }else if(environment.getKq().contains("污染")) {
                ivEnvironmentWeather.setBackgroundResource(R.drawable.ico_cha);
            }
        }
        tvCloud.setText(environment.getSd());
        tvNetdegree.setText(environment.getWd());
        return convertView;
    }

}
