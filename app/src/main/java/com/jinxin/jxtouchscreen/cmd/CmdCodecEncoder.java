package com.jinxin.jxtouchscreen.cmd;

import com.jinxin.jxtouchscreen.util.L;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Created by XTER on 2017/02/13.
 * 编码
 */
public class CmdCodecEncoder implements ProtocolEncoder {

	@Override
	public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
		L.d("编码不做任何操作");
	}

	@Override
	public void dispose(IoSession ioSession) throws Exception {

	}
}
