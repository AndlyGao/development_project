package com.chengning.fenghuo;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.chengning.common.app.ActivityInfo;
import com.chengning.common.base.BaseDialogFragment;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuo.activity.MainActivity;
import com.chengning.fenghuo.data.access.LoginDA;
import com.chengning.fenghuo.data.bean.LoginUserBean;
import com.chengning.fenghuo.data.bean.UserInfoBean;
import com.chengning.fenghuo.util.ActivityManagerUtil;
import com.chengning.fenghuo.util.ChatClientManager;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.JUrl;
import com.chengning.fenghuo.util.SPHelper;
import com.chengning.fenghuo.util.UIHelper;
import com.chengning.fenghuo.util.Utils;
import com.chengning.fenghuo.widget.LoginHintDialog;
import com.chengning.fenghuo.widget.NoLoginHongbaoHintDialog;
import com.google.gson.Gson;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginManager {

	public static final String TAG = LoginManager.class.getSimpleName();
	private final long ONE_DAY = 24 * 60 * 60 * 1000;
	private final long ONE_WEEK = ONE_DAY * 7;

	private static LoginManager mLogin = null;
	
	public static LoginManager getInst(){
	    if (mLogin == null) {
	        synchronized (LoginManager.class) {
	            if (mLogin == null) {
	                mLogin = new LoginManager();
	            }
	        }
	    }
		return mLogin;
	}

	protected StringBuffer buffer = new StringBuffer();

	private LoginHintDialog mLoginHintDialog;
	
	private LoginManager(){
		
	}
	
	public void init(){
		// check old login
		// deviceToken...
		
		App.getInst().setIsLogin(isLogin());
		App.getInst().setUserInfoBean(getLoginUserBean().getUserinfo());
		
	}

	public boolean verifyCookie(PersistentCookieStore cookieStore) {
		List<Cookie> cookies = cookieStore.getCookies();
		String cookieName,cookieValue;
		boolean isExpired;
		if (isLogin() && !Common.isListEmpty(cookies)) {
			for (Cookie cookie : cookies) {
				cookieName = cookie.getName();  
	            cookieValue = cookie.getValue(); 
	            if (!TextUtils.isEmpty(cookieName) && "fh_uid".equals(cookieName) 
	            		&& !TextUtils.isEmpty(cookieValue) 
	            		&& getLoginUserBean().getUserinfo().getUid().equals(cookieValue)) {
	            	((BasicClientCookie)cookie).setDomain("www.pp78.com");
	            	isExpired = cookie.isExpired(new Date());
					if (!isExpired){
						return true;
					}
	            	
	            } 
				 
			}
		} 
		return false;
	}
	
	public static void startLoginActivity(Context context){
		if (context != null && context instanceof Activity) {
				Activity activity = (Activity) context;
				App.startLoginActivity(activity);
		} else{
			App.startLoginActivity();
		}
	}
	
	public static LoginUserBean getEmptyLoginUserBean(){
		LoginUserBean bean = new LoginUserBean();
		bean.setUserinfo(new UserInfoBean());
		bean.setLasttime("");
		return bean;
	}
	
	public void logout(){
		
		clearData();
		
		final Context context = App.getInst().getApplicationContext();
		// cookie
		
		RequestParams params = new RequestParams();
		params.put("devicetoken", AVInstallation.getCurrentInstallation().getInstallationId());
		HttpUtil.post(JUrl.SITE + JUrl.OUT_LOGIN, params, new MyJsonHttpResponseHandler() {

            @Override
            public void onDataSuccess(int status, String mod, String message,
                                      String data, JSONObject obj) {
                PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
                myCookieStore.clear();
                HttpUtil.getClient().setCookieStore(null);
            }

            @Override
            public void onDataFailure(int status, String mod, String message,
                                      String data, JSONObject obj) {

            }
        });
		SPHelper.getInst().saveBoolean(SPHelper.KEY_LOGOUT_CHANGE, true);
	}
	
	public void clearData(){
		Context context = App.getInst().getApplicationContext();
		
		// clean db
		LoginDA.getInst(context).deleteAll();

		App.getInst().setIsLogin(false);
		App.getInst().setUserInfoBean(getEmptyLoginUserBean().getUserinfo());
		App.getInst().setUserAvatarChange(true);
		ChatClientManager.getInst().close();
	}
	
	public boolean isLogin(){
		LoginUserBean bean = getLoginUserBean();
		boolean isLogin = !TextUtils.isEmpty(bean.getLasttime()) && !TextUtils.isEmpty(bean.getUserinfo().getUid());
		return isLogin;
	}
	
	public static void noticeNotLaunched(Context context){
		UIHelper.showToast(context, context.getResources().getString(R.string.no_login_hint));
	}

	public boolean checkLoginWithNotice(Context context){
		boolean isLogin = isLogin();
		if(!isLogin){
			noticeNotLaunched(context);
		}
		return isLogin;
	}
	public boolean checkLoginWithLoginDialog(FragmentActivity activity){
		boolean isLogin = isLogin();
		if(!isLogin){
			showLoginHintDialog(activity);
		}
		return isLogin;
	}
	
	private void showLoginHintDialog(FragmentActivity context){
		if(mLoginHintDialog == null){
			mLoginHintDialog = new LoginHintDialog();
		}
		mLoginHintDialog.show(context.getSupportFragmentManager(), LoginHintDialog.class.getSimpleName());
	}
	
	public boolean checkLoginWithLogin(Context context){
		boolean isLogin = isLogin();
		if(!isLogin){
			startLoginActivity(context);
		}
		return isLogin;
	}
	
	public LoginUserBean getLoginUserBean(){
		LoginUserBean bean = LoginDA.getInst(App.getInst()).getLoginUserBean();
		if(bean == null){
			bean = getEmptyLoginUserBean();
			LoginDA.getInst(App.getInst()).insertOne(bean);
		}
		return bean;
	}
	
	public void saveUserInfo(UserInfoBean bean){
		LoginUserBean lBean = getLoginUserBean();
		lBean.setUserinfo(bean);
		saveLoginUserBean(lBean, false);
	}
	
	public void saveLoginUserBean(LoginUserBean bean){
		saveLoginUserBean(bean, true);
	}
	
	public void saveLoginUserBean(LoginUserBean bean, boolean isNewTime){
		if(isNewTime && !TextUtils.isEmpty(bean.getUserinfo().getUid())){
			bean.setLasttime(String.valueOf(System.currentTimeMillis()));
		}
			
		LoginDA.getInst(App.getInst()).deleteAll();
		LoginDA.getInst(App.getInst()).insertOne(bean);
		
		App.getInst().setUserInfoBean(bean.getUserinfo());
		App.getInst().setIsLogin(isLogin());
		
//		ChatClientManager.getInst().open(bean.getUserinfo().getUid(), new AVIMClientCallback() {
//			
//			@Override
//			public void done(AVIMClient arg0, AVIMException arg1) {
//			}
//		});
	}
	
	/**
	 * 获取用户信息
	 */
	public void getUserInfoByUid(final Activity context, final String uid) {
		HttpUtil.get(context, JUrl.SITE + JUrl.Get_USER_INFO + uid, new MyJsonHttpResponseHandler() {
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Common.handleHttpFailure(context, throwable);
			}

			@Override
			public void onFinish() {
				UIHelper.removePD();
			}

			@Override
			public void onDataSuccess(int status, String mod, String message,
									  String data, JSONObject obj) {
				LoginManager.getInst().clearData();

				Gson gson = new Gson();
				UserInfoBean mUserInfoBean = gson.fromJson(data, UserInfoBean.class);

				LoginUserBean lBean = getLoginUserBean();
				lBean.setUserinfo(mUserInfoBean);
				saveLoginUserBean(lBean, true);
				MainActivity.launchAfterLoginChange(context, "");
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
									  String data, JSONObject obj) {
				UIHelper.showToast(context, message);
			}

		}); 
	}

	/**
	 * 每日登录
	 * @param context
	 * @param uid
	 */
	public void everydayLogin(final BaseFragmentActivity context, final String uid) {
		if (LoginManager.getInst().isLogin()) {
			handleLogin(context);
		} else {
			context.getHandler().postDelayed(new Runnable() {
				@Override
				public void run() {
					handleNoLogin(context);
				}
			}, 2000);

		}
	}

	/**
	 * 未登录（弹窗）
	 * @param context
	 */
	private void handleNoLogin(final BaseFragmentActivity context) {
        String mMoney = SPHelper.getInst().getString(SPHelper.KEY_REGIST_MONEY);
        if (!TextUtils.isEmpty(mMoney)) {
            long mTime = SPHelper.getInst().getLong(SPHelper.KEY_DIALOG_TIME_NOLOGIN, 0);
            final long curTime = Common.getCurTimeMillis();
            if ((curTime - mTime) >= ONE_WEEK ) {
                NoLoginHongbaoHintDialog dialog = new NoLoginHongbaoHintDialog();
                dialog.setData(mMoney);
                dialog.setShowSuccessListener(new BaseDialogFragment.BaseShowSuccessListener() {
                    @Override
                    public void success() {
                        SPHelper.getInst().saveLong(SPHelper.KEY_DIALOG_TIME_NOLOGIN, curTime);
                    }
                });
                dialog.showAllowingStateLoss(context, context.getSupportFragmentManager(),NoLoginHongbaoHintDialog.class.getSimpleName());
            }

        }

	}

	/**
	 * 登录（弹窗）
	 * @param context
	 */
	private void handleLogin(final BaseFragmentActivity context) {
		HttpUtil.post(JUrl.SITE + JUrl.TASK_HINT_URL, null, new MyJsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  Throwable throwable, JSONObject errorResponse) {

				Common.handleHttpFailure(context, throwable);
			}

			@Override
			public void onDataSuccess(int status, String mod, String message,
									  String data, JSONObject obj) {
				try {
					Utils.showHints(context, data);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
									  String data, JSONObject obj) {

			}
		});
	}

	/**
	 * 登录后保存用户信息
	 * @param context
	 * @param data
	 */
	public void getUserInfo(final BaseFragmentActivity context, final String data){
//		HomeFinishActivityUtil.sendBroadcast(context);
		LoginManager.getInst().clearData();
		
		Gson gson = new Gson();
		LoginUserBean mLoginUsertBean = gson.fromJson(data, LoginUserBean.class);
		
		saveLoginUserBean(mLoginUsertBean);
		ActivityManagerUtil.getInst().finishAllActivitys();
		MainActivity.launchAfterLoginChange(context, data);
		ChatClientManager.getInst().open(mLoginUsertBean.getUserinfo().getUid(), new AVIMClientCallback() {

            @Override
            public void done(AVIMClient arg0, AVIMException arg1) {
                if (null == arg1) {
                    ChatClientManager.getInst().setmClient(arg0);
                }
            }
        });
	}

	/**
	 * 更新cookie
	 * @param oldDomain
	 */
	public void updateCookie(String oldDomain) {
		String newDomain = URI.create(JUrl.SITE).getHost();
		if (TextUtils.equals(oldDomain, newDomain)) {
			return;
		}

		String oldRootDomain = Common.getRootDomain(oldDomain);
		String newRootDomain = Common.getRootDomain(newDomain);

		PersistentCookieStore myCookieStore = new PersistentCookieStore(App.getInst());
		List<Cookie> cookiesList = new ArrayList<Cookie>();
		List<Cookie> cookies = myCookieStore.getCookies();
		String cookieName;
		String cookieValue;
		String curDomain = oldDomain;

		for (Cookie cookie : cookies) {
			if (TextUtils.equals(oldRootDomain, cookie.getDomain())) {
				curDomain = newRootDomain;
			} else if (TextUtils.equals(oldDomain, cookie.getDomain())) {
				curDomain = newDomain;
			} else {
				continue;
			}
			cookieName = cookie.getName();
			cookieValue = cookie.getValue();
			BasicClientCookie clientCookie = new BasicClientCookie(cookieName, cookieValue);
			clientCookie.setDomain(curDomain);
			clientCookie.setComment(cookie.getComment());
			clientCookie.setExpiryDate(cookie.getExpiryDate());
			clientCookie.setPath(cookie.getPath());
//            	clientCookie.setSecure(cookie.getPath);
			clientCookie.setVersion(cookie.getVersion());
			cookiesList.add(clientCookie);
		}
		if (!Common.isListEmpty(cookiesList)) {
			for (Cookie cookie : cookiesList) {
				myCookieStore.addCookie(cookie);
			}
		}

		HttpUtil.getClient().setCookieStore(myCookieStore);
	}
}
