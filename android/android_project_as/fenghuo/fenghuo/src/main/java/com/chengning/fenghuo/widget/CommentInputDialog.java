package com.chengning.fenghuo.widget;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.chengning.common.base.BaseDialogFragment;
import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.Consts;
import com.chengning.fenghuo.MyJsonHttpResponseHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.data.bean.BaseArticlesBean;
import com.chengning.fenghuo.data.bean.CommentSuccessBean;
import com.chengning.fenghuo.event.CommentSuccessEvent;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.JUrl;
import com.chengning.fenghuo.util.SPHelper;
import com.chengning.fenghuo.util.UIHelper;
import com.chengning.fenghuo.util.Utils;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import de.greenrobot.event.EventBus;

public class CommentInputDialog extends BaseDialogFragment {

	private View mView;
	private View mEmpty;
	private View mCancel;
	private View mPublish;
	private EditText mInput;
	private EditText mName;
	
	private FragmentActivity mContext;

	private String mRootTid;
	
	private boolean isPublish;
	private String mStrAid;
	private Consts.ArticleCommentType mType;
	private boolean isThreeReply = false;

	public void setBean(FragmentActivity context, BaseArticlesBean bean){
		mContext = context;
		Bundle args = new Bundle();
		args.putSerializable("bean", bean);
		setArguments(args);
	}

	public void setBean(FragmentActivity context, BaseArticlesBean bean, Consts.ArticleCommentType type){
		mContext = context;
		Bundle args = new Bundle();
		args.putSerializable("bean", bean);
		args.putSerializable("comment_type", type);
		setArguments(args);
	}
	
	public void publish(){
		String name = mName.getText().toString();
		String input = mInput.getText().toString();
		// check input
		if(TextUtils.isEmpty(input) || input.length() < 2){
			UIHelper.showToast(mContext, "请至少输入两个字符");
			mPublish.setEnabled(true);
		}else{
			// publish
			if (!Common.hasNet()) {
	         	Common.showHttpFailureToast(mContext);
	         	mPublish.setEnabled(true);
	         	return;
			}
			BaseArticlesBean bean = (BaseArticlesBean) getArguments().getSerializable("bean") ;
			
			String url = JUrl.SITE + JUrl.URL_DO_COMMENT_PUBLISH;
//			url = App.getInst().isLogin() ? url + JUrl.URL_DO_COMMENT_PUBLISH : url + JUrl.URL_DO_ANON_COMMENT_PUBLISH;
			RequestParams params = new RequestParams();
			params.put("content", input);
			params.put("topictype", "reply"); 
			params.put("totid", bean.getTid());

			params.put("roottid", bean.getTid());
			if (isThreeReply) {
				params.put("roottid", bean.getTotid());
			}

			params.put("touid", bean.getUid()); 
			params.put("from", bean.getFrom());
			HttpUtil.post(url, params, new MyJsonHttpResponseHandler() {
				
				public void onFailure(int statusCode, Header[] headers,
		    			Throwable throwable, JSONObject error) {
	
		         	Common.showHttpFailureToast(mContext);
		         	mPublish.setEnabled(true);
				};
				
				@Override
				public void onDataSuccess(int status, String mod, String message,
						String data, JSONObject obj) {
					isPublish = true;
					SPHelper.getInst().removeByKey(mStrAid);
					UIHelper.showToast(mContext, message);
					dismissAllowingStateLoss();
					mPublish.setEnabled(true);
					Gson gson = new Gson();
					CommentSuccessBean bean = gson.fromJson(data, CommentSuccessBean.class);
					if (bean != null && mType != null) {
						EventBus.getDefault().post(new CommentSuccessEvent(bean.getList(), mType));
					}

					try {
						Utils.showHints(mContext, data);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onDataFailure(int status, String mod, String message,
						String data, JSONObject obj) {
		             
		             UIHelper.showToast(mContext, message);
		             mPublish.setEnabled(true);
				}
			}); 
		}
	}

	@Override
	public View configContentView() {
		mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_comment_input, null);
		return mView;
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
				mPublish.setEnabled(false);
				publish();
			}
		});
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
		isPublish = false;
		
		BaseArticlesBean bean = (BaseArticlesBean) getArguments().getSerializable("bean") ;
		mStrAid = bean.getTid();
		mType = (Consts.ArticleCommentType) getArguments().getSerializable("comment_type");
		String content = SPHelper.getInst().getString(mStrAid);
		if(!TextUtils.isEmpty(content)){
			mInput.setText(content);
			mInput.setSelection(content.length());
		}
		if (isThreeReply && bean != null && !TextUtils.isEmpty(bean.getNickname())) {
			mInput.setHint("回复 " + bean.getNickname() + ":");
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

	public void setIsThreeReply(boolean isThreeReply) {
		this.isThreeReply = isThreeReply;
	}
}
