package com.jinxin.jxtouchscreen.jpush;

public class BroadPushMessage {

    private String content;
    private String target;

    public BroadPushMessage(String content, String target) {
        this.content = content;
        this.target = target;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
    
}
