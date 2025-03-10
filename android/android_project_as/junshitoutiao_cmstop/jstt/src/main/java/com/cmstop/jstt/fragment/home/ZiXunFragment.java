package com.cmstop.jstt.fragment.home;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.chengning.common.app.ActivityInfo.ActivityState;
import com.chengning.common.base.BaseFragment;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.base.SimpleFragmentPagerAdapter;
import com.chengning.common.util.HttpUtil;
import com.chengning.common.util.SerializeUtil;
import com.cmstop.jstt.App;
import com.cmstop.jstt.Const;
import com.cmstop.jstt.Const.PointActionType;
import com.cmstop.jstt.LoadStateManager;
import com.cmstop.jstt.LoadStateManager.LoadState;
import com.cmstop.jstt.MyStatusResponseHandler;
import com.cmstop.jstt.R;
import com.cmstop.jstt.SettingManager;
import com.cmstop.jstt.activity.MyActivity;
import com.cmstop.jstt.base.IForceListenRefreshExtend;
import com.cmstop.jstt.base.IScrollCallBack;
import com.cmstop.jstt.beans.data.DirectoratePointBean;
import com.cmstop.jstt.beans.data.LoginBean;
import com.cmstop.jstt.beans.data.MChannelBean;
import com.cmstop.jstt.beans.data.MChannelNavBean;
import com.cmstop.jstt.fragment.home.AbstractChannelItemListFragment.OnRecommendHttpListener;
import com.cmstop.jstt.interf.IFragmentBackListener;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.utils.JUrl;
import com.cmstop.jstt.utils.SPHelper;
import com.cmstop.jstt.utils.TaskUpdateUtil;
import com.cmstop.jstt.utils.Utils;
import com.cmstop.jstt.views.MyTabPageIndicator;
import com.cmstop.jstt.views.ProgressRefreshView;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;


public class ZiXunFragment extends BaseFragment implements IForceListenRefreshExtend, IFragmentBackListener {

	private static final String TAG = ZiXunFragment.class.getSimpleName();

	public static final int TASK_SUCCESS = 3;

	private View mView;

	private View mContent;
	private MyTabPageIndicator mIndicator;
	private ImageView mIndicatorSlideRight;
	private View mUser;
	private ImageView mUserHead;
	private ViewPager mPager;
	private Button mTopBtn;

	private ProgressRefreshView mProgressRefresh;

	private FragmentManager mFragmentManager;

	private LoadStateManager mLoadStateManager;
	private SimpleFragmentPagerAdapter mAdapter;

	private RecommendFragment mRecommendFragment;
	private Fragment mLastFragment;
	private RefreshState mRefreshState = RefreshState.RefreshComplete;
	private OnRefreshStateListener mListener;

	private int mCurIndex;
	private ArrayList<String> fragmentStrsList;
	private ArrayList<Fragment> fragmentsList;

	private IScrollCallBack mScrollCallBack;

	/**
	 * 是否点击更多导航栏
	 */
	private boolean isNavMore;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Common.setTheme(getContext());
		mView = inflater.inflate(R.layout.fragment_zixun, container, false);
		return mView ;
	}

	@Override
	public void initViews() {

		mProgressRefresh = new ProgressRefreshView(getContext(), mView);

		mContent = mView.findViewById(R.id.content);
		mUser = mView.findViewById(R.id.top_user);
		mUserHead = (ImageView) mView.findViewById(R.id.top_user_head);
		mIndicator = (MyTabPageIndicator)mView.findViewById(R.id.indicator);
		mIndicatorSlideRight = (ImageView)mView.findViewById(R.id.indicator_slide_right);
		mPager = (ViewPager)mView.findViewById(R.id.viewpager);
		mTopBtn = (Button) mView.findViewById(R.id.topbtn);

		mFragmentManager = getChildFragmentManager();
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

		mLoadStateManager = new LoadStateManager();
		mLoadStateManager.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {

			@Override
			public void OnStateChange(LoadState state, Object obj) {
				switch (state) {
					case Init:
						mProgressRefresh.setWaitVisibility(true);
						mProgressRefresh.setRefreshVisibility(false);
						mContent.setVisibility(View.INVISIBLE);
						break;
					case Success:
						mProgressRefresh.setRootViewVisibility(false);
						mContent.setVisibility(View.VISIBLE);
						break;
					case Failure:
						mProgressRefresh.setWaitVisibility(false);
						mProgressRefresh.setRefreshVisibility(true);
						mContent.setVisibility(View.INVISIBLE);
						break;
					default:
						break;
				}
			}
		});

		String chanStrOld = SPHelper.getInst().getString(SPHelper.KEY_CHANNEL_LIST);
		if(!TextUtils.isEmpty(chanStrOld)){
			ArrayList<MChannelNavBean> channelList = SerializeUtil.deSerialize(chanStrOld, ArrayList.class);
			if(!Common.isListEmpty(channelList)){
				mLoadStateManager.setState(LoadState.Success);
				addFragments(channelList);
			}else{
				mLoadStateManager.setState(LoadState.Init);
				addRecommendFragment();
			}
		}else{
			mLoadStateManager.setState(LoadState.Init);
			addRecommendFragment();
		}
		updateHead();
		if (App.getInst().isLogin()) {
			initEveryLogin();
		}
	}

	@Override
	public void installListeners() {
		mUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				HomeActivity.setChangeView(3);
				//TODO 切换到我的界面
				MyActivity.launch(getContext());
			}
		});
		mProgressRefresh.setRefreshOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				addRecommendFragment();
			}
		});
		mTopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				forceTop();
				mTopBtn.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch(msg.what){
			case TASK_SUCCESS:
				if(App.getInst().isLogin()){
					LoginBean mBean = (LoginBean) msg.obj;
					if(mBean.getCredits_rule()!=null){
						DirectoratePointBean pointBean = TaskUpdateUtil.convertLoginToPoint(mBean);
						try {
							if(pointBean != null){
								TaskUpdateUtil.showHints(getActivity(), pointBean, PointActionType.LOGIN);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else {
					if (!Common.isListEmpty(App.getInst().getLoginBean().getCredits_rule())) {
						DirectoratePointBean pointBean = TaskUpdateUtil.convertLoginToPoint(App.getInst().getLoginBean());
						try {
							if (pointBean != null) {
								TaskUpdateUtil.showHints(getActivity(),pointBean, PointActionType.LOGIN);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						// 手动制空credits_rules
						LoginBean mLoginBean = new LoginBean();
						mLoginBean = App.getInst().getLoginBean();
						mLoginBean.setCredits_rule(null);
						App.getInst().saveLoginBean(mLoginBean);
					}

				}

				break;
			default:
				break;
		}
	}

	private void addRecommendFragment(){
		ArrayList<String> strs = new ArrayList<String>();
		ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();

		strs.add(Const.CHANNEL_TUIJIAN);
		RecommendFragment f1 = new RecommendFragment();
		f1.setOnRecommendHttpListener(mOnRecommendHttpListener);
		f1.setmScrollCallback(mScrollCallBack);
		fragmentsList.add(f1);

		this.mRecommendFragment = f1;
		this.mLastFragment = f1;
		this.fragmentStrsList = strs;
		this.fragmentsList = fragmentsList;

		if(mLastFragment != null && mLastFragment instanceof IForceListenRefreshExtend){
			((IForceListenRefreshExtend)mLastFragment).setOnRefreshStateListener(mControlListener);
		}

		mAdapter = new SimpleFragmentPagerAdapter(mFragmentManager,
				fragmentsList,strs);

		mPager.setAdapter(mAdapter);
		mPager.setOffscreenPageLimit(0);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(mOnPageChangeListener);
		mIndicator.setCurrentItem(0);
		mIndicator.notifyDataSetChanged();
	}

	private void addFragments(List<MChannelNavBean> channelList){
		ArrayList<String> strs = new ArrayList<String>();
		ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();

		if(!Common.isListEmpty(channelList)){
			for(int i = 0; i < channelList.size(); i++){
				MChannelNavBean b = channelList.get(i);
				if(Const.CHANNEL_SHOWTYPE_NOT == b.getShowtype()){
					continue;
				}
				if(i == 0){
					strs.add(b.getName());
					RecommendFragment f1 = null;
//					if(mRecommendFragment != null && mRecommendFragment.isAdded()){
//						f1 = mRecommendFragment;
//					}else{
//						f1 = new RecommendFragment();
//					}
					f1 = new RecommendFragment();
					f1.setmScrollCallback(mScrollCallBack);
					f1.setOnRecommendHttpListener(mOnRecommendHttpListener);
					fragmentsList.add(f1);

					this.mRecommendFragment = f1;
					this.mLastFragment = f1;

					if(mLastFragment != null && mLastFragment instanceof IForceListenRefreshExtend){
						((IForceListenRefreshExtend)mLastFragment).setOnRefreshStateListener(mControlListener);
					}
				}else if(TextUtils.equals("独家", b.getName())){
					strs.add(b.getName());
					VideoFragment videoF = new VideoFragment(b);
					videoF.setmScrollCallback(mScrollCallBack);
					fragmentsList.add(videoF);
				}else if(TextUtils.equals("商城", b.getName())){
					strs.add(b.getName());
					ShopFragment shopF = new ShopFragment();
					fragmentsList.add(shopF);
				}else if(Const.CHANNEL_SHOWTYPE_BAIDU_FEEDS == b.getShowtype()){
					strs.add(b.getName());
					FeedsChannelFragment ff = new FeedsChannelFragment(b);
					fragmentsList.add(ff);
				}else{
					strs.add(b.getName());
					ChannelFragment cf = new ChannelFragment(b);
					cf.setmScrollCallback(mScrollCallBack);
					fragmentsList.add(cf);
				}
			}
		}
		this.fragmentStrsList = strs;
		this.fragmentsList = fragmentsList;

		mAdapter = new SimpleFragmentPagerAdapter(mFragmentManager,
				fragmentsList,strs);

		mPager.setAdapter(mAdapter);
		mPager.setOffscreenPageLimit(0);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(mOnPageChangeListener);
		mIndicator.setCurrentItem(0);
		mIndicator.notifyDataSetChanged();
	}

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int index) {

			handleNavEventAnalytics(isNavMore ? "homemore" : "normal", Common.isListEmpty(fragmentStrsList) ? "" :fragmentStrsList.get(index));

			mCurIndex = index;

			if(mLastFragment != null && mLastFragment instanceof IForceListenRefreshExtend){
				((IForceListenRefreshExtend)mLastFragment).setOnRefreshStateListener(null);
			}
			Fragment f = fragmentsList.get(index);
			mLastFragment = f;
			if(f instanceof IForceListenRefreshExtend){
				setRefreshState(((IForceListenRefreshExtend)f).getRefreshState());
				((IForceListenRefreshExtend) f).setOnRefreshStateListener(mControlListener);

				((IForceListenRefreshExtend) f).forceSetPageSelected(true);
			}else{
				setRefreshState(RefreshState.RefreshComplete);
			}
			if(f instanceof AbstractChannelItemListFragment){
				((AbstractChannelItemListFragment)f).dealScrollY();
			}else{
				mTopBtn.setVisibility(View.GONE);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	private OnRecommendHttpListener mOnRecommendHttpListener = new OnRecommendHttpListener() {

		@Override
		public void onHttpSucess(MChannelBean bean) {
			if(getContext() == null){
				return;
			}
			ArrayList<MChannelNavBean> netChanList = bean.getNav();

			boolean isSame = false;
			String chanStr = SerializeUtil.serialize(netChanList);
			String chanStrOld = SPHelper.getInst().getString(SPHelper.KEY_CHANNEL_LIST);
			// 是否与以前的数据相同
			isSame = !TextUtils.isEmpty(chanStr) && chanStr.equals(chanStrOld);
			Log.d(TAG, "OnRecommendHttpListener onHttpSucess isSame: " + isSame);

			if(!isSame){
				SPHelper.getInst().saveString(SPHelper.KEY_CHANNEL_LIST, chanStr);

				mLoadStateManager.setState(LoadState.Success);
				if(getActivityInfo().getActivityState() != ActivityState.Killed){
					addFragments(netChanList);
				}
			}
		}

		@Override
		public void onHttpFailure() {
			if(mLoadStateManager.getState() == LoadState.Init){
				mLoadStateManager.setState(LoadState.Failure);
			}
		}

	};

	/**
	 * 切换导航
	 * @param nav
	 */
	public void onNav(String nav){

		isNavMore = true;

		int index = -1;
		for(int i = 0; i < fragmentStrsList.size(); i++){
			if(nav.equals(fragmentStrsList.get(i))){
				index = i;
				break;
			}
		}
		if(index >= 0){
			mIndicator.setCurrentItem(index);
		}
	}

	/**
	 * 处理导航栏点击事件统计
	 * @param enter
	 * @param nav
	 */
	private void handleNavEventAnalytics(String enter, String nav) {
		this.isNavMore = false;
		Map<String, String> mHashMap = new HashMap<String, String>();
		mHashMap.put("enter", enter);
		mHashMap.put("nav_index", nav);
		MobclickAgent.onEvent(getContext(), "nav_click", mHashMap);
	}

	@Override
	public void onStart(){
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (SettingManager.getInst().getNoPicModel() != SPHelper.getInst().getInt(SPHelper.KEY_HOME_NO_PIC_MODEL)) {
			if (null != mAdapter) {
				mAdapter.notifyDataSetChanged();
			}
			SPHelper.getInst().saveInt(SPHelper.KEY_HOME_NO_PIC_MODEL,
					SettingManager.getInst().getNoPicModel());
		}
		updateHead();
	}

	/**
	 * 更新首页头像
	 */
	private void updateHead(){
		if (App.getInst().isLogin()) {
			Utils.setCircleImage(App.getInst().getLoginBean().getFace(), mUserHead);
		} else {
			mUserHead.setImageResource(R.drawable.home_user_state);
		}

		if (Common.isTrue(SettingManager.getInst().getNightModel())) {
			mUserHead.setColorFilter(getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
		}
	}

	private void initEveryLogin(){
		if (Common.hasNet()) {
			MobclickAgent.onEvent(getContext(), "user_login");
			//cookie登录请求每日登录
			HttpUtil.get(JUrl.SITE + JUrl.URL_GET_LOGIN_EVERYDAY,new MyStatusResponseHandler(){

				@Override
				public void onFailure(int statusCode, Header[] headers,
									  Throwable throwable, JSONObject errorResponse) {
					Common.handleHttpFailure(getActivity(), throwable);
//					getHandler().obtainMessage(HTTP_FAIL).sendToTarget();
				}

				@Override
				public void onFinish() {
				}

				@Override
				public void onDataSuccess(int status, String mod, String message,
										  String data, JSONObject obj) {
//					UIHelper.removePD();
					Gson gson = new Gson();
					LoginBean mBean = gson.fromJson(data, LoginBean.class);
					getHandler().obtainMessage(TASK_SUCCESS, mBean).sendToTarget();

				}

				@Override
				public void onDataFailure(int status, String mod, String message,
										  String data, JSONObject obj) {
				}

			});
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop(){
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void setRefreshState(RefreshState state){
		this.mRefreshState = state;
		if(mControlListener != null){
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
	}

	@Override
	public void forceRefresh() {
		if (mCurIndex == 0) {
			MobclickAgent.onEvent(getActivity(), Const.UMEVENT_HOME_MANLOAD);
		}
		if(mLastFragment != null && mLastFragment instanceof IForceListenRefreshExtend
				&& !(((IForceListenRefreshExtend)mLastFragment).getRefreshState() == RefreshState.Refreshing)){
			((IForceListenRefreshExtend)mLastFragment).forceRefresh();
			mTopBtn.setVisibility(View.GONE);
		}
	}

	@Override
	public void forceCheckRefresh() {
		if(mLastFragment != null && mLastFragment instanceof IForceListenRefreshExtend){
			((IForceListenRefreshExtend)mLastFragment).forceCheckRefresh();
		}
	}

	@Override
	public void forceTop() {
		if(mLastFragment != null && mLastFragment instanceof IForceListenRefreshExtend
				&& !(((IForceListenRefreshExtend)mLastFragment).getRefreshState() == RefreshState.Refreshing)){
			((IForceListenRefreshExtend)mLastFragment).forceTop();
		}
	}

	@Override
	public RefreshState getRefreshState() {
		return mRefreshState;
	}

	@Override
	public void setOnRefreshStateListener(OnRefreshStateListener listener) {
		this.mListener = listener;
	}

	@Override
	public void forceSetPageSelected(boolean isSelected) {
		if(mLastFragment != null && mLastFragment instanceof IForceListenRefreshExtend){
			((IForceListenRefreshExtend)mLastFragment).forceSetPageSelected(true);
		}
	}

	private OnRefreshStateListener mControlListener = new OnRefreshStateListener() {

		@Override
		public void onStart() {
			if(mListener != null){
				mListener.onStart();
			}
		}

		@Override
		public void onFinish() {
			if(mListener != null){
				mListener.onFinish();
			}
		}
	};

	@Override
	public boolean canGoBack() {
		if(mLastFragment != null && mLastFragment instanceof IFragmentBackListener){
			IFragmentBackListener iBack = (IFragmentBackListener)mLastFragment;
			if(iBack.canGoBack()){
				iBack.goBack();
				return true;
			}
		}
		return false;
	}

	@Override
	public void goBack() {

	}

	/**
	 * 登录改变更新
	 */
//	public void refresh(String data){
//		//TODO 初始化用户头像
//		if(!TextUtils.isEmpty(data)){
//			getHandler().obtainMessage(LOGIN_CHANGE,data).sendToTarget();
//		}
//		refreshHome();
//	}
	/**
	 * 刷新首页
	 */
//	private void refreshHome(){
//		if(mAdapter != null){
//			mIndicator.setCurrentItem(0);
//			Fragment f = mAdapter.getItem(0);
//			if(f instanceof IForceRefresh){
//				((IForceRefresh)f).forceRefresh();
//			}
//		}
//	}

}
