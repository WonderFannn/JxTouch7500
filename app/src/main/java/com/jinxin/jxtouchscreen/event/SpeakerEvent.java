package com.jinxin.jxtouchscreen.event;

/**
 * Created by zj on 2016/12/9.
 */
public class SpeakerEvent {
    StringBuilder speaker;

    public SpeakerEvent(StringBuilder speaker) {
        this.speaker = speaker;
    }

    public StringBuilder getSpeaker() {
        return speaker;
    }

    public void setSpeaker(StringBuilder speaker) {
        this.speaker = speaker;
    }
}
