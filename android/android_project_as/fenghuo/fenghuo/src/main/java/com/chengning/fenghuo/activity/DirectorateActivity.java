package com.chengning.fenghuo.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chengning.common.base.BaseActivity;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.util.DisplayUtil;
import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuo.LoadStateManager;
import com.chengning.fenghuo.LoadStateManager.LoadState;
import com.chengning.fenghuo.LoginManager;
import com.chengning.fenghuo.MyJsonHttpResponseHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.adapter.DirectorateTaskAdapter;
import com.chengning.fenghuo.data.bean.DirectorateBean;
import com.chengning.fenghuo.data.bean.DirectorateMemberBean;
import com.chengning.fenghuo.data.bean.DirectorateTaskBean;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.ImageUtils;
import com.chengning.fenghuo.util.JUrl;
import com.chengning.fenghuo.util.Utils;
import com.chengning.fenghuo.widget.LoadFullListView;
import com.chengning.fenghuo.widget.ProgressRefreshView;
import com.chengning.fenghuo.widget.TitleBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * @description 指挥部活动
 * @author wangyungang
 * @date 2015:7:15 11:12
 */
public class DirectorateActivity extends BaseActivity {

	private static final int MSG_UI = 0;

	protected static final int BG_SUCCESS = 1;

	protected static final int HTTP_FAIL = 2;
	
	private View topBgImg;
	
	private LinearLayout mAvatarLL;
	private ImageView mAvatar;
	private TextView mName;
	
	private RelativeLayout mRlayout;
	private TextView mPoint;
	private TextView mMoney;
	private Button mAttendanceBtn;
	
	private LinearLayout mHelpLayoput;
	
	private LoadFullListView listView;
	private View mListBottomLine;
	
	private DirectorateTaskAdapter adapter;

	private List<DirectorateTaskBean> mList;

	private DirectorateBean mBean;

	protected LayerDrawable bg;

	protected Bitmap bkg;

	private TitleBar titleBar;

	private LoadStateManager mLoadStateManager;

	private ProgressRefreshView mProgressRefresh;

	@Override
	public void onCreate(Bundle paramBundle) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_directorate);
		if (null != paramBundle) {
			mBean = (DirectorateBean) paramBundle.getSerializable("bean");
		}
		super.onCreate(paramBundle);
	}

	@Override
	public void initViews() {

		titleBar = new TitleBar(getActivity(), true);
		titleBar.setTitle("指挥部", getResources().getColor(R.color.white));
		titleBar.setBackText("返回", getResources().getColor(R.color.white), getResources().getDrawable(R.drawable.nav_back_white));
		titleBar.setBackTextBackgroundColor(getResources().getColor(R.color.transparent));
		titleBar.setBackgroundColor(getResources().getColor(R.color.transparent));
		
		mProgressRefresh = new ProgressRefreshView(getActivity(), true);
		
		topBgImg = findViewById(R.id.directorate_bg_img);
		
		mAvatarLL = (LinearLayout) findViewById(R.id.directorate_avatar_ll);
		mAvatar = (ImageView) findViewById(R.id.directorate_avatar_img);
		mName = (TextView) findViewById(R.id.directorate_name);
//		mMedal = (TextView) findViewById(R.id.directorate_medal);
		mRlayout = (RelativeLayout) findViewById(R.id.directorate_poin_or_money_rl);
		mPoint = (TextView) findViewById(R.id.directorate_poin_tv);
		mMoney = (TextView) findViewById(R.id.directorate_money_tv);
		mAttendanceBtn = (Button) findViewById(R.id.directorate_attendance_btn);
		
		mHelpLayoput = (LinearLayout) findViewById(R.id.directorate_task_help_ll);
		listView = (LoadFullListView) findViewById(R.id.directorate_listview);
		mListBottomLine = findViewById(R.id.directorate_listview_bottom_line);
	}

	@Override
	public void initDatas() {

		mLoadStateManager = new LoadStateManager();
		mLoadStateManager.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {
			
			@Override
			public void OnStateChange(LoadState state, Object obj) {
				switch (state) {
				case Init:
					mAvatarLL.setVisibility(View.GONE);
					listView.setVisibility(View.GONE);
					mHelpLayoput.setVisibility(View.GONE);
					mRlayout.setVisibility(View.GONE);
					mListBottomLine.setVisibility(View.GONE);
					mProgressRefresh.setWaitVisibility(true);
					mProgressRefresh.setRefreshVisibility(false);
					break;
				case Success:
					mAvatarLL.setVisibility(View.VISIBLE);
					listView.setVisibility(View.VISIBLE);
					mHelpLayoput.setVisibility(View.VISIBLE);
					mRlayout.setVisibility(View.VISIBLE);
					mListBottomLine.setVisibility(View.VISIBLE);
					mProgressRefresh.setRootViewVisibility(false);
					break;
				case Failure:
					mAvatarLL.setVisibility(View.GONE);
					listView.setVisibility(View.GONE);
					mHelpLayoput.setVisibility(View.GONE);
					mRlayout.setVisibility(View.GONE);
					mListBottomLine.setVisibility(View.GONE);
					mProgressRefresh.setWaitVisibility(false);
					mProgressRefresh.setRefreshVisibility(true);
					break;
				default:
					break;
				}
			}
		});
		
		mLoadStateManager.setState(LoadState.Init);
		if (null != mBean) {
			Message message = getHandler().obtainMessage(MSG_UI, mBean);
			getHandler().sendMessage(message);
		} else {
			getDirectorateMsg(getActivity());
		}
		
	};
	
	@SuppressLint("NewApi")
	@Override
	public void processHandlerMessage(Message msg) {

		switch (msg.what) {
		case MSG_UI :
			mBean =(DirectorateBean) msg.obj;
			if (null != mBean) {
				Utils.setCircleImage(mBean.getMember().getFace(), mAvatar);
				// username rolename
				mName.setText(mBean.getMember().getNickname());
				// user medal
				
				StringBuffer mRoleName = new StringBuffer();
				mRoleName.append("军衔：").append(mBean.getMember().getRole_name());
				StringBuffer mExtcredits = new StringBuffer();
				mExtcredits.append("积分：").append(mBean.getMember().getExtcredits1());
				
				mPoint.setText(mRoleName.toString());
				mMoney.setText(mExtcredits.toString());
				mList = new ArrayList<DirectorateTaskBean>();
				mList= mBean.getTask_list();
				adapter = new DirectorateTaskAdapter(getActivity(), mList);
				listView.setAdapter(adapter);
				setBackground(mBean);
			}
			
			break;
		case BG_SUCCESS:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
              	topBgImg.setBackground(bg);
			} else {
				topBgImg.setBackgroundDrawable(bg);
			}
            bg = null;
            mLoadStateManager.setState(LoadState.Success);
			break;
			
		case HTTP_FAIL :
			
			mLoadStateManager.setState(LoadState.Failure);

		default:
			break;
		}
	}
	
	private void setBackground(DirectorateBean bean) {
		
		ImageLoader.getInstance().loadImage(bean.getMember().getFace(), 
				  new SimpleImageLoadingListener(){

				@Override
				public void onLoadingComplete(String imageUri,
						View view, final Bitmap loadedImage) {
					topBgImg.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

						private boolean hasMeasured;

						@Override
						public boolean onPreDraw() {
							if (hasMeasured == false) {
								DisplayUtil.getInst().init(getActivity());
								int with = DisplayUtil.getInst().getScreenWidth();
								int height = topBgImg.getMeasuredHeight();
								Bitmap bmp = ImageUtils.bigBitmap(loadedImage, with, height);
			  	                bg = Utils.blur(getActivity(), bmp, topBgImg);
			  	                bmp.recycle();
			  	                getHandler().obtainMessage(BG_SUCCESS,bg).sendToTarget();
			  	                hasMeasured = true;
							}
							return true;
						}
					});
					
					super.onLoadingComplete(imageUri, view, loadedImage);
				}	
			
		});
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        
        savedInstanceState.putSerializable("bean", mBean);
        super.onSaveInstanceState(savedInstanceState);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
		mBean = (DirectorateBean) savedInstanceState.getSerializable("bean");
		super.onRestoreInstanceState(savedInstanceState);
    }
	
	/**
	 * 获取指挥部信息
	 * @param context
	 */
	private void getDirectorateMsg(final Activity context) {
		HttpUtil.get(JUrl.SITE + JUrl.DRRECTORATE_URL, new MyJsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				
				Common.handleHttpFailure(context, throwable);
				getHandler().obtainMessage(HTTP_FAIL).sendToTarget();
			}

			@Override
	         public void onFinish() {
	        	 
	         }

			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(data);
				
					Gson gson = new Gson();
					DirectorateMemberBean bean = gson.fromJson(jsonObj.optString("member"), DirectorateMemberBean.class);
					
					ArrayList<DirectorateTaskBean> taskBeans = gson.fromJson(jsonObj.optJSONArray("task_list").toString(), 
							new TypeToken<ArrayList<DirectorateTaskBean>>(){}.getType());
					DirectorateBean directorateBean = new DirectorateBean();
					directorateBean.setMember(bean);
					directorateBean.setTask_list(taskBeans);
					getHandler().obtainMessage(MSG_UI, directorateBean).sendToTarget();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				if (-99 == status) {
					LoginManager.noticeNotLaunched(context);
				}
			}
			
		});
		
	}

	@Override
	public void installListeners() {

		mAttendanceBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mProgressRefresh.setRefreshOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				getDirectorateMsg(getActivity());
			}
		});
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DirectorateTaskDetailActivity.launch(getActivity(), mList.get(position).getAction());
			}
		});
		
	}

	@Override
	public void uninstallListeners() {
		
	}

	public static void launch(Activity context) {
		Intent intent = new Intent(context, DirectorateActivity.class);
		context.startActivity(intent);
	}

	@Override
	public Activity getActivity() {
		return DirectorateActivity.this;
	}

}
