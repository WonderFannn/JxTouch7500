package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;


/**
 * Created by XTER on 2016/7/14.
 */
public class ListDialogAdapter extends BaseAdapter {

	LayoutInflater layoutInflater;
	private String[] items;
	private boolean[] status;

	public ListDialogAdapter(Context context, String[] items, boolean[] staus) {
		layoutInflater = LayoutInflater.from(context);
		this.items = items;
		this.status = staus;
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int i) {
		return items[i];
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		Holder holder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_gv_power_amplifier_, null);
			holder = new Holder(view);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		if (status[position]) {
			holder.ivChecker.setBackgroundResource(R.drawable.btn_check_on);
		} else {
			holder.ivChecker.setBackgroundResource(R.drawable.btn_check_off);
		}

		holder.tvItem.setText(items[position]);

		return view;
	}

	public void notifyDataSetChanged(String[] items, boolean[] status) {
		this.items = items;
		this.status = status;
		super.notifyDataSetChanged();
	}

	static class Holder {
		ImageView ivChecker;
		TextView tvItem;

		public Holder(View view) {
			ivChecker = (ImageView) view.findViewById(R.id.iv_item_check_radio_list);
			tvItem = (TextView) view.findViewById(R.id.tv_item_radio_list);
		}
	}
}
