package com.jinxin.jxtouchscreen.activity.music;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.adapter.GridViewPowAmpSpeakerAdapter;
import com.jinxin.jxtouchscreen.adapter.GridViewPowerAmplifierAdapter;
import com.jinxin.jxtouchscreen.adapter.ListDialogAdapter;
import com.jinxin.jxtouchscreen.constant.MusicSetting;
import com.jinxin.jxtouchscreen.constant.StaticConstant;
import com.jinxin.jxtouchscreen.db.DBM;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.ChangeSongEvent;
import com.jinxin.jxtouchscreen.event.InputEvent;
import com.jinxin.jxtouchscreen.event.SongEvent;
import com.jinxin.jxtouchscreen.event.SpeakerEvent;
import com.jinxin.jxtouchscreen.model.Music;
import com.jinxin.jxtouchscreen.util.AppUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zj on 2016/12/2.
 *
 */
public class PowerAmplifierPageThree extends Fragment {
    @Bind(R.id.gv_input)
    GridView gvInput;
    @Bind(R.id.gv_speaker)
    GridView gvSpeaker;
    @Bind(R.id.iv_item_check_detail_list1)
    ImageView ivItemCheckDetailList1;
    @Bind(R.id.iv_item_check_detail_list2)
    ImageView ivItemCheckDetailList2;
    @Bind(R.id.iv_item_check_detail_list)
    ImageView ivItemCheckDetailList;
    @Bind(R.id.tv_item_detail_list)
    TextView tvMusicStateInput;
    @Bind(R.id.tv_music_playing_title)
    TextView tvMusicStatePlaySong;
    @Bind(R.id.fl_music_status_output_working)
    TextView tvMusicStateOutputSong;
    @Bind(R.id.rl_detail_top_bar)
    PercentRelativeLayout rlDetailTopBar;
    @Bind(R.id.rl_detail_top_bar1)
    PercentRelativeLayout rlDetailTopBar1;
    @Bind(R.id.rl_detail_top_bar2)
    PercentRelativeLayout rlDetailTopBar2;
    private String[] inputString = new String[]{"USB", "SD", "AUX", "INPUT1", "INPUT2", "INPUT3", "INPUT4"};
    List<String> input = new ArrayList<>();
    List<String> locateion = new ArrayList<>();
    private GridViewPowerAmplifierAdapter gridViewPowerAmplifierAdapterInput;
    private GridViewPowAmpSpeakerAdapter gridViewPowAmpSpeakerAdapter;
    private boolean a = true;
    private boolean b = true;
    private boolean c = true;
    String[] locationString;
    private String currentInput;//当前输入源
    private int currentPlayingSongIndex;//当前播放的音乐
    private String currentSpeakerName;//当前扬声器
    private List<Music> musics;
    private String speakers;//取到所有扬声器
    private List<String> speakerNames;
    Music musicIndex;

    StringBuilder m;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_power_amplifier_page_three, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView(view);
        return view;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    tvMusicStateInput.setText(msg.obj.toString());
                    break;
                case 1:
                    m = (StringBuilder) msg.obj;
                    StringBuilder n = new StringBuilder();
                    for (int i = 0; i < m.length(); i++) {
                        if (m.charAt(i) == '1') {
                            n.append(locationString[i] + " ");
                        }
                    }
                    tvMusicStateOutputSong.setText(n.toString());
                    break;
                case 2:
                    musicIndex = (Music) msg.obj;
                    tvMusicStatePlaySong.setText(musicIndex.getMusicName());
                    break;
                case 3:
                    tvMusicStatePlaySong.setText(msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };

    private void initView(final View view) {
        //进去先隐藏3个item的内容
        view.findViewById(R.id.ll_detail).setVisibility(View.GONE);
        gvInput.setVisibility(View.GONE);
        gvSpeaker.setVisibility(View.GONE);
        ivItemCheckDetailList.setBackgroundResource(R.drawable.ico_fold);
        ivItemCheckDetailList1.setBackgroundResource(R.drawable.ico_fold);
        ivItemCheckDetailList2.setBackgroundResource(R.drawable.ico_fold);

        //初始化音乐播放状态
        currentInput = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.INPUT,
                StaticConstant.INPUT_TYPE_USB);
        tvMusicStateInput.setText(currentInput);
        String speakerStr = SPM.getStr(MusicSetting.SP_NAME, MusicSetting.SPEAKER, "00000000");
        List<String> speakers = AppUtil.getSpeakerNames();
        locationString = speakers.toArray(new String[speakers.size()]);
        final boolean[] status = new boolean[locationString.length];
        StringBuilder n1 = new StringBuilder();
        for (int i = 0; i < speakerStr.length(); i++) {
            if (speakerStr.charAt(i) == '1') {
                n1.append(locationString[i]).append(" ");
            }
        }
        tvMusicStateOutputSong.setText(n1.toString());

        //初始化当前播放音乐
        musics = DBM.loadMusicDataFromDb();
        currentPlayingSongIndex = SPM.getInt(MusicSetting.SP_NAME, MusicSetting.CURRENT_PLAYING_SONG, -1);
        for (Music m : musics) {
            if (m.getPlayNo() == currentPlayingSongIndex) {
                tvMusicStatePlaySong.setText(m.getMusicName());
            }
        }

        rlDetailTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!c) {
                    view.findViewById(R.id.ll_detail).setVisibility(View.GONE);
                    ivItemCheckDetailList.setBackgroundResource(R.drawable.ico_fold);
                } else {
                    view.findViewById(R.id.ll_detail).setVisibility(View.VISIBLE);
                    if (!a || !b) {
                        gvInput.setVisibility(View.GONE);
                        ivItemCheckDetailList1.setBackgroundResource(R.drawable.ico_fold);
                        gvSpeaker.setVisibility(View.GONE);
                        ivItemCheckDetailList2.setBackgroundResource(R.drawable.ico_fold);
                    }
                    ivItemCheckDetailList.setBackgroundResource(R.drawable.ico_unfold);
                }
                c = !c;
            }
        });
        rlDetailTopBar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!a) {
                    gvInput.setVisibility(View.GONE);
                    ivItemCheckDetailList1.setBackgroundResource(R.drawable.ico_fold);
                } else {
                    gvInput.setVisibility(View.VISIBLE);
                    if (!c || !b) {
                        view.findViewById(R.id.ll_detail).setVisibility(View.GONE);
                        ivItemCheckDetailList.setBackgroundResource(R.drawable.ico_fold);
                        gvSpeaker.setVisibility(View.GONE);
                        ivItemCheckDetailList2.setBackgroundResource(R.drawable.ico_fold);
                    }
                    ivItemCheckDetailList1.setBackgroundResource(R.drawable.ico_unfold);
                }
                a = !a;
            }
        });
        rlDetailTopBar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!b) {
                    gvSpeaker.setVisibility(View.GONE);
                    ivItemCheckDetailList2.setBackgroundResource(R.drawable.ico_fold);
                } else {
                    if (!a || !c) {
                        view.findViewById(R.id.ll_detail).setVisibility(View.GONE);
                        ivItemCheckDetailList.setBackgroundResource(R.drawable.ico_fold);
                        gvInput.setVisibility(View.GONE);
                        ivItemCheckDetailList1.setBackgroundResource(R.drawable.ico_fold);
                    }
                    gvSpeaker.setVisibility(View.VISIBLE);
                    ivItemCheckDetailList2.setBackgroundResource(R.drawable.ico_unfold);
                }
                b = !b;
            }
        });

        //输入源
        final boolean[] statusInput = new boolean[inputString.length];
        for (int i = 0; i < inputString.length; i++) {
            if (inputString[i].equalsIgnoreCase(SPM.getStr(MusicSetting.SP_NAME, MusicSetting.INPUT,
                    StaticConstant.INPUT_TYPE_USB))) {
                statusInput[i] = true;
            }
        }
        final ListDialogAdapter adapterInput = new ListDialogAdapter(getActivity(), inputString, statusInput);
        gvInput.setAdapter(adapterInput);

        gvInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < inputString.length; i++) {
                    statusInput[i] = false;
                }
                statusInput[position] = true;
                adapterInput.notifyDataSetChanged(inputString, statusInput);
                for (int i = 0; i < inputString.length; i++) {
                    if (statusInput[i]) {
                        EventBus.getDefault().post(new InputEvent(inputString[i]));
                        handler.obtainMessage(0, inputString[i]).sendToTarget();
                    }
                }
            }
        });

        //扬声器
        for (int i = 0; i < speakerStr.length(); i++) {
            if (speakerStr.charAt(i) == '0') {
                status[i] = false;
            } else {
                status[i] = true;
            }
        }
        if (gridViewPowAmpSpeakerAdapter == null) {
            gridViewPowAmpSpeakerAdapter = new GridViewPowAmpSpeakerAdapter(getActivity(), locationString, status);
        }
        gvSpeaker.setAdapter(gridViewPowAmpSpeakerAdapter);
        gvSpeaker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                status[position] = !status[position];
                gridViewPowAmpSpeakerAdapter.notifyDataSetChanged(locationString, status);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < status.length; i++) {
                    if (status[i]) {
                        sb.append(1);
                    } else {
                        sb.append(0);
                    }
                }
                EventBus.getDefault().post(new SpeakerEvent(sb));
                handler.obtainMessage(1, sb).sendToTarget();
            }
        });
    }

    @Subscribe
    public void onEventMainThread(SongEvent event) {
        musics = DBM.loadMusicDataFromDb();
        currentPlayingSongIndex = SPM.getInt(MusicSetting.SP_NAME, MusicSetting.CURRENT_PLAYING_SONG, -1);
        /*for (Music m : musics) {
            if (m.getNo() == currentPlayingSongIndex) {
                tvMusicStatePlaySong.setText(m.getMusicName());
            }
        }*/
        musicIndex = musics.get(event.getIndex());
        handler.obtainMessage(2, musicIndex).sendToTarget();
    }

    @Subscribe
    public void onEventMainThread(ChangeSongEvent event) {
        handler.obtainMessage(3,event.getSongName()).sendToTarget();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }
}
