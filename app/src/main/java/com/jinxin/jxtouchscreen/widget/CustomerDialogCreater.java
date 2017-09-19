package com.jinxin.jxtouchscreen.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.util.SysUtil;


/**
 * Created by Yang on 16/4/19.
 */
public class CustomerDialogCreater extends Dialog {

	public CustomerDialogCreater(Context context) {
		super(context);
	}

	public CustomerDialogCreater(Context context, int themeResId, View childView, float width, float height) {
		super(context, themeResId);
		View view = this.getLayoutInflater().inflate(R.layout.dialog_base_layout, null);
		ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
		ScrollView llContent = (ScrollView) view.findViewById(R.id.ll_dialog);
		llContent.addView(childView);
		setContentView(view);
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		this.getWindow().setGravity(Gravity.CENTER);

		this.getWindow().setWindowAnimations(R.style.window_anim_style);
		if (width == 0)
			lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		else
			lp.width = (int) (SysUtil.getScreenWidth() * width);

		if (height == 0)
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		else
			lp.height = (int) (SysUtil.getScreenHeight() * height);
		this.getWindow().setAttributes(lp);
		ivClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
	}

}
