package com.shenyuan.militarynews.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.chengning.common.base.BaseActivity;
import com.chengning.common.base.BaseResponseBean;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.base.MyRetrofitResponseCallback;
import com.chengning.common.base.util.RetrofitManager;
import com.chengning.common.util.PageHelper;
import com.chengning.common.util.StatusBarUtil;
import com.chengning.common.widget.MultiStateView;
import com.chengning.common.widget.MultiStateView.ViewState;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.LoadStateManager;
import com.shenyuan.militarynews.LoadStateManager.LoadState;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.adapter.SearchNewsAdapter;
import com.shenyuan.militarynews.beans.data.MChannelItemBean;
import com.shenyuan.militarynews.beans.data.SearchNewsListBean;
import com.shenyuan.militarynews.data.access.LocalStateServer;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.SPHelper;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.utils.Utils;
import com.shenyuan.militarynews.views.PullToRefreshListView_FootLoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

;

public class SearchNewsActivity extends BaseActivity {

	
	private static final int DATA_SUCCESS = 10000;
	
	private static final int MAX_HISTORY = 100;
	
	private EditText mEditText;
	private Button mBtnClear;
	private View mCancel;
	private TextView mCancelTv;
	private PullToRefreshListView_FootLoad mPullListView;  
	private MultiStateView mMultiStateView;
	
	private View mSearchHistoryLayout;
	private ListView mHistoryListView;
	private Button mSearchHistoryClear; 
	
	private List<String> mHistoryList;
	private SearchNewsAdapter mAdapter;
	private List<MChannelItemBean> mList;
	private LoadStateManager mLoadStateManager;
	private PageHelper mPage;
	private HistoryAdapter mHistoryAdapter;
	
	private String mStrSearchKeyWord;
	private String mStrSearchHistory;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(this);
		setContentView(R.layout.activity_searchnews);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.normalstate_bg), true);
        }
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initViews() {
		
		mEditText = (EditText) findViewById(R.id.search_edittext);
		mBtnClear = (Button) findViewById(R.id.title_bar_search_clear);
		mCancel = findViewById(R.id.title_bar_cancel);
		mCancelTv = (TextView) findViewById(R.id.title_bar_cancel_text);
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.setVisibility(View.INVISIBLE);
		mPullListView = (PullToRefreshListView_FootLoad) findViewById(R.id.list);
		mPullListView.setMode(Mode.DISABLED);
		
		mSearchHistoryLayout= findViewById(R.id.search_history_ll);
		mHistoryListView = (ListView) findViewById(R.id.history_list);
		mSearchHistoryClear = (Button) findViewById(R.id.search_history_clear);
	}

	@Override
	public void initDatas() {
		mPage = new PageHelper();
		mList = new ArrayList<MChannelItemBean>();
		initLoadState();
		mAdapter= new SearchNewsAdapter(getActivity(), mList, null);
		mPullListView.setAdapter(mAdapter);
		
		mHistoryList = new ArrayList<String>();
		mHistoryAdapter = new HistoryAdapter();
		mHistoryListView.setAdapter(mHistoryAdapter);
		
		mStrSearchHistory = SPHelper.getInst().getString(SPHelper.SEARCH_NEWS_HISTORY);
		if(!TextUtils.isEmpty(mStrSearchHistory)){
			String[] historyKeywords = mStrSearchHistory.split(",");
			mHistoryList.clear();
			for(String t : historyKeywords){  
				mHistoryList.add(t);  
			}  
			mHistoryAdapter.notifyDataSetChanged();
		}else{
			mSearchHistoryLayout.setVisibility(View.GONE);
		}

		dealHistory();
		
		getHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mEditText, 0);		
			}
		}, 300);
		
	}
	
	private void dealHistory(){
		if(Common.isListEmpty(mHistoryList)){
			mSearchHistoryLayout.setVisibility(View.GONE);
		}else{
			mHistoryAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void installListeners() {
		mBtnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditText.setText("");
				mSearchHistoryLayout.setVisibility(View.VISIBLE);
				dealHistory();
				mMultiStateView.setVisibility(View.GONE);
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener(){
			@Override
			public void onLastItemVisible() {
				if(mPage.hasNextPage()){
					mPullListView.setFootLoading();
					getDataByHttp();
				}
			}
		}); 
		mPullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				View tagView = view.findViewById(R.id.item_channel_item_content_layout);
				Object tag = tagView.getTag();
				if(tag instanceof MChannelItemBean){
					Common.handleTextViewReaded(getActivity(), view, R.id.item_channel_item_title_one_small, true);
					Common.handleTextViewReaded(getActivity(), view, R.id.item_channel_item_title_one_big_two_small, true);
					Common.handleTextViewReaded(getActivity(), view, R.id.item_channel_item_title_one_big, true);
					Common.handleTextViewReaded(getActivity(), view, R.id.item_channel_item_title_three_small, true);
					Common.handleTextViewReaded(getActivity(), view, R.id.item_channel_item_title_two_big_1, true);
					Common.handleTextViewReaded(getActivity(), view, R.id.item_channel_item_title_two_big_2, true);
					
					MChannelItemBean bean = (MChannelItemBean) tag;
					handleBeanClick(bean);
				}
			}
		});
		mMultiStateView.setRefreshOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getDataByHttpInit();
			}
		});
		mHistoryListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mStrSearchKeyWord = mHistoryList.get(position);
				mHistoryList.remove(position);
				mHistoryList.add(0, mStrSearchKeyWord);
				mEditText.setText(mStrSearchKeyWord);
				getDataByHttpInit();
			}
		});
		mSearchHistoryClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mHistoryList.clear();
				dealHistory();
			}
		});
		mEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			@Override
			public void afterTextChanged(Editable s) {
				String str = mEditText.getText().toString().trim();
				if(TextUtils.isEmpty(str)){
					if(mBtnClear.getVisibility() != View.GONE){
						mBtnClear.setVisibility(View.GONE);
					}
				}else{
					if(mBtnClear.getVisibility() != View.VISIBLE){
						mBtnClear.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		mEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH){  
					mStrSearchKeyWord = mEditText.getText().toString().trim();
					if(TextUtils.isEmpty(mStrSearchKeyWord)){
						return true;
					}
					
					int index = mHistoryList.indexOf(mStrSearchKeyWord);
					if(index != -1){
						mHistoryList.remove(index);
						mHistoryList.add(0, mStrSearchKeyWord);		
					}else{
						mHistoryList.add(0, mStrSearchKeyWord);
					}
						
					getDataByHttpInit();
		            return true;  
		        }
				return false;
			}
		});
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case DATA_SUCCESS:
			mList.clear();
			mList.addAll(mPage.getDataList());
			mPage.setMaxid(mList.get(0).getAid());
			mAdapter.notifyDataSetChanged();
			handleRefreshSuccess();
			mLoadStateManager.setState(LoadState.Success);
			break;
		default:
			break;
		}
	}
	
	private void getDataByHttpInit(){
		if(TextUtils.isEmpty(mStrSearchKeyWord)){
			return;
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
		
		mMultiStateView.setVisibility(View.VISIBLE);
		mSearchHistoryLayout.setVisibility(View.GONE);
		mCancelTv.setText("返回");
		mPage.setCurrentPage(0);
		mPage.setMaxid("");
		mPage.clear();
		getDataByHttp();
	}
	
	private void getDataByHttp(){
		if(mPage.isCurrentPageFirst()){
			mLoadStateManager.setState(LoadState.Init);
		}
		
		if(mPage.isRequestRunning()){
			return;
		}

		String keycode = mStrSearchKeyWord;
//		try {
//			keycode = URLEncoder.encode(mStrSearchKeyWord, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			UIHelper.showToast(getActivity(), "搜索字不合法");
//			return;
//		}
		
		String url = JUrl.URL_GET_SEARCH;
		url = mPage.isCurrentPageFirst() ? mPage.appendFirstPage(url) : mPage.appendNextPageAndMaxid(url);
		mPage.setRequestStart(url.toString());

		Map<String, String> params = new HashMap<>();
		params.put("keywords", keycode);
		Observable observable
				= App.getInst().getApiInterface().get(url, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {

				Gson gson = new Gson();
				SearchNewsListBean bean = gson.fromJson(data,SearchNewsListBean.class);
				if(mPage.isCurrentPageFirst()){
					mAdapter.setKeywords(handleKeyWords(bean.getKeywords()));
				}
				int currentPage = (null == bean ? 1 : mPage.getCurrentPage() + 1);
				mPage.setCurrentPage(currentPage);
				mPage.setPageData(currentPage, (null == bean ? null : bean.getList()));
				mPage.setMaxPage((null == bean ? 0 : bean.getMaxpage()));
				getHandler().obtainMessage(DATA_SUCCESS).sendToTarget();

			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				handleRefreshSuccess();
				if(status == -2){
					mLoadStateManager.setState(LoadState.NoData);
				}else{
					UIHelper.showToast(getActivity(), message);
					if(mPage.isCurrentPageFirst()){
						mLoadStateManager.setState(LoadState.Failure);
					}
				}
			}

			@Override
			public void onError(Throwable t) {
				handleRefreshSuccess();
				Common.handleHttpFailure(getActivity(), t);
				if(mPage.isCurrentPageFirst()){
					mLoadStateManager.setState(LoadState.Failure);
				}
				super.onError(t);
			}
		});

	}

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
	
	public void handleBeanClick(MChannelItemBean bean){
		if(bean == null){
			return;
		}
		LocalStateServer.getInst(getActivity()).setReadStateRead(LocalStateServer.PREFIX_CHANNEL_ITEM, bean.getAid());
		String area = "normal";
		Utils.handleBeanClick(getActivity(), bean, area);
	}
	
	private void handleRefreshSuccess() {
		mPullListView.setFootLoadFull();
		mPage.setRequestEnd();
	}
	
	private String[] handleKeyWords(ArrayList<String> list){
		if(Common.isListEmpty(list)){
			return new String[]{};
		}
		return list.toArray(new String[]{});
	}
	
	@Override
	public Activity getActivity() {
		return this;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		SPHelper.getInst().saveString(SPHelper.SEARCH_NEWS_HISTORY, listToString(mHistoryList));
	}
	
	private String listToString(List<String> list){
		if(list==null){
	      return "";
	   }
	   StringBuilder result = new StringBuilder();
	   boolean first = true;
	   int i = 0;
	   for(String string :list) {
	      if(first) {
	         first=false;
	      }else{
	         result.append(",");
	      }
	      result.append(string);
	      i++;
	      if(i >= MAX_HISTORY){
	    	  break;
	      }
	   }
	   return result.toString();
	}
	
	class HistoryAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(mHistoryList.size() > MAX_HISTORY)
				return MAX_HISTORY;
			return mHistoryList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.item_search_history, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.item_title);
			title.setText(mHistoryList.get(position));
			ImageView delete = (ImageView) convertView.findViewById(R.id.item_delete);
			delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mHistoryList.remove(position);
					dealHistory();
				}
			});
			return convertView;
		}
	}

}
