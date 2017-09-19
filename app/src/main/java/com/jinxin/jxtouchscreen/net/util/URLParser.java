package com.jinxin.jxtouchscreen.net.util;


public class URLParser {

	public final String host;
	public final String protocol;
	public final String path;
	public final int port;
	public final String url;

	public URLParser(String url) {
		this.url = url;
		if (url == null) {
			host = null;
			protocol = null;
			path = null;
			port = 0;
		} else {
			int flag = 0;
			if ((flag = url.indexOf("://")) > 0) {
				protocol = url.substring(0, flag);
				url = url.substring(flag + 3);
			} else {
				protocol = null;
			}

			if ((flag = url.indexOf("/")) > 0) {
				path = url.substring(flag);
				url = url.substring(0, flag);
			} else {
				path = "/";
			}

			if ((flag = url.indexOf(":")) > 0) {
				host = url.substring(0, flag);
				port = Integer.parseInt(url.substring(flag + 1));
			} else {
				host = url;
				port = 80;
			}
		}
	}
}
