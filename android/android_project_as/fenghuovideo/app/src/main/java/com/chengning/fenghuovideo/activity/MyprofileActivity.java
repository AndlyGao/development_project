package com.chengning.fenghuovideo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.util.PermissionManager;
import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuovideo.App;
import com.chengning.fenghuovideo.LoginManager;
import com.chengning.fenghuovideo.MyJsonHttpResponseHandler;
import com.chengning.fenghuovideo.R;
import com.chengning.fenghuovideo.SettingManager;
import com.chengning.fenghuovideo.data.access.UserinfoOtherServer;
import com.chengning.fenghuovideo.data.bean.UserInfoBean;
import com.chengning.fenghuovideo.event.NicknameModifyEvent;
import com.chengning.fenghuovideo.util.Common;
import com.chengning.fenghuovideo.util.ImagePickHelper;
import com.chengning.fenghuovideo.util.JUrl;
import com.chengning.fenghuovideo.util.UIHelper;
import com.chengning.fenghuovideo.util.Utils;
import com.chengning.fenghuovideo.widget.NicknameModifyDialog;
import com.chengning.fenghuovideo.widget.TitleBar;
import com.chengning.fenghuovideo.widget.TitleBar.BackOnClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class MyprofileActivity extends BaseFragmentActivity {
	private static final int GetInfo_Sucess = 4; // 保存完成
	// private static final int Save_Sucess = 55;
	public static final int ABOUT_REQUEST_CHANGE = 113;
	// private final String[] sex = { "未填写", "男", "女" };
	private final String[] sexdialog = { "男", "女" };
	private App mApp;
	private UserInfoBean mUserInfoBean;
	private ImagePickHelper mImagePickHelper;

	private int Savecount = 0;
	private boolean isAboutMeChange;
	private String mFinalFace = "finalface.jpg";

	private RelativeLayout mUserImgBtn;
	private RelativeLayout mGenderBtn;
	private RelativeLayout mNameBtn;
	private RelativeLayout mLevelBtn;
	private ImageView mUserImg;
	private TextView logoutButton;
	private TextView mNameTv;
	private TextView mPointTv;
	private TextView mLevelTv;
	private TextView mGenderTv;
	private TextView mIntroductionTv;
	private TextView mGoldTv;
	private TitleBar mTitleBar;
	private ImageView mImgBoult;
	 private ImageView mNameBoult;
	private ImageView mGenderBoult;
	private ImageView mIntroductionBoult;

	private RelativeLayout mLabelBtn;
	private TextView mLabelTv;

	private String uid;

	private RelativeLayout mIntroBtn;
	private PermissionManager permissionManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_my_profile);
		super.onCreate(savedInstanceState);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果是返回键,直接返回到桌面
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Savecount = 1;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static void launchByUid(Activity from, String uid, String action) {
		Intent intent = new Intent(from, MyprofileActivity.class);
		intent.putExtra("uid", uid);
		intent.putExtra("action", action);
		from.startActivity(intent);
	}

	public static void launchByBean(Activity from, UserInfoBean bean,
			String action) {
		Intent intent = new Intent(from, MyprofileActivity.class);
		intent.putExtra("bean", bean);
		intent.putExtra("action", action);
		from.startActivity(intent);
	}

	@Override
	public Activity getActivity() {
		return MyprofileActivity.this;
	}

	@Override
	public void initViews() {

		mTitleBar = new TitleBar(getActivity(), true);
		mTitleBar.showDefaultBack();
		// mTitleBar.setTitle("我的资料");

		mUserImgBtn = (RelativeLayout) this.findViewById(R.id.infoedit_img_rl);
		mUserImg = (ImageView) this.findViewById(R.id.infoedit_img);
		logoutButton = (TextView) this.findViewById(R.id.infoedit_logout);
		mNameTv = (TextView) this.findViewById(R.id.infoedit_name_edit);
		mIntroductionTv = (TextView) this
				.findViewById(R.id.info_introduction_tv);
		mLevelBtn = (RelativeLayout) this.findViewById(R.id.infoedit_level_rl);
		mLevelTv = (TextView) this.findViewById(R.id.infoedit_level_tv);
		mPointTv = (TextView) this.findViewById(R.id.infoedit_point_tv);
		mGoldTv = (TextView) this.findViewById(R.id.infoedit_gold_tv);
		mGenderBtn = (RelativeLayout) this
				.findViewById(R.id.infoedit_gender_rl);
		mGenderTv = (TextView) this.findViewById(R.id.info_gender_tv);
		mNameBtn = (RelativeLayout) this.findViewById(R.id.infoedit_name_rl);
		mIntroBtn = (RelativeLayout) this
				.findViewById(R.id.infoedit_introduction_rl);

		mImgBoult = (ImageView) findViewById(R.id.infoedit_img_boult);
		mNameBoult = (ImageView) findViewById(R.id.infoedit_name_boult);
		mGenderBoult = (ImageView) findViewById(R.id.infoedit_gender_boult);
		mIntroductionBoult = (ImageView) findViewById(R.id.infoedit_introduction_boult);

	}

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public void initDatas() {

		String mAction = getIntent().getStringExtra("action");
		EventBus.getDefault().register(getActivity());
		if (TextUtils.isEmpty(mAction)) {
			finish();
			return;
		}

		initUserInfo(mAction);
		mImagePickHelper = new ImagePickHelper(getActivity(), mFinalFace);
	}

	/**
	 * 初始化用户信息
	 * 
	 * @param action
	 */
	private void initUserInfo(String action) {
		mApp = App.getInst();

		if ("more".equals(action)) {
			mUserInfoBean = (UserInfoBean) getIntent().getSerializableExtra(
					"bean");
			handleInfo(mUserInfoBean.getUid());
		} else if ("direct".equals(action)) {

			uid = App.getInst().getUserInfoBean().getUid();
			handleInfo(uid);
			if (Common.hasNet()) {
				getUserInfo(uid);
			}
		}

		setDataForViews();

	}

	private void handleInfo(String uid) {

		if (mApp.isLogin() && mApp.getUserInfoBean().getUid().equals(uid)) {

			mUserInfoBean = mApp.getUserInfoBean();
			handleInfoIsEditable(true);
		} else {
			mUserInfoBean = UserinfoOtherServer.getInst(getActivity())
					.queryTargetUid(uid);
			handleInfoIsEditable(false);
		}
	}

	/**
	 * 处理资料是否可修改
	 * 
	 * @param isEditable
	 */
	private void handleInfoIsEditable(boolean isEditable) {
		mTitleBar.setTitle(isEditable ? "我的资料" : "资料");
		mUserImgBtn.setEnabled(isEditable ? true : false);
		mNameBtn.setEnabled(isEditable ? true : false);
		mGenderBtn.setEnabled(isEditable ? true : false);
		mIntroBtn.setEnabled(isEditable ? true : false);

		logoutButton.setVisibility(isEditable ? View.VISIBLE : View.GONE);
		mImgBoult.setVisibility(isEditable ? View.VISIBLE : View.GONE);
		mNameBoult.setVisibility(isEditable ? View.VISIBLE : View.GONE);
		mGenderBoult.setVisibility(isEditable ? View.VISIBLE : View.GONE);
		mIntroductionBoult.setVisibility(isEditable ? View.VISIBLE
				: View.INVISIBLE);
		if (!isEditable) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIntroductionBoult
					.getLayoutParams();
			params.width = 0;
			params.height = 0;
			params.setMargins(0, 0, 0, 0);
			mIntroductionBoult.setLayoutParams(params);
		}

	}

	public void setDataForViews() {
		if (null != mUserInfoBean) {
			if (!TextUtils.isEmpty(mUserInfoBean.getNickname())) {
				mNameTv.setText(mUserInfoBean.getNickname());
			}

			if (!TextUtils.isEmpty(mUserInfoBean.getRole_name())) {
				mLevelTv.setText(mUserInfoBean.getRole_name());
			}
			mGenderTv.setText(Utils.getGenderString(mUserInfoBean.getGender()));
			if (!TextUtils.isEmpty(mUserInfoBean.getAboutme())) {
				mIntroductionTv.setText(mUserInfoBean.getAboutme());
			}
			mGoldTv.setText(String.valueOf(mUserInfoBean.getExtcredits2()));
			mPointTv.setText(String.valueOf(mUserInfoBean.getExtcredits1()));
			Utils.handleDefaultAvatar(mUserInfoBean.getFace_original(), mUserImg);
			if (Common.isTrue(SettingManager.getInst().getNightModel())) {
				mUserImg.setColorFilter(getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
			}

		}
	}

	@Override
	public void installListeners() {
		mTitleBar.setBackOnClickListener(new BackOnClickListener() {

			@Override
			public void onClick() {
				Savecount = 1;
			}
		});

		logoutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginManager.getInst().logout();
				getActivity().finish();
			}
		});
		mUserImgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showImgDialog();
			}
		});
		mGenderBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showEditDialog("请选择性别");
			}
		});
		mNameBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handleNickname((BaseFragmentActivity) getActivity(),mNameTv.getText().toString());
			}
		});

		mIntroBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditMyDescriptionActivity.launchForResult(getActivity());
			}
		});
		mLevelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (LoginManager.getInst().checkLoginWithNotice(getActivity())) {
					MyGradeIntroActivity.launch(getActivity());
				}
			}
		});

	}

	/**
	 * 修改昵称
	 *
	 * @param name
	 */
	protected void handleNickname(BaseFragmentActivity activity, String name) {
		if (TextUtils.isEmpty(name)) {
			return;
		}
		NicknameModifyDialog dialog = new NicknameModifyDialog();
		dialog.setData(name);
		dialog.showAllowingStateLoss(activity, getSupportFragmentManager(), NicknameModifyDialog.class.getSimpleName());
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case GetInfo_Sucess:
			mUserInfoBean = (UserInfoBean) msg.obj;
			if (null != mApp.getUserInfoBean()
					&& mUserInfoBean.getUid().equals(
							mApp.getUserInfoBean().getUid())) {
				LoginManager.getInst().saveUserInfo(mUserInfoBean);
			}

			setDataForViews();
			break;
		default:

			break;
		}
	}

	public void getUserInfo(String uid) {
		HttpUtil.get(getActivity(), JUrl.SITE + JUrl.Get_USER_INFO + uid, null,
				new MyJsonHttpResponseHandler() {

					@Override
					public void onDataSuccess(int status, String mod,
											  String message, String data, JSONObject obj) {
						Gson gson = new Gson();
						UserInfoBean bean = gson.fromJson(data,
								new TypeToken<UserInfoBean>() {
								}.getType());
						getHandler().obtainMessage(GetInfo_Sucess, bean)
								.sendToTarget();
					}

					@Override
					public void onDataFailure(int status, String mod,
											  String message, String data, JSONObject obj) {
						UIHelper.removePD();
						Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
					}
				});
	}

	/***
	 * 图像
	 */
	private void showImgDialog() {
		permissionManager = new PermissionManager();
		mImagePickHelper.showPickDialog(permissionManager);
	}

	/**
	 * 回调结果处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (mImagePickHelper
				.handleActivityResult(requestCode, resultCode, data)) {
			EditFace();
		}
		switch (resultCode) {
		case ABOUT_REQUEST_CHANGE:
			isAboutMeChange = data.getBooleanExtra("about", false);

			if (isAboutMeChange) {
				mIntroductionTv.setText(App.getInst().getUserInfoBean()
						.getAboutme());
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void EditFace() {
		if (Common.hasNet()) {
			try {
				UIHelper.addPD(getActivity(),
						getResources().getString(R.string.handle_hint));
				File file = Common.creatFile(JUrl.FilePathTemp, mFinalFace);
				RequestParams rp = new RequestParams();
				rp.put("face", file, "image/jpeg");
				HttpUtil.post(getActivity(), JUrl.SITE + JUrl.Edit_Face, rp,
						new MyJsonHttpResponseHandler() {
							public void onFailure(int statusCode,
									Header[] headers, Throwable throwable,
									JSONObject errorResponse) {
								Common.handleHttpFailure(getActivity(), throwable);
							};

							@Override
							public void onFinish() {
								UIHelper.removePD();
							}

							@Override
							public void onDataSuccess(int status, String mod,
									String message, String data, JSONObject obj) {
//								App.getInst().setUserAvatarChange(true);
								if (App.getInst().getUserInfoBean().getUid() != null) {
									getUserInfo(App.getInst().getUserInfoBean()
											.getUid());
								}
								UIHelper.removePD();
							}

							@Override
							public void onDataFailure(int status, String mod,
									String message, String data, JSONObject obj) {
								UIHelper.showToast(getActivity(), message);
								UIHelper.removePD();
							};

						});
			} catch (FileNotFoundException e) {
				UIHelper.removePD();
				e.printStackTrace();
			}
		} else {

			UIHelper.showToast(getActivity(), R.string.intnet_fail);
		}
	}

	private void showEditDialog(String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
				R.style.EditDialog);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setSingleChoiceItems(sexdialog, mUserInfoBean.getGender() - 1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mUserInfoBean.setGender(which + 1);
					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mGenderTv.setText(Utils.getGenderString(mUserInfoBean
						.getGender()));
				EditGender();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}

	public void EditGender() {
		if (Common.hasNet()) {
			RequestParams params = new RequestParams();
			params.put("gender", mUserInfoBean.getGender());
			HttpUtil.post(JUrl.SITE + JUrl.URL_EDIT_GENDER, params,
					new MyJsonHttpResponseHandler() {

						@Override
						public void onDataSuccess(int status, String mod,
												  String message, String data, JSONObject obj) {
							UIHelper.showToast(getActivity(), "修改性别成功");
						}

						@Override
						public void onDataFailure(int status, String mod,
												  String message, String data, JSONObject obj) {
							UIHelper.showToast(getActivity(), "修改性别失败");

						}
					});
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(getActivity());
		super.onDestroy();
	}

	@Subscribe
	public void onEventMainThread(NicknameModifyEvent event) {
		updateNickname(event.getNickname());
	}

	/**
	 * 更新昵称
	 * @param nickname
	 */
	private void updateNickname(String nickname) {
		if (TextUtils.isEmpty(nickname)) {
			return;
		}
		mNameTv.setText(nickname);
		mUserInfoBean.setNickname(nickname);
		getHandler().obtainMessage(GetInfo_Sucess, mUserInfoBean)
				.sendToTarget();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		permissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
	}
}
