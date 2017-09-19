package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.model.ProductFunVO;
import com.jinxin.jxtouchscreen.util.StringUtils;
import com.jinxin.jxtouchscreen.util.SysUtil;
import com.jinxin.jxtouchscreen.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/4/12.
 */

public class ModelViewAdapter extends BaseAdapter{
    LayoutInflater layoutInflater;
    private int groupIndex;
    private List<List<ProductFunVO>> mData;
    private Context mContext;

    public ModelViewAdapter(Context context,int index , List<List<ProductFunVO>> data) {
        layoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.groupIndex = index;
        this.mData = data;
    }
    @Override
    public int getCount() {
        return mData.get(groupIndex).size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(groupIndex).get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.mode_device_item, null);
            holder = new Holder(view);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        ProductFunVO _productFunVO = (ProductFunVO) getChild(groupIndex, i);
        if (_productFunVO != null) {
            holder.productName.setText(_productFunVO.getProductFun().getFunName());
            String funType = _productFunVO.getProductFun().getFunType();
            if(ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funType)){//双路开关状态
                String opreationDesc = _productFunVO.getProductPatternOperation().getParaDesc();
                if (!TextUtils.isEmpty(opreationDesc)) {
                    if(opreationDesc.equals("none")){
                        holder.tvOpen.setText(_productFunVO.isOpen() ? "开" : "关");
                    }else{
                        try {
                            StringBuffer sb = new StringBuffer();
                            JSONObject jb = new JSONObject(opreationDesc);
                            if(!jb.isNull("left")){
                                String leftStatus = jb.getString("left");
                                sb.append(mContext.getString(R.string.left));
                                sb.append(leftStatus.equals("on") ? "开" : "关");
                                sb.append("|");
                            }
                            if(!jb.isNull("right")){
                                String rightStatus = jb.getString("right");
                                sb.append(mContext.getString(R.string.right));
                                sb.append(rightStatus.equals("on") ? "开" : "关");
                            }
                            holder.tvOpen.setText(sb);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }else if(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT.equals(funType)
                    || ProductConstants.FUN_TYPE_COLOR_LIGHT.equals(funType)
                    || ProductConstants.FUN_TYPE_POP_LIGHT.equals(funType)
                    || ProductConstants.FUN_TYPE_CRYSTAL_LIGHT.equals(funType)
                    || ProductConstants.FUN_TYPE_CEILING_LIGHT.equals(funType)
                    || ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT.equals(funType)
                    || ProductConstants.FUN_TYPE_LIGHT_BELT.equals(funType)
                    || ProductConstants.FUN_TYPE_SPOTLIGHT.equals(funType)){
                //彩灯的状态
                String opreationDesc = _productFunVO.getProductPatternOperation().getParaDesc();
                if (!TextUtils.isEmpty(opreationDesc)) {
                    if(opreationDesc.equals("none")){
                        holder.tvOpen.setText(_productFunVO.isOpen() ? "开" : "关");
                    }else{
                        try {
                            JSONObject jb = new JSONObject(opreationDesc);
                            String colorStr = jb.getString("color");
                            String color = StringUtils.rgbToHex(colorStr);
                            Log.e("Tag","color=====" + color);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(StringUtils.dip2px(mContext,10),StringUtils.dip2px(mContext,10));
                            holder.tvOpen.setLayoutParams(lp);
                            holder.tvOpen.setBackgroundColor(Color.parseColor(color));
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }else if (ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(funType)
                    || ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(funType)
                    || ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(funType)
                    || ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(funType)
                    || ProductConstants.FUN_TYPE_THREE_SWITCH_THREE.equals(funType)
                    || ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funType)
                    || ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(funType)
                    || ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR.equals(funType)
                    || ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE.equals(funType)
                    || ProductConstants.FUN_TYPE_FIVE_SWITCH.equals(funType)
                    || ProductConstants.FUN_TYPE_THREE_SWITCH_SIX.equals(funType)){
                String opreationDesc = _productFunVO.getProductPatternOperation().getParaDesc();
                StringBuffer sb = new StringBuffer();
                //对多路开关进行处理
                if (!TextUtils.isEmpty(opreationDesc)) {
                    if(opreationDesc.equals("none")){
                        holder.tvOpen.setText(_productFunVO.isOpen() ? "开" : "关");
                    }else{
                        try {
                            JSONObject jb = new JSONObject(opreationDesc);
                            if(!jb.isNull("key1")){
                                String key1 = jb.getString("key1").equals("on") ? "开" : "关";
                                sb.append(mContext.getString(R.string.key1));
                                sb.append(key1);
                                sb.append("|");
                            }
                            if(!jb.isNull("key2")){
                                String key2 = jb.getString("key2").equals("on") ? "开" : "关";
                                sb.append(mContext.getString(R.string.key2));
                                sb.append(key2);
                                sb.append("|");
                            }
                            if(!jb.isNull("key3")){
                                String key3 = jb.getString("key3").equals("on") ? "开" : "关";
                                sb.append(mContext.getString(R.string.key3));
                                sb.append(key3);
                                sb.append("|");
                            }
                            if(!jb.isNull("key4")){
                                String key4 = jb.getString("key4").equals("on") ? "开" : "关";
                                sb.append(mContext.getString(R.string.key4));
                                sb.append(key4);
                                sb.append("|");
                            }
                            if(!jb.isNull("key5")){
                                String key5 = jb.getString("key5").equals("on") ? "开" : "关";
                                sb.append(mContext.getString(R.string.key5));
                                sb.append(key5);
                                sb.append("|");
                            }
                            if(!jb.isNull("key6")){
                                String key6 = jb.getString("key6").equals("on") ? "开" : "关";
                                sb.append(mContext.getString(R.string.key6));
                                sb.append(key6);
                                sb.append("|");
                            }
                            if(!jb.isNull("key7")){
                                String key7 = jb.getString("key7").equals("on") ? "开" : "关";
                                sb.append(mContext.getString(R.string.key7));
                                sb.append(key7);
                                sb.append("|");
                            }
                            if(!jb.isNull("key8")){
                                String key8 = jb.getString("key8").equals("on") ? "开" : "关";
                                sb.append(mContext.getString(R.string.key8));
                                sb.append(key8);
                            }
                            if (sb.toString().endsWith("|")){
                                sb.deleteCharAt(sb.length()-1);
                            }
                            holder.tvOpen.setText(sb.toString());
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
            else{
                holder.tvOpen.setText(_productFunVO.isOpen() ? "开" : "关");
            }
            holder.tvOpen.setTextColor(mContext.getResources().getColor(R.color.white));

//            holder.tvOpen.setTextColor(_productFunVO.isOpen() ? mContext.getResources().getColor(R.color.text_green)
//                    : mContext.getResources().getColor(R.color.white));
        }

        return view;
    }

    class Holder{
        TextView productName;
        TextView tvOpen;
        public Holder(View view) {
            productName = (TextView) view.findViewById(R.id.product_detail_name);
            tvOpen = (TextView) view.findViewById(R.id.child_imageView);
        }
    }
    public Object getChild(int groupPosition, int childPosition) {
        List<ProductFunVO> pfvoList = mData
                .get(groupPosition);
        if (pfvoList != null) {
            return pfvoList.get(childPosition);
        }
        return null;
    }

}
