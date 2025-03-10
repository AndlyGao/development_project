package com.chengning.fenghuo.widget;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengning.common.base.BaseDialogFragment;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.activity.LoginActivity;
import com.chengning.fenghuo.adapter.PointHintNoCompleteTaskAdapter;
import com.chengning.fenghuo.data.bean.CreditsRuleBean;

import java.util.ArrayList;

public class NoLoginHongbaoHintDialog extends BaseDialogFragment {

	private View mCloseBtn;

	private Button mRegBtn;
	private TextView mMoneyTv;

	private View mView;
	private View mEmpty;

	private String mMoney;


	public void setData(String money){
		Bundle bundle = new Bundle();
		bundle.putString("money",money);
		setArguments(bundle);
	}

	@Override
	public void initData() {
		mMoney = getArguments().getString("money");
		if (!TextUtils.isEmpty(mMoney)) {
			mMoneyTv.setText("￥" + mMoney);
		}
	}

	@Override
	public void initView() {
		mEmpty = mView.findViewById(R.id.dialog_point_hint_empty);
		mCloseBtn = mView.findViewById(R.id.dialog_close_rl);
		mRegBtn = (Button) mView.findViewById(R.id.dialog_reg_btn);
		mMoneyTv = (TextView) mView.findViewById(R.id.dialog_reg_money_tv);

	}


	@Override
	public View configContentView() {
		mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nologin_hongbao_hint, null);
		return mView;
	}

	@Override
	public void initListener() {

		mEmpty.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				dismissAllowingStateLoss();
			}
		});

		mCloseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});

		mRegBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LoginActivity.launch(getActivity());
				dismissAllowingStateLoss();
			}

		});
	}
	
}
