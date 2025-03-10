package com.chengning.fenghuo.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVQuery.CachePolicy;
import com.chengning.common.util.SerializeUtil;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.LoginManager;
import com.chengning.fenghuo.MyConversationHandler;
import com.chengning.fenghuo.MyMessageHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.data.access.ChatConversationDA;
import com.chengning.fenghuo.data.access.ChatMessageDA;
import com.chengning.fenghuo.data.bean.ChatConversationBean;
import com.chengning.fenghuo.data.bean.ChatMessageBean;
import com.chengning.fenghuo.data.bean.ConversationBean;
import com.chengning.fenghuo.data.bean.UserInfoBean;
import com.chengning.fenghuo.util.ChatClientManager.MessageListener;

public class ChatClientManager extends AVIMClientEventHandler {

	private static final String TAG = ChatClientManager.class.getSimpleName();

	private static ChatClientManager mChatClientManager;

	private AVIMClient mClient;
	private String mClientId;
	private AVIMConversation mConversation;

	public synchronized static ChatClientManager getInst() {

		if (null == mChatClientManager) {
			mChatClientManager = new ChatClientManager();
		}
		return mChatClientManager;

	}

	private ChatClientManager() {

	}

	public void init(Context context) {

		AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class,
				new MyMessageHandler(context));
		AVIMMessageManager
				.setConversationEventHandler(new MyConversationHandler());
		AVIMClient.setClientEventHandler(ChatClientManager.getInst());

		// 签名
		// AVIMClient.setSignatureFactory(new SignatureFactory());
		AVIMClient.setMessageQueryCacheEnable(false);
		AVIMClient.setOfflineMessagePush(true);

		if (!App.getInst().isLogin()) {
			return;
		}
		open(App.getInst().getUserInfoBean().getUid(),
				new AVIMClientCallback() {

					@Override
					public void done(AVIMClient arg0, AVIMException arg1) {
						if (null == arg1) {
							setmClient(arg0);
						}

					}
				});
	}

	public void open(String clientId, AVIMClientCallback callback) {
		this.mClientId = clientId;
		mClient = AVIMClient.getInstance(clientId, "mobile");
		mClient.open(callback);
	}

	public void close() {
		if (null != mClient) {
			mClient.close(new AVIMClientCallback() {

				@Override
				public void done(AVIMClient arg0, AVIMException arg1) {
				}
			});
		}
		mClient = null;
		mClientId = null;
	}

	// public void close(final boolean isHasHint){
	// if (null != mClient) {
	// mClient.close(new AVIMClientCallback() {
	//
	// @Override
	// public void done(AVIMClient arg0, AVIMException arg1) {
	// if (isHasHint) {
	// Context context = App.getInst().getBaseContext();
	// UIHelper.showToast(context,
	// context.getString(R.string.str_chat_offline_notice));
	// }
	// }
	// });
	// }
	// }

	public AVIMClient getmClient() {
		return mClient;
	}

	public void setmClient(AVIMClient mClient) {
		this.mClient = mClient;
	}

	public String getClientId() {

		if (TextUtils.isEmpty(mClientId)) {
			throw new IllegalStateException(
					"Please call AVImClientManager.open first");
		}
		return mClientId;
	}

	/**
	 * 创建对话（单聊）
	 * 
	 * @param memberId
	 * @param listener
	 */
	public void creatConversation(final String memberId, final String name,
			UserInfoBean mUserBean, UserInfoBean mTargetBean,
			final Runnable runnable, final MessageListener listener) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("creat_bean", creatConversationAttr(mUserBean));
		attributes.put("target_bean", creatConversationAttr(mTargetBean));
		mClient.createConversation(Arrays.asList(memberId), name, attributes,
				false, true, new AVIMConversationCreatedCallback() {

					@Override
					public void done(AVIMConversation avimConversation,
							AVIMException e) {
						if (filterException(e)) {
							setConversation(avimConversation);
							new Handler().post(runnable);
						} else {
							listener.handleFailure(e);
						}
					}
				});
	}

	public String creatConversationAttr(UserInfoBean bean) {
		JSONObject cObject = new JSONObject();
		try {
			cObject.put("face", bean.getFace());
			cObject.put("nickname", bean.getNickname());
			cObject.put("uid", bean.getUid());
			cObject.put("role_name", bean.getRole_name());
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}

		return cObject.toString();
	}

	public void queryConversationByMem(final List<String> values,
			final QueryListener runnable, final MessageListener listener) {
		AVIMConversationQuery conversationQuery = mClient.getQuery();
		conversationQuery.containsMembers(values);
		conversationQuery.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ONLY);
		conversationQuery.findInBackground(new AVIMConversationQueryCallback() {

			@Override
			public void done(List<AVIMConversation> list, AVIMException e) {
				if (filterException(e)) {
					// 注意：此处仍有漏洞，如果获取了多个 conversation，默认取第一个
					if (null != list && list.size() > 0) {
						setConversation(list.get(0));
						runnable.sussess();
					} else {
						runnable.failure();
					}
				} else {
					listener.handleFailure(e);
				}

			}
		});
	}

	public interface QueryListener {

		void sussess();

		void failure();

	}

	public void querySingleConversation(final String memberId,
			final Runnable runnable, final MessageListener listener) {
		AVIMConversationQuery conversationQuery = mClient.getQuery();
		conversationQuery.withMembers(Arrays.asList(memberId), true);
		conversationQuery.findInBackground(new AVIMConversationQueryCallback() {

			@Override
			public void done(List<AVIMConversation> list, AVIMException e) {
				if (filterException(e)) {
					// 注意：此处仍有漏洞，如果获取了多个 conversation，默认取第一个
					if (!Common.isListEmpty(list)) {
						setConversation(list.get(0));
						new Handler().post(runnable);
					} else {
					}
				} else {
					listener.handleFailure(e);
				}

			}
		});
	}

	/**
	 * 拉取消息，必须加入 conversation 后才能拉取消息
	 * 
	 * @param conId
	 * @param limit
	 * @param listener
	 * @param offset
	 */
	public void fetchMessages(Activity activity, final String conId,
			int offset, final MessageListener listener) {
		// setConversation(conversation);
		// conversation.queryMessages(10, new AVIMMessagesQueryCallback() {
		// @Override
		// public void done(List<AVIMMessage> list, AVIMException e) {
		// if (filterException(e)) {
		//
		// listener.fetchSuccess(conversation, list);
		// } else{
		// listener.handleFailure(e);
		// }
		// }
		// });
		List<ChatMessageBean> list = ChatMessageDA.getInst(activity)
				.queryMessage(App.getInst().getUserInfoBean().getUid(), conId,
						offset);
		listener.fetchSuccess(list);
	}

	/**
	 * 发送消息
	 * 
	 * @param conversation
	 * @param msg
	 */
	public void sendMessage(String conId, final AVIMTypedMessage msg,
			final MessageListener listener) {
		mClient.getConversation(conId).sendMessage(msg,
				new AVIMConversationCallback() {

					@Override
					public void done(AVIMException e) {
						if (filterException(e)) {
							listener.sendSuccess(msg);
						} else {
							listener.handleFailure(e);
						}
					}
				});
	}

	/**
	 * 查询对话
	 * 
	 * @param client
	 */
	public void queryConversations(Activity activity, Integer count,
			CachePolicy policy, final MessageListener listener) {
		// AVIMConversationQuery query = mClient.getQuery();
		// if (null == query) {
		// return;
		// }
		// if (null != count) {
		// query.limit(count);
		// }
		// if (null != policy) {
		// query.setQueryPolicy(policy);
		// } else {
		// query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		// }
		// query.findInBackground(new AVIMConversationQueryCallback() {
		// @Override
		// public void done(List<AVIMConversation> conversations, AVIMException
		// e) {
		//
		// if (filterException(e)) {
		// listener.querySuccess(conversations);
		// } else{
		// listener.handleFailure(e);
		// }
		//
		// }
		// });
		List<ChatConversationBean> list = ChatConversationDA.getInst(activity)
				.queryAllConByUid(App.getInst().getUserInfoBean().getUid());
		listener.querySuccess(list);
	}

	/**
	 * 更新对话
	 * 
	 * @param conversation
	 */
	public void updateConversation(AVIMConversation conversation) {
		conversation.updateInfoInBackground(new AVIMConversationCallback() {

			@Override
			public void done(AVIMException e) {
				if (filterException(e)) {

				}
			}
		});
	}

	/**
	 * 查询消息历史记录
	 * 
	 * @param conversation
	 * @param pager
	 * @param pageSize
	 * @param listener
	 */
	public void queryHistoryMessage(Activity activity, final String conId,
			int offset, final MessageListener listener) {
		// conversation.queryMessages(pager.getMessageId(),
		// pager.getTimestamp(), pageSize, new AVIMMessagesQueryCallback() {
		// @Override
		// public void done(List<AVIMMessage> secondPage, AVIMException e) {
		// if (filterException(e)) {
		// listener.fetchHistorySuccess(conversation, secondPage);
		// } else{
		// listener.handleFailure(e);
		// }
		// }
		// });
		List<ChatMessageBean> list = ChatMessageDA.getInst(activity)
				.queryMessage(App.getInst().getUserInfoBean().getUid(), conId,
						offset);
		listener.fetchHistorySuccess(list);
	}

	/**
	 * 处理异常
	 * 
	 * @param e
	 * @return
	 */
	protected boolean filterException(AVIMException e) {
		if (e != null) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			return false;
		} else {
			return true;
		}
	}

	public AVIMConversation getConversation() {
		return mConversation;
	}

	public void setConversation(AVIMConversation mConversation) {
		this.mConversation = mConversation;
	}

	public interface MessageListener {
		/**
		 * 获取消息成功
		 * 
		 * @param list
		 * @param conversation
		 * @param list
		 * @param isFirstPage
		 */
		void fetchSuccess(List<ChatMessageBean> list);

		/**
		 * 获取历史消息成功
		 * 
		 * @param conversation
		 * @param secondPage
		 */
		void fetchHistorySuccess(List<ChatMessageBean> secondPage);

		/**
		 * 错误处理
		 * 
		 * @param e
		 * @param msg
		 */
		void handleFailure(AVIMException e);

		/**
		 * 发送消息成功
		 * 
		 * @param msg
		 */
		void sendSuccess(AVIMTypedMessage msg);

		/**
		 * 查询对话成功
		 * 
		 * @param conversations
		 */
		void querySuccess(List<ChatConversationBean> conversations);
	}

	public enum FailureType {
		SENDFAILURE, OTHERFAILURE;
	}

	@Override
	public void onClientOffline(AVIMClient arg0, int arg1) {

		if (4111 == arg1) {
			// 适当地弹出友好提示，告知当前用户的 Client Id 在其他设备上登陆了
			Context context = App.getInst().getBaseContext();
			handleOfflineHint(context);
			close();
		}

	}

	/**
	 * 下线提示
	 * 
	 * @param context
	 */
	public void handleOfflineHint(Context context) {
		UIHelper.showToast(context,
				context.getString(R.string.str_chat_offline_notice));
	}

	@Override
	public void onConnectionPaused(AVIMClient arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionResume(AVIMClient arg0) {
		// TODO Auto-generated method stub

	}

}
