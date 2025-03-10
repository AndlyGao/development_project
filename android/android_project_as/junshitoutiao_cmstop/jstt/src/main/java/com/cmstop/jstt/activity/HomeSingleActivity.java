package com.cmstop.jstt.activity;

import java.util.ArrayList;

import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.IForceListenRefresh.OnRefreshStateListener;
import com.chengning.common.base.IForceListenRefresh.RefreshState;
import com.chengning.common.update.UpdateVersionUtil;
import com.chengning.common.util.DisplayUtil;
import com.chengning.common.util.HomeWatcher;
import com.chengning.common.util.HomeWatcher.OnHomePressedListener;
import com.cmstop.jstt.App;
import com.cmstop.jstt.Const;
import com.cmstop.jstt.R;
import com.cmstop.jstt.SettingManager;
import com.cmstop.jstt.base.IForceListenRefreshExtend;
import com.cmstop.jstt.event.SwitchNavEvent;
import com.cmstop.jstt.fragment.home.ZiXunFragment;
import com.cmstop.jstt.interf.IFragmentBackListener;
import com.cmstop.jstt.service.UpdateBadgeService;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.utils.SPHelper;
import com.chengning.common.util.StatusBarUtil;
import com.cmstop.jstt.utils.UIHelper;
import com.cmstop.jstt.utils.UmengShare;
import com.cmstop.jstt.views.FirstRunPage;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class HomeSingleActivity extends BaseFragmentActivity implements OnHomePressedListener {

	public static final String ACTION_FINISHHOME = App.class.getPackage().getName() + ".action_finishhome";

	private View mTopSearch;
	
	private ArrayList<Fragment> mFragmentsList;
	
	private HomeWatcher mHomeWatcher;
    private boolean mIsHomePressed;
    private boolean mIsHomeStop;
    private BroadcastReceiver finishReceiver;
    protected int mCurIndex = 0;
    private long lastTime = 0;
    private int lastNightModel;
    private boolean mIsFromPush;
    
    private Fragment mCurFragment;
    private RefreshState mRefreshState;

	public static void launch(Activity from){
		Intent intent = new Intent(from, HomeSingleActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		from.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_home);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.common_logo_color), false);
        }
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initViews() {
		mTopSearch = findViewById(R.id.title_bar_search);
	}

	@Override
	public void initDatas() {
		lastNightModel = SettingManager.getInst().getNightModel();
		DisplayUtil.getInst().init(getActivity());
		
		addFragment();
		getHandler().postDelayed(new Runnable() {	
			@Override
			public void run() {
				UpdateVersionUtil.checkUpdate(getActivity(), Const.UPDATE_APP_KEY, false);
			}
		}, 2000);

		FirstRunPage firstRunPage = new FirstRunPage(getActivity());
	
		Intent intent = new Intent(this, UpdateBadgeService.class);
		startService(intent);
		EventBus.getDefault().register(getActivity());
	}

	private void addFragment() {
		ZiXunFragment mHomeFragment1 =  new ZiXunFragment();
		mCurFragment = mHomeFragment1;
		mFragmentsList = new ArrayList<Fragment>();
		mFragmentsList.add(mHomeFragment1);
		
		FragmentManager mFragmentManager = getSupportFragmentManager();
		mFragmentManager.beginTransaction()
			.replace(R.id.home_content_layout, mHomeFragment1)
			.commitAllowingStateLoss();
		
		
	}

	@Override
	public void installListeners() {
		finishReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		IntentFilter intentFileter = new IntentFilter(ACTION_FINISHHOME);
		registerReceiver(finishReceiver, intentFileter);
		
		mTopSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), SearchNewsActivity.class));
			}
		});
	}

	@Override
	public void processHandlerMessage(Message msg) {
		
	}

	private void onNav(String channel){
		if (TextUtils.isEmpty(channel)) {
			return;
		}
		for(Fragment f: mFragmentsList){
			if(f instanceof ZiXunFragment){
				((ZiXunFragment)f).onNav(channel);
			}
		}
	}

	@Subscribe
	public void onEventMainThread(SwitchNavEvent event) {
		if (event == null) {
			return;
		}
		String navName = event.getNavName();
		onNav(navName);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(this);
        mHomeWatcher.startWatch();
        
        if (App.getInst().isNightModelChange()) {
        	if (SettingManager.getInst().getNightModel() != lastNightModel  ) {
        		finish();
            	launch(getActivity());
        	}
        	App.getInst().setNightModelChange(false);
        }
      //取消桌面红点
      Common.cancleBadge(getActivity());
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}


	@Override
	public void onPause() {
		super.onPause();
		mHomeWatcher.setOnHomePressedListener(null);
        mHomeWatcher.stopWatch();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(finishReceiver);
		EventBus.getDefault().unregister(getActivity());
	}

	@Override
	protected void onStart() {
		super.onStart();
		handleOnStart();
	}
	
	public static final int HANDLENUMMAX = 2;
	private int handleNum = 0;
	private void handleOnStart(){
		if(mIsHomeStop){
			Fragment f = mFragmentsList.get(mCurIndex);
			if(null != f && f instanceof IForceListenRefreshExtend){
				if(Common.hasNet()){
					((IForceListenRefreshExtend) f).forceCheckRefresh();
					mIsHomePressed = false;
					mIsHomeStop = false;
				}else{
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if(handleNum >= HANDLENUMMAX){
								mIsHomePressed = false;
								mIsHomeStop = false;
								return;
							}
							handleNum ++;
							handleOnStart();
						}
					}, 1000);
				}
			}
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		handleNum = HANDLENUMMAX;
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mIsHomeStop = mIsHomePressed;
		SPHelper.getInst().saveInt(SPHelper.KEY_HOME_TAB_INDEX_CACHE, mCurIndex);
	}

	@Override
	public Activity getActivity() {
		return this;
	}
	
	@Override
	public void onHomePressed() {
		mIsHomePressed = true;
		handleNum = 0;
	}

	@Override
	public void onHomeLongPressed() {
		mIsHomePressed = true;
		handleNum = 0;
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
//        super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putBoolean("push", mIsFromPush);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
//        super.onRestoreInstanceState(savedInstanceState);
    	mIsFromPush = savedInstanceState.getBoolean("push",false);
    }

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		UmengShare.getInstance().onActivityResult(arg0, arg1, arg2);
	}
	
	
	private OnRefreshStateListener mControlListener = new OnRefreshStateListener() {
		
		@Override
		public void onStart() {
			mRefreshState = RefreshState.Refreshing;
		}
		
		@Override
		public void onFinish() {
			mRefreshState = RefreshState.RefreshComplete;
		}
	};
	
	private void setRefreshState(RefreshState state){
		this.mRefreshState = state;
		switch (mRefreshState) {
		case Refreshing:
			mControlListener.onStart();
			break;
		case RefreshComplete:
			mControlListener.onFinish();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if(mCurFragment != null && mCurFragment instanceof IFragmentBackListener){
			IFragmentBackListener iBack = (IFragmentBackListener)mCurFragment;
			if(iBack.canGoBack()){
				iBack.goBack();
				return;
			}
		}
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime < 1500) {
			finish();
		} else {
			lastTime = currentTime;
			UIHelper.showToast(this, "再按一次退出");
		}	
		return;
	}
	
}
