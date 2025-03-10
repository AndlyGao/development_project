package com.shenyuan.militarynews.activity;

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
import com.loopj.android.http.RequestParams;
import com.shenyuan.militarynews.MyStatusResponseHandler;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.chengning.common.util.StatusBarUtil;;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.views.TitleBar;

public class PwdFindActivity extends BaseActivity {

	private TitleBar mTitleBar;
	private EditText mPhone;
	private Button mGetCode;
	private EditText mCode;
	private Button mNext;
	
	private String mStrPhone;
	private String mStrCode;
	
	private int mPhone_CountDown = 0;
	private Runnable mRunnable;
	private static final int COUNTDOWN = 0x01;
	
	private static final int REQUEST_MODIFY = 0x10;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_pwdfind);
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
		mTitleBar.setTitle(getResources().getString(R.string.findpassword));
		
		mPhone = (EditText) findViewById(R.id.fp_et_phone);
		mGetCode = (Button) findViewById(R.id.fp_btn_getcode);
		mCode = (EditText) findViewById(R.id.fp_et_code);
		mNext = (Button) findViewById(R.id.fp_nextbtn);
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
		mNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				verify();
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
		}else{
			mGetCode.setText(mPhone_CountDown + "秒后重新获取");
			getHandler().postDelayed(mRunnable, 1000);
		}
	}
	
	private void getCode(){
		mStrPhone = mPhone.getText().toString().trim();
		if(TextUtils.isEmpty(mStrPhone)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_account));
			return;
		}
		
		if (mPhone_CountDown != 0) { 
			return;
		}
		
		mPhone_CountDown = 60;
		setRequestEnable(false);
		
		RequestParams params = new RequestParams();
		params.put("sms_bind_num", mStrPhone);
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
	
	private void verify(){
		mStrPhone = mPhone.getText().toString().trim();
		mStrCode = mCode.getText().toString().trim();
		if(TextUtils.isEmpty(mStrPhone)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_account));
			return;
		}
		if(TextUtils.isEmpty(mStrCode)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_code));
			return;
		}

		UIHelper.addPD(getActivity(), "正在验证...");
		RequestParams params = new RequestParams();
		params.put("sms_bind_num", mStrPhone);
		params.put("bind_key", mStrCode);
		HttpUtil.post(JUrl.SITE + JUrl.URL_VERIFYCODE, params, new MyStatusResponseHandler() {
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.removePD();
				startActivityForResult(new Intent(getActivity(), PwdModifyActivity.class)
					.putExtra(PwdModifyActivity.BOOTTYPE, PwdModifyActivity.FORGETPWD)
					.putExtra(PwdModifyActivity.PHONE, mStrPhone), REQUEST_MODIFY);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_MODIFY && resultCode == RESULT_OK){
			finish();
		}
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
