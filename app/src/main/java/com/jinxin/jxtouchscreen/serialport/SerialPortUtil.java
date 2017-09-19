package com.jinxin.jxtouchscreen.serialport;

import android.serialport.SerialPort;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.util.SysUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 串口操作类
 *
 * Created by sail on 2017/4/26 0026.
 */

public class SerialPortUtil {
    private String TAG = SerialPortUtil.class.getSimpleName();
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    //串口名
    //虚拟机使用,7500串口使用
    private String path = "/dev/ttyS3";
    //    STM32使用
//    private String path = "/dev/ttyS7";
    //波特率
    private int baudrate = 115200;
    private static SerialPortUtil portUtil;
    private OnDataReceiveListener onDataReceiveListener = null;
    private boolean isStop = false;

    public interface OnDataReceiveListener {
        public void onDataReceive(final byte[] buffer,final int index, final int packlen) throws InterruptedException;
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        mReadThread = new ReadThread();
        isStop = false;
        Log.d(TAG, "setOnDataReceiveListener: launch");
        mReadThread.start();
        onDataReceiveListener = dataReceiveListener;
    }

    public static SerialPortUtil getInstance() {
        if (null == portUtil) {
            portUtil = new SerialPortUtil();
            portUtil.onCreate();
        }
        return portUtil;
    }


    public boolean sendMessageByArea(String area) throws UnsupportedEncodingException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("area", area);
        jsonObj.put("time", SysUtil.getNow2());

        int type = 0;
        if (jsonObj.containsKey("area")) {
            switch (jsonObj.getString("area")) {
                case "alarm":
                case "state":
                    type = 9;
                    break;
                case LocalConstant.UPDATE:
                    type = 5;
                    break;
                case LocalConstant.GATEWAY_INFO:
                case LocalConstant.GATEWAY_LOG:
                    type = 4;
                    break;
                default:
                    type = 3;
                    break;
            }
        } else if (jsonObj.containsKey("heartbeat")) {
            type = 2;
        }
        byte[] typeBytes = intToBytes(type);
        byte[] contentBytes = jsonObj.toJSONString().getBytes("utf-8");
        byte[] lenBytes = intToBytes(contentBytes.length);
        byte[] buffer = new byte[8+contentBytes.length];
        System.arraycopy(typeBytes,0,buffer,0,4);
        System.arraycopy(lenBytes,0,buffer,4,4);
        System.arraycopy(contentBytes,0,buffer,8,contentBytes.length);
        return sendBuffer(2,buffer);
    }

    public boolean sendBuffer(int type, byte[] mBuffer) throws UnsupportedEncodingException {
        boolean result = true;
        Log.d(TAG, "sendBuffer ");

        byte[] mPackage = new byte[mBuffer.length+12];
        byte[] typeBytes = intToBytes(type);
        byte[] lengthBytes = intToBytes(mBuffer.length);

        mPackage[0] = mPackage[1] = "A".getBytes("utf-8")[0];
        System.arraycopy(typeBytes,0,mPackage,2,4);
        System.arraycopy(lengthBytes,0,mPackage,6,4);
        System.arraycopy(mBuffer,0,mPackage,10,mBuffer.length);
        mPackage[mPackage.length-2] = mPackage[mPackage.length-1] = "E".getBytes("utf-8")[0];

        try {
            if (mOutputStream != null) {
                mOutputStream.write(mPackage);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * 初始化串口信息
     */
    public void onCreate() {

        try {
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            Log.d(TAG, "ReadThread: launch");
//            super.run();
            // 定义一个包的最大长度,目前默认最大10M
            int maxLength = 10485760;
            byte[] buffer = new byte[maxLength];
            // 每次收到实际长度
            int available = 0;
            // 当前已经收到包的总长度
            int currentLength = 0;
            // 协议头长度10个字节（包头2，类型4,长度4）
            int headerLength = 10;

            while (!isInterrupted()) {
                try {
                    available = mInputStream.available();
                    if (available > 0) {
                        // 防止超出数组最大长度导致溢出
                        sleep(10);
                        if (available > maxLength - currentLength) {
                            available = maxLength - currentLength;
                        }
                        mInputStream.read(buffer, currentLength, available);
                        currentLength += available;
                        Log.d(TAG, "readbuffer: currentLength:"+currentLength);
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                int cursor = 0;
                // 如果当前收到包大于头的长度，则解析当前包
                while (currentLength >= headerLength) {
                    // 取到头部第一个字节
                    if ((buffer[cursor] != (byte) 0x41)
                            && (buffer[cursor+1] != (byte) 0x41)) {
                        --currentLength;
                        ++cursor;
                        Log.d(TAG, "包头为AA"+currentLength+"--"+cursor);
                        continue;
                    }
                    int contentLength = parseLen(buffer, cursor);
                    // 如果内容包的长度大于最大内容长度或者小于等于0，则说明这个包有问题，丢弃
                    if (contentLength <= 0 || contentLength > maxLength - 12) {
                        Log.d(TAG, "包有问题"+contentLength);
                        currentLength = 0;
                        break;
                    }
                    // 如果当前获取到长度小于整个包的长度，则跳出循环等待继续接收数据
                    int factPackLen = contentLength + headerLength + 2;
                    if (currentLength < factPackLen) {
                        Log.d(TAG, "currentlen:"+currentLength+"---factPackLen:"+factPackLen);
                        break;
                    }
                    // 一个完整包即产生  ,传给onDataReceive处理
                    try {
                        onDataReceiveListener.onDataReceive(buffer, cursor, factPackLen);
                        Log.d(TAG, "一个完整包即产生  ,传给onDataReceive处理");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    currentLength -= factPackLen;
                    cursor += factPackLen;
                    // 残留字节放入缓存头继续解析
                    if (currentLength > 0 && cursor > 0) {
                        System.arraycopy(buffer, cursor, buffer, 0, currentLength);
//                        currentLength = 0;
                        cursor = 0;
                    }
                }
            }
        }
    }

    public int parseLen(byte buffer[], int index) {

        byte[] bytes = new byte[4];
        System.arraycopy(buffer,index+6,bytes,0,4);
        int value= 0;
        //由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift= (4 - 1 - i) * 8;
            value +=(bytes[i] & 0x000000FF) << shift;//往高位游
        }
        return value;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
        }
    }


    private byte[] intToBytes( int value ){
        byte[] src = new byte[4];
        src[0] =  (byte) ((value>>24) & 0xFF);
        src[1] =  (byte) ((value>>16) & 0xFF);
        src[2] =  (byte) ((value>>8) & 0xFF);
        src[3] =  (byte) (value & 0xFF);
        return src;
    }

}