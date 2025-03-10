package com.shenyuan.militarynews.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.SimpleFragmentPagerAdapter;
import com.chengning.common.widget.extend.TabTextViewPageIndicator;
import com.chengning.common.widget.extend.TabTextViewPageIndicator.OnAddTabListener;
import com.chengning.common.widget.extend.TabTextViewPageIndicator.TabView;
import com.shenyuan.militarynews.PushMsgManager;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.base.BaseNetListFragment;
import com.shenyuan.militarynews.base.IScrollCallBack;
import com.shenyuan.militarynews.fragment.home.AbstractChannelItemListFragment;
import com.shenyuan.militarynews.fragment.home.MyCommentFragment;
import com.shenyuan.militarynews.utils.Common;
import com.chengning.common.util.StatusBarUtil;;
import com.shenyuan.militarynews.views.TitleBar;
import com.umeng.analytics.MobclickAgent;

public class MyCommentActivity extends BaseFragmentActivity{

	private TitleBar mTitleBar;
	private TabTextViewPageIndicator mIndicator;
	private ViewPager mViewPager;
	private FragmentManager mFragmentManager;
	protected int mPageItemIndex = 0;
	private SimpleFragmentPagerAdapter mAdapter;
	private MyCommentFragment myCmtFragment;
	private MyCommentFragment cmtMyFragment;
	private boolean mIsPush;
	private int mLastNightMode;
	
	private Button mTopBtn;
	private IScrollCallBack mScrollCallBack;
	private ArrayList<Fragment> fragmentsList;
	protected Fragment mLastFragment;
	
	public static void launch(Activity from, boolean isPush){
		
		MobclickAgent.onEvent(from, "ucenter_mycomments");
		Intent intent = new Intent(from, MyCommentActivity.class);
		intent.putExtra("isPush", isPush);
		from.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_my_comment_new);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.normalstate_bg), true);
        }
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Activity getActivity() {
		return MyCommentActivity.this;
	}

	@Override
	public void initViews() {
		mTitleBar = new TitleBar(getActivity(), true);
		mTitleBar.setTitle("我的评论");
		mTitleBar.showDefaultBack();
		
		mIndicator = (TabTextViewPageIndicator) findViewById(R.id.my_comment_indicator);
		mViewPager = (ViewPager) findViewById(R.id.my_comment_viewpager);
		mTopBtn = (Button) findViewById(R.id.topbtn);
		
		mIndicator.setOnAddTabListener(new OnAddTabListener() {

			@Override
			public TabView onAddTab(Context context) {
				TabView tabView = mIndicator.new TabView(getActivity());
				
				tabView.setBackgroundDrawable(Common.isTrue(mLastNightMode) ? getResources().getDrawable(
						R.drawable.news_tab_indicator_night) : getResources().getDrawable(
						R.drawable.news_tab_indicator_white));
				tabView.setTextSize(15);

				return tabView;
			}
		});
		
	}

	@Override
	public void initDatas() {
		
		mScrollCallBack = new IScrollCallBack() {
			@Override
			public void show() {
				mTopBtn.setVisibility(View.VISIBLE);
			}
			@Override
			public void hidden() {
				mTopBtn.setVisibility(View.GONE);
			}
		};
		
		mIsPush = getIntent().getBooleanExtra("isPush", false);
		mFragmentManager = getSupportFragmentManager();
		mLastNightMode = SettingManager.getInst().getNightModel();
		addFragment();
		PushMsgManager.getInstance().getPushUserDataBean().setComment_new(0);
		
	}

	
	private void addFragment(){
		ArrayList<String> strs = new ArrayList<String>();
		strs.add("我的评论");
		strs.add("评论我的");
		myCmtFragment = new MyCommentFragment(MyCommentFragment.COMMENT_TYPE_MY);
		cmtMyFragment = new MyCommentFragment(MyCommentFragment.COMMENT_TYPE_CMT_MY);
		myCmtFragment.setmScrollCallback(mScrollCallBack);
		cmtMyFragment.setmScrollCallback(mScrollCallBack);
		
		fragmentsList = new ArrayList<Fragment>();
		fragmentsList.add(myCmtFragment);
		fragmentsList.add(cmtMyFragment);
		
		mAdapter = new SimpleFragmentPagerAdapter(mFragmentManager,
				fragmentsList,strs);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(1);
		if (mIsPush) {
			mPageItemIndex = 1;
		}
		
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(pageChangeListener);
		mViewPager.setCurrentItem(mPageItemIndex);
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			mPageItemIndex = arg0;
			mLastFragment = fragmentsList.get(arg0);
			if(mLastFragment instanceof BaseNetListFragment){
				((BaseNetListFragment)mLastFragment).dealScrollY();
			}
//			mTitleRightBtn.setText(mTagMap.get(mPageItemIndex).getStr());
//			switch (mPageItemIndex) {
//				case 0 :
//					newsFragment.setUserVisibleHint(true);
//					break;
//				case 1 :
//					discussFragment.setUserVisibleHint(true);
//					break;
//
//				default :
//					break;
//			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};
	
	@Override
	public void installListeners() {
		mTopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				forceTop();
				mTopBtn.setVisibility(View.GONE);
			}
		});
	}

	protected void forceTop() {
		if(mLastFragment instanceof BaseNetListFragment){
			((BaseNetListFragment)mLastFragment).forceTop();
		}
	}

	@Override
	public void processHandlerMessage(Message msg) {
		
	}
	
	@Override
	public void onResume() {
		int mCurNightMode = SettingManager.getInst().getNightModel();
		if (mLastNightMode != mCurNightMode) {
			finish();
			Intent intent = new Intent(getActivity(), MyCommentActivity.class);
			intent.putExtra("isPush", mIsPush);
			startActivity(intent);
		}
		super.onResume();
	}

}
