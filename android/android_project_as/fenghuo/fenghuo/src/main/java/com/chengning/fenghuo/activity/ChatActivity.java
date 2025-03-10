package com.chengning.fenghuo.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.util.PermissionManager;
import com.chengning.common.util.HttpUtil;
import com.chengning.common.util.ThreadHelper;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.Consts;
import com.chengning.fenghuo.Consts.ChatAction;
import com.chengning.fenghuo.LoginManager;
import com.chengning.fenghuo.MyJsonHttpResponseHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.adapter.ChatMsgListAdapter;
import com.chengning.fenghuo.adapter.EmjoyGridAdapter;
import com.chengning.fenghuo.adapter.EmjoyGridViewPaperAdapter;
import com.chengning.fenghuo.data.access.ChatConversationDA;
import com.chengning.fenghuo.data.access.ChatMessageDA;
import com.chengning.fenghuo.data.access.UserinfoOtherServer;
import com.chengning.fenghuo.data.bean.ChatConversationBean;
import com.chengning.fenghuo.data.bean.ChatMessageBean;
import com.chengning.fenghuo.data.bean.UserInfoBean;
import com.chengning.fenghuo.emotion.weibo.Emotion;
import com.chengning.fenghuo.emotion.weibo.Emotions;
import com.chengning.fenghuo.emotion.weibo.EmotionsDB;
import com.chengning.fenghuo.util.ChatClientManager;
import com.chengning.fenghuo.util.ChatClientManager.MessageListener;
import com.chengning.fenghuo.util.ChatClientManager.QueryListener;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.EmotionHelper;
import com.chengning.fenghuo.util.ImagePickHelper;
import com.chengning.fenghuo.util.JUrl;
import com.chengning.fenghuo.util.NotificationUtils;
import com.chengning.fenghuo.util.UIHelper;
import com.chengning.fenghuo.util.Utils;
import com.chengning.fenghuo.widget.ChatSettingDialog;
import com.chengning.fenghuo.widget.ChatSettingDialog.ClearHistoryListener;
import com.chengning.fenghuo.widget.DynamicSettingDialog;
import com.chengning.fenghuo.widget.PullToRefreshListView_FootLoad;
import com.chengning.fenghuo.widget.TitleBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.RequestParams;

public class ChatActivity extends BaseFragmentActivity {

	private final static int EmJoyChange = 4;
	private static final int IMAGE_REQUEST_CODE = 12; // 请求码 本地图片
	private static final int CAMERA_REQUEST_CODE = 14; // 拍照

	private static final int DATA_FETCH_SUCCESS = 0;
	private static final int DATA_ADD_SUCCESS = 1;
	private static final int DATA_FAILTURE = 2;
	private final static int EmjoyRowCount = 6;
	private static final int DATA_FETCH_HISTORY_SUCCESS = 5;
	private static final int DATA_SET_CONNAME = 7;
	
	private static String mChatImgName;

	private PullToRefreshListView_FootLoad mListView;
	private TextView mSendBtn;
	private EditText mInputEdit;
	private List<ChatMessageBean> messageLists;
	private ChatClientManager mClientManager;
	protected ChatClientManager.MessageListener listener;
	private ChatMsgListAdapter adapter;
	private BroadcastReceiver receiver;
	private ImageButton mAlbumBtn;

	private TitleBar mTitleBar;
	private String mChatName;
	private String mMemberId;
	private String mConversationId;
	private ChatAction mAction;
	private ChatConversationBean mConversation;

	private boolean mIsFirst = true;
	private OnClickListener sendListner;
	private ImageButton mEmjoyBtn;
	private RelativeLayout mCameraBtn;
	private RelativeLayout mPicLibBtn;
	private LinearLayout mNunLayout;
	private RelativeLayout mEmjoyRl;
	private LinearLayout mPicRl;
	private ViewPager mEmjoyPager;

	private String[] mContentEtStr = new String[1];
	protected Button mPreSelectedBt;
	protected int mBottomState;
	private EmjoyGridViewPaperAdapter mEmjoyPaperAdapter;

	private ImagePickHelper mPickHelper;
	private UserInfoBean mUserBean;
	private UserInfoBean mTargetBean;

	// private ArrayList<String> mDelIds;
	private Vibrator vibrator;
	protected ChatSettingDialog mSettingDialog;

	private int mOffset = 0;
	
	//TODO 留言开关（0：关 1：开）ios需要，android不需要
	protected int chatState = 0;
	
	private HandlerThread mChatThread;
	private PermissionManager permissionManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_chat);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {

		IntentFilter filter = new IntentFilter();
		filter.addAction(getPackageName() + "ACTION_RECEIVE_MSG");
		registerReceiver(receiver, filter);

		if (null != mConversation) {
			NotificationUtils.addTag(mConversation.getCon_id());
		}

		super.onResume();
	}

	@Override
	public void onPause() {
		unregisterReceiver(receiver);
		if (null != mConversation) {
			NotificationUtils.removeTag(mConversation.getCon_id());
		}
		super.onPause();
	}

	@Override
	public Activity getActivity() {
		return ChatActivity.this;
	}

	@Override
	public void initViews() {
		mTitleBar = new TitleBar(getActivity(), true);
		mTitleBar.showDefaultBack();
		mTitleBar.setRightButton(R.drawable.nav_more);
		mListView = (PullToRefreshListView_FootLoad) findViewById(R.id.chat_list);
		mSendBtn = (TextView) findViewById(R.id.chat_send);
		mAlbumBtn = (ImageButton) this.findViewById(R.id.chat_album);
		mInputEdit = (EditText) findViewById(R.id.chat_input);
		mEmjoyBtn = (ImageButton) this.findViewById(R.id.chat_emjoy);
		mCameraBtn = (RelativeLayout) this.findViewById(R.id.chat_camera_btn);
		mPicLibBtn = (RelativeLayout) this.findViewById(R.id.chat_piclib_btn);

		mNunLayout = (LinearLayout) this.findViewById(R.id.chat_emjoydotlist);
		mEmjoyRl = (RelativeLayout) this.findViewById(R.id.chat_emjoy_rl);

		mPicRl = (LinearLayout) this.findViewById(R.id.chat_imgrl);
		mEmjoyPager = (ViewPager) this.findViewById(R.id.chat_emjoygrid);

	}

	@Override
	public void initDatas() {

		if (!LoginManager.getInst().checkLoginWithNotice(getActivity())) {
			finish();
			return;
		}
		mClientManager = ChatClientManager.getInst();

		mAction = (ChatAction) getIntent().getSerializableExtra(
				Consts.CHAT_ENTRY);

		vibrator = (Vibrator) getActivity().getSystemService(
				Service.VIBRATOR_SERVICE);

		mPickHelper = new ImagePickHelper();
		InitEmJoyData();
		initListener();

		messageLists = new ArrayList<ChatMessageBean>();
		adapter = new ChatMsgListAdapter(getActivity(), messageLists,
				sendListner);
		mListView.setAdapter(adapter);
		handleIsOnline();

	}

	private void initListener() {
		// mInputEdit.addTextChangedListener(EmotionHelper.generateTextWatcher(
		// mInputEdit, mContentEtStr));
		// 更换表情
		mInputEdit.setFilters(new InputFilter[] {
				EmotionHelper.generateEmotionFilter(),
				EmotionHelper.generateHyperlinkFilter() });

		sendListner = new OnClickListener() {

			@Override
			public void onClick(View v) {
				AVIMTextMessage message = (AVIMTextMessage) v.getTag();
				mClientManager.sendMessage(mConversation.getCon_id(), message,
						listener);
			}
		};

		listener = new MessageListener() {

			@Override
			public void sendSuccess(AVIMTypedMessage msg) {
				// notifyAdapter();
				mSendBtn.setEnabled(true);
				updateConversation(mConversation, msg, false, true);
			}

			@Override
			public void handleFailure(AVIMException e) {
				// switch (e.getCode()) {
				// // case 0:
				// // mCientManager.close(true);
				// // finish();
				// // break;
				// case 119:
				// mCientManager.close(true);
				// finish();
				// break;
				//
				// default:
				// getHandler().obtainMessage(DATA_FAILTURE).sendToTarget();
				// break;
				// }
				mSendBtn.setEnabled(true);
				getHandler().obtainMessage(DATA_FAILTURE).sendToTarget();
			}

			@Override
			public void fetchSuccess(List<ChatMessageBean> list) {
				// mConversation = conversation;
				if (null != list) {
					getHandler().obtainMessage(DATA_FETCH_SUCCESS, list)
							.sendToTarget();
				}

			}

			@Override
			public void fetchHistorySuccess(List<ChatMessageBean> secondPage) {
				if (0 == secondPage.size()) {
					UIHelper.showToast(getActivity(), "无历史消息了");
				}
				getHandler().obtainMessage(DATA_FETCH_HISTORY_SUCCESS,
						secondPage).sendToTarget();
			}

			@Override
			public void querySuccess(List<ChatConversationBean> conversations) {
				// TODO Auto-generated method stub

			}
		};

		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if ((getPackageName() + "ACTION_RECEIVE_MSG").equals(intent
						.getAction())) {
					AVIMTypedMessage message = (AVIMTypedMessage) intent
							.getParcelableExtra("msg");
					if (message.getConversationId().equals(
							mConversation.getCon_id())) {
						updateConversation(mConversation, message, false);

						vibrator.vibrate(500);
					}

				}

			}
		};
	}

	private void updateConversation(ChatConversationBean mConversation,
			AVIMTypedMessage msg, boolean isInited) {
		updateConversation(mConversation, msg, isInited, false);
	}

	protected void updateConversation(final ChatConversationBean mConversation,
			AVIMTypedMessage message, boolean isInited, boolean isUpdated) {
		final ChatMessageBean msgBean = ChatMessageBean.convertToMessageBean(message);
		messageLists.add(msgBean);
		// int location = 0;
		// if (isUpdated) {
		// msgBean = ChatMessageBean.convertToMessageBean(message);
		// for (ChatMessageBean bean : messageLists) {
		// if (bean.getMsg_id().equals(msgBean.getMsg_id())) {
		// location = messageLists.indexOf(bean);
		// break;
		// }
		// }
		// messageLists.set(location, msgBean);
		// } else {
		// if (!isInited) {
		// msgBean = ChatMessageBean.convertToMessageBean(message);
		// } else {
		// msgBean = ChatMessageBean.initMessageBean(mConversation, message);
		// }
		//
		// messageLists.add(msgBean);
		// }
		notifyAdapter();
		
		if (mChatThread == null) {
			mChatThread = ThreadHelper.creatThread("my_chat");
		}
		ThreadHelper.handle(mChatThread, new Runnable() {
			
			@Override
			public void run() {
				ChatMessageDA.getInst(getActivity()).creatMsgList(msgBean);
				ChatConversationDA.getInst(getActivity()).updateLatestMessage(
						mConversation, msgBean);
			}
		});
		
	}

	private void initClientData(ChatAction action) {

		switch (action) {
		case LIST:

			mConversationId = getIntent()
					.getStringExtra(Consts.CONVERSATION_ID);
			if (!TextUtils.isEmpty(mConversationId)) {
				NotificationUtils.addTag(mConversationId);
				mConversation = ChatConversationDA.getInst(getActivity())
						.queryConversation(
								App.getInst().getUserInfoBean().getUid(),
								mConversationId);
				ArrayList<String> list = mConversation.getCon_members();
				for (String id : list) {
					if (!mConversation.getUid().equals(id)) {
						mMemberId = id;
						break;
					}
				}
				// mConversation =
				// mClientManager.getClient().getConversation(mConversationId);
//				Gson gson = new Gson();
//				ConversationBean bean = null;
//
//				AVIMConversation con = mClientManager.getmClient()
//						.getConversation(mConversation.getCon_id());
//				if (mConversation.getCreator().equals(App.getInst().getUserInfoBean().getUid())) {
//					bean = gson.fromJson(
//							(String) con.getAttribute("target_bean"),
//							ConversationBean.class);
//
//				} else {
//					bean = gson.fromJson(
//							(String) con.getAttribute("creat_bean"),
//							ConversationBean.class);
//				}

//				mMemberId = bean.getUid();
				mClientManager.fetchMessages(getActivity(),
						mConversation.getCon_id(), mOffset, listener);
				getHandler().obtainMessage(DATA_SET_CONNAME, mConversation.getCon_name()).sendToTarget();
//				initUser(mMemberId);
			}
			break;
		case USER_INFO:

			mMemberId = getIntent().getStringExtra(Consts.MEMBER_ID);
			mChatName = getIntent().getStringExtra("name");
			getHandler().obtainMessage(DATA_SET_CONNAME, mChatName).sendToTarget();
//			mTitleBar.setTitle(mChatName);
			ArrayList<String> members = new ArrayList<String>();
			members.add(mMemberId);
			members.add(App.getInst().getUserInfoBean().getUid());
			mClientManager.queryConversationByMem(members, new QueryListener() {
				
				@Override
				public void sussess() {
					String id = mClientManager.getConversation()
							.getConversationId();
					mConversation = ChatConversationDA.getInst(getActivity())
							.queryConversation(
									App.getInst().getUserInfoBean().getUid(),
									id);

					NotificationUtils.addTag(id);
					mClientManager.fetchMessages(getActivity(), id, mOffset,
							listener);
				}
				
				@Override
				public void failure() {
					
				}
			}, listener);
//			mClientManager.querySingleConversation(mMemberId, new Runnable() {
//
//				@Override
//				public void run() {
//					
//				}
//			}, listener);

			break;

		default:
			break;
		}
		initUser(mMemberId);
	}

	/**
	 * 处理聊天是否下线
	 */
	private void handleIsOnline() {
		if (null == mClientManager.getmClient()) {
			mClientManager.handleOfflineHint(getActivity());
			finish();
			return;
		}
		// mClientManager.getmClient().getClientStatus(
		// new AVIMClientStatusCallback() {
		//
		// @Override
		// public void done(AVIMClientStatus status) {
		// if (null == status) {
		// // mCientManager.close(true);
		// mClientManager.handleOfflineHint(getActivity());
		// finish();
		// } else {
		if (mChatThread == null) {
			mChatThread = ThreadHelper.creatThread("my_chat");
		}
		ThreadHelper.handle(mChatThread, new Runnable() {
			
			@Override
			public void run() {
				initClientData(mAction);
			}
		});
		
		// }
		// }
		// });
	}

	private void initUser(String id) {
		mUserBean = App.getInst().getUserInfoBean();
		if (!TextUtils.isEmpty(mMemberId)) {
			mTargetBean = UserinfoOtherServer.getInst(getActivity())
					.queryTargetUid(id);
		}
		
		//TODO 配合ios(android无用，不可删)
		getState(id);

	}

	private void getState(String id) {
		RequestParams params = new RequestParams();
		params.put("uid", id);
		HttpUtil.get(JUrl.SITE + JUrl.URL_GET_STATE_IOS, params, new MyJsonHttpResponseHandler() {
			
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				chatState  = status;
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				// TODO Auto-generated method stub
				chatState = status;
			}
		});
	}

	protected void notifyAdapter() {
		getHandler().obtainMessage(DATA_ADD_SUCCESS).sendToTarget();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void installListeners() {

		mTitleBar.setRightButtonOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSettingDialog = new ChatSettingDialog();
				mSettingDialog.setData(mConversation);
				mSettingDialog.setListener(new ClearHistoryListener() {

					@Override
					public void success() {
						// TODO Auto-generated method stub
						messageLists.clear();
						adapter.notifyDataSetChanged();
						finish();
					}
				});
				mSettingDialog.show(getSupportFragmentManager(),
						DynamicSettingDialog.class.getSimpleName());
				// 小米手机上UI概率显示不全，强制延时刷新UI
				getHandler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (mSettingDialog != null) {
							mSettingDialog.shouldRefreshUI();
						}
					}
				}, 100);

			}
		});

		mSendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSendBtn.setEnabled(false);
				String string = mInputEdit.getText().toString();
				handleTextMsg(string);

			}
		});
		mInputEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			}
		});
		mInputEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int textLength = mInputEdit.getText().length();
				if (textLength > 0) {
					mSendBtn.setEnabled(true);
				} else {
					mSendBtn.setEnabled(false);
				}
			}
		});
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (null != mConversation) {
					queryHistory();
				} else {
					getHandler().obtainMessage(DATA_FAILTURE).sendToTarget();
				}

			}
		});

		mEmjoyPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				if (mPreSelectedBt != null) {
					mPreSelectedBt.setBackgroundResource(R.drawable.emjoy_tab1);
				}

				Button currentBt = (Button) mNunLayout.getChildAt(position);
				currentBt.setBackgroundResource(R.drawable.emjoy_tab2);
				mPreSelectedBt = currentBt;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		permissionManager = new PermissionManager();
		mCameraBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					UIHelper.showToast(getActivity(),
							getResources().getString(R.string.error_sdcard));
				} else {
					StringBuffer buffer = new StringBuffer();
					buffer.append(System.currentTimeMillis()).append("_")
							.append("fenghuoChat.jpg");
					mChatImgName = buffer.toString();
					mPickHelper.handleCameraClick(getActivity(), mChatImgName, permissionManager);
				}
			}
		});

		mPicLibBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					UIHelper.showToast(getActivity(),
							getResources().getString(R.string.error_sdcard));
				} else {
					mPickHelper.handleAbulmClick(getActivity(), permissionManager);
				}
			}
		});

		mAlbumBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBottomState != 2) {
					mBottomState = 2;
				} else {
					mBottomState = 0;
				}
				ShowKeyBoardOrEmjoy(mBottomState);
			}
		});

		mEmjoyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBottomState != 1) {
					mBottomState = 1;
				} else {
					mBottomState = 0;
				}
				ShowKeyBoardOrEmjoy(mBottomState);
			}
		});

		mInputEdit.setOnTouchListener(new OnTouchListener() {
			boolean ismove = false;
			int x = 0;
			int y = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					x = 0;
					y = 0;
					x = (int) event.getX();
					y = (int) event.getY();
					break;
				case MotionEvent.ACTION_UP:
					if (!ismove) {
						mBottomState = 0;
						ShowKeyBoardOrEmjoy(mBottomState);
					}
					ismove = false;
					break;
				case MotionEvent.ACTION_MOVE:
					int a = (int) Math.abs(event.getX() - x);
					int b = (int) Math.abs(event.getY() - y);
					if (a > 20 || b > 20) {
						ismove = true;
					}
					break;
				}
				return false;
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		permissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
	}
	
	void queryHistory() {
		if (mChatThread == null) {
			mChatThread = ThreadHelper.creatThread("my_chat");
		}
		ThreadHelper.handle(mChatThread, new Runnable() {
			
			@Override
			public void run() {
				mClientManager.queryHistoryMessage(getActivity(),
						mConversation.getCon_id(), mOffset, listener);
			}
		});
		
	}

	/**
	 * 处理文本消息
	 * 
	 * @param string
	 */
	protected void handleTextMsg(String string) {
		if (null == mUserBean) {
			mSendBtn.setEnabled(true);
			return;
		}
		AVIMTextMessage msg = (AVIMTextMessage) new AVIMTextMessage();
		msg.setText(string);
		msg.setFrom(mUserBean.getUid());
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("face", mUserBean.getFace());
		attributes.put("name", mUserBean.getNickname());
		attributes.put("sw", chatState);
		msg.setAttrs(attributes);
		sendMessage(msg);
	}

	/**
	 * 发送聊天消息
	 * 
	 * @param msg
	 */
	protected void sendMessage(final AVIMTypedMessage msg) {
//		AVIMConversation con = mClientManager.getmClient().getConversation(
//				mConversation.getCon_id());
		 if (null != mConversation) {
		 mClientManager
		 .sendMessage(mConversation.getCon_id(), msg, listener);
		 // updateConversation(mConversation, msg, true);
		 } else {
		mClientManager.creatConversation(mMemberId, mChatName, mUserBean,
				mTargetBean, new Runnable() {
					public void run() {
						// mConversation = mClientManager.getConversation();
						String id = mClientManager.getConversation()
								.getConversationId();
						mConversation = ChatConversationBean.convertToBean(id);

						mClientManager.sendMessage(id, msg, listener);
						// updateConversation(mConversation, msg, true);
					}
				}, listener);
		 }

	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case DATA_FETCH_SUCCESS:
			messageLists.clear();

			messageLists.addAll((ArrayList<ChatMessageBean>) msg.obj);
			handleListSort(messageLists);
			adapter.notifyDataSetChanged();

			handleRefreshComplete();

			if (mIsFirst) {
				// scrollToPosition(adapter.getCount() - 1);
				// scrollToPosition(mListView.getBottom());
			}
			mOffset = Common.calculateOffset(messageLists);

			resetUnread(mConversation);

			break;
		case DATA_ADD_SUCCESS:
			adapter.notifyDataSetChanged();
			// scrollToPosition(mListView.getBottom());
			mInputEdit.setText("");
			break;
		case DATA_FAILTURE:
			handleRefreshComplete();
			Common.handleHttpFailure(getActivity(), null);
			break;
		case EmJoyChange:
			VisiableEmjoy(mBottomState);
			break;
		case DATA_FETCH_HISTORY_SUCCESS:
			ArrayList<ChatMessageBean> list = (ArrayList<ChatMessageBean>) msg.obj;
			messageLists.addAll(0, list);

			handleListSort(messageLists);

			adapter.notifyDataSetChanged();
			handleRefreshComplete();
			mOffset = Common.calculateOffset(messageLists);
			scrollToPosition(list.size() + 1);
			break;
		case DATA_SET_CONNAME:
			mTitleBar.setTitle((String)msg.obj);
			break;
		default:
			break;
		}

	}

	/**
	 * 未读消息清零
	 * @param bean
	 */
	private void resetUnread(final ChatConversationBean bean) {
		if (null == bean) {
			return;
		}
		if (mChatThread == null) {
			mChatThread =ThreadHelper.creatThread("my_chat");
		}
		ThreadHelper.handle(mChatThread, new Runnable() {
			
			@Override
			public void run() {
				ChatConversationDA.getInst(getActivity()).resetUnreadCount(
						bean);
			}
		});
		
	}

	/**
	 * 排序
	 * 
	 * @param list
	 */
	private void handleListSort(List<ChatMessageBean> list) {
		Collections.sort(list, new Comparator<ChatMessageBean>() {
			@Override
			public int compare(ChatMessageBean lhs, ChatMessageBean rhs) {
				Date date1 = Common.stringToDate(lhs.getTime());
				Date date2 = Common.stringToDate(rhs.getTime());
				// 对日期字段进行升序，如果欲降序可采用after方法
				if (date1.after(date2)) {
					return 1;
				}
				return -1;
			}
		});
	}

	private void handleRefreshComplete() {
		mListView.setFootLoadFull();
		mListView.onRefreshComplete();
	}

	// /**
	// * 更新消息列表信息
	// * @return
	// */
	// private ArrayList<AVIMTypedMessage>
	// updataMsgList(ArrayList<AVIMTypedMessage> list) {
	//
	// for (AVIMTypedMessage message : list) {
	// if (message.getFrom().equals(mUserBean.getUid())) {
	// handleMsgFace(message, mUserBean.getFace());
	//
	// } else if (null != mConversation) {
	// handleMsgFace(message, mConversation.getCon_face());
	// }
	// }
	// return list;
	// }

	/**
	 * 处理消息头像
	 * 
	 * @param message
	 * @param face
	 */
	private void handleMsgFace(AVIMTypedMessage message, String face) {
		Map<String, Object> attrs = null;
		switch (message.getMessageType()) {
		case AVIMMessageType.TEXT_MESSAGE_TYPE:
			if (null != (attrs = ((AVIMTextMessage) message).getAttrs())) {
				attrs.put("face", face);
			}
			break;
		case AVIMMessageType.IMAGE_MESSAGE_TYPE:
			if (null != (attrs = ((AVIMImageMessage) message).getAttrs())) {
				attrs.put("face", face);
			}
			break;

		default:
			break;
		}

	}

	private void scrollToPosition(final int pos) {

		mListView.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				mListView.setSelection(pos);
			}
		});
	}

	/**
	 * 是否聊天数据有改变
	 */
	protected boolean isChatChanged = false;
	private List<Emotion> mEmjoyItems;
	private ArrayList<GridView> mEmjoyPagerItems;

	protected void InitEmJoyData() {
		Emotions emotions = EmotionsDB.getEmotions();
		mEmjoyItems = emotions.getEmotions();
		mEmjoyPagerItems = new ArrayList<GridView>();
		List<List<Emotion>> emjoySpliteItems = Common.splitList(mEmjoyItems,
				EmjoyRowCount * 5);
		for (int i = 0; i < emjoySpliteItems.size(); i++) {
			GridView gv = new GridView(this);
			gv.setAdapter(new EmjoyGridAdapter(this, emjoySpliteItems.get(i), i));
			gv.setGravity(Gravity.CENTER);
			gv.setClickable(true);
			gv.setFocusable(true);
			gv.setSelector(R.color.transparent);
			gv.setNumColumns(6);
			gv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					EmjoyGridAdapter adapter = (EmjoyGridAdapter) parent
							.getAdapter();
					Emotion emotion = (Emotion) adapter.getItem(position);
					EmotionHelper.handleEmotionInsert(mInputEdit, emotion,Consts.EMOTION_SIZE_MIDDLE);
				}
			});
			mEmjoyPagerItems.add(gv);
		}
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.emjoy_tab1);
		for (int i = 0; i < mEmjoyPagerItems.size(); i++) {
			Button bt = new Button(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					bitmap.getWidth(), bitmap.getHeight());
			params.setMargins(5, 0, 5, 0);
			bt.setLayoutParams(params);
			bt.setBackgroundResource(R.drawable.emjoy_tab1);
			mNunLayout.addView(bt);
		}
		Button currentBt = (Button) mNunLayout.getChildAt(0);
		currentBt.setBackgroundResource(R.drawable.emjoy_tab2);
		mPreSelectedBt = currentBt;
		mEmjoyPaperAdapter = new EmjoyGridViewPaperAdapter(this,
				mEmjoyPagerItems);
		mEmjoyPager.setAdapter(mEmjoyPaperAdapter);
	}

	public void ShowKeyBoardOrEmjoy(int s) {
		mEmjoyBtn.setClickable(false);
		if (s == 2) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.showSoftInput(mInputEdit, 0);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mInputEdit.getWindowToken(), 0);
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mInputEdit.getWindowToken(), 0);
					getHandler().obtainMessage(EmJoyChange,
							mBottomState).sendToTarget();
				}
			}, 300);
		} else if (s == 1) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.showSoftInput(mInputEdit, 0);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mInputEdit.getWindowToken(), 0);
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mInputEdit.getWindowToken(), 0);
					getHandler().obtainMessage(EmJoyChange,
							mBottomState).sendToTarget();

				}
			}, 300);
		} else if (s == 0) {
			mPicRl.setVisibility(View.GONE);
			mEmjoyRl.setVisibility(View.GONE);
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					getHandler().obtainMessage(EmJoyChange,
							mBottomState).sendToTarget();
				}
			}, 300);
		}

	}

	/**
	 * 设置表情可见性
	 * 
	 * @param s
	 */
	public void VisiableEmjoy(int s) {
		mEmjoyBtn.setClickable(true);
		if (s == 2) {
			mEmjoyBtn.setImageResource(R.drawable.liaotian_toolbar_emjoy);
			// mAlbumBtn.setImageResource(R.drawable.toolbar_btn_key);
			mPicRl.setVisibility(View.VISIBLE);
			mEmjoyRl.setVisibility(View.GONE);
		} else if (s == 1) {
			// mAlbumBtn.setImageResource(R.drawable.liaotian_toolbar_photo);
			mEmjoyBtn.setImageResource(R.drawable.liaotian_toolbar_key);
			mEmjoyRl.setVisibility(View.VISIBLE);
			mPicRl.setVisibility(View.GONE);
		} else if (s == 0) {
			// mAlbumBtn.setImageResource(R.drawable.liaotian_toolbar_photo);
			mEmjoyBtn.setImageResource(R.drawable.liaotian_toolbar_emjoy);
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.showSoftInput(mInputEdit, 0);
		}
		// scrollToPosition(mListView.getBottom());
	}

	/**
	 * 回调结果处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case ImagePickHelper.PHOTO_REQUEST_TAKEPHOTO:

			if (!TextUtils.isEmpty(mChatImgName)) {
				File file = Common.creatFile(JUrl.FilePathTemp, mChatImgName);
				try {
					if (file.exists() && file.isFile()) {
						handlePicMessage(file.getPath());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;
		case ImagePickHelper.PHOTO_REQUEST_GALLERY:
			Uri uri = null;
			if (data != null && (uri = data.getData()) != null) {
				String path = Utils.getFilePathFromContentUri(uri, this);
				handlePicMessage(path);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 处理图片消息
	 * 
	 * @param imgPath
	 */
	private void handlePicMessage(String imgPath) {
		AVIMImageMessage imageMessage;
		try {
			imageMessage = new AVIMImageMessage(imgPath);
			imageMessage.setFrom(mUserBean.getUid());
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("face", mUserBean.getFace());
			attributes.put("name", mUserBean.getNickname());
			attributes.put("sw", chatState);
			imageMessage.setAttrs(attributes);
			sendMessage(imageMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	@Override
	public void onDestroy() {
		ThreadHelper.destory(mChatThread);
		getHandler().removeMessages(DATA_ADD_SUCCESS);
		getHandler().removeMessages(DATA_FAILTURE);
		getHandler().removeMessages(DATA_FETCH_HISTORY_SUCCESS);
		getHandler().removeMessages(DATA_FETCH_SUCCESS);
		getHandler().removeMessages(EmJoyChange);
		super.onDestroy();
	}
}
