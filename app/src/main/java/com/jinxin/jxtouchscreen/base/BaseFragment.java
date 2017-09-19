package com.jinxin.jxtouchscreen.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

/**
 * 基类
 */
public abstract class BaseFragment extends Fragment {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflate(inflater, container);
		ButterKnife.bind(this, rootView);
		initData(savedInstanceState);
		return rootView;
	}

	/**
	 * 构建视图
	 *
	 * @param inflater  视图渲染器
	 * @param container 视图容器
	 * @return 视图
	 */
	public abstract View inflate(LayoutInflater inflater, ViewGroup container);

	/**
	 * 初始化数据
	 */
	public abstract void initData(Bundle savedInstanceState);

	/**
	 * 更新UI
	 *
	 * @param msg msg
	 */
	protected void uiHandleMessage(Message msg) {
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	public static class BaseFragmentUIHandler extends Handler {
		WeakReference<BaseFragment> baseFragmentWeakReference;

		public BaseFragmentUIHandler(BaseFragment baseFragment) {
			baseFragmentWeakReference = new WeakReference<BaseFragment>(baseFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			BaseFragment baseFragment = baseFragmentWeakReference.get();
			baseFragment.uiHandleMessage(msg);
		}
	}
}
