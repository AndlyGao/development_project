package com.shenyuan.militarynews.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.chengning.common.base.BaseActivity;
import com.chengning.common.base.BaseResponseBean;
import com.chengning.common.base.MyRetrofitResponseCallback;
import com.chengning.common.base.util.RetrofitManager;
import com.chengning.common.util.StatusBarUtil;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.views.TitleBar;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

;

public class PwdModifyActivity extends BaseActivity {
	
	public static final String BOOTTYPE = "boot-type";
	public static final String PHONE = "phone";
	public static final int FORGETPWD = 1;
	public static final int MODIFYPWD = 2;
	
	private TitleBar mTitleBar;
	private EditText mOldPwd;
	private EditText mNewPwd;
	private EditText mNewPwdAgain;
	private Button mCommit;
	
	private String mStrOldPwd;
	private String mStrNewPwd;
	private String mStrNewPwdAgain;
	private String mStrPhone;
	
	private View mOneLine;
	private int mBoottype;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_pwdmodify);
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
		mTitleBar.setTitle(getResources().getString(R.string.setnewpwd));
		
		mOldPwd = (EditText) findViewById(R.id.pm_et_old);
		mNewPwd = (EditText) findViewById(R.id.pm_et_new);
		mNewPwdAgain = (EditText) findViewById(R.id.pm_et_newagain);
		mCommit = (Button) findViewById(R.id.pm_btn_commit);
		
		mOneLine = findViewById(R.id.pm_v_oneline);
	}

	@Override
	public void initDatas() {
		mBoottype = getIntent().getIntExtra(BOOTTYPE, FORGETPWD);
		if(mBoottype == FORGETPWD){
			mOldPwd.setVisibility(View.GONE);
			mOneLine.setVisibility(View.GONE);
		}else if(mBoottype == MODIFYPWD){
			
		}
		
		mStrPhone = getIntent().getStringExtra(PHONE);
	}

	@Override
	public void installListeners() {
		mCommit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mBoottype == FORGETPWD){
					modifyPwByForget();
				}else if(mBoottype == MODIFYPWD){
					modifyPwByNormal();
				}
			}
		});
	}
	
	@Override
	public void processHandlerMessage(Message msg) {

	}
	
	private void modifyPwByForget(){
		mStrNewPwd = mNewPwd.getText().toString().trim();
		mStrNewPwdAgain = mNewPwdAgain.getText().toString().trim();
		if(TextUtils.isEmpty(mStrNewPwd)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_newpassword));
			return;
		}
		if(TextUtils.isEmpty(mStrNewPwdAgain)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_newpasswordagain));
			return;
		}

		HashMap params = new HashMap<String, String>();
		params.put("sms_bind_num", mStrPhone);
		params.put("userpwd", mStrNewPwd);
		params.put("confirm", mStrNewPwdAgain);

		Observable<BaseResponseBean> observable
				= App.getInst().getApiInterface().post(JUrl.URL_MODIFYPWBYFORGET, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

			@Override
			public void onSubscribe(Disposable d) {
				UIHelper.addPD(getActivity(), "正在提交..");
				super.onSubscribe(d);
			}

			@Override
			public void onComplete() {
				UIHelper.removePD();
				super.onComplete();
			}

			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
				UIHelper.showToast(getActivity(), message);
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				UIHelper.showToast(getActivity(), message);
			}

			@Override
			public void onError(Throwable t) {
				Common.showHttpFailureToast(getActivity());
				super.onError(t);
			}
		});

	}
	
	private void modifyPwByNormal(){
		mStrOldPwd = mOldPwd.getText().toString().trim();
		mStrNewPwd = mNewPwd.getText().toString().trim();
		mStrNewPwdAgain = mNewPwdAgain.getText().toString().trim();
		if(TextUtils.isEmpty(mStrOldPwd)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_oldpassword));
			return;
		}
		if(TextUtils.isEmpty(mStrNewPwd)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_newpassword));
			return;
		}
		if(TextUtils.isEmpty(mStrNewPwdAgain)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_newpasswordagain));
			return;
		}
		if(!mStrNewPwd.equals(mStrNewPwdAgain)){
			UIHelper.showToast(getActivity(), "两次密码输入不一致");
			return;
		}

		HashMap params = new HashMap<String, String>();
		params.put("userpwd", mStrOldPwd);
		params.put("confirm", mStrNewPwd);

		Observable<BaseResponseBean> observable
				= App.getInst().getApiInterface().post(JUrl.URL_MODIFYPWBYNORMAL, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

			@Override
			public void onSubscribe(Disposable d) {
				UIHelper.addPD(getActivity(), "正在提交..");
				super.onSubscribe(d);
			}

			@Override
			public void onComplete() {
				UIHelper.removePD();
				super.onComplete();
			}

			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
				UIHelper.showToast(getActivity(), message);
				finish();
			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				UIHelper.showToast(getActivity(), message);
			}

			@Override
			public void onError(Throwable t) {
				Common.showHttpFailureToast(getActivity());
				super.onError(t);
			}
		});

	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(BOOTTYPE, mBoottype);
		if(mBoottype == FORGETPWD){
			savedInstanceState.putString(PHONE, mStrPhone);
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mBoottype = savedInstanceState.getInt(BOOTTYPE);
		if(mBoottype == FORGETPWD){
			mStrPhone = savedInstanceState.getString(PHONE);
		}
	}

	@Override
	public Activity getActivity() {
		return this;
	}

}
