package com.shenyuan.militarynews.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.BaseResponseBean;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.base.MyRetrofitResponseCallback;
import com.chengning.common.base.util.GlideHelper;
import com.chengning.common.base.util.RetrofitManager;
import com.chengning.common.util.PageHelper;
import com.chengning.common.util.StatusBarUtil;
import com.chengning.common.util.ThreadHelper;
import com.chengning.common.widget.MultiStateView;
import com.chengning.common.widget.MultiStateView.ViewState;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.LoadStateManager;
import com.shenyuan.militarynews.LoadStateManager.LoadState;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.adapter.MyFavoriteAdapter;
import com.shenyuan.militarynews.base.IScrollCallBack;
import com.shenyuan.militarynews.base.ReturnTopOnScrollListener;
import com.shenyuan.militarynews.beans.data.FavListBean;
import com.shenyuan.militarynews.beans.data.MChannelItemBean;
import com.shenyuan.militarynews.data.access.LocalStateServer;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.views.FavoriteMergeDialog;
import com.shenyuan.militarynews.views.FavoriteMergeDialog.OnFavoriteMergeListener;
import com.shenyuan.militarynews.views.PullToRefreshListView_FootLoad;
import com.shenyuan.militarynews.views.TitleBar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

;

/**
 * 我的收藏
 * 
 * @author wyg
 * @date 2015-9-16
 */
public class MyFavoriteActivity extends BaseFragmentActivity
		implements
			OnFavoriteMergeListener {

	protected static final int DATA_SUCCESS = 0;
	protected static final int DELETE_SUCCESS = 1;
	protected static final int DATA_SUCCESS_HTTP = 3;
	protected static final int DELETE_SUCCESS_HTTP = 4;
	protected static final int REQ_CODE_ARTICLE_DETAIL = 1;

	private TitleBar mTitleBar;
	private PullToRefreshListView_FootLoad mListView;
	private LoadStateManager mLoadStateManager;
	private ArrayList<MChannelItemBean> mFavoriteDatas;
	private MyFavoriteAdapter adapter;
	private boolean hasNextPage;
	private TextView mTitleRightBtn;
	private boolean isEditFlag;

	private PageHelper mPage;

	/**
	 * 点击列表的位置
	 */
	private int pos;
	private int lastNightModel;
	private MultiStateView mMultiStateView;
	private HandlerThread mFavThread;
	protected int offset = 0;
	
	private IScrollCallBack mScrollCallBack;
	private Button mTopBtn;
	private int mScrollY = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_my_favorite);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.normalstate_bg), true);
        }
		super.onCreate(savedInstanceState);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void initViews() {
		mTitleBar = new TitleBar(getActivity(), true);
		mTitleBar.setTitle("我的收藏");
		mTitleBar.showDefaultBack();
		mTitleBar.setRightButton(EditState.Delete.getStr(), getResources()
				.getColor(R.color.article_time));
		mTitleRightBtn = (TextView) mTitleBar.getRightButton();
		mTitleRightBtn.setTag(EditState.Delete);

		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.setEmptyHint("暂时还没有收藏任何文章");
		mMultiStateView.setEmptyHintColor(!Common.isTrue(SettingManager
				.getInst().getNightModel()) ? getResources().getColor(
				R.color.article_time) : getResources().getColor(
				R.color.night_text_color));
		mListView = (PullToRefreshListView_FootLoad) findViewById(R.id.my_favorite_listview);
		mTopBtn = (Button) findViewById(R.id.topbtn);

	}

	public static enum EditState {
		Delete("删除"), Compelete("完成"), ;
		private String str;

		private EditState(String str) {
			this.str = str;
		}

		public String getStr() {
			return str;
		}
	}

	@Override
	public void initDatas() {

		mPage = new PageHelper();

		lastNightModel = SettingManager.getInst().getNightModel();

		initLoadState();

		isEditFlag = false;
		mFavoriteDatas = new ArrayList<MChannelItemBean>();
		adapter = new MyFavoriteAdapter(getActivity(), mFavoriteDatas,
				new DeleteListener(), isEditFlag);
		mListView.setAdapter(adapter);
		getMyFavorite(true);
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
	}
	
	private void initLoadState() {
		mLoadStateManager = new LoadStateManager();
		mLoadStateManager
				.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {

					@Override
					public void OnStateChange(LoadState state, Object obj) {
						switch (state) {
							case Init :
								mMultiStateView.setViewState(ViewState.LOADING);
								mTitleBar.hideRightButton();
								break;
							case Success :
								mMultiStateView.setViewState(ViewState.CONTENT);
								mTitleBar.showRightButton();
								break;
							case NoData :
								mMultiStateView.setViewState(ViewState.EMPTY);
								mTitleBar.hideRightButton();
								break;
							case Failure :
								mMultiStateView.setViewState(ViewState.ERROR);
								mTitleBar.hideRightButton();
								break;
						}
					}

				});
		mLoadStateManager.setState(LoadState.Init);
	}

	/**
	 * 我的收藏
	 */
	private void getMyFavorite(final boolean isFirstPage) {
		if (App.getInst().isLogin()) {
			getMyFavoriteByHttp(false, isFirstPage);
		} else {
			getMyFavoriteByDB();
		}
		
	}

	/**
	 * 从数据库获取收藏列表
	 * 
	 */
	private void getMyFavoriteByDB() {

		if (mFavThread == null) {
			mFavThread = ThreadHelper.creatThread("my_fav");
		}
		ThreadHelper.handle(mFavThread, new Runnable() {

			@Override
			public void run() {
				ArrayList<MChannelItemBean> list = LocalStateServer.getInst(getActivity())
						.getFavArticles(offset );
				getHandler().obtainMessage(DATA_SUCCESS, list).sendToTarget();
			}

		});

	}

	/**
	 * 删除收藏监听类
	 * 
	 */
	public class DeleteListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int pos = (Integer) v.findViewById(R.id.item_favorite_delete)
					.getTag();
			if (App.getInst().isLogin()) {
				deleteFavoriteByHttp(getActivity(), mFavoriteDatas.get(pos)
						.getAid(), pos);
			} else {
				deleteFavorite(getActivity(), pos);
			}
		}

	}

	@Override
	public void installListeners() {

		mTitleBar.setRightButtonOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (EditState.Delete.equals(mTitleRightBtn.getTag())) {
					mTitleRightBtn.setText(EditState.Compelete.getStr());
					mTitleRightBtn.setTag(EditState.Compelete);
					mTitleRightBtn.setTextColor(getResources().getColor(
							R.color.common_blue));
					isEditFlag = true;

				} else if (EditState.Compelete.equals(mTitleRightBtn.getTag())) {
					mTitleRightBtn.setText(EditState.Delete.getStr());
					mTitleRightBtn.setTag(EditState.Delete);
					mTitleRightBtn.setTextColor(getResources().getColor(
							R.color.article_time));
					isEditFlag = false;
				}
				adapter.setDelFlag(isEditFlag);
				adapter.notifyDataSetChanged();
			}
		});

		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!App.getInst().isLogin()) {
					offset = 0;
				} 
				getMyFavorite(true);
			}

		});

		mListView.setOnLastPageVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				boolean isNextPage = false;
				isNextPage = App.getInst().isLogin() ? mPage.hasNextPage() : hasNextPage;
				if (isNextPage) {
					mListView.setFootLoading();
					getMyFavorite(false);
				}

			}

		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				View title = view.findViewById(R.id.item_favorite_title);
				MChannelItemBean bean = (MChannelItemBean) title.getTag();
				pos = position - 1;

				if (Const.CHANNEL_ARTICLE_TUWEN.equals(bean.getChannel())) {
					PhotoPageActivity.launchByReqCode(getActivity(), bean,
							REQ_CODE_ARTICLE_DETAIL);
				} else if (Const.CHANNEL_ARTICLE_VIDEO.equals(bean.getChannel())) {
					VideoDetailActivity.launchByReqCode(getActivity(), bean,
							REQ_CODE_ARTICLE_DETAIL);
				}else {
					ArticleActivity.launchByReqCode(getActivity(), bean,
							REQ_CODE_ARTICLE_DETAIL);
				}

				MobclickAgent.onEvent(getActivity(), "ucenter_favclick");
			}
		});
		mMultiStateView.setRefreshOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (App.getInst().isLogin()) {
					mLoadStateManager.setState(LoadState.Init);
					getMyFavorite(true);
				}
			}
		});
		
		mTopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				forceTop();
				mTopBtn.setVisibility(View.GONE);
			}
		});
		
		//加入滑动距离的判断..
		ReturnTopOnScrollListener mScrollListener = new ReturnTopOnScrollListener(mScrollCallBack,mListView);
		mListView.setOnScrollListener(new GlideHelper.PauseOnScrollListener(getActivity(), mScrollListener));
	}

	protected void forceTop() {
		mListView.setSelection(0);
	}

	/**
	 * 删除收藏
	 * 
	 */
	protected void deleteFavorite(Activity context, final int pos) {
		if (mFavThread == null) {
			mFavThread = ThreadHelper.creatThread("my_fav");
		}
		ThreadHelper.handle(mFavThread, new Runnable() {
	
			@Override
			public void run() {
				String prefix = null;
				if (Const.CHANNEL_ARTICLE_TUWEN.equals(mFavoriteDatas.get(
						pos).getChannel())) {
					prefix = LocalStateServer.PREFIX_CHANNEL_ITEM_PIC;
				} else {
					prefix = LocalStateServer.PREFIX_CHANNEL_ITEM;
				}
				LocalStateServer.getInst(getActivity()).deleteFavArticle(
						prefix, mFavoriteDatas.get(pos).getAid());
				Message msg = getHandler().obtainMessage(DELETE_SUCCESS);
				msg.arg1 = pos;
				msg.sendToTarget();
			}

		});
	}


	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
			case DATA_SUCCESS :
				// TODO 获取本地收藏数据成功
				ArrayList beans = (ArrayList) msg.obj;

				if (0 == offset) {
					mFavoriteDatas.clear();
				}
				int size = beans.size();
				if (size < 20) {
					hasNextPage = false;
				} else {
					hasNextPage = true;
				}
				mFavoriteDatas.addAll(beans);
				offset = Common.calculateOffset(mFavoriteDatas);
				adapter.notifyDataSetChanged();

				mListView.setFootLoadFull();
				mListView.onRefreshComplete();

				mLoadStateManager.setState(Common.isListEmpty(mFavoriteDatas) ? LoadState.NoData : LoadState.Success);
				break;
			case DELETE_SUCCESS :
				mFavoriteDatas.remove(msg.arg1);

				if (Common.isListEmpty(mFavoriteDatas)) {
					mLoadStateManager.setState(LoadState.NoData);
				}
				adapter.notifyDataSetChanged();
				break;
			case DATA_SUCCESS_HTTP :
				mFavoriteDatas.clear();
				mFavoriteDatas.addAll(mPage.getDataList());
				if (!Common.isListEmpty(mFavoriteDatas)) {
					mPage.setMaxid(mFavoriteDatas.get(0).getAid());
					mLoadStateManager.setState(LoadState.Success);
				} else {
					mLoadStateManager.setState(LoadState.NoData);
				}
				adapter.notifyDataSetChanged();
				handleRefreshSuccess();
				mPage.setRequestEnd();
				break;
			default :
				break;
		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch (arg0) {
			case REQ_CODE_ARTICLE_DETAIL :
				if (Activity.RESULT_OK == arg1) {

					if (!Common.isTrue(arg2.getIntExtra("favState", -1))) {
						Message message = getHandler().obtainMessage(
								DELETE_SUCCESS);
						message.arg1 = pos;
						message.sendToTarget();
					}

				}
				break;

			default :
				break;
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	/**
	 * 登录后获取收藏列表
	 */
	private void getMyFavoriteByHttp(boolean isIgnoreRequestRunning,
			final boolean isFirstPage) {
		if (Common.hasNet()) {
			String url = JUrl.URL_GET_FAVORITE_ATRICLES;
			url = isFirstPage ? mPage.appendFirstPage(url) : mPage
					.appendNextPageAndMaxid(url);
			if (!isIgnoreRequestRunning && mPage.isRequestRunning()) {
				if (!mPage.equalsRunningUrl(url.toString())) {
					if (isFirstPage) {
						mListView.onRefreshComplete();
					} else {
						mListView.setFootLoadFull();
					}
				}
				return;
			}
			mPage.setRequestStart(url.toString());

			Observable observable
					= App.getInst().getApiInterface().get(url);
			RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

				@Override
				public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
					Gson gson = new Gson();
					FavListBean bean = gson.fromJson(data, FavListBean.class);
					if (isFirstPage) {
						mPage.clear();

						if (FavoriteMergeDialog
								.checkFavoriteMergeKnowWithDialog(MyFavoriteActivity.this)) {
						}
					}
					int currentPage = (null == bean ? 1 : mPage
							.getCurrentPage() + 1);
					mPage.setCurrentPage(currentPage);
					mPage.setPageData(currentPage,
							(null == bean ? null : bean.getList()));
					mPage.setMaxPage(null == bean ? 0 : bean.getTotal_page());
					getHandler().obtainMessage(DATA_SUCCESS_HTTP)
							.sendToTarget();
				}

				@Override
				public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
					UIHelper.showToast(getActivity(), message);
					handleRefreshSuccess();
					mPage.setRequestEnd();
					if (isFirstPage) {
						mLoadStateManager.setState(LoadState.Failure);
					}
				}

				@Override
				public void onError(Throwable t) {
					if (isFirstPage) {
						mLoadStateManager.setState(LoadState.Failure);
					}
					super.onError(t);
				}
			});

		} else {
			mLoadStateManager.setState(LoadState.Failure);
		}
	}

	private void handleRefreshSuccess() {
		mListView.setFootLoadFull();
		mListView.onRefreshComplete();
	}

	/**
	 * 登录删除收藏
	 */
	protected void deleteFavoriteByHttp(final Activity context,
			final String id, final int position) {
		// TODO 删除收藏

		Map<String, String> params = new HashMap<>();
		params.put("aid", id);
		Observable observable
				= App.getInst().getApiInterface().get(JUrl.URL_CANCEL_FAVORITE_ATRICLES, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
				Message msg = getHandler()
						.obtainMessage(DELETE_SUCCESS);
				msg.arg1 = position;
				msg.sendToTarget();
			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				UIHelper.showToast(context, message);
			}

			@Override
			public void onError(Throwable t) {
				Common.handleHttpFailure(context, t);
				super.onError(t);
			}
		});
	}

	@Override
	public void onResume() {
		if (SettingManager.getInst().getNightModel() != lastNightModel) {
			finish();
			launchThisActivity(getActivity());
		}
		super.onResume();
	}

	@Override
	public void onFavoriteMergeSuccess() {
		mLoadStateManager.setState(LoadState.Init);
		getMyFavorite(true);
	}

	@Override
	public void onFavoriteMergeFailure() {
		mLoadStateManager.setState(LoadState.Failure);
	}

	@Override
	public void onDestroy() {
		ThreadHelper.destory(mFavThread);
		getHandler().removeMessages(DATA_SUCCESS);
		getHandler().removeMessages(DATA_SUCCESS_HTTP);
		getHandler().removeMessages(DELETE_SUCCESS);
		getHandler().removeMessages(DELETE_SUCCESS_HTTP);
		super.onDestroy();
	}

	public static void launch(Activity from) {
		Map<String, String> mLoginType = new HashMap<String, String>();
		if (App.getInst().isLogin()) {
			mLoginType.put("usertype", "loginuser");
		} else {
			mLoginType.put("usertype", "anonymous");
		}
		
		MobclickAgent.onEvent(from, "ucenter_favlist", mLoginType);
		
		from.startActivity(new Intent(from, MyFavoriteActivity.class));
	}
}
