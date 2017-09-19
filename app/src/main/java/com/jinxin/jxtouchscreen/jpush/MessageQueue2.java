package com.jinxin.jxtouchscreen.jpush;

import com.jinxin.jxtouchscreen.util.L;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 消息队列
 *
 * @version V1.0
 * @author: Guan.yuxuan.CD
 * @date 2014-3-11 下午3:03:45
 */
public class MessageQueue2 {


	private static MessageQueue2 instance = null;

	private static BlockingQueue<BroadPushMessage> messageQueue = new LinkedBlockingQueue<BroadPushMessage>();
	private Lock lock = new ReentrantLock();
	private final Condition produceCondition = lock.newCondition();
	private final Condition consumeCondition = lock.newCondition();

	private static Integer maxMessageSize = 5000;
	private static long timeout = 500;

	private MessageQueue2() {

	}

	public static MessageQueue2 getInstance() {
		if (instance == null) {
			instance = new MessageQueue2();
			instance.initQueue(maxMessageSize, timeout);
			L.i("最大消息数" + maxMessageSize + ",超时" + timeout);
		}
		return instance;
	}

	public void initQueue(Integer maxMessageSize, long timeout) {
		this.maxMessageSize = maxMessageSize;
		this.timeout = timeout;
	}

	public boolean offer(BroadPushMessage message) {
		lock.lock();
		boolean status = false;
		try {
			int messageSize = messageQueue.size();
			while (messageSize == maxMessageSize) {
				produceCondition.await(timeout, TimeUnit.MILLISECONDS);
				L.w("待推送消息队列已满，增加新消息超时！");
				break;
			}
			messageSize = messageQueue.size();
			if (messageSize < maxMessageSize) {
				status = messageQueue.offer(message);
				L.e("offer 推送消息队列消息数为:" + messageQueue.size());
			}
			consumeCondition.signal();
		} catch (Exception e) {
			L.e("向消息队列添加推送消息失败:");
		} finally {
			lock.unlock();
		}

		return status;
	}

	public BroadPushMessage poll() {
		L.d("获取待推送消息");
		lock.lock();
		BroadPushMessage message = null;
		try {
			while (messageQueue.size() == 0) {
//				L.d("即时消息队列中待发送消息数量为 0，线程 " + Thread.currentThread().getName() + "进入等待状态");
				consumeCondition.await();
			}
			message = messageQueue.poll();
//			L.d("poll" + message.getContent() + ",推送消息队列消息数为:" + messageQueue.size());
			produceCondition.signal();
		} catch (Exception e) {
			L.e("从消息队列获取推送消息失败:");
		} finally {
			lock.unlock();
		}

		return message;
	}


	public Integer getMaxMessageSize() {
		return maxMessageSize;
	}

	public void setMaxMessageSize(Integer maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}


}
