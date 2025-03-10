package com.cmstop.jstt.views;

import java.util.ArrayList;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.TextView;

import com.chengning.common.base.BaseDialogFragment;
import com.chengning.common.base.BaseFragmentActivity;
import com.cmstop.jstt.adapter.PointHintNoCompleteTaskAdapter;
import com.cmstop.jstt.beans.data.CreditsRuleBean;
import com.cmstop.jstt.R;

public class PointHintDialog extends BaseDialogFragment {
	
	private View mCloseBtn;
	private View mBg;
	private View mTopBg;
	private TextView mTitleTv;
	private TextView contentTv;
	private TextView mTaskTv;
	private LoadFullListView mListView;
	private TextView confirmTv;
	private SpannableStringBuilder content;
	private String title;
	private ArrayList<CreditsRuleBean> list;
	private DialogOnClickListener mListener;
	private View mView;
	private View mEmpty;
	private ArrayList<SpannableStringBuilder> mRuleLists;
	protected boolean hasMeasured;
	
	public void setData(String titleBuilder,SpannableStringBuilder nameBuilder, ArrayList<CreditsRuleBean> list, final DialogOnClickListener listener){
		this.title = titleBuilder;
		this.content = nameBuilder;
		this.list = list;
		this.mListener = listener;
	}


	@Override
	public void initData() {
		
		if (null != list) {
			initList(list);
		}
		
		if (null != content) {
			contentTv.setText(content);
		} else {
			contentTv.setText("");
		}
		if (null != title) {
			mTitleTv.setText(title);
		} else {
			mTitleTv.setText("");
		}
		
			
	}

	private void initList(ArrayList<CreditsRuleBean> list) {
			mRuleLists = new ArrayList<SpannableStringBuilder>();
			for (CreditsRuleBean list2 : list) {
				if ("未完成".equals(list2.getComplete())){
					int spStart = 0;
					int spEnd = 0;
					SpannableStringBuilder contentBuilder = new SpannableStringBuilder("");
					contentBuilder.append(list2.getRulename()).append("能获得 ");
					spStart = contentBuilder.length();
					contentBuilder.append(list2.getExtcredits1());
					spEnd = contentBuilder.length();
					ForegroundColorSpan sp = new ForegroundColorSpan(getContext().getResources().getColor(R.color.dialog_medal_color));
					contentBuilder.setSpan(sp, spStart, spEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					contentBuilder.append(" 积分。");
					mRuleLists.add(contentBuilder);
				}
				
			}
			if (0 < mRuleLists.size()) {
				mListView.setAdapter(new PointHintNoCompleteTaskAdapter(getContext(), mRuleLists));
			} else {
				mTaskTv.setText("干的不错！今天的任务已经全部完成，明天继续努力！");
			}
		
	}

	@Override
	public void initView() {
		mEmpty = mView.findViewById(R.id.dialog_point_hint_empty);
		mBg = mView.findViewById(R.id.dialog_point_hint_bg);
		mTopBg = mView.findViewById(R.id.dialog_point_hint_bg_rl);
		mCloseBtn = mView.findViewById(R.id.dialog_point_hint_close_btn);
		mTitleTv = (TextView) mView.findViewById(R.id.dialog_point_hint_title);
		contentTv = (TextView) mView.findViewById(R.id.dialog_point_hint_content);
		mTaskTv = (TextView) mView.findViewById(R.id.dialog_point_hint_task);
		mListView = (LoadFullListView) mView.findViewById(R.id.dialog_point_hint_list);
		confirmTv = (TextView) mView.findViewById(R.id.dialog_point_hint_confirm_btn);
		
	}

	public static interface DialogOnClickListener   {
		void onConfirmClick();
	}

	@Override
	public BaseFragmentActivity getContext() {
		return (BaseFragmentActivity)getActivity();
	}

	@Override
	public View configContentView() {
		mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_point_hint, null);
		return mView;
	}

	@Override
	public void initListener() {
		mTopBg.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				if (hasMeasured == false) {

					LayoutParams layoutParams = mBg.getLayoutParams();
					layoutParams.width = mTopBg.getMeasuredWidth();
					mBg.setLayoutParams(layoutParams);
                    hasMeasured = true;

                }
				return true;
			}
		});
		
		
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
		
		confirmTv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onConfirmClick();
				dismissAllowingStateLoss();
			}

		});
	}
	
}
