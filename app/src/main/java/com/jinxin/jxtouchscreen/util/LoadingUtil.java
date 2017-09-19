package com.jinxin.jxtouchscreen.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;

/**
 * Created by XTER on 2017/01/17.
 * 加载动画
 */
public class LoadingUtil {
	public static Dialog loadingDialog;

	/**
	 * 得到自定义的progressDialog
	 *
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createLoadingDialog(Context context, String msg) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_layout, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.ll_loading);// 加载布局
		//俩控件
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.loading_image);
		TextView tipTextView = (TextView) v.findViewById(R.id.loading_text);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_rotate);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);// 设置加载信息

		loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		loadingDialog.setCancelable(true);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout);// 设置布局

//		WindowManager.LayoutParams lp = loadingDialog.getWindow().getAttributes();
//		loadingDialog.getWindow().setGravity(Gravity.CENTER);
//		if (width == 0)
//			lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//		else
//			lp.width = (int) (SysUtil.getScreenWidth() * width);
//
//		if (height == 0)
//			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//		else
//			lp.height = (int) (SysUtil.getScreenHeight() * height);
//		loadingDialog.getWindow().setAttributes(lp);

		return loadingDialog;
	}

	public synchronized static void showLoading(Context context, String msg){
		if(loadingDialog==null)
			loadingDialog = createLoadingDialog(context,msg);
		loadingDialog.show();
	}
}
