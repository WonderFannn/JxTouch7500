package com.jinxin.jxtouchscreen.widget.bl;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jinxin.jxtouchscreen.R;

public class BackGroupDialog extends Dialog {
	
	// private PaintView paintView;
	 int drawbles[] = { R.drawable.wireless_infrared_send, R.drawable.wireless_infrared_study };
	 private int drawble;
	 private String text;
	 private Context context;
//	 int text[]={""}
	 public BackGroupDialog(Context context,int style,int drawableId,String texts) {
	  super(context,style);
	 // this.paintView = paintView;
	  // TODO Auto-generated constructor stub
	  drawble=drawableId;
	  text=texts;
	  this.context=context;
	 }

	 private  TextView ok;
	 private  TextView tv;
	 private  ImageView img;
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	  // TODO Auto-generated method stub
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.history);
	  ok=(TextView) findViewById(R.id.Wireless_dialog_ok);
	  tv=(TextView) findViewById(R.id.Wireless_dialog_tv);
	  img=(ImageView) findViewById(R.id.Wireless_infrared_dialog_img);
	  Glide.with(context).load(drawble).asBitmap().fitCenter().into(img);
	  tv.setText(text);
	  ok.setSelected(true);

	 }
	
	 public void setTextView(String text) {
			tv.setText(text);
		}
	 public TextView setTextViewOk(boolean clickable) {
		 	ok.setSelected(clickable);
			return ok;
		}

	 public TextView setTextViewState(String text) {
		 tv.setText(text);
			return tv;
		}
	 public ImageView setImageViewBackgroundGif(int imageId) {
		 Glide.with(context).load(imageId).asGif().fitCenter().into(img);
			return img;
		}
	 public ImageView setImageViewBackground(int imageId) {
		 Glide.with(context).load(imageId).asBitmap().fitCenter().into(img);
			return img;
		}
	
	}


