package com.chengning.fenghuo.widget;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.chengning.fenghuo.R;
import com.chengning.fenghuo.activity.CircleChangeBgActivity;

public class CircleChangeBgDialog extends DialogFragment{
	
	private View mChangeBgView;
	private TextView mTvChangeBg;
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); 
	    LayoutInflater inflater = getActivity().getLayoutInflater(); 
	    mChangeBgView = inflater.inflate(R.layout.dialog_circle_changebg, null); 
	    builder.setView(mChangeBgView);
	    
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
//	    initData();
	    initListener();
	}	
	
	private void initView(){
		mTvChangeBg = (TextView) mChangeBgView.findViewById(R.id.tv_changebg);
	}
	
	private void initListener(){
		mTvChangeBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
				startActivity(new Intent(getActivity(), CircleChangeBgActivity.class));
			}
		});
	}
}
