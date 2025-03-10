package com.chengning.fenghuo.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengning.common.base.BaseFragment;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.util.HttpUtil;
import com.chengning.common.util.PageHelper;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.LoadStateManager;
import com.chengning.fenghuo.LoadStateManager.LoadState;
import com.chengning.fenghuo.MyJsonHttpResponseHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.SettingManager;
import com.chengning.fenghuo.activity.ArticleActivity;
import com.chengning.fenghuo.adapter.CircleChannelItemAdapterRecyler;
import com.chengning.fenghuo.adapter.ColumnistArticleListAdapterRecyler;
import com.chengning.fenghuo.base.BaseListBean;
import com.chengning.fenghuo.base.BaseRecylerViewAdapter;
import com.chengning.fenghuo.base.BaseRecylerViewAdapter.OnItemClickListener;
import com.chengning.fenghuo.base.IScrollCallBack;
import com.chengning.fenghuo.base.ReturnTopOnScrollListener;
import com.chengning.fenghuo.data.bean.BaseArticlesBean;
import com.chengning.fenghuo.data.bean.UserInfoDiscussListBean;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.JUrl;
import com.chengning.fenghuo.util.UIHelper;
import com.chengning.fenghuo.widget.LoadMoreRecyclerView;
import com.chengning.fenghuo.widget.ProgressRefreshView;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

@SuppressLint("ResourceAsColor")
public class UserDynamicFragment extends BaseFragment {

	private static final String TAG = UserDynamicFragment.class.getSimpleName();
	public static boolean isForeground = false;
	private static final int DATA_SUCCESS = 1;

	private LoadMoreRecyclerView mPullListView;
	private List<BaseArticlesBean> mDataList;
	private BaseRecylerViewAdapter mAdapter;

	private TextView mTitle;

	private View mView;

	private String mUid;
	private ProgressRefreshView mProgressRefresh;
	private LoadStateManager mLoadStateManager;

	private PageHelper mPage;
	private boolean mIsColumnist;

	private IScrollCallBack mScrollCallback = null;
	private ImageView mTopBtn;
	private ReturnTopOnScrollListener mScrollListener;

	public UserDynamicFragment() {
	}

	public static UserDynamicFragment newInstace(String uid, boolean isColumnist) {
		UserDynamicFragment fragment = new UserDynamicFragment();
		Bundle bundle = new Bundle();
		bundle.putString("uid", uid);
		bundle.putBoolean("isColumnist", isColumnist);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_user_article, container,
				false);
		return mView;
	}

	@Override
	public void initViews() {
		mTitle = (TextView) mView.findViewById(R.id.circle_list_dynamic_name);
		mPullListView = (LoadMoreRecyclerView) mView
				.findViewById(R.id.list);
		mProgressRefresh = new ProgressRefreshView(getContext(), mView);
		mTopBtn = (ImageView) mView
				.findViewById(R.id.topbtn);
	}

	@Override
	public void initDatas() {
		mPage = new PageHelper();
		mUid = getArguments().getString("uid");
		mIsColumnist = getArguments().getBoolean("isColumnist");
		initLoadState();
		showEmpty();
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
	}

	/**
	 * 初始化加载状态
	 */
	private void initLoadState() {
		mLoadStateManager = new LoadStateManager();
		mLoadStateManager
				.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {

					@Override
					public void OnStateChange(LoadState state, Object obj) {
						switch (state) {
							case Init:
								mProgressRefresh.setWaitVisibility(true);
								mProgressRefresh.setRefreshVisibility(false);
								mProgressRefresh.setNoDataTvVisibility(false);
								mPullListView.setVisibility(View.GONE);
								break;
							case Success:
								mProgressRefresh.setRootViewVisibility(false);
								mPullListView.setVisibility(View.VISIBLE);
								break;
							case NoData:
								mProgressRefresh.setWaitVisibility(false);
								mProgressRefresh.setRefreshVisibility(false);
								mProgressRefresh.setNoDataTvVisibility(true);
								mPullListView.setVisibility(View.GONE);
								break;
							case Failure:
								mProgressRefresh.setWaitVisibility(false);
								mProgressRefresh.setRefreshVisibility(true);
								mProgressRefresh.setNoDataTvVisibility(false);
								mPullListView.setVisibility(View.GONE);
								break;
						}
					}
				});
		mLoadStateManager.setState(LoadState.Init);
	}

	@Override
	public void installListeners() {

		mProgressRefresh.setRefreshOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				getDataByHttp(true);
			}
		});

		mAdapter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClickListener(View view, int position) {
				// TODO 跳转到讨论详情
				if (mIsColumnist) {
					ArticleActivity.launch(getContext(),
							mDataList.get(position));
				} else {
//					DynamicDetailActivity.launch(getContext(),
//							mDataList.get(position));
				}
			}
		});
		//加入滑动距离的判断..
//		mScrollListener = new ReturnTopOnScrollListener(getmScrollCallback(), mPullListView);
//		mPullListView.setOnScrollListener(mScrollListener);
		mTopBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				forceTop();
				mTopBtn.setVisibility(View.GONE);
			}
		});
	}

	private void getDataByHttp() {
		getDataByHttp(false);
	}

	private void getDataByHttp(final boolean isFirstPage) {
		getHandler().postDelayed(new Runnable() {

			@Override
			public void run() {
				getDataByHttp(false, isFirstPage);
			}
		}, Common.TIME_WAIT_COMPLETE);
	}

	@Deprecated
	private void getDataByHttp(boolean isIgnoreRequestRunning,
			final boolean isFirstPage) {
		String url = null;
		boolean isMy = App.getInst().isLogin()
				&& App.getInst().getUserInfoBean().getUid().equals(mUid);
		url = isMy ? JUrl.SITE + JUrl.URL_GET_MY_DISCUSS_LIST : JUrl.appendUid(
				JUrl.SITE + JUrl.URL_GET_DISCUSS_LIST, mUid);
		url = isFirstPage ? mPage.appendFirstPage(url) : mPage
				.appendNextPageAndMaxid(url);
		if (!isIgnoreRequestRunning && mPage.isRequestRunning()) {
			if (!mPage.equalsRunningUrl(url.toString())) {
				if (isFirstPage) {
//					mPullListView.onRefreshComplete();
				} else {
//					mPullListView.setFootLoadFull();
				}
			}
			return;
		}
		mPage.setRequestStart(url.toString());

		RequestParams params = new RequestParams();

		params.put("type", mIsColumnist ? 2 : 1);
		HttpUtil.get(url.toString(), params, new MyJsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  Throwable throwable, JSONObject errorResponse) {
				Common.handleHttpFailure(getContext(), throwable);
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
				BaseListBean bean = null;
				bean = gson.fromJson(data,
						UserInfoDiscussListBean.class);
				StringBuffer buffer = new StringBuffer(mIsColumnist ? "全部文章("
						: "全部圈子(");
				buffer.append(bean.getSize()).append(")");
				mTitle.setText(buffer.toString());
				if (isFirstPage) {
					mPage.clear();
				}
				int currentPage = (null == bean ? 1
						: mPage.getCurrentPage() + 1);
				mPage.setCurrentPage(currentPage);
				mPage.setPageData(currentPage,
						(null == bean ? null : bean.getList()));
				mPage.setMaxPage((null == bean ? 0 : bean.getTotal_page()));
				getHandler().obtainMessage(DATA_SUCCESS).sendToTarget();

			}

			@Override
			public void onDataFailure(int status, String mod, String message,
									  String data, JSONObject obj) {
				UIHelper.showToast(getContext(), message);
				handleRefreshSuccess();
			}
		});
	}

	protected void handleRefreshSuccess() {
		if (!mPage.isCurrentPageFirst()) {
			mPullListView.notifyMoreFinish();
		}
		mPullListView.setAutoLoadMoreEnable(mPage.hasNextPage());	
		mPage.setRequestEnd();
	}

	@Override
	public void uninstallListeners() {

	}

	private void showEmpty() {
		mDataList = new ArrayList<BaseArticlesBean>();

		if (mIsColumnist) {
			mAdapter = new ColumnistArticleListAdapterRecyler(getContext(), mDataList,
					SettingManager.getInst().getNoPicModel(), false, new ColumnistArticleListAdapterRecyler.NameFollowListener() {
						
						@Override
						public void followChange() {
							
						}
					}, mPullListView);
		} else {
			boolean isMy = App.getInst().isLogin()
					&& App.getInst().getUserInfoBean().getUid().equals(mUid);
			if (isMy) {
				mAdapter = new CircleChannelItemAdapterRecyler(getContext(),
						mDataList, true, mTitle, mPullListView);
			} else {
				mAdapter = new CircleChannelItemAdapterRecyler(getContext(),
						mDataList, false, false, mPullListView);
			}
		}

		mPullListView.setAdapter(mAdapter);
		mPullListView.setLayoutManager(new LinearLayoutManager(mPullListView.getContext())); 
		mPullListView.setAutoLoadMoreEnable(true);
		mPullListView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
            	mPullListView.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                    	 if (!TextUtils.isEmpty(mUid) && mPage.hasNextPage()) {
 							getDataByHttp();
 						}
                    	 
                     }
                }, 1000);
           }

      });
		getDataByHttp(true);

	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {

		case DATA_SUCCESS:

			mDataList.clear();
			mDataList.addAll(mPage.getDataList());
			if (!Common.isListEmpty(mDataList)) {
				mPage.setMaxid(mDataList.get(0).getTid());
				mLoadStateManager.setState(LoadState.Success);
			} else {
				mLoadStateManager.setState(LoadState.NoData);
				mProgressRefresh.setNoDataTvText(mIsColumnist ? "暂无文章"
						: "暂无圈子");
			}
			mAdapter.notifyDataSetChanged();
			handleRefreshSuccess();
			break;
		default:
			break;
		}
	}

	public IScrollCallBack getmScrollCallback() {
		return mScrollCallback;
	}
	public void setmScrollCallback(IScrollCallBack mScrollCallback) {
		this.mScrollCallback = mScrollCallback;
	}

	public void forceTop() {
//		mPullListView.setSelection(0);
	}

	public void dealScrollY(){
		if (mScrollListener == null) {
			return;
		}
		mScrollListener.dealScrollY();
	}

}
