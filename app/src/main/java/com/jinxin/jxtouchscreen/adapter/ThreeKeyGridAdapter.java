package com.jinxin.jxtouchscreen.adapter;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.event.PosEvent;
import com.jinxin.jxtouchscreen.model.ProductFun;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by XTER on 2017/03/27.
 * 三键界面适配器
 */
public class ThreeKeyGridAdapter extends BaseAdapter {

	public static final String[] THREE_SWITCHES_NAMES = BaseApp.getContext().getResources().getStringArray(R.array.three_switches);
	public static final int[] THREE_SWITCHES_IMGS = {R.drawable.selector_three_switch_three, R.drawable.selector_three_switch_four, R.drawable.selector_three_switch_five, R.drawable.selector_three_switch_six};

	private boolean[] status;
	private ProductFun productFun;

	LayoutInflater inflater;

	public ThreeKeyGridAdapter(ProductFun productFun, boolean[] switchStatus) {
		this.inflater = LayoutInflater.from(BaseApp.getContext());
		this.productFun = productFun;
		this.status = switchStatus;
	}

	@Override
	public int getCount() {
		return status.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		final int pos = position;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.three_switch_gv, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.tvSwitcher.setText(THREE_SWITCHES_NAMES[position] + "开关");
		for(int i=0;i<THREE_SWITCHES_IMGS.length;i++){
			if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCHES[i])){
				holder.ivSwitcher.setBackgroundResource(THREE_SWITCHES_IMGS[i]);
			}
		}
		//改变按钮背景图
		holder.ivSwitcher.setSelected(status[position]);
		//按钮的点击响应事件
		holder.ivSwitcher.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new PosEvent(pos));
			}
		});
		return convertView;
	}

	/**
	 * 更新列表视图
	 *
	 * @param switchStatus 开关状态
	 */
	public void notifyDataSetChanged(boolean[] switchStatus) {
		this.status = switchStatus;
		super.notifyDataSetChanged();
	}

	static class Holder {
		ImageView ivSwitcher;
		TextView tvSwitcher;

		public Holder(View view) {
			ivSwitcher = (ImageView) view.findViewById(R.id.three_switch_gv_imag);
			tvSwitcher = (TextView) view.findViewById(R.id.three_switch_gv_iv);
		}
	}
}
