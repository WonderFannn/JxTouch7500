package com.jinxin.jxtouchscreen.net.data;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.input.InputManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.view.KeyEventCompat;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.jinxin.jxtouchscreen.R;
import com.jinxin.jxtouchscreen.activity.LoginActivity;
import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.NetConstant;
import com.jinxin.jxtouchscreen.db.SPM;
import com.jinxin.jxtouchscreen.event.UpdateDataEvent;
import com.jinxin.jxtouchscreen.event.UpdateDataServiceDisconnectEvent;
import com.jinxin.jxtouchscreen.util.L;
import com.jinxin.jxtouchscreen.util.RootShellCmd;
import com.jinxin.jxtouchscreen.util.SysUtil;
import com.jinxin.jxtouchscreen.util.ToastUtil;
import com.jinxin.jxtouchscreen.widget.VoiceService;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;

public class UpdateDataService extends Service {

	//	public static final String TEST_IP = "192.168.191.9";
	public static final int PORT = 12305;

	private IoConnector dataConnector;
	private static IoSession mSession;

	//定义浮动窗口布局
	LinearLayout mFloatLayout;
	WindowManager.LayoutParams wmParams;
	//创建浮动窗口设置布局参数的对象
	WindowManager mWindowManager;

	Button mFloatView;

	@Override
	public void onCreate() {
		super.onCreate();
//		createFloatView();
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int onStartCommand(Intent intent, final int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (dataConnector == null)
					dataConnector = new NioSocketConnector();

				//断开连接后直接返回登录连接大网关的界面
				dataConnector.getFilterChain().addFirst("reconnect", new IoFilterAdapter() {
					@Override
					public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
						L.w("与大网关数据通道已断开");
						ToastUtil.showLong(BaseApp.getContext(), "与大网关数据通道已断开");
						EventBus.getDefault().post(new UpdateDataServiceDisconnectEvent());
						//跳转主界面
						stopService(new Intent(getApplicationContext(), VoiceService.class));
						Intent intent = new Intent();
						intent.setClass(UpdateDataService.this, LoginActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});

				KeepAliveFilter keepAliveFilter = new KeepAliveFilter(new KeepAliveMessageFactory() {
					@Override
					public boolean isRequest(IoSession ioSession, Object o) {
						return false;
					}

					@Override
					public boolean isResponse(IoSession ioSession, Object o) {
						if (o instanceof String) {
							String msg = (String) o;
							if (msg.endsWith("heart") && msg.contains("gatewayheartbeat")) {
								L.v("有心跳");
								L.d(msg);
								return true;
							}
						}
						return false;
					}

					@Override
					public Object getRequest(IoSession ioSession) {
						JSONObject jsonObj = new JSONObject();
						jsonObj.put("heartbeat", "gatewayheartbeat");
						return jsonObj;
					}

					@Override
					public Object getResponse(IoSession ioSession, Object o) {
						return o;
					}
				});

				keepAliveFilter.setRequestInterval(20);

				dataConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new UpdateDataCodecFactory()));
//				dataConnector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

				dataConnector.getFilterChain().addLast("heartbeat", keepAliveFilter);

				dataConnector.getSessionConfig().setReadBufferSize(1024 * 1024);

				dataConnector.setHandler(new UpdateDataHandler());

				dataConnector.setDefaultRemoteAddress(new InetSocketAddress(SPM.getStr(SPM.CONSTANT, NetConstant.KEY_GATEWAYIP, ""), PORT));

				ConnectFuture future = dataConnector.connect(new InetSocketAddress(
						SPM.getStr(SPM.CONSTANT, NetConstant.KEY_GATEWAYIP, ""), PORT));
				future.awaitUninterruptibly();
				if (future.isConnected()) {
					mSession = future.getSession();
					EventBus.getDefault().post(new UpdateDataEvent());
				} else {
//					EventBus.getDefault().post(new UpdateDataServiceDisconnectEvent());
				}
			}
		}).start();
		return 0;
	}

	public static synchronized IoSession getSession() {
		return mSession;
	}

	/**
	 * 普通请求
	 *
	 * @param area 请求动作
		*/
	public synchronized static void updateData(String area) {
		if (mSession != null && mSession.isConnected()) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("area", area);
			jsonObj.put("time", SysUtil.getNow2());
			mSession.write(jsonObj);
		}
	}

	/**
	 * 特殊请求--需自行封装数据
	 *
	 * @param jo 数据
	 */
	public synchronized static void updateSpecialData(JSONObject jo) {
		if (mSession != null && mSession.isConnected()) {
			mSession.write(jo);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dataConnector != null) {
			dataConnector.dispose();
		}
		L.w("停止数据更新服务");
		mSession = null;
		if (mFloatLayout != null) {
			//移除悬浮窗口
			mWindowManager.removeView(mFloatLayout);
		}
	}

	private void createFloatView() {
		wmParams = new WindowManager.LayoutParams();
		//获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
		//设置window type
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		//设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		//设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		//调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.START | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 0;

		//设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		//获取浮动窗口视图所在布局
		mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
		//添加mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
		//浮动窗口按钮
		mFloatView = (Button) mFloatLayout.findViewById(R.id.float_back_btn);

		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		//设置监听浮动窗口的触摸移动
		mFloatView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
						wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight();
						mWindowManager.updateViewLayout(mFloatLayout, wmParams);
						break;
					case MotionEvent.ACTION_UP:
						balancePos(wmParams);
						mWindowManager.updateViewLayout(mFloatLayout, wmParams);
						break;
				}

				return false;  //此处必须返回false，否则OnClickListener获取不到监听
			}
		});

		mFloatView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				L.d("回退");
//				SysUtil.execShellCmd("input keyevent 4");
				new RootShellCmd().simulateKey(4);
//				SysUtil.execShellCmd(4);
//				Instrumentation inst = new Instrumentation();
//				inst.sendKeyDownUpSync(4);
			}
		});
	}


	private void balancePos(WindowManager.LayoutParams wmParams) {
		int distanceX = Math.abs(wmParams.x - SysUtil.getScreenWidth());
		int distanceY = Math.abs(wmParams.y - SysUtil.getScreenHeight());
		if (distanceX <= distanceY) {
			if (wmParams.x > SysUtil.getScreenWidth() / 2) {
				wmParams.x = SysUtil.getScreenWidth() - mFloatView.getMeasuredWidth() / 2;
			} else {
				wmParams.x = 0;
			}
		} else {
			if (wmParams.y > SysUtil.getScreenHeight() / 2) {
				wmParams.y = SysUtil.getScreenHeight() - mFloatView.getMeasuredHeight() / 2;
			} else {
				wmParams.y = 0;
			}
		}
	}
}
