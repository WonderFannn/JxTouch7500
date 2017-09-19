package com.jinxin.jxtouchscreen.activity.music;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jinxin.jxtouchscreen.R;


public class MusicDiskFragment extends Fragment {

	private ImageView ivDisk;
	private ImageView ivHand;

	Animator animDisk;
	Animator animHand;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music_disk, container, false);
		initView(view);
		return view;
	}

	protected void initView(View view) {
		ivDisk = (ImageView) view.findViewById(R.id.iv_disk_above);
		ivHand = (ImageView) view.findViewById(R.id.iv_disk_hand);

		animDisk = AnimatorInflater.loadAnimator(getActivity(), R.animator.disk_rotate);
		animDisk.setTarget(ivDisk);
	}

	public void startDiskAnim() {
		animDisk.start();
	}

	public void stopDiskAnim() {
		if(animDisk.isRunning()){
			if (Build.VERSION.SDK_INT >= 19){
				animDisk.pause();
			}
			else{
				animDisk.cancel();
				animDisk.end();
			}
		}
	}

	public void startHandAnim() {
		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.music_hand_play);
		animation.setFillAfter(true);
		ivHand.startAnimation(animation);
	}

	public void stopHandAnim() {
		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.music_hand_stop);
		animation.setFillAfter(true);
		ivHand.startAnimation(animation);
	}
}
