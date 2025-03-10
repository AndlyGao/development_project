package com.cmstop.jstt.activity;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.chengning.common.base.BaseActivity;
import com.chengning.common.util.HttpUtil;
import com.cmstop.jstt.App;
import com.cmstop.jstt.Const;
import com.cmstop.jstt.MyStatusResponseHandler;
import com.cmstop.jstt.R;
import com.cmstop.jstt.SettingManager;
import com.cmstop.jstt.beans.data.LoginBean;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.utils.JUrl;
import com.chengning.common.util.StatusBarUtil;
import com.cmstop.jstt.utils.UIHelper;
import com.cmstop.jstt.views.TitleBar;
import com.loopj.android.http.RequestParams;

public class ModifyPhoneActivity extends BaseActivity {

	private TitleBar mTitleBar;
	private EditText mAccount;
	private EditText mCode;
	private Button mGetCode;
	private Button mRegister;
	
	private String mStrAccount;
	private String mStrCode;
	
	
	private int mPhone_CountDown = 0;
	private Runnable mRunnable;
	private static final int COUNTDOWN = 0x01;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_modifyphone);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.normalstate_bg), true);
        }
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initViews() {
		mTitleBar = new TitleBar(getActivity(), true);
		mTitleBar.showDefaultBack();
		
		String mStrPhone = App.getInst().getLoginBean().getMobile();
		if(mStrPhone == null || mStrPhone.equals("") || mStrPhone.length() != 11)
			mTitleBar.setTitle(getResources().getString(R.string.bind_phone));
		else
			mTitleBar.setTitle(getResources().getString(R.string.change_phone));
		
		mAccount = (EditText) findViewById(R.id.pv_et_account);
		mCode = (EditText) findViewById(R.id.pv_et_code);
		mGetCode = (Button) findViewById(R.id.pv_btn_getcode);
		mRegister = (Button) findViewById(R.id.pv_btn_res);
	}

	@Override
	public void initDatas() {
		mRunnable = new Runnable() {
			
			@Override
			public void run() {
                try{    
                    Message message = getHandler().obtainMessage(COUNTDOWN, null);
					message.sendToTarget();
                }catch (Exception e) {  
                	e.printStackTrace();
                }  
			}
		};
	}

	@Override
	public void installListeners() {
		mGetCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getCode();
			}
		});
		mRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});
	}
	
	private void getCode(){
		mStrAccount = mAccount.getText().toString().trim();
		if(TextUtils.isEmpty(mStrAccount)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_account));
			return;
		}
		
		if (mPhone_CountDown != 0) { 
			return;
		}
		
		mPhone_CountDown = 60;
		setRequestEnable(false);
		
		RequestParams params = new RequestParams();
		params.put("sms_bind_num", mStrAccount);
		HttpUtil.post(JUrl.SITE + JUrl.URL_GETPHONECODE, params, new MyStatusResponseHandler() {
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.showToast(getActivity(), "验证码发送成功，请查收");
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.showToast(getActivity(), message);
				setRequestEnable(true);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Common.showHttpFailureToast(getActivity());
				setRequestEnable(true);
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
		
	}
	
	private void register(){
		mStrAccount = mAccount.getText().toString().trim();
		mStrCode = mCode.getText().toString().trim();
		if(TextUtils.isEmpty(mStrAccount)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_phone));
			return;
		}
		if(TextUtils.isEmpty(mStrCode)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_code));
			return;
		}
		UIHelper.addPD(getActivity(), "正在提交...");
		RequestParams params = new RequestParams();
		params.put("sms_bind_num", mStrAccount);
		params.put("sms_bind_key", mStrCode);
		HttpUtil.post(JUrl.SITE + JUrl.URL_PHONEVERIFY, params, new MyStatusResponseHandler() {
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.removePD();
				UIHelper.showToast(getActivity(), message);
				LoginBean mBean = App.getInst().getLoginBean();
				mBean.setMobile(data);
				App.getInst().saveLoginBean(mBean);
				Intent intent = new Intent();
				intent.putExtra("result", data);
				setResult(Const.BIND_RESULT_CODE,intent);
				finish();
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.removePD();
				UIHelper.showToast(getActivity(), message);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				UIHelper.removePD();
				Common.showHttpFailureToast(getActivity());
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case COUNTDOWN:
			mPhone_CountDown--;
			if (mPhone_CountDown > 0) { 
				setRequestEnable(false);
			} else { 
				setRequestEnable(true);
			}
			break;  
		}
	}
	
	private void setRequestEnable(boolean value){
		if(value){
			mPhone_CountDown = 0;
			mGetCode.setText(getResources().getString(R.string.get_code));
			mGetCode.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.common_btn_bg_blue));
		}else{
			mGetCode.setText(mPhone_CountDown + "秒后重新获取");
			mGetCode.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.shape_blue_btn_unenable_bg));
			getHandler().postDelayed(mRunnable, 1000);
		}
	}
	
	@Override
	public Activity getActivity() {
		return this;
	}

}
