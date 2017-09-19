package com.jinxin.jxtouchscreen.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.app.BaseApp;

/**
 * 多button提示框
 * Created by HJK on 2017/4/13 0013.
 */

public class MessageBox extends Dialog {
    public static final int MB_NONE = 0;
    public static final int MB_OK = 1;
    public static final int MB_CANCEL = 2;
    private int result = MB_NONE;

    private Button button_ok;
    private Button button_cancel;

    public MessageBox(Context context, String title, String content, int type) {
        super(context, R.style.dialog);
        setContentView(BaseApp.getSkinManager().view(R.layout.relogin_message_layout));

        TextView textview_title = (TextView)findViewById(R.id.textview_title);
        textview_title.setText(title.trim());

        TextView textview_content = (TextView)findViewById(R.id.textview_content);
        textview_content.setText(content.trim());

        this.button_ok = (Button)findViewById(R.id.button_ok);
        button_ok.setVisibility((type & MB_OK) == MB_OK ? View.VISIBLE : View.GONE);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = MB_OK;
                dismiss();
            }
        });
        this.button_cancel = (Button)findViewById(R.id.button_cancel);
        button_cancel.setVisibility((type & MB_CANCEL) == MB_CANCEL ? View.VISIBLE : View.GONE);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = MB_CANCEL;
                dismiss();
            }
        });
    }
    /**
     * 设置左右键文字
     * @param ok
     * @param cancel
     */
    public void setButtonText(String ok,String cancel){
        if(!TextUtils.isEmpty(ok)){
            this.button_ok.setText(ok);
        }
        if(!TextUtils.isEmpty(cancel)){
            this.button_cancel.setText(cancel);
        }
    }
    public int getResult(){
        return result;
    }
}

