package com.jinxin.jxtouchscreen.activity.music;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.event.PosEvent;
import com.jinxin.jxtouchscreen.event.SongEvent;
import com.jinxin.jxtouchscreen.model.Music;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.ButterKnife;

/**
 * 音乐列表页面
 */
public class MusicListFragment extends Fragment {

	private ListView lvMusicList;
	private MusicListAdapter musicListAdapter;
	private List<Music> musicList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		musicList = (List<Music>) getArguments().getSerializable("music_list");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_music_list, container, false);
		ButterKnife.bind(this,rootView);
		initView(rootView);
		return rootView;
	}

	protected void initView(View view) {
		lvMusicList = (ListView) view.findViewById(R.id.lv_music_list);
		if (musicList != null && musicList.size() > 0) {
			musicListAdapter = new MusicListAdapter(getActivity(), musicList);
			lvMusicList.setAdapter(musicListAdapter);
		}

		lvMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				EventBus.getDefault().post(new PosEvent(position));
				EventBus.getDefault().post(new SongEvent(position));
			}
		});
		lvMusicList.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int i) {

			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
	}

	public void updateMusicList(List<Music> list) {
//		musicListAdapter.notifyDataSetChanged(list);
		if (list != null && list.size() > 0) {
			if (musicListAdapter == null) {
				musicListAdapter = new MusicListAdapter(getActivity(), list);
				lvMusicList.setAdapter(musicListAdapter);
			} else {
				musicListAdapter.notifyDataSetChanged(list);
			}
		}
	}

	public void updateListFocus(int index) {
		if (musicListAdapter != null)
			musicListAdapter.setSelectedIndex(index);
		if (lvMusicList != null)
			lvMusicList.setSelection(index);
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}
}
