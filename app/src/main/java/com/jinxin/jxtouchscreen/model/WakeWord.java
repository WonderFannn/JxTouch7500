package com.jinxin.jxtouchscreen.model;

import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by HJK on 2017/4/18 0018.
 */

@Table(LocalConstant.WAKE_WORD)
public class WakeWord extends BaseModel{

    private int id; // 记录主键
    private String customerId; //
    private String wakeUpWord; //唤醒词
    private String status; //
    private Double operateTime; //

    public WakeWord() {
        super();
    }

    public WakeWord(int id, String customerId, String wakeUpWord, String status, Double operateTime) {
        super();
        this.id = id;
        this.customerId = customerId;
        this.wakeUpWord = wakeUpWord;
        this.status = status;
        this.operateTime = operateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getWakeUpWord() {
        return wakeUpWord;
    }

    public void setWakeUpWord(String wakeUpWord) {
        this.wakeUpWord = wakeUpWord;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Double operateTime) {
        this.operateTime = operateTime;
    }
}
