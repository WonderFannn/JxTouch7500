package com.jinxin.jxtouchscreen.cmd;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

/**
 * Created by XTER on 2017/02/13.
 * 命令解码
 */
public class CmdCodecDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
//		if (ioBuffer.hasRemaining()) {
//			protocolDecoderOutput.write(ioBuffer.asInputStream());
//			return true;
//		}
//		return false;
		int type = ioBuffer.getInt();
		int len = ioBuffer.getInt();
		String cmdResponse = ioBuffer.getString(Charset.forName("utf-8").newDecoder());
		if (len > 0) {
			protocolDecoderOutput.write(cmdResponse);
		}else{
			protocolDecoderOutput.write("");
		}
		return true;
	}
}