package com.chengning.fenghuovideo.wxapi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.avos.avoscloud.AVInstallation;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuovideo.App;
import com.chengning.fenghuovideo.Consts;
import com.chengning.fenghuovideo.LoginManager;
import com.chengning.fenghuovideo.MyJsonHttpResponseHandler;
import com.chengning.fenghuovideo.R;
import com.chengning.fenghuovideo.activity.AccountEditActivity;
import com.chengning.fenghuovideo.util.Common;
import com.chengning.fenghuovideo.util.JUrl;
import com.chengning.fenghuovideo.util.UIHelper;
import com.chengning.fenghuovideo.util.UmengShare;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class WXEntryActivity extends BaseFragmentActivity implements IWXAPIEventHandler{
	
	private static final int Auth_Login_Scuess = 0;
	
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
	public int mAction;

	private String mUrl;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Common.setTheme(this);
        setContentView(R.layout.activity_tencent_login);
        super.onCreate(savedInstanceState);
        
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        handleIntent(getIntent());
	}
    
    void handleIntent(Intent intent){
    	if(intent.hasExtra("cmd") && intent.getStringExtra("cmd").equals("login")){
    		login();
    	}else{
            api.handleIntent(intent, this);
    	}
    }
    
    public void register(Context context){
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
    	api = WXAPIFactory.createWXAPI(context, Consts.WX_APP_ID, false);
    	if (api.isWXAppInstalled()) {//判断是否安装微信客户端  
    		api.registerApp(Consts.WX_APP_ID);
    	} else {
    		UIHelper.showToast(getActivity(), getString(R.string.umeng_share_install_wx));
			loginFailed();
    	}
    }
    
    public void login(){
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo_test";
		api.sendReq(req); 
    }

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
        loginFailed();
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			if(resp instanceof SendAuth.Resp){
				try {
					SendAuth.Resp r = (SendAuth.Resp) resp;
					mUrl = JUrl.append(mUrl, "code", String.valueOf(r.code));
			    	loginWx(mUrl.toString());
				} catch (Exception e) {
					e.printStackTrace();
					UIHelper.showToast(getActivity(), getString(R.string.umeng_share_err_auth));
					loginFailed();
				}
			} else{
				loginFailed();
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
            loginFailed();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
            loginFailed();
			break;
		default: 
			loginFailed();
			break;
		}
	}

	/**
	 * 登录微信
	 * @param url
	 */
	private void loginWx(String url) {
		RequestParams params = new RequestParams();
		params.put("devicetype", 2);
		params.put("devicetoken", AVInstallation.getCurrentInstallation().getInstallationId());
		params.put("objectid", AVInstallation.getCurrentInstallation().getObjectId());
		if (Common.isMIUI()) {
			//小米推送唯一标识（暂定）
			params.put("regid", MiPushClient.getRegId(getActivity()));
		}
		
		HttpUtil.post(getActivity(), url, params, new MyJsonHttpResponseHandler() {
   	    	  
   	         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
   	             Common.handleHttpFailure(getActivity(), throwable);
   	             UIHelper.removePD();
   	             finish();
   	         }; 
   	         
			@Override
			public void onDataSuccess(int status, String mod,
					String message, String data, JSONObject obj) {

                if(mod.equals("wxlogin")) {
                    
                    Message msg = getHandler().obtainMessage(Auth_Login_Scuess, data);
					msg.sendToTarget(); 
					UIHelper.removePD();      
                } else {

    				Intent intent = new Intent(Consts.ACTION_WX_BIND_SUCESS);
    				intent.putExtra("data", data);
    				getActivity().sendBroadcast(intent);
                    
                    loginFailed();
				} 
			}

			@Override
			public void onDataFailure(int status, String mod,
					String message, String data, JSONObject obj) {
				
				switch (status) {
				case 1:
					loginFailed();
			        String unandroid = "";
					try {
						unandroid = new JSONObject(data).optString("unionid");
				        
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Bundle bundle = new Bundle();
			        bundle.putString("unionid", unandroid);
					Intent intent = new Intent();
					intent.putExtra("login_from", JUrl.Login_From_WeiXin);
                    intent.putExtra("login_param", bundle);
					intent.setClass(getActivity(), AccountEditActivity.class);
					getActivity().startActivity(intent); 
					break;
				default:
		            	UIHelper.showToast(getActivity(), message);
		            	loginFailed();
					break;
				}
				 
			};
		});
	}


	private void loginFailed(){
        finish();
	}
	
	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
			case Auth_Login_Scuess: 
				loginFailed();
				String uid = (String) msg.obj; 
				LoginManager.getInst().getUserInfo(getActivity(), uid);
				
			break;
		}
	}
	
	@Override
	public void initViews() {
		
	}

	@Override
	public void initDatas() {
        Intent i = getActivity().getIntent();
        mAction = i.getIntExtra("action", 0);
//        App.getInst().mWeixinAction = mAction;
        if (JUrl.Action_Bind == mAction) {
	        mUrl = JUrl.SITE + JUrl.Bind_WeiXin;
	    } else if(JUrl.Action_Login == mAction) {
	    	mUrl = JUrl.SITE + JUrl.URL_LOGIN_WEIXIN;
	    }  
        register(getActivity());
        handleIntent(getIntent());
	}

	@Override
	public void installListeners() {
		
	}

	@Override
	public void uninstallListeners() {
		
	}

	@Override
	public BaseFragmentActivity getActivity() {
		return this;
	}


}