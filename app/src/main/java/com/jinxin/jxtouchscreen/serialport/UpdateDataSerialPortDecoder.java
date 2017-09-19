package com.jinxin.jxtouchscreen.serialport;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.net.data.UpdateDataHandler;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/6/28 0028.
 */
public class UpdateDataSerialPortDecoder {
    private static final String TAG = "UpdateDataSerialPortDecoder";
    private boolean success = false;
    private int type;
    private int length;
    private String contentString;
    private UpdateDataHandler mUpdateDataHandler;

    public UpdateDataSerialPortDecoder (byte[] buffer){

        byte[] typeBytes = new byte[4];
        System.arraycopy(buffer,0,typeBytes,0,4);
        type = bytesToInt(typeBytes);

        byte[] lengthBytes = new byte[4];
        System.arraycopy(buffer,4,lengthBytes,0,4);
        length = bytesToInt(lengthBytes);
//        if (length == buffer.length-10) {
            byte[] content = new byte[length];
            System.arraycopy(buffer, 8, content, 0, length);
            try {
                contentString = new String(content,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mUpdateDataHandler  = new UpdateDataHandler();
            success = true;
//        }else {
//            Log.d(TAG, "UpdateDataSerialPortDecoder:"+"初始化失败,长度不一致");
//        }


    }
    public void startDecode(){
        if (success){
            if (type == 3 || type == 4 || type == 9){
                JSONObject jsonObject = JSON.parseObject(contentString);
                mUpdateDataHandler.updateData(jsonObject);
            }else {

            }
        }
    }

    private int bytesToInt(byte[] typeBytes) {
        int sum = 0;
        for (byte b:typeBytes) {
            sum = (sum << 8) | (b & 0xff);
        }
        return sum;
    }
}
