package com.jinxin.jxtouchscreen.app;

import com.jinxin.jxtouchscreen.model.EventState;

/**
 * Created by XTER on 2017/02/27.
 * 状态管理
 */
public class EventStateManager {

	public interface StateChangedListener {
		void onStateChanged(EventState eventState);
	}

	private StateChangedListener stateChangedListener;

	private static class EventStateHolder {
		private static final EventStateManager INSTANCE = new EventStateManager();
	}

	public static EventStateManager getInstance() {
		return EventStateHolder.INSTANCE;
	}

	public boolean save(EventState eventState) {
		if (compare() > 0) {
			stateChangedListener.onStateChanged(eventState);
			return true;
		}
		return false;
	}

	public int compare() {
		return 0;
	}
}
