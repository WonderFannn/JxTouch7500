package com.jinxin.jxtouchscreen;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
	@Test
	public void addition_isCorrect() throws Exception {

		assertEquals(4, 2 + 2);
		System.out.println("testt"+0x41+"ttt:::::"+"20_zAaBAA".getBytes("utf-8")[0]);
		System.out.println("123456789".substring(0,3));
		System.out.println("123456789".substring(3,5));

		byte[] b = intToBytes(77471747);
		int c = bytesToInt(b);
		System.out.println(c);


	}

	private byte[] intToBytes( int value ){
		byte[] src = new byte[4];
		src[0] =  (byte) ((value>>24) & 0xFF);
		src[1] =  (byte) ((value>>16) & 0xFF);
		src[2] =  (byte) ((value>>8) & 0xFF);
		src[3] =  (byte) (value & 0xFF);
		return src;
	}

	private int bytesToInt(byte[] typeBytes) {
		int sum = 0;
		for (byte b:typeBytes) {
			sum = (sum << 8) | (b & 0xff);
		}
		return sum;
	}
}