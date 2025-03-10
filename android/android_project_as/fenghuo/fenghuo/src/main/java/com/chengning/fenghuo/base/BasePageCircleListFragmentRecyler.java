package com.chengning.fenghuo.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chengning.common.base.BaseFragment;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.base.IForceListenRefresh;
import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.LoadStateManager;
import com.chengning.fenghuo.LoadStateManager.LoadState;
import com.chengning.fenghuo.MyJsonHttpResponseHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.adapter.CircleChannelItemAdapterRecyler;
import com.chengning.fenghuo.base.BaseRecylerViewAdapter.OnItemClickListener;
import com.chengning.fenghuo.data.access.CircleChannelItemDA;
import com.chengning.fenghuo.data.bean.CircleBean;
import com.chengning.fenghuo.data.bean.CircleListBean;
import com.chengning.fenghuo.data.bean.DynamicItemBean;
import com.chengning.fenghuo.data.bean.SettingBean;
import com.chengning.fenghuo.util.ArticleCommentListeners.CircleCommentHandleSuccessListener;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.JUrl;
import com.chengning.fenghuo.util.SPHelper;
import com.chengning.fenghuo.util.UIHelper;
import com.chengning.fenghuo.widget.LoadMoreRecyclerView;
import com.chengning.fenghuo.widget.ProgressRefreshView;
import com.chengning.fenghuo.widget.PullToRefreshRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public abstract class BasePageCircleListFragmentRecyler<T> extends BaseFragment
		implements IForceListenRefresh {

	private static final String TAG = BasePageListFragment.class
			.getSimpleName();

	public static final int INIT_NEWSDATA_UI = 10001;
	public static final int ADD_MORE_DATA_UI = 10002;
	public static final int UPDATE_DATA_UI = 10003;

	public static final int INIT_WAIT_TIME = 500;

	private static final int DELETE_DATA_UI = 10004;

	private View mView;
	private ProgressRefreshView mProgressRefreshView;
	private PullToRefreshRecyclerView mPullListView;

	private List mDataList;
	private BaseRecylerViewAdapter mAdapter;
	private Activity mContext;
	private LayoutInflater mInflater;
	private CircleChannelItemDA mListServer;
	private LoadStateManager mLoadStateManager;

	private HashSet<String> mUrlWorking = new HashSet<String>();
	public int qun_id;

	private int mCurrentPage;
	private int mTarPage;

	private boolean isDataFromDB = false;
	private boolean isVisibleToUserInViewPager = false;
	
	private boolean isHasNextPage = false;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			processHandlerMessage(msg);
		}
	};

	private Runnable LOAD_DATA = new Runnable() {
		@Override
		public void run() {
			getDataOnInit();
		}
	};

	private SettingBean bean;

	protected CircleBean mcircleBean;

	private RefreshState mRefreshState = RefreshState.RefreshComplete;

	private OnRefreshStateListener mOnRefreshStateListener;

	private int mCachePage;

	private long mLastRequestTime;

	private String mUrlWorkingUrl = null;

	private TextView mCircleListTopName;

	private String url = "";

	
	public abstract BaseRecylerViewAdapter buildAdapter(Activity activity, List<T> list);

	public abstract String buildTAG();

	public abstract String buildChannel();

	public abstract String buildUrl(int tarPage);

	public abstract void onHttpSuccess(Gson gson, String data, String message, int page);
	public abstract void onListItemClick(View view,int position);

	public void onHttpFailure() {

	}

	public Activity getContext() {
		return mContext;
	}

	public LayoutInflater getLayoutInflater() {
		return mInflater;
	}

	public List<T> getDataList() {
		return mDataList;
	}

	public BaseRecylerViewAdapter getAdapter() {
		return mAdapter;
	}

	public View getRootView() {
		return mView;
	}

	public boolean isVisibleToUserInViewPager() {
		return isVisibleToUserInViewPager;
	}

	@Override
	public void forceRefresh() {
		if (mPullListView != null && !mPullListView.isRefreshing()) {
			mPullListView.setRefreshing();
		}
	}

	@Override
	public void forceCheckRefresh() {
		if (mPullListView != null && !mPullListView.isRefreshing()
				&& Common.hasNet()
				&& mLoadStateManager.getState() == LoadState.Success) {
			long oldTime = SPHelper.getInst().getLong(
					SPHelper.PREFIX_TIME_UPDATE_CIRCLE + buildChannel());
			if (Common.isTargetTimeBefore(oldTime)) {
				if (mAdapter.getItemCount()> 0) {
					mPullListView.setRefreshing();
				} else {
					mLoadStateManager.setState(LoadState.Init);
					getListByHttp();
				}
			}
		}
	}

	public void onRefreshPull(PullToRefreshBase refreshView) {
		mLoadStateManager.setState(LoadState.Success);
		getListByHttp();
	}

	public void showToast(int rosourceId) {
		UIHelper.showToast(getContext(), getContext().getString(rosourceId));
	}

	public void showToast(String text) {
		UIHelper.showToast(getContext(), text);
	}

	public void getDataOnInit() {
		mTarPage = JUrl.PAGE_START;
		if (Common.hasNet()) {
			long oldTime = SPHelper.getInst().getLong(
					SPHelper.PREFIX_TIME_UPDATE_CIRCLE + buildChannel());
			if (Common.isTargetTimeBefore(oldTime) || 
					SPHelper.getInst().getBoolean(SPHelper.KEY_LAST_LOGIN_STATE_CIRCLE) != App.getInst().isLogin()) {
				CircleListBean bean = mListServer.queryChannelItem(buildChannel(),
						JUrl.PAGE_START);
				if (null != bean) {
					isDataFromDB = true;
					initCircleListData(bean);
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							mPullListView.setRefreshing();
						}
					}, 500);
				} else {
					getListByHttp();
				}
			} else {
				getNewsByDB();
			}
		} else {
			getNewsByDB();
		}
	}

	public void getNewsByDB() {
		mTarPage = JUrl.PAGE_START;
		CircleListBean bean = mListServer.queryChannelItem(buildChannel(),
				JUrl.PAGE_START);
		if (null != bean) {
			isDataFromDB = true;

			// viewpager 未缓存界面只自动缓存数据 然后恢复的情况
			if (mCachePage > mCurrentPage) {
				while (mCachePage >= mCurrentPage) {
					mCurrentPage = mTarPage;
					mTarPage = mCurrentPage + 1;
					CircleListBean tempBean = mListServer.queryChannelItem(
							buildChannel(), mTarPage);
					if (null != tempBean) {
						bean = tempBean;
					} else {
						break;
					}
				}
				mTarPage = mCurrentPage = mCachePage;
				if (mHandler != null) {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							privateForceSetPageSelected(true);
						}
					}, 100);
				}
			}
			
			initCircleListData(bean);
			
		} else {
			getListByHttp();
		}
	}

	/**
	 * 初始化圈数据
	 * @param bean
	 */
	private void initCircleListData(CircleListBean bean) {
		sendListMessage(INIT_NEWSDATA_UI, bean);
		
	}

	private void privateForceSetPageSelected(final boolean isSelected) {
		if (isSelected) {
			if (!Common.isListEmpty(mDataList) && mAdapter != null
					&& mAdapter instanceof IAdapterData) {
				// 恢复时加载下一页数据
				IAdapterData i = (IAdapterData) mAdapter;
				int lastPosition = i.getViewLastPostion();
				if (Math.abs(mDataList.size() - lastPosition) < 8) {
					mPullListView.getRefreshableView().setLoadingMore(true);
					getNextNewsByDB();
				}
			}
		}
	}

	public void getNextNewsByDB() {
		mTarPage = mCurrentPage + 1;
		CircleListBean bean = mListServer
				.queryChannelItem(buildChannel(), mTarPage);
		if (null != bean) {
			isDataFromDB = true;
			sendListMessage(ADD_MORE_DATA_UI, bean);
		} else {
			getListByHttp(mTarPage);
		}
	}

	private void getListByHttp() {
		mCurrentPage = JUrl.PAGE_START;
		getListByHttp(mCurrentPage);
	}

	private void getListByHttp(int page) {
		mTarPage = page;
		getNewsListByHttp();
	}

	private void handleHttpFailure(Throwable throwable) {
		if (!Common.hasNet()) {
			showToast("加载失败，请检查网络");
			if ((mTarPage > JUrl.PAGE_START) || (mDataList.size() > 0)) {
				mLoadStateManager.setState(LoadState.Success);
			} else {
				mLoadStateManager.setState(LoadState.Failure);
			}
		} else {
			if (throwable != null
					&& throwable.getClass().isInstance(
							new ConnectTimeoutException())) {
				showToast(R.string.intent_timeout);
			} else {
				showToast(R.string.server_fail);
			}
			if ((mTarPage > JUrl.PAGE_START) || (mDataList.size() > 0)) {
				mLoadStateManager.setState(LoadState.Success);
			} else {
				mLoadStateManager.setState(LoadState.Failure);
			}
		}
		handleRefreshLoadComplete(mTarPage == JUrl.PAGE_START);
	}

	/**
	 * 数据刷新加载完成
	 * @param isFirstPage 
	 */
	private void handleRefreshLoadComplete(boolean isFirstPage) {
		if (!isFirstPage) {
			mPullListView.getRefreshableView().notifyMoreFinish();
		}
		mPullListView.onRefreshComplete();
		mPullListView.getRefreshableView().setAutoLoadMoreEnable(SPHelper.getInst().getInt(
					SPHelper.PREFIX_KEY_CIRCLE_MAXPAGE
						+ buildChannel()) > mCurrentPage);
	}

	private void getNewsListByHttp() {
		url = buildUrl(mTarPage);
		if (mUrlWorking.contains(url)) {
			return;
		}
		if (!Common.hasNet()) {
			handleHttpFailure(null);
			onHttpFailure();
			return;
		}
		// 限制请求时间间隔至少1s
		long time = System.currentTimeMillis();
		if (time - mLastRequestTime < 500) {
			return;
		} else {
			mLastRequestTime = time;
		}
		mUrlWorkingUrl = url;
		mUrlWorking.add(url);
		setRefreshState(RefreshState.Refreshing);
		HttpUtil.get(url, null, new MyJsonHttpResponseHandler() {
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				CircleListBean circleBean = new CircleListBean();
				
				Gson gson = new Gson();
				int totalPage = 0;
				try {
					JSONObject mObj = new JSONObject(data);
					totalPage = mObj.optInt("total_page", 0);
					ArrayList<DynamicItemBean> list = new ArrayList<DynamicItemBean>();
					list = gson.fromJson(mObj.optString("list"), new TypeToken<List<DynamicItemBean>>(){}.getType());	
					circleBean.setList(list);
					
				} catch (Exception e) {
					e.printStackTrace();
					circleBean = null;
				}

				if (circleBean != null) {
					isDataFromDB = false;
					if (mTarPage > JUrl.PAGE_START) {
						sendListMessage(ADD_MORE_DATA_UI, circleBean);
					} else {
						SPHelper.getInst().saveInt(
								SPHelper.PREFIX_KEY_CIRCLE_MAXPAGE
										+ buildChannel(), totalPage);
						SPHelper.getInst().saveLong(
								SPHelper.PREFIX_TIME_UPDATE_CIRCLE
										+ buildChannel(),
								System.currentTimeMillis());
						mListServer.clearChannel(buildChannel());
						mListServer.insertCircle(circleBean, buildChannel(),
								mTarPage);
						sendListMessage(INIT_NEWSDATA_UI, circleBean);
					}
					setRefreshState(RefreshState.RefreshComplete);
					onHttpSuccess(gson, data, message, mTarPage);
				} else {
					handleHttpFailure(null);
					onHttpFailure();
					mUrlWorking.remove(url);
				}
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				if (status == -2) {
					UIHelper.showToast(getContext(), message);
					handleRefreshLoadComplete(mTarPage == JUrl.PAGE_START);
					setRefreshState(RefreshState.RefreshComplete);
					mLoadStateManager.setState(LoadState.Success);
					onHttpFailure();
				} else {
					handleHttpFailure(null);
					onHttpFailure();
				}
				mUrlWorking.remove(url);
			};

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				mUrlWorking.remove(url);
				handleHttpFailure(throwable);
				onHttpFailure();
			};
		});
	}

	protected void setCircleListTopDiscussName(CircleBean bean) {
		StringBuffer buffer = new StringBuffer("热门讨论(");
		buffer.append(bean.getThread_num()).append(")");
		mCircleListTopName.setText(buffer.toString());
	}

	private void setRefreshState(RefreshState state) {
		this.mRefreshState = state;
		if (mOnRefreshStateListener != null) {
			switch (mRefreshState) {
			case Refreshing:
				mOnRefreshStateListener.onStart();
				break;
			case RefreshComplete:
				mOnRefreshStateListener.onFinish();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public RefreshState getRefreshState() {
		return mRefreshState;
	}

	@Override
	public void setOnRefreshStateListener(OnRefreshStateListener listener) {
		this.mOnRefreshStateListener = listener;

	}

	public void sendListMessage(int what, CircleListBean bean) {
		if (bean != null && getActivity() != null) {
			Message message = mHandler
					.obtainMessage(what, bean.getList());
			message.sendToTarget();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mCachePage = savedInstanceState.getInt("cache_page");
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Common.setTheme(getActivity());
		this.mInflater = inflater;
		mView = inflater.inflate(R.layout.fragment_base_circle_list, container,
				false);
		return mView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		isVisibleToUserInViewPager = isVisibleToUser;
		if (!isVisibleToUser) {
			mHandler.removeCallbacks(LOAD_DATA);
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		mContext = activity;
		super.onAttach(activity);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("cache_page", mCurrentPage);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void initViews() {
		mContext = this.getActivity();
		mProgressRefreshView = new ProgressRefreshView(mContext, mView);
		mPullListView = (PullToRefreshRecyclerView) mView
				.findViewById(R.id.list);
	}

	@Override
	public void initDatas() {
		mCurrentPage = JUrl.PAGE_START;
		mDataList = new ArrayList<T>();
		mListServer = CircleChannelItemDA.getInst(mContext);
		initLoadState();
		mAdapter = buildAdapter(mContext, mDataList);
		if (mAdapter instanceof CircleChannelItemAdapterRecyler) {
			((CircleChannelItemAdapterRecyler)mAdapter).setCircleCommentHandleCallback(new CircleCommentHandleSuccessListener() {
				
				@Override
				public void digSuccess(final DynamicItemBean bean, final int pos) {
					mHandler.obtainMessage(UPDATE_DATA_UI, pos, pos, bean).sendToTarget();
				}

				@Override
				public void commentSuccess(DynamicItemBean bean, int pos) {
					mHandler.obtainMessage(UPDATE_DATA_UI, pos, pos, bean).sendToTarget();
				}

				@Override
				public void deleteSuccess(DynamicItemBean bean, int pos) {
					mHandler.obtainMessage(DELETE_DATA_UI, pos, pos, bean).sendToTarget();
				}
			});
		}
		mPullListView.getRefreshableView().setHasFixedSize(true);
		mPullListView.setAdapter(mAdapter);
		mHandler.postDelayed(LOAD_DATA, INIT_WAIT_TIME);
	}

	private void initLoadState() {
		mLoadStateManager = new LoadStateManager();
		mLoadStateManager.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {

			@Override
			public void OnStateChange(LoadState state, Object obj) {
				switch (state) {
				case Init:
					mProgressRefreshView.setWaitVisibility(true);
					mProgressRefreshView.setRefreshVisibility(false);
					mPullListView.setVisibility(View.GONE);
					break;
				case Success:
					mProgressRefreshView.setRootViewVisibility(false);
					mPullListView.setVisibility(View.VISIBLE);
					break;
				case Failure:
					mProgressRefreshView.setWaitVisibility(false);
					mProgressRefreshView.setRefreshVisibility(true);
					mPullListView.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
		});
		
		mLoadStateManager.setState(LoadState.Init);
	}

	@Override
	public void installListeners() {

		mProgressRefreshView.setRefreshOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				getHandler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getDataOnInit();
					}
				}, 200);
			}
		});
		
		mPullListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(final PullToRefreshBase refreshView) {
				getHandler().postDelayed(new Runnable() {

					@Override
					public void run() {
						onRefreshPull(refreshView);
					}
				}, 200);

			}

		});
		
		mAdapter.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClickListener(View view, int position) {
				onListItemClick(view, position);
			}
		});
		
		mPullListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getContext()));
		mPullListView.setScrollingWhileRefreshingEnabled(true);
		mPullListView.getRefreshableView().setAutoLoadMoreEnable(true);
		mPullListView.getRefreshableView().setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
            	mPullListView.postDelayed(new Runnable() {

					@Override
                     public void run() {
                    	 if (SPHelper.getInst().getInt(
         						SPHelper.PREFIX_KEY_CIRCLE_MAXPAGE
         								+ buildChannel()) > mCurrentPage) {
         					if (isDataFromDB) {
         						getNextNewsByDB();
         					} else {
         						if (Common.hasNet()) {
         							mTarPage = mCurrentPage + 1;
         							getListByHttp(mTarPage);
         						} else {
         							handleHttpFailure(null);
         							onHttpFailure();
         						}
         					}
         				} 
                    	 
                     }
                }, 1000);
           }

      });

	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case INIT_NEWSDATA_UI:
			List list = (List) msg.obj;
			int listSize = msg.arg1;
			if (!Common.isListEmpty(list)) {
				mDataList.clear();
				mDataList.addAll(list);
				
			} else {
				mDataList.clear();
			}
//			mPullListView.setAdapter(mAdapter);
			mPullListView.getRefreshableView().getAdapter().notifyDataSetChanged();
			mCurrentPage = mTarPage;
			mLoadStateManager.setState(LoadState.Success);

			handleRefreshLoadComplete(mTarPage == JUrl.PAGE_START);
			mUrlWorking.remove(mUrlWorkingUrl);
			
			break;
		case ADD_MORE_DATA_UI:
			List nextlist = (List) msg.obj;
			int nextListSize = msg.arg1;
			if (!Common.isListEmpty(nextlist)) {
				mDataList.addAll(nextlist);
				mAdapter.notifyDataSetChanged();
			}
			mCurrentPage = mTarPage;
			mLoadStateManager.setState(LoadState.Success);

			handleRefreshLoadComplete(mTarPage == JUrl.PAGE_START);
			
			mUrlWorking.remove(mUrlWorkingUrl);
			break;
		case UPDATE_DATA_UI:
			int pos = msg.arg1;
			mDataList.set(pos, msg.obj);
			mListServer.clearChannel(buildChannel());
			CircleListBean circleBean = new CircleListBean();
			circleBean.setList((ArrayList<DynamicItemBean>) mDataList);
			mListServer.insertCircle(circleBean , buildChannel(),
					mTarPage);
			break;
		case DELETE_DATA_UI:
			int delPos = msg.arg1;
			mDataList.remove(delPos);
			mListServer.clearChannel(buildChannel());
			CircleListBean circleBean1 = new CircleListBean();
			circleBean1.setList((ArrayList<DynamicItemBean>) mDataList);
			mListServer.insertCircle(circleBean1 , buildChannel(),
					mTarPage);
			break;
		default:
			break;
		}
	}

	public PullToRefreshRecyclerView getPullListView() {
		return mPullListView;
	}
	
}
