package com.shenyuan.militarynews.wxapi;


import org.apache.http.Header;
import de.greenrobot.event.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.util.HttpUtil;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.MyStatusResponseHandler;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.activity.CompleteInfoActivity;
import com.shenyuan.militarynews.activity.LoginActivity;
import com.shenyuan.militarynews.beans.data.LoginBean;
import com.shenyuan.militarynews.event.LoginResultEvent;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.SPHelper;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.utils.UmengShare;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.bean.SHARE_MEDIA;


public class WXEntryActivity extends BaseFragmentActivity implements IWXAPIEventHandler{
	
	private static final int Auth_Login_Scuess = 0;
	
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

	private  String mUrl;
	private String unandroid = "";
	private String mStrFace;

	protected String mStrName;
	
	private String mParams;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	Common.setTheme(this);
        setContentView(R.layout.activity_tencent_login);
        super.onCreate(savedInstanceState);
    }

	@Override
	public void initViews() {

	}
	
	@Override
	public void initDatas() {
        
        mUrl = JUrl.SITE + JUrl.URL_LOGIN_WX;
		mParams = SPHelper.getInst().getString(SPHelper.WX_LOGIN_TYPE);
		if (TextUtils.equals(mParams, "bind")) {
			mUrl = JUrl.SITE + JUrl.URL_BIND_WEIXIN;
        }
		register(getActivity());
        handleIntent(getIntent());
	}
	
	@Override
	public void installListeners() {

	}

	private void register(Context context){
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
    	api = WXAPIFactory.createWXAPI(context, Const.WX_APP_ID, false);
    	if (api.isWXAppInstalled()) {//判断是否安装微信客户端  
    		api.registerApp(Const.WX_APP_ID);
    	} else {
    		UIHelper.showToast(getActivity(), getString(R.string.umeng_share_install_wx));  
    		finish();
    	}
    }
	
    private void handleIntent(Intent intent){
    	if(intent.hasExtra("cmd") && (TextUtils.equals(mParams, "login") || TextUtils.equals(mParams, "bind"))){
    		login();
    	}else{
            api.handleIntent(intent, this);
    	}
    }
    
    private void login() {
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo_test";
		api.sendReq(req);
		finish();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}
    
	@Override
	public void onReq(BaseReq arg0) {
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			if (resp instanceof SendAuth.Resp) {
				try {
					SendAuth.Resp r = (SendAuth.Resp) resp;
					mUrl = JUrl.append(mUrl, "code", String.valueOf(r.code));
			    	loginWx(mUrl.toString());
				} catch (Exception e) {
					e.printStackTrace();
					UIHelper.showToast(getActivity(), getResources().getString(R.string.umeng_share_err_auth));
					finish();
				}
			} else if (resp instanceof SendMessageToWX.Resp) {
				if (App.getInst().isLogin()) {
					if (UmengShare.getInstance().getShareMedia()== SHARE_MEDIA.WEIXIN) {
						UmengShare.getPointByShare(
								getActivity(),
								String.valueOf(UmengShare.getInstance()
										.getAid()), 1);
					} else if (UmengShare.getInstance().getShareMedia() == SHARE_MEDIA.WEIXIN_CIRCLE) {
						UmengShare.getPointByShare(
								getActivity(),
								String.valueOf(UmengShare.getInstance()
										.getAid()), 2);
					}
				}else {
					finish();
				}
			}else{
				finish();
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
            finish();
            break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			finish();
			break;
		default: 
			finish(); 
			break;
		}
	}

	

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case Auth_Login_Scuess:
			String mResult =  (String) msg.obj; 
			Gson gson = new Gson();
			LoginBean mBean = gson.fromJson(mResult, LoginBean.class);
			App.getInst().saveLoginBean(mBean);
			EventBus.getDefault().post(new LoginResultEvent());
//			startActivity(new Intent(getActivity(), AccountCenterActivity.class));
			this.finish();
			break;
		}
	}
	
	/**
	 * 登录微信
	 * 
	 * @param url
	 */
	private void loginWx(String url) {
		RequestParams params = new RequestParams();
		HttpUtil.get(url, params, new MyStatusResponseHandler() {
			@Override
			public void onDataSuccess(int status, String mod, String message, String data, JSONObject obj) {
				
				if (TextUtils.equals(mParams, "bind")) {
					Intent intent = new Intent(Const.ACTION_WX_BIND_SUCESS);
    				intent.putExtra("data", data);
    				getActivity().sendBroadcast(intent);
                    finish();
                } else {
                	Message msg = getHandler().obtainMessage(Auth_Login_Scuess, data);
    				msg.sendToTarget();
                }
			}
			@Override
			public void onDataFailure(int status, String mod, String message, String data, JSONObject obj) {
				UIHelper.showToast(getActivity(), message);
				if(status == -4){
					try {
						unandroid = new JSONObject(data).getString("unionid");
						mStrFace = new JSONObject(data).getString("face");
						mStrName = new JSONObject(data).getString("name");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Intent i = new Intent();
					i.putExtra(LoginActivity.WXID, unandroid);
					setResult(LoginActivity.RESULTCODE_WX_COMPLETE, i);
					startActivity(new Intent(getActivity(), CompleteInfoActivity.class)
						.putExtra(LoginActivity.WXID, unandroid)
						.putExtra(LoginActivity.THIRDFACE, mStrFace)
						.putExtra(LoginActivity.THIRDNICK, mStrName)
						.setAction(LoginActivity.ACTION_WX)); 
					finish();
				}else{
					finish();
				}
			};
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Common.handleHttpFailure(getActivity(), throwable);
				UIHelper.removePD();
				finish();
			};
		});
	}

	@Override
	public void onStop(){
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public BaseFragmentActivity getActivity() {
		return this;
	}

}