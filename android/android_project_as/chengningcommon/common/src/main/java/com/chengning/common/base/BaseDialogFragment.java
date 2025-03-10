package com.chengning.common.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chengning.common.app.ActivityInfo;

public abstract class BaseDialogFragment extends DialogFragment {
	private BaseShowSuccessListener mShowLisener;

	private Activity mContext;

	@Override
	public void onAttach(Activity activity) {
		mContext = activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Translucent_NoTitleBar);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return configContentView();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
		initData();
		initListener();

	}

	/**
	 * 获取上下文
	 * @return
	 */
	public Activity getContext() {
		return mContext;
	}

	/**
	 * 配置根视图
	 * @return
	 */
	public abstract View configContentView();

	/**
	 * 初始化视图
	 */
	public abstract void initView();

	/**
	 * 初始化数据
	 */
	public abstract void initData() ;

	/**
	 * 初始化监听器
	 */
	public abstract void initListener();

	@Override
	public void show(FragmentManager manager, String tag) {
		if (mShowLisener != null) {
			mShowLisener.success();
		}
		super.show(manager, tag);
	}

	public void showAllowingStateLoss(BaseFragmentActivity activity, FragmentManager manager, String tag) {
		if (activity != null && activity.getActivityInfo().getActivityState() != ActivityInfo.ActivityState.Running) {
			return;
		}
		if (mShowLisener != null) {
			mShowLisener.success();
		}
		if (!isAdded()) {
			super.show(manager, tag);
		}
	}

	public void setShowSuccessListener(BaseShowSuccessListener listener){
		this.mShowLisener = listener;
	}

	public interface BaseShowSuccessListener {
		void success();
	}

}
