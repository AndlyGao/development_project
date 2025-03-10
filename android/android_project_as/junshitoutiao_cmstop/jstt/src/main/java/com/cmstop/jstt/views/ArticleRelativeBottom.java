package com.cmstop.jstt.views;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmstop.jstt.R;
import com.cmstop.jstt.ad.AdDataManager;
import com.cmstop.jstt.adapter.ArticleRelativeItemAdapter;
import com.cmstop.jstt.beans.data.ArticlesBean;
import com.cmstop.jstt.beans.data.MChannelItemBean;
import com.cmstop.jstt.utils.Common;

public class ArticleRelativeBottom {
	
	private Activity mContext;
	
	private View mLayout;
	private LinearLayout mList;
	private View mNoData;

	private String aid;

	private ArticlesBean mArticleBean;

	private MChannelItemBean mChannelBean;

	private TextView mTitle;
	
	// 文章页相关推荐、相关文章
	public ArticleRelativeBottom(Activity activity, final View root){
		this.mContext = activity;
		mLayout = root.findViewById(R.id.article_relative_bottom_layout);
		mTitle = (TextView) root.findViewById(R.id.article_relative_bottom_title);
		mList = (LinearLayout) root.findViewById(R.id.article_relative_bottom_list);
		mNoData = root.findViewById(R.id.article_relative_bottom_no_data);
		
		mNoData.setVisibility(View.GONE);
	}
	
	public void setChannelBean(final MChannelItemBean bean){
		this.mChannelBean = bean;
		this.aid = bean.getAid();
	}
	
	public void setData(ArticlesBean bean){
		this.mArticleBean = bean;
		mArticleBean.setTid(aid);
		ArrayList<MChannelItemBean> dataList = bean.getRelations();
		
		boolean isHasRelative = !Common.isListEmpty(dataList);
		if (isHasRelative ) {
			mLayout.setVisibility(View.VISIBLE);
			// ad
			AdDataManager ad = new AdDataManager(mContext);
			MChannelItemBean adBig = bean.getAdd_code_big();
			int offset = adBig != null && adBig.getAd_type() != 0 ? 1 :0;

			handleRelative(mList, dataList, ad, offset);
		}else{
			mLayout.setVisibility(View.GONE);
			
//			int height = mContext.getResources().getDimensionPixelSize(R.dimen.article_comment_empty_height);
//			LayoutParams lp = mList.getLayoutParams();
//			lp.width = LayoutParams.MATCH_PARENT;
//			lp.height = height;
//			mList.setLayoutParams(lp);
//			
//			mNoData.requestLayout();
//			mNoData.invalidate();
//			mNoData.setVisibility(View.VISIBLE);
		}


		/*** close baidu block
		 // debug appsid blockid 来自demo
		 mLayout.setVisibility(View.VISIBLE);
		 CpuAdView cad = new CpuAdView(mContext, "f0770880", "5922", null);
		 //		CpuAdView cad = new CpuAdView(mContext, "d77e414", "2365", null);
		 LayoutParams clp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		 //		mList.removeAllViews();
		 mList.addView(cad, clp);
		 ***/
	}
	
	private void handleRelative(LinearLayout layout,
			ArrayList<MChannelItemBean> dataList, AdDataManager ad){
		handleRelative(layout, dataList, ad, 0);
	}
	
	private void handleRelative(LinearLayout layout,
			ArrayList<MChannelItemBean> dataList, AdDataManager ad, int adOffset) {
		if(!Common.isListEmpty(dataList)){
			
			//			mNoData.setVisibility(View.INVISIBLE);
			LayoutParams lp = layout.getLayoutParams();
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = LayoutParams.WRAP_CONTENT;
			layout.setLayoutParams(lp);
			
			layout.removeAllViews();
			ArticleRelativeItemAdapter mAdapter = new ArticleRelativeItemAdapter(mContext, dataList, ad, adOffset);
			int size = mAdapter.getCount();
			for(int i = 0; i < size; i++){
				View v = mAdapter.getView(i, null, null);
				if(i == size - 1){
					v.findViewById(R.id.item_channel_item_line).setVisibility(View.INVISIBLE);
				}
				layout.addView(v);
			}
		}
	}
	
	public void show(boolean isShowed) {
		mLayout.setVisibility(isShowed ? View.VISIBLE : View.GONE);
	}
	
	public void setTitle(String title){
		mTitle.setText(title);
	}
	
	/**
	 * 
	 * @param titleId R.String.xxx
	 */
	public void setTitle(int titleId){
		mTitle.setText(mContext.getString(titleId));
	}

}
