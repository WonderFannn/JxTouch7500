package com.jinxin.jxtouchscreen.event;

/**
 * Created by zj on 2016/12/13 0013.
 */
public class ChangeSongEvent {
    private String songName;

    public ChangeSongEvent(String songName) {
        this.songName = songName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
