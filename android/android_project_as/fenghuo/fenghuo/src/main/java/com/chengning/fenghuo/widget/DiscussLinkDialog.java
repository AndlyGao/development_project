package com.chengning.fenghuo.widget;


import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.chengning.fenghuo.R;
import com.chengning.fenghuo.event.DiscussLinkEvent;
import com.chengning.fenghuo.util.UIHelper;

import de.greenrobot.event.EventBus;

public class DiscussLinkDialog extends DialogFragment{
	
	private View mLinkView;
	
	private EditText mTitleEt;
	private EditText mLinkEt;
	private TextView mCancelTv;
	private TextView mOkTv;
	
	private String mStrTitle = "";
	private String mStrLink = "";
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); 
	    LayoutInflater inflater = getActivity().getLayoutInflater(); 
	    mLinkView = inflater.inflate(R.layout.dialog_discuss_link, null); 
	    builder.setView(mLinkView);
	    
		return builder.create();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    
	    initView();
	    initData();
	    initListener();
	}



	public void initView() {
		mTitleEt = (EditText) mLinkView.findViewById(R.id.link_dialog_title);
		mLinkEt = (EditText) mLinkView.findViewById(R.id.link_dialog_link);
		mCancelTv = (TextView) mLinkView.findViewById(R.id.link_dialog_cancel);
		mOkTv = (TextView) mLinkView.findViewById(R.id.link_dialog_ok);
	}
	
	public void initListener() {
		
		mCancelTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		
		mOkTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mStrTitle = mTitleEt.getText().toString();
				mStrLink = mLinkEt.getText().toString();
				if(mStrTitle == null || mStrTitle.equals("")){
					UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_linktitle));
					return;
				}
				if(mStrLink == null || mStrLink.equals("")){
					UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_linkaddr));
					return;
				}
				EventBus.getDefault().post(new DiscussLinkEvent(mStrTitle, mStrLink));
				dismissAllowingStateLoss();
			}
		});

	}

	public void initData() {
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(getActivity() != null){
					((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
							.showSoftInput(mTitleEt, 0);
				}
			}

		}, 600);
		
		mTitleEt.setText(mStrTitle);
		mLinkEt.setText(mStrLink);
	}
	
	@Override
	public void dismissAllowingStateLoss() {
		super.dismissAllowingStateLoss();
	}

	public void setContent(String title, String link){
		this.mStrTitle = title;
		this.mStrLink = link;
	}
	
	
}
