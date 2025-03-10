package com.chengning.fenghuo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVInstallation;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.util.DisplayUtil;
import com.chengning.common.util.HttpUtil;
import com.chengning.common.util.SerializeUtil;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.Consts;
import com.chengning.fenghuo.Consts.ArticleType;
import com.chengning.fenghuo.LoadStateManager;
import com.chengning.fenghuo.LoadStateManager.LoadState;
import com.chengning.fenghuo.MyJsonHttpResponseHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.SettingManager;
import com.chengning.fenghuo.SettingManager.FontSize;
import com.chengning.fenghuo.data.access.LocalStateDA;
import com.chengning.fenghuo.data.bean.ArticlesBean;
import com.chengning.fenghuo.data.bean.BaseArticlesBean;
import com.chengning.fenghuo.data.bean.ChannelItemBean;
import com.chengning.fenghuo.data.bean.CommentItemBean;
import com.chengning.fenghuo.data.bean.DynamicItemBean;
import com.chengning.fenghuo.data.bean.RewardNumBean;
import com.chengning.fenghuo.data.bean.SubscribeContentItemBean;
import com.chengning.fenghuo.event.CollectEvent;
import com.chengning.fenghuo.event.CommentSuccessEvent;
import com.chengning.fenghuo.event.PayResultEvent;
import com.chengning.fenghuo.util.ArticleContentUtil;
import com.chengning.fenghuo.util.ArticleManagerUtils;
import com.chengning.fenghuo.util.ArticleManagerUtils.DataStateListener;
import com.chengning.fenghuo.util.ArticleWebView;
import com.chengning.fenghuo.util.ArticleWebView.OnWebViewListener;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.JUrl;
import com.chengning.fenghuo.util.SPHelper;
import com.chengning.fenghuo.util.UIHelper;
import com.chengning.fenghuo.util.UmengShare;
import com.chengning.fenghuo.util.Utils;
import com.chengning.fenghuo.widget.ArticleAdBottom;
import com.chengning.fenghuo.widget.ArticleCommentBottom;
import com.chengning.fenghuo.widget.ArticleCommentBottom.FavChangeListenner;
import com.chengning.fenghuo.widget.ArticleMoreDialog;
import com.chengning.fenghuo.widget.ArticleReadFinishDialog;
import com.chengning.fenghuo.widget.ArticleRelativeBottom;
import com.chengning.fenghuo.widget.ArticleRewardDialog;
import com.chengning.fenghuo.widget.NoFocusingBottomListenerScrollView;
import com.chengning.fenghuo.widget.ProgressRefreshView;
import com.chengning.fenghuo.widget.TitleBar;
import com.chengning.fenghuo.widget.TitleBar.BackOnClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class ArticleActivity extends BaseFragmentActivity {

	private static final int MSG_DATA_READY = 1;

	private View mRoot;
	private TitleBar mTitleBar;
	private ProgressRefreshView mProgressRefresh;
	private View mCoverLayout;
	private TextView mArticleTitle;
	private TextView mArticleName;
	private TextView mArticleFrom;
	private TextView mArticleTime;
	private RelativeLayout mArticleContentLayout;

	private ArticleWebView<String> mArticleWebView;
	private ArticleContentUtil<String> mArticleContentView;

	private BaseArticlesBean mBean;
	private ArticlesBean mArticleBean;

	private ArticleMoreDialog mSettingDialog;
	private ArticleRewardDialog mRewardDialog;

	private LoadStateManager mLoadStateManager;
	private ArticleManagerUtils mArticleManagerUtils;

	private App app;

	private boolean isFavChange = false;

	private boolean isFavState;

	private ArticleCommentBottom mArticleComment;

	private boolean mIsFromPush;

	private BroadcastReceiver mShareSuccessReceiver;
	private Runnable mShareSuccessRunnable;

	private FavChangeListenner mFavChangeListener;

	private TextView mArticleFromTv;

	private int lastNightModel;

	private ArticleType mCollectType;
	
	private ArticleRelativeBottom mArticleRelative;
	private ArticleAdBottom mArticleAd;
	private View mZanShangBtn;
	private View mShangTouTiaoBtn;
	private View mBuXiHuanBtn;
	private NoFocusingBottomListenerScrollView mContentScroll;
	private TextView mShangTouTiaoTv;
	private TextView mBuXiHuanTv;
	private View mArticleCommentLayout;
	private int mScrollY;
	private long sTime;
	private boolean isReaded;
	private int mScreenHeight;
	private boolean isStartRead;
	private DataStateListener mLikeState;
	private DataStateListener mNotLikeState;

	public boolean isFavChange() {
		return isFavChange;
	}

	public void setFavChange(boolean isFavChange) {
		this.isFavChange = isFavChange;
	}

	public static void launch(Activity from, BaseArticlesBean bean) {
		Intent intent = new Intent(from, ArticleActivity.class);
		intent.putExtra("bean", bean);
		from.startActivity(intent);
	}

	public static void launchByReqCode(Activity from, BaseArticlesBean bean,
			ArticleType type) {
		Intent intent = new Intent(from, ArticleActivity.class);
		intent.putExtra("bean", bean);
		intent.putExtra("collect_type", type);
		from.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_article);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void initViews() {
		mRoot = findViewById(R.id.root);
		mTitleBar = new TitleBar(this, true);
		mProgressRefresh = new ProgressRefreshView(getActivity(), true);
		mCoverLayout = findViewById(R.id.cover_layout);
		mContentScroll = (NoFocusingBottomListenerScrollView) findViewById(R.id.article_content_scroll);
		mArticleTitle = (TextView) findViewById(R.id.article_content_top_title);
		mArticleName = (TextView) findViewById(R.id.article_content_top_name);
		mArticleFrom = (TextView) findViewById(R.id.article_content_top_from);
		mArticleFromTv = (TextView) findViewById(R.id.article_content_top_from_tv);
		mArticleTime = (TextView) findViewById(R.id.article_content_top_time);
		mArticleContentLayout = (RelativeLayout) findViewById(R.id.article_content_content_layout);
		mArticleCommentLayout = findViewById(R.id.article_content_pager_layout);
		mZanShangBtn = findViewById(R.id.article_content_zanshang_layout);
		mShangTouTiaoBtn = findViewById(R.id.article_content_shangtoutiao_layout);
		mShangTouTiaoTv = (TextView) findViewById(R.id.article_content_shangtoutiao_tv);
		mBuXiHuanBtn = findViewById(R.id.article_content_buxihuan_layout);
		mBuXiHuanTv = (TextView) findViewById(R.id.article_content_buxihuan_tv);

		mTitleBar.setBackText("");
		mTitleBar.showBack();
		mTitleBar.setRightButton(R.drawable.more_icon);
		
		mArticleRelative = new ArticleRelativeBottom(this, mRoot);
		mArticleAd = new ArticleAdBottom(this, mRoot);
	}

	@Override
	public void initDatas() {
		app = App.getInst();
		DisplayUtil.getInst().init(getActivity());
		mScreenHeight = DisplayUtil.getInst().getScreenHeight();
		UmengShare.getInstance().checkInit(getActivity());

		mBean = (BaseArticlesBean) getIntent().getSerializableExtra("bean");
		mCollectType = (ArticleType) getIntent().getSerializableExtra("collect_type");
		
		mIsFromPush = getIntent().getBooleanExtra("push", false);

		lastNightModel = SettingManager.getInst().getNightModel();
		
		initLoadState();
		initBottomComment();
		initContent();
		
		EventBus.getDefault().register(getActivity());
	}

	/**
	 * 初始化内容
	 */
	private void initContent() {
		if (mIsFromPush) {
			LocalStateDA.getInst(getActivity()).setReadStateRead(
					LocalStateDA.PREFIX_CHANNEL_ITEM, mBean.getTid());
		}
		mLikeState = new DataStateListener() {

			@Override
			public void success(Object... objects) {
				if (objects == null ) {
					return;
				}
				String digCounts = (String) objects[0];
				handleZanTV(Common.trimZero(digCounts));
				mShangTouTiaoTv.setSelected(true);
				mShangTouTiaoBtn.setEnabled(true);
			}

			@Override
			public void init() {
				mShangTouTiaoBtn.setEnabled(false);
			}

			@Override
			public void failure() {
				mShangTouTiaoBtn.setEnabled(true);
			}
		};
		mNotLikeState = new DataStateListener() {

			@Override
			public void init() {
				mBuXiHuanBtn.setEnabled(false);
			}

			@Override
			public void success(Object... objects) {
				if (objects == null ) {
					return;
				}
				String digCounts = (String) objects[0];
				handleNotLikeTV(Common.trimZero(digCounts));
				mBuXiHuanTv.setSelected(true);
				mBuXiHuanBtn.setEnabled(true);
			}

			@Override
			public void failure() {
				mBuXiHuanBtn.setEnabled(true);
			}
		};

		mArticleManagerUtils = new ArticleManagerUtils();

		if (Common.hasNet()) {
			getDataByHttp();
		} else {
			boolean isArticleSaved = false;
			String aStr = LocalStateDA
					.getInst(getActivity())
					.queryLocalStateNotNull(LocalStateDA.PREFIX_CHANNEL_ITEM,
							mBean.getTid()).getData_item_article();
			if (!TextUtils.isEmpty(aStr)) {
				try {
					ArticlesBean aBean = SerializeUtil.deSerialize(aStr,
							ArticlesBean.class);
					if (!TextUtils.isEmpty(aBean.getTid())) {
						isArticleSaved = true;
						getHandler().obtainMessage(MSG_DATA_READY, aBean)
								.sendToTarget();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!isArticleSaved) {
				getDataByHttp();
			}
		}

		mShareSuccessReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(Consts.ACTION_WX_SHARE_SUCESS)) {
					final String data = intent.getStringExtra("data");
					if (null != data) {
						mShareSuccessRunnable = new Runnable() {

							@Override
							public void run() {
								try {
									Utils.showHints(getActivity(), data);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						};
					}
				}
			}
		};
		IntentFilter intentFileter = new IntentFilter();
		intentFileter.addAction(Consts.ACTION_WX_SHARE_SUCESS);
		intentFileter.setPriority(200);
		registerReceiver(mShareSuccessReceiver, intentFileter);
	}

	/**
	 * 初始化底部评论
	 */
	private void initBottomComment() {
		mFavChangeListener = new FavChangeListenner() {

			@Override
			public void setChange(boolean isFaved) {
				isFavState = isFaved;
				setFavChange(!isFavState);
			}
		};
		mArticleComment = new ArticleCommentBottom(getActivity(), mRoot,
				mFavChangeListener);
		mArticleComment.setChannelBean(mBean, Consts.ArticleCommentType.ARTICLE);
		mArticleComment.setCommentClickListener(new Runnable() {
			@Override
			public void run() {
				int top = mArticleCommentLayout.getTop();
				mContentScroll.smoothScrollTo(0, top == mScrollY ? 0 : top);
			}
		});
		mArticleRelative.setChannelBean(mBean);
		mArticleAd.setChannelBean(mBean);
	}

	/**
	 * 初始化加载状态
	 */
	private void initLoadState() {
		mLoadStateManager = new LoadStateManager();
		mLoadStateManager
				.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {

					@Override
					public void OnStateChange(LoadState state, Object obj) {
						switch (state) {
							case Init:
								mProgressRefresh.setWaitVisibility(true);
								mProgressRefresh.setRefreshVisibility(false);
								mCoverLayout.setVisibility(View.VISIBLE);
								break;
							case Success:
								mProgressRefresh.setRootViewVisibility(false);
								mCoverLayout.setVisibility(View.INVISIBLE);
								break;
							case Failure:
								mProgressRefresh.setWaitVisibility(false);
								mProgressRefresh.setRefreshVisibility(true);
								mCoverLayout.setVisibility(View.VISIBLE);
								break;
							default:
								break;
						}
					}
				});
		mLoadStateManager.setState(LoadState.Init);
	}

	@Override
	public void installListeners() {
		mTitleBar.setBackOnClickListener(new BackOnClickListener() {

			@Override
			public void onClick() {
				if (mIsFromPush) {
					MainActivity.launch(getActivity());
				}
				if (isFavChange()) {
					setResultOk();
				}
			}
		});
		mTitleBar.setRightButtonOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null == mArticleBean) {
					return;
				}
				if (null == mSettingDialog) {
					mSettingDialog = new ArticleMoreDialog();
					mSettingDialog.setBean(mArticleBean);
					mSettingDialog.setListener(new ArticleMoreDialog.OnArticleSettingListener() {

						@Override
						public void onFontChange(FontSize font) {
							if (null == mArticleBean) {
								return;
							}
							if (null == mArticleWebView) {
								getHandler().obtainMessage(MSG_DATA_READY,
										mArticleBean).sendToTarget();
							} else {
								mArticleWebView.setFont(font, true);

							}
						}

						@Override
						public void onNightModeChange(boolean mode) {
							mSettingDialog.dismissAllowingStateLoss();
							setDayAndNightModel(mode);
							finish();
							launch(getActivity(), mBean);
						}
					});
				}
				mSettingDialog.show(getSupportFragmentManager(),
						ArticleMoreDialog.class.getSimpleName());
				// 小米手机上UI概率显示不全，强制延时刷新UI
				getHandler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (mSettingDialog != null) {
							mSettingDialog.shouldRefreshUI();
						}
					}
				}, 100);

			}
		});

		mArticleFrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserInfoActivity.launch(getActivity(), ((TextView) v).getText()
						.toString().trim());
			}
		});

		mZanShangBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mArticleBean == null) {
					return;
				}
				UIHelper.addPD(getActivity(), "正在获取打赏金额...");
				HttpUtil.getClient().get(JUrl.SITE + JUrl.URL_GET_REWARD, new MyJsonHttpResponseHandler() {
					@Override
					public void onDataSuccess(int status, String mod, String message,
											  String data, JSONObject obj) {
						UIHelper.removePD();
						Gson gson = new Gson();
						List<RewardNumBean> mNumList = gson.fromJson(data,
								new TypeToken<List<RewardNumBean>>() {
								}.getType());
						if (null == mRewardDialog) {
							mRewardDialog = new ArticleRewardDialog();
							mRewardDialog.setmArticleTid(mArticleBean.getTid());
						}
						mRewardDialog.setmRewardNumList(mNumList);
						if (!mRewardDialog.isAdded()) {
							mRewardDialog.show(getSupportFragmentManager(),
									ArticleRewardDialog.class.getSimpleName());
						}
					}

					@Override
					public void onDataFailure(int status, String mod, String message,
											  String data, JSONObject obj) {
						UIHelper.removePD();
						UIHelper.showToast(getActivity(), message);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
										  Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable, errorResponse);
						UIHelper.removePD();
						Common.handleHttpFailure(getActivity(), throwable);
					}
				});


			}
		});
		mShangTouTiaoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mArticleBean == null || mArticleManagerUtils == null) {
					return;
				}
				mArticleManagerUtils.doLike(getActivity(), mArticleBean, mLikeState);
			}
		});

		mBuXiHuanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mArticleBean == null || mArticleManagerUtils == null) {
					return;
				}
				mArticleManagerUtils.doNotLike(getActivity(), mArticleBean, mNotLikeState);
			}
		});

		mProgressRefresh.setRefreshOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				getDataByHttp();
			}
		});


		mContentScroll.setOnScrollToBottomLintener(new NoFocusingBottomListenerScrollView.OnScrollToBottomListener() {
			@Override
			public void onScrollBottomListener(boolean isBottom) {
				mContentScroll.setLoading();
				mArticleComment.getNextPageComment(getActivity(), new DataStateListener() {

					@Override
					public void init() {

					}

					@Override
					public void success(Object... objects) {
						mContentScroll.setLoadFinish();
					}

					@Override
					public void failure() {
						mContentScroll.setLoadFinish();
					}
				});
			}

			@Override
			public void onScrollY(int scrollY) {
				mScrollY = scrollY;
				if (mScrollY > 0 && !isStartRead) {
					isStartRead = true;
					sTime = Common.getCurTimeMillis();
				}
				float contentBottom = mArticleContentLayout.getBottom();
				double diff = contentBottom - mScreenHeight * 0.9 - mScrollY;
				if (-10 < diff && diff < 50 && !isReaded) {
					isReaded = true;
//					UIHelper.showToast(getActivity(), "已读完");
					Log.e("样本采集数据:", mContentScroll.getSampleList().toString());
					long eTime = Common.getCurTimeMillis();
					double[] data = Common.getVariance(mContentScroll.getSampleList());
					if (data != null && data.length >= 2) {
						Log.e("样本计算数据:", "avg:" + data[0] + "	" + "variance:" + data[1]);

						float time = eTime - sTime;
						Log.e("样本总时间:", "time:" + time);
						double expectTime = handleExpectTime(contentBottom, mArticleWebView.getImgHeight());
						Log.e("预估总时间:", "alltime:" + expectTime);
						double[] expectData = handleExpectAverage(contentBottom,mArticleWebView.getImgHeight(), mContentScroll.getSampleList());
                        Log.e("预估均值:", "expectAvg:" + expectData[0]);
                        Log.e("预估方差:", "expectVariance:" + expectData[1]);
						try {
							if (time >= expectTime) {
								handleReadFinish(data, expectData);
							}

						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

//						File avgFile = Common.creatFile(JUrl.sampleFilePath, "avg.txt");
//						File varianceFile = Common.creatFile(JUrl.sampleFilePath, "variance.txt");
//						FileUtils.writeFileFromString(avgFile, data[0] + "\r\n", true);
//						FileUtils.writeFileFromString(varianceFile, data[1] + "\r\n", true);

					}
				}
				if (mScrollY <= 0) {
					mContentScroll.clearSamples();
				}
			}
		});

	}

	/**
	 * 阅读完毕
	 * @param data
	 * @param expectData
	 * @throws UnsupportedEncodingException
	 */
	private void handleReadFinish(double[] data, double[] expectData) throws UnsupportedEncodingException {
		if (!avgIsValid(data[0], expectData[0]) || !varianceIsValid(data[1], expectData[1])) {
			return;
		}
		StringBuilder builder = new StringBuilder(mArticleBean.getTid());
		builder.append("|").append(String.valueOf(data[0])).
				append("|").append(String.valueOf(expectData[0])).append("|")
				.append(String.valueOf(data[1])).append("|")
				.append(String.valueOf(expectData[1]));
		String encodeStr = Common.encrypt(builder.toString(), Consts.ENCODE_KEY);
		RequestParams params = new RequestParams();
		params.put("key",encodeStr);
		HttpUtil.get(getActivity(), JUrl.SITE + JUrl.URL_ARTICLE_READ_FINISH, params, new MyJsonHttpResponseHandler() {
			@Override
			public void onDataSuccess(int status, String mod, String message, String data, JSONObject obj) {

				try {
					JSONObject object = new JSONObject(data);
					String amount = object.getString("amount");
					if (!TextUtils.isEmpty(amount) && TextUtils.isDigitsOnly(amount) && Integer.valueOf(amount) != 0) {
						ArticleReadFinishDialog dialog = new ArticleReadFinishDialog();
						dialog.setData(amount);
						// 调用show方法可能会报java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState  
						dialog.show(getSupportFragmentManager(), ArticleReadFinishDialog.class.getSimpleName());
						dialog.cancle(getHandler());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, JSONObject obj) {

			}
		});
	}

	/**
	 * 根据图片计算预估时间
	 * @param contentHeight
	 * @param imgAllHeight
	 * @return
	 */
	public double handleExpectTime(float contentHeight, float imgAllHeight) {
		int screenNum = Math.round(contentHeight / mScreenHeight) - 1;
		if (screenNum < 2) {
			return  0;
		}
		float heightRadTio = imgAllHeight/DisplayUtil.getInst().px2dip(contentHeight);
		double everyTime = mArticleBean.getView_second();
		if (heightRadTio > Consts.EXPECT_RATIO_IMG_HEIGHT) {
			everyTime = (28 - 25 * heightRadTio) / 3 ;
		}
		double allTime = screenNum * everyTime * 1000;
		return  allTime;
	}

	/**
	 * 根据图片计算预估均值、方差
	 * @param contentHeight
	 * @param imgAllHeight
	 * @param sampleList
     * @return
	 */
	public double[] handleExpectAverage(float contentHeight, float imgAllHeight, List<Double> sampleList) {
		float heightRadTio = imgAllHeight/DisplayUtil.getInst().px2dip(contentHeight);
		double avg = Consts.EXPECT_AVERAGE;
		if (heightRadTio > Consts.EXPECT_RATIO_IMG_HEIGHT) {
			avg = (10 * heightRadTio - 1) / 6;
		}
		//根据样本每次滑动是否为屏幕的80%
        float num = 0;
        for (double sample : sampleList){
            if (sample > 1) {
                num++;
            }
        }
        float sampleRadio = num/sampleList.size();
        avg = 1.25 * sampleRadio + avg;
		double variance = 0.0;
		if (sampleRadio> 0.5) {
			variance = 2.1 - 2 * sampleRadio;
		} else {
			variance = 2 * sampleRadio + 0.1;
		}
		double[] data = new double[2];
		data[0] = avg;
		data[1] = variance;
		return  data;
	}

	/**
	 * 判断均值是否有效
	 * @param sampleAvg
	 * @param expectAvg
	 * @return
	 */
	public boolean avgIsValid (double sampleAvg, double expectAvg) {
		double diff = sampleAvg - expectAvg;
		if (diff > 0) {
			double errorRatio = diff / expectAvg;
			return errorRatio < Consts.EXPECT_AVERAGE_ERROR_RATIO;
		}
		return true ;
	}

	/**
	 * 判断方差是否有效
	 * @param sampleVariance
	 * @return
	 */
	public boolean varianceIsValid (double sampleVariance,double expectVariance) {
		boolean b = sampleVariance <= (expectVariance  + expectVariance * Consts.EXPECT_VARIANCE_ERROR_RATIO);
		return b;
	}

	/**
	 * 设置白天黑夜模式
	 *
	 * @param isChecked
         */
	private void setDayAndNightModel(boolean isChecked) {
		app.setNightModelChange(true);
		SettingManager.getInst().saveNightModel(
				isChecked ? Common.TRUE : Common.FALSE);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable("bean", mBean);
		savedInstanceState.putBoolean("push", mIsFromPush);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mBean = (BaseArticlesBean) savedInstanceState.getSerializable("bean");
		mIsFromPush = savedInstanceState.getBoolean("push", false);
	}

	private void handleHttpFailure() {
		Common.showHttpFailureToast(getActivity());
		getHandler().post(new Runnable() {

			@Override
			public void run() {
				mLoadStateManager.setState(LoadState.Failure);
			}
		});
	}

	private void getDataByHttp() {
		final String aid = String.valueOf(mBean.getTid());
		String url = JUrl.SITE + JUrl.URL_GET_ARTICLES_SUBCSRIBE + aid;
		BasicHeader header = new BasicHeader("Accept-Encoding", "gzip, deflate");
		Header[] headers = { new BasicHeader("Accept", "*/*"), header,
				new BasicHeader("Accept-Language", "zh-cn") };
		
		RequestParams params = new RequestParams();
		params.put("devicetoken", AVInstallation
				.getCurrentInstallation()
				.getInstallationId());
		HttpUtil.getClient().post(this, url.toString(), headers, params, "text/html",
				new MyJsonHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers,
										  Throwable throwable, JSONObject error) {

						handleHttpFailure();
					}

					@Override
					public void onDataSuccess(int status, String mod,
											  String message, String data, JSONObject obj) {

						Gson gson = new Gson();
						ArticlesBean bean = gson.fromJson(data,
								ArticlesBean.class);
						LocalStateDA.getInst(getActivity()).setArticle(
								LocalStateDA.PREFIX_CHANNEL_ITEM,
								bean.getTid(), SerializeUtil.serialize(bean));
						getHandler().obtainMessage(MSG_DATA_READY, bean)
								.sendToTarget();
					}

					@Override
					public void onDataFailure(int status, String mod,
											  String message, String data, JSONObject obj) {

						UIHelper.showToast(getActivity(), message);
						mLoadStateManager.setState(LoadState.Failure);
					}
				});
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch (arg0) {
		case ArticleContentUtil.REQUEST_PHOTO_PAGE_CODE:
			if (Activity.RESULT_OK == arg1) {
				if (null == arg2) {
					return;
				} else {
					int state = arg2.getExtras().getInt("favState", -1);
					// isFavState = Common.isTrue(state);
					mArticleComment.setCollectState(Common.isTrue(state));
					mArticleBean.setIs_favor(state);
					// setFavChange(!isFavState);
					// mFavChangeListener.setChange(Common.isTrue(state));
				}

			}
			break;

		default:
			break;
		}
		super.onActivityResult(arg0, arg1, arg2);
		UmengShare.getInstance().onActivityResult(arg0, arg1, arg2);

	}

	@Override
	public BaseFragmentActivity getActivity() {
		return ArticleActivity.this;
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case MSG_DATA_READY:
			// data ready

			mArticleBean = (ArticlesBean) msg.obj;

			if(mIsFromPush){
				mBean.setTitle(mArticleBean.getTitle());
				mBean.setDateline(mArticleBean.getDateline());
				mBean.setContent_type(Consts.CONTENT_TYPE_NORMAL);
				String img = "";
				for (SubscribeContentItemBean bean : mArticleBean.getContent()) {
					if(bean.getType().equals("img")){
						img = bean.getSrc();
						break;
					}
				};
				mBean.setImage_list(img);
				mArticleComment.setChannelBean(mBean, Consts.ArticleCommentType.ARTICLE);
			}

			ChannelItemBean mItemBean = (ChannelItemBean) mBean;
			String imageList = mArticleBean.getImage_list();
			if (mItemBean != null) {
				mArticleBean.setImage_arr(mItemBean.getImage_arr());
				if (TextUtils.isEmpty(imageList )) {
					mArticleBean.setImage_list(mItemBean.getImage_list());
				}
			}

			mArticleTitle.setText(mArticleBean.getTitle());
			StringBuffer buffer = new StringBuffer();
			buffer.append("(编辑: ").append(mArticleBean.getNickname())
					.append(")");
			mArticleName.setText(buffer.toString());

			handleArticleFrom(Common.isTrue(mArticleBean.getIs_special()));

			mArticleTime.setText(mArticleBean.getDateline());

			handleZanTV(Common.trimZero(mArticleBean.getDigcounts()));
			handleNotLikeTV(Common.trimZero(mArticleBean.getBadcounts()));
			mShangTouTiaoTv.setSelected(Common.isTrue(mArticleBean.getIs_dig()));
			mBuXiHuanBtn.setSelected(Common.isTrue(mArticleBean.getIs_bad()));

			// content
			mArticleContentLayout.removeAllViews();
			if (null != mArticleWebView) {
				mArticleWebView.destroy();
			}
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				// Call requires API level 11
				mArticleWebView = new ArticleWebView<String>();
				mArticleWebView.setChannelBean(mBean);
				View contentR = mArticleWebView.build(this, mArticleBean,
						new OnWebViewListener() {

							@Override
							public void onPageStarted() {
								mLoadStateManager.setState(LoadState.Init);
							}

							@Override
							public void onPageFinished() {
								// webview 延迟显示避免闪屏
								getHandler().postDelayed(
										new Runnable() {

											@Override
											public void run() {
												mLoadStateManager
														.setState(LoadState.Success);
											}
										},
										Common.isTrue(SettingManager.getInst()
												.getNightModel()) ? 500 : 20);
							}

							@Override
							public void onTimeout() {
								onPageFinished();
							}

						}, getHandler());
				contentR.setFocusable(false);
				mArticleWebView.getWebView().setFocusable(false);
				mArticleContentLayout.addView(contentR);
			} else {
				mArticleContentView = new ArticleContentUtil<String>();
				mArticleContentView.setChannelBean(mBean);
				View contentR = mArticleContentView.build(this, mArticleBean);
				contentR.setFocusable(false);
				mArticleContentLayout.addView(contentR);
				mLoadStateManager.setState(LoadState.Success);
			}

			if (!app.isLogin()) {

				String img = "";
				if (mBean instanceof ChannelItemBean) {
					img = ((ChannelItemBean) mBean).getImage_list();
				} else if (mBean instanceof DynamicItemBean) {
					img = ((DynamicItemBean) mBean).getImage_list().get(0)
							.getImage_small();
				}

				mArticleBean.setImage_list(img);
			}
			mArticleComment.setData(mArticleBean);
			mArticleRelative.setData(mArticleBean);
			mArticleAd.setData(mArticleBean);
//			sTime = Common.getCurTimeMillis();
			isReaded = false;
			isStartRead = false;
			break;
		default:
			break;
		}
	}

	private void handleNotLikeTV(String badCount) {
		mBuXiHuanTv.setText(TextUtils.isEmpty(badCount)? "不喜欢"
				:  badCount);
	}

	private void handleZanTV(String digCount) {
		mShangTouTiaoTv.setText(TextUtils.isEmpty(digCount) ? "上头条"
				: digCount);
	}

	private void handleArticleFrom(boolean isColumnist) {
		
		if (isColumnist) {
			mArticleFrom.setText(mArticleBean.getNickname());

//			mArticleFrom.setVisibility(View.VISIBLE);
//			mArticleFromTv.setVisibility(View.VISIBLE);
			mArticleFrom.setTextColor(getResources().getColor(
					R.color.common_blue));

			mArticleFrom.setEnabled(isColumnist);
		} else {
			mArticleFrom.setText(!TextUtils.isEmpty(mArticleBean.getFrom()) ? mArticleBean.getFrom() : "");

//			mArticleFrom.setVisibility(View.VISIBLE);
//			mArticleFromTv.setVisibility(View.VISIBLE);
		}
//		else {
//			mArticleFrom.setVisibility(View.GONE);
//			mArticleFromTv.setVisibility(View.GONE);
//		}
	}

	protected void setResultOk() {
		if (mCollectType != null) {
			CollectEvent result = new CollectEvent();
			result.setFavState(isFavState ? Common.TRUE : Common.FALSE);
			result.setArticleType(mCollectType);
			EventBus.getDefault().post(result);
		}
	}

	@Override
	public void onBackPressed() {
		try {
			if (mIsFromPush) {
				MainActivity.launch(getActivity());
			}
			if (isFavChange()) {
				setResultOk();
			}
			super.onBackPressed();
		} catch (IllegalStateException ignored) {

		}
		
	}

	@Override
	public void onResume() {
		super.onResume();
		if (SettingManager.getInst().getNightModel() != lastNightModel) {
			finish();
			if (null != mBean) {
				launch(getActivity(), mBean);
			}
			
		}
		
		mArticleComment.updateCollectState(mArticleBean);
		if (mShareSuccessRunnable != null) {
			mShareSuccessRunnable.run();
			mShareSuccessRunnable = null;
		}
		
	}
	
	@Subscribe
    public void onEventMainThread(PayResultEvent event) {
		if(event.getResultCode() == 0){
			mRewardDialog.dismissAllowingStateLoss();
			mRewardDialog = null;
			UIHelper.showToast(getActivity(), "打赏成功");
		}else if(event.getResultCode() == -2){
			UIHelper.showToast(getActivity(), "取消打赏");
		}else{
			UIHelper.showToast(getActivity(), "打赏失败");
		}
    }
	
	@Subscribe
    public void onEventMainThread(CommentSuccessEvent event) {
		if (event != null && event.getType() == Consts.ArticleCommentType.ARTICLE) {
			CommentItemBean bean = event.getmBean();
			if (bean == null) {
				return;
			}
			ArrayList<CommentItemBean> list = mArticleBean.getReply_list();
			if (Common.isListEmpty(list)) {
				list.add(event.getmBean());
			} else {
				list.add(0, event.getmBean());
			}
			mArticleBean.setReply_list( list);
			mArticleBean.setReplys(mArticleBean.getReplys() + 1);
			mArticleComment.setData(mArticleBean);
//			int top = mArticleCommentLayout.getTop();
			mContentScroll.smoothScrollTo(0, mArticleCommentLayout.getTop());
		}
    }

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (null != mShareSuccessReceiver) {
			unregisterReceiver(mShareSuccessReceiver);
		}
		EventBus.getDefault().unregister(getActivity());
		if (mArticleComment != null) {
			mArticleComment.destory();
		}
		super.onDestroy();
	}

}
