package com.shenyuan.militarynews.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.BaseResponseBean;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.base.MyRetrofitResponseCallback;
import com.chengning.common.base.util.RetrofitManager;
import com.chengning.common.widget.MultiStateView;
import com.chengning.common.widget.MultiStateView.ViewState;
import com.google.gson.Gson;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.LoadStateManager;
import com.shenyuan.militarynews.LoadStateManager.LoadState;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.beans.data.BindDataBean;
import com.shenyuan.militarynews.beans.data.LoginBean;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.SPHelper;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.views.CommonHintDialog;
import com.shenyuan.militarynews.views.CommonHintDialog.CommonDialogLitener;
import com.shenyuan.militarynews.views.TitleBar;
import com.shenyuan.militarynews.wxapi.AuthTencentActivity;
import com.shenyuan.militarynews.wxapi.AuthWeiboActivity;
import com.shenyuan.militarynews.wxapi.WXEntryActivity;

import java.util.HashMap;

import io.reactivex.Observable;

public class AccountBindActivity extends BaseFragmentActivity {

	private RelativeLayout mQQBtn;
	private RelativeLayout mSinaBtn;
	private RelativeLayout mWeixinBtn;
	private RelativeLayout mPhoneBtn;
	private LoginBean mUserInfoBean;
	private TextView mPhoneTv;
	private TextView mQQTv;
	private TextView mSinaTv;
	private TextView mWeixinTv;
	private BindDataBean mBindState;
	private View mContentRl;
	private MultiStateView mMultiStateView;
	private LoadStateManager mLoadStateManager;

	private final static int REQUEST_PHONE_BIND = 11;
	private final static int REQUEST_PHONE_UNBIND = 15;
	private final static int REQUEST_SINA_BIND = 12;
	private final static int REQUEST_QQ_BIND = 13;
//	private final static int REQUEST_WX_BIND = 14;
	
	private final static int UPDATE_BIND_STATE_SUCCESS = 1;
	private static final int UPDATE_BIND_STATE_FAILE = 2;
	protected static final int REMOVE_BING_SUCCESS = 3;
	
	private BroadcastReceiver mWXBindSuccessReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_account_bind);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case UPDATE_BIND_STATE_SUCCESS:
			mBindState = (BindDataBean) msg.obj;
			updateBindState(mBindState);
			
			mLoadStateManager.setState(LoadState.Success);

			break;
		case UPDATE_BIND_STATE_FAILE:
			mLoadStateManager.setState(LoadState.Failure);
			break;
		case REMOVE_BING_SUCCESS:
			BindType type = (BindType) msg.obj;
			setBindRemove(type);
			
			break;
		default:
			break;
		}

	}

	private void updateBindState(BindDataBean bindState) {
		mPhoneTv.setText("未绑定");
		if (!TextUtils.isEmpty(bindState.getMobile())) {
			mPhoneTv.setText(handlePhoneShow(bindState.getMobile()));
		}

		mQQTv.setText("未绑定");
		if (!TextUtils.isEmpty(bindState.getQq())) {
			mQQTv.setText(bindState.getQq());
		}

		mSinaTv.setText("未绑定");
		if (!TextUtils.isEmpty(bindState.getSina())) {
			mSinaTv.setText(bindState.getSina());
		}

		mWeixinTv.setText("未绑定");
		if (!TextUtils.isEmpty(bindState.getWx())) {
			mWeixinTv.setText(bindState.getWx());
		}
	}

	private String handlePhoneShow(String mobile) {
		try{
			if (TextUtils.isEmpty(mobile)){
				return "";
			}
			if (!TextUtils.isDigitsOnly(mobile) || mobile.length() != 11) {
				return mobile;
			}
			return new StringBuilder(TextUtils.substring(mobile, 0, 3)).append("****")
					.append(TextUtils.substring(mobile, 7, 11)).toString();
		}catch(Exception e){
			return mobile;
		}
		
	}

	private void setBindRemove(BindType type) {
		if (BindType.MOBILE == type) {
			mPhoneTv.setText("未绑定");
		} else if (BindType.QQ == type) {
			mQQTv.setText("未绑定");
		} else if (BindType.SINA == type) {
			mSinaTv.setText("未绑定");
		} else if (BindType.WEIXIN == type) {
			mWeixinTv.setText("未绑定");
		}
	}

	@Override
	public void initViews() {
		TitleBar titleBar = new TitleBar(getActivity(), true);
		titleBar.setTitle("账号绑定");
		titleBar.showDefaultBack();

		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);

		mContentRl = mMultiStateView.findViewById(R.id.accountbind_content_rl);
		mPhoneBtn = (RelativeLayout) this
				.findViewById(R.id.accountbind_phone_rl);
		mQQBtn = (RelativeLayout) this.findViewById(R.id.accountbind_qq_rl);
		mSinaBtn = (RelativeLayout) this.findViewById(R.id.accountbind_sine_rl);
		mWeixinBtn = (RelativeLayout) this
				.findViewById(R.id.accountbind_weixin_rl);

		mPhoneTv = (TextView) this.findViewById(R.id.accountbind_phone_txt);
		mQQTv = (TextView) this.findViewById(R.id.accountbind_qq_txt);
		mSinaTv = (TextView) this.findViewById(R.id.accountbind_sina_txt);
		mWeixinTv = (TextView) this.findViewById(R.id.accountbind_weixin_txt);

	}

	@Override
	public void initDatas() {
		mUserInfoBean = App.getInst().getLoginBean();

		mLoadStateManager = new LoadStateManager();
		mLoadStateManager
				.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {

					@Override
					public void OnStateChange(LoadState state, Object obj) {
						switch (state) {
						case Init:
							mMultiStateView.setViewState(ViewState.LOADING);
							break;
						case Success:
							mMultiStateView.setViewState(ViewState.CONTENT);
							break;
						case Failure:
							mMultiStateView.setViewState(ViewState.ERROR);
							break;
						default:
							break;
						}
					}

				});

		mLoadStateManager.setState(LoadState.Init);

		GetBindState(getActivity());

		mWXBindSuccessReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (TextUtils.equals(action, Const.ACTION_WX_BIND_SUCESS)) {
					final String data = intent.getStringExtra("data");
					if (null != data) {
						mWeixinTv.setText(data);
					}
				}
			}
		};
		IntentFilter intentFileter = new IntentFilter();
		intentFileter.addAction(Const.ACTION_WX_BIND_SUCESS);
		registerReceiver(mWXBindSuccessReceiver, intentFileter);
	}

	@Override
	public void installListeners() {

		mPhoneBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.equals(mPhoneTv.getText(),"未绑定")) {
//					Intent intent = new Intent();
//					intent.putExtra("cmd", "login");
//					intent.putExtra("action", JUrl.Action_Bind);
//					intent.putExtra("bind_param", 1);
//					intent.setClass(getActivity(), PhoneRegistActivity.class);
//					getActivity().startActivityForResult(intent,
//							Result_code_phone);
					startActivityForResult(new Intent(getActivity(), ModifyPhoneActivity.class), REQUEST_PHONE_BIND);
				} else {
					showRemoveBindDialog(BindType.MOBILE);
				}
			}
		});

		mQQBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.equals(mQQTv.getText(), "未绑定")) {
					Intent intent = new Intent();
					intent.putExtra("cmd", "bind");
					intent.setClass(getActivity(), AuthTencentActivity.class);
					getActivity()
							.startActivityForResult(intent, REQUEST_QQ_BIND);
				} else {
					showRemoveBindDialog(BindType.QQ);
				}
			}
		});

		mSinaBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.equals(mSinaTv.getText(), "未绑定")) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), AuthWeiboActivity.class);
					intent.putExtra("cmd", "bind");
					getActivity().startActivityForResult(intent, REQUEST_SINA_BIND);
				} else {
					showRemoveBindDialog(BindType.SINA);
				}
			}
		});

		mWeixinBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.equals(mWeixinTv.getText(), "未绑定")) {
					SPHelper.getInst().saveString(SPHelper.WX_LOGIN_TYPE, "bind");
					Intent intent = new Intent();
					intent.putExtra("cmd", "bind");
					intent.setClass(getActivity(), WXEntryActivity.class);
					getActivity().startActivity(intent);
				} else {
					showRemoveBindDialog(BindType.WEIXIN);
				}
			}
		});

		mMultiStateView.setRefreshOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				GetBindState(getActivity());
			}
		});
	}

//	protected void showRemovePhoneBindDialog() {
//		RemovePhoneBindDialog dialog = new RemovePhoneBindDialog();
//		dialog.setData(new RemovePhoneBindOkLitener() {
//
//			@Override
//			public void confirm() {
//				Intent intent = new Intent();
//				intent.putExtra("cmd", "login");
//				intent.putExtra("action", JUrl.Action_Bind);
//				intent.putExtra("bind_param", 2);
//				intent.setClass(getActivity(), PhoneRegistActivity.class);
//				getActivity().startActivityForResult(intent, Result_code_phone);
//			}
//
//			@Override
//			public void cancle() {
//				removeBind(BindType.PHONE);
//			}
//		});
//		dialog.show(getSupportFragmentManager(),
//				RemovePhoneBindDialog.class.getSimpleName());
//
//	}

	enum BindType {
		MOBILE("mobile"), QQ("qq"), SINA("xwb"), WEIXIN("wx");

		private String str;

		private BindType(String str) {
			this.str = str;
		}

		public String getStr() {
			return str;
		}
		
	}

	/**
	 * 显示解除绑定对话框
	 */
	protected void showRemoveBindDialog(final BindType type) {
		CommonHintDialog dialog = new CommonHintDialog();
		String msg = "您确定要解除绑定吗？";
		String title = "解绑";
		if (type == BindType.MOBILE) {
			title = "更换绑定手机";
			msg = "确定要更换绑定的手机号吗？";
		}
		dialog.setData(title, msg, new CommonDialogLitener() {
			
			@Override
			public void confirm() {
				switch (type) {
				case MOBILE:
					Intent intent = new Intent(getActivity(), ModifyPhoneActivity.class);
					startActivityForResult(intent, REQUEST_PHONE_UNBIND);
					break;

				default:
					removeBind(type);
					break;
				}
				
			}
			
			@Override
			public void cancle() {
				
			}
		});
		dialog.show(getSupportFragmentManager(),
				CommonHintDialog.class.getSimpleName());
	}

	/**
	 * 解除绑定
	 * @param type
	 */
	protected void removeBind(final BindType type) {

		HashMap params = new HashMap();
		params.put("type",type.getStr());
        Observable<BaseResponseBean> observable
				= App.getInst().getApiInterface().get(JUrl.URL_REMOVE_BIND, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {
			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
				getHandler().obtainMessage(REMOVE_BING_SUCCESS, type)
						.sendToTarget();
			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				UIHelper.showToast(getActivity(), message);
			}
		});
	}

	public void GetBindState(final Activity mContext) {
		if (Common.hasNet()) {

			Observable observable = App.getInst().getApiInterface().post(JUrl.URL_GET_BIND_STATE);
			RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {
				@Override
				public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
					Gson gson = new Gson();
					BindDataBean bean = gson.fromJson(data, BindDataBean.class);
					Message msg = getHandler().obtainMessage(
							UPDATE_BIND_STATE_SUCCESS, bean);
					msg.sendToTarget();
				}

				@Override
				public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
					UIHelper.showToast(mContext, message);
					getHandler().obtainMessage(UPDATE_BIND_STATE_FAILE)
							.sendToTarget();
				}

				@Override
				public void onError(Throwable t) {
					Common.handleHttpFailure(getActivity(),t);
					getHandler().obtainMessage(UPDATE_BIND_STATE_FAILE)
							.sendToTarget();
					super.onError(t);
				}
			});

		} else {
			UIHelper.showToast(mContext, R.string.intnet_fail);
			getHandler().obtainMessage(UPDATE_BIND_STATE_FAILE).sendToTarget();
		}
	}

	@Override
	public void uninstallListeners() {

	}

	/**
	 * 回调结果处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		handleBindSuccess(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void handleBindSuccess(int requestCode, int resultCode, Intent data) {

		if (Const.BIND_RESULT_CODE == resultCode && null != data) {
			String result = data.getStringExtra("result").trim();
			Log.e("binddata", "data" + result);
			if (TextUtils.isEmpty(result)) {
				return;
			}
			switch (requestCode) {
			case REQUEST_PHONE_BIND:
				mPhoneTv.setText(handlePhoneShow(result));
				break;
			case REQUEST_QQ_BIND:
				mQQTv.setText(result);
				break;
			case REQUEST_SINA_BIND:
				mSinaTv.setText(result);
				break;
			case REQUEST_PHONE_UNBIND:
				mPhoneTv.setText(handlePhoneShow(result));
				break;

			default:
				break;
			}
		}

	}

	@Override
	public Activity getActivity() {
		return AccountBindActivity.this;
	}

	@Override
	public void onResume() {
		super.onResume();
		// if(mWXBindSuccessRunnable != null){
		// mWXBindSuccessRunnable.run();
		// mWXBindSuccessRunnable = null;
		// }
	}

	@Override
	public void onDestroy() {
		if (null != mWXBindSuccessReceiver) {
			unregisterReceiver(mWXBindSuccessReceiver);
		}
		super.onDestroy();
	}

}
