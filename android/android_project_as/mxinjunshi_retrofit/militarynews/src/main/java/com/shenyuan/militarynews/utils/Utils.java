package com.shenyuan.militarynews.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chengning.common.base.util.GlideHelper;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.activity.AdDetailActivity;
import com.shenyuan.militarynews.activity.ArticleActivity;
import com.shenyuan.militarynews.activity.PhotoPageActivity;
import com.shenyuan.militarynews.activity.VideoDetailActivity;
import com.shenyuan.militarynews.beans.data.MChannelItemBean;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class Utils {
	
	final static int BUFFER_SIZE = 4096;

	/**
	 * 设置图片为圆形
	 * @param imageId （int类型）
	 * @param image
	 * imageId = R.drawable.image // from drawables (only images, non-9patch) 
	 *  
	 */
	public static void setCircleImage(Activity context, int imageId,ImageView image){
		if (GlideHelper.isInvalidContext(context)) {
			return;
		}
		RequestOptions options = new RequestOptions();
		GlideHelper.getInst().calculateImageWH(options,image)
				.transform(GlideHelper.getInst().creatTransform(context, GlideHelper.GlideImageType.circled));
		Glide.with(context).load(imageId)
                .apply(options)
                .into(image);
	}
	
	/**
	 * 设置图片为圆形
	 * @param imageUri （String 类型）
	 * @param image
	 * imageUri = "http://site.com/image.png"; // from Web 
	 * imageUri = "file:///mnt/sdcard/image.png"; // from SD card
	 * imageUri = "content://media/external/audio/albumart/13"; // from content provider
	 * imageUri = "assets://image.png"; // from assets 
	 *  
	 */ 
	public static void setCircleImage(Activity context, String imageUri, ImageView image){
		if (GlideHelper.isInvalidContext(context)) {
			return;
		}
		RequestOptions options = new RequestOptions();
		GlideHelper.getInst().calculateImageWH(options,image)
				.transform(GlideHelper.getInst().creatTransform(context, GlideHelper.GlideImageType.circled));
		Glide.with(context).load(imageUri)
				.apply(options)
				.into(image);
	}
	
	public static void showFace(Activity context, String imageUri, ImageView image){
		if(!TextUtils.isEmpty(imageUri)){
			Utils.setCircleImage(context,imageUri, image);
		}else{
			Utils.setCircleImage(context, R.drawable.person_head_portrait_not_log, image);
		}
		
	}
	
	/**  
     *  
     * @param in InputStream  
     * @return String  
     * @throws Exception  
     *   
     */  
    public static String InputStreamTOString(InputStream in) throws Exception{  
          
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] data = new byte[BUFFER_SIZE];  
        int count = -1;  
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
            outStream.write(data, 0, count);  
        data = null;  
        return new String(outStream.toByteArray(),"UTF-8");  
    }  
      
    /**  
     * InputStream转化为String  
     * @param in  
     * @param encoding
     * @return  
     * @throws Exception  
     */  
    public static String InputStreamTOString(InputStream in,String encoding) throws Exception{  
          
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] data = new byte[BUFFER_SIZE];  
        int count = -1;  
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
            outStream.write(data, 0, count);  
          
        data = null;  
        return new String(outStream.toByteArray(),"ISO-8859-1");  
    }
    
    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
    
    /**
     * 判断是否是同一天
     * @param ms1
     * @param ms2
     * @return
     */
    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }
    
    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

	/**
	* 隐藏软键盘
	* @return
	*/
	@SuppressWarnings("static-access")
	public static void hideIme(Activity context) {
		if (context == null)
			return;
		final View v = context.getWindow().peekDecorView();
		if (v != null && v.getWindowToken() != null) {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}
	
	/**
	 * 判断应用是否已安装
	 * 
	 * @param context
	 * @param packageName 包名
	 * @return
	 */ 
	public static boolean isInstalled(Context context, String packageName) { 
	    boolean hasInstalled = false; 
	    PackageManager pm = context.getPackageManager(); 
	    List<PackageInfo> list = pm 
	            .getInstalledPackages(PackageManager.PERMISSION_GRANTED); 
	    for (PackageInfo p : list) { 
	        if (packageName != null && packageName.equals(p.packageName)) { 
	            hasInstalled = true; 
	            break; 
	        } 
	    } 
	    return hasInstalled; 
	}
	/**
	 * 判断是收藏状态
	 */
	public static boolean isFavorite(String state){
		if(state.equals("1")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
     * 返回载入中界面
     * @param activity
     * @param parent
     * @param rootId
     */
	public static View addProssBar(Activity activity,View parent, int rootId){ 
			View wait = parent.inflate(parent.getContext(), R.layout.wait, null);
			RelativeLayout	root = (RelativeLayout) parent.findViewById(rootId); 
			root.addView(wait);
			return wait;
	}
	public static void removeProssBar(View parent, int rootId){
		RelativeLayout root = (RelativeLayout) parent.findViewById(rootId);
		View v = parent.findViewById(R.id.waitLayout_root);
		if(v!=null)
			root.removeView(v);
	}
	
	/**
	 * touch 刷新的view
	 * @param activity
	 * @param parent
	 * @param rootId
	 * @return
	 */
	public static View addTouchPhoto(Activity activity,View parent, int rootId){
		
		View wait = parent.inflate(activity, R.layout.touch_refresh, null);
		RelativeLayout root = (RelativeLayout) parent.findViewById(rootId);
		root.addView(wait);
		return wait;
	}
	public static void removeTouchPhoto(View parent, int rootId){
		RelativeLayout root = (RelativeLayout) parent.findViewById(rootId);
		View v = parent.findViewById(R.id.touch_refresh_root);
		if(v!=null)
			root.removeView(v);
	}
	
	public static void handleBeanClick(Activity activity, MChannelItemBean bean, String area) {
		if(Const.CHANNEL_ARTICLE_TUWEN.equals(bean.getChannel())){
			PhotoPageActivity.launch(activity, bean, area);
		}else if(Const.CHANNEL_ARTICLE_WEB.equals(bean.getChannel())){
			// 网页类型文章跳转
//			AdDetailActivity.launch(getContext(), AdDetailActivity.TYPE_ARTICLE, bean.getTitle(), bean.getRedirecturl(), bean.getImage());
			AdDetailActivity.launch(activity, AdDetailActivity.TYPE_ARTICLE, bean.getRedirecturl(), bean.getImage());
		} else if (TextUtils.equals(Const.CHANNEL_ARTICLE_VIDEO, bean.getChannel())) {
			VideoDetailActivity.launch(activity, bean, area);
		} else {
			ArticleActivity.launch(activity, bean, area);
		}
	}
	public static void handleBeanClick(Activity activity, MChannelItemBean bean) {
		handleBeanClick(activity, bean, "normal");
	}

}
