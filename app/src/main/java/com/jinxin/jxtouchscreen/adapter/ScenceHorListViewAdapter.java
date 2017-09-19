package com.jinxin.jxtouchscreen.adapter;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.base.QuickAdapter;
import com.jinxin.jxtouchscreen.model.CustomerArea;

import java.util.List;

public class ScenceHorListViewAdapter extends QuickAdapter<CustomerArea> {

	private int selectedPos;

	public ScenceHorListViewAdapter(Context context, int res, List<CustomerArea> data) {
		super(context, res, data);
	}

	@Override
	public View getItemView(int position, View convertView, ViewHolder holder) {
		TextView tvTitle = holder.getView(R.id.hor_listview_text);
		ImageView ivIcon = holder.getView(R.id.hor_listview_img);

		tvTitle.setText(data.get(position).getAreaName());

		if (position == selectedPos) {
			ivIcon.setImageResource(R.drawable.hor_listview_after);
			tvTitle.setBackgroundResource(R.drawable.hor_listview_background);
			tvTitle.setTextSize(24);
		}else{
			ivIcon.setImageResource(R.drawable.hor_listview_before);
			tvTitle.setBackgroundResource(R.drawable.hor_listview_background_ahpa);
			tvTitle.setTextSize(22);
		}

		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).getIdd();
	}

	public int getSelectedPos() {
		return selectedPos;
	}

	public void setSelectedPos(int selectedPos) {
		this.selectedPos = selectedPos;
		notifyDataSetChanged();
	}
}