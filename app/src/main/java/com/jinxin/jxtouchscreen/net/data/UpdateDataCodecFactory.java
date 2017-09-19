package com.jinxin.jxtouchscreen.net.data;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;


/**
 * Created by XTER on 2017/2/14.
 * 数据通信协议
 */
public class UpdateDataCodecFactory implements ProtocolCodecFactory {
	@Override
	public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
		return new UpdateDataEncoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
		return new UpdateDataDecoder();
	}
}
