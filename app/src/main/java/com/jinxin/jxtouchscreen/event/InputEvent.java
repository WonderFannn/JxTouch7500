package com.jinxin.jxtouchscreen.event;

/**
 * Created by zj on 2016/12/9 0009.
 */
public class InputEvent {
    private String inputString;

    public InputEvent(String inputString) {
        this.inputString = inputString;
    }

    public String getInputString() {
        return inputString;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }
}
