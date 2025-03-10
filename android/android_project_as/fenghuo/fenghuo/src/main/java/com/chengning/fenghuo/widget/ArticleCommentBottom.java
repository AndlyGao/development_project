package com.chengning.fenghuo.widget;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.Consts;
import com.chengning.fenghuo.Consts.ArticleType;
import com.chengning.fenghuo.MyJsonHttpResponseHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.adapter.ArticleCommentItemAdapter;
import com.chengning.fenghuo.data.access.LocalStateDA;
import com.chengning.fenghuo.data.bean.ArticlesBean;
import com.chengning.fenghuo.data.bean.BaseArticlesBean;
import com.chengning.fenghuo.data.bean.CommentItemBean;
import com.chengning.fenghuo.data.bean.CommentListBean;
import com.chengning.fenghuo.util.ArticleManagerUtils;
import com.chengning.fenghuo.util.ArticleManagerUtils.CollectState;
import com.chengning.fenghuo.util.ArticleManagerUtils.DataStateListener;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.JUrl;
import com.chengning.fenghuo.util.UIHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ArticleCommentBottom {

	private final View loadingView;
	private final View all;
	private FragmentActivity mContext;
	
	private View mCommentLayout;
	private LinearLayout mList;
	private TextView mInput;
	private ImageButton mComment;
	private TextView mCommentCount;
	private TextView mAllCommentCount;
	private TextView mNoComment;
	private ImageButton mFav;

	private boolean isFavState;
	protected boolean isFavBtnClick = false;

	private String aid;

	private BaseArticlesBean mArticleBean;
	private ArticleManagerUtils mArticleManagerUtils;

	private BaseArticlesBean mChannelBean;

	private FavChangeListenner mListener;

	private View mShare;
	private String maxid;
	private int mCurPage = 1;
	private int tempPage = 1;
	private int totalPage = 1;
	private Runnable mCommentRunable;
	HashMap<BaseArticlesBean, View> map = new HashMap<BaseArticlesBean, View>();
	private DataStateListener mLikeState;

	public ArticleCommentBottom(FragmentActivity activity, final View root, FavChangeListenner listener){
		this.mContext = activity;
		this.mListener = listener;
		mCommentLayout = activity.findViewById(R.id.article_comment_bottom_layout);
		mList = (LinearLayout) mCommentLayout.findViewById(R.id.article_comment_bottom_list);
		mInput = (TextView) root.findViewById(R.id.article_comment_bottom_toolbar_input);
		mComment = (ImageButton) root.findViewById(R.id.article_comment_bottom_toolbar_comment);
		mCommentCount = (TextView) root.findViewById(R.id.article_comment_bottom_toolbar_comment_count);
		mAllCommentCount = (TextView) mCommentLayout.findViewById(R.id.article_comment_bottom_comment_tv);
		mNoComment = (TextView) mCommentLayout.findViewById(R.id.article_comment_bottom_no_data);
		mFav = (ImageButton) root.findViewById(R.id.article_comment_bottom_toolbar_fav);
		mShare = root.findViewById(R.id.article_comment_bottom_toolbar_share);
		loadingView = root.findViewById(R.id.footer_loading_ll);
		all = View.inflate(mContext, R.layout.article_comment_bottom_more, null);
		init();
	}
	
	private void init() {
		mCommentCount.setVisibility(View.INVISIBLE);
//		mInput.setText("写评论...");
//		mComment.setImageResource(R.drawable.zhengwen_toolbar_comment);
//		mFav.setImageResource(R.drawable.article_bottom_fav_state);
		
		CollectState collectState = new CollectState() {
			
			@Override
			public void setState(boolean state) {
				mFav.setSelected(state);
				isFavState = state;
				mArticleBean.setIs_favor(state ? 1 : 0);
				if (state && isFavBtnClick) {
					UIHelper.showToast(mContext, "文章" + mContext.getResources().getString(R.string.collect_hint));
				}
				mListener.setChange(state);
				
			}
		};
		
		mLikeState = new DataStateListener() {
			
			@Override
			public void success(Object... objects) {
					
				mFav.setEnabled(true);
			}
			
			@Override
			public void init() {
				mFav.setEnabled(false);	
			}
			
			@Override
			public void failure() {
				mFav.setEnabled(true);
			}
		};
		
		mArticleManagerUtils = new ArticleManagerUtils(collectState);
		map.clear();
	}

	public void setChannelBean(final BaseArticlesBean bean, final Consts.ArticleCommentType type){
		this.mChannelBean = bean;
		this.aid = bean.getTid();
		mInput.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if (null != mArticleBean && LoginManager.getInst().checkLoginWithNotice(mContext)) {
				try {
					if (null != mArticleBean) {
						CommentInputDialog dialog = new CommentInputDialog();
						dialog.setBean((BaseFragmentActivity)mContext, mArticleBean, type);
						dialog.show(((BaseFragmentActivity)mContext).getSupportFragmentManager(), CommentInputDialog.class.getSimpleName());

					}
				} catch (Exception ignored) {

				}

			}
		});
		mComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null != mArticleBean && mCommentRunable != null) {
					mCommentRunable.run();
				}
				
			}
		});
		
		mFav.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null != mArticleBean) {
					
					isFavBtnClick = true;
					if (isFavState) {
						mArticleManagerUtils.cancleCollect(mContext, mChannelBean);
					} else {
						mArticleManagerUtils.doCollect(mContext, mChannelBean, ArticleType.ARTICLE);	
					}
					
				}
				
			}
		});
		
		mShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (null != mArticleBean) {
						ArticleShareDialog dialog = new ArticleShareDialog();
						dialog.setBean(mArticleBean);
						dialog.show(((FragmentActivity) mContext).getSupportFragmentManager(),
								ArticleShareDialog.class.getSimpleName());
					}
				} catch (Exception ignored) {

				}
			}
		});
		
	}
	
	public void setData(BaseArticlesBean bean){
		this.mArticleBean = bean;
		if (null == mArticleBean) {
			return;
		}
		handleArticleBottomCmt(bean);
		
	}

	private void handleArticleBottomCmt(BaseArticlesBean bean) {
		int count = bean.getReplys();
		
		mAllCommentCount.setVisibility(View.VISIBLE);
		mAllCommentCount.setText("(" + count + ")");
		
//		if (App.getInst().isLogin()) {
//			setCollectState(Common.isTrue(bean.getIs_favor()));
//		} else {
//			setCollectState(LocalStateDA.getInst(mContext).isFavorite(LocalStateDA.PREFIX_CHANNEL_ITEM, aid));
//		}
		updateCollectState(bean);
		ArrayList<CommentItemBean> mReplyList = bean.getReply_list();
		if (mArticleBean instanceof ArticlesBean) {
			totalPage = ((ArticlesBean)mArticleBean).getTotal_page();
		}

		if(!Common.isListEmpty(mReplyList)){
			maxid = mReplyList.get(0).getTid();
			mList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			mList.setVisibility(View.VISIBLE);
			mNoComment.setVisibility(View.GONE);
			mList.removeAllViews();

			loadCommentView(mContext, mReplyList);

			mCommentCount.setText(String.valueOf(count));
			mCommentCount.setVisibility(View.VISIBLE);
			
		}else{
			int height = mContext.getResources().getDimensionPixelSize(R.dimen.article_comment_empty_height);
			mList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
			mList.setVisibility(View.GONE);
			mNoComment.setVisibility(View.VISIBLE);
			mCommentCount.setVisibility(View.GONE);
		}
	}

	private void loadCommentView(final Activity mContext, ArrayList<CommentItemBean> mReplyList) {
		final ArticleCommentItemAdapter mAdapter;
		mAdapter = new ArticleCommentItemAdapter(mContext, mReplyList);
		mAdapter.setCommentDeleteListener(new ArticleCommentItemAdapter.ArticleCommentDeleteListener() {

			public void deleteSuccess(BaseArticlesBean bean) {
				if (bean == null) {
					return;
				}

				View view = map.get(bean);
				if (view == null) {
					return;
				}
				mList.removeView(view);
//				mAdapter.getList().remove(Integer.valueOf(data));
				int count  = Common.strToInt(mCommentCount.getText().toString());
				if (count <= 1) {
					mCommentCount.setVisibility(View.GONE);
					mCommentCount.setText("0");
					mNoComment.setVisibility(View.VISIBLE);
				} else {
					mCommentCount.setVisibility(View.VISIBLE);
					mCommentCount.setText((count - 1) + "");
				}
				if (Common.isListEmpty(mAdapter.getList())) {
					mList.removeView(all);
				}
			}
		});
		int size = mAdapter.getCount();
		for(int i = 0; i < size; i++){
			final View itemView = mAdapter.getView(i, null, null);
//			itemView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					View view = itemView.findViewById(R.id.item_article_comment_top_user_image);
//					if (view == null) {
//						return;
//					}
//					BaseArticlesBean bean = (BaseArticlesBean) view.getTag();
//					ArticleReplyDetailActivity.launch(mContext, bean);
//				}
//			});
			map.put(mReplyList.get(i), itemView);
			mList.addView(itemView);
		}

		if(mCurPage == totalPage){
			mList.addView(all);
		}
	}

	/**
	 * 更新收藏状态
	 * @param bean
	 */
	public void updateCollectState(BaseArticlesBean bean){
		if (null == bean) {
			return;
		}
		if (App.getInst().isLogin()) {
			setCollectState(Common.isTrue(bean.getIs_favor()));
		} else {
			setCollectState(LocalStateDA.getInst(mContext).isFavorite(LocalStateDA.PREFIX_CHANNEL_ITEM, aid));
		}
	}

	public void setCollectState(boolean isFavState) {
//		if (type == ArticleType.ARTICLE) {
			mArticleManagerUtils.setCollectState(isFavState);
//		}
		
	}
	
	public void destory() {
		mArticleManagerUtils.destoryFavHandleThread();
	}
	
	public interface FavChangeListenner {
		void setChange(boolean isFaved);
	}
	
	public void setIsHasShare(boolean isHas){
		mShare.setVisibility(isHas ? View.VISIBLE : View.GONE);
	}

	public void getNextPageComment(final Activity context, final DataStateListener listener) {

		if (totalPage > mCurPage) {
			setLoading(true);
			listener.init();
			tempPage++;
			RequestParams params = new RequestParams();
			params.put("tid", mArticleBean.getTid());
			params.put(JUrl.KEY_PAGE, tempPage);
			params.put(JUrl.KEY_MAXID, maxid);
			HttpUtil.get(context, JUrl.SITE + JUrl.URL_GET_ARTICLE_COMMENT, params, new MyJsonHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					Common.handleHttpFailure(context, throwable);
					tempPage = mCurPage;
					listener.failure();
				}

				@Override
				public void onDataSuccess(int status, String mod, String message,
										  String data, JSONObject obj) {
					Gson gson = new Gson();
					CommentListBean bean = gson.fromJson(data, CommentListBean.class);
					if (bean != null && !Common.isListEmpty(bean.getList())) {
						mCurPage = tempPage;
						loadCommentView(context, bean.getList());
//						runnable.run();
						listener.success();
					}
				}

				@Override
				public void onDataFailure(int status, String mod, String message,
										  String data, JSONObject obj) {
					UIHelper.showToast(context, message);
					tempPage = mCurPage;
					listener.failure();
				}

				@Override
				public void onFinish() {
					setLoading(false);
					super.onFinish();
				}
			});
		}
	}

	public void setCommentClickListener(Runnable runnable){
		this.mCommentRunable = runnable;
	};

	private void setLoading(boolean isLoading) {
		loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
	}

}
