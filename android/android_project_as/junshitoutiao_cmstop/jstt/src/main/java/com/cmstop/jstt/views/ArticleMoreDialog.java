package com.cmstop.jstt.views;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.chengning.common.base.BaseDialogFragment;
import com.chengning.common.base.util.BaseUmengShare.BaseUMShareListener;
import com.chengning.common.util.BrightnessTools;
import com.cmstop.jstt.R;
import com.cmstop.jstt.SettingManager;
import com.cmstop.jstt.SettingManager.FontSize;
import com.cmstop.jstt.beans.data.ArticlesBean;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.utils.UmengShare;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class ArticleMoreDialog extends BaseDialogFragment {
	
	private static final int FONT_SMALL = 0;
	private static final int FONT_MIDDLE = 1;
	private static final int FONT_LARGE = 2;
	private static final int FONT_EXTRA_LARGE = 3;
	
	private View mSetting;

	private View mTop;
	private View mShareWeixin;
	private View mSharePyq;
	private View mShareWeibo;
	private View mShareQQ;
	private View mCancelLayout;
	private View mNightModeBtn;
	private View mFontSettingBtn;
	private View mCancel;
	private View mBackLayout;
	private SeekBar mLightSeekBar;
	private SeekBar mFontSeekBar;
	private View mFontSmall;
	private View mFontMiddle;
	private View mFontLarge;
	private View mFontExtraLarge;
	private View mBack;
	
	private OnArticleSettingListener mListener;
	
	private BrightnessTools mBrightnessTools;
	
	private ArticlesBean mArticleBean;

	public void setBean(ArticlesBean articleBean){
		Bundle arg = new Bundle();
		arg.putSerializable("articleBean", articleBean);
		setArguments(arg);
	}

	@Override
	public View configContentView() {
		mSetting = LayoutInflater.from(getContext()).inflate(R.layout.dialog_article_more, null);
		return mSetting;
	}

	@Override
	public void initListener() {
		mTop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		// share
		mShareWeixin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (null != mArticleBean) {
					UmengShare.getInstance().shareToWeixin(getActivity(),
							UmengShare.getInstance().translateObjectToShareBean(mArticleBean));
				}
			}
		});
		mSharePyq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != mArticleBean) {
					UmengShare.getInstance().shareToCircle(getActivity(),
							UmengShare.getInstance().translateObjectToShareBean(mArticleBean));
				}
			}
		});
		mShareWeibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != mArticleBean) {
					UmengShare.getInstance().shareToWeibo(getActivity(), 
							new BaseUMShareListener(getActivity()) {
					    @Override
					    public void onResult(SHARE_MEDIA platform) {
//					    	if (App.getInst().isLogin()) {
//								UmengShare.getInstance()
//										.getPointAndMoneyByWeibo(
//												getActivity(), mArticleBean);
//							}
					    	super.onResult(platform);
					    }

					}, UmengShare.getInstance().translateObjectToShareBean(mArticleBean));
				}
			}
		});
		mShareQQ.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != mArticleBean) {
					UmengShare.getInstance().shareToQq(getActivity(),
							UmengShare.getInstance().translateObjectToShareBean(mArticleBean));
				}
			}
		});
		// cancel layout
		mNightModeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean nightMode = Common.isTrue(SettingManager.getInst().getNightModel());
				nightMode = !nightMode;
				mNightModeBtn.setSelected(nightMode);
				mListener.onNightModeChange(nightMode);
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		// back layout
		mFontSettingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCancelLayout.setVisibility(View.GONE);
				mBackLayout.setVisibility(View.VISIBLE);
				handleEventAnalyticsFontSetting(getActivity());
			}
		});
		mLightSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mBrightnessTools.setScreenMode(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
				mBrightnessTools.setScreenBrightness(progress);
				mBrightnessTools.saveScreenBrightness(progress);
			}
		});
		mFontSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				FontSize font = null;
				switch (progress) {
				case FONT_SMALL:
					font = FontSize.Small;
					break;
				default:
				case FONT_MIDDLE:
					font = FontSize.Middle;
					break;
				case FONT_LARGE:
					font = FontSize.Large;
					break;
				case FONT_EXTRA_LARGE:
					font = FontSize.ExtraLarge;
					break;
				}
				saveFont(font);
				setFontView(font);
				mListener.onFontChange(font);
			}
		});
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mBackLayout.setVisibility(View.GONE);
				mCancelLayout.setVisibility(View.VISIBLE);
			}
		});
	}

	/**
	 * 事件统计(设置字体)
	 * @param mContext
	 */
	protected void handleEventAnalyticsFontSetting(Activity mContext) {
		MobclickAgent.onEvent(mContext, "article_fontsetting");
	}

	@Override
	public void initView() {
		mTop = mSetting.findViewById(R.id.article_more_top);
		mShareWeixin = mSetting.findViewById(R.id.article_more_share_weixin);
		mSharePyq = mSetting.findViewById(R.id.article_more_share_pyq);
		mShareWeibo = mSetting.findViewById(R.id.article_more_share_weibo);
		mShareQQ = mSetting.findViewById(R.id.article_more_share_qq);
		mCancelLayout = mSetting.findViewById(R.id.article_more_cancel_layout);
		mNightModeBtn = mSetting.findViewById(R.id.article_more_night_mode);
		mFontSettingBtn = mSetting.findViewById(R.id.article_more_font_setting);
		mCancel = mSetting.findViewById(R.id.article_more_cancel);
		mBackLayout = mSetting.findViewById(R.id.article_more_back_layout);
		mLightSeekBar = (SeekBar) mSetting.findViewById(R.id.article_more_light_seekbar);
		mFontSeekBar = (SeekBar) mSetting.findViewById(R.id.article_more_font_seekbar);
		mFontSmall = mSetting.findViewById(R.id.article_more_font_small);
		mFontMiddle = mSetting.findViewById(R.id.article_more_font_middle);
		mFontLarge = mSetting.findViewById(R.id.article_more_font_large);
		mFontExtraLarge = mSetting.findViewById(R.id.article_more_font_extra_large);
		mBack = mSetting.findViewById(R.id.article_more_back);
	}

	@Override
	public void initData() {
		Bundle arg = getArguments();
		mArticleBean = (ArticlesBean) arg.getSerializable("articleBean");
		
		mCancelLayout.setVisibility(View.VISIBLE);
		mBackLayout.setVisibility(View.GONE);
		
		// 夜间模式
		mNightModeBtn.setSelected(Common.isTrue(SettingManager.getInst().getNightModel()));
		
		mBrightnessTools = new BrightnessTools(getContext(), getContext().getContentResolver());
		mLightSeekBar.setProgress(mBrightnessTools.getScreenBrightness());
		
		readFont();
	}
	
	public void shouldRefreshUI(){
		if(mSetting != null){
			mSetting.postInvalidate();
		}
	}
	
	public void setListener(OnArticleSettingListener listener){
		this.mListener = listener;
	}
	
	private void readFont(){
		int size = SettingManager.getInst().getFontSize();
		FontSize font = FontSize.getFontSize(size);

		setFontSeekBar(font);
		setFontView(font);
	}
	
	private void saveFont(FontSize font){
		SettingManager.getInst().saveFontSize(font.getSize());
	}
	
	public void setFontSeekBar(FontSize font){
		switch (font) {
		case Small:
			mFontSeekBar.setProgress(FONT_SMALL);
			break;
		case Middle:
			mFontSeekBar.setProgress(FONT_MIDDLE);
			break;
		case Large:
			mFontSeekBar.setProgress(FONT_LARGE);
			break;
		case ExtraLarge:
			mFontSeekBar.setProgress(FONT_EXTRA_LARGE);
			break;
		}
	}
	
	public void setFontView(FontSize font){
		mFontSmall.setVisibility(View.INVISIBLE);
		mFontMiddle.setVisibility(View.INVISIBLE);
		mFontLarge.setVisibility(View.INVISIBLE);
		mFontExtraLarge.setVisibility(View.INVISIBLE);
		
		switch (font) {
		case Small:
			mFontSmall.setVisibility(View.VISIBLE);
			break;
		case Middle:
			mFontMiddle.setVisibility(View.VISIBLE);
			break;
		case Large:
			mFontLarge.setVisibility(View.VISIBLE);
			break;
		case ExtraLarge:
			mFontExtraLarge.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	public static interface OnArticleSettingListener{
		public void onFontChange(FontSize font);
		public void onNightModeChange(boolean mode);
	}
}
