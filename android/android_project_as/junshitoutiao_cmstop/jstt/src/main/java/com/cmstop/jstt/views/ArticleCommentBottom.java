package com.cmstop.jstt.views;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengning.common.app.ActivityInfo.ActivityState;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.IBaseActivity;
import com.chengning.common.util.HttpUtil;
import com.chengning.common.util.SerializeUtil;
import com.chengning.common.util.ThreadHelper;
import com.loopj.android.http.RequestParams;
import com.cmstop.jstt.App;
import com.cmstop.jstt.MyStatusResponseHandler;
import com.cmstop.jstt.R;
import com.cmstop.jstt.activity.ArticleActivity;
import com.cmstop.jstt.activity.ArticleCommentActivity;
import com.cmstop.jstt.activity.VideoDetailActivity;
import com.cmstop.jstt.adapter.ArticleCommentItemAdapter;
import com.cmstop.jstt.beans.data.ArticlesBean;
import com.cmstop.jstt.beans.data.CommentListBean;
import com.cmstop.jstt.beans.data.MChannelItemBean;
import com.cmstop.jstt.data.access.LocalStateServer;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.utils.JUrl;
import com.cmstop.jstt.utils.UIHelper;
import com.cmstop.jstt.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ArticleCommentBottom implements Handler.Callback{
	
	protected static final int FAV_HANDLE_SUCCESS = 0;

	private Activity mContext;
	
	private LinearLayout mList;
	private View mNoData;
	private View mInput;
	private View mComment;
	private TextView mCommentCount;
	private View mFav;

	private boolean isFavState;

	private String aid;

	private ArticlesBean mArticleBean;

	private MChannelItemBean mChannelBean;

	private Handler mHandler;

	private HandlerThread mFavHandlerThread;

	private View mLayout;

	private View mCommentToolbar;

	private View mShare;
	
	public ArticleCommentBottom(Activity activity, final View root){
		this.mContext = activity;
		mLayout = root.findViewById(R.id.article_comment_bottom_layout);
		mCommentToolbar = root.findViewById(R.id.article_comment_bottom_toolbar);
		mList = (LinearLayout) root.findViewById(R.id.article_comment_bottom_list);
		mNoData = root.findViewById(R.id.article_comment_bottom_no_data);
		mInput = root.findViewById(R.id.article_comment_bottom_toolbar_input);
		mComment = root.findViewById(R.id.article_comment_bottom_toolbar_comment);
		mCommentCount = (TextView) root.findViewById(R.id.article_comment_bottom_toolbar_comment_count);
		mFav = root.findViewById(R.id.article_comment_bottom_toolbar_fav);
		mShare = root.findViewById(R.id.article_comment_bottom_toolbar_share);
		mHandler = new Handler(this);
		setVisibility(View.GONE);
	}

	public void setVisibility(int visiable) {
		if (mCommentToolbar == null) {
			return;
		}
		mCommentToolbar.setVisibility(visiable);
	}

	public void setChannelBean(final MChannelItemBean bean){
		if (bean == null) {
			return;
		}
		this.mChannelBean = bean;
		this.aid = bean.getAid();
		mInput.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mContext instanceof IBaseActivity &&((IBaseActivity)mContext).getActivityInfo().getActivityState() == ActivityState.SaveInstanceStated){
					return;
				}
//				if(CommentCheckUtil.checkLoginNotNeededOfComment(mContext)){
					CommentInputDialog dialog = new CommentInputDialog();
					dialog.setAid(aid);
					dialog.showAllowingStateLoss((BaseFragmentActivity)mContext, ((BaseFragmentActivity)mContext).getSupportFragmentManager(), CommentInputDialog.class.getSimpleName());
//				}
			}
		});
		mComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				handleEventAnalytics(mContext, "artilce_commentslist");
				ArticleCommentActivity.launch(mContext, aid);
			}
		});
		
		mFav.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isFavState) {
					cancleCollect();
				} else {
					handleEventAnalytics(mContext, "artilce_favorite");
					doCollect();
				}
			}
		});
		
		mShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				handleEventAnalyticsShareBtnClick(mContext);
				if (null != mArticleBean) {
					ArticleShareDialog dialog = new ArticleShareDialog();
					dialog.setBean(mArticleBean);
					dialog.showAllowingStateLoss((BaseFragmentActivity)mContext, ((FragmentActivity)mContext).getSupportFragmentManager(),
							PicArticleDialog.class.getSimpleName());
				}
			}
		});
	}
	
	public void setIsHasShare(boolean isHas){
		mShare.setVisibility(isHas ? View.VISIBLE : View.GONE);
	}
	
	/**
	 * 处理自定义事件统计
	 * @param mContext
	 * @param eventId
	 *
	 */
	protected void handleEventAnalytics(Activity mContext, String eventId) {
		MobclickAgent.onEvent(mContext, eventId);
	}

	/**
	 * 事件统计(右下角分享)
	 * @param mContext
	 */
	protected void handleEventAnalyticsShareBtnClick(Activity mContext) {
		Map<String, String> mHashMap = new HashMap<String, String>();
		mHashMap.put("type", "rightbottom");
		MobclickAgent.onEvent(mContext, "article_panel", mHashMap);
	}
	public void setData(ArticlesBean bean){
		this.mArticleBean = bean;
		mArticleBean.setTid(aid);
		CommentListBean cBean = bean.getComments();
		
		if(cBean != null && !Common.isListEmpty(cBean.getList())){
			mNoData.setVisibility(View.INVISIBLE);

			LayoutParams lp = mList.getLayoutParams();
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = LayoutParams.WRAP_CONTENT;
			mList.setLayoutParams(lp);
			
			mList.removeAllViews();
			ArticleCommentItemAdapter mAdapter = new ArticleCommentItemAdapter(mContext, cBean.getList(), aid);
			int size = mAdapter.getCount();
			for(int i = 0; i < size; i++){
				View v = mAdapter.getView(i, null, null);
				if(i == size - 1){
					v.findViewById(R.id.article_comment_item_line).setVisibility(View.INVISIBLE);
				}
				mList.addView(v);
			}

			if(size >= 5){
				View more = View.inflate(mContext, R.layout.article_comment_bottom_more, null);
				more.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ArticleCommentActivity.launch(mContext, aid);
					}
				});
				mList.addView(more);
			}
			
			int count = cBean.getCount();
			mCommentCount.setText(String.valueOf(count));
			mCommentCount.setVisibility(View.VISIBLE);
		}else{
			int height = mContext.getResources().getDimensionPixelSize(R.dimen.article_comment_empty_height);
			LayoutParams lp = mList.getLayoutParams();
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = height;
			mList.setLayoutParams(lp);
			
			mNoData.requestLayout();
			mNoData.invalidate();
			mNoData.setVisibility(View.VISIBLE);
			
			mCommentCount.setVisibility(View.GONE);
		}
		if (App.getInst().isLogin()) {
			setCollectState(Utils.isFavorite(bean.getIs_favor()));
		} else {
			setCollectState(LocalStateServer.getInst(mContext).isFavorite(
					LocalStateServer.PREFIX_CHANNEL_ITEM, aid));
		}
	}
	
	/**
     * 设置收藏状态
     * @param b
     */
	public void setCollectState(boolean b) {
		// 改变收藏图标
		mFav.setSelected(b);
		isFavState = b;
	}
	
	 /**
     * 取消收藏
     */
    private void cancleCollect(){
    	if(mArticleBean == null){
    		return;
    	}
    	if(App.getInst().isLogin()){
    		deleteFavoriteByHttp(mContext,mChannelBean.getAid());
    	}else{
    		cancleCollectByDB();
    	}
    	
    }
    
    /**
     * 未登录取消收藏
     */
    private void cancleCollectByDB() {
    	if (mFavHandlerThread == null) {
			mFavHandlerThread = ThreadHelper.creatThread("fav_handle");
		}
		ThreadHelper.handle(mFavHandlerThread, new Runnable() {
			
			@Override
			public void run() {
				LocalStateServer.getInst(mContext).deleteFavArticle(LocalStateServer.PREFIX_CHANNEL_ITEM,
						mChannelBean.getAid());
				mHandler.obtainMessage(FAV_HANDLE_SUCCESS, false).sendToTarget();
			}

		});
	}

	/**
     * 收藏
     */
    private void doCollect(){
    	if(mArticleBean == null){
    		return;
    	}
    	if(App.getInst().isLogin()){
    		doCollectByHttp(mContext,mChannelBean);
    	}else{
    		doCollectByDB();
			UIHelper.showToast(mContext,mContext.getResources().getString(R.string.article_collect_hint));
    	}
    }

    /**
     * 未登录收藏
     */
	private void doCollectByDB() {
		
		if (mFavHandlerThread == null) {
			mFavHandlerThread = ThreadHelper.creatThread("fav_handle");
		}
		
		ThreadHelper.handle(mFavHandlerThread, new Runnable() {
			
			@Override
			public void run() {
				LocalStateServer.getInst(mContext).saveFavArticle(LocalStateServer.PREFIX_CHANNEL_ITEM,
						mChannelBean.getAid(), SerializeUtil.serialize(mChannelBean));
				mHandler.obtainMessage(FAV_HANDLE_SUCCESS, true).sendToTarget();
			}

		});
		
	}
	/**
	 * 登录收藏
	 */
	protected void doCollectByHttp(final Activity context, final MChannelItemBean bean) {
		RequestParams params = new RequestParams();
		params.put("aid", bean.getAid());
		if(!Common.hasNet()){
			UIHelper.showToast(context, context.getResources().getString(R.string.intnet_fail));
		}
		HttpUtil.getClient().get(context, JUrl.SITE + JUrl.URL_COLLECT_FAVORITE_ATRICLES, params, new MyStatusResponseHandler() {
			
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				setCollectState(true);
				setFavChange(mContext, true);
				mArticleBean.setIs_favor("1");
				UIHelper.showToast(mContext,mContext.getResources().getString(R.string.article_collect_hint));
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.showToast(context, message);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse){
				Common.showHttpFailureToast(context);
			}  
		});
	}
	
	protected void setFavChange(Activity mContext, boolean b) {
		if (mContext instanceof ArticleActivity) {
			((ArticleActivity)mContext).setFavChange(b);
			
		} else if (mContext instanceof VideoDetailActivity) {
			((VideoDetailActivity)mContext).setFavChange(b);
			
		}
	}

	/**
	 * 登录删除收藏
	 */
	protected void deleteFavoriteByHttp(final Activity context, final String id) {
		RequestParams params = new RequestParams();
		params.put("aid", id);
		HttpUtil.getClient().get(JUrl.SITE + JUrl.URL_CANCEL_FAVORITE_ATRICLES, params, new MyStatusResponseHandler() {
			
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				setCollectState(false);
				setFavChange(mContext, true);
				mArticleBean.setIs_favor("0");
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.showToast(context, message);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Common.handleHttpFailure(context, throwable);
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case FAV_HANDLE_SUCCESS:
				setCollectState((Boolean)msg.obj);
				setFavChange(mContext, true);
				break;
			default:
				break;
		}
		return false;
	}
	
	public void destroy(){
		ThreadHelper.destory(mFavHandlerThread);
	}
	
	public void show(boolean isShowed) {
		mLayout.setVisibility(isShowed ? View.VISIBLE : View.GONE);
		mCommentToolbar.setVisibility(isShowed ? View.VISIBLE : View.GONE);
	}
}
