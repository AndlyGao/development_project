package com.chengning.fenghuo.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chengning.common.base.BaseFragment;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.data.bean.UserInfoBean;

public class TaskInviteApprenticeFragment extends BaseFragment {

	private static final String TAG = TaskInviteApprenticeFragment.class.getSimpleName();

	static final int INIT_DATA_UI = 1;

	private LayoutInflater mInflater;
	private Bundle savedInstanceState;
	
	private View mView;	
	private TextView mCode;
	private TextView mWeixin;
	private TextView mQQ;
	private TextView mSms;
	private TextView mFace;
	private TextView mShow;

	public TaskInviteApprenticeFragment() {
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.mInflater = inflater;
		mView = inflater.inflate(R.layout.fragment_task_invite_apprentice,
				container, false);
		return mView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}
	
	@Override
	public void initViews() {

		mCode = (TextView) mView.findViewById(R.id.task_invite_apprentice_code);
		mWeixin = (TextView) mView.findViewById(R.id.task_invite_apprentice_weixin);
		mQQ = (TextView) mView.findViewById(R.id.task_invite_apprentice_qq);
		mSms = (TextView) mView.findViewById(R.id.task_invite_apprentice_sms);
		mFace = (TextView) mView.findViewById(R.id.task_invite_apprentice_face);
		mShow = (TextView) mView.findViewById(R.id.task_invite_apprentice_show_income);
	}

	@Override
	public void initDatas() {

	}

	@Override
	public void installListeners() {
		mCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager myClipboard;
				myClipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
				ClipData myClip;
				// TODO(邀请码)
				String text = "123456789";
				myClip = ClipData.newPlainText("邀请码", text);
				myClipboard.setPrimaryClip(myClip);
				Toast.makeText(getActivity(), "邀请码已复制到剪贴板", Toast.LENGTH_LONG).show();
			}
		});
		mWeixin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO()
			}
		});
		mQQ.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO()
			}
		});
		mSms.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO()
			}
		});
		mFace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO()
			}
		});
		mShow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO()
			}
		});
	}

	@Override
	public void uninstallListeners() {

	}


	public void setUserData(UserInfoBean bean) {
		sendDataMessage(INIT_DATA_UI, bean);
	}

	public void sendDataMessage(int what, Object obj) {
		if (getContext() != null) {
			Message message = getHandler().obtainMessage(what, obj);
			message.sendToTarget();
		}
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case INIT_DATA_UI:
			
			break;
		default:
			break;
		}
	}

	public void onResume() {
		super.onResume();

	}

	public void onPause() {
		super.onPause();
	}

}
