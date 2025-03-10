package com.chengning.fenghuo.adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengning.common.base.BasePageListAdapter;
import com.chengning.common.base.BaseViewHolder;
import com.chengning.common.util.SerializeUtil;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.Consts;
import com.chengning.fenghuo.LoginManager;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.SettingManager;
import com.chengning.fenghuo.activity.ArticleReplyDetailActivity;
import com.chengning.fenghuo.data.bean.BaseArticlesBean;
import com.chengning.fenghuo.data.bean.CommentItemBean;
import com.chengning.fenghuo.util.ArticleCommentListeners;
import com.chengning.fenghuo.util.ArticleManagerUtils;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.Utils;
import com.chengning.fenghuo.widget.CommentInputDialog;
import com.chengning.fenghuo.widget.DeleteArticleCommentDialog;
import com.chengning.fenghuo.widget.DeleteDynamicDialog;
import com.chengning.fenghuo.widget.DynamicTextView;
import com.chengning.fenghuo.widget.RemovePhoneBindDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ArticleCommentItemAdapter extends BasePageListAdapter {

	private boolean isCmtDetail = false;
	private ArticleCommentDeleteListener mCommentDeleteListener;

	public ArticleCommentItemAdapter(Activity activity, List list) {
		super(activity, list);
	}

	public ArticleCommentItemAdapter(Activity activity, List list, boolean isCmtDetail) {
		super(activity, list);
		this.isCmtDetail = isCmtDetail;
	}

	@Override
	public int buildLayoutId() {
		return R.layout.item_article_comment;
	}

	@Override
	public void handleLayout(View convertView, final int position, Object obj) {
		ImageView image;
		TextView name;
		DynamicTextView content;
		TextView time;
		TextView comment;
		final TextView like;
		TextView delete;
		LinearLayout listView;
		View mView;

		mView = BaseViewHolder.get(convertView,
				R.id.root);
		image = BaseViewHolder.get(convertView, R.id.item_article_comment_top_user_image);
		name = BaseViewHolder.get(convertView, R.id.item_article_comment_top_user_name);
		content = BaseViewHolder.get(convertView, R.id.item_article_comment_content_text);
		time = BaseViewHolder.get(convertView, R.id.item_article_comment_top_time);
		comment = BaseViewHolder.get(convertView, R.id.item_article_comment_top_comment);
		like = BaseViewHolder.get(convertView, R.id.item_article_comment_top_like);
		delete = BaseViewHolder.get(convertView, R.id.item_article_comment_top_delete);
		listView = BaseViewHolder.get(convertView, R.id.item_article_comment_content_listview);
		
		final CommentItemBean bean = (CommentItemBean) obj;

//		convertView.setVisibility(!Common.isTrue(bean.getLocal_del_flag()) ? View.VISIBLE : View.GONE);
		mView.setVisibility(!Common.isTrue(bean.getLocal_del_flag()) ? View.VISIBLE : View.GONE);

		Utils.showFace(bean.getFace(), image);
		if (Common.isTrue(SettingManager.getInst().getNightModel())) {
			image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
		}
		Common.handleUserNameDisplay(getContext(), bean, name);

		content.setContent(hanldeCommentContent(bean));
		
		time.setText(Common.dateCompareNow(bean.getDateline()) + " · ");
		
		image.setTag(bean);
		image.setOnClickListener(new ArticleCommentListeners.NameOnClickListener(getContext()));

		int reply = bean.getReplys();
		comment.setText(isCmtDetail ? "回复" : Common.trimZero(reply) + "回复");
		int padding = (isCmtDetail || reply == 0) ? getContext().getResources().getDimensionPixelOffset(R.dimen.comment_reply_tv_padding_no)
				: getContext().getResources().getDimensionPixelOffset(R.dimen.comment_reply_tv_padding);
		comment.setPadding(padding, 0, padding, 0);
		comment.setSelected(isCmtDetail ? false : reply != 0);

		comment.setTag(bean);
		comment.setOnClickListener(isCmtDetail ? new ArticleCommentListeners.CommentOnClickListener(getContext()) : new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
				ArticleReplyDetailActivity.launch(getContext(), bean);
			}
		});
		like.setTag(bean);
//		like.setEnabled(true);
		like.setSelected(Common.isTrue(bean.getIs_dig()));
		String likeCount = Common.trimZero(bean.getDigcounts());
		like.setText(TextUtils.isEmpty(likeCount) ? "赞" : likeCount);
		ArticleManagerUtils.DataStateListener likeState = new ArticleManagerUtils.DataStateListener() {

			@Override
			public void success(Object... objects) {
				if (objects == null) {
					return;
				}
				String digCounts = (String) objects[0];
				like.setText(Common.trimZero(digCounts));
				like.setEnabled(true);
				like.setSelected(true);
				bean.setIs_dig(1);
				bean.setDigcounts(bean.getDigcounts() + 1);
			}

			@Override
			public void init() {
				like.setEnabled(false);
			}

			@Override
			public void failure() {
				like.setEnabled(true);
			}
		};
		like.setOnClickListener(new ArticleCommentListeners.LikeOnClickListener(getContext(), new ArticleManagerUtils(), likeState));

		//删除
		delete.setTag(bean);
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleDelete(v, position);
			}
		});
		if (App.getInst().isLogin() &&
				LoginManager.getInst().getLoginUserBean().getUserinfo().getUid().equals(bean.getUid())) {
			delete.setVisibility(View.VISIBLE);
		} else {
			delete.setVisibility(View.GONE);
		}

		handleHuifuList(listView, bean);
		listView.setTag(bean);
		mView.setTag(bean);
		mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
				if (isCmtDetail) {
//					View v = view.findViewById(R.id.item_article_comment_top_user_image);
					CommentInputDialog dialog = new CommentInputDialog();
					dialog.setBean((FragmentActivity)getContext(), bean, Consts.ArticleCommentType.REPLY);
					dialog.setIsThreeReply(true);
					dialog.show(((FragmentActivity)getContext()).getSupportFragmentManager(), CommentInputDialog.class.getSimpleName());
				} else {
					ArticleReplyDetailActivity.launch(getContext(), bean);
				}
			}
		});
		mView.setOnLongClickListener(new ArticleCommentListeners.ArticleCommentOnLongClickListener(getContext(),
				null, content.getText().toString()));
	}

	private void handleDelete(final  View v, final int position) {
		DeleteArticleCommentDialog dialog = new DeleteArticleCommentDialog();
		dialog.setData(new RemovePhoneBindDialog.RemovePhoneBindOkLitener() {

			@Override
			public void confirm() {
				final BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
				new ArticleManagerUtils().deleteDynamic(getContext(), bean.getTid(), new ArticleManagerUtils.DeleteListener() {
					@Override
					public void deleteSuccess(String data) {
						bean.setLocal_del_flag(Common.TRUE);
						getList().remove(position);
						notifyDataSetChanged();
						if (mCommentDeleteListener != null) {
							mCommentDeleteListener.deleteSuccess(bean);
						}
					}
				});
			}

			@Override
			public void cancle() {
			}
		});
		dialog.show(((FragmentActivity)getContext()).getSupportFragmentManager(),
				DeleteDynamicDialog.class.getSimpleName());
	}

	private void handleHuifuList(LinearLayout listView, final CommentItemBean bean) {
		ArrayList<CommentItemBean> mReplyList = bean.getHuifu_list();
		if  (Common.isListEmpty(mReplyList)) {
			listView.setVisibility(View.GONE);
			return;
		}
		int count = bean.getReplys();
//		listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		listView.setVisibility(View.VISIBLE);
		listView.removeAllViews();
		ArticleCommentItemParentAdapter mAdapter;
		mAdapter = new ArticleCommentItemParentAdapter(getContext(), mReplyList);
		int size = mAdapter.getCount();
		for(int i = 0; i < size; i++){
			final View itemView = mAdapter.getView(i, null, null);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ArticleReplyDetailActivity.launch(getContext(), bean);
				}
			});
			listView.addView(itemView);
		}

		if(size < count){
			TextView more = (TextView) View.inflate(getContext(), R.layout.article_comment_reply_more, null);
			more.setText("查看全部" + count + "条回复");
			more.setTag(bean);
			more.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
					ArticleReplyDetailActivity.launch(getContext(), bean);
				}
			});
			listView.addView(more);
		}
	}

	SpannableStringBuilder hanldeCommentContent(BaseArticlesBean bean) {
		CommentItemBean commentItemBean = (CommentItemBean) bean;
		String parentContent = commentItemBean.getParent_content();
		SpannableStringBuilder builder = new SpannableStringBuilder();
		String shortContent = commentItemBean.getShort_content();
		if (!TextUtils.isEmpty(shortContent)) {
			builder.append(shortContent);
			int oStart = builder.length();
			builder.append("全文");
			int oEnd = builder.length();
			builder.setSpan(new URLSpan(Consts.REPLY_DETAIL_SCHEME + SerializeUtil.serialize(bean)), oStart,
					oEnd, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
		} else {
			builder.append(Utils.handleDynamicContentConvert(commentItemBean));
			if (!TextUtils.isEmpty(parentContent) ) {
				builder.append("//").append("@").append(commentItemBean.getTousername())
						.append(":").append(parentContent);
			}
		}

		return builder;
	}


	public void setCommentDeleteListener(ArticleCommentDeleteListener listener) {
		this.mCommentDeleteListener = listener;
	}

	public static interface ArticleCommentDeleteListener {
		void deleteSuccess(BaseArticlesBean bean);
	}
}
