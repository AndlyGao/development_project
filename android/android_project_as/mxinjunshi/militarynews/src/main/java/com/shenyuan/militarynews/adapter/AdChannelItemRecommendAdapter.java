package com.shenyuan.militarynews.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chengning.common.base.BaseTypeListAdapter;
import com.chengning.common.base.BaseViewHolder;
import com.chengning.common.base.Clearable;
import com.chengning.common.util.DisplayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.IIsRecom;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.ad.AdDataDummy;
import com.shenyuan.militarynews.ad.AdDataDummy.NativeResponseDummy;
import com.shenyuan.militarynews.ad.AdDataListener;
import com.shenyuan.militarynews.ad.AdDataManager;
import com.shenyuan.militarynews.beans.data.MChannelItemBean;
import com.shenyuan.militarynews.data.access.LocalStateServer;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.MyCoverRoundedBitmapDisplayer;
import com.shenyuan.militarynews.utils.MyRoundedBitmapDisplayer;
import com.shenyuan.militarynews.utils.Utils;

public class AdChannelItemRecommendAdapter extends BaseTypeListAdapter implements IIsRecom, Clearable {
	
	private static final String TAG = AdChannelItemRecommendAdapter.class.getSimpleName(); 

	private Activity mContext;
	private DisplayImageOptions mOptions;
	private DisplayImageOptions mOptionsWithImage;
	private DisplayImageOptions mOptionsWithImageSmall;

	private int mImageThreeSmallWidth;
	private int mImageThreeSmallHeight;
	private int mImageOneTwoBigWidth;
	private int mImageOneTwoBigHeight;
	private int mImageOneTwoSmallWidth;
	private int mImageOneTwoSmallHeight;
	private int mImageOneSmallWidth;
	private int mImageOneSamllHeight;
	private OnClickListener mMoreListener;
	
	private int mPreListSize;
	private AdDataManager mAdDataManager;
	
	private boolean isRecom = false;
	private boolean mIsHomeRecom = false;
	
	private String mChannel;

	private boolean mIsDividerExclusive;

	public boolean isRecom() {
		return isRecom;
	}

	public void setIsRecom(boolean isRecom) {
		this.isRecom = isRecom;
	}

	public boolean isHomeRecom() {
		return mIsHomeRecom;
	}

	public void setHomeRecom(boolean isHomeRecom) {
		this.mIsHomeRecom = isHomeRecom;
	}

	public AdChannelItemRecommendAdapter(Activity activity, List list, OnClickListener moreOnClickListenner) {
		super(activity, list);

		mContext = activity;
		mMoreListener = moreOnClickListenner;

		mOptions = new DisplayImageOptions.Builder() 
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.loading)
		.showImageOnFail(R.drawable.loading)
		.resetViewBeforeLoading(true)
		.cacheInMemory()  
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new MyRoundedBitmapDisplayer(App.getInst().getResources().getDimensionPixelSize(R.dimen.common_round_corner)))
		.build();
		
		mOptionsWithImage = new DisplayImageOptions.Builder() 
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.loading)
		.showImageOnFail(R.drawable.loading)
		.resetViewBeforeLoading(true)
		.cacheInMemory()  
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new MyCoverRoundedBitmapDisplayer(App.getInst().getResources().getDimensionPixelSize(R.dimen.common_round_corner), R.drawable.video_list_icon))
		.build();
		
		mOptionsWithImageSmall = new DisplayImageOptions.Builder() 
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.loading)
		.showImageOnFail(R.drawable.loading)
		.resetViewBeforeLoading(true)
		.cacheInMemory()  
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new MyCoverRoundedBitmapDisplayer(App.getInst().getResources().getDimensionPixelSize(R.dimen.common_round_corner), R.drawable.video_list_icon_small))
		.build();
		
		DisplayUtil.getInst().init(activity);
		int gapWidth = activity.getResources().getDimensionPixelSize(R.dimen.common_horizontal_margin);
		int gap2Width = activity.getResources().getDimensionPixelSize(R.dimen.tuwen_image_gap);
		int gapThreeSmall = activity.getResources().getDimensionPixelSize(R.dimen.common_horizontal_margin_three_small); 
		float imageLayoutWidth = (float)(DisplayUtil.getInst().getScreenWidth() - gapWidth * 2);
		
		mImageThreeSmallWidth =  ((int)imageLayoutWidth - gapThreeSmall * 2) / 3;
		mImageThreeSmallHeight = (int) (mImageThreeSmallWidth / 1.5);
		mImageOneSmallWidth = mImageThreeSmallWidth;
		mImageOneSamllHeight = mImageThreeSmallHeight;
		
		mImageOneTwoBigWidth = (int) (imageLayoutWidth * 0.615f + 0.5f);
		mImageOneTwoSmallWidth = (int) (imageLayoutWidth - gap2Width - mImageOneTwoBigWidth);
		mImageOneTwoSmallHeight = (int) (((float)mImageOneTwoSmallWidth) / 1.338f + 0.5f);
		mImageOneTwoBigHeight = mImageOneTwoSmallHeight * 2 + gap2Width;
		
		mAdDataManager = new AdDataManager(getContext());
	}
	
	@Override
	public void notifyDataSetChanged(){
		int size = getCount();
		if(size > mPreListSize){
			// 分页增加
			mAdDataManager.resetListenerMap();
		}else{
			mAdDataManager.reset();
		}
		for(int i = 0; i < getCount(); i++){
			MChannelItemBean bean = (MChannelItemBean) getItem(i);
			if(Const.NEWS_TYPE_COMMON_AD == bean.getNews_show_type() || Const.NEWS_TYPE_COMMON_AD_ONE_BIG == bean.getNews_show_type()
					|| Const.NEWS_TYPE_COMMON_AD_THREE_SMALL == bean.getNews_show_type()){
				// ad
				mAdDataManager.getData(bean.getAd_type(), bean.getAd_place_id(), i, mEmptyAdDataListener);
			}
		}
		
		mPreListSize = size;
		super.notifyDataSetChanged();
	}

	@Override
	public void clear() {
		mAdDataManager.reset();
	}
	
	private AdDataListener mEmptyAdDataListener = new AdDataListener() {
		
		@Override
		public void onData(NativeResponseDummy obj) {
		}
	};

	private OnClickListener refreshListener;

	public static enum ChannelItemType {
		/**
		 * OneSmall，1个小图，默认type，type必须从0开始
		 */
		OneSmall(0, Const.NEWS_TYPE_COMMON, Const.NEWS_TYPE_COMMON_NO_PIC),	// 1个小图
		OneBig(1, Const.NEWS_TYPE_ONE_BIG_PIC),	// 1个大图
		OneBigTwoSmall(2, Const.NEWS_TYPE_ONE_BIG_TWO_SMALL_PIC),	// 1大图2小图
		ThreeSmall(3, Const.NEWS_TYPE_THREE_SMALL_PIC, Const.NEWS_TYPE_SIX_SMALL_PIC),	// 3小图
		TwoBig(4, Const.NEWS_TYPE_TWO_BIG_PIC), // 2大图
		Ad(5, Const.NEWS_TYPE_COMMON_AD), // 广告
		AdOneBig(6, Const.NEWS_TYPE_COMMON_AD_ONE_BIG), // 广告
		AdThreeSmall(7, Const.NEWS_TYPE_COMMON_AD_THREE_SMALL), // 广告
		;
		
		private int type;
		private int showType[];
		
		private ChannelItemType(int type, int... showType){
			this.type = type;
			this.showType = showType;
		}
		
		public int getType(){
			return type;
		}
		
		public int[] getShowType(){
			return showType;
		}
		
		public static ChannelItemType parseType(int type){
			ChannelItemType it = ChannelItemType.OneSmall;
			for(ChannelItemType c : values()){
				if(c.type == type){
					it = c;
					break;
				}
			}
			return it;
		}
		
		public static ChannelItemType parseShowType(int showType){
			ChannelItemType it = ChannelItemType.OneSmall;
			for(ChannelItemType c : values()){
				for(int i : c.showType){
					if(i == showType){
						it = c;
						break;
					}
				}
			}
			return it;
		}
	}

	@Override
	public int getItemViewType(int position) {
		MChannelItemBean bean = (MChannelItemBean) getItem(position);
		ChannelItemType itemType = ChannelItemType.parseShowType(bean.getNews_show_type());
		if(itemType == ChannelItemType.Ad || itemType == ChannelItemType.AdOneBig || itemType == ChannelItemType.AdThreeSmall){
			return itemType.getType();
		}else if(Common.isTrue(SettingManager.getInst().getNoPicModel())){
			// 无图模式固定样式
			return ChannelItemType.OneSmall.getType();
		}else{
			return itemType.getType();
		}
	}

	@Override
	public int getViewTypeCount() {
		if(Common.isTrue(SettingManager.getInst().getNoPicModel())){
			return 1;
		}else{
			return ChannelItemType.values().length;
		}
	}
	
	@Override
	public View buildLayoutView(int position, int type) {
		View view = View.inflate(getContext(), R.layout.item_channel_item_recommend, null);
		ViewStub stub = (ViewStub) view.findViewById(R.id.item_channel_item_stub);
		ChannelItemType ct = ChannelItemType.parseType(type);
		switch (ct) {
		default:
		case OneSmall:
			stub.setLayoutResource(R.layout.item_channel_item_one_small);
			break;
		case OneBig:
			stub.setLayoutResource(R.layout.item_channel_item_one_big);
			break;
		case OneBigTwoSmall:
			stub.setLayoutResource(R.layout.item_channel_item_one_big_two_small);
			break;
		case ThreeSmall:
			stub.setLayoutResource(R.layout.item_channel_item_three_small);
			break;
		case TwoBig:
			stub.setLayoutResource(R.layout.item_channel_item_two_big);
			break;
		case Ad:
			stub.setLayoutResource(R.layout.item_channel_item_one_small);
			break;
		case AdOneBig:
			stub.setLayoutResource(Common.isTrue(SettingManager.getInst()
					.getNoPicModel()) ? R.layout.item_channel_item_one_small
					: R.layout.item_channel_item_one_big);
			break;
		case AdThreeSmall:
			stub.setLayoutResource(Common.isTrue(SettingManager.getInst()
					.getNoPicModel()) ? R.layout.item_channel_item_one_small
							: R.layout.item_channel_item_three_small_ad);
			break;
		}
		stub.inflate();
		return view;
	}

	@Override
	public int buildLayoutId(int position, int type) {
		return -1;
	}

	@Override
	public void handleLayout(final View convertView, final int position, Object obj,
			int type) {
		// type
		View contentLayout = BaseViewHolder.get(convertView, R.id.item_channel_item_content_layout);
		final ChannelItemType itemType = ChannelItemType.parseType(type);

		MChannelItemBean bean = (MChannelItemBean) getItem(position);
		MChannelItemBean bean2 = null;
		
		if(itemType == ChannelItemType.Ad || itemType == ChannelItemType.AdOneBig || itemType == ChannelItemType.AdThreeSmall){
			contentLayout.setVisibility(View.GONE);
			// 标记位置，防止异步ad返回后view位置变化
			BaseViewHolder.get(convertView, R.id.item_channel_item_time).setTag(position);
			final MChannelItemBean finalBean = bean;
			
//			Log.e(TAG, "aditem 1 position: " + position);
			mAdDataManager.getData(finalBean.getAd_type(),finalBean.getAd_place_id(), position, new AdDataListener() {
//			mAdDataManager.getData("8090820183818305", position, new AdDataListener() {
				
				@Override
				public void onData(NativeResponseDummy obj) {
					if(convertView == null){
						return;
					}
					View tagObjView = BaseViewHolder.get(convertView, R.id.item_channel_item_time);
					Object tagObj = tagObjView != null ? tagObjView.getTag() : null;
					if(tagObj == null || !(tagObj instanceof Integer) || (Integer)tagObj != position){
						return;
					}
					View contentLayout = BaseViewHolder.get(convertView, R.id.item_channel_item_content_layout);
					contentLayout.setVisibility(View.VISIBLE);
					
					NativeResponseDummy adBean = obj;

					
					// 处理广告动态3图1图的数据
					View currentLayout = convertView;
					ChannelItemType itemTypeAd = itemType;
					if(itemTypeAd == ChannelItemType.AdThreeSmall){
						if(AdDataDummy.NATIVE_3IMAGE == adBean.getAdPatternType()){
							itemTypeAd = ChannelItemType.AdThreeSmall;
							ViewGroup group = BaseViewHolder.get(convertView, R.id.item_channel_item_three_small_ad_layout);
							if(group == null){
								contentLayout.setVisibility(View.GONE);
								return;
							}
							group.removeAllViews();
							currentLayout = View.inflate(getContext(), R.layout.item_channel_item_three_small, null);
							group.addView(currentLayout);
						}else{
							itemTypeAd = ChannelItemType.Ad;
							ViewGroup group = BaseViewHolder.get(convertView, R.id.item_channel_item_three_small_ad_layout);
							if(group == null){
								contentLayout.setVisibility(View.GONE);
								return;
							}
							group.removeAllViews();
							currentLayout = View.inflate(getContext(), R.layout.item_channel_item_one_small, null);
							group.addView(currentLayout);
						}
					}
//					Log.e(TAG, "aditem 2 position: " + position + ", title: " + adBean.getTitle());
					
					TextView title = null;
					TextView topTitle;
					ImageView image;
					ImageView image2 = null;
					ImageView image3 = null;
					TextView tag;
					TextView time;
					TextView like;
					TextView comment;
					View moreLayout;
					TextView more;
					View divider;

					topTitle = BaseViewHolder.get(convertView, R.id.item_channel_item_top_title);
					moreLayout = BaseViewHolder.get(convertView, R.id.item_channel_item_more_ll);
					more = BaseViewHolder.get(convertView, R.id.item_channel_item_more_tv);
					divider = BaseViewHolder.get(convertView, R.id.item_channel_item_divider);

					if(itemTypeAd == ChannelItemType.AdThreeSmall && !Common.isTrue(SettingManager.getInst().getNoPicModel())){
						// 三图广告且不在无图模式下
						title = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_three_small);
						image2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image2);
						image3 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image3);
					}else if(itemTypeAd == ChannelItemType.AdOneBig && !Common.isTrue(SettingManager.getInst().getNoPicModel())){
						// 大图广告且不在无图模式下
						title = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_one_big);
					}else{
						title = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_one_small);
					}
					
					image = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image);
					tag = BaseViewHolder.get(currentLayout, R.id.item_channel_item_tag);
					time = BaseViewHolder.get(currentLayout, R.id.item_channel_item_time);
					comment = BaseViewHolder.get(currentLayout, R.id.item_channel_item_comment);
					like = BaseViewHolder.get(currentLayout, R.id.item_channel_item_like);
					
					topTitle.setVisibility(View.GONE);
					moreLayout.setVisibility(View.GONE);
					
					if (!Common.isTrue(SettingManager.getInst().getNoPicModel())) {
						if(itemTypeAd == ChannelItemType.AdThreeSmall){
							updateSize(image, mImageThreeSmallWidth, mImageThreeSmallHeight);
							updateSize(image2, mImageThreeSmallWidth, mImageThreeSmallHeight);
							updateSize(image3, mImageThreeSmallWidth, mImageThreeSmallHeight);
							if(!Common.isListEmpty(adBean.getMultiPicUrls()) && adBean.getMultiPicUrls().size() > 2){
								image.setVisibility(View.VISIBLE);
								image2.setVisibility(View.VISIBLE);
								image3.setVisibility(View.VISIBLE);
								ImageLoader.getInstance().displayImage(adBean.getMultiPicUrls().get(0), image, getImageOptions(finalBean));
								ImageLoader.getInstance().displayImage(adBean.getMultiPicUrls().get(1), image2, getImageOptions(finalBean));
								ImageLoader.getInstance().displayImage(adBean.getMultiPicUrls().get(2), image3, getImageOptions(finalBean));
								if (Common.isTrue(SettingManager.getInst().getNightModel())) {
									image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
									image2.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
									image3.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
								}
							}else{
								ImageLoader.getInstance().cancelDisplayTask(image);
								ImageLoader.getInstance().cancelDisplayTask(image2);
								ImageLoader.getInstance().cancelDisplayTask(image3);
								image.setVisibility(View.GONE);
								image2.setVisibility(View.GONE);
								image3.setVisibility(View.GONE);
							}
						}else{
							if(itemTypeAd == ChannelItemType.Ad){
								updateSize(image, mImageOneSmallWidth, mImageOneSamllHeight);
							}
							if (!TextUtils.isEmpty(adBean.getImageUrl())) {
								image.setVisibility(View.VISIBLE);
								ImageLoader.getInstance().displayImage(adBean.getImageUrl(), image, mOptions);
							} else if (!TextUtils.isEmpty(adBean.getIconUrl())) {
								image.setVisibility(View.VISIBLE);
								ImageLoader.getInstance().displayImage(adBean.getIconUrl(), image, mOptions);
							} else {
								ImageLoader.getInstance().cancelDisplayTask(image);
								image.setVisibility(View.GONE);
							}
							if (Common.isTrue(SettingManager.getInst().getNightModel())) {
								image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
							}
						}
					} else {
						ImageLoader.getInstance().cancelDisplayTask(image);
						if(image != null){
							image.setVisibility(View.GONE);
						}
						if(itemTypeAd == ChannelItemType.AdThreeSmall){
							if(image2 != null){
								image2.setVisibility(View.GONE);
							}
							if(image3 != null){
								image3.setVisibility(View.GONE);
							}
						}
					}
					
					title.setText(adBean.getTitle());
					time.setText(finalBean.getRed_tag());
					tag.setVisibility(View.GONE);
					comment.setVisibility(View.GONE);
					like.setVisibility(View.GONE);
					contentLayout.setTag(adBean);
					
					adBean.recordImpression(convertView);
					handleDivider(divider, position);

//					Common.handleTextViewReaded(getContext(), title,
//							LocalStateServer.getInst(getContext()).isRead(LocalStateServer.PREFIX_CHANNEL_ITEM, bean.getAid()));
				}
			});
		}else{
			contentLayout.setVisibility(View.VISIBLE);

			int dataPosition = position;
			if(itemType == ChannelItemType.TwoBig){
				// 奇偶成对，只显示一个
				int preIndex = dataPosition - 1;
				int nextIndex = dataPosition + 1;
				int anotherIndex =  0;
				List<MChannelItemBean> list = getList();
				if(preIndex >= 0 && list.get(preIndex).getNews_show_type() == Const.NEWS_TYPE_TWO_BIG_PIC){
					// 前一个则还原显示顺序
					anotherIndex = preIndex;
					bean2 = bean;
					bean = list.get(preIndex);
				}else if(nextIndex < list.size() && list.get(nextIndex).getNews_show_type() == Const.NEWS_TYPE_TWO_BIG_PIC){
					anotherIndex = nextIndex;
					bean2 = list.get(nextIndex);
				}else{
					contentLayout.setVisibility(View.GONE);
					return;
				}
				if(anotherIndex % 2 == 0){
					contentLayout.setVisibility(View.GONE);
					return;
				}else{
					contentLayout.setVisibility(View.VISIBLE);
				}
			}
			
			TextView title = null;
			TextView category = null;
			TextView topTitle;
			ImageView image;
			TextView tag;
			TextView time;
			TextView like;
			TextView comment;
			ImageView image2 = null;
			ImageView image3 = null;
			View moreLayout;
			TextView more;
			TextView refresh;
			View divider;

			View currentLayout = convertView;
			topTitle = BaseViewHolder.get(convertView, R.id.item_channel_item_top_title);
			moreLayout = BaseViewHolder.get(convertView, R.id.item_channel_item_more_ll);
			more = BaseViewHolder.get(convertView, R.id.item_channel_item_more_tv);
			refresh = BaseViewHolder.get(convertView, R.id.item_channel_item_click_refresh);
			
			if (!TextUtils.isEmpty(bean.getNav())) {
				topTitle.setVisibility(View.VISIBLE);
				topTitle.setText(bean.getNav());
			} else {
				topTitle.setVisibility(View.GONE);
				if (!(Const.NEWS_TYPE_COMMON == bean.getNews_show_type() || Const.NEWS_TYPE_COMMON_NO_PIC == bean.getNews_show_type())) {
					if (dataPosition > 0 ){
						String nav = ((List<MChannelItemBean>)getList()).get(dataPosition - 1).getNav();
						String Localnav = ((List<MChannelItemBean>)getList()).get(dataPosition - 1).getLocal_nav();
						if (!TextUtils.isEmpty(nav)) {
							bean.setLocal_nav(nav);
						} else if (!TextUtils.isEmpty(Localnav)) {
								bean.setLocal_nav(Localnav);
						}
					}
				}
			}
			
			if (!TextUtils.isEmpty(bean.getMore())) {
				moreLayout.setVisibility(View.VISIBLE);
				more.setText(bean.getMore());
				
				if (!TextUtils.isEmpty(bean.getNav())) {
					moreLayout.setTag(bean.getNav());
				} else if (!TextUtils.isEmpty(bean.getLocal_nav())) {
					moreLayout.setTag(bean.getLocal_nav());
				}
				moreLayout.setOnClickListener(mMoreListener);
			} else if (bean2 != null && !TextUtils.isEmpty(bean2.getMore())) {
				moreLayout.setVisibility(View.VISIBLE);
				more.setText(bean2.getMore());
				moreLayout.setTag(bean.getNav());
				moreLayout.setOnClickListener(mMoreListener);
			}else {
				moreLayout.setVisibility(View.GONE);
			}
			
			//设置刚刚看到这里，点击刷新
			refresh.setVisibility(!TextUtils.isEmpty(bean.getTips()) ? View.VISIBLE : View.GONE);
			refresh.setOnClickListener(refreshListener);
			
			switch (itemType) {
			default:
			case OneSmall:
				title = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_one_small);
				break;
			case OneBig:
				title = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_one_big);
				category = BaseViewHolder.get(currentLayout, R.id.item_channel_item_category);
				break;
			case OneBigTwoSmall:
				title = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_one_big_two_small);
				image2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image2);
				image3 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image3);
				
				break;
			case ThreeSmall:
				title = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_three_small);
				image2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image2);
				image3 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image3);
				break;
			case TwoBig:
				View layoutTB1 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_list_two_big_1);
				image2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image_1);
				TextView titleTB1 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_two_big_1);
				TextView tagTB1 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_tag_1);
				TextView timeTB1 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_time_1);
				TextView likeTB1 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_like_1);
				TextView commentTB1 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_comment_1);
				
				View layoutTB2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_list_two_big_2);
				image3 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image_2);
				TextView titleTB2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_two_big_2);
				TextView tagTB2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_tag_2);
				TextView timeTB2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_time_2);
				TextView likeTB2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_like_2);
				TextView commentTB2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_comment_2);

				titleTB1.setTag(bean);
				titleTB1.setText(bean.getTitle());
				handleTime(timeTB1, bean);
				timeTB1.setTag(position);
				handleTag(tagTB1, bean);
				likeTB1.setText(bean.getDigg());
				commentTB1.setText(bean.getPl());

				layoutTB1.setOnClickListener(twoBigListener1);
				
				Common.handleTextViewReaded(mContext, titleTB1,
						LocalStateServer.getInst(getContext()).isRead(LocalStateServer.PREFIX_CHANNEL_ITEM, bean.getAid()));
				
				if (!TextUtils.isEmpty(bean.getImage()) && !Common.isTrue(SettingManager.getInst().getNoPicModel())) {
					image2.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(bean.getImage(), image2, getImageOptions(bean));
				} else {
					ImageLoader.getInstance().cancelDisplayTask(image2);
					image2.setVisibility(View.GONE);
				}
				if (Common.isTrue(SettingManager.getInst().getNightModel())) {
					image2.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
				}

				titleTB2.setTag(bean2);
				titleTB2.setText(bean2.getTitle());
				handleTime(timeTB2, bean2);
				timeTB2.setTag(position);
				handleTag(tagTB2, bean2);
				likeTB2.setText(bean2.getDigg());
				commentTB2.setText(bean2.getPl());

				layoutTB2.setOnClickListener(twoBigListener2);
				Common.handleTextViewReaded(mContext, titleTB2,
							LocalStateServer.getInst(getContext()).isRead(LocalStateServer.PREFIX_CHANNEL_ITEM, bean2.getAid()));
				if (!TextUtils.isEmpty(bean2.getImage()) && !Common.isTrue(SettingManager.getInst().getNoPicModel())) {
					image3.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(bean2.getImage(), image3, getImageOptions(bean));
				} else {
					ImageLoader.getInstance().cancelDisplayTask(image3);
					image3.setVisibility(View.GONE);
				}
				if (Common.isTrue(SettingManager.getInst().getNightModel())) {
					image3.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
				}
				break;
			}
			
			image = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image);
			tag = BaseViewHolder.get(currentLayout, R.id.item_channel_item_tag);
			time = BaseViewHolder.get(currentLayout, R.id.item_channel_item_time);
			comment = BaseViewHolder.get(currentLayout, R.id.item_channel_item_comment);
			like = BaseViewHolder.get(currentLayout, R.id.item_channel_item_like);
			divider = BaseViewHolder.get(convertView, R.id.item_channel_item_divider);

			if (!Common.isTrue(SettingManager.getInst().getNoPicModel())) {
				switch (itemType) {
				default:
				case OneSmall:
					updateSize(image, mImageOneSmallWidth, mImageOneSamllHeight);
					if (!TextUtils.isEmpty(bean.getImage())) {
						image.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(bean.getImage(), image, getImageOptions(bean));
					} else {
						ImageLoader.getInstance().cancelDisplayTask(image);
						image.setVisibility(View.GONE);
					}
					if (Common.isTrue(SettingManager.getInst().getNightModel())) {
						image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
					}
					break;
				case OneBig:
					if (!TextUtils.isEmpty(bean.getImage())) {
						image.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(bean.getImage(), image, getImageOptions(bean));
					} else {
						ImageLoader.getInstance().cancelDisplayTask(image);
						image.setVisibility(View.GONE);
					}
					if (Common.isTrue(SettingManager.getInst().getNightModel())) {
						image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
					}
					break;
				case OneBigTwoSmall:
					updateSize(image, mImageOneTwoBigWidth, mImageOneTwoBigHeight);
					updateSize(image2, mImageOneTwoSmallWidth, mImageOneTwoSmallHeight);
					updateSize(image3, mImageOneTwoSmallWidth, mImageOneTwoSmallHeight);
					if(!Common.isListEmpty(bean.getImage_arr()) && bean.getImage_arr().size() > 2){
						image.setVisibility(View.VISIBLE);
						image2.setVisibility(View.VISIBLE);
						image3.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(bean.getImage_arr().get(0), image, getImageOptions(bean, true));
						ImageLoader.getInstance().displayImage(bean.getImage_arr().get(1), image2, getImageOptions(bean));
						ImageLoader.getInstance().displayImage(bean.getImage_arr().get(2), image3, getImageOptions(bean));
						if (Common.isTrue(SettingManager.getInst().getNightModel())) {
							image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
							image2.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
							image3.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
						}
					}else{
						ImageLoader.getInstance().cancelDisplayTask(image);
						ImageLoader.getInstance().cancelDisplayTask(image2);
						ImageLoader.getInstance().cancelDisplayTask(image3);
						image.setVisibility(View.GONE);
						image2.setVisibility(View.GONE);
						image3.setVisibility(View.GONE);
					}
					break;
				case ThreeSmall:
					updateSize(image, mImageThreeSmallWidth, mImageThreeSmallHeight);
					updateSize(image2, mImageThreeSmallWidth, mImageThreeSmallHeight);
					updateSize(image3, mImageThreeSmallWidth, mImageThreeSmallHeight);
					if(!Common.isListEmpty(bean.getImage_arr()) && bean.getImage_arr().size() > 2){
						image.setVisibility(View.VISIBLE);
						image2.setVisibility(View.VISIBLE);
						image3.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(bean.getImage_arr().get(0), image, getImageOptions(bean));
						ImageLoader.getInstance().displayImage(bean.getImage_arr().get(1), image2, getImageOptions(bean));
						ImageLoader.getInstance().displayImage(bean.getImage_arr().get(2), image3, getImageOptions(bean));
						if (Common.isTrue(SettingManager.getInst().getNightModel())) {
							image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
							image2.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
							image3.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
						}
					}else{
						ImageLoader.getInstance().cancelDisplayTask(image);
						ImageLoader.getInstance().cancelDisplayTask(image2);
						ImageLoader.getInstance().cancelDisplayTask(image3);
						image.setVisibility(View.GONE);
						image2.setVisibility(View.GONE);
						image3.setVisibility(View.GONE);
					}
					break;
					
				case TwoBig:
					// do nothing.
					break;
				}
			} else {
				ImageLoader.getInstance().cancelDisplayTask(image);
				if(image != null){
					image.setVisibility(View.GONE);
				}
				if (itemType == ChannelItemType.OneBigTwoSmall) {
					image2.setVisibility(View.GONE);
					image3.setVisibility(View.GONE);
				}else if(itemType == ChannelItemType.ThreeSmall) {
					image2.setVisibility(View.GONE);
					image3.setVisibility(View.GONE);
				}else if(itemType == ChannelItemType.TwoBig) {
					image2.setVisibility(View.GONE);
					image3.setVisibility(View.GONE);
				}
			}
			
			if (itemType != ChannelItemType.TwoBig) {
				title.setText(bean.getTitle());
				handleTime(time, bean);
//				handleTag(tag, bean);
				like.setText(bean.getDigg());
				comment.setText(bean.getPl() + "评");
				contentLayout.setTag(bean);

				Common.handleTextViewReaded(getContext(), title,
						LocalStateServer.getInst(getContext()).isRead(LocalStateServer.PREFIX_CHANNEL_ITEM, bean.getAid()));
			}
			handleTag(tag, bean);
			handleCategory(category, bean);
			handleDivider(divider, position);
		}
		
	}
	
	protected void handleDivider(View divider, int position) {
		if (divider != null) {
			divider.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) divider.getLayoutParams();
			params.height = mIsDividerExclusive ? 
					getContext().getResources().getDimensionPixelSize(R.dimen.channel_item_divider_height) 
					: getContext().getResources().getDimensionPixelSize(R.dimen.channel_item_divider_height_common);
			params.leftMargin = params.rightMargin = mIsDividerExclusive ? 0 
					: getContext().getResources().getDimensionPixelSize(R.dimen.common_horizontal_margin);
			divider.setLayoutParams(params);
		}
	}

	private void handleCategory(TextView category, MChannelItemBean bean) {
		if (category == null) {
			return;
		}
		category.setText(bean.getCategory());
		category.setVisibility(mIsDividerExclusive ? View.VISIBLE : View.GONE);
	}

	private DisplayImageOptions getImageOptions(MChannelItemBean bean){
		return getImageOptions(bean, false);
	}
	
	private DisplayImageOptions getImageOptions(MChannelItemBean bean, boolean forceBig){
		if(Const.CHANNEL_ARTICLE_VIDEO.equals(bean.getChannel())){
			if(forceBig || Const.NEWS_TYPE_ONE_BIG_PIC == bean.getNews_show_type()){
				return mOptionsWithImage;
			}else{
				return mOptionsWithImageSmall;
			}
		}else{
			return mOptions;
		}
	}
	
	private void updateSize(View v, int width, int height){
		LayoutParams lp = v.getLayoutParams();
		lp.width = width;
		lp.height = height;
		v.setLayoutParams(lp);
	}
	
	private OnClickListener twoBigListener1 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			TextView title = (TextView) v.findViewById(R.id.item_channel_item_title_two_big_1);
			Common.handleTextViewReaded(getContext(), title, true);
			
			int position = (Integer) v.findViewById(R.id.item_channel_item_time_1).getTag();
			ChannelItemType type = ChannelItemType.TwoBig;
			MChannelItemBean bean = (MChannelItemBean) title.getTag();
			handleBeanClick(type, bean, position);
		}
	};
	
	private OnClickListener twoBigListener2 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			TextView title = (TextView) v.findViewById(R.id.item_channel_item_title_two_big_2);
			Common.handleTextViewReaded(getContext(), title, true);

			int position = (Integer) v.findViewById(R.id.item_channel_item_time_2).getTag();
			ChannelItemType type = ChannelItemType.TwoBig;
			MChannelItemBean bean = (MChannelItemBean) title.getTag();
			handleBeanClick(type, bean, position);
		}
	};

	public void handleBeanClick(ChannelItemType type, MChannelItemBean bean, int position){
		LocalStateServer.getInst(getContext()).setReadStateRead(LocalStateServer.PREFIX_CHANNEL_ITEM, bean.getAid());

		String area = (mIsHomeRecom && position >= 0 && position < 30) ? "new30" : "normal";
		Utils.handleBeanClick(getContext(), bean, area);
	}
	
	public void handleTime(TextView tv, MChannelItemBean bean){
		// 推荐下第一页显示栏目名称，否则显示时间
//		tv.setText((isRecom && bean.getLocal_page() == JUrl.PAGE_START) ? 
//				bean.getCategory() : Common.getDateCompareNow(bean.getPubTimestamp()));
//		tv.setText(Common.formatTimeHoursMinutesBefore(bean.getPubTimestamp()));
		tv.setText(Common.formatTimestmpByJs(bean.getPubTimestamp()));
	}
	
	public void handleTag(TextView tv, MChannelItemBean bean){
		if(tv == null){
			return;
		}
//		// 红色logo 红字标签 #ff3838
//		if(!TextUtils.isEmpty(bean.getRed_tag())){
//			tv.setCompoundDrawablePadding(getContext().getResources().getDimensionPixelSize(R.dimen.channel_item_left_bottom_drawable_padding));
//			tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.redian_hot, 0, 0, 0);	
//
////			sb.append("<font color='#ff3838'>").append(bean.getRed_tag()).append("</font>").append(" ");
//			tv.setText(bean.getRed_tag());
//			tv.setVisibility(View.VISIBLE);
//		}else{
//			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//			tv.setVisibility(View.GONE);
//		}
		String tag = bean.getRed_tag();
//		String tag = bean.getCategory();
		if(!TextUtils.isEmpty(tag)){
			tv.setText(tag);
			tv.setVisibility(View.VISIBLE);
		}else{
			tv.setVisibility(View.GONE);
		}
	}
	
	public static String getNewMaxIdNoAd(ArrayList<MChannelItemBean> list){
		if(!Common.isListEmpty(list)){
			for(MChannelItemBean bean : list){
				if(!TextUtils.isEmpty(bean.getAd_place_id())){
//					continue;
				}else{
					return bean.getAid();
				}
			}
		}
		return null;
	}
	
	public void  setRefreshListener(OnClickListener listener) {
		this.refreshListener = listener;
	}

	public void setChannel(String channel) {
		this.mChannel = channel;
	}
	
	public void setDividerExclusive(boolean isDividerExclusive) {
		this.mIsDividerExclusive = isDividerExclusive;
	}
}
