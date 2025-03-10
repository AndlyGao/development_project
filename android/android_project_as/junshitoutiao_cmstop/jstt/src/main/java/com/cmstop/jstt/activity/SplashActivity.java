package com.cmstop.jstt.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.avos.avoscloud.AVInstallation;
import com.chengning.common.base.BaseActivity;
import com.chengning.common.update.UpdateUtil;
import com.chengning.common.util.HttpCacheControl;
import com.chengning.common.util.HttpCacheControl.CacheControlMaxAgeListener;
import com.chengning.common.util.HttpUtil;
import com.cmstop.jstt.App;
import com.cmstop.jstt.Const;
import com.cmstop.jstt.LoginManager;
import com.cmstop.jstt.MyStatusResponseHandler;
import com.cmstop.jstt.R;
import com.cmstop.jstt.ad.AdDataBaiduDummy;
import com.cmstop.jstt.beans.data.AdStartBean;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.utils.JUrl;
import com.cmstop.jstt.utils.SPHelper;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.http.Header;
import org.json.JSONObject;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int MSG_TIME = 1;

    private static final long TIME_DELAY = 1000;

    private static final int AD_SUCCESS = 2;

    private ImageView mImage;
    private ImageView mTimerImage;

    private int mIndex = 3;

    protected String adUrl;

    protected String imgUrl;

    protected String mall_url;

    protected String order_url;

    protected String mall_logout_url;

    protected String title;

    private DisplayImageOptions options;

    protected String defaultImage;

    protected boolean isAdDispaly = false;

    private boolean isShowing = false;

    private HttpCacheControl mCacheControl;
    private String video_url;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        super.onCreate(savedInstanceState);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void initViews() {
        mImage = (ImageView) findViewById(R.id.splash_image);
        mTimerImage = (ImageView) findViewById(R.id.splash_timer);
    }

    @Override
    public void initDatas() {
// 		getHandler().sendMessageDelayed(getHandler().obtainMessage(MSG_TIME), TIME_DELAY);

        String defaultSavedImage = SPHelper.getInst().getString(SPHelper.KEY_AD_START_IMAGE);
        if (TextUtils.isEmpty(defaultSavedImage)) {
            defaultSavedImage = "drawable://" + R.drawable.splash_pic;
            SPHelper.getInst().saveString(SPHelper.KEY_AD_START_IMAGE, defaultSavedImage);
        }
        defaultImage = defaultSavedImage;
        adUrl = SPHelper.getInst().getString(SPHelper.KEY_AD_START_URL);
        JUrl.SITE = SPHelper.getInst().getString(SPHelper.KEY_CACHE_SITE, JUrl.DEFAULT_SITE);

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.color.transparent)
                .showImageForEmptyUri(R.color.transparent)
                .showImageOnFail(R.color.transparent)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(200))
//		.postProcessor(new com.shenyuan.project.util.SyBitmapProcessor())
                .build();

        if (Common.hasNet()) {
            mCacheControl = new HttpCacheControl();
            mCacheControl.init(App.getInst(), JUrl.URL_CHANGE_SITE, new CacheControlMaxAgeListener() {

                @Override
                public void success() {
                    getStartAd();
                }

                @Override
                public void failure() {
                    getSite();
                }
            });
        } else {
            getHandler().obtainMessage(AD_SUCCESS, defaultImage).sendToTarget();
        }

    }

    /**
     * 获取域名
     */
    private void getSite() {
        HttpUtil.get(JUrl.URL_CHANGE_SITE, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                JUrl.SITE = response.optString("api_url");
                SPHelper.getInst().saveString(SPHelper.KEY_CACHE_SITE, JUrl.SITE);
                mCacheControl.saveCacheControlMaxAge(JUrl.URL_CHANGE_SITE, headers);
                LoginManager.getInst().updateCookie();
                getStartAd();
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                getStartAd();
                super.onFailure(statusCode, headers, responseString, throwable);
            }

        });
    }

    private void getStartAd() {
        AsyncHttpClient client = HttpUtil.getClient();
        client.setTimeout(3000);
        AVInstallation avInstallation = AVInstallation.getCurrentInstallation();
        RequestParams params = new RequestParams();
        params.put("type", "android");
        params.put("devicetoken", avInstallation.getInstallationId());
        params.put("objectid", avInstallation.getObjectId());
        params.put("version", UpdateUtil.getVersionName(getActivity()));
        params.put("channel", Common.getUmengChannel(getActivity()));

        client.post(JUrl.SITE + JUrl.URL_GET_AD_START, params, new MyStatusResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                getHandler().obtainMessage(AD_SUCCESS, defaultImage).sendToTarget();
            }

            @Override
            public void onDataSuccess(int status, String mod, String message,
                                      String data, JSONObject obj) {
                Gson gson = new Gson();
                AdStartBean bean = gson.fromJson(data, AdStartBean.class);

                SPHelper.getInst().saveString(SPHelper.KEY_GUIDE_DOWNLOAD_IMAGE_URL, bean.getBoot_image());
                SPHelper.getInst().saveString(SPHelper.KEY_GUIDE_DOWNLOAD_URL, bean.getDownload_url());
                SPHelper.getInst().saveInt(SPHelper.KEY_GUIDE_DOWNLOAD_SHOW, bean.getIs_boot());
                SPHelper.getInst().saveString(SPHelper.KEY_GUIDE_DOWNLOAD_NAME, bean.getAppid());
                SPHelper.getInst().saveString(SPHelper.KEY_GUIDE_DOWNLOAD_PACKAGE_NAME, bean.getApp_marking());
                SPHelper.getInst().saveString(SPHelper.KEY_DATEFORMAT, bean.getDateformat());
                SPHelper.getInst().saveString(SPHelper.KEY_BAIDU_AD_APPID, bean.getAd_appid());

                AdDataBaiduDummy.setAppSid(getActivity(), SPHelper.getInst().getString(SPHelper.KEY_BAIDU_AD_APPID, Const.BAIDU_AD_APPID_DEFAULT));

                //是否轮询查看最新文章个数
                SPHelper.getInst().saveInt(SPHelper.BADGE_KEY_NEW_ARTICLE_IS_POLLING, bean.getIs_polling());
                //轮询查看最新文章时间
                SPHelper.getInst().saveInt(SPHelper.BADGE_KEY_NEW_ARTICLE_POLLING_TIME, bean.getCheck_news_interval());

                adUrl = bean.getUrl();
                imgUrl = bean.getImage();
                mall_url = bean.getMall_url();
                video_url = bean.getVideo_url();
                order_url = bean.getOrder_url();
                mall_logout_url = bean.getMall_logout_url();
                String imageSaveStr = SPHelper.getInst().getString(SPHelper.KEY_AD_START_IMAGE);
                if (bean != null && !TextUtils.isEmpty(bean.getImage())
                        && !imageSaveStr.equals(bean.getImage())) {
                    // has change
                    SPHelper.getInst().saveString(SPHelper.KEY_AD_START_IMAGE, bean.getImage());
                    SPHelper.getInst().saveString(SPHelper.KEY_AD_START_URL, bean.getUrl());

                    defaultImage = bean.getImage();
//					getHandler().obtainMessage(AD_SUCCESS, defaultImage).sendToTarget();
                } else {
                    if (!TextUtils.isEmpty(imageSaveStr)) {
                        defaultImage = imageSaveStr;
//						Message msg = getHandler().obtainMessage(AD_SUCCESS, imageSaveStr);
//						msg.sendToTarget();
                    }
//					else{
//						Message msg = getHandler().obtainMessage(AD_SUCCESS, defaultImage);
//						msg.sendToTarget();
//					}
                }

                if (!TextUtils.isEmpty(video_url)) {
                    SPHelper.getInst().saveString(SPHelper.KEY_VIDEO_URL, video_url);
                }

                if (!TextUtils.isEmpty(mall_url)) {
                    SPHelper.getInst().saveString(SPHelper.KEY_MALL_URL, mall_url);
                }
                if (!TextUtils.isEmpty(order_url)) {
                    SPHelper.getInst().saveString(SPHelper.KEY_ORDER_URL, order_url);
                }
                if (!TextUtils.isEmpty(mall_logout_url)) {
                    SPHelper.getInst().saveString(SPHelper.KEY_MALL_LOGOUT_URL, mall_logout_url);
                }
                getHandler().obtainMessage(AD_SUCCESS, defaultImage).sendToTarget();
            }

            @Override
            public void onDataFailure(int status, String mod, String message,
                                      String data, JSONObject obj) {
                getHandler().obtainMessage(AD_SUCCESS, defaultImage).sendToTarget();
            }
        });
    }


    @Override
    public void installListeners() {
        mTimerImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startHomeActiivty();
            }
        });
        mImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(adUrl)) {
                    AdDetailActivity.launch(getActivity(), AdDetailActivity.TYPE_AD, adUrl, imgUrl);
                    finish();
                }

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        isShowing = true;
        getHandler().sendMessageDelayed(getHandler().obtainMessage(MSG_TIME), TIME_DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();
        isShowing = false;
        getHandler().removeMessages(MSG_TIME);
    }

    @Override
    public void processHandlerMessage(Message msg) {
        switch (msg.what) {
            case MSG_TIME:
                if (mIndex > 0) {
                    mTimerImage.setBackgroundResource(R.drawable.timer_bg);
                    switch (mIndex) {
                        case 1:
                            mTimerImage.setImageResource(R.drawable.timer_1);
                            break;
                        case 2:
                            mTimerImage.setImageResource(R.drawable.timer_2);
                            break;
                        case 3:
                            mTimerImage.setImageResource(R.drawable.timer_3);
                            break;
                        default:
                            break;
                    }
                    mIndex--;
                    getHandler().removeMessages(MSG_TIME);
                    getHandler().sendMessageDelayed(getHandler().obtainMessage(MSG_TIME), TIME_DELAY);
                } else {
                    startHomeActiivty();
                }
                break;
            case AD_SUCCESS:
                displayImage((String) msg.obj, mImage, options);

                AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(1000);
                anim.setInterpolator(new DecelerateInterpolator());
                mImage.startAnimation(anim);
                break;
            default:
                break;
        }
    }

    private void displayImage(String imageUrl, ImageView view, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage(imageUrl, view, options, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {

            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                isAdDispaly = true;
                if (isShowing) {
                    getHandler().sendMessage(getHandler().obtainMessage(MSG_TIME));
                }
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                isAdDispaly = true;
                if (isShowing) {
                    getHandler().sendMessage(getHandler().obtainMessage(MSG_TIME));
                }
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                isAdDispaly = true;
                if (isShowing) {
                    getHandler().sendMessage(getHandler().obtainMessage(MSG_TIME));
                }
            }
        });
    }

    public void startHomeActiivty() {
        getHandler().removeMessages(MSG_TIME);

        Intent intent = new Intent(SplashActivity.this, HomeSingleActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

}
