package com.chengning.fenghuo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuo.data.bean.PushUserDataBean;
import com.chengning.fenghuo.data.bean.UserInfoBean;
import com.chengning.fenghuo.util.JUrl;
import com.google.gson.Gson;

public class PushManager {

	private static PushManager pushManager = null;

	public static final String STR_0 = "0";
	public static final String STR_MORE = "...";

	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_AT_NEW = 1; // @我的
	public static final int TYPE_COMMENT_NEW = 2; // 评论
	public static final int TYPE_DIG_NEW = 3; // 赞我的
	public static final int TYPE_DYNAMIC_NEW = 4; // 动态
	public static final int TYPE_FANS_NEW = 5; // 关注
	public static final int TYPE_PM_NEW = 6; // 消息
	public static final int TYPE_QUN_NOTICE_NEW = 7;
	public static final int TYPE_NOTICE_NEW = 8;
	public static final int TYPE_MY_NEW = 9;

	public static final int[] ARRAY_TYPE = { TYPE_AT_NEW, TYPE_COMMENT_NEW,
			TYPE_DIG_NEW, TYPE_DYNAMIC_NEW, TYPE_FANS_NEW, TYPE_PM_NEW,
			TYPE_QUN_NOTICE_NEW, TYPE_NOTICE_NEW, TYPE_MY_NEW };

	private static final int TIME_ADD_LISTENER_EVENT_DELAYED = 2;
	private static final int WAIT_TIME_BEFORE = 2000;
	// 从服务器请求的间隔时间
	private static final int INTERVAL = 60 * 1000;

	private static final int MSG_GET_PUSH_DATA = 1;

	private HashSet<Integer> mChangedTypes;
	private HashMap<Integer, HashSet<PushEventListener>> mListenerMap;

	private Context mContext;
	private Handler mHandler;
	private PushThread mThread;
	private PushUserDataBean mBean;

	private boolean hasInited = false;

	/**
	 * @deprecated
	 */
	public static PushManager getInstance() {
		if (pushManager == null) {
			pushManager = new PushManager();
		}
		return pushManager;
	}

	private PushManager() {
		mChangedTypes = new HashSet<Integer>();
		mListenerMap = new HashMap<Integer, HashSet<PushEventListener>>();
	}

	public void init(Context context) {
		mContext = context;
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_GET_PUSH_DATA:
					getPushData();
					break;
				}
			}
		};

		destroy();
		mThread = new PushThread();
		mThread.start();
		mThread.runing = true;

		boolean hasListener = false;
		Iterator iterator = mListenerMap.keySet().iterator();
		while (iterator.hasNext() && !hasListener) {
			Integer key2 = (Integer) iterator.next();
			hasListener = mListenerMap.get(key2).size() != 0;
		}
		mThread.pause = !hasListener;

		hasInited = true;
	}

	public void destroy() {
		if (mThread != null) {
			mThread.runing = false;
			mThread.pause = true;
			mThread = null;
		}
		hasInited = false;
	}

	public PushUserDataBean getPushUserDataBean() {
		return mBean;
	}

	public void addListener(final int type, final PushEventListener listener) {
		Integer key = Integer.valueOf(type);
		if (!mListenerMap.containsKey(key)) {
			HashSet<PushEventListener> set = new HashSet<PushManager.PushEventListener>();
			mListenerMap.put(key, set);
		}
		mListenerMap.get(key).add(listener);
		if (!hasInited) {
			return;
		}
		mThread.pause = false;

		if (mBean != null && mContext != null) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (mContext != null && listener != null) {
						forceHandlePushData(type, mBean);
					}
				}
			}, TIME_ADD_LISTENER_EVENT_DELAYED);
		}
	}

	public void removeListener(int type, PushEventListener listener) {
		Integer key = Integer.valueOf(type);
		if (mListenerMap.containsKey(key)) {
			HashSet<PushEventListener> set = mListenerMap.get(key);
			if (set.contains(listener)) {
				set.remove(listener);
			}
		}
		boolean hasListener = false;
		Iterator iterator = mListenerMap.keySet().iterator();
		while (iterator.hasNext() && !hasListener) {
			Integer key2 = (Integer) iterator.next();
			hasListener = mListenerMap.get(key2).size() != 0;
		}
		if (!hasInited) {
			return;
		}
		mThread.pause = !hasListener;
	}

	private int notifyPushEvent(final int type, final PushUserDataBean bean) {
		Integer key = Integer.valueOf(type);
		if (mListenerMap.containsKey(key)) {
			HashSet<PushEventListener> set = mListenerMap.get(key);
			Iterator iterator = set.iterator();
			while (iterator.hasNext()) {
				final PushEventListener listener = (PushEventListener) iterator
						.next();
				if (listener != null) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							listener.onEvent(type, bean);
						}
					});
				}
			}
			return set.size();
		} else {
			return 0;
		}
	}

	/**
	 * 定时向服务器请求
	 * 
	 * @author swh
	 * 
	 */
	private class PushThread extends Thread {
		private boolean runing = true;
		private boolean pause = true;

		@Override
		public void run() {
			try {
				Thread.sleep(WAIT_TIME_BEFORE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (runing) {
				if (pause) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				} else {
					mHandler.obtainMessage(MSG_GET_PUSH_DATA).sendToTarget();
				}
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getPushData() {
		if (!App.getInst().isLogin()) {
			return;
		}
		UserInfoBean uBean = App.getInst().getUserInfoBean();
		String url = JUrl.SITE + JUrl.URL_PUSH;
		url = JUrl.appendUid(url, uBean.getUid());
		HttpUtil.get(url.toString(), null, new MyJsonHttpResponseHandler() {

			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				Gson gson = new Gson();
				PushUserDataBean bean = gson.fromJson(data,
						PushUserDataBean.class);
				handlePushData(bean);
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				// do thing.
			};

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				// do thing.
			}
		});
	}

	private void handlePushData(PushUserDataBean bean) {
		if (mContext == null) {
			destroy();
			return;
		}
		if (bean == null) {
			return;
		}

		mBean = bean;
		int handled = 0;
		mChangedTypes.clear();

		for (int type : ARRAY_TYPE) {
			handled += notifyPushEvent(type, bean);
		}
	}

	public void forceHandlePushData(int type, PushUserDataBean bean) {
		if (mContext == null) {
			destroy();
			return;
		}
		if (bean == null) {
			return;
		}

		int handled = 0;

		for (int t : ARRAY_TYPE) {
			if (t == type) {
				handled += notifyPushEvent(t, bean);
			}
		}
	}

	public static String minusValue(String s, String t) {
		String ret = STR_0;
		if (TextUtils.isEmpty(t)) {
			ret = s;
		} else if (!TextUtils.isEmpty(s) && !STR_0.equals(s)
				&& !STR_0.equals(t)) {
			try {
				int sn = Integer.parseInt(s);
				int tn = Integer.parseInt(t);
				int retn = sn - tn;
				if (retn <= 0) {
					ret = STR_0;
				} else {
					ret = Integer.toString(retn);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public abstract static class SimplePushEventListener implements
			PushEventListener {
		private TextView mRedDot;
		private View[] mOther;

		public SimplePushEventListener(TextView red, View... other) {
			this.mRedDot = red;
			this.mOther = other;
		}

		public abstract String configValue(int type, PushUserDataBean bean);

		@Override
		public int onEvent(int type, PushUserDataBean bean) {
			String value = configValue(type, bean);
			if (PushManager.STR_0.equals(value) || TextUtils.isEmpty(value)) {
				if (mOther != null) {
					for (View v : mOther) {
						v.setVisibility(View.VISIBLE);
					}
				}
				mRedDot.setVisibility(View.INVISIBLE);
			} else {
				if (value.length() > 2) {
					value = PushManager.STR_MORE;
				}

				if (mOther != null) {
					for (View v : mOther) {
						v.setVisibility(View.INVISIBLE);
					}
				}
				mRedDot.setVisibility(View.VISIBLE);
				mRedDot.setText(value);
			}
			return 0;
		}
	}

	public static interface PushEventListener {
		public int onEvent(int type, PushUserDataBean bean);
	}
}
