package com.chengning.fenghuo.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengning.common.base.BaseTypeListAdapter;
import com.chengning.common.base.BaseViewHolder;
import com.chengning.common.util.DisplayUtil;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.Consts;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.SettingManager;
import com.chengning.fenghuo.data.bean.BaseChannelItemBean;
import com.chengning.fenghuo.data.bean.ChannelItemBean;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.MyRoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChannelItemDailyAdapter extends BaseTypeListAdapter{

	private Activity mContext;
	private DisplayImageOptions mOptions;
	private int mImageThreeSmallWidth;
	private int mImageThreeSmallHeight;
	
	public ChannelItemDailyAdapter(Activity activity, List list) {
		super(activity, list);
		this.mContext = activity;
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
				.displayer(new MyRoundedBitmapDisplayer(App.getInst().getResources().getDimensionPixelSize(R.dimen.common_round_corner)))
				.build();

 		DisplayUtil.getInst().init(activity);
 		int gapWidth = activity.getResources().getDimensionPixelSize(R.dimen.common_horizontal_margin);
 		
 		mImageThreeSmallWidth = (DisplayUtil.getInst().getScreenWidth() - gapWidth * 4) / 3 ;
 		mImageThreeSmallHeight = activity.getResources().getDimensionPixelSize(R.dimen.channel_item_image_three_height);
	}
	public static enum ChannelItemType {
		OneSmall(0,Consts.SHOW_TYPE_ONE_SMALL),
		OneBigOfDailyVideo(1,Consts.SHOW_TYPE_ONE_BIG_VIDEO),
		ThreeSmall(2,Consts.SHOW_TYPE_THREE_SMALL),
		OneBigOfDaily(3,Consts.SHOW_TYPE_ONE_BIG),
		;
		private int type;
		private int showType[];
		
		private ChannelItemType(int type,int... showType){
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
		public static ChannelItemType parseItemType(int showType){
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
		if(Common.isTrue(SettingManager.getInst().getNoPicModel())){
			// 无图模式固定样式
			return ChannelItemType.OneSmall.getType();
		}else{
			ChannelItemBean bean = (ChannelItemBean)getItem(position);
			return ChannelItemType.parseItemType(bean.getShow_type()).getType();
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
	public int buildLayoutId(int position, int type) {
		return -1;
	}
	
	@Override
	public View buildLayoutView(int position,int type){
		View view = View.inflate(getContext(), R.layout.item_channel_item_container_daily, null);
		ViewStub stub = (ViewStub) view.findViewById(R.id.item_channel_item_stub);
		ChannelItemType itemType = ChannelItemType.parseType(type);
		switch(itemType){
			 default:
			 case OneSmall:
				 stub.setLayoutResource(R.layout.item_channel_item_one_small);
				 break;
			 case OneBigOfDailyVideo:
				 stub.setLayoutResource(R.layout.item_channel_item_one_big_daily_video);
				 break;
			 case ThreeSmall:
				 stub.setLayoutResource(R.layout.item_channel_item_three_small);
				 break;
			 case OneBigOfDaily:
				 stub.setLayoutResource(R.layout.item_channel_item_one_big_daily_video);
				 break;
		}
		stub.inflate();
		return view;
		
	}
	@Override
	public void handleLayout(View convertView, final int position, Object obj,
			int type) {
		ChannelItemType itemType = ChannelItemType.parseType(type);
		ChannelItemBean bean = (ChannelItemBean) obj;
		View contentLayout = BaseViewHolder.get(convertView, R.id.item_channel_item_content_layout);
		contentLayout.setVisibility(View.VISIBLE);
		
		TextView headerText;
		TextView title = null;
		ImageView image;
		TextView tag;
		TextView time;
		TextView author;
		TextView comment;
		ImageView image2 = null;
		ImageView image3 = null;
		ImageView face = null;
		
		View currentLayout = convertView;
		headerText = BaseViewHolder.get(convertView, R.id.item_channel_item_extend_header_text);
		
		if(isPart(bean)){
			headerText.setVisibility(View.VISIBLE);
			headerText.setText(formatIsPartTime(bean.getDateline()));
		}else{
			headerText.setVisibility(View.GONE);
		}
		switch(itemType){
			default:
			case OneSmall:
				break;
			case OneBigOfDailyVideo:
//				title = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title);
				TextView titleDailyVideo = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_daily_video);
//				titleDailyVidep.setText(bean.getTitle());
				
				String spTitle = "span " + bean.getTitle();
				SpannableString spannable = new SpannableString(spTitle);
				Drawable drawable = getContext().getResources().getDrawable(R.drawable.tuijian_ribao_video_icon);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
				spannable.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				titleDailyVideo.setText(spannable);
				
				View dailyHeaderLayoutVideo = BaseViewHolder.get(currentLayout, R.id.item_channel_item_extend_header_layout);
				dailyHeaderLayoutVideo.setOnClickListener(null);
				break;
			case ThreeSmall:
//				title = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_three_small);
				image2 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image2);
				image3 = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image3);
				break;
			case OneBigOfDaily:
				TextView titleDaily = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title_daily_video);
				
				titleDaily.setText(bean.getTitle());
				View dailyHeaderLayout = BaseViewHolder.get(currentLayout, R.id.item_channel_item_extend_header_layout);
				dailyHeaderLayout.setOnClickListener(null);
				break;
		}
		title = BaseViewHolder.get(currentLayout, R.id.item_channel_item_title);
		image = BaseViewHolder.get(currentLayout, R.id.item_channel_item_image);
		tag = BaseViewHolder.get(currentLayout, R.id.item_channel_item_tag);
	    face = BaseViewHolder.get(convertView, R.id.item_channel_item_face);
		time = BaseViewHolder.get(currentLayout, R.id.item_channel_item_time);
		author = BaseViewHolder.get(currentLayout, R.id.item_channel_item_author);
		comment = BaseViewHolder.get(currentLayout, R.id.item_channel_item_comment);
		
		if(!Common.isTrue(SettingManager.getInst().getNoPicModel())){
			switch(itemType){
				default:
				case OneSmall:
				case OneBigOfDailyVideo:
					if(!TextUtils.isEmpty(((ChannelItemBean) bean).getImage_list())){
						image.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(bean.getImage_list(), image,getDisplayOptions(bean));
					}else{
						ImageLoader.getInstance().cancelDisplayTask(image);
						image.setVisibility(View.GONE);
					}
					if(Common.isTrue(SettingManager.getInst().getNightModel())){
						image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color),PorterDuff.Mode.MULTIPLY);						
					}
					break;
				case ThreeSmall:
					updateSize(image,mImageThreeSmallWidth,mImageThreeSmallHeight);
					updateSize(image2,mImageThreeSmallWidth,mImageThreeSmallHeight);
					updateSize(image3,mImageThreeSmallWidth,mImageThreeSmallHeight);
					if(!Common.isListEmpty(bean.getImage_arr()) && bean.getImage_arr().size() > 2){
						image.setVisibility(View.VISIBLE);
						image2.setVisibility(View.VISIBLE);
						image3.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(bean.getImage_arr().get(0), image ,getDisplayOptions(bean));
						ImageLoader.getInstance().displayImage(bean.getImage_arr().get(1), image2 ,getDisplayOptions(bean));
						ImageLoader.getInstance().displayImage(bean.getImage_arr().get(2), image3 ,getDisplayOptions(bean));
						if(Common.isTrue(SettingManager.getInst().getNightModel())){
							image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color));
							image2.setColorFilter(getContext().getResources().getColor(R.color.night_img_color));
							image3.setColorFilter(getContext().getResources().getColor(R.color.night_img_color));
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
				case OneBigOfDaily:
					if(!TextUtils.isEmpty(((ChannelItemBean) bean).getImage_list())){
						image.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(bean.getImage_list(), image,getDisplayOptions(bean));
					}else{
						ImageLoader.getInstance().cancelDisplayTask(image);
						image.setVisibility(View.GONE);
					}
					if(Common.isTrue(SettingManager.getInst().getNightModel())){
						image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color),PorterDuff.Mode.MULTIPLY);						
					}
					break;
			}
		}else{
			ImageLoader.getInstance().cancelDisplayTask(image);
			if(image != null){
				image.setVisibility(View.GONE);
			}
			if(itemType == ChannelItemType.ThreeSmall){
				image2.setVisibility(View.GONE);
				image3.setVisibility(View.GONE);
			}
		}
		
		title.setText(bean.getTitle());
		
		// item
		if(!TextUtils.isEmpty(bean.getCh_name())){
			// 独家tag使用橙色背景
			tag.setBackgroundResource("独家".equals(bean.getCh_name()) 
					? R.color.item_channel_tag_orange 
							: R.color.item_channel_tag);
			
			tag.setText(bean.getCh_name());
			tag.setVisibility(View.VISIBLE);
			
			time.setVisibility(View.GONE);
		}else{
			tag.setVisibility(View.GONE);
			
			time.setText(Common.getDateMMDDNotNull(bean.getDateline()));
			time.setVisibility(View.VISIBLE);
		}
		
		face.setVisibility(View.GONE);
		
		if(itemType == ChannelItemType.OneBigOfDailyVideo){
			tag.setVisibility(View.GONE);
			face.setVisibility(View.GONE);
			time.setVisibility(View.GONE);
		}
		
		author.setText(bean.getNickname());
		comment.setText(bean.getReplys()+"");
		contentLayout.setTag(bean);
		
		title.setTag(bean);
	}
	private void updateSize(View v, int width, int height){
		LayoutParams lp = v.getLayoutParams();
		lp.width = width;
		lp.height = height;
		v.setLayoutParams(lp);
	}
	public DisplayImageOptions getDisplayOptions(BaseChannelItemBean  mBean){
		return mOptions;
	}
	
	private boolean isPart(BaseChannelItemBean bean){
		if(bean instanceof ChannelItemBean
				&& (Common.isTrue(((ChannelItemBean)bean).getIs_part()))){
			// 表示为分开的项
			return true;
		}else{
			return false;
		}
	}
	
	private String formatIsPartTime(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// 2016年2月3日 13:50
			Date date = sdf.parse(dateStr);
			return new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(date);
			
		} catch (ParseException e) {
			e.printStackTrace();
			return dateStr;
		}
	}
}
