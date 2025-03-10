package com.shenyuan.militarynews.views;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.chengning.common.base.BaseDialogFragment;
import com.chengning.common.base.BaseResponseBean;
import com.chengning.common.base.MyRetrofitResponseCallback;
import com.chengning.common.base.util.RetrofitManager;
import com.google.gson.Gson;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const.PointActionType;
import com.shenyuan.militarynews.LoginManager;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.beans.data.DirectoratePointBean;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.SPHelper;
import com.shenyuan.militarynews.utils.TaskUpdateUtil;
import com.shenyuan.militarynews.utils.UIHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class CommentInputDialog extends BaseDialogFragment {

	private static final String TAG = CommentInputDialog.class.getSimpleName();
	
	private View mView;
	private View mEmpty;
	private View mCancel;
	private View mPublish;
	private EditText mInput;
	private EditText mName;
	
	private boolean isPublish;
	private String mStrAid;
	
	public void setAid(String aid){
		Bundle args = new Bundle();
		args.putString("aid", aid);
		setArguments(args);
	}
	
	

	@Override
	public View configContentView() {
		mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_comment_input, null);
		return mView;
	}

	@Override
	public void initView() {
		mEmpty = mView.findViewById(R.id.comment_input_empty);
		mCancel = mView.findViewById(R.id.comment_input_cancel);
		mPublish = mView.findViewById(R.id.comment_input_publish);
		mInput = (EditText) mView.findViewById(R.id.comment_input_input);
		mName = (EditText) mView.findViewById(R.id.comment_input_name);
	}

	@Override
	public void initData() {
		boolean isLogin = App.getInst().isLogin();
		mName.setVisibility(isLogin ? View.GONE : View.VISIBLE);
		mName.setText(isLogin ? 
				LoginManager.getInst().getLoginDbBean().getUserinfo().getUname()
				: SPHelper.getInst().getString(SPHelper.KEY_COMMENT_USERNAME));
		
		isPublish = false;
		mStrAid = getArguments().getString("aid");
		String content = SPHelper.getInst().getString(mStrAid);
		if(!TextUtils.isEmpty(content)){
			mInput.setText(content);
			mInput.setSelection(content.length());
		}
	}

	@Override
	public void initListener() {
		
		mEmpty.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		mPublish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Map<String, String> mLoginType = new HashMap<String, String>();
				if (App.getInst().isLogin()) {
					mLoginType.put("usertype", "loginuser");
				} else {
					mLoginType.put("usertype", "anonymous");
				}
				mLoginType.put("article_id", mStrAid);
				MobclickAgent.onEvent(getContext(), "article_comment", mLoginType);
				mPublish.setEnabled(false);
				publish(mStrAid);
			}
		});
	}
	
	public void publish(String aid){
		String name = mName.getText().toString();
		String input = mInput.getText().toString();
		
		SPHelper.getInst().saveString(SPHelper.KEY_COMMENT_USERNAME, name);
		
		// check input
		if(TextUtils.isEmpty(input) || input.length() < 2){
			UIHelper.showToast(getContext(), "请至少输入两个字符");
			mPublish.setEnabled(true);
		}else{
			if(!Common.hasNet()){
	         	Common.showHttpFailureToast(getContext());
	         	mPublish.setEnabled(true);
	         	return;
			}
			// publish

			HashMap params = new HashMap<String, String>();
			params.put("aid", aid);
			params.put("msg", input);
			if (!App.getInst().isLogin()) {
				params.put("username", name);
			}
			Observable<BaseResponseBean> observable
					= App.getInst().getApiInterface().post(JUrl.URL_DO_COMMENT_REPLY_PUBLISH, params);
			RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

				@Override
				public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
					isPublish = true;
					SPHelper.getInst().removeByKey(mStrAid);
					UIHelper.showToast(getContext(), message);
					dismissAllowingStateLoss();
					mPublish.setEnabled(true);
					DirectoratePointBean bean = new Gson().fromJson(data,DirectoratePointBean.class);
					TaskUpdateUtil.showHints((FragmentActivity) getContext(), bean, PointActionType.REPLY);
				}

				@Override
				public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
					UIHelper.showToast(getContext(), message);
					mPublish.setEnabled(true);
				}

				@Override
				public void onError(Throwable t) {
					Common.showHttpFailureToast(getContext());
					mPublish.setEnabled(true);
					super.onError(t);
				}
			});
		}
	}
	
	@Override
	public void onDestroy() {
		if(!isPublish){
			String input = mInput.getText().toString();
			if(!TextUtils.isEmpty(input)){
				SPHelper.getInst().saveString(mStrAid, input);
			}
		}
		super.onDestroy();
	}
	
}
