package com.jinxin.jxtouchscreen.activity.music;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.model.Music;
import com.jinxin.jxtouchscreen.util.StringUtils;

import java.util.List;

/**
 * 音乐列表适配器
 */
public class MusicListAdapter extends BaseAdapter {
	LayoutInflater layoutInflater;
	List<Music> musicList;
	int selectedIndex;

	public MusicListAdapter(Context context, List<Music> musicList) {
		this.layoutInflater = LayoutInflater.from(context);
		this.musicList = musicList;
	}

	@Override
	public int getCount() {
		return musicList.size();
	}

	@Override
	public Object getItem(int i) {
		return musicList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		Holder holder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_music_list, null);
			holder = new Holder(view);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		holder.tvMusicTitle.setText(StringUtils.getFileName(musicList.get(position).getMusicName()));
//		holder.tvMusicTitle.setText(musicList.get(position).getMusicName());
		holder.tvMusicTime.setText(musicList.get(position).getCreateTime());

		if (position == selectedIndex) {
			holder.tvMusicTitle.setTextColor(ContextCompat.getColor(BaseApp.getContext(), R.color.music_list_hightlight_text));
			holder.tvMusicTime.setTextColor(ContextCompat.getColor(BaseApp.getContext(), R.color.music_list_hightlight_text));
		}else{
			holder.tvMusicTitle.setTextColor(ContextCompat.getColor(BaseApp.getContext(), R.color.color_ccc));
			holder.tvMusicTime.setTextColor(ContextCompat.getColor(BaseApp.getContext(), R.color.color_ccc));
		}

		return view;
	}

	public void notifyDataSetChanged(List<Music> musicList) {
		this.musicList = musicList;
		super.notifyDataSetChanged();
	}

	public void setSelectedIndex(int index) {
		selectedIndex = index;
		super.notifyDataSetChanged();
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	static class Holder {
		TextView tvMusicTitle;
		TextView tvMusicTime;

		public Holder(View view) {
			tvMusicTime = (TextView) view.findViewById(R.id.tv_music_time);
			tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
		}
	}
}
