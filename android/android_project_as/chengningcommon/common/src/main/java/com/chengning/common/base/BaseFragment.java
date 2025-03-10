package com.chengning.common.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chengning.common.app.ActivityInfo;

import me.yokeyword.fragmentation.SupportFragment;

public abstract class BaseFragment extends SupportFragment implements
		IBaseActivity {

	private Activity mContext;
	private Handler mHandler;

	public Activity getContext(){
		return mContext;
	}
	
	public void initFragment() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				processHandlerMessage(msg);
			}
		};

		initViews();
		initDatas();
		installListeners();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity){
		mContext = activity;
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initFragment();
    }
	
	@Override
	abstract public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState);

//    @Override
//    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
//        super.onLazyInitView(savedInstanceState);
//        initDatas();
//    }

    @Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		uninstallListeners();
		super.onDestroy();
	}

	@Override
	public Handler getHandler() {
		return mHandler;
	}

	@Override
	public void launchThisActivity(Activity from) {
		from.startActivity(new Intent(from, getActivity().getClass()));
	}

	@Override
	public void uninstallListeners() {
		
	}

	@Override
	public ActivityInfo getActivityInfo(){
		ActivityInfo info = null;
		if(getContext() instanceof IBaseActivity){
			info = ((IBaseActivity)getContext()).getActivityInfo();
		}else{
			throw new IllegalStateException("activity is not instance of IBaseActivity");
		}
		return info;
	}

}
