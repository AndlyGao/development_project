package com.chengning.yiqikantoutiao.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.chengning.common.base.util.BaseUmengShare;
import com.chengning.common.util.HttpUtil;
import com.chengning.yiqikantoutiao.Consts;
import com.chengning.yiqikantoutiao.MyJsonHttpResponseHandler;
import com.chengning.yiqikantoutiao.data.bean.ArticlesBean;
import com.chengning.yiqikantoutiao.data.bean.BaseArticlesBean;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class UmengShare extends BaseUmengShare {

	public static final int REQUEST_CODE_SHARE_MULTI_IMG = 100;
	private static UmengShare share;
	private String tid;
	private int mShareType;

	public static UmengShare getInstance(){
		if(share == null){
			share = new UmengShare();
		}
		return share;
	}

	public static String handleImageUrlWithBean(ArticlesBean bean){
		String imgUrl = bean.getImage_list();
		if(TextUtils.isEmpty(imgUrl)){
			imgUrl = handleImageUrl(bean.getImage_arr());
		}
		return imgUrl;
	}

	public static String handleImageUrl(ArrayList<String> imgUrls){
		String imgUrl = null;
		if(!Common.isListEmpty(imgUrls)){
			for(String s : imgUrls){
				if(!TextUtils.isEmpty(s)){
					imgUrl = s;
					break;
				}
			}
		}
		return imgUrl;
	}

	@Override
	public void handleEventAnalytics(Activity activity, String eventtype, BaseShareBean bean) {
//		ArticlesBean bean = (ArticlesBean)obj;
//		Map<String, String> mHashMap = new HashMap<String, String>();
//		mHashMap.put("type", eventtype);
//		mHashMap.put("article_id", bean.getTid());
//		MobclickAgent.onEvent(activity, "artilce_share", mHashMap);
	}

	@Override
	public BaseShareBean translateObjectToShareBean(Object... objs) {
		BaseShareBean bean = null;
		Object obj = objs[0];
		if (obj instanceof ArticlesBean) {
			bean = new BaseShareBean();
			ArticlesBean articlesBean = (ArticlesBean) obj;
			bean.setAid(articlesBean.getTid());
			bean.setTitle(articlesBean.getTitle());
			bean.setLink(articlesBean.getLink());
			bean.setImageUrl(handleImageUrlWithBean(articlesBean));
		}
		return bean;
	}

	/**
	 * 分享多张图片到朋友圈
	 * @param context
	 * @param title
	 * @param imgs
	 */
	public void shareMultiImgToWxq(Activity context, String title, ArrayList<String> imgs) throws Exception {
		Intent intent = new Intent();
		ComponentName comp = new ComponentName("com.tencent.mm",
				"com.tencent.mm.ui.tools.ShareToTimeLineUI");
		intent.setComponent(comp);
		intent.setAction(Intent.ACTION_SEND_MULTIPLE);
		intent.setType("image/*");
		intent.putExtra("Kdescription", title);
		ArrayList<Uri> imageUris = new ArrayList<Uri>();
		int length = imgs.size();
		for (int i=0; i< length; i++ ) {
			File file = Common.creatFile(JUrl.SHARE_IMG_FILE_PATH, "shareImg" + i +".png");
			file = Common.saveImageToSdCard(file, ImageLoader.getInstance().loadImageSync(imgs.get(i)));
			if (file != null) {
				imageUris.add(Uri.fromFile(file));
			}
		}
		if (Common.isListEmpty(imageUris)) {
			return;
		}
		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
		context.startActivity(intent);
	}

	public void setShareType(int type) {
		this.mShareType = type;
	}

	public int getShareType() {
		return mShareType;
	}

	@Override
	public void setAid(String aid) {
		setShareType(Consts.TASK_SHARE_ARTICLE);
		super.setAid(aid);
	}
}
