package com.jinxin.jxtouchscreen.cmd;

import com.jinxin.jxtouchscreen.util.L;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.io.InputStream;

/**
 * Created by XTER on 2017/01/20.
 * 命令应答解析
 */
public abstract class CmdResponse extends IoHandlerAdapter {
	public abstract void response(InputStream is);

	public abstract void onCmdResponse(String result);

	public abstract byte[] toOutputBytes();

	private RemoteJsonResultInfo resultInfo;

	public RemoteJsonResultInfo getResultInfo() {
		return this.resultInfo;
	}

	public void setResultInfo(RemoteJsonResultInfo resultInfo) {
		this.resultInfo = resultInfo;
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
//		response((InputStream) message);
		L.i((String) message);
		onCmdResponse((String) message);
		session.close(true);
	}

}
