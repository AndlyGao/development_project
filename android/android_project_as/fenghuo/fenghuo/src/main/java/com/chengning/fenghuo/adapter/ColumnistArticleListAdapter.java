package com.chengning.fenghuo.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengning.common.base.BasePageListAdapter;
import com.chengning.common.base.BaseViewHolder;
import com.chengning.common.util.DisplayUtil;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.SettingManager;
import com.chengning.fenghuo.activity.UserInfoActivity;
import com.chengning.fenghuo.data.bean.BaseArticlesBean;
import com.chengning.fenghuo.data.bean.ChannelItemBean;
import com.chengning.fenghuo.data.bean.DynamicItemBean;
import com.chengning.fenghuo.util.ArticleManagerUtils;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.MyRoundedBitmapDisplayer;
import com.chengning.fenghuo.util.PoPupWindowUtils;
import com.chengning.fenghuo.util.PoPupWindowUtils.PopupLocation;
import com.chengning.fenghuo.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ColumnistArticleListAdapter extends BasePageListAdapter {
	private DisplayImageOptions mOptions;
	private int mNoPicModel;
	private Activity mContext;
	private boolean mIsNameClickable;
	private NameFollowListener mListener;

	public ColumnistArticleListAdapter(Activity activity, List list,
			int noPicModel, boolean isNameClickable, NameFollowListener listener) {
		super(activity, list);
		this.mContext = activity;
		this.mNoPicModel = noPicModel;
		this.mIsNameClickable = isNameClickable;
		this.mListener = listener;
		mOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.loading)
				.showImageForEmptyUri(R.drawable.loading)
				.showImageOnFail(R.drawable.loading)
				.resetViewBeforeLoading(true).cacheInMemory(true)
				.cacheOnDisc(true).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new MyRoundedBitmapDisplayer(App.getInst().getResources().getDimensionPixelSize(R.dimen.common_round_corner))).build();

		DisplayUtil.getInst().init(activity);
	}

	@Override
	public int buildLayoutId() {
		return R.layout.item_channel_item_one_small;
	}

	@Override
	public void handleLayout(View convertView, final int position, Object obj) {
		final ImageView head = BaseViewHolder.get(convertView, R.id.item_channel_item_image);
		final TextView name = BaseViewHolder.get(convertView, R.id.item_channel_item_time);
		ImageView face = BaseViewHolder.get(convertView, R.id.item_channel_item_face);
		TextView comment = BaseViewHolder.get(convertView, R.id.item_channel_item_comment);
		TextView title = BaseViewHolder.get(convertView, R.id.item_channel_item_title);
		TextView tag = BaseViewHolder.get(convertView, R.id.item_channel_item_tag);
		TextView author = BaseViewHolder.get(convertView, R.id.item_channel_item_author);
		final BaseArticlesBean bean = (BaseArticlesBean) obj;
		boolean hasImage = false;
		String imagUrl = "";
		if (bean instanceof DynamicItemBean) {
			if (null != bean.getImage_list() && !TextUtils.isEmpty(((DynamicItemBean)bean).getImage_list().get(0).getImage_small())) {
				hasImage = true;
				imagUrl = ((DynamicItemBean)bean).getImage_list().get(0).getImage_small();
			}
		} else if (bean instanceof ChannelItemBean){
			imagUrl = ((ChannelItemBean)bean).getImage_list();
			if (!TextUtils.isEmpty(imagUrl)){
				hasImage = true;
			}
		}
		
		switch (mNoPicModel) {
		case Common.TRUE:
			ImageLoader.getInstance().cancelDisplayTask(head);
			head.setVisibility(View.GONE);
			break;
		case Common.FALSE:
			if (hasImage) {
				head.setVisibility(View.VISIBLE);
				if (Common.isTrue(SettingManager.getInst().getNightModel())) {
					head.setColorFilter(
							mContext.getResources().getColor(
									R.color.night_img_color),
							PorterDuff.Mode.MULTIPLY);
				}
				ImageLoader.getInstance().displayImage(imagUrl, head, mOptions);
			} else {
				head.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
		// delete my favorite discuss
		tag.setVisibility(View.GONE);
		name.setText(bean.getNickname());
		name.setTextColor(mContext.getResources().getColor(R.color.common_blue));
		face.setVisibility(View.VISIBLE);
		Utils.showFace(bean.getFace(), face);
		if (Common.isTrue(SettingManager.getInst().getNightModel())) {
			face.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
		}
		if (mIsNameClickable) {
			name.setTag(bean);
			face.setTag(bean);
			name.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					final BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
					handleNameClick(bean,v);
				
				}
			});
			
			face.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
					handleNameClick(bean, name);
				}
			});
		}
		author.setVisibility(View.GONE);
		title.setText(bean.getTitle());
		comment.setText(bean.getReplys() + "");
		title.setTag(bean);
		head.setTag(bean);

	}
	
	protected void handleNameClick(final BaseArticlesBean bean, View v) {
		boolean isFollow = false;
		String uid = null;
		if(bean instanceof ChannelItemBean) {
			uid = ((ChannelItemBean) bean).getUid();
			isFollow = Common.isTrue(((ChannelItemBean) bean).getIs_follow());
		}else if(bean instanceof DynamicItemBean){
			uid = ((DynamicItemBean) bean).getUid();
			isFollow = Common.isTrue(((DynamicItemBean) bean).getIs_follow());
		}
		if(TextUtils.isEmpty(uid)){
			return;
		}
		final String uidF = uid;
		final boolean isFollowF = isFollow;
		final PoPupWindowUtils popUtils = new PoPupWindowUtils();
		popUtils.setLeftText(isFollowF ? "已关注" : "关注");
		popUtils.setRightText("主页");
		popUtils.setOnLeftClick(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArticleManagerUtils.followUser(getContext(), uidF, new Runnable() {
					
					@Override
					public void run() {
						if(bean instanceof ChannelItemBean) {
							ChannelItemBean cb = ((ChannelItemBean) bean);
							if(uidF.equals(cb.getUid())){
								cb.setIs_follow(!isFollowF ? Common.TRUE : Common.FALSE);
							}
						}else if(bean instanceof DynamicItemBean){
							DynamicItemBean db = ((DynamicItemBean) bean);
							if(uidF.equals(db.getUid())){
								db.setIs_follow(!isFollowF ? Common.TRUE : Common.FALSE);
							}
						}
						
						for(Object bA : getList()){
							if(bA instanceof ChannelItemBean) {
								ChannelItemBean cb = ((ChannelItemBean) bA);
								if(uidF.equals(cb.getUid())){
									cb.setIs_follow(!isFollowF ? Common.TRUE : Common.FALSE);
								}
							}else if(bA instanceof DynamicItemBean){
								DynamicItemBean db = ((DynamicItemBean) bA);
								if(uidF.equals(db.getUid())){
									db.setIs_follow(!isFollowF ? Common.TRUE : Common.FALSE);
								}
							}
						}
						mListener.followChange();
					}
				});
				popUtils.dismiss();
			}
		});
		popUtils.setOnRightClick(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(bean instanceof DynamicItemBean) {
					UserInfoActivity.launch(mContext,
							((DynamicItemBean) bean)
									.getNickname());
				}else if(bean instanceof ChannelItemBean){
					UserInfoActivity.launch(mContext,
						((ChannelItemBean) bean)
								.getNickname());
				}
				popUtils.dismiss();
			}
		});
		popUtils.showPopupWindow(mContext, v, R.layout.popupwindow_comment, PopupLocation.TOP);
	}

	public interface NameFollowListener {
		void followChange();
	}
	
}
