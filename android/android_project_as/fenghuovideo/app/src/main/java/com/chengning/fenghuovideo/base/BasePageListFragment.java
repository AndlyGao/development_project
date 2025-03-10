package com.chengning.fenghuovideo.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.avos.avoscloud.AVInstallation;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.base.IForceListenRefresh;
import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuovideo.LoadStateManager;
import com.chengning.fenghuovideo.LoadStateManager.LoadState;
import com.chengning.fenghuovideo.MyJsonHttpResponseHandler;
import com.chengning.fenghuovideo.R;
import com.chengning.fenghuovideo.VideoModuleManager;
import com.chengning.fenghuovideo.data.bean.ChannelBean;
import com.chengning.fenghuovideo.data.bean.ChannelItemBean;
import com.chengning.fenghuovideo.data.bean.SettingBean;
import com.chengning.fenghuovideo.nicevideo.FhVideoPlayerHelper;
import com.chengning.fenghuovideo.util.Common;
import com.chengning.fenghuovideo.util.JUrl;
import com.chengning.fenghuovideo.util.SPHelper;
import com.chengning.fenghuovideo.util.UIHelper;
import com.chengning.fenghuovideo.widget.PullToRefreshListView_FootLoad;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import com.chengning.fenghuovideo.data.access.ChannelItemListDA;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class BasePageListFragment<T> extends Fragment  implements IForceListenRefreshExtend{

	private static final String TAG = BasePageListFragment.class.getSimpleName();
	
	public static final int INIT_NEWSDATA_UI = 10001;
	public static final int ADD_MORE_DATA_UI = 10002;
	public static final int ADD_MORE_DATA_CENTRE = 10003;
	
	public static final int INIT_WAIT_TIME = 500;
	
	private View mView;
	private ImageView mTouchRefresh;
	private ProgressBar mWait; 
	private PullToRefreshListView_FootLoad mPullListView;

	private List<T> mDataList;
	private BaseAdapter mAdapter;
	private Activity mContext;
	private LayoutInflater mInflater;
	private ChannelItemListDA mListServer;
	private LoadStateManager mLoadStateManager;
	
	private HashSet<String> mUrlWorking = new HashSet<String>();

	private int mCurrentPage;
	private int mTarPage;
	//点击更多推荐
//	private int mCurrentPageMore;
//	private int mTarPageMore;
	
	private boolean isDataFromDB = false;
	private boolean isVisibleToUserInViewPager = false;
	
	private Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg){
			processHandlerMessage(msg);
		}
	};
	
	private Runnable LOAD_DATA = new Runnable(){
		@Override
		public void run() {
			getDataOnInit();
		}
	};

	private SettingBean bean;

	protected ChannelBean mMChannelBean;

	private IForceListenRefresh.RefreshState mRefreshState = IForceListenRefresh.RefreshState.RefreshComplete;

	private IForceListenRefresh.OnRefreshStateListener mOnRefreshStateListener;

	private int mCachePage;

	private long mLastRequestTime;

	private String mUrlWorkingUrl= null;
	private String url = null;

	private String mCurVersion;
	private ReturnTopOnScrollListener mScrollListener;
	private IScrollCallBack mScrollCallback = null;
	
	
	public abstract BaseAdapter buildAdapter(Activity activity, List<T> list);
	public abstract String buildTAG();
	public abstract String buildChannel();
	public abstract String buildUrl(int tarPage);
	public abstract void configPullToRefreshListView_FootLoad(PullToRefreshListView v);
	public abstract List<T> onHttpSuccess(Gson gson, String data, ChannelBean channelBean,int tarPage);
	public abstract void onInitNewsDataUI();
	public abstract void onListItemClick(AdapterView<?> parent, View view,
			int position, long id);

	public Activity getContext(){
		return mContext;
	}
	
	public LayoutInflater getLayoutInflater(){
		return mInflater;
	}
	
	public List<T> getDataList(){
		return mDataList;
	}
	
	public BaseAdapter getAdapter(){
		return mAdapter;
	}
	
	public View getRootView(){
		return mView;
	}
	
	public Handler getHandler(){
		return mHandler;
	}
	
	public IScrollCallBack getmScrollCallback() {
		return mScrollCallback;
	}
	public void setmScrollCallback(IScrollCallBack mScrollCallback) {
		this.mScrollCallback = mScrollCallback;
	}
	
	public boolean isEmpty(){
		boolean isEmpty = false;
		isEmpty = mDataList == null || mDataList.size() == 0;
		return isEmpty;
	}
	
	public boolean isVisibleToUserInViewPager(){
		return isVisibleToUserInViewPager;
	}
	
	@Override
	public void forceRefresh(){
		if(mPullListView != null && !mPullListView.isRefreshing()){
			mPullListView.setRefreshing();
		}
	}
	
	@Override
	public void forceCheckRefresh(){
		if(mPullListView != null && !mPullListView.isRefreshing() 
				&& Common.hasNet()
				&& mLoadStateManager.getState() == LoadState.Success){
			long oldTime = SPHelper.getInst().getLong(SPHelper.PREFIX_TIME_UPDATE_CHANNEL + buildChannel());
			if(Common.isTargetTimeBefore(oldTime)){
				if(mAdapter.getCount() > 0){
					mPullListView.setRefreshing();
				}else{
					mLoadStateManager.setState(LoadState.Init);
					getListByHttp();
				}
//				mTarPage = JUrl.PAGE_START;
//				List<MChannelItemBean> list = mListServer.queryChannelItemList(buildChannel(), JUrl.PAGE_START);
//				if(!Common.isListEmpty(list)){
//					isDataFromDB = true;
//					onGetNewsByDB(mTarPage);
//					sendListMessage(INIT_NEWSDATA_UI, list);
//					
//					mHandler.postDelayed(new Runnable() {
//						
//						@Override
//						public void run() {
//							mPullListView.setRefreshing();
//						}
//					}, Common.TIME_WAIT_COMPLETE);
//				}else{
//					getListByHttp();
//				}
			}
		}
	}
	
	@Override
	public void forceSetPageSelected(final boolean isSelected){
	}
	
	@Override
	public void forceTop(){
		mPullListView.setSelection(0);
	}
	
	public void onHttpFailure(){
		
	}
	
	public void onRefreshPull(PullToRefreshBase refreshView) {
		mLoadStateManager.setState(LoadState.Success);
		getListByHttp();
	}
	
	public void showToast(int rosourceId){
		UIHelper.showToast(getContext(), getContext().getString(rosourceId));
	}
	
	public void showToast(String text){
		UIHelper.showToast(getContext(), text);
	}
	
	public void getDataOnInit(){
		
		mTarPage = JUrl.PAGE_START;
		if(Common.hasNet()){
			mCurVersion = Common.getVersionName(getActivity());
			
			long oldTime = SPHelper.getInst().getLong(SPHelper.PREFIX_TIME_UPDATE_CHANNEL + buildChannel());
			if(Common.isTargetTimeBefore(oldTime) || !SPHelper.getInst().getString(SPHelper.KEY_HOME_NAV_DATA_VERSION).equals(mCurVersion)){
				if (!SPHelper.getInst().getString(SPHelper.KEY_HOME_NAV_DATA_VERSION).equals(mCurVersion)) {
					SPHelper.getInst().saveString(SPHelper.KEY_HOME_NAV_DATA_VERSION, mCurVersion);
				}
				List<ChannelItemBean> list = mListServer.queryChannelItemList(buildChannel(), JUrl.PAGE_START);
				if(!Common.isListEmpty(list)){
					isDataFromDB = true;
//					onGetNewsByDB(mTarPage);
					sendListMessage(INIT_NEWSDATA_UI, list);
					
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							mPullListView.setRefreshing();
						}
					}, Common.TIME_WAIT_COMPLETE);
				}else{
					getListByHttp();
				}
			}else{
				getNewsByDB();
			}
		}else{
			getNewsByDB();
		}
	}
	
	public void getNewsByDB(){
		mTarPage = JUrl.PAGE_START;	
//		List<BaseChannelItemBean> mAllDataList = new ArrayList<BaseChannelItemBean>();
		
		List<ChannelItemBean> list = mListServer.queryChannelItemList(buildChannel(), JUrl.PAGE_START);
		
//		if(!Common.isListEmpty(list) || (columnList != null && columnList.getTopic_list() != null)){
		if(!Common.isListEmpty(list)){
			isDataFromDB = true;
//			onGetNewsByDB(mTarPage);
//			if(!Common.isListEmpty(list)){
//				list.addAll(list);
//			}
//			if(columnList != null && columnList.getTopic_list() != null){
//				mAllDataList.addAll(columnList.getTopic_list());
//			}
			// viewpager 未缓存界面只自动缓存数据 然后恢复的情况
			if(mCachePage > mCurrentPage){
				while(mCachePage >= mCurrentPage){
					mCurrentPage = mTarPage;
					mTarPage = mCurrentPage + 1;
					List<ChannelItemBean> tempList = mListServer.queryChannelItemList(buildChannel(), mTarPage);
					
					if(!Common.isListEmpty(tempList)){
//						onGetNewsByDB(mTarPage);
						list.addAll(tempList);
//						mAllDataList.addAll(tempList);
					}
//					else if(tempColumn != null && !Common.isListEmpty(tempColumn.getTopic_list())){
//						mAllDataList.addAll(tempColumn.getTopic_list());
//					}
					else{
						break;
					}
					
				}
				mTarPage = mCurrentPage = mCachePage;
				if(getHandler() != null){
					getHandler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							privateForceSetPageSelected(true);
						}
					}, 100);
				}
			}
			sendListMessage(INIT_NEWSDATA_UI, list);
		}else{
			getListByHttp();
		}
	}
//	public void getNewsMoreByDB(){
//		List<ChannelItemBean> list = mListServer.queryChannelItemList(buildChannel()+AbstractChannelItemListFragment.moreChannelTag, mCurrentPageMore);				
//		sendListMessage(ADD_MORE_DATA_CENTRE, list);			
//	}
	private void privateForceSetPageSelected(final boolean isSelected){
		if(isSelected){
			if(!Common.isListEmpty(mDataList) && mAdapter != null && mAdapter instanceof IAdapterData){
				// 恢复时加载下一页数据
				IAdapterData i = (IAdapterData) mAdapter;
				int lastPosition = i.getViewLastPostion();
				if(Math.abs(mDataList.size() - lastPosition) < 8){
					mPullListView.setFootLoading();
					getNextNewsByDB();
				}
			}
		}
	}
	
	public void getNextNewsByDB(){
		mTarPage = mCurrentPage + 1;
//		List<BaseChannelItemBean> mAllDataList = new ArrayList<BaseChannelItemBean>();
		
		List<ChannelItemBean> list = mListServer.queryChannelItemList(buildChannel(), mTarPage);
		if(!Common.isListEmpty(list)){
			isDataFromDB = true;
//			onGetNewsByDB(mTarPage);
//			if(!Common.isListEmpty(list)){
//				mAllDataList.addAll(list);
//			}
//			if((circleData != null && !Common.isListEmpty(circleData.getTopic_list()))){
//				mAllDataList.addAll(circleData.getTopic_list());
//			}
			sendListMessage(ADD_MORE_DATA_UI, list);
		}else{
			getListByHttp(mTarPage);
		}
	}
	
	private void getListByHttp()
	{
		mCurrentPage = JUrl.PAGE_START;	
//		mCurrentPageMore = JUrl.PAGE_START;
		getListByHttp(mCurrentPage);
	}
	
	protected void getListByHttp(int page)
	{
//		if(fromMore){
////			mTarPage = mCurrentPageMore;
////			mCurrentPageMore = page;
//		}else{
			mTarPage = page;
//		}
		getNewsListByHttp();
	}
	
	private void handleHttpFailure(Throwable throwable){
		mPullListView.setFootLoadFull();
		mPullListView.onRefreshComplete();
		if (!Common.hasNet()) {
			showToast("加载失败，请检查网络");
			if ((mTarPage > JUrl.PAGE_START) || (mDataList.size() > 0)) {
				mLoadStateManager.setState(LoadState.Success);
			} else {
				mLoadStateManager.setState(LoadState.Failure);
			}
		} else {
			if(throwable!=null&&throwable.getClass().isInstance(new ConnectTimeoutException())){  
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
	}

	private void getNewsListByHttp()
	{
//		if(fromMore){
////			 url = JUrl.appendPage(JUrl.URL_GET_CHANNEL_ITEM_LIST_MORE, mTarPage);
//			 url = buildUrl(mCurrentPageMore);
//			 int totalPage = (int) SPHelper.getInst().getInt(SPHelper.PREFIX_KEY_CHANNEL_MAXPAGE + buildChannel()+AbstractChannelItemListFragment.moreChannelTag);
//			 if( totalPage == 0 ||totalPage >= mCurrentPageMore){
//				
//			 }else{
//				 UIHelper.showToast(getContext(), "无更多推荐内容");
//				 return;
//			 }
//		}else{
			 final String url = buildUrl(mTarPage);
//		}
		if(mUrlWorking.contains(url)){
			return;
		}
		if(!Common.hasNet()){
            handleHttpFailure(null);
            onHttpFailure();
            return;
		}
		// 限制请求时间间隔至少1s
		long time = System.currentTimeMillis();
		if(time - mLastRequestTime < 500){
			return;
		}else{
			mLastRequestTime = time;
		}
			mUrlWorkingUrl = url;
			mUrlWorking.add(url);
		setRefreshState(IForceListenRefresh.RefreshState.Refreshing);
		
		RequestParams params = new RequestParams();
		params.put("devicetoken", AVInstallation
				.getCurrentInstallation()
				.getInstallationId());
		HttpUtil.post(url, params, new MyJsonHttpResponseHandler() {
			
			@Override
	        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
	            
	            mUrlWorking.remove(url);
	            handleHttpFailure(throwable);
	            onHttpFailure();
	        };

			@Override
	        public void onFinish() {
	        };

			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				Gson gson = new Gson();
	            ChannelBean channelBean = null;
	            try {
	            	channelBean = gson.fromJson(data, ChannelBean.class);
				} catch (Exception e) {
					e.printStackTrace();
					channelBean = null;
				}
	            if(channelBean != null){
	            	isDataFromDB = false;
	            	mMChannelBean = channelBean;
	            	List list = onHttpSuccess(gson, data, channelBean, mTarPage);
	            	
		            if(mTarPage > JUrl.PAGE_START){
		            	mListServer.setChannelAndPage(list, buildChannel(), mTarPage);
		            	mListServer.insertChannelItemList(list, buildChannel(), mTarPage);
		            	
		            	sendListMessage(ADD_MORE_DATA_UI, list);
		            }else{
		            	SPHelper.getInst().saveInt(SPHelper.PREFIX_KEY_CHANNEL_MAXPAGE + buildChannel(), channelBean.getTotal_page());
		            	SPHelper.getInst().saveLong(SPHelper.PREFIX_TIME_UPDATE_CHANNEL + buildChannel(), System.currentTimeMillis());
		            	
		            	mListServer.setChannelAndPage(list, buildChannel(), mTarPage);
		            	mListServer.clearChannel(buildChannel());
		            	mListServer.insertChannelItemList(list, buildChannel(), mTarPage);
		            	
		            	sendListMessage(INIT_NEWSDATA_UI, list);
		            }
	            setRefreshState(IForceListenRefresh.RefreshState.RefreshComplete);
	            }else{
		            handleHttpFailure(null);
		            onHttpFailure();
		            mUrlWorking.remove(url);
	            }
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				Log.e(TAG, "HttpUtil onDataFailure");   
				if(status == -2){
					// (yangyang)栏目文章列表 GET提交 status: -2  (message) 当前页没有数据！
//					if(isVisibleToUserInViewPager()){
//						UIHelper.showToast(getContext(), message);
//					}
					UIHelper.showToast(getContext(), message);
					
					mPullListView.setFootLoadFull();
					mPullListView.onRefreshComplete();
					setRefreshState(IForceListenRefresh.RefreshState.RefreshComplete);
					mLoadStateManager.setState(LoadState.Success);
		            onHttpFailure();
				}else{
		            handleHttpFailure(null);
		            onHttpFailure();
				}
	            mUrlWorking.remove(url);
			}; 
	    }); 
	}
	
	private void setRefreshState(IForceListenRefresh.RefreshState state){
		this.mRefreshState = state;
		if(mOnRefreshStateListener != null){
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
	public IForceListenRefresh.RefreshState getRefreshState() {
		return mRefreshState;
	}

	@Override
	public void setOnRefreshStateListener(IForceListenRefresh.OnRefreshStateListener listener) {
		this.mOnRefreshStateListener = listener;
		
	}
	
	public void sendListMessage(int what, List list){
		int listSize = 0;
		if(list != null){
			listSize = list.size();
		}
		if(getActivity() != null){
			Message message = getHandler().obtainMessage(what, list);
			message.arg1 = listSize;
		    message.sendToTarget();  
		}
	}
	
	public void processHandlerMessage(Message msg) {
		switch (msg.what) 
		{
		case INIT_NEWSDATA_UI:
			List list = (List) msg.obj;
			int listSize = msg.arg1;
			if(!Common.isListEmpty(list)){
				mDataList.clear();
				mDataList.addAll(list);
				mAdapter.notifyDataSetChanged();
			}else{
				mDataList.clear();
				mAdapter.notifyDataSetChanged();
			}
			mCurrentPage = mTarPage;
			mLoadStateManager.setState(LoadState.Success);
			onInitNewsDataUI();
			mPullListView.setFootLoadFull();
			mPullListView.onRefreshComplete();

			mUrlWorking.remove(mUrlWorkingUrl);
			break;
		case ADD_MORE_DATA_UI:
			List nextlist = (List) msg.obj;
			int nextListSize = msg.arg1;
			if(!Common.isListEmpty(nextlist)){
				mDataList.addAll(nextlist);
				mAdapter.notifyDataSetChanged();
			}
			mCurrentPage = mTarPage;
			mLoadStateManager.setState(LoadState.Success);
			
			mPullListView.setFootLoadFull();
			mPullListView.onRefreshComplete();
			mUrlWorking.remove(mUrlWorkingUrl);
			break;
//		case ADD_MORE_DATA_CENTRE:
//			List<ChannelItemBean> moreList = (List)msg.obj;
//			int position = 0;			
//			if(!Common.isListEmpty(moreList)){
//				//将morelist数据插入元datalist数据中
//				for(int i=0;i<mDataList.size();i++){
//					if(mDataList.get(i) instanceof DynamicBean){
//						position = i;
//						break;
//					}
//				}
//				if(position == 0){
//						position = mDataList.size();
//				}
//				for(ChannelItemBean item : moreList){
//					mDataList.add(position,(T) item);
//					position ++;
//				}
//				mAdapter.notifyDataSetChanged();
////				mCurrentPage = mTarPage;
//				mCurrentPageMore ++;
//				mLoadStateManager.setState(LoadState.Success);
//				
//				mPullListView.setFootLoadFull();
//				mPullListView.onRefreshComplete();
//				mUrlWorking.remove(mUrlWorkingUrl);
//			}else{
//				UIHelper.showToast(getActivity(), "没有更多推荐内容");
//			}
//			break;
		default:
			break;
		}
	}
	
	public void initFragment() {
		initViews();
		installListeners();
		initDatas();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		if(savedInstanceState != null){
			mCachePage = savedInstanceState.getInt("cache_page");
		}
        super.onCreate(savedInstanceState); 
    }
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);  
		initFragment(); 
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	    
		Common.setTheme(getActivity());
		this.mInflater = inflater;
	    mView = inflater.inflate(R.layout.fragment_base_list, container, false);
	    return mView ;
	}
	
	@Override  
	public void setUserVisibleHint(boolean isVisibleToUser) {  
		super.setUserVisibleHint(isVisibleToUser);  
		isVisibleToUserInViewPager = isVisibleToUser;
		if(!isVisibleToUser){
			mHandler.removeCallbacks(LOAD_DATA);
		}else{
			if(mLoadStateManager != null && mLoadStateManager.getState() == LoadState.Init){
				mHandler.postDelayed(LOAD_DATA,INIT_WAIT_TIME);
			}
			
		}
	}
   
	protected void initViews() {
		mContext = this.getActivity();
		
		mTouchRefresh = (ImageView)mView.findViewById(R.id.touchRefrush); 
		mWait = (ProgressBar)mView.findViewById(R.id.wait);
		
		mPullListView = (PullToRefreshListView_FootLoad) mView.findViewById(R.id.list);
		configPullToRefreshListView_FootLoad(mPullListView);
	} 
	 
	protected void initDatas() {
		
		mCurrentPage = JUrl.PAGE_START;
//		mCurrentPageMore = JUrl.PAGE_START;
		
		mDataList = new ArrayList<T>();
		mListServer = ChannelItemListDA.getInst(mContext);
		mLoadStateManager = new LoadStateManager();
		mLoadStateManager.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {
			
			@Override
			public void OnStateChange(LoadState state, Object obj) {
				switch (state) {
				case Init:
					mWait.setVisibility(View.VISIBLE);
					mTouchRefresh.setVisibility(View.GONE);
					mPullListView.setVisibility(View.GONE);
					break;
				case Success:
					mWait.setVisibility(View.GONE);
					mTouchRefresh.setVisibility(View.GONE);
					mPullListView.setVisibility(View.VISIBLE);
					break;
				case Failure:
					mWait.setVisibility(View.GONE);
					mTouchRefresh.setVisibility(View.VISIBLE);
					mPullListView.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
		});

		mAdapter = buildAdapter(mContext, mDataList);
		mPullListView.setAdapter(mAdapter);

		mLoadStateManager.setState(LoadState.Init);
		mHandler.postDelayed(LOAD_DATA,INIT_WAIT_TIME);
	}

	
	public void dealScrollY(){
		if (mScrollListener == null) {
			return;
		}
		mScrollListener.dealScrollY();
	}
	
	protected void installListeners() {
		mTouchRefresh.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				getDataOnInit();
			}
		});   
		
		mPullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onListItemClick(parent, view, position, id);
			}
		});
		//加入滑动距离的判断..
		mScrollListener = new ReturnTopOnScrollListener(getmScrollCallback(), mPullListView);
//		AbsListView.OnScrollListener onScrollListener = new VideoModuleManager.OnVideoModuleScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true, mScrollListener));
		AbsListView.OnScrollListener onScrollListener = FhVideoPlayerHelper.getInst().getOnScrollListener();
				mPullListView.setOnScrollListener(onScrollListener);
		mPullListView.getRefreshableView().setRecyclerListener(new AbsListView.RecyclerListener() {
			@Override
			public void onMovedToScrapHeap(View view) {
//				VideoModuleManager.getInst().onMovedToScrapHeap(null, view);
				FhVideoPlayerHelper.getInst().onMovedToScrapHeap(view);
			}
		});
		mPullListView.setOnRefreshListener(new OnRefreshListener(){
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
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener(){

			@Override
			public void onLastItemVisible() {
				handleOnLastItemVisible();
			}
			
		}); 
	}
	
	public boolean handleOnLastItemVisible(){
		boolean hasMore;
		if(SPHelper.getInst().getInt(SPHelper.PREFIX_KEY_CHANNEL_MAXPAGE + buildChannel()) > mCurrentPage){			
			hasMore = true;
			if(isDataFromDB){
				mPullListView.setFootLoading();
				getNextNewsByDB();
			}else{
				if(Common.hasNet()){
					mPullListView.setFootLoading();
					mTarPage = mCurrentPage + 1;
					getListByHttp(mTarPage);
				}else{
					handleHttpFailure(null);
					onHttpFailure();
				}
			}
		}else{
			hasMore = false;
			mPullListView.setFootLoadFull();
		}
		return hasMore;
	}
	
	@Override
	public void onAttach(Activity activity){
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
	public void onSaveInstanceState(Bundle outState){
		outState.putInt("cache_page", mCurrentPage);
		super.onSaveInstanceState(outState);
	}
	
}
