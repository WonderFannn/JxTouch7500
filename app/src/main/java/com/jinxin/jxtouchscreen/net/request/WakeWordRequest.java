package com.jinxin.jxtouchscreen.net.request;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.constant.LocalConstant;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.model.WakeWord;
import com.jinxin.jxtouchscreen.net.base.BaseJsonRequest;
import com.jinxin.jxtouchscreen.net.base.Header;
import com.jinxin.jxtouchscreen.net.base.ICallback;
import com.jinxin.jxtouchscreen.util.AppUtil;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.SysUtil;

/**
 * Created by HJK on 2017/4/18 0018.
 */

public class WakeWordRequest extends BaseJsonRequest<WakeWord> {

    public WakeWordRequest(ICallback<WakeWord> callback) {
        super(callback);
    }

    @Override
    public Header createRequestHeader() {
        return new Header(NetConstant.BS_WAKE_WORD_BROWSE,
                NetConstant.TRD_WAKE_WORD_BROWSE, NetConstant.ACTION_REQUEST,
                SysUtil.getNow2(), NetConstant.TEST_TRUE);
    }

    @Override
    public JSONObject createRequestBody() throws JSONException {
        JSONObject serviceContent = new JSONObject();
        String password = SPM.getStr(SPM.CONSTANT, LocalConstant.KEY_PASSWORD, "");
        String account = AppUtil.getCurrentAccount();
        serviceContent.put("account", account);
        serviceContent.put("secretKey", password);
        return serviceContent;
    }

    @Override
    public void onResponse(JSONObject response) throws JSONException {
        if (response != null && response.containsKey("response")) {
            L.d(response.toString());
            JSONObject jo = response.getJSONObject("response");
            if (jo.getString("rspCode").equalsIgnoreCase("0000")) {
                if(TextUtils.isEmpty(response.getString("serviceContent")))
                    return;
                JSONObject jo1 = response.getJSONObject("serviceContent");
                WakeWord wakeWord = JSON.parseObject(jo1.toString(), WakeWord.class);
                getCallback().onReceive(wakeWord);
            } else
                getCallback().onError(jo.getString("rspDesc"));
        }
    }

    @Override
    public void onError(String error) {
        L.e(error);
    }
}
