package com.shenyuan.militarynews.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.util.RetrofitManager;
import com.chengning.common.util.StatusBarUtil;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.SPHelper;
import com.shenyuan.militarynews.utils.UmengShare;
import com.shenyuan.militarynews.views.PicArticleDialog;

import java.net.URI;
import java.util.List;

import okhttp3.Cookie;

;

public class AdDetailActivity extends BaseFragmentActivity {
	
	public final static String TYPE_ARTICLE = "article";
	public final static String TYPE_AD = "ad";
	
	private ImageView iv_ad_close;
	private TextView tv_ad_title;
	private WebView view;
//	private String title;
	private String url;
	private String imgUrl;
	private String mTitle;
	private ImageView back;
	private ImageView forward;
	private ImageView refresh;
	private ImageView close;
	private ImageView share;
	private String mType;
	
	private CookieManager cookieManager;
	private boolean mIsFromPush;
	
	public static void launch(Activity from, String type, String url, String imgUrl){
		Intent intent = new Intent(from, AdDetailActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("adurl", url);
		intent.putExtra("imgurl", imgUrl);
		from.startActivity(intent);
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putString("type", mType);
    	savedInstanceState.putString("adurl", url);
    	savedInstanceState.putString("imgurl", imgUrl);
    	savedInstanceState.putBoolean("push", mIsFromPush);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    	mType  = savedInstanceState.getString("type");
    	url  = savedInstanceState.getString("adurl");
    	imgUrl  = savedInstanceState.getString("imgurl");
    	mIsFromPush = savedInstanceState.getBoolean("push", false);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_ad_webview);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.mainbgcolor), true);
        }
		super.onCreate(savedInstanceState);
		
		mType = this.getIntent().getStringExtra("type");
		url = this.getIntent().getStringExtra("adurl");
		imgUrl = this.getIntent().getStringExtra("imgurl");
		mIsFromPush = getIntent().getBooleanExtra("push", false);
		
		//这里进行判断是否为商品(是的话，带入cookie)..
		if(!TextUtils.isEmpty(url) && !TextUtils.isEmpty(SPHelper.getInst().getString(SPHelper.KEY_MALL_URL))){
			if(url.contains(SPHelper.getInst().getString(SPHelper.KEY_MALL_URL)) || url.contains(JUrl.SITE + JUrl.URL_DEFAULT_SHOPURL)){
				CookieSyncManager.createInstance(getActivity());    
			    cookieManager = CookieManager.getInstance();
				
				if(App.getInst().isLogin()){
					String host = URI.create(url).getHost();
					RetrofitManager.CookieManager mManager = new RetrofitManager.CookieManager(App.getInst());
					List<Cookie> cookies = mManager.getCookies(url, host);
					if(!Common.isListEmpty(cookies)){
						cookieManager.removeAllCookie();
						cookieManager.setAcceptCookie(true);
						for (okhttp3.Cookie cookie : cookies) {
							String cookieString = cookie.name() + "=" + cookie.value() +
									";domain="+ cookie.domain();
							cookieManager.setCookie(cookie.domain(), cookieString);
						}
						CookieSyncManager.getInstance().sync();
					}
				}else{
					//TODO 根据需求决定是直接清除cookie还是直接传给后台
					String cookie = cookieManager.getCookie(url);
					cookieManager.setCookie(url, cookie);
				}
			}
		}
		
		iv_ad_close = (ImageView) findViewById(R.id.ad_close);
		tv_ad_title = (TextView) findViewById(R.id.ad_title);
		view = (WebView) this.findViewById(R.id.ad_webview);
		back = (ImageView) this.findViewById(R.id.back);
		forward = (ImageView) this.findViewById(R.id.forward);
		refresh = (ImageView) this.findViewById(R.id.refresh);
		close = (ImageView) this.findViewById(R.id.close);
		share = (ImageView) this.findViewById(R.id.share);
		
		view.getSettings().setJavaScriptEnabled(true);
		view.addJavascriptInterface(new JsInterface(), "chengning");
		view.setWebChromeClient(new myWebChromeClient());
		view.setWebViewClient(new myWebViewClient());
		view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		String ua = view.getSettings().getUserAgentString();
		view.getSettings().setUserAgentString(ua + " " + Common.getUAAndroid(getActivity(), Const.USER_AGENT_PREFIX ));
		
		view.loadUrl(url);
		this.setClickListener();

	}	
	private class myWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

	}
	private class myWebChromeClient extends WebChromeClient{
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			tv_ad_title.setText(title);
			mTitle = title;
		}
	}
	private void setClickListener(){
		iv_ad_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleActivityClose(mType);
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(view.canGoBack()){
					view.goBack();
				}
			}
		});
		forward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (view.canGoForward()) {
					view.goForward();
				}
			}
		});
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.reload();
			}
		});
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleActivityClose(mType);
			}
		});
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PicArticleDialog dialog = new PicArticleDialog();
				dialog.setData(url, imgUrl, mTitle);
				dialog.showAllowingStateLoss((BaseFragmentActivity) getActivity(), getSupportFragmentManager(),
						PicArticleDialog.class.getSimpleName());
			}
		});
	}
	
	protected void handleActivityClose(String type) {
		
		if (TextUtils.equals(type, TYPE_AD)) {
			nextActivity();
		}
		finish();
		if(mIsFromPush){
        	HomeActivity.launch(getActivity());
		}
	}

	public void nextActivity() {
		HomeActivity.launch(getActivity());
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		UmengShare.getInstance().onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public void onBackPressedSupport() {
		super.onBackPressedSupport();
		handleActivityClose(mType);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void initViews() {
		
	}

	@Override
	public void initDatas() {
		
	}

	@Override
	public void installListeners() {
	}

	@Override
	public void processHandlerMessage(Message msg) {
	}
	
	private class JsInterface {
        @JavascriptInterface
        public void goTologinAction() {
        	startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
}
