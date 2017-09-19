package com.jinxin.jxtouchscreen.cmd;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by XTER on 2017/02/13.
 * 命令编解码
 */
public class CmdCodecFactory implements ProtocolCodecFactory {

	public ProtocolEncoder getEncoder(IoSession session) {
		return new CmdCodecEncoder();
	}

	public ProtocolDecoder getDecoder(IoSession session) {
		return new CmdCodecDecoder();
	}
}
