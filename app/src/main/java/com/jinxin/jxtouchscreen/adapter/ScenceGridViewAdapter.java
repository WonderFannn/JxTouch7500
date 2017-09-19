package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.base.QuickAdapter;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.model.CustomerPattern;
import com.jinxin.jxtouchscreen.util.AppUtil;

import java.util.List;

/**
 * Created by XTER on 2017/1/7.
 * 模式列表
 */
public class ScenceGridViewAdapter extends QuickAdapter<CustomerPattern> {
	public ScenceGridViewAdapter(Context context, int res, List<CustomerPattern> data) {
		super(context, res, data);
	}

	@Override
	public View getItemView(int position, View convertView, ViewHolder holder) {
		TextView tvTitle = holder.getView(R.id.space_txt);
		ImageView imageiv = holder.getView(R.id.space_img);

		tvTitle.setText(data.get(position).getPaternName());
		if (TextUtils.isEmpty(data.get(position).getIcon())){
			imageiv.setImageBitmap(AppUtil.getAssetsBitmap(BaseApp.getContext(), "images/img/upload/defaultp.png"));
		}else {
			String iconPath=data.get(position).getIcon();
			if (iconPath.startsWith("/")){
				iconPath=iconPath.substring(1);
			}
			Bitmap _bitmap=AppUtil.getAssetsBitmap(BaseApp.getContext(),iconPath);
			if (_bitmap!=null){
				imageiv.setImageBitmap(_bitmap);
			}else {
				imageiv.setImageBitmap(AppUtil.getAssetsBitmap(BaseApp.getContext(), "images/img/upload/defaultp.png"));
			}
		}
		return convertView;
	}
}
