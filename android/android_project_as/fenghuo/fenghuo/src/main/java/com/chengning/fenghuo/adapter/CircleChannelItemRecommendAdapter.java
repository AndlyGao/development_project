package com.chengning.fenghuo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.BaseTypeListAdapter;
import com.chengning.common.base.BaseViewHolder;
import com.chengning.common.util.DisplayUtil;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.LoginManager;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.SettingManager;
import com.chengning.fenghuo.activity.AdDetailActivity;
import com.chengning.fenghuo.activity.DynamicDetailActivity;
import com.chengning.fenghuo.activity.PhotoPageCircleActivity;
import com.chengning.fenghuo.activity.UserInfoActivity;
import com.chengning.fenghuo.data.access.LocalStateDA;
import com.chengning.fenghuo.data.bean.BaseArticlesBean;
import com.chengning.fenghuo.data.bean.CommentItemBean;
import com.chengning.fenghuo.data.bean.DigBean;
import com.chengning.fenghuo.data.bean.DynamicItemBean;
import com.chengning.fenghuo.data.bean.Image;
import com.chengning.fenghuo.data.bean.UserInfoBean;
import com.chengning.fenghuo.util.ArticleCommentListeners;
import com.chengning.fenghuo.util.ArticleCommentListeners.CircleCommentHandleSuccessListener;
import com.chengning.fenghuo.util.ArticleCommentListeners.DynamicCommentOnClickListener;
import com.chengning.fenghuo.util.ArticleCommentListeners.DynamicCommentOnLongClickListener;
import com.chengning.fenghuo.util.ArticleManagerUtils;
import com.chengning.fenghuo.util.ArticleManagerUtils.DataStateListener;
import com.chengning.fenghuo.util.ArticleManagerUtils.DeleteListener;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.PoPupWindowUtils;
import com.chengning.fenghuo.util.PoPupWindowUtils.PopupLocation;
import com.chengning.fenghuo.util.UIHelper;
import com.chengning.fenghuo.util.Utils;
import com.chengning.fenghuo.widget.CircleChangeBgDialog;
import com.chengning.fenghuo.widget.CommentInputDialog;
import com.chengning.fenghuo.widget.DynamicCommentInputDialog;
import com.chengning.fenghuo.widget.DynamicCommentInputDialog.DialogCommentListener;
import com.chengning.fenghuo.widget.DynamicTextView;
import com.chengning.fenghuo.widget.FlowLayout;
import com.chengning.fenghuo.widget.FlowLayoutSingleLine;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CircleChannelItemRecommendAdapter extends BaseTypeListAdapter {
	private DisplayImageOptions mOptions;
	private Activity mContext;

	private int mImageThreeSmallWidth;
	private int mImageThreeSmallHeight;
	
	private int mImageTwoSmallWidth;
	private int mImageTwoSmallHeight;
	
	private int mImageOneSmallWidth;
	private int mImageOneSmallHeight;
	
	private List<ImageView> mImageViewList;
	private boolean isShowDel;
	private boolean isFavDel = false;
	private TextView tv;
	private ArticleManagerUtils mArticleManagerUtils;
	private static SparseBooleanArray isSelected;
	
	private HashMap<DynamicItemBean, ItemState> mStates = new HashMap<DynamicItemBean, ItemState>();
	private HashMap<DynamicItemBean, DuanPingItemState> mDuanStates = new HashMap<DynamicItemBean, DuanPingItemState>();
	private CircleCommentHandleSuccessListener mCircleCommentHandleSuccess;

	public CircleChannelItemRecommendAdapter(Activity activity, List list, boolean isShowDel, boolean isShowDelFav) {
		super(activity, list);
		this.mContext = activity;
		this.isShowDel = false;
		this.isFavDel = isShowDelFav;
		mImageViewList = new ArrayList<ImageView>();
		mArticleManagerUtils = new ArticleManagerUtils();
		mOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.loading)
				.showImageForEmptyUri(R.drawable.loading)
				.showImageOnFail(R.drawable.loading)
				.resetViewBeforeLoading(true)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
//				.displayer(new MyRoundedBitmapDisplayer(App.getInst().getResources().
//						getDimensionPixelSize(R.dimen.common_round_corner)))
				.build();

		DisplayUtil.getInst().init(activity);
		
		int gapWidth = activity.getResources().getDimensionPixelSize(R.dimen.common_horizontal_margin);
		int leftMargin = activity.getResources().getDimensionPixelSize(R.dimen.dynamic_list_img_left_margin);
		mImageThreeSmallWidth = (DisplayUtil.getInst().getScreenWidth() - leftMargin - gapWidth * 4) / 3;
		mImageThreeSmallHeight = activity.getResources().getDimensionPixelSize(R.dimen.channel_item_image_three_height);
		
		mImageTwoSmallWidth = (DisplayUtil.getInst().getScreenWidth() - leftMargin - gapWidth * 3) / 2;
		mImageTwoSmallHeight = activity.getResources().getDimensionPixelSize(R.dimen.channel_item_image_two_height);
		
		mImageOneSmallWidth = (DisplayUtil.getInst().getScreenWidth() - leftMargin - gapWidth * 2) * 2 / 3;
		mImageOneSmallHeight = 0;
		
	}

	public CircleChannelItemRecommendAdapter(Activity activity, List list, boolean isShowDel, TextView tv) {
		super(activity, list);
		this.mContext = activity;
		this.isShowDel = false;
		mImageViewList = new ArrayList<ImageView>();
		mArticleManagerUtils = new ArticleManagerUtils();
		mOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.loading)
				.showImageForEmptyUri(R.drawable.loading)
				.showImageOnFail(R.drawable.loading)
				.resetViewBeforeLoading(true)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
//				.displayer(new MyRoundedBitmapDisplayer(App.getInst().getResources()
//						.getDimensionPixelSize(R.dimen.common_round_corner)))
				.build();

		DisplayUtil.getInst().init(activity);
		mImageThreeSmallWidth = DisplayUtil.getInst().getScreenWidth() / 2;
		this.tv = tv;
		
		int gapWidth = activity.getResources().getDimensionPixelSize(R.dimen.common_horizontal_margin);
		
		int leftMargin = activity.getResources().getDimensionPixelSize(R.dimen.dynamic_list_img_left_margin);
		mImageThreeSmallWidth = (DisplayUtil.getInst().getScreenWidth() - leftMargin - gapWidth * 4) / 3;
		mImageThreeSmallHeight = activity.getResources().getDimensionPixelSize(R.dimen.channel_item_image_three_height);
		
		mImageTwoSmallWidth = (DisplayUtil.getInst().getScreenWidth() - leftMargin - gapWidth * 4) * 2 / 3;
		mImageTwoSmallHeight = 0;
		
	}
	
	/**
	 * 处理 赞和评论
	 * @param bean
	 * @param zanOrCmtLayout
	 * @param zanFlow
	 * @param zanMoreFlow
	 * @param cmtLayoutLine
	 * @param cmtLayout
	 * @param zanMore
	 * @param zanLayout
	 * @param zan_tv
	 * @param cmtMoreLine 
	 */
	private void handleZanUpdateView(final int position, final DynamicItemBean bean, View zanOrCmtLayout,
			final FlowLayoutSingleLine zanFlow, final FlowLayout zanMoreFlow,
			View cmtLayoutLine, LinearLayout cmtLayout, final ImageView zanMore, 
			final View zanLayout, TextView zan_tv, View cmtMoreLine, final TextView cmtMore) {
		zan_tv.setText(bean.getDigcounts() + "");
		boolean isHasZan = !Common.isListEmpty(bean.getDig_list());
		boolean isHasCmt = !Common.isListEmpty(bean.getReply_list());
		if (!isHasZan && !isHasCmt) {
			zanOrCmtLayout.setVisibility(View.GONE);
		} else {
			zanOrCmtLayout.setVisibility(View.VISIBLE);
			zanFlow.removeAllViews();
			zanMoreFlow.removeAllViews();
			int margin = (int) mContext.getResources().getDimension(R.dimen.flow_textview_margin);
			addDataToZan(zanFlow, bean.getDig_list(), margin);
			addDataToZan(zanMoreFlow, bean.getDig_list(), margin);
			
			updateViewMoreState(getItemState(bean), isHasZan);
			updateViewMore(getItemState(bean), zanMore, zanLayout, zanFlow, zanMoreFlow);
			zanMore.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ItemState iState = getItemState(bean);
					switch (iState.moreState) {
					case None:
						
						break;
					case Expand:
						iState.moreState = MoreState.Pack;
						updateViewMore(getItemState(bean), zanMore,zanLayout,zanFlow,zanMoreFlow);
						break;
					case Pack:
						iState.moreState = MoreState.Expand;
						updateViewMore(getItemState(bean), zanMore,zanLayout,zanFlow,zanMoreFlow);
						break;
				
					}
				}
			});
			
			loadCmtData(position,isHasZan, isHasCmt, cmtLayoutLine, cmtLayout, bean,cmtMoreLine, cmtMore);
			
		}
	}

	/**
	 * 显示评论和赞pop
	 * @param mContext 
	 * @param position 
	 * @param zanFlow
	 */
	protected void showCmtPopupwindow(final Activity mContext, final View view, final int position, final FlowLayoutSingleLine zanFlow,
			final View zanOrCmtLayout, final FlowLayout zanMoreFlow, 
			final View cmtLayoutLine, final LinearLayout cmtLayout, 
			final ImageView zanMore, final View zanLayout, final TextView zan_tv,final View cmtMoreLine, final TextView cmtMore) {
		
		if (!LoginManager.getInst().checkLoginWithLoginDialog((FragmentActivity)mContext)) {
			return;
		}
		
		final DynamicItemBean bean = (DynamicItemBean)getList().get(position);
		final PoPupWindowUtils popUtils = new PoPupWindowUtils();
		final boolean isDig = Common.isTrue(bean.getIs_dig());
		popUtils.setLeftText(isDig ? "取消" : "赞");
		popUtils.setOnLeftClick(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final View left = popUtils.getLeftView();
				
				
				final ArrayList<DigBean> digList = bean.getDig_list();
				DataStateListener mState = new DataStateListener() {
					@Override
					public void success(Object... objects) {
						left.setEnabled(true);
						bean.setIs_dig(isDig ? 0 : 1);
						int digCount = bean.getDigcounts();
						if (isDig) {
							if (digCount > 0) {
								bean.setDigcounts(digCount-1);
							}
							
						} else {
							bean.setDigcounts(digCount+1);
						}
						if (digList != null) {
							if (!isDig) {
								DigBean bean = new DigBean();
								bean.setNickname(App.getInst().getUserInfoBean().getNickname());
								bean.setUid(App.getInst().getUserInfoBean().getUid());
								digList.add(0,bean);
							} else {
								for (DigBean bean : digList) {
									if (bean.getUid().equals(App.getInst().getUserInfoBean().getUid())) {
										digList.remove(bean);
										break;
									}
								}
							}
							bean.setDig_list(digList);
						}
//						notifyDataSetChanged();
						handleZanUpdateView(position, bean,zanOrCmtLayout,zanFlow,
								zanMoreFlow,cmtLayoutLine,cmtLayout,zanMore,zanLayout,zan_tv, cmtMoreLine, cmtMore);
						if (mCircleCommentHandleSuccess != null) {
							mCircleCommentHandleSuccess.digSuccess(bean, position);
						}
					
					}

					@Override
					public void init() {
						left.setEnabled(false); 
					}

					@Override
					public void failure() {
						left.setEnabled(true);
					}
				};
				if (!isDig) {
					new ArticleManagerUtils().doLike(mContext, bean, mState);
				} else {
					new ArticleManagerUtils().cancleLike(mContext, bean, mState);
				}
				popUtils.dismiss();
				
			}
		});
		popUtils.setOnRightClick(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DynamicCommentInputDialog dialog = new DynamicCommentInputDialog();
				dialog.setCommentCallback(new DialogCommentListener() {
					@Override
					public void onCommentFail() {
					}
					@Override
					public void onCommentSuccess(CommentItemBean iBean) {
						bean.getReply_list().add(iBean);
						bean.setReplys(bean.getReplys() + 1);
//						notifyDataSetChanged();
						handleZanUpdateView(position, bean,zanOrCmtLayout,zanFlow,
								zanMoreFlow,cmtLayoutLine,cmtLayout,zanMore,zanLayout,zan_tv, cmtMoreLine, cmtMore);
						if (mCircleCommentHandleSuccess != null) {
							mCircleCommentHandleSuccess.commentSuccess(bean, position);
						}
					}
				});
				dialog.setBean(bean);
				dialog.show(((BaseFragmentActivity)mContext).getSupportFragmentManager(), CommentInputDialog.class.getSimpleName());
				popUtils.dismiss();
			}
		});
		popUtils.showPopupWindow(mContext, view, R.layout.popupwindow_circle_list_comment, PopupLocation.LEFT);
	}

	/**
	 * 加载评论数据
	 * @param isHasCmt
	 * @param cmtLayout
	 * @param bean
	 * @param cmtMoreLine 
	 */
	private void loadCmtData(final int position, final boolean isHasZan, final boolean isHasCmt,
			final View cmtLayoutLine, final LinearLayout cmtLayout, final DynamicItemBean bean, final View cmtMoreLine, final TextView cmtMore) {
		cmtLayoutLine.setVisibility(!isHasZan|| !isHasCmt ? View.GONE :  View.VISIBLE);
		cmtLayout.setVisibility(!isHasCmt ? View.GONE :  View.VISIBLE);
		
		if (!isHasCmt || bean == null) {
			cmtMore.setVisibility(View.GONE);
			cmtMoreLine.setVisibility(View.GONE);
			return;
		}
		ArrayList<CommentItemBean> mReplyList = bean.getReply_list();
		
		cmtLayout.removeAllViews();
		DynamicCommentItemParentAdapter mAdapter;
		mAdapter = new DynamicCommentItemParentAdapter(mContext, mReplyList);
		int size = mAdapter.getCount();
		for(int i = 0; i < size; i++){
			final int cmtPos  = i;
			View view = mAdapter.getView(i, null, null);
			View v = view.findViewById(R.id.item_dynamic_comment_item_content);
			BaseArticlesBean cmtBean = (BaseArticlesBean) v.getTag();
			v.setOnClickListener(new DynamicCommentOnClickListener(mContext,new DeleteListener() {
				
				@Override
				public void deleteSuccess(String data) {
					ArrayList<CommentItemBean> list = bean.getReply_list();
					list.remove(cmtPos);
					bean.setReply_list(list);
					int replyCount = bean.getReplys();
					if (replyCount > 1) {
						replyCount -= 1;
						bean.setReplys(replyCount);
					}
//					notifyDataSetChanged();
					loadCmtData(position,isHasZan, isHasCmt, cmtLayoutLine, cmtLayout, bean,cmtMoreLine, cmtMore);
					if (mCircleCommentHandleSuccess != null) {
						mCircleCommentHandleSuccess.digSuccess(bean, position);
					}
				}
			},
			new DialogCommentListener() {
				@Override
				public void onCommentFail() {
				}
				@Override
				public void onCommentSuccess(CommentItemBean iBean) {
					bean.getReply_list().add(iBean);
					bean.setReplys(bean.getReplys() + 1);
//					notifyDataSetChanged();
					loadCmtData(position,isHasZan, isHasCmt, cmtLayoutLine, cmtLayout, bean,cmtMoreLine, cmtMore);
					if (mCircleCommentHandleSuccess != null) {
						mCircleCommentHandleSuccess.digSuccess(bean, position);
					}
				}
			}));
			v.setOnLongClickListener(new DynamicCommentOnLongClickListener(mContext,new DeleteListener() {
				
				@Override
				public void deleteSuccess(String data) {
					ArrayList<CommentItemBean> list = bean.getReply_list();
					list.remove(cmtPos);
					bean.setReply_list(list);
					int replyCount = bean.getReplys();
					if (replyCount >= 1) {
						replyCount -= 1;
						bean.setReplys(replyCount);
					}
					boolean isHasCmt = !Common.isListEmpty(bean.getReply_list());
					loadCmtData(position,isHasZan, isHasCmt, cmtLayoutLine, cmtLayout, bean,cmtMoreLine, cmtMore);
//					notifyDataSetChanged();
					if (mCircleCommentHandleSuccess != null) {
						mCircleCommentHandleSuccess.digSuccess(bean, position);
					}
				}
			}, Utils.handleDynamicContentConvert(cmtBean)));
			cmtLayout.addView(view);
		}

		if(bean.getReplys() > size){
			cmtMore.setVisibility(View.VISIBLE);
			cmtMoreLine.setVisibility(View.VISIBLE);
			cmtMore.setText("查看所有评论(" + bean.getReplys() + ")");
			cmtMore.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					DynamicDetailActivity.launch(mContext, bean);
				}
			});
		} else {
			cmtMore.setVisibility(View.GONE);
			cmtMoreLine.setVisibility(View.GONE);
		}
	}

	/**
	 * 添加赞数据
	 * @param zanFlow
	 * @param list
	 * @param margin
	 */
	private void addDataToZan(View zanFlow, ArrayList<DigBean> list, int margin) {
		if (Common.isListEmpty(list)) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.textview_circle_list_zan_name, null);
			builder.setLength(0);
			builder.append(list.get(i).getNickname());
			if (i != size - 1) {
				builder.append(",");
			}
			tv.setText(builder.toString());
			tv.setTag(list.get(i));
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					DigBean bean = (DigBean) v.getTag();
					UserInfoActivity.launchByUid(mContext, bean.getUid());
				}
			});
			MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, margin, 0);
			if (zanFlow instanceof FlowLayoutSingleLine) {
				((FlowLayoutSingleLine)zanFlow).addView(tv,lp);
			} else if (zanFlow instanceof FlowLayout) {
				((FlowLayout)zanFlow).addView(tv,lp);
			}
			
		}
	}

	/**
	 * 点击详情
	 * @param bean
	 */
	private void handleDetailClick(DynamicItemBean bean) {
		if(bean == null){
			return;
		}
		LocalStateDA.getInst(getContext()).setReadStateRead(LocalStateDA.PREFIX_CHANNEL_ITEM, bean.getTid());
		DynamicDetailActivity.launch(getContext(), bean);
	}

	private void updateViewMore(ItemState itemState, ImageView zanMore,
			View zanLayout, FlowLayoutSingleLine zanSingleFlow,FlowLayout zanMoreFlow) {
		switch (itemState.moreState) {
		case None:
			zanLayout.setVisibility(View.GONE);
			zanMoreFlow.setVisibility(View.GONE);
			break;
		case Pack:
			zanLayout.setVisibility(View.VISIBLE);
			zanMoreFlow.setVisibility(View.VISIBLE);
			zanSingleFlow.setVisibility(View.GONE);
			zanMore.setImageResource(itemState.moreState.drawableRightId);
			break;
		case Expand:
			zanLayout.setVisibility(View.VISIBLE);
			zanMoreFlow.setVisibility(View.GONE);
			zanSingleFlow.setVisibility(View.VISIBLE);
			zanMore.setImageResource(itemState.moreState.drawableRightId);
			break;
		default:
			break;
		}
		
	}
	
	public class ItemState{
		private boolean isFirst = true;
		public MoreState moreState = MoreState.None;
	}
	
	public enum MoreState{
		None(0),
		/** 查看 */
		Expand(R.drawable.xiala),
		/** 收起 */
		Pack(R.drawable.xiala2)
		;
		
		private int drawableRightId; 
		
		MoreState(int id){
			this.drawableRightId = id;
		}
	}
	
	public ItemState getItemState(DynamicItemBean bean){
		if(mStates.containsKey(bean)){
			return mStates.get(bean);
		}else{
			ItemState state = new ItemState();
			mStates.put(bean, state);
			return state;
		}
	}
	
	private void updateViewDuanPing(DuanPingItemState itemState, TextView duanpingState,
			DynamicTextView content, int lines) {
	
		switch (itemState.moreState) {
		case None:
			duanpingState.setVisibility(View.GONE);
			break;
		case Pack:
			duanpingState.setVisibility(View.VISIBLE);
			duanpingState.setText(itemState.moreState.str);
			content.setMaxLines(lines);
			break;
		case Expand:
			duanpingState.setVisibility(View.VISIBLE);
			duanpingState.setText(itemState.moreState.str);
			content.setMaxLines(6);
			break;
		default:
			break;
		}
		
	}

	public void updateViewMoreState(ItemState state, boolean isHasZan){
		
		if(state.isFirst){
			state.isFirst = false;
			if(!isHasZan){
				state.moreState = MoreState.None;
			}else {
				state.moreState = MoreState.Expand;
			}
		}
		
		if (isHasZan && state.moreState == MoreState.None ) {
			state.moreState = MoreState.Expand;
		} else if (!isHasZan) {
			state.moreState = MoreState.None;
		}
			
		
	}
	public void updateViewDuanPingState(DuanPingItemState state, boolean isHasZan){
		
		if(state.isFirst){
			state.isFirst = false;
			if(!isHasZan){
				state.moreState = DuanPingState.None;
			}else {
				state.moreState = DuanPingState.Expand;
			}
		}
			
		if (isHasZan && state.moreState == DuanPingState.None ) {
			state.moreState = DuanPingState.Expand;
		} else if (!isHasZan) {
			state.moreState = DuanPingState.None;
		}
	}
	
	public class DuanPingItemState{
		private boolean isFirst = true;
		public DuanPingState moreState = DuanPingState.None;
	}
	
	public enum DuanPingState{
		None(""),
		/** 查看 */
		Expand("全文"),
		/** 收起 */
		Pack("收起")
		;
		
		private String str;
	
		private DuanPingState(String str) {
			this.str = str;
		} 
		
	}
	
	public DuanPingItemState getDuanPingItemState(DynamicItemBean bean){
		if(mDuanStates.containsKey(bean)){
			return mDuanStates.get(bean);
		}else{
			DuanPingItemState state = new DuanPingItemState();
			mDuanStates.put(bean, state);
			return state;
		}
	}
	
	
	private void updateSize(View v, int width, int height) {
		LayoutParams lp = v.getLayoutParams();
		lp.width = width;
		lp.height = height;
		v.setLayoutParams(lp);
	}
	
	private void updateSizeForOneBig(View v, int w, int h){
		float min = 45.0f;
		float max = 200.25f;
		float miniHWScale = min/max;
		float curH = 0;
		float curW = 0;
		
		if( w > h ){
			float scale = h/(w * 1.0f);
			if(scale < miniHWScale){
				curH = min;
				curW = curH/h * w;
			}else{
				curW = max;
				curH = curW/w * h;
			}
		}else if( w < h ){
			float scale = w/(h * 1.0f);
			if(scale < miniHWScale){
				curW = min;
				curH = curW/w * h;
			}else{
				curH = max;
				curW = curH/h * w;
			}
		}else{
			curW = max;
			curH = max;
		}
		if(curW > max) curW = max;
		if(curH > max) curH = max;
		

		curW = DisplayUtil.getInst().dip2px(curW);
		curH = DisplayUtil.getInst().dip2px(curH);
		updateSize(v, (int)curW, (int)curH);
	}

	public void setDelFavFlag(boolean isDelete) {
		isFavDel = isDelete;
	}

	@Override
	public void notifyDataSetChanged() {
		if (isFavDel) {
			initData(getList());
		}
		super.notifyDataSetChanged();
		
	}
	private void initData(List list) {
		if (!Common.isListEmpty(list)) {
			int size = list.size();
			isSelected = new SparseBooleanArray(size);
			for (int i = 0; i < size; i++) {
				isSelected.put(i, false);
			}
		}
	}

	public static SparseBooleanArray getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(SparseBooleanArray isSelected) {
		CircleChannelItemRecommendAdapter.isSelected = isSelected;
	}

	public static void setAllSelected(boolean isChecked) {
		if (isSelected != null) {
			for (int i = 0; i < isSelected.size(); i++) {
				isSelected.put(i, isChecked);
			}
		}
	}
	
	public static void clearList(){
		isSelected.clear();
	}
	
	private void setImageViewGone(){
		for (ImageView o : mImageViewList) {
			o.setVisibility(View.GONE);
		}
	}
	public void setCircleCommentHandleCallback(CircleCommentHandleSuccessListener r){
		this.mCircleCommentHandleSuccess = r;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return ChannelItemType.CommonItemWithHeader.type;
		}
		return ChannelItemType.CommonItem.type;
	}

	@Override
	public int getViewTypeCount() {
		return ChannelItemType.values().length;
	}

	@Override
	public int buildLayoutId(int position, int type) {
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public View buildLayoutView(int position,int type){
		ChannelItemType itemType = ChannelItemType.parseType(type);
		View view = View.inflate(getContext(), R.layout.item_channel_item_container, null);
		ViewStub stub = (ViewStub) view.findViewById(R.id.item_channel_item_stub);
		switch(itemType){
			 default:
			 case CommonItemWithHeader:
				 stub.setLayoutResource(R.layout.item_circlechannel_item_with_header);
				 break;
			 case CommonItem:
				 stub.setLayoutResource(R.layout.item_circlechannel_item);
				 break;
		}
		stub.inflate();
		return view;
	}

	private CircleChangeBgDialog mChangeBgDialog;
	private ImageView iv_circlebg;
	public ImageView getCircleBg(){
		return iv_circlebg;
	}
	
	private ImageView iv_circlehead;
	public ImageView getCircleHead(){
		return iv_circlehead;
	}
	@Override
	public void handleLayout(View convertView, final int position, Object obj,
			int type) {
		final ChannelItemType itemType = ChannelItemType.parseType(type);
		
		switch (itemType) {
		case CommonItemWithHeader:
			LinearLayout header = (LinearLayout) BaseViewHolder.get(convertView, R.id.item_circlechannel_header);
			View mHeadLayout = header.findViewById(R.id.home_circle_head_layout);
			ImageView mAvatarImgBg = (ImageView) header.findViewById(R.id.home_circle_head_bg);
			ImageView mAvatarImg= (ImageView) header.findViewById(R.id.home_circle_head_avatar);
			TextView mNicknameTv = (TextView) header.findViewById(R.id.home_circle_head_name);
			iv_circlebg = mAvatarImgBg;
			iv_circlehead = mAvatarImg;
			mAvatarImg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (LoginManager.getInst().checkLoginWithLoginDialog((FragmentActivity)mContext)) {
						UserInfoActivity.launch(mContext, App.getInst().getUserInfoBean().getNickname());
					} 
					
				}
			});
			
			mHeadLayout.setOnClickListener(new OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					if(LoginManager.getInst().isLogin()){
						if(mChangeBgDialog == null){
							mChangeBgDialog = new CircleChangeBgDialog();
						}
						mChangeBgDialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), CircleChangeBgDialog.class.getSimpleName());
					}
				}
			});
			
			if (App.getInst().isLogin()) {
				mNicknameTv.setVisibility(View.VISIBLE);
				UserInfoBean infoBean = App.getInst().getUserInfoBean();
				mNicknameTv.setText(infoBean.getNickname());
				Utils.setCircleImage(infoBean.getFace(), mAvatarImg);
//				ImageLoader.getInstance().displayImage(infoBean.getFace(), mAvatarImg);
				ImageLoader.getInstance().displayImage(infoBean.getProfile_image(), mAvatarImgBg);
			} else {
				mNicknameTv.setVisibility(View.GONE);
//				Utils.setCircleImage(R.drawable.home_circle_default_avatar, mAvatarImg);
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.home_circle_default_avatar, mAvatarImg);
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.home_circle_head_bg, mAvatarImgBg);
			}
			break;
		case CommonItem:
			
			break;

		default:
			break;
		}
		
		final ImageView head = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_head);
		TextView name = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_name);
		TextView time = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_time);
//		TextView from = BaseViewHolder.get(convertView,
//				R.id.item_circlechannel_from);
		TextView title = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_title);
		final DynamicTextView content = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_content);
		final DynamicTextView contentDetail = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_content_detail);
		View view1  = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_image_ll1);
		final ImageView image1 = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_image1);
		ImageView image2 = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_image2);
		ImageView image3 = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_image3);
		View view2 = BaseViewHolder.get(convertView,R.id.item_circlechannel_image_ll2);
		ImageView image4 = (ImageView) BaseViewHolder.get(convertView,R.id.item_circlechannel_image4);
		ImageView image5 = (ImageView) BaseViewHolder.get(convertView,R.id.item_circlechannel_image5);
		ImageView image6 = (ImageView) BaseViewHolder.get(convertView,R.id.item_circlechannel_image6);
		View view3 = BaseViewHolder.get(convertView,R.id.item_circlechannel_image_ll3);
		ImageView image7 = (ImageView) BaseViewHolder.get(convertView,R.id.item_circlechannel_image7);
		ImageView image8 = (ImageView) BaseViewHolder.get(convertView,R.id.item_circlechannel_image8);
		ImageView image9 = (ImageView) BaseViewHolder.get(convertView,R.id.item_circlechannel_image9);
		mImageViewList.removeAll(mImageViewList);
		mImageViewList.add(image1);
		mImageViewList.add(image2);
		mImageViewList.add(image3);
		mImageViewList.add(image4);
		mImageViewList.add(image5);
		mImageViewList.add(image6);
		mImageViewList.add(image7);
		mImageViewList.add(image8);
		mImageViewList.add(image9);
		final TextView comment_tv = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_comment_tv);
		final TextView zan_tv = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_zan_tv);
		ImageView del = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_del);
		CheckBox del_favorite = BaseViewHolder.get(convertView,
				R.id.circle_list_item_check);

		final View zanOrCmtLayout = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_comment_zan_layout);
		final View zanLayout = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_zan_layout);
		final LinearLayout cmtLayout = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_cmt_layout);
		final View cmtLayoutLine = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_cmt_layout_divider);
		final View cmtMoreLine = BaseViewHolder.get(convertView,R.id.item_circlechannel_cmt_more_divider);
		final TextView cmtMore = BaseViewHolder.get(convertView,R.id.item_circlechannel_cmt_more);
		
		final FlowLayoutSingleLine zanFlow = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_zan_flow);
		final ImageView zanMore = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_zan_more);
		final FlowLayout zanMoreFlow = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_zan_more_flow);
		
		final TextView detail = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_detail);
		final TextView duanpingState = (TextView) BaseViewHolder.get(convertView, R.id.item_circlechannel_duanping);
		final TextView shareContent = BaseViewHolder.get(convertView,
				R.id.item_circlechannel_share);
		
		final DynamicItemBean bean = (DynamicItemBean) obj;
		boolean hasImage = false;
		if (bean.getImage_list() != null && bean.getImage_list().size() > 0) {
			hasImage = true;
		} 
		switch (SettingManager.getInst().getNoPicModel()) {
		case Common.TRUE:
			setImageViewGone();
			for (ImageView o : mImageViewList) {
				ImageLoader.getInstance().cancelDisplayTask(o);
			}
			break;
		case Common.FALSE:
			setImageViewGone();
			if (hasImage) {
				int size = bean.getImage_list().size();
				view1.setVisibility(View.VISIBLE);
				view2.setVisibility(size > 3 ? View.VISIBLE : View.GONE);
				view3.setVisibility(size > 6 ? View.VISIBLE : View.GONE);
				
				if(size == 1){
					image1.setVisibility(View.VISIBLE);
					
					Image mImageBean = bean.getImage_list().get(0);
					
					int w = mImageBean.getImage_width();
					int h = mImageBean.getImage_height();
					updateSizeForOneBig(image1, w, h);
					ImageLoader.getInstance().displayImage(mImageBean.getImage_middle(), image1, mOptions);
					if (Common.isTrue(SettingManager.getInst()
							.getNightModel())) {
						image1.setColorFilter(
								mContext.getResources().getColor(
										R.color.night_img_color),
								PorterDuff.Mode.MULTIPLY);
					}
					image1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext, PhotoPageCircleActivity.class);
							intent.putExtra("index", (Integer)image1.getTag());
							intent.putExtra("imgList", bean.getImage_list());
							mContext.startActivity(intent);
							mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);  
						}
					});
					
				}else{
					for (int i = 0; i < bean.getImage_list().size(); i++) {
						int j = i;
						if(size == 4 && i > 1){
							j++;
						}
						final ImageView o = mImageViewList.get(j);
						o.setTag(i);
						o.setVisibility(View.VISIBLE);
						updateSize(o, mContext.getResources().getDimensionPixelSize(R.dimen.circle_list_img_width),
								mContext.getResources().getDimensionPixelSize(R.dimen.circle_list_img_width));
						ImageLoader.getInstance().displayImage(
								bean.getImage_list().get(i).getImage_middle(),
								o, mOptions);
						if (Common.isTrue(SettingManager.getInst()
								.getNightModel())) {
							o.setColorFilter(
									mContext.getResources().getColor(
											R.color.night_img_color),
									PorterDuff.Mode.MULTIPLY);
						}
						
						o.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext, PhotoPageCircleActivity.class);
								intent.putExtra("index", (Integer)o.getTag());
								intent.putExtra("imgList", bean.getImage_list());
								mContext.startActivity(intent);
								mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);  
							}
						});
					}
				}
			} else {
				view1.setVisibility(View.GONE);
				view2.setVisibility(View.GONE);
				view3.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
		// delete my favorite discuss
		del_favorite.setTag(position);
		del_favorite.setVisibility(isFavDel ? View.VISIBLE : View.GONE);
		if (isFavDel) {
//			initData(getList());
			del_favorite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isSelected.get(position)) {
						isSelected.put(position, false);
//						setIsSelected(isSelected);
					} else {
						isSelected.put(position, true);
//						setIsSelected(isSelected);
					}

				}
			});
			del_favorite.setChecked(isSelected.get(position));
		}
		Utils.showFace(bean.getFace(), head);
		if (Common.isTrue(SettingManager.getInst().getNightModel())) {
			 head.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
		}
		Common.handleUserNameDisplay(getContext(), bean, name);
		name.setVisibility(!TextUtils.isEmpty(bean.getNickname()) ? View.VISIBLE
				: View.GONE);
		try {
			time.setText(Common.formatTimeHoursMinutesBefore(Common.formatYYMMDDHHMMSStoLong(bean.getDateline())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		from.setText("来自" + bean.getFrom());
		if(TextUtils.isEmpty(bean.getTitle())){
			title.setVisibility(View.GONE);
		}else{
			title.setVisibility(View.VISIBLE);
			title.setText(bean.getTitle());
			title.setTag(bean);
			title.setOnLongClickListener(new DynamicCommentOnLongClickListener(mContext,new DeleteListener() {
				
				@Override
				public void deleteSuccess(String data) {
					getList().remove(position);
					notifyDataSetChanged();
					if (mCircleCommentHandleSuccess != null) {
						mCircleCommentHandleSuccess.deleteSuccess(bean, position);
					}
				}
			},bean.getTitle()));
		}
		
		//share content
		if (!TextUtils.isEmpty(bean.getRedirecttitle())) {
			shareContent.setVisibility(View.VISIBLE);
			shareContent.setText(bean.getRedirecttitle());
			
		} else {
			shareContent.setVisibility(View.GONE);
		}
		shareContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO 跳转到分享的文章详情页
				AdDetailActivity.launch(mContext, AdDetailActivity.TYPE_ARTICLE, bean.getRedirecturl(), bean.getRedirecturl());
			}
		});
		
		//详情是否可见
		detail.setVisibility(Common.isTrue(bean.getIs_special()) ? View.VISIBLE : View.GONE);
		
		String contentTxt = Utils.handleDynamicContentConvert(bean);
		if (Common.isTrue(bean.getIs_special())) {
			contentDetail.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
			duanpingState.setVisibility(View.GONE);
			contentDetail.setContent(contentTxt, ImageSpan.ALIGN_BOTTOM);
			contentDetail.setEmotionSize(App.getInst().getResources().getDimensionPixelSize(R.dimen.emotion_size_18));
			contentDetail.setTag(bean);
			contentDetail.setOnLongClickListener(new DynamicCommentOnLongClickListener(mContext,new DeleteListener() {
				
				@Override
				public void deleteSuccess(String data) {
					getList().remove(position);
					notifyDataSetChanged();
					if (mCircleCommentHandleSuccess != null) {
						mCircleCommentHandleSuccess.deleteSuccess(bean, position);
					}
				}
			}, contentTxt));
			detail.setTag(bean);
			detail.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					handleDetailClick((DynamicItemBean)v.getTag());
				}
			});
		} else {
			contentDetail.setVisibility(View.GONE);
			content.setVisibility(View.VISIBLE);
			content.setMaxLines(100);// 这里需要设置一下比6大的数字，不然在复用时用引用到maxline为6的，导致全文不显示..
			content.setContent(contentTxt, ImageSpan.ALIGN_BOTTOM);
			content.setEmotionSize(App.getInst().getResources().getDimensionPixelSize(R.dimen.emotion_size_18));
			content.setTag(bean);
			content.setOnLongClickListener(new DynamicCommentOnLongClickListener(mContext,new DeleteListener() {
				
				@Override
				public void deleteSuccess(String data) {
					getList().remove(position);
					notifyDataSetChanged();
					if (mCircleCommentHandleSuccess != null) {
						mCircleCommentHandleSuccess.deleteSuccess(bean, position);
					}
				}
			}, contentTxt));
			content.post(new Runnable() {
				
				@Override
				public void run() {
					final int lines = content.getLineCount();
					updateViewDuanPingState(getDuanPingItemState(bean), !Common.isTrue(bean.getIs_special()) &&  lines> 6);
					updateViewDuanPing(getDuanPingItemState(bean), duanpingState, content,lines);
					duanpingState.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							DuanPingItemState iState = getDuanPingItemState(bean);
							switch (iState.moreState) {
							case None:
								
								break;
							case Expand:
								iState.moreState = DuanPingState.Pack;
								updateViewDuanPing(getDuanPingItemState(bean), duanpingState, content,lines);
								break;
							case Pack:
								iState.moreState = DuanPingState.Expand;
								updateViewDuanPing(getDuanPingItemState(bean), duanpingState, content, lines);
								break;
						
							}
						}
					});
				}
			});
		}
		
		//处理 赞和评论
		handleZanUpdateView(position, bean,zanOrCmtLayout,zanFlow,zanMoreFlow
				,cmtLayoutLine,cmtLayout,zanMore,zanLayout,zan_tv,cmtMoreLine, cmtMore);
		
		if (isShowDel) {
			del.setTag(bean);
			del.setVisibility(View.VISIBLE);
			del.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DynamicItemBean bean = (DynamicItemBean) v.getTag();
					mArticleManagerUtils.deleteDynamic(mContext, bean.getTid(), new DeleteListener() {
						
						@Override
						public void deleteSuccess(String data) {
							UIHelper.showToast(mContext, "删除成功");
							getList().remove(position);
							tv.setText("全部评论(" + getList().size() + ")");
							notifyDataSetChanged();
						}
					});
				}
			});
		} else {
			del.setVisibility(View.GONE);
			del.setOnClickListener(null);
		}

		head.setTag(bean);
		head.setOnClickListener(new ArticleCommentListeners.NameOnClickListener(getContext()));
		comment_tv.setTag(bean);
		comment_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showCmtPopupwindow(mContext,v,position,zanFlow,
						zanOrCmtLayout,zanMoreFlow,cmtLayoutLine,
						cmtLayout,zanMore,zanLayout,zan_tv,cmtMoreLine,cmtMore);
			}
		});
		
	}
	
	enum ChannelItemType{
		CommonItemWithHeader(0),
		CommonItem(1),
		;
		
		private int type;
		
		private ChannelItemType(int type){
			this.type = type;
		}
		
		public static ChannelItemType parseType(int type){
			ChannelItemType it = ChannelItemType.CommonItem;
			for(ChannelItemType c : values()){
				if(c.type == type){
					it = c;
					break;
				}
			}
			return it;
		}
	}
	
}
