package com.chengning.fenghuo.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.util.HttpUtil;
import com.chengning.common.util.PageHelper;
import com.chengning.common.widget.MultiStateView;
import com.chengning.common.widget.MultiStateView.ViewState;
import com.chengning.fenghuo.LoadStateManager;
import com.chengning.fenghuo.LoadStateManager.LoadState;
import com.chengning.fenghuo.MyJsonHttpResponseHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.SettingManager;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.UIHelper;
import com.chengning.fenghuo.widget.PullToRefreshListView_FootLoad;
import com.chengning.fenghuo.widget.TitleBar;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

public abstract class BasePageListActivity<T> extends BaseFragmentActivity implements IForceListenRefreshExtend{

public static final int DATA_SUCCESS = 10002;
	
	private TitleBar mTitleBar;
	private MultiStateView mMultiStateView;
	private PullToRefreshListView_FootLoad mPullListView;
	private LoadStateManager mLoadStateManager;
	private BaseAdapter mAdapter;

	private List mCommentList;
	
	private PageHelper mPage;

	private IScrollCallBack mScrollCallback = null;
	private ImageView mTopBtn;
	
	public abstract BaseFragmentActivity buildContext();
	public abstract String buildUrl();
	public abstract BaseAdapter buildAdapter(FragmentActivity activity, List list);
	public abstract String buildMaxId(List list);
	public abstract String configTitle();
	public abstract String configNoData();
	public abstract View configContentView();
	public abstract void initExtraView();
	public abstract void initExtraData();
	public abstract void initExtraListener();
	public abstract void handleItemClick(AdapterView<?> parent, View view, int position, long id);
	public abstract BaseListBean<T> handleHttpSuccess(Gson gson, String data);
	
	public TitleBar getTitleBar(){
		return mTitleBar;
	}
	
	public PullToRefreshListView_FootLoad getPullListView(){
		return mPullListView;
	}
	
	@Override
	public FragmentActivity getActivity() {
		return buildContext();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(configContentView());
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void initViews() {
		mTitleBar = new TitleBar(getActivity(), true);
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mPullListView = (PullToRefreshListView_FootLoad) mMultiStateView.findViewById(R.id.list);
		initExtraView();

		mTitleBar.showDefaultBack();
		mMultiStateView.setEmptyHint(configNoData());
		mMultiStateView.setEmptyHintColorResource(!Common.isTrue(SettingManager.getInst().getNightModel())
				? R.color.article_time : R.color.night_text_color);
		mTopBtn = (ImageView) findViewById(R.id.topbtn);
	}

	@Override
	public void initDatas() {

		initExtraData();
		mScrollCallback = new IScrollCallBack() {
			@Override
			public void show() {
				mTopBtn.setVisibility(View.VISIBLE);
			}
			@Override
			public void hidden() {
				mTopBtn.setVisibility(View.GONE);
			}
		};
		setmScrollCallback(mScrollCallback);
		
		mPage = new PageHelper();
		
		mTitleBar.setTitle(configTitle());
		
		mCommentList = new ArrayList<T>();
		
		initLoadState();
		
		mAdapter = buildAdapter(getActivity(), mCommentList);;
		mPullListView.setAdapter(mAdapter);
		
		if (!Common.hasNet()) {
         	Common.showHttpFailureToast(getActivity());
         	mLoadStateManager.setState(LoadState.Failure);
		} else {
			getDataByHttp(true);
		}
		
	}

	/**
	 * 初始化加载状态
	 */
	private void initLoadState() {
		mLoadStateManager = new LoadStateManager();
		mLoadStateManager.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {

			@Override
			public void OnStateChange(LoadState state, Object obj) {
				switch (state) {
					case Init:
						mMultiStateView.setViewState(ViewState.LOADING);
						break;
					case Success:
						mMultiStateView.setViewState(ViewState.CONTENT);
						break;
					case NoData:
						mMultiStateView.setViewState(ViewState.EMPTY);
						break;
					case Failure:
						mMultiStateView.setViewState(ViewState.ERROR);
						break;
				}
			}
		});
		mLoadStateManager.setState(LoadState.Init);
	}

	@Override
	public void installListeners() {
		
		mMultiStateView.setRefreshOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				getDataByHttp(true);
			}
		});
		
		mPullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				handleItemClick(parent, view, position, id);
			}
		});

		//加入滑动距离的判断..
		ReturnTopOnScrollListener mScrollListener = new ReturnTopOnScrollListener(getmScrollCallback(), mPullListView);
		mPullListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true, mScrollListener));
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getDataByHttp(true);
			}

		}); 
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (mPage.hasNextPage()) {
					mPullListView.setFootLoading();
					getDataByHttp();
				}
			}

		});
		mTopBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				forceTop();
				mTopBtn.setVisibility(View.GONE);
			}
		});
		initExtraListener();
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case DATA_SUCCESS:
			
			mCommentList.clear();
			mCommentList.addAll(mPage.getDataList());
			if(!Common.isListEmpty(mCommentList)){
				mPage.setMaxid(buildMaxId(mCommentList));
				mLoadStateManager.setState(LoadState.Success);
			}else{
				mLoadStateManager.setState(LoadState.NoData);
				handleNoData();
			}
			mAdapter.notifyDataSetChanged();
			handleRefreshSuccess();
			break; 
		default:
			break;
		}
	}

	public void handleNoData() {

	}

	private void getDataByHttp(){
		getDataByHttp(false);
	}
	
	private void getDataByHttp(final boolean isFirstPage){
		getHandler().postDelayed(new Runnable() {

			@Override
			public void run() {
				getDataByHttp(false, isFirstPage);
			}
		}, Common.TIME_WAIT_COMPLETE);
	}
	
	@Deprecated
	private void getDataByHttp(boolean isIgnoreRequestRunning, final boolean isFirstPage){
		String url = buildUrl();
		url = isFirstPage ? mPage.appendFirstPage(url) : mPage.appendNextPageAndMaxid(url);
		if(!isIgnoreRequestRunning && mPage.isRequestRunning()){
			if(!mPage.equalsRunningUrl(url.toString())){
				if(isFirstPage){
					mPullListView.onRefreshComplete();
				} else {
					mPullListView.setFootLoadFull();
				}
			}
			return;
		}
		mPage.setRequestStart(url.toString());

//		HttpUtil.getClient().cancelRequests(getActivity(), true);
		HttpUtil.get(getActivity(), url.toString(), null, new MyJsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Common.handleHttpFailure(getActivity(), throwable);
				handleRefreshSuccess();
				if (mPage.isCurrentPageFirst()) {
					mLoadStateManager.setState(LoadState.Failure);
				}
			}

			;

			@Override
			public void onDataSuccess(int status, String mod, String message,
									  String data, JSONObject obj) {
				Gson gson = new Gson();
				BaseListBean<T> bean = handleHttpSuccess(gson, data);
				if (isFirstPage) {
					mPage.clear();
				}
				int currentPage = (null == bean ? 1 : mPage.getCurrentPage() + 1);
				mPage.setCurrentPage(currentPage);
				mPage.setPageData(currentPage, (null == bean ? null : bean.getList()));
				mPage.setMaxPage((null == bean ? 0 : bean.getTotal_page()));
				getHandler().obtainMessage(DATA_SUCCESS).sendToTarget();

			}

			@Override
			public void onDataFailure(int status, String mod, String message,
									  String data, JSONObject obj) {
				handleRefreshSuccess();
				if (mPage.isCurrentPageFirst()) {
					mLoadStateManager.setState(LoadState.Failure);
				}
				handleHttpFailure(mLoadStateManager, mMultiStateView, status, message,
						data);
			}
		}); 
	}

	public void handleHttpFailure(LoadStateManager mLoadStateManager, MultiStateView mMultiStateView, int status, String message, String data) {
		UIHelper.showToast(getActivity(), message);
	}

	protected void handleRefreshSuccess() {
		mPullListView.setFootLoadFull();
		mPullListView.onRefreshComplete();
		mPage.setRequestEnd();
	}

	@Override
	public void forceTop() {
		mPullListView.setSelection(0);
	}

	@Override
	public void forceSetPageSelected(boolean isSelected) {

	}

	@Override
	public void forceRefresh() {

	}

	@Override
	public void forceCheckRefresh() {

	}

	@Override
	public RefreshState getRefreshState() {
		return null;
	}

	@Override
	public void setOnRefreshStateListener(OnRefreshStateListener listener) {

	}
	public IScrollCallBack getmScrollCallback() {
		return mScrollCallback;
	}
	public void setmScrollCallback(IScrollCallBack mScrollCallback) {
		this.mScrollCallback = mScrollCallback;
	}
}
