package com.shenyuan.militarynews.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chengning.common.base.BaseActivity;
import com.chengning.common.base.BaseResponseBean;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.base.MyRetrofitResponseCallback;
import com.chengning.common.base.util.RetrofitManager;
import com.chengning.common.util.StatusBarUtil;
import com.chengning.common.widget.MultiStateView;
import com.chengning.common.widget.MultiStateView.ViewState;
import com.google.gson.Gson;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.LoadStateManager;
import com.shenyuan.militarynews.LoadStateManager.LoadState;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.beans.data.DirectorateTaskBean;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.views.TitleBar;

import java.util.HashMap;

import io.reactivex.Observable;

;


public class DirectorateTaskDetailActivity extends BaseActivity {

	private static final  int DATA_SUCCESS = 0;
	private static final int HTTP_FAIL = 1;
	
	private static final int TASK_EVERYDAY_LOGIN = 1;
	private static final int TASK_SHARE_DISCUSS = 2;
	private static final int TASK_CMT_DISCUSS = 3;
	
	private int actionType;
	private TitleBar mTitleBar;
	
	private ScrollView mScrollView;
	private LinearLayout mLayout;
	private TextView mCountTv;
	private TextView mPointTv;
	private TextView mTaskDes1;
	private TextView mTaskDes2;
	private TextView mIsFinishTv;
	private ImageView mIsFinishIv;
//	private TextView mMoneyTv;
	private String action;
	private DirectorateTaskBean bean;
	private LoadStateManager mLoadStateManager;
	private MultiStateView mMultiStateView;

	
	@Override
	public void onCreate(Bundle paramBundle) {
		
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_task_des_daylogin);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.normalstate_bg), true);
        }
		if (null != paramBundle) {
			actionType = paramBundle.getInt("type");
			bean = (DirectorateTaskBean) paramBundle.getSerializable("bean");
		} else {
			actionType = getIntent().getIntExtra("pos", 0);
		}
		
//		switch (actionType) {
//		case TASK_EVERYDAY_LOGIN:			
//			break;
//		//TODO
//		case TASK_SHARE_DISCUSS:
//			break;
//		default:
//			break;
//		}
		
		super.onCreate(paramBundle);
	}

	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("type", actionType);
        savedInstanceState.putSerializable("bean", bean);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
		actionType = savedInstanceState.getInt("type");
		bean = (DirectorateTaskBean) savedInstanceState.getSerializable("bean");
    }
	
	@Override
	public void processHandlerMessage(Message msg) {

		switch (msg.what) {
		case DATA_SUCCESS:
			bean = (DirectorateTaskBean) msg.obj;
			mTitleBar.setTitle(bean.getRulename());
			String str = "";
			if (1 == bean.getCycletype()) {
				str = "次/日";
			}
			if (0 == bean.getRewardnum()) {
				
				mLayout.setVisibility(View.GONE);
				
			} else {
				StringBuffer rewardNum = new StringBuffer();
				rewardNum.append(bean.getRewardnum()).append(str);
				mCountTv.setText(rewardNum.toString());
			}
			
			if ("完成".equals(bean.getComplete())) {
				mIsFinishTv.setText("已完成");
				mIsFinishTv.setTextColor(this.getResources().getColor(R.color.blue));
				mIsFinishIv.setBackgroundResource(R.drawable.task_instr_btn_fini);
			}else{
				mIsFinishTv.setText("未完成");
//				mIsFinishTv.setTextColor(this.getResources().getColor(R.color.content_color));
				mIsFinishIv.setBackgroundResource(R.drawable.task_instr_btn_unfini);
			}
			
			StringBuffer mPointStr = new StringBuffer();
			mPointStr.append("+").append(bean.getExtcredits2());
			mPointTv.setText(mPointStr.toString());
//			mMoneyTv.setText("+" + bean.getExtcredits2());

			mLoadStateManager.setState(LoadState.Success);
			
			break;
		case HTTP_FAIL :
			
			mLoadStateManager.setState(LoadState.Failure);
			
			break;

		default:
			break;
		}
	}

	@Override
	public void initViews() {

		mTitleBar = new TitleBar(getActivity(), true);
		
		mTitleBar.showDefaultBack();
		
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		
		mScrollView = (ScrollView) findViewById(R.id.directorate_task_detail_scroll_view);
		mLayout = (LinearLayout) findViewById(R.id.directorate_task_detail_count_ll);
		mCountTv = (TextView) findViewById(R.id.directorate_task_detail_count);
		mPointTv = (TextView) findViewById(R.id.directorate_task_detail_point);
		mTaskDes1 = (TextView) findViewById(R.id.task_des_tv1);
		mTaskDes2 = (TextView) findViewById(R.id.task_des_tv2);
		mIsFinishTv = (TextView) findViewById(R.id.directorate_task_finish_text);
		mIsFinishIv = (ImageView) findViewById(R.id.directorate_task_finish_img);
	}

	@Override
	public void initDatas() {
		
		mLoadStateManager = new LoadStateManager();
		mLoadStateManager.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {
			@Override
			public void OnStateChange(LoadState state, Object obj) {
				switch (state) {case Init:
					mMultiStateView.setViewState(ViewState.LOADING);
					break;
				case Success:
					mMultiStateView.setViewState(ViewState.CONTENT);
					break;
				case Failure:
					mMultiStateView.setViewState(ViewState.ERROR);
					break;
				default:
					break;
				}
			}
		});
		
		
		mLoadStateManager.setState(LoadState.Init);
		
		if (null != bean) {
			getHandler().obtainMessage(DATA_SUCCESS, bean).sendToTarget();
		} else {
			action = getIntent().getStringExtra("action");
			getTaskDetail(getActivity(), action);
		}
		if(TextUtils.equals(action,"login")){
			mTaskDes1.setText(getActivity().getResources().getString(R.string.directorate_task_detail_login_text1));
			mTaskDes2.setText(getActivity().getResources().getString(R.string.directorate_task_detail_login_text2));			
		}else if(TextUtils.equals(action, "share")){
			mTaskDes1.setText(getActivity().getResources().getString(R.string.directorate_task_detail_share_text1));
			mTaskDes2.setText(getActivity().getResources().getString(R.string.directorate_task_detail_share_text2));
		}else if(TextUtils.equals(action, "reply")){
			mTaskDes1.setText(getActivity().getResources().getString(R.string.directorate_task_detail_comment_text1));
			mTaskDes2.setText(getActivity().getResources().getString(R.string.directorate_task_detail_comment_text2));
		}
		
	}

	/**
	 * 获取任务详情
	 * @param context
	 * @param action
	 */
	private void getTaskDetail(final Activity context, String action) {
		HashMap params = new HashMap<String, String>();
		params.put("action", action);

		Observable<BaseResponseBean> observable
				= App.getInst().getApiInterface().get(JUrl.URL_DIRECTORATE_DETAIL_INFO, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {
			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
				DirectorateTaskBean bean = new Gson().fromJson(data, DirectorateTaskBean.class);
				getHandler().obtainMessage(DATA_SUCCESS, bean).sendToTarget();
			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				UIHelper.showToast(getActivity(), message);
				getHandler().obtainMessage(HTTP_FAIL).sendToTarget();
			}

			@Override
			public void onError(Throwable t) {
				Common.handleHttpFailure(context, t);
				getHandler().obtainMessage(HTTP_FAIL).sendToTarget();
				super.onError(t);
			}
		});
	}

	@Override
	public void installListeners() {

		mMultiStateView.setRefreshOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				getTaskDetail(getActivity(), action);
			}
		});
		
	}

	@Override
	public void uninstallListeners() {

	}

	public static void launch(Activity context,String action, int pos){
		Intent intent = new Intent(context, DirectorateTaskDetailActivity.class);
		intent.putExtra("action", action);
		intent.putExtra("pos", pos);
		context.startActivity(intent);
	}

	@Override
	public Activity getActivity() {
		return DirectorateTaskDetailActivity.this;
	}
}
