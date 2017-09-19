package com.jinxin.jxtouchscreen.net.data;

import com.jinxin.jxtouchscreen.util.L;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

/**
 * Created by XTER on 2017/2/14.
 * 数据通信解码--处理粘包
 */
public class UpdateDataDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
		//标记位置，重置则从此处重置
		ioBuffer.mark();
		//报文类型
		int type = ioBuffer.getInt();
		//报文长度
		int len = ioBuffer.getInt();

		String typeStr = "";
		switch (type) {
			case 2:
				typeStr = "心跳";
				break;
			case 3:
				typeStr = "数据更新";
				break;
			case 4:
				typeStr = "网关信息";
				break;
			case 5:
				typeStr = "应用升级";
				break;
			default:
				typeStr = "伪心跳";
				break;
		}
		L.v(typeStr);

		if (type == 3 || type == 4 || type == 9) {
			//数据更新、网关信息
			if (len > ioBuffer.remaining()) {
				//若长度不够，则继续解析
				ioBuffer.reset();
				return false;
			} else {
				//否则视为解析完毕
				String content1 = ioBuffer.getString(Charset.forName("utf-8").newDecoder());
				protocolDecoderOutput.write(content1);
				return true;
			}
		} else if (type == 5) {
			//更新apk
			if (len > ioBuffer.remaining()) {
				//若长度不够，则继续解析
				int limit = ioBuffer.limit();
				int percent = (int) ((float) limit / len * 100);
//				NotificationUtil.getInstance().updateDownloadView(percent);
				L.v("文件进度：" + percent + "%");
				ioBuffer.reset();
				return false;
			} else {
				byte[] bytes = new byte[len];
				ioBuffer.get(bytes);
				protocolDecoderOutput.write(bytes);
				return true;
			}
		} else if (type == 2) {
			//心跳
			String content2 = ioBuffer.getString(Charset.forName("utf-8").newDecoder());
			protocolDecoderOutput.write(content2 + "heart");
			return true;
		} else {
			//其他
			String content2 = ioBuffer.getString(Charset.forName("utf-8").newDecoder());
			protocolDecoderOutput.write(content2);
			return true;
		}
	}

}
