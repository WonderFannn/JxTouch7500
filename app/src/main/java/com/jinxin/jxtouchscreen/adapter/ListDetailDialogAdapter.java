package com.jinxin.jxtouchscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.MusicSetting;
import com.jinxin.jxtouchscreen.constant.StaticConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.model.Music;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.widget.FlowLayout;

import java.util.List;

/**
 * Created by XTER on 2016/7/19.
 * 
 */
public class ListDetailDialogAdapter extends BaseAdapter {
	LayoutInflater layoutInflater;
	private String[] items;
	private boolean[] status;

	private String currentInput;
	private int currentPlayingSongIndex;
	private List<Music> musics;
	private String speakers;
	private List<String> speakerNames;
	Context context;

	public ListDetailDialogAdapter(Context context, String[] items, boolean[] staus) {
		layoutInflater = LayoutInflater.from(context);
		this.items = items;
		this.status = staus;
		this.context=context;
		currentInput = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.INPUT,
				StaticConstant.INPUT_TYPE_USB);
		currentPlayingSongIndex = SPM.getInt(MusicSetting.SP_NAME, MusicSetting.CURRENT_PLAYING_SONG, -1);
		musics = DBM.loadMusicDataFromDb();
		speakers = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.SPEAKER, "00000000");
		speakerNames = AppUtil.getSpeakerNames();
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
			view = layoutInflater.inflate(R.layout.item_detail_list_dialog, null);
			holder = new Holder(view);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		holder.tvItem.setText(items[position]);

		if (status[position]) {
			holder.ivChecker.setBackgroundResource(R.drawable.ico_fold);
			holder.llDetail.setVisibility(View.VISIBLE);

			if (items[position].equalsIgnoreCase(currentInput)) {
				//歌曲名
				holder.tvMusicPlayingStatus.setText(BaseApp.getContext().getString(R.string.music_status_playing));
				if (musics.size() > 0) {
					boolean tag = false;
					for (Music m : musics) {
						if (m.getPlayNo() == currentPlayingSongIndex) {
							holder.tvMusicPlayingTitle.setText(m.getMusicName());
							tag = true;
						}
					}
					if (!tag)
						holder.tvMusicPlayingTitle.setText(BaseApp.getContext().getString(R.string.music_unkown_song));
				}
				//扬声器
				holder.flOutputWorkingDetail.removeAllViews();
				for (int i = 0; i < speakers.length(); i++) {
					if (speakers.charAt(i) == '1') {
						TextView outputView = (TextView) layoutInflater.
								inflate(R.layout.item_output_speaker, holder.flOutputWorkingDetail, false);
						outputView.setText(speakerNames.get(i));
						holder.flOutputWorkingDetail.addView(outputView);
					}
				}
			} else {
				holder.tvMusicPlayingStatus.setText(BaseApp.getContext().getString(R.string.music_status_no_play));
			}

		} else {
			holder.ivChecker.setBackgroundResource(R.drawable.ico_unfold);
			holder.llDetail.setVisibility(View.GONE);
		}

		return view;
	}

	public void notifyDataSetChanged(String[] items, boolean[] status) {
		this.items = items;
		this.status = status;
		currentInput = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.INPUT,
				StaticConstant.INPUT_TYPE_USB);
		currentPlayingSongIndex = SPM.getInt(MusicSetting.SP_NAME, MusicSetting.CURRENT_PLAYING_SONG, -1);
		musics = DBM.loadMusicDataFromDb();
		speakers = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.SPEAKER, "00000000");
		super.notifyDataSetChanged();
	}

	static class Holder {
		ImageView ivChecker;
		TextView tvItem;

		LinearLayout llDetail;
		TextView tvMusicPlayingStatus;
		TextView tvMusicPlayingTitle;
		FlowLayout flOutputWorkingDetail;

		public Holder(View view) {
			ivChecker = (ImageView) view.findViewById(R.id.iv_item_check_detail_list);
			tvItem = (TextView) view.findViewById(R.id.tv_item_detail_list);

			llDetail = (LinearLayout) view.findViewById(R.id.ll_detail);
			tvMusicPlayingStatus = (TextView) view.findViewById(R.id.tv_music_play_status);
			tvMusicPlayingTitle = (TextView) view.findViewById(R.id.tv_music_playing_title);
			flOutputWorkingDetail = (FlowLayout) view.findViewById(R.id.fl_music_status_output_working);
		}
	}
}
