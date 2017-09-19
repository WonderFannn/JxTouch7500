package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.model.ProductFun;
import com.jinxin.jxtouchscreen.widget.fancycoverflow.FancyCoverFlowAdapter;

import java.util.List;

/**
 * Created by XTER on 2017/1/7.
 * 设备适配器
 */
public class DeviceFancyCoverFlowAdapter extends FancyCoverFlowAdapter {

    LayoutInflater layoutInflater;

    private List<ProductFun> productFunList;

    public DeviceFancyCoverFlowAdapter(Context context, List<ProductFun> productFunList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.productFunList = productFunList;
    }

    @Override
    public View getCoverFlowItem(int position, View reusableView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (reusableView == null) {
            reusableView = layoutInflater.inflate(R.layout.item_fancy_coverflow_adapter, parent, false);
            viewHolder = new ViewHolder(reusableView);
            reusableView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) reusableView.getTag();
        }
        ProductFun productFun = productFunList.get(position);
        if (productFun != null) {
            viewHolder.fancyCoverflowAdapterItemTv.setText(productFun.getFunName());
            switch (productFun.getFunType()) {
                /*case ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT://灯光
                case ProductConstants.FUN_TYPE_CEILING_LIGHT://吸顶灯
                case ProductConstants.FUN_TYPE_WIRELESS_SOCKET://无线插座
                case ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT://无线射灯
                case ProductConstants.FUN_TYPE_POP_LIGHT://球泡灯
                case ProductConstants.FUN_TYPE_ZG_LOCK://ZG锁
                case ProductConstants.FUN_TYPE_AUTO_LOCK: //自动锁
                case ProductConstants.FUN_TYPE_ONE_SWITCH://单路交流开关
                case ProductConstants.FUN_TYPE_LIGHT_BELT://灯带
                    if (productFun.isOpen()) {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.btn_background);
                    } else {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.btn_background_before);
                    }
                    break;
                case ProductConstants.FUN_TYPE_POWER_AMPLIFIER://功放
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.selector_power_amiplier_click);
                    break;
                case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH://多路开关
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.selector_six_key_click);
                    break;
                default:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.btn_background_selector);
                    break;*/
                //窗帘
                case ProductConstants.FUN_TYPE_CURTAIN:
                case ProductConstants.FUN_TYPE_WIRELESS_CURTAIN:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_curtain);
                    break;
                //双路智能开关
                case ProductConstants.FUN_TYPE_DOUBLE_SWITCH:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_two_switch);
                    break;
                //锁
                case ProductConstants.FUN_TYPE_AUTO_LOCK:
                    if (productFun.isOpen()) {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_lock);
                    } else {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_lock_dark);
                    }
                    break;
                case ProductConstants.FUN_TYPE_ZG_LOCK:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_lock);
                    break;
                //五路开关
                case ProductConstants.FUN_TYPE_FIVE_SWITCH:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_five_switch);
                    break;
                //三路开关
                case ProductConstants.FUN_TYPE_THREE_SWITCH_THREE:
                case ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR:
                case ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE:
                case ProductConstants.FUN_TYPE_THREE_SWITCH_SIX:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_three_switch);
                    break;
                //功放
                case ProductConstants.FUN_TYPE_POWER_AMPLIFIER:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_music);
                    break;
                //无线红外转发
                case ProductConstants.FUN_TYPE_INFRARED_TRASPOND:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_red_infra);
                    break;
                //无线空调
                case ProductConstants.FUN_TYPE_AIRCONDITION:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_air_condition);
                    break;
                //彩灯
                case ProductConstants.FUN_TYPE_COLOR_LIGHT://彩灯
                case ProductConstants.FUN_TYPE_CEILING_LIGHT://吸顶灯
                case ProductConstants.FUN_TYPE_POP_LIGHT://球泡灯
                case ProductConstants.FUN_TYPE_SPOTLIGHT://无线射灯
                case ProductConstants.FUN_TYPE_LIGHT_BELT://灯带
                case ProductConstants.FUN_TYPE_CRYSTAL_LIGHT://水晶灯
                    if (productFun.isOpen()) {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.color_light_click_after);
                    } else {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.color_light_click_before);
                    }
                    break;
                //环境控制
                case ProductConstants.FUN_TYPE_ENV:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_environment);
                    break;
                //灯光
                case ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT:
                if (productFun.isOpen()) {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_light);
                    } else {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_light_dark);
                    }
                    break;
                case ProductConstants.FUN_TYPE_WIRELESS_SOCKET://无线插座
                if (productFun.isOpen()) {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_socket);
                    } else {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_socket_dark);
                    }
                    break;
                case ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET://无线空调插座
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_socket);
                    break;
                case ProductConstants.FUN_TYPE_GAS_SENSE:
                case ProductConstants.FUN_TYPE_INFRARED:
                case ProductConstants.FUN_TYPE_SMOKE_SENSE:
                case ProductConstants.FUN_TYPE_DOOR_CONTACT://门磁
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_security_alarm);
                    break;
                case ProductConstants.FUN_TYPE_WIRELESS_GATEWAY://无线网关
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_wir_gateway);
                    break;
                case ProductConstants.FUN_TYPE_ONE_SWITCH://单路交流开关
                if (productFun.isOpen()) {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_one_switch);
                    } else {
                        viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_one_switch_dark);
                    }
                    break;
                //六键开关
                case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH:
                case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE:
                case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR:
                case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE:
                case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO:
                case ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE:
                    viewHolder.fancyCoverflowAdapterItemRl.setBackgroundResource(R.drawable.logo_six_switch);
                    break;
                default:

                    break;
            }
        }

        return reusableView;
    }

    @Override
    public int getCount() {
        return productFunList.size();

    }

    @Override
    public Object getItem(int position) {
        return productFunList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productFunList.get(position).getFunId();
    }

    public void replace(List<ProductFun> productFuns) {
        this.productFunList = productFuns;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView fancyCoverflowAdapterItemTv;
        ImageView fancyCoverflowAdapterItemRl;

        ViewHolder(View view) {
            fancyCoverflowAdapterItemRl = (ImageView) view.findViewById(R.id.fancy_coverflow_adapter_item_rl);
            fancyCoverflowAdapterItemTv = (TextView) view.findViewById(R.id.fancy_coverflow_adapter_item_tv);
        }
    }
}
