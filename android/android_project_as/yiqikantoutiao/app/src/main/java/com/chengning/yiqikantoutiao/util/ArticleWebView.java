package com.chengning.yiqikantoutiao.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chengning.common.util.DisplayUtil;
import com.chengning.yiqikantoutiao.App;
import com.chengning.yiqikantoutiao.Consts;
import com.chengning.yiqikantoutiao.R;
import com.chengning.yiqikantoutiao.SettingManager;
import com.chengning.yiqikantoutiao.SettingManager.FontSize;
import com.chengning.yiqikantoutiao.activity.AdDetailActivity;
import com.chengning.yiqikantoutiao.activity.PhotoPageArticleActivity;
import com.chengning.yiqikantoutiao.activity.UserInfoActivity;
import com.chengning.yiqikantoutiao.activity.VideoActivity;
import com.chengning.yiqikantoutiao.activity.VideoActivity.VideoData;
import com.chengning.yiqikantoutiao.data.bean.ArticlesBean;
import com.chengning.yiqikantoutiao.data.bean.BaseArticlesBean;
import com.chengning.yiqikantoutiao.data.bean.SubscribeContentItemBean;
import com.chengning.yiqikantoutiao.emotion.weibo.EmotionsDB;
import com.chengning.yiqikantoutiao.util.ImageCacheLoader.OnImageCacheListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleWebView<T> {
	
	// 模板
	public static final String FILE_TEMPLATE = "template_article_content.html";
	public static final String TEMPLATE_CONTENT = "TAG_TEMPLATE_CONTENT";
	public static final String TEMPLATE_CONFIG_FONT = "TAG_TEMPLATE_CONFIG_FONT";
	public static final String TEMPLATE_CONFIG_IS_ARTICLE = "TAG_TEMPLATE_CONFIG_IS_ARTICLE";
	public static final String TEMPLATE_CONFIG_NIGHT_MODE = "TAG_TEMPLATE_CONFIG_NIGHT_MODE";
	
	// day
	public static final String URL_IMAGE_DEFAULT = "chengning://img_default.jpg";
	public static final String URL_IMAGE_LOADING = "chengning://img_loading.jpg";
	public static final String URL_IMAGE_ERROR = "chengning://img_error.jpg";
	public static final String URL_IMAGE_DEFAULT_VIDEO = "chengning://img_default_video.jpg";
	// night
	public static final String URL_IMAGE_DEFAULT_NIGHT = "chengning://img_default_night.jpg";
	public static final String URL_IMAGE_LOADING_NIGHT = "chengning://img_loading_night.jpg";
	public static final String URL_IMAGE_ERROR_NIGHT = "chengning://img_error_night.jpg";
	public static final String URL_IMAGE_DEFAULT_VIDEO_NIGHT = "chengning://img_default_video_night.jpg";
	
	// html标签
	public static final String TAG_NEWLINE = "<br />";
	public static final String TAG_P_START = "<p>";
	public static final String TAG_P_END = "</p>";
	// img
	public static final String TAG_IMAGE_START = "<div class=\"cont_img\"><img src=\"" + URL_IMAGE_LOADING
			+ "\" src_default=\"" + URL_IMAGE_DEFAULT
			+ "\" src_loading=\"" + URL_IMAGE_LOADING
			+ "\" src_error=\"" + URL_IMAGE_ERROR
			+ "\" src_origin=\"";
	public static final String TAG_IMAGE_END = "\" noimage=\"0\" onclick=\"imgclick(this)\" onerror=\"imgerror(this)\" /></div>";

	public static final String TAG_NO_PIC_IMAGE_START = "<div class=\"cont_img\"><img src=\"" + URL_IMAGE_DEFAULT
			+ "\" src_default=\"" + URL_IMAGE_DEFAULT
			+ "\" src_loading=\"" + URL_IMAGE_LOADING
			+ "\" src_error=\"" + URL_IMAGE_ERROR
			+ "\" src_origin=\"";
	public static final String TAG_NO_PIC_IMAGE_END = "\" noimage=\"1\" onclick=\"noimgclick(this)\" onerror=\"imgerror(this)\" /></div>";
	// video img
	public static final String TAG_VIDEO_IMAGE_START = "<div class=\"cont_img\"><img src=\"" + URL_IMAGE_LOADING
			+ "\" src_default=\"" + URL_IMAGE_DEFAULT_VIDEO
			+ "\" src_loading=\"" + URL_IMAGE_LOADING
			+ "\" src_error=\"" + URL_IMAGE_DEFAULT_VIDEO
			+ "\" src_origin=\"";
	public static final String TAG_VIDEO_IMAGE_END = "\" noimage=\"0\" onclick=\"videoimgclick(this)\" onerror=\"imgerror(this)\" /></div>";
	public static final String TAG_NO_PIC_VIDEO_IMAGE_START = "<div class=\"cont_img\"><img src=\"" + URL_IMAGE_DEFAULT_VIDEO
			+ "\" src_default=\"" + URL_IMAGE_DEFAULT_VIDEO
			+ "\" src_loading=\"" + URL_IMAGE_LOADING
			+ "\" src_error=\"" + URL_IMAGE_DEFAULT_VIDEO
			+ "\" src_origin=\"";
	public static final String TAG_NO_PIC_VIDEO_IMAGE_END = "\" noimage=\"1\" onclick=\"videoimgclick(this)\" onerror=\"imgerror(this)\" /></div>";
	
	public static final String TAG_USERNAME_START = "<a onclick=\"userclick(this)\" class=\"userbtn\" username=\"";
	public static final String TAG_USERNAME_MIDDLE = "\">";
	public static final String TAG_USERNAME_END = "</a>";
	
	public static final String TAG_EMOTION_START = "<img class=\"emotion\" src=\"";
	public static final String TAG_EMOTION_END = "\" \" noimage=\"1\" /> ";
	
	public static final String TAG_SITE_START = "<a onclick=\"siteclick(this)\" class=\"userbtn\" site=\"";
	public static final String TAG_SITE_MIDDLE = "\">";
	public static final String TAG_SITE_END = "</a>";
	
	public static final String REG_EMOTION = "^(assets://emotions/).*(.png)$";
	
	public static final int REQUEST_PHOTO_PAGE_CODE = 1;
	
	public static final long TIME_OUT = 1000 * 10;

	private Activity mContext;
	private BaseArticlesBean<T> mBean;
	private OnWebViewListener mListener;
	private Handler mHandler;
	
	private WebView mWebView;
	private ArrayList<String> mImageUrls;
	private HashSet<String> mVideoImageUrls;
	private DisplayImageOptions mOptionsWithColorfilter;
	
	private boolean mIsPageFinishedCalled = false;

	private BaseArticlesBean<T> mChannelBean;

	private StringBuilder sb;

	private DisplayImageOptions mEmotionOptions;
	
	//1：文章  0：讨论
	private String isArticle = "1";
	private long imgHeight;
	private int errorImgHeight;

	public LinearLayout build(Activity context, BaseArticlesBean<T> bean, OnWebViewListener listener, Handler handler){
		mContext = context;
		mBean = bean;
		mListener = listener;
		mHandler = handler;
		mImageUrls = new ArrayList<String>();
		mVideoImageUrls = new HashSet<String>();
		errorImgHeight = mContext.getResources().getDimensionPixelSize(R.dimen.article_img_empty_height);
		
		LinearLayout linearLayout = new LinearLayout(context);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.topMargin = context.getResources().getDimensionPixelSize(R.dimen.common_horizontal_margin);
		linearLayout.setLayoutParams(lp);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		
		sb = new StringBuilder();
		
		// 视频
		if(!TextUtils.isEmpty(bean.getVideo_photo())){
			mVideoImageUrls.add(bean.getVideo_photo());

			if(Common.isTrue(SettingManager.getInst().getNoPicModel()) && !Common.hasImageCache(bean.getVideo_photo())){
				// 无图模式
				sb
				.append(TAG_NO_PIC_VIDEO_IMAGE_START)
				.append(replceImgDefaultWithNightBg(URL_IMAGE_DEFAULT_VIDEO))
				.append(TAG_NO_PIC_VIDEO_IMAGE_END);
			}else{
				sb
				.append(replceImgDefaultWithNightBg(TAG_VIDEO_IMAGE_START))
				.append(bean.getVideo_photo())
				.append(TAG_VIDEO_IMAGE_END);
			}
		}
		
		if (bean instanceof ArticlesBean) {
			isArticle = "1";
			handleArticle(((ArticlesBean)bean).getContent());
		}
		// 加空行，避免复制光标移动不下来
		sb.append(TAG_NEWLINE);
		
		
		// webview
		String contentStr = sb.toString();
		String template = getFromAssets(context, FILE_TEMPLATE);
		String webData = template;
		// 字体
		int fontConfig = 2;
		FontSize fontSize = FontSize.getFontSize(SettingManager.getInst().getFontSize());
		switch (fontSize) {
		case Small:
			fontConfig = 1;
			break;
		case Middle:
			fontConfig = 2;
			break;
		case Large:
			fontConfig = 3;
			break;
		case ExtraLarge:
			fontConfig = 4;
			break;
		default:
			break;
		}
		webData = webData.replace(TEMPLATE_CONFIG_FONT, String.valueOf(fontConfig));
		webData = webData.replace(TEMPLATE_CONFIG_IS_ARTICLE, isArticle);
		
		mEmotionOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)//设置下载的图片是否缓存在内存中  
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)  
		.preProcessor(new BitmapProcessor() {
			
			@Override
			public Bitmap process(Bitmap arg0) {
				return arg0;
			}
		})
		.build();
		
		// 夜间模式
		mOptionsWithColorfilter = new DisplayImageOptions.Builder() 
		.showStubImage(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.loading)
		.showImageOnFail(R.drawable.loading)
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.postProcessor(new BitmapProcessor() {
			
			@Override
			public Bitmap process(Bitmap bitmap) {
				return Common.isTrue(SettingManager.getInst().getNightModel()) ? bitmapWithColorfilter(bitmap) : bitmap;
			}
		})
		.build();
		int nightMode = Common.isTrue(SettingManager.getInst().getNightModel()) ? 2 : 1; 
		webData = webData.replace(TEMPLATE_CONFIG_NIGHT_MODE, String.valueOf(nightMode));
		
		// content
		webData = webData.replace(TEMPLATE_CONTENT, contentStr);

		mWebView = new WebView(context);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setVerticalScrollbarOverlay(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setHorizontalScrollbarOverlay(false);
		mWebView.setMinimumHeight(1000);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.setWebViewClient(new MyWebViewClient());
		
		WebSettings webSettings = mWebView.getSettings();
		
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setJavaScriptEnabled(true);
		// 设置无图模式
//		webSettings.setLoadsImagesAutomatically(!Common.isTrue(SettingManager.getInst().getNoPicModel()));
		// 阻止获取图片，onPageFinished后再加载
//		webSettings.setBlockNetworkImage(true);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setRenderPriority(RenderPriority.HIGH);

		// 处理超时
		mHandler.removeCallbacks(timeoutRunnable);
		mHandler.postDelayed(timeoutRunnable , TIME_OUT);

		mWebView.loadData(webData, "text/html", "utf-8");
		
		mWebView.addJavascriptInterface(new JavascriptInterface(context, bean), "imagelistner");
		
		linearLayout.removeAllViews();
		linearLayout.addView(mWebView);

		return linearLayout;
	}
	
	/**
	 * 处理文章
	 * @param contents
	 */
	private void handleArticle(ArrayList<SubscribeContentItemBean> contents) {
		for (SubscribeContentItemBean b : contents) {
			if(b.getType().equals("txt")) {
				handleContent(b.getContent());
			} else if(b.getType().equals("img")){
				handleImg(b.getSrc());
				imgHeight += b.getHeight() == 0 ? errorImgHeight : b.getHeight();
			}
		}
	}

	/**
	 * 图片处理
	 * @param image
	 */
	private void handleImg(String image) {
		if(!TextUtils.isEmpty(image)){
			mImageUrls.add(image);
			if (Common.isTrue(SettingManager.getInst().getNoPicModel()) && !Common.hasImageCache(image)) {
				// 无图模式
				sb
				.append(replceImgDefaultWithNightBg(TAG_NO_PIC_IMAGE_START))
				.append(image)
				.append(TAG_NO_PIC_IMAGE_END);
			} else {
				sb
					.append(replceImgDefaultWithNightBg(TAG_IMAGE_START))
					.append(image)
					.append(TAG_IMAGE_END);
			}
		}
	}

	/**
	 * 内容处理
	 * @param content
	 */
	private void handleContent(String content) {
		if(!TextUtils.isEmpty(content)){
			
			//用户名
			Matcher nameMatcher = Pattern.compile(Consts.NICKNAME_COMPILE_STR).matcher(content);
			while (nameMatcher.find()) {
				String key = nameMatcher.group(1);
				String value = nameMatcher.group(0);
				StringBuffer find = new StringBuffer();
				find.append(TAG_USERNAME_START).append(key)
					.append(TAG_USERNAME_MIDDLE).append(value).append(TAG_USERNAME_END);
				content = content.replace(value, find.toString());
			}
			
			//网址
			Matcher siteMatcher = Pattern.compile(Consts.SITE_COMPILE_STR).matcher(content);
			
			while (siteMatcher.find()) {
				String allValue = siteMatcher.group(0);
				String value = siteMatcher.group(1);
				StringBuffer find = new StringBuffer();
				find.append(TAG_SITE_START).append(value)
					.append(TAG_SITE_MIDDLE).append(value).append(TAG_SITE_END);
				content = content.replace(allValue, find.toString());
			}
			
			//表情
			Matcher emotionMatcher = Pattern.compile(Consts.EMOTION_COMPILE_STR).matcher(content);
			while (emotionMatcher.find()) {
				
				String key = emotionMatcher.group(0);
				key = key.replace("/", "");
				if (!TextUtils.isEmpty(EmotionsDB.getEmotionUri(key))) {
					StringBuffer find = new StringBuffer();
					find.append(TAG_EMOTION_START).append(EmotionsDB.getEmotionUri(key))
						.append(TAG_EMOTION_END);
					content = content.replace(key, find.toString());
				}
				
			}
			
			
			
			// 分段
			if(content.endsWith("\r\n")){
				content = content.substring(0, content.length() - 2);
			}
			String[] array = content.split("\n\n");
			for(String s : array){
				sb
				.append(TAG_P_START)
				.append(s.replace("\n", TAG_NEWLINE))
				.append(TAG_P_END);
			}
				
		}
	}

	public WebView getWebView(){
		return mWebView;
	}
	
	public void setChannelBean(BaseArticlesBean<T> bean) {
		this.mChannelBean = bean;
	}
	
	public void destroy(){
		try {
			mHandler.removeCallbacks(timeoutRunnable);
			mListener = null;
		    mWebView.removeAllViews();
		    mWebView.destroy();
		    mWebView = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Runnable timeoutRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(!mIsPageFinishedCalled){
				if(mListener != null){
					mListener.onTimeout();
				}
			}
		}
	};

	public ArrayList<SubscribeContentItemBean> contents;
	
	public void setFont(FontSize font, boolean isArticle){
		int fontConfig = 2;
		FontSize fontSize = FontSize.getFontSize(SettingManager.getInst().getFontSize());
		switch (fontSize) {
		case Small:
			fontConfig = 1;
			break;
		case Middle:
			fontConfig = 2;
			break;
		case Large:
			fontConfig = 3;
			break;
		case ExtraLarge:
			fontConfig = 4;
			break;
		default:
			break;
		}
		int isArticleConfig = isArticle ? 1 : 0;
		if(mWebView != null){
			mWebView.loadUrl("javascript:fontchange('" + fontConfig + "','" + isArticleConfig + "')");
		}
	}
	
	public void loadImage(){
		if(mWebView != null){
			mWebView.loadUrl("javascript:loadimage()");
		}
	}

	public long getImgHeight() {
		return imgHeight;
	}

	public class JavascriptInterface{
		private Activity context;
		private BaseArticlesBean<T> bean;

		public JavascriptInterface(Activity context, BaseArticlesBean<T> bean){
			this.context = context;
			this.bean = bean;
		}
		
		/**
		 * js图片点击
		 */
		@android.webkit.JavascriptInterface
		public void openImage(String image){
			int index = 0;
			index = mImageUrls.indexOf(image);
			
			Intent intent = new Intent(context, PhotoPageArticleActivity.class);
			intent.putExtra("index", index);
			intent.putExtra("articleBean", bean);
			intent.putExtra("channelBean", mChannelBean);
			intent.putExtra("imgList", mImageUrls);
		    context.startActivityForResult(intent, REQUEST_PHOTO_PAGE_CODE);
		}
		
		@android.webkit.JavascriptInterface
		public void openVideo(String src){
			// video html ：video标签poster属性可能与video_photo一样，导致图片缓存冲突，解决办法为新开进程
			VideoData video = new VideoData();
			video.setUseUrl(!TextUtils.isEmpty(mBean.getVideo_play()));
			video.setUrl(mBean.getVideo_play());
			video.setHtml("");
			VideoActivity.launch(context, video);
		}
		
		@android.webkit.JavascriptInterface
		public void openUser(String name){
			
			UserInfoActivity.launch(context, name);
		}
		
		@android.webkit.JavascriptInterface
		public void openSite(String site){
			
		  if (!site.matches("^(http[s]{0,1}|ftp)://.*")) {
			  site = "http://" + site;
          }
          AdDetailActivity.launch(context, AdDetailActivity.TYPE_ARTICLE, site, site);
		}
		
		@android.webkit.JavascriptInterface
		public void log(String msg){
			Log.d(ArticleWebView.class.getSimpleName(), msg);
		}
	}
	
	// 监听 
    private class MyWebViewClient extends WebViewClient {
    	
    	private ExecutorService executorService;
    	
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.matches("^(http[s]{0,1}|ftp)://.*")) {
            	url = "http://" + url;
            }
            AdDetailActivity.launch((Activity)view.getContext(), AdDetailActivity.TYPE_ARTICLE, url, url);
            return true;
        } 
        
		@SuppressLint("NewApi")
		@Override
        public WebResourceResponse shouldInterceptRequest(WebView view, final String url){
//			Log.d(TAG, "shouldInterceptRequest: " + url);
			
        	WebResourceResponse response = null;
        	final boolean isEmotion = url.matches(REG_EMOTION);
            if(mImageUrls.contains(url) 
            		|| mVideoImageUrls.contains(url) 
            		|| URL_IMAGE_DEFAULT.equals(url) 
            		|| URL_IMAGE_LOADING.equals(url) 
            		|| URL_IMAGE_ERROR.equals(url) 
            		|| URL_IMAGE_DEFAULT_VIDEO.equals(url)
            		|| URL_IMAGE_DEFAULT_NIGHT.equals(url) 
            		|| URL_IMAGE_LOADING_NIGHT.equals(url) 
            		|| URL_IMAGE_ERROR_NIGHT.equals(url) 
            		|| URL_IMAGE_DEFAULT_VIDEO_NIGHT.equals(url)
            		|| isEmotion
            		){
                try {
                    final PipedOutputStream out = new PipedOutputStream();
                    final PipedInputStream in = new PipedInputStream(out);
                    response = new WebResourceResponse("image/*", "utf-8", in);
                    
                    if(executorService == null){
                    	executorService = Executors.newCachedThreadPool();
                    }
                    
                    executorService.execute(new Runnable() {
						
						@Override
						public void run() {
							boolean isImageLoaderCache = false;
				        	boolean isVideo = mVideoImageUrls.contains(url);
							Bitmap bitmap = null;
				        	if(URL_IMAGE_DEFAULT.equals(url) || URL_IMAGE_DEFAULT_NIGHT.equals(url)){
				        		bitmap = ImageCacheLoader.getInst().loadImageSync(url, new OnImageCacheListener() {
									
									@Override
									public Bitmap getBitmap() {
										return getEmptyBitmap(R.layout.no_pic_press, R.id.no_pic_inside, R.id.no_pic_inside2);
									}
								});
				        	}else if(URL_IMAGE_LOADING.equals(url) || URL_IMAGE_LOADING_NIGHT.equals(url)){
				        		bitmap = ImageCacheLoader.getInst().loadImageSync(url, new OnImageCacheListener() {
									
									@Override
									public Bitmap getBitmap() {
										return getEmptyBitmap(R.layout.no_pic_loading, R.id.no_pic_inside, R.id.no_pic_inside2);
									}
								});
				        	}else if(URL_IMAGE_ERROR.equals(url) || URL_IMAGE_ERROR_NIGHT.equals(url)){
				        		bitmap = ImageCacheLoader.getInst().loadImageSync(url, new OnImageCacheListener() {
									
									@Override
									public Bitmap getBitmap() {
										return getEmptyBitmap(R.layout.no_pic_error, R.id.no_pic_inside, R.id.no_pic_inside2);
									}
								});
				        	}else if(URL_IMAGE_DEFAULT_VIDEO.equals(url) || URL_IMAGE_DEFAULT_VIDEO_NIGHT.equals(url)){
				        		bitmap = ImageCacheLoader.getInst().loadImageSync(url, new OnImageCacheListener() {
									
									@Override
									public Bitmap getBitmap() {
										return getEmptyBitmapBig(R.layout.no_pic_video, R.id.no_pic_inside, R.id.no_pic_inside2);
									}
								});
				        	} else if (isEmotion) {
				        		isImageLoaderCache = true;
				        		bitmap = ImageLoader.getInstance().loadImageSync(url, mEmotionOptions);
				        	} else{
				        		isImageLoaderCache = true;
				        		bitmap = ImageLoader.getInstance().loadImageSync(url, mOptionsWithColorfilter);
				        	}
//				        	ImageLoader.getInstance().getMemoryCache().put(arg0, arg1)
				        	if((bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) && isVideo){
				        		isImageLoaderCache = false;
				        		bitmap = ImageCacheLoader.getInst().loadImageSync(url, new OnImageCacheListener() {
									
									@Override
									public Bitmap getBitmap() {
										return getEmptyBitmapBig(R.layout.no_pic_video, R.id.no_pic_inside, R.id.no_pic_inside2);
									}
								});
				        	}
							
                            if (bitmap != null) {
                                try {
                                	if(isVideo){
                                		try {
                                			DisplayUtil.getInst().init(mContext);
                                    		int tenDp = mContext.getResources().getDimensionPixelSize(R.dimen.ten_dp);
                                    		int tarWidth = DisplayUtil.getInst().getScreenWidth() - tenDp * 2;
                                    		float scale = ((float)tarWidth) / ((float)bitmap.getWidth());
                                    		Bitmap bitmap2 = Common.bitmapWithImage(mContext, bitmap, R.drawable.video_play_icon, scale, 0);
                                    		bitmap = bitmap2;
            				        		isImageLoaderCache = false;
										} catch (Exception e) {
											e.printStackTrace();
										}
                                	}
                                	if (isEmotion) {
                                		out.write(Bitmap2BytesPng(bitmap));
                                	} else {
                                		out.write(Bitmap2BytesJpg(bitmap));
                                	}
                                    out.close();
                                    if(!bitmap.isRecycled() && !isImageLoaderCache){
        								bitmap.recycle();
        							}
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else{
                            	try {
									out.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
                            }
						}
					});
                }catch (Exception e){
                    e.printStackTrace();

                	response = super.shouldInterceptRequest(view, url);
                }
            }else{
            	response = super.shouldInterceptRequest(view, url);
            }
            return response;
        }
 
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mIsPageFinishedCalled = true;
            
            if(mListener != null){
            	mListener.onPageFinished();
            }
            
            if(mWebView != null){
        		mWebView.getSettings().setBlockNetworkImage(false);
            }
            loadImage();
        } 
 
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon); 
    		
            if(mListener != null){
            	mListener.onPageStarted();
            }
        } 
 
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl); 
        } 
    }
    
    private Bitmap getEmptyBitmap(int layoutId, int childId1, int childId2){
		View vL = View.inflate(mContext, layoutId, null);

		View v = vL.findViewById(childId1);
		LayoutParams lp = v.getLayoutParams();
		lp.width = DisplayUtil.getInst().getScreenWidth();
		lp.height = mContext.getResources().getDimensionPixelSize(R.dimen.article_img_empty_height);
		v.setLayoutParams(lp);
		v.setBackgroundResource(Common.isTrue(SettingManager.getInst().getNightModel()) ? 
				R.color.night_bg_color : R.color.common_bg);

		View v2 = vL.findViewById(childId2);
		LayoutParams lp2 = v2.getLayoutParams();
		lp2.width = mContext.getResources().getDimensionPixelSize(R.dimen.article_img_empty_width);
		lp2.height = mContext.getResources().getDimensionPixelSize(R.dimen.article_img_empty_height);
		v2.setLayoutParams(lp2);
		v2.setBackgroundResource(R.color.article_img_empty_bg);
		
		Bitmap bitmap = Common.getViewBitmap_ARGB8888(v, lp.width, lp.height);
    	return bitmap;
    }
    
    private Bitmap getEmptyBitmapBig(int layoutId, int childId1, int childId2){
		View vL = View.inflate(mContext, layoutId, null);

		View v = vL.findViewById(childId1);
		LayoutParams lp = v.getLayoutParams();
		lp.width = DisplayUtil.getInst().getScreenWidth();
		lp.height = mContext.getResources().getDimensionPixelSize(R.dimen.article_img_empty_high_height);
		v.setLayoutParams(lp);
		
		Bitmap bitmap = Common.getViewBitmap_ARGB8888(v, lp.width, lp.height);
    	return bitmap;
    }
	
	public byte[] Bitmap2BytesPng(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	public byte[] Bitmap2BytesJpg(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		return baos.toByteArray();
	}
	
	public String getFromAssets(Context context, String fileName){
		try {
        	InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
		} catch (Exception e) {
			e.printStackTrace(); 
		}
       return "";
    }
	
	public Bitmap bitmapWithColorfilter(Bitmap bitmap) {
		if(bitmap == null){
			return null;
		}
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();    

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        PorterDuffColorFilter f = new PorterDuffColorFilter(App.getInst().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
        return bmpGrayscale;
    }
	
	private String replceImgDefaultWithNightBg(String str){
		if(TextUtils.isEmpty(str)){
			return str;
		}
		if(Common.isTrue(SettingManager.getInst().getNightModel())){
			return str.replace(URL_IMAGE_DEFAULT, URL_IMAGE_DEFAULT_NIGHT)
					.replace(URL_IMAGE_LOADING, URL_IMAGE_LOADING_NIGHT)
					.replace(URL_IMAGE_ERROR, URL_IMAGE_ERROR_NIGHT)
					.replace(URL_IMAGE_DEFAULT_VIDEO, URL_IMAGE_DEFAULT_VIDEO_NIGHT);
		}else{
			return str;
		}
	}
	
	public static interface OnWebViewListener{
		void onPageStarted();
		void onPageFinished();
		void onTimeout();
    }

}
