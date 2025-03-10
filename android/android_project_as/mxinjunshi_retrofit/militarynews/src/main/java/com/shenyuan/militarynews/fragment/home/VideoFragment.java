package com.shenyuan.militarynews.fragment.home;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengning.common.util.SerializeUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.activity.ExclusiveDetailActivity;
import com.shenyuan.militarynews.beans.data.MChannelBean;
import com.shenyuan.militarynews.beans.data.MChannelItemBean;
import com.shenyuan.militarynews.beans.data.MChannelNavBean;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.SPHelper;
import com.shenyuan.militarynews.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends AbstractChannelItemListFragment {

	private LinearLayout mHeader;
	private PullToRefreshListView mListView;

	@Override
	public void initDatas(){
		super.initDatas();
//		setHomeRecom(true);
	}

	@Override
	public String buildTAG() {
		return VideoFragment.class.getSimpleName();
	}
	
	@Override
	public String buildChannel() {
		return "exclusive";
	}

	@Override
	public String buildUrl(int tarPage) {
		String url = JUrl.SITE + JUrl.URL_GET_CHANNEL_EXCLUSIVE;
		url =JUrl.appendPage(url, tarPage);
		url = appendMaxid(url, tarPage);
		return url.toString();
	}
	
	@Override
	public List<MChannelItemBean> onHttpSuccess(Gson gson, String data,
			MChannelBean channelBean, int tarPage) {
		if (tarPage == JUrl.PAGE_START) {
			ArrayList<MChannelNavBean> nav = channelBean.getNav();
			String navStr = SerializeUtil.serialize(nav);
			SPHelper.getInst().saveString(SPHelper.KEY_CHANNEL_EXCLUSIVE_NAV, navStr);
		}
		
		return super.onHttpSuccess(gson, data, channelBean, tarPage);
	}
	@Override
	public void configPullToRefreshListView_FootLoad(PullToRefreshListView v) {
		this.mListView = v;
		mHeader = new LinearLayout(getContext());
		mHeader.setOrientation(LinearLayout.VERTICAL);
		mListView.getRefreshableView().addHeaderView(mHeader);
		super.configPullToRefreshListView_FootLoad(v);
	}
	
	@Override
	public void onInitNewsDataUI(){
		ArrayList<MChannelNavBean> nav = (ArrayList<MChannelNavBean>) SerializeUtil.deSerialize(SPHelper.getInst().getString(SPHelper.KEY_CHANNEL_EXCLUSIVE_NAV));
		addHeader(nav);
		super.onInitNewsDataUI();
	}

	private void addHeader(ArrayList<MChannelNavBean> nav) {
		if (!Common.isListEmpty(nav)) {
			mHeader.removeAllViews();
			View header = LayoutInflater.from(getContext()).inflate(R.layout.header_home_video, null);
			setHeader(header, nav);
			mHeader.addView(header);
			
		} else {
			mHeader.removeAllViews();
		}
	}

	private void setHeader(View header, final ArrayList<MChannelNavBean> nav) {
		RecyclerView mRecyclerView = (RecyclerView) header.findViewById(R.id.header_video_nav);  
        //设置布局管理器  
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());  
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);  
        mRecyclerView.setLayoutManager(linearLayoutManager);  
        //设置适配器  
        GalleryAdapter mHeaderAdapter = new GalleryAdapter(getContext(), nav);  
        mRecyclerView.setAdapter(mHeaderAdapter);
        mHeaderAdapter.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClickListener(View view, int position) {
				ExclusiveDetailActivity.launch(getContext(), nav.get(position));
			}
		});
	}
	
	public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {  
		
		private LayoutInflater mInflater;  
		private ArrayList<MChannelNavBean> mDatas;  
		protected OnItemClickListener itemOnListener;

		public GalleryAdapter(Activity context, ArrayList<MChannelNavBean> list) {  
		    mInflater = LayoutInflater.from(context);  
		    mDatas = list;  
		}  

		public class ViewHolder extends RecyclerView.ViewHolder { 
		    public ViewHolder(View arg0)  {  
		        super(arg0);  
		    }  
		
		    ImageView mAvatar;  
		    TextView mName;  
		}  

		@Override  
		public int getItemCount() {
			if (Common.isListEmpty(mDatas)) {
				return 0;
			}
			return mDatas.size();  
		}  

		/** 
		 * 设置值 
		 */  
		@Override  
		public void onBindViewHolder(final ViewHolder viewHolder, final int i){  
			MChannelNavBean bean = mDatas.get(i);
		    Utils.setCircleImage(getContext(), bean.getImage(), viewHolder.mAvatar);
		    viewHolder.mName.setText(bean.getName());
		    viewHolder.itemView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					itemOnListener.onItemClickListener(viewHolder.itemView, i);
				}
			});
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
			View view = mInflater.inflate(R.layout.item_header_home_video,  
					arg0, false);  
		    ViewHolder viewHolder = new ViewHolder(view);  
		
		    viewHolder.mAvatar = (ImageView) view.findViewById(R.id.item_header_home_video_avatar);  
		    viewHolder.mName = (TextView) view.findViewById(R.id.item_header_home_video_name);
		    return viewHolder; 
		}  
		
	    /**
	     * 设置点击事件监听器
	     *
	     * @param listener 监听器对象
	     */
	    public void setOnItemClickListener(OnItemClickListener listener) {
	        this.itemOnListener = listener;
	    }

	    
	}
	
	public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }
}  
