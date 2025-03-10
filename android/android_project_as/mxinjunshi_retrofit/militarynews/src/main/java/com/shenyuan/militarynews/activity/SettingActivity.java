package com.shenyuan.militarynews.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.bumptech.glide.Glide;
import com.chengning.common.app.ActivityInfo.ActivityState;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.util.GlideHelper;
import com.chengning.common.update.UpdateVersionUtil;
import com.chengning.common.util.StatusBarUtil;
import com.chengning.common.util.ThreadHelper;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.SettingManager.FontSize;
import com.shenyuan.militarynews.beans.data.SettingBean;
import com.shenyuan.militarynews.data.access.LocalStateServer;
import com.shenyuan.militarynews.service.OfflineDownloadService.OfflineDownloadState;
import com.shenyuan.militarynews.service.OfflineDownloadService.OnOfflineDownloadListener;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.SPHelper;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.views.SettingFontDialog;
import com.shenyuan.militarynews.views.SettingFontDialog.SettingFontDialogItemClick;
import com.shenyuan.militarynews.views.SettingOfflineProgress;
import com.shenyuan.militarynews.views.SettingOfflineProgressCancleDialog;
import com.shenyuan.militarynews.views.SettingOfflineProgressWarningDialog;
import com.shenyuan.militarynews.views.SwitchButton;
import com.shenyuan.militarynews.views.TitleBar;
import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

;

public class SettingActivity extends BaseFragmentActivity implements SettingOfflineProgressWarningDialog.OnSOPWListener, SettingOfflineProgressCancleDialog.OnSOPCListener{

	private static final String TAG = SettingActivity.class.getSimpleName();

	private static final int SET_CACHE_SUCCESS = 0;
	private static final int SET_SUCCESS = 1;

	private TitleBar mTitleBar;
	private View mLlAccount;
	private View mFont;
	private TextView mFontText;
	private View mCache;
	private TextView mCacheText;
	private View mAccountInfo;
	private TextView mTvModifyPhone;
	private View mAccountBind;
	private SwitchButton mPushSwitch;
	private SwitchButton mNightModeSwitch;
	private SwitchButton mNoImageModeSwitch;
	private View mOffline;
	private View mAboutMe;
	private View mUpdate;
	private View mFeedback;
	
	private App app;
	// 离线阅读进度
	private SettingOfflineProgress mOfflineProgress;

//	private int REQUESTCODE_MODIFYPHONE= 0x00;

	private HandlerThread mSettingThread;

	private boolean mPushState;

	private boolean mNightState;

	private boolean mNoImageState;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_setting); 
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.normalstate_bg), true);
        }
		super.onCreate(savedInstanceState);
	}
	
	/**
     * 设置无图模式
     */
    private void setNoPicModel(final boolean b) {
    	// TODO 设置无图模式
    	if (mSettingThread == null) {
			mSettingThread = ThreadHelper.creatThread("my_setting");
		}
		ThreadHelper.handle(mSettingThread, new Runnable() {
	
			@Override
			public void run() {
				SettingManager.getInst().saveNoPicModel(b ? Common.TRUE : Common.FALSE);
			}

		});
    	
    }
    
    /**
     * 设置白天黑夜模式
     * @param isChecked 
     */
    private void setDayAndNightModel(final boolean isChecked) {
    	// TODO 设置白天黑夜模式
    	if (mSettingThread == null) {
			mSettingThread = ThreadHelper.creatThread("my_setting");
		}
		ThreadHelper.handle(mSettingThread, new Runnable() {
	
			@Override
			public void run() {
				app.setNightModelChange(true);
		    	SettingManager.getInst().saveNightModel(isChecked ? Common.TRUE : Common.FALSE);
			}

		});
    	
    }
    
	@Override
	public Activity getActivity() {
		return SettingActivity.this;
	}

	@Override
	public void initViews() {
		// view
		mTitleBar = new TitleBar(getActivity(), true);
		mFont = findViewById(R.id.setting_font_rl);
		mFontText = (TextView) findViewById(R.id.setting_font_text);
		mCache = findViewById(R.id.setting_cache_rl);
		mCacheText = (TextView) findViewById(R.id.setting_cache_text);
		mPushSwitch = (SwitchButton) findViewById(R.id.setting_push_switch);
		mNightModeSwitch = (SwitchButton) findViewById(R.id.setting_night_mode_switch);
		mNoImageModeSwitch = (SwitchButton) findViewById(R.id.setting_no_image_mode_switch);
		mOffline = findViewById(R.id.setting_offline_rl);
		mAboutMe = findViewById(R.id.setting_aboutme_rl);
		mUpdate = findViewById(R.id.setting_update_rl);
		mFeedback = findViewById(R.id.setting_feedback_rl);
		mLlAccount = findViewById(R.id.setting_account_ll);
		mAccountInfo = findViewById(R.id.setting_account_info_rl);
		mAccountBind = findViewById(R.id.setting_account_bind_rl);

		mTitleBar.showDefaultBack();
		mTitleBar.setTitle("设置");
		mOfflineProgress = new SettingOfflineProgress(this, mOffline);
		
	}

	@Override
	public void initDatas() {
		
		app = App.getInst();
		mLlAccount.setVisibility(app.isLogin() ? View.VISIBLE : View.GONE);
//		if(app.isLogin()){
//			updatePhone();
//		}
		
		// data
		initSettedData();
		
	}

	/**
	 * 初始化设置的数据
	 */
	private void initSettedData() {
		if (mSettingThread == null) {
			mSettingThread = ThreadHelper.creatThread("my_setting");
		}
		ThreadHelper.handle(mSettingThread, new Runnable() {
	
			@Override
			public void run() {
				showCacheSize();
				SettingBean bean = SettingManager.getInst().getSettingBean();
				getHandler().obtainMessage(SET_SUCCESS, bean).sendToTarget();
			}

		});
	}

	@Override
	public void installListeners() {
		mAccountInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				startActivityForResult(new Intent(getActivity(), ModifyPhoneActivity.class), REQUESTCODE_MODIFYPHONE);
				AccountInfoActivity.launch(getActivity());
			}
		});
		mAccountBind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), AccountBindActivity.class));
				
			}
		});
		mFont.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (getActivityInfo().getActivityState() == ActivityState.SaveInstanceStated) {
					return;
				}
				SettingFontDialog dialog = new SettingFontDialog();
				dialog.setData(new SettingFontDialogItemClick() {
					@Override
					public void onItemClick(int position) {
						FontSize f = FontSize.Middle;
						switch (position) {
						case 0:
							f = FontSize.Small;
							break;
						case 1:
							f = FontSize.Middle;
							break;
						case 2:
							f = FontSize.Large;
							break;
						case 3:
							f = FontSize.ExtraLarge;
							break;
						}
						
						setFontSize(f.getSize());
						
						mFontText.setText(f.getStr());
					}

				}, new String[]{
						FontSize.Small.getStr(), 
						FontSize.Middle.getStr(), 
						FontSize.Large.getStr(), 
						FontSize.ExtraLarge.getStr(), 
						});
				dialog.showAllowingStateLoss((BaseFragmentActivity) getActivity(), getSupportFragmentManager(), SettingFontDialog.class.getSimpleName());
			}
		});
		
		mCache.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clearCache(getActivity());
			}
		});

	    // push
	    mPushSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if (mPushState != isChecked) {
					mPushState = isChecked;
					setPush(!isChecked);
					
					Map<String, String> map_ekv = new HashMap<String, String>();
					if(isChecked)
						map_ekv.put("status", "off");
					else
						map_ekv.put("status", "on");
		            MobclickAgent.onEvent(getActivity(), "setting_newpush", map_ekv);
				}
			}
		});
		
		// TODO(夜间模式)
	    mNightModeSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mNightState != isChecked) {
					setDayAndNightModel(!isChecked);
					
					Map<String, String> map_ekv = new HashMap<String, String>();
					if(isChecked)
						map_ekv.put("status", "off");
					else
						map_ekv.put("status", "on");
		            MobclickAgent.onEvent(getActivity(), "setting_nightmode", map_ekv);
					
					finish();
					startActivity(new Intent(getActivity(), SettingActivity.class));
				}
				
			}
		});
	    // TODO(无图模式)
	    mNoImageModeSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if (mNoImageState != isChecked) {
					mNoImageState = isChecked;
					setNoPicModel(!isChecked);
					Map<String, String> map_ekv = new HashMap<String, String>();
					if(isChecked)
						map_ekv.put("status", "off");
					else
						map_ekv.put("status", "on");
		            MobclickAgent.onEvent(getActivity(), "setting_nopicmode", map_ekv);
				}
			}
	    });
	    mOfflineProgress.setOnOfflineDownloadListener(new OnOfflineDownloadListener() {
			
			@Override
			public void onState(OfflineDownloadState state, Object obj) {
				switch (state) {
				case Finish:
					if(mCacheText != null){
						showCacheSize();
					}
					break;
				default:
					break;
				}
			}
		});
		
		mAboutMe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), AboutActivity.class));
			}
		});
		
		mUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UpdateVersionUtil.checkUpdate(getActivity(), Const.UPDATE_APP_KEY, true);
			}
		});
		mFeedback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    FeedbackAPI.openFeedbackActivity();
			}
		});
		
	}
	
	protected void setPush(final boolean isChecked) {
		if (mSettingThread == null) {
			mSettingThread = ThreadHelper.creatThread("my_setting");
		}
		ThreadHelper.handle(mSettingThread, new Runnable() {
	
			@Override
			public void run() {
				SettingManager.getInst().saveIsPush(isChecked ? Common.TRUE : Common.FALSE);
			}

		});
		
	}

	/**
	 * 设置字体
	 * @param size
	 */
	private void setFontSize(final int size) {
		if (mSettingThread == null) {
			mSettingThread = ThreadHelper.creatThread("my_setting");
		}
		ThreadHelper.handle(mSettingThread, new Runnable() {
	
			@Override
			public void run() {
				SettingManager.getInst().saveFontSize(size);
			}

		});
	}
	
	private void showCacheSize(){
		// cache
		long size = 0;
		try {
//			StringBuilder builder = new StringBuilder(Environment
//					.getExternalStorageDirectory().getAbsolutePath()).append(File.separator).
//					append("Android").append(File.separator).append("Data").append(File.separator).
//					append("com.shenyuan.militarynews").append(File.separator).append("cache");
//			long size = Common.getFileSize(new File(builder.toString()));
			size = Common.getFileSize(getActivity().getExternalCacheDir());
			long cacheSize = Common.getFileSize(getActivity().getCacheDir());
			size += cacheSize;
		} catch (Exception e) {
			e.printStackTrace();
		}
		long glideCacheSize = Glide.getPhotoCacheDir(getActivity()).length();
		size += glideCacheSize;
		int temp = (int) (size/1024);
	    float mb = temp/(float)1024;
	    DecimalFormat to= new DecimalFormat("0.00");
	    getHandler().obtainMessage(SET_CACHE_SUCCESS, String.valueOf(to.format(mb)+" MB")).sendToTarget();
	    
	}

	/**
	 * 清除缓存
	 */
	protected void clearCache(final Activity context) {
		UIHelper.addPD(context, "清理中");
		if (mSettingThread == null) {
			mSettingThread = ThreadHelper.creatThread("my_setting");
		}
		ThreadHelper.handle(mSettingThread, new Runnable() {
	
			@Override
			public void run() {
				SPHelper.getInst().saveLong(SPHelper.KEY_OFFLINE_DOWNLOAD_TIME, -1);
				LocalStateServer.getInst(context).clearArticle();
				Common.clearCacheFolder(context.getExternalCacheDir(),System.currentTimeMillis());
				Common.clearCacheFolder(context.getCacheDir(),System.currentTimeMillis());
				Common.clearCacheFolder(Glide.getPhotoCacheDir(context),System.currentTimeMillis());
				getHandler().post(new Runnable() {
				
					@Override
					public void run() {
						getHandler().obtainMessage(SET_CACHE_SUCCESS, "0.00MB").sendToTarget();
					}
				});
			}

		});
		GlideHelper.getInst().clearCache(getActivity());
	}
	
	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
			case SET_CACHE_SUCCESS :
				mCacheText.setText((String)msg.obj);
				UIHelper.removePD();
				break;
			case SET_SUCCESS :
				SettingBean bean = (SettingBean) msg.obj;
				if (bean != null) {
					mFontText.setText(FontSize.getFontSize(bean.getFontsize()).getStr());
					mPushState = !Common.isTrue(bean.getIs_push());
					mNightState = !Common.isTrue(bean.getIs_night_model());
					mNoImageState = !Common.isTrue(bean.getIs_no_pic_model());
					mPushSwitch.setChecked(mPushState);
					mNightModeSwitch.setChecked(mNightState);
					mNoImageModeSwitch.setChecked(mNoImageState);
					
				}
				
				break;
			default :
				break;
		}
	}
	
	@Override
	public void onDestroy() {
		mOfflineProgress.destroy();
		ThreadHelper.destory(mSettingThread);
		getHandler().removeMessages(SET_CACHE_SUCCESS);
		getHandler().removeMessages(SET_SUCCESS);
		super.onDestroy();
	}

	@Override
	public void onSOPCConfirm() {
		mOfflineProgress.onSOPCConfirm();
	}

	@Override
	public void onSOPWConfirm() {
		mOfflineProgress.onSOPWConfirm();
	}
}
