package com.shenyuan.militarynews.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.avos.avoscloud.AVInstallation;
import com.chengning.common.app.ActivityInfo.ActivityState;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.BaseResponseBean;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.base.MyRetrofitResponseCallback;
import com.chengning.common.base.util.RetrofitManager;
import com.chengning.common.util.DisplayUtil;
import com.chengning.common.util.SerializeUtil;
import com.chengning.common.util.StatusBarUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.Const.LikeAction;
import com.shenyuan.militarynews.Const.PointActionType;
import com.shenyuan.militarynews.LoadStateManager;
import com.shenyuan.militarynews.LoadStateManager.LoadState;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.beans.data.ArticlesBean;
import com.shenyuan.militarynews.beans.data.CommentItemBean;
import com.shenyuan.militarynews.beans.data.DirectoratePointBean;
import com.shenyuan.militarynews.beans.data.GoodBean;
import com.shenyuan.militarynews.beans.data.MChannelItemBean;
import com.shenyuan.militarynews.data.access.LocalStateServer;
import com.shenyuan.militarynews.utils.ArticleContentUtil;
import com.shenyuan.militarynews.utils.ArticleManagerUtils;
import com.shenyuan.militarynews.utils.ArticleManagerUtils.LikeState;
import com.shenyuan.militarynews.utils.ArticleVideoView;
import com.shenyuan.militarynews.utils.ArticleVideoView.FullScreenShowListener;
import com.shenyuan.militarynews.utils.ArticleWebView.OnWebViewListener;
import com.shenyuan.militarynews.utils.CommentCheckUtil;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.TaskUpdateUtil;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.utils.UmengShare;
import com.shenyuan.militarynews.views.ArticleCommentBottom;
import com.shenyuan.militarynews.views.ArticleRelativeBottom;
import com.shenyuan.militarynews.views.CommentInputDialog;
import com.shenyuan.militarynews.views.CommentReplyInputDialog;
import com.shenyuan.militarynews.views.NonFocusingScrollView;
import com.shenyuan.militarynews.views.PicArticleDialog;
import com.shenyuan.militarynews.views.ProgressRefreshView;
import com.shenyuan.militarynews.views.TitleBar;
import com.shenyuan.militarynews.views.TitleBar.BackOnClickListener;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

;

public class VideoDetailActivity extends BaseFragmentActivity {
	
	private static final String TAG = VideoDetailActivity.class.getSimpleName();
	
	private static final int MSG_DATA_READY = 1;
	
	private TitleBar mTitleBar;
	private View mCoverLayout;
	private ProgressRefreshView mProgressRefresh;
	private TextView mArticleTitle;
	private TextView mArticleTime;
	private TextView mLikeTextView;
	private RelativeLayout mArticleContentLayout;
	private ArticleCommentBottom mArticleComment;
	private ArticleRelativeBottom mArticleRelative;
	
	private ArticleVideoView mArticleVideoView;

	private boolean mIsFromPush;
	private MChannelItemBean mBean;  
	private String mArticleArea;
	private ArticlesBean mArticleBean;
	
	private Activity mContext;
	private LoadStateManager mLoadStateManager;

	private View mRoot;

	private App app;

	private boolean isFavChange = false;

	private boolean isFavState;
	private BroadcastReceiver mShareSuccessReceiver;
	private Runnable mShareSuccessRunnable;

	private ArticleManagerUtils mArticleManagerUtils;

	private LikeState mLikeState;

	private BroadcastReceiver mCollectReceiver;

	private RelativeLayout contentR;

	private NonFocusingScrollView mScrollView;

	private View mCmtToolbar;

	private int mScreenWidth;

	private ImageView mShareImg;
	
	public boolean isFavChange() {
		return isFavChange;
	}

	public void setFavChange(boolean isFavChange) {
		this.isFavChange = isFavChange;
	}

	public static void launch(Activity from, MChannelItemBean bean, String articleArea){
		Intent intent = new Intent(from, VideoDetailActivity.class);
		intent.putExtra("bean", bean);
		intent.putExtra("article_area", articleArea);
		from.startActivity(intent);
	}
	
	public static void launchByReqCode(Activity from, MChannelItemBean bean, int code){
		Intent intent = new Intent(from, VideoDetailActivity.class);
		intent.putExtra("bean", bean);
		intent.putExtra("article_area", "normal");
		from.startActivityForResult(intent, code);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_video_detail);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.black), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.black), false);
        }
		super.onCreate(savedInstanceState);
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putSerializable("bean", mBean);
    	savedInstanceState.putString("article_area", mArticleArea);
    	savedInstanceState.putBoolean("push", mIsFromPush);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    	mBean = (MChannelItemBean) savedInstanceState.getSerializable("bean");
    	mArticleArea  = savedInstanceState.getString("article_area");
		mIsFromPush = savedInstanceState.getBoolean("push", false);
    }

	@Override
	public BaseFragmentActivity getActivity() {
		return this;
	}

	@Override
	public void initViews() {
		// view
		DisplayUtil.getInst().init(getActivity());
		mRoot = findViewById(R.id.root);
		mTitleBar = new TitleBar(this, true);
		mTitleBar.showDefaultBack();
		mTitleBar.setBackgroundColor(getResources().getColor(R.color.transparent));
		mTitleBar.setBackText("", getResources().getColor(R.color.transparent), 
				getResources().getDrawable(R.drawable.nav_back_white));
		
		mProgressRefresh = new ProgressRefreshView(getActivity(), true);
		mCoverLayout = findViewById(R.id.cover_layout);
		mArticleTitle = (TextView)findViewById(R.id.article_content_top_title);
		mArticleTime = (TextView)findViewById(R.id.article_content_top_time);
		mLikeTextView = (TextView) findViewById(R.id.article_content_bottom_zan_text_view);
		mShareImg = (ImageView) findViewById(R.id.article_content_bottom_share_img);
		mArticleContentLayout = (RelativeLayout) findViewById(R.id.article_content_content_layout);
		mArticleComment = new ArticleCommentBottom(this, mRoot);
		mArticleRelative = new ArticleRelativeBottom(this, mRoot);
		
		mScrollView = (NonFocusingScrollView) findViewById(R.id.article_content_scroll);
		mCmtToolbar = findViewById(R.id.article_comment_bottom_toolbar);
		
	}

	@Override
	public void initDatas() {
		app = App.getInst();
		mContext = this;
		UmengShare.getInstance().checkInit(getActivity());
    	CommentCheckUtil.onActivityCreate();
		
		// data
		mBean = (MChannelItemBean) getIntent().getSerializableExtra("bean");
		mArticleArea = getIntent().getStringExtra("article_area");
		mIsFromPush = getIntent().getBooleanExtra("push", false);
		
		if(mIsFromPush){
			LocalStateServer.getInst(getActivity()).setReadStateRead(LocalStateServer.PREFIX_CHANNEL_ITEM, mBean.getAid());
		}
		
		initLoadState();
		
		if(Common.hasNet()){
			getDataByHttp();
			getClicksByHttp(String.valueOf(mBean.getAid()));
		}else{
			boolean isArticleSaved = false;
			String aStr = LocalStateServer.getInst(getActivity()).queryLocalStateNotNull(LocalStateServer.PREFIX_CHANNEL_ITEM, String.valueOf(mBean.getAid())).getData_item_article();
			if(!TextUtils.isEmpty(aStr)){
				try {
					ArticlesBean aBean = SerializeUtil.deSerialize(aStr, ArticlesBean.class);
					if(aBean != null && !TextUtils.isEmpty(aBean.getTid())){
						isArticleSaved = true;
		           		getHandler().obtainMessage(MSG_DATA_READY, aBean).sendToTarget();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(!isArticleSaved){
				getDataByHttp();
			}
		}
		initBroadcast();
		
		getHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				String area = mArticleArea;
				if(TextUtils.isEmpty(area)){
					area = "normal";
				}
				map.put("article_area", area);
//				map.put("article_id", String.valueOf(mBean.getAid()));
				MobclickAgent.onEvent(getActivity(), Const.UMEVENT_ARTICLE_CLICK, map);
			}
		}, 50);
		
		initLikeOrThread();
		mScreenWidth = DisplayUtil.getInst().getScreenWidth();
	}

	/**
	 * 初始化顶和踩
	 */
	private void initLikeOrThread() {
		mLikeState = new ArticleManagerUtils.LikeState(){

			@Override
			public void init() {
				mLikeTextView.setEnabled(false);
//				mTreadBtn.setEnabled(false);
			}

			@Override
			public void success(String data) {
//				mLikeBtn.setEnabled(true);
				mLikeTextView.setEnabled(true);
				Gson g = new Gson(); 
           	 	GoodBean b = g.fromJson(data,new TypeToken<GoodBean>(){}.getType()); 
				if(mArticleBean != null){
					mArticleBean.setGoodpost(b.goodpost);
					mArticleBean.setBadpost(b.badpost);
				}
				
				mLikeTextView.setText(String.valueOf(b.goodpost));
				mLikeTextView.setSelected(true);
//				mTreadTextView.setText(String.valueOf(b.badpost));
			}

			@Override
			public void failure() {
				mLikeTextView.setEnabled(true);
//				mTreadBtn.setEnabled(true);
			}
			
		};
		mArticleManagerUtils = new ArticleManagerUtils();
		mArticleManagerUtils.setState(mLikeState);
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
					mCoverLayout.setVisibility(View.VISIBLE);
					mProgressRefresh.setWaitVisibility(true);
					mProgressRefresh.setRefreshVisibility(false);
					mProgressRefresh.setNoDataTvVisibility(false);
					break;
				case Success:
					mCoverLayout.setVisibility(View.INVISIBLE);
					mProgressRefresh.setRootViewVisibility(false);
					break;
				case Failure:
					mCoverLayout.setVisibility(View.VISIBLE);
					mProgressRefresh.setWaitVisibility(false);
					mProgressRefresh.setRefreshVisibility(true);
					mProgressRefresh.setNoDataTvVisibility(false);
					break;
				default:
					break;
				}
			}
		});
		
		mLoadStateManager.setState(LoadState.Init);
	}

	private void getClicksByHttp(String aid) {

		HashMap params = new HashMap<String, String>();
		params.put("aid", aid);

		Observable<BaseResponseBean> observable
				= App.getInst().getApiInterface().get(JUrl.URL_GET_CLICKS, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {
			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {

			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {

			}
		});
		
	}

	@Override
	public void installListeners() {
		// listener
		mTitleBar.setBackOnClickListener(new BackOnClickListener() {
			
			@Override
			public void onClick() {
				if (!handleBackClick()) {
					finish();
				};
				
			}
		}, false);
		
		mProgressRefresh.setRefreshOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				getDataByHttp();
			}
		});
		
		mLikeTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mArticleBean == null || mArticleManagerUtils== null || mContext == null){
					return;
				}
				mArticleManagerUtils.doLikeByHttp(mContext, mArticleBean.getTid(), LikeAction.GOOD);
			}
		});
		mShareImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null != mArticleBean) {
					PicArticleDialog dialog = new PicArticleDialog();
					dialog.setBean(mArticleBean);
					dialog.showAllowingStateLoss(getActivity(), getSupportFragmentManager(),
							PicArticleDialog.class.getSimpleName());
				}
			}
		});
	}

	/**
	 * 处理返回点击
	 */
	protected boolean handleBackClick() {
		if(mArticleVideoView == null){
			return false;
		}
		if (!mArticleVideoView.onBackPressed()) {
			if (isFavChange()) {
				setResultOk();
			}
			if(mIsFromPush){
	        	HomeActivity.launch(mContext);
			}
			return false;
		}
		return true;
	}

	protected void setResultOk() {
		Intent intent = new Intent();
		intent.putExtra("favState", isFavState ? Common.TRUE : Common.FALSE);
		setResult(Activity.RESULT_OK, intent);
	}


	/**
     * 设置白天黑夜模式
     * @param isChecked 
     */
    private void setDayAndNightModel(boolean isChecked) {
    	app.setNightModelChange(true);
//    	app.setmNightModel(isChecked ? Common.TRUE : Common.FALSE);
    	SettingManager.getInst().saveNightModel(isChecked ? Common.TRUE : Common.FALSE);
    }
	
	@SuppressLint("NewApi")
	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case MSG_DATA_READY:
			// data ready
			mArticleBean = (ArticlesBean) msg.obj;

			updateChannelItemBean();
			
			mArticleTitle.setText(mArticleBean.getTitle());
			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
				// Call requires API level 11
				mArticleTitle.setTextIsSelectable(true);
			}
			mArticleTime.setText(Common.getDateMMDDHHMMNotNullWithYYMMDDHHMMSS(mArticleBean.getPubDate()));
//			mArticleAuthor.setText(mArticleBean.getAuthor());
//			mArticleClick.setText("阅读  " + mArticleBean.getClick());
			mArticleContentLayout.removeAllViews();
			
			// content
			if(mArticleVideoView != null){
				mArticleVideoView.destroy();
			}
			if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
				// Call requires API level 11
				mArticleVideoView = new ArticleVideoView();
				mArticleVideoView.setFullScreenListener(new FullScreenShowListener() {
					
					@Override
					public void showFullScreen() {
						setFullScreen(true);
					}
					
					@Override
					public void hideFullScreen() {
						setFullScreen(false);
					}
				});
				contentR = mArticleVideoView.build(this, mArticleBean, new OnWebViewListener() {

					@Override
					public void onPageStarted() {
						mLoadStateManager.setState(LoadState.Init);
					}
					
					@Override
					public void onPageFinished() {
						// webview 延迟显示避免闪屏
						getHandler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								mLoadStateManager.setState(LoadState.Success);
							}
						}, Common.isTrue(SettingManager.getInst().getNightModel()) ? 500 : 20);
					}
					
					@Override
					public void onTimeout() {
						onPageFinished();
					}
				}, getHandler());
//				contentR.setFocusable(false);
//				mArticleWebView.getWebView().setFocusable(false);
				mArticleContentLayout.addView(contentR);
			}else{
				View contentR = ArticleContentUtil.build(this, mArticleBean, mBean);
				contentR.setFocusable(false);
				mArticleContentLayout.addView(contentR);
				mLoadStateManager.setState(LoadState.Success);
			}
			mLikeTextView.setText(String.valueOf(mArticleBean.getGoodpost()));
			mLikeTextView.setSelected(Common.isTrue(mArticleBean.getIs_dig()));
			
			mArticleBean.setChannel(mBean.getChannel());
			mArticleComment.setData(mArticleBean);
			mArticleComment.setIsHasShare(true);
			mArticleComment.setVisibility(View.VISIBLE);
			mArticleRelative.setData(mArticleBean);
			mArticleRelative.setTitle("相关视频");
			break;
		default:
			break;
		}
	}

	/**
	 * 更新channelbean（避免收藏的时候缺少相关字段）
	 */
	private void updateChannelItemBean() {
		if (TextUtils.isEmpty(mBean.getAuthor())) {
			mBean.setAuthor(mArticleBean.getAuthor());
		}
		if (TextUtils.isEmpty(mBean.getPubDate())) {
			mBean.setPubDate(mArticleBean.getPubDate());
		}
		mArticleComment.setChannelBean(mBean);
		mArticleRelative.setChannelBean(mBean);

	}

	/**
	 * 设置全屏试图相关试图可见性
	 * @param isFullScreen
	 */
	protected void setFullScreen(boolean isFullScreen) {
		mScrollView.setVisibility(isFullScreen ? View.GONE :View.VISIBLE);
		mCmtToolbar.setVisibility(isFullScreen ? View.GONE :View.VISIBLE);
		if (isFullScreen) {
			WindowManager.LayoutParams attrs = getWindow().getAttributes();  
		    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;  
		    getWindow().setAttributes(attrs);  
		    getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);  
		    
			RelativeLayout.LayoutParams rlp = (LayoutParams) contentR.getLayoutParams();
			rlp.height = mScreenWidth;
			contentR.setLayoutParams(rlp);
		} else {
			WindowManager.LayoutParams attrs = getWindow().getAttributes();  
		    attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		    getWindow().setAttributes(attrs);  
		    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		    
			RelativeLayout.LayoutParams rlp = (LayoutParams) contentR.getLayoutParams();
			rlp.height = mScreenWidth*9/16;
			contentR.setLayoutParams(rlp);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (mShareSuccessRunnable != null) {
			mShareSuccessRunnable.run();
			mShareSuccessRunnable = null;
		}
		//登录后显示每日登录
		TaskUpdateUtil.LoginShowUpdate(getActivity());
		
		if (mArticleVideoView != null) {
			mArticleVideoView.onResume();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (mArticleVideoView != null) {
			mArticleVideoView.onPause();
		}
	}
	
	@Override
	public void onDestroy() {
		if(mArticleVideoView != null){
			mArticleContentLayout.removeAllViews();
			mArticleVideoView.destroy();
		}
		if (null != mShareSuccessReceiver) {
			unregisterReceiver(mShareSuccessReceiver);
		}
		if (null != mCollectReceiver) {
			unregisterReceiver(mCollectReceiver);
		}
		
		if (mArticleComment != null) {
			mArticleComment.destroy();
		}
		super.onDestroy();
	}
    
    private void handleHttpFailure(){
    	Common.showHttpFailureToast(mContext);
    	getHandler().post(new Runnable() {
			
			@Override
			public void run() {
				mLoadStateManager.setState(LoadState.Failure);
			}
		});
    }
    
    private void getDataByHttp(){
        final String aid = String.valueOf(mBean.getAid());
//		String url = JUrl.SITE + JUrl.URL_GET_ARTICLE_NEW + aid;
		String url = JUrl.URL_GET_ARTICLE_NEW + aid;
		url = JUrl.appendType(url, mBean.getChannel());

		Map<String, String> params = new HashMap<>();
		params.put("devicetoken", AVInstallation
				.getCurrentInstallation()
				.getInstallationId());
		Observable observable
				= App.getInst().getApiInterface().postArticleDetail(url, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {
			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean respons) {
				try {
					Gson gson = new Gson();
					ArticlesBean bean = gson.fromJson(data, ArticlesBean.class);
					bean.setTid(String.valueOf(mBean.getAid()));
					LocalStateServer.getInst(getActivity()).setArticle(LocalStateServer.PREFIX_CHANNEL_ITEM, aid,SerializeUtil.serialize(bean));
					getHandler().obtainMessage(MSG_DATA_READY, bean).sendToTarget();
				} catch (Exception e) {
					e.printStackTrace();

					handleHttpFailure();
				}
			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				UIHelper.showToast(mContext, message);
				mLoadStateManager.setState(LoadState.Failure);
			}

			@Override
			public void onError(Throwable t) {
				handleHttpFailure();
				super.onError(t);
			}
		});

    }

	@Override
	public void onBackPressedSupport() {

		if (!handleBackClick()) {
			super.onBackPressedSupport();
		};

	}

	@Override
    public void onStop(){
    	CommentCheckUtil.onActivityStop();
    	super.onStop();
    }

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch (arg0) {
		case ArticleContentUtil.REQUEST_PHOTO_PAGE_CODE:
			if (Activity.RESULT_OK == arg1) {
				
				isFavState = Common.isTrue(arg2.getIntExtra("favState", -1));
				mArticleComment.setCollectState(isFavState);
//				setFavChange(!isFavState);
				
			}
			break;
		case CommentCheckUtil.REQUEST_CODE_LOGIN_COMMENT:
			CommentCheckUtil.onActivityResult(arg0, arg1, arg2, getHandler(), new Runnable() {
				
				@Override
				public void run() {
					if(getActivityInfo().getActivityState() != ActivityState.Killed){
						CommentInputDialog dialog = new CommentInputDialog();
						dialog.setAid(mBean.getAid());
						dialog.showAllowingStateLoss(getActivity(), getSupportFragmentManager(),
								CommentInputDialog.class.getSimpleName());
					}
				}
			});
			break;
		case CommentCheckUtil.REQUEST_CODE_LOGIN_COMMENT_CHILD:
			try {
				final CommentItemBean bean = (CommentItemBean) arg2.getExtras().getSerializable("bean");
				CommentCheckUtil.onActivityResult(arg0, arg1, arg2, getHandler(), new Runnable() {
					
					@Override
					public void run() {
						if(getActivityInfo().getActivityState() != ActivityState.Killed){
							CommentReplyInputDialog dialog = new CommentReplyInputDialog();
							dialog.setData(bean);
							dialog.showAllowingStateLoss(getActivity(), getSupportFragmentManager(),
									CommentReplyInputDialog.class.getSimpleName());
						}
					
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		super.onActivityResult(arg0, arg1, arg2);
		UmengShare.getInstance().onActivityResult(arg0, arg1, arg2);
	}
	
	/**
	 * 初始化广播
	 */
	protected void initBroadcast(){
		mShareSuccessReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(Const.ACTION_WX_SHARE_SUCESS)) {
					final DirectoratePointBean data = (DirectoratePointBean) intent.getSerializableExtra("data");
					if (null != data) {
						mShareSuccessRunnable = new Runnable() {

							@Override
							public void run() {
								try {
									TaskUpdateUtil.showHints((FragmentActivity) getActivity(), data,PointActionType.SHARE);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						};
					}
				}
			}
		};
		IntentFilter intentFileter = new IntentFilter();
		intentFileter.addAction(Const.ACTION_WX_SHARE_SUCESS);
		intentFileter.setPriority(200);
		registerReceiver(mShareSuccessReceiver, intentFileter);
		
		mCollectReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(Const.ACTION_COLLECT_SUCESS)) {
					isFavState = intent.getBooleanExtra("data", false);
					setFavChange(true);
				}
			}
		};
		registerReceiver(mCollectReceiver, new IntentFilter(Const.ACTION_COLLECT_SUCESS));
		
	}
	
	
	
}
