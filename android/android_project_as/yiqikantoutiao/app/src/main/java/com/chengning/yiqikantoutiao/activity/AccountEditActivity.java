package com.chengning.yiqikantoutiao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVInstallation;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.util.PermissionManager;
import com.chengning.common.util.HttpUtil;
import com.chengning.yiqikantoutiao.LoginManager;
import com.chengning.yiqikantoutiao.MyJsonHttpResponseHandler;
import com.chengning.yiqikantoutiao.R;
import com.chengning.yiqikantoutiao.util.Common;
import com.chengning.yiqikantoutiao.util.ImagePickHelper;
import com.chengning.yiqikantoutiao.util.JUrl;
import com.chengning.yiqikantoutiao.util.UIHelper;
import com.chengning.yiqikantoutiao.util.Utils;
import com.chengning.yiqikantoutiao.widget.SwitchButton;
import com.chengning.yiqikantoutiao.widget.TitleBar;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 完善资料页
 * @author Administrator
 *
 */
public class AccountEditActivity extends BaseFragmentActivity {

	private static final int REGIST_CODE = 11; 
	private static final int REGIST_SUCCESS = 10;
	
	private String mFinalFace = "finalface.jpg";
	
	private ImageView mUserImg;
	private Button mSubmitBtn;
	private EditText mPwdEv;
	private EditText mNameEv;
	private SwitchButton mPwdSwitch;
	private int mLoginFrom; 
	private Bundle mLoginParam;
	private TitleBar titleBar; 
	
	private ImagePickHelper mImagePickHelper;
	private String mface;
	
	//是否自己选择了头像
	private boolean isSelfAvatar = false;
	private JSONObject obj;
	private TextView mUseProtoTv;
	private PermissionManager permissionManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_account_edit);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case REGIST_CODE:
			RegistUser();
			break;
		case REGIST_SUCCESS:
			Login();
			break; 
		}
	}
	
	public void Login() {  
		String name = mNameEv.getText().toString();
		String pwd = mPwdEv.getText().toString();
		RequestParams params = new RequestParams();
		params.put("username", name);
		params.put("password", pwd);
		
		if (Common.isMIUI()) {
			//小米推送唯一标识（暂定）
			params.put("regid", MiPushClient.getRegId(getActivity()));
		}
		params.put("devicetype", "2");
		params.put("devicetoken", AVInstallation.getCurrentInstallation().getInstallationId());
		params.put("objectid", AVInstallation.getCurrentInstallation().getObjectId());
		
		PersistentCookieStore myCookieStore = new PersistentCookieStore(this);  
		HttpUtil.getClient().setCookieStore(myCookieStore);  
		 
		HttpUtil.post(getActivity(), JUrl.SITE + JUrl.URL_LOGIN, params, new MyJsonHttpResponseHandler() {
	         @Override
	         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
	             UIHelper.removePD(); 
	             Common.handleHttpFailure(getActivity(), throwable); 
	         };  
	         
			@Override
			public void onDataSuccess(int status, String mod, String message,
                                      String data, JSONObject obj) {
				LoginManager.getInst().getUserInfo(getActivity(), data);
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
                                      String data, JSONObject obj) {
				UIHelper.showToast(getActivity(),message); 
			};   
		});
 
	}

	@Override
	public void initViews() {
		titleBar = new TitleBar(getActivity(), true);
		titleBar.setTitle("完善资料");
		titleBar.showDefaultBack();
		
		mUserImg = (ImageView)this.findViewById(R.id.accountedit_userimg);
		mNameEv = (EditText)this.findViewById(R.id.accountedit_name_edit);
		mPwdEv = (EditText)this.findViewById(R.id.accountedit_pwd_edit);
		mSubmitBtn = (Button)this.findViewById(R.id.accountedit_submit_btn);
		mPwdSwitch = (SwitchButton)this.findViewById(R.id.accountedit_pwd_switch);
		mUseProtoTv = (TextView) findViewById(R.id.phone_regist_useproto_tv);
	}

	@Override
	public void initDatas() {
		Intent intent = this.getIntent();
		mLoginFrom = intent.getIntExtra("login_from", 0);
		mLoginParam = intent.getBundleExtra("login_param");
		mImagePickHelper = new ImagePickHelper(this, mFinalFace);
		
		if (JUrl.Login_From_Sina == mLoginFrom) {
			try {
				obj = new JSONObject(mLoginParam.getString("json"));
				mface =  obj.getString("bind_avatar_large");
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		} else {
			mface = mLoginParam.getString("face");
		}
		
		Utils.showFace(mface, mUserImg);
		
		initSummitBtnText(mLoginFrom);
	}

	/**
	 * 初始化注册按钮显示文字
	 * @param loginFrom
	 */
	private void initSummitBtnText(int loginFrom) {
		switch (loginFrom) {
		case JUrl.Login_From_Phone:
			mSubmitBtn.setText("完成注册");
			break;
		case JUrl.Login_From_QQ:
			mSubmitBtn.setText("下一步");
			break;
		case JUrl.Login_From_Sina:
			mSubmitBtn.setText("下一步");
			break;
		case JUrl.Login_From_WeiXin:
			mSubmitBtn.setText("下一步");
			break; 
		default:
			break;
		}
	}

	@Override
	public void installListeners() {
		
		mUserImg.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!Environment.getExternalStorageState()
		                   .equals(Environment.MEDIA_MOUNTED))      //如果SD卡存在，则获取跟目录
			    {                           
			    	UIHelper.showToast(getActivity(), getResources().getString(R.string.error_sdcard));
			    } else {
			    	showImgDialog();
			    }
			} 
		}); 
		
		mPwdSwitch.setChecked(true);
		mPwdSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				int position = mPwdEv.getSelectionStart();
				if(isChecked) {
					mPwdEv.setTransformationMethod(PasswordTransformationMethod.getInstance());//设置密码为不可见。
				} else {
					mPwdEv.setTransformationMethod(null); 
				}
				mPwdEv.setSelection(position);
			}  
		});
		
		mSubmitBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(Common.hasNet()) {
					if(!TextUtils.isEmpty(mNameEv.getText()) &&
					!TextUtils.isEmpty(mPwdEv.getText())) {

						UIHelper.addPD(getActivity(), getResources().getString(R.string.handle_hint)); 
						//先传图片后注册
						 
						RegistUser();  
					} else {
						UIHelper.showToast(getActivity(),"用户名和密码不能为空"); 
					}
				} else {
					UIHelper.showToast(getActivity(),R.string.intnet_fail);
				} 
				
			} 
		});
		
		mUseProtoTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), UseProtoActivity.class));
			}
		});
	}
	
	public void RegistUser() { 
		if(Common.hasNet()) {
			String name = mNameEv.getText().toString();
			String pwd = mPwdEv.getText().toString();
			if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
				switch(mLoginFrom) {
				case JUrl.Login_From_Phone:
					PhoneRegistUser(mLoginParam, name, pwd, mFinalFace);
					break;
				case JUrl.Login_From_QQ:
					QQRegistUser(mLoginParam, name, pwd);
					break;
				case JUrl.Login_From_Sina:
					SinaRegistUser(obj, name, pwd);
					break;
				case JUrl.Login_From_WeiXin:
					WeiXinRegistUser(mLoginParam, name, pwd);
					break; 
				default:
					UIHelper.removePD(); 
					UIHelper.showToast(getActivity(), "注册失败,请重新打开应用");
					break;
				} 
			} else {
				UIHelper.showToast(getActivity(), "密码或昵称不能为空");
			}
		} else {
			UIHelper.removePD();
			UIHelper.showToast(getActivity(), R.string.intnet_fail);
		}
	}

    public void onResume()  {
        super.onResume();
    }
	
    /***
     * 新浪注册
     * @param name
     * @param pwd
     */
	public void SinaRegistUser(JSONObject obj, String name, String pwd) {
		RequestParams params = new RequestParams();
		try {
			params.put("bind_uid", obj.getString("bind_uid"));
			params.put("bind_access_token", obj.getString("bind_access_token"));
			params.put("bind_expires_in", obj.getString("bind_expires_in"));
			params.put("bind_name", obj.getString("bind_name"));
			params.put("bind_screen_name", obj.getString("bind_screen_name"));
			params.put("bind_avatar_large", obj.getString("bind_avatar_large"));
			params.put("bind_domain", obj.getString("bind_domain"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		params = handleFaceParams(params, isSelfAvatar);
		params.put("nickname", name);
		params.put("password", pwd);
		
		if (Common.isMIUI()) {
			//小米推送唯一标识（暂定）
			params.put("regid", MiPushClient.getRegId(getActivity()));
		}
		params.put("devicetype", "2");
		params.put("devicetoken", AVInstallation.getCurrentInstallation().getInstallationId());
		params.put("objectid", AVInstallation.getCurrentInstallation().getObjectId());

		HttpUtil.post(getActivity(), JUrl.SITE + JUrl.Bind_Edit_Sina, params, new MyJsonHttpResponseHandler() {
	    	 // 成功后返回一多个json 
	         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
	             Common.handleHttpFailure(getActivity(), throwable);
	         }; 
	         
	         @Override
	         public void onFinish() {
	        	 UIHelper.removePD();
	         }
	         
			@Override
			public void onDataSuccess(int status, String mod, String message,
                                      String data, JSONObject obj) {
				launchToVerifyPhone(data);
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
                                      String data, JSONObject obj) {
					UIHelper.showToast(getActivity(), message);
			};   
		}); 
	}
	
	/**
	 * 微信注册
	 * @param bundle
	 * @param name
	 * @param pwd
	 */
	public void WeiXinRegistUser(Bundle bundle, String name, String pwd) {
		RequestParams params = new RequestParams();
		params.put("unionid", bundle.getString("unionid"));
		params.put("nickname", name);
		params.put("password", pwd);
		
		params = handleFaceParams(params, isSelfAvatar);
		if (Common.isMIUI()) {
			//小米推送唯一标识（暂定）
			params.put("regid", MiPushClient.getRegId(getActivity()));
		}
		params.put("devicetype", "2");
		params.put("devicetoken", AVInstallation.getCurrentInstallation().getInstallationId());
		params.put("objectid", AVInstallation.getCurrentInstallation().getObjectId());
		
		HttpUtil.post(getActivity(), JUrl.SITE + JUrl.Bind_Edit_WeiXin, params, new MyJsonHttpResponseHandler() {
            // 成功后返回一多个json
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                 Common.handleHttpFailure(getActivity(), throwable);
            };

            @Override
	         public void onFinish()  {
	        	 UIHelper.removePD();
	         }

			@Override
			public void onDataSuccess(int status, String mod, String message,
                                      String data, JSONObject obj) {
				launchToVerifyPhone(data);
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
                                      String data, JSONObject obj) {
				
					UIHelper.showToast(getActivity(), message);
				
			}

            ;
        });
	}
	
	private RequestParams handleFaceParams(RequestParams params, boolean isSelfAvatar) {
		if (isSelfAvatar) {
			File file = Common.creatFile(JUrl.FilePathTemp, mFinalFace);
			try {
				params.put("face", file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			params.put("face", mface);
		}
		return params;
	}

	/**
	 * qq注册
	 * @param bundle
	 * @param name
	 * @param pwd
	 */
	public void QQRegistUser(Bundle bundle, String name, String pwd) {
		RequestParams params = new RequestParams();
		params.put("qq_username", bundle.getString("nickname"));  
		params.put("nickname", name); 
		params.put("password", pwd);
		params = handleFaceParams(params, isSelfAvatar);
		if (Common.isMIUI()) {
			//小米推送唯一标识（暂定）
			params.put("regid", MiPushClient.getRegId(getActivity()));
		}
		params.put("devicetype", "2");
		params.put("devicetoken", AVInstallation.getCurrentInstallation().getInstallationId());
		params.put("objectid", AVInstallation.getCurrentInstallation().getObjectId());
		
		HttpUtil.post(getActivity(), JUrl.SITE + JUrl.Bind_Edit_QQ, params, new MyJsonHttpResponseHandler() {
	    	 // 成功后返回一多个json 
	         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
	             Common.handleHttpFailure(getActivity(), throwable);
	         }; 
	         
	         @Override
	         public void onFinish()  {
	        	 UIHelper.removePD();
	         }
	         
			@Override
			public void onDataSuccess(int status, String mod, String message,
                                      String data, JSONObject obj) {
				launchToVerifyPhone(data);
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
                                      String data, JSONObject obj) {
				
					UIHelper.showToast(getActivity(), message);
				
			};   
		}); 
	}
	
	/**
	 * 跳到验证手机界面
	 * @param data
	 */
	protected void launchToVerifyPhone(String data) {
		
		Intent intent = new Intent();
//		intent.putExtra("uid", data);
		intent.putExtra("action", JUrl.Action_LoginAfterBindPhone);
		intent.putExtra("action_from", mLoginFrom);
		intent.putExtra("user_data", data);
//		intent.putExtra("is_self_avatar", isSelfAvatar);
		intent.setClass(getActivity(), PhoneRegistActivity.class);
		getActivity().startActivity(intent);
		getActivity().finish(); 
	}

	private void PhoneRegistUser(Bundle bundle, String name, String pwd, String face) {
		
		RequestParams params = new RequestParams();
		params.put("sms_bind_num", bundle.getString("phone"));  
		params.put("nickname", name); 
		params.put("password", pwd);
		File file = Common.creatFile(JUrl.FilePathTemp,mFinalFace);
		if(isSelfAvatar && file!=null) {
			try {
				params.put("face", file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (Common.isMIUI()) {
			//小米推送唯一标识（暂定）
			params.put("regid", MiPushClient.getRegId(getActivity()));
		}
		params.put("devicetype", "2");
		params.put("devicetoken", AVInstallation.getCurrentInstallation().getInstallationId());
		params.put("objectid", AVInstallation.getCurrentInstallation().getObjectId());
		HttpUtil.post(getActivity(), JUrl.SITE + JUrl.Bind_User, params, new MyJsonHttpResponseHandler() {
	    	 // 成功后返回一多个json 
	         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
	             Common.handleHttpFailure(getActivity(), throwable);
	         }; 
	         
	         @Override
	         public void onFinish() {
	        	 UIHelper.removePD();
	         }

			@Override
			public void onDataSuccess(int status, String mod, String message,
                                      String data, JSONObject obj) {
				LoginManager.getInst().clearData();
				
				getHandler().obtainMessage(REGIST_SUCCESS, data).sendToTarget();
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
                                      String data, JSONObject obj) {
				UIHelper.showToast(getActivity(), message);
			};   
		}); 
	}

	/***
	 * 
	 */
	private void showImgDialog() {
		permissionManager = new PermissionManager();
		mImagePickHelper.showPickDialog(permissionManager);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		permissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
	}
	
	/**
	 * 回调结果处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(mImagePickHelper.handleActivityResult(requestCode, resultCode, data)){
			ImageLoader.getInstance().clearDiscCache();
			ImageLoader.getInstance().clearMemoryCache();
			StringBuilder path = new StringBuilder("file://").append(JUrl.FilePathTemp).append("/").append(mFinalFace);
			Utils.setCircleImage(path.toString(), mUserImg);
			isSelfAvatar = true;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	

	@Override
	public void uninstallListeners() {
		
	}

	@Override
	public BaseFragmentActivity getActivity() {
		return AccountEditActivity.this;
	}

}
