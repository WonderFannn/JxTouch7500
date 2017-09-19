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
import com.jinxin.jxtouchscreen.constant.ProductConstants;
import com.jinxin.jxtouchscreen.event.PosEvent;
import com.jinxin.jxtouchscreen.model.ProductFun;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by XTER on 2016/8/15.
 * 五路开关适配器
 */
public class SwitchesListAdapter extends BaseAdapter {

	/**
	 * 开关状态
	 */
	private boolean[] status;
	private ProductFun productFun;
	private DialogFragment fragment;

	LayoutInflater inflater;

	public static final String[] MULTI_SWITCHES_NAMES = BaseApp.getContext().getResources().getStringArray(R.array.multi_switches);

	public SwitchesListAdapter(DialogFragment fragment, ProductFun productFun, boolean[] switchStatus) {
		this.inflater = LayoutInflater.from(BaseApp.getContext());
		this.fragment = fragment;
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
			convertView = inflater.inflate(R.layout.item_five_switches, null);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		//五路开关
		if(productFun.getFunType().equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)){
			holder.tvSwitcher.setText("第 " + (position + 1) + " 路开关");
		}
		//多路开关
		for(String funType:ProductConstants.FUN_TYPE_MULITIPLE_SWITCHES){
			if(productFun.getFunType().equals(funType)){
				holder.tvSwitcher.setText(MULTI_SWITCHES_NAMES[position]+"开关");
			}
		}

		//改变按钮背景图
		if (status[position]) {
			holder.ivSwitcher.setBackgroundResource(R.drawable.selector_socket_on_multi);
			holder.ibSwitcher.setBackgroundResource(R.drawable.six_switch_on);
		} else {
			holder.ivSwitcher.setBackgroundResource(R.drawable.selector_socket_off_multi);
			holder.ibSwitcher.setBackgroundResource(R.drawable.six_switch_off);
		}

		//按钮的点击响应事件
		holder.ivSwitcher.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new PosEvent(pos));
			}
		});
		holder.ibSwitcher.setOnClickListener(new View.OnClickListener() {

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
		ImageButton ibSwitcher;

		public Holder(View view) {
			ivSwitcher = (ImageView) view.findViewById(R.id.img_switch);
			tvSwitcher = (TextView) view.findViewById(R.id.tv_item_switch);
			ibSwitcher = (ImageButton) view.findViewById(R.id.ib_item_switch_toggle);
		}
	}

}
