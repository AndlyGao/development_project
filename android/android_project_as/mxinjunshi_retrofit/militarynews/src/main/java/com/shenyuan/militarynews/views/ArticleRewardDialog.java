package com.shenyuan.militarynews.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.chengning.common.base.BaseDialogFragment;
import com.chengning.common.base.BaseResponseBean;
import com.chengning.common.base.MyRetrofitResponseCallback;
import com.chengning.common.base.util.RetrofitManager;
import com.chengning.common.util.DisplayUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.beans.data.RewardNumBean;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.wxapi.WXPayEntryActivity;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class ArticleRewardDialog extends BaseDialogFragment{
	
	private View mReward;
	private Button mBtnClose;
	private LinearLayout mPriceLl;
	private Button mBtnPay;
	
	private String mArticleTid;
	private int mCurrentIndex;
	private List<RadioButton> mRadioList;
	
	private List<RewardNumBean> mRewardNumList;
	
	@Override
	public void initView() {
		mBtnClose = (Button) mReward.findViewById(R.id.reward_dialog_close);
		mPriceLl = (LinearLayout) mReward.findViewById(R.id.reward_dialog_price_ll);
		mBtnPay = (Button) mReward.findViewById(R.id.reward_btn_pay);
	}
	
	@Override
	public void initListener() {
		mBtnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		
		mBtnPay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pay();
			}
		});

	}

	@Override
	public void initData() {
		
		mRadioList = new ArrayList<RadioButton>();
		
		if(mRewardNumList == null){
			mRewardNumList = new ArrayList<RewardNumBean>();
		}
		
		int maxNum = mRewardNumList.size();
		
		boolean isOdd = false;
		int row = 0;
		if(maxNum%2 == 0){
			row = maxNum / 2;
		}else{
			row = maxNum /2 + 1;
			isOdd = true;
		}
		
		for(int i = 0; i<row; i++){
			RewardNumBean bean1 = mRewardNumList.get(i*2);
			RewardNumBean bean2 = mRewardNumList.get(i*2 + 1);
			View view = LayoutInflater.from(getContext()).inflate(R.layout.item_reward_dialog_price, null);
			RadioButton mRadioIndex1 = (RadioButton) view.findViewById(R.id.reward_btn_index1);
			RadioButton mRadioIndex2 = (RadioButton) view.findViewById(R.id.reward_btn_index2);
			mRadioIndex1.setTag(i*2);
			mRadioIndex1.setText(doTextSize(bean1.getPrice()+""));
			mRadioList.add(mRadioIndex1);
			if(i == row - 1 && isOdd){
				mRadioIndex2.setVisibility(View.INVISIBLE);
			}else{
				mRadioIndex2.setTag(i*2 + 1);
				mRadioIndex2.setText(doTextSize(bean2.getPrice()+""));
				mRadioList.add(mRadioIndex2);
			}
			mPriceLl.addView(view);
		}
		
		for (final RadioButton item : mRadioList) {
			item.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						updateRadioUI((Integer)(item.getTag()));
					}
				}
			});
		}
		
		if(maxNum != 0){
			mCurrentIndex = 0;
			mRadioList.get(mCurrentIndex).setChecked(true);
		}
		
		api = WXAPIFactory.createWXAPI(getContext(), Const.WX_APP_ID, false);
    	if (api.isWXAppInstalled()) {//判断是否安装微信客户端  
    		api.registerApp(Const.WX_APP_ID);
    	} else {
    		UIHelper.showToast(getContext(), getResources().getString(R.string.no_wx_no_pay));
    		mBtnPay.setEnabled(false);
    	}

    	DisplayUtil.getInst().init(getContext());
	}
	
	@Override
	public View configContentView() {
		mReward = LayoutInflater.from(getContext()).inflate(R.layout.dialog_article_reward, null);
		return mReward;
	}
	
	private void updateRadioUI(int index){
		mCurrentIndex = index;
		for (RadioButton mRadio : mRadioList) {
			mRadio.setChecked(false);
		}
		mRadioList.get(index).setChecked(true);
	}
	
	private Spannable doTextSize(String num){
		Spannable strSpan = new SpannableString(num + "元");
		int index = strSpan.toString().indexOf("元");
		strSpan.setSpan(new AbsoluteSizeSpan(DisplayUtil.getInst().dip2px(20)), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
		strSpan.setSpan(new AbsoluteSizeSpan(DisplayUtil.getInst().dip2px(14)), index, strSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
		return strSpan;
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		mPriceLl.removeAllViews();
	}

	private IWXAPI api;
	private void pay(){
		if(mRewardNumList.size() == 0){
			mBtnPay.setEnabled(false);
			return;
		}

		HashMap params = new HashMap<String, String>();
		params.put("aid", mArticleTid);
//		params.put("money", "0.01");
		params.put("money", mRewardNumList.get(mCurrentIndex).getPrice());
		Observable<BaseResponseBean> observable
				= App.getInst().getApiInterface().post(JUrl.URL_GET_REWARD_ORDER, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

			@Override
			public void onSubscribe(Disposable d) {
				UIHelper.addPD(getActivity(), getResources().getString(R.string.handle_hint));
				super.onSubscribe(d);
			}

			@Override
			public void onComplete() {
				UIHelper.removePD();
				super.onComplete();
			}

			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
				mBtnPay.setEnabled(true);
				startActivity(new Intent(getActivity(), WXPayEntryActivity.class));
				JsonObject result =  new JsonParser().parse(data).getAsJsonObject();
				PayReq req = new PayReq();
				req.appId			= result.get("appid").getAsString();
				req.partnerId		= result.get("partnerid").getAsString();
				req.prepayId		= result.get("prepayid").getAsString();
				req.nonceStr		= result.get("noncestr").getAsString();
				req.timeStamp		= result.get("timestamp").getAsString();
				req.packageValue	= result.get("package").getAsString();
				req.sign			= result.get("sign").getAsString();
				req.extData			= "app data"; // optional
				// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
				api.sendReq(req);
			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				mBtnPay.setEnabled(true);
				UIHelper.showToast(getActivity(), message);
			}

			@Override
			public void onError(Throwable t) {
				UIHelper.removePD();
				mBtnPay.setEnabled(true);
				Common.handleHttpFailure(getActivity(), t);
				super.onError(t);
			}
		});
	}

	public String getmArticleTid() {
		return mArticleTid;
	}

	public void setmArticleTid(String mArticleTid) {
		this.mArticleTid = mArticleTid;
	}

	public List<RewardNumBean> getmRewardNumList() {
		return mRewardNumList;
	}

	public void setmRewardNumList(List<RewardNumBean> mRewardNumList) {
		this.mRewardNumList = mRewardNumList;
	}
	
}
