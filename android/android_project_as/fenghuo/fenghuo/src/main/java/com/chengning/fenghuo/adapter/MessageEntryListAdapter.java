package com.chengning.fenghuo.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengning.fenghuo.Consts.UserVip;
import com.chengning.fenghuo.PushManager;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.data.bean.MessageEntryBean;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.Utils;
import com.chengning.fenghuo.widget.DynamicTextView;

	public class MessageEntryListAdapter extends BaseAdapter { 
		private List<MessageEntryBean> mItemList;
		private Context mContext;  
		private OnClickListener mClickListener;
		private OnCreateContextMenuListener mContextMenuListener;
		
		public MessageEntryListAdapter(Context context, List<MessageEntryBean> list, 
				OnClickListener clickListener, OnCreateContextMenuListener contextMenuListener) {
			mItemList = list;
			mContext = context; 
			mClickListener = clickListener;
			mContextMenuListener = contextMenuListener;
		} 
		
		public int getCount() {
			return mItemList.size();
		}

		public Object getItem(int position) {
			return mItemList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}
		
		public class ViewHolder
		{
			ImageView image;
			ImageView userVipImage;
			TextView title;
			TextView stick;
			TextView time;
			DynamicTextView messageAbstract;
			TextView red;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = View.inflate(mContext, R.layout.item_message_entry, null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.message_entry_image);
				holder.userVipImage = (ImageView)convertView.findViewById(R.id.message_entry_user_vip_image);
				holder.title = (TextView) convertView.findViewById(R.id.message_entry_title);
				holder.stick = (TextView) convertView.findViewById(R.id.message_entry_stick);
				holder.time = (TextView) convertView.findViewById(R.id.message_entry_time);
				holder.messageAbstract = (DynamicTextView) convertView.findViewById(R.id.message_entry_abstract);
				holder.red = (TextView)convertView.findViewById(R.id.message_entry_red);
				
				convertView.setTag(holder);
				convertView.setOnClickListener(mClickListener);
				convertView.setOnCreateContextMenuListener(mContextMenuListener);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			MessageEntryBean bean = (MessageEntryBean)getItem(position);
			bean.setPos(position);
			if(bean != null){
				Utils.showFace(bean.getFace(), holder.image);
				UserVip.showVip(null, holder.userVipImage);
				holder.title.setText(Common.isMessageFromOther(bean) ?  bean.getMsgnickname() :
						bean.getTonickname());
				holder.time.setText(Common.dateCompareNow(bean.getDateline()));
				
				List<String> message = bean.getMessage();
				if(message != null && message.size() > 0){
					holder.messageAbstract.setContent(message.get(message.size() - 1));
					holder.messageAbstract.setVisibility(View.VISIBLE);
				}else{
					holder.messageAbstract.setVisibility(View.GONE);
				}
				String stick = bean.getLocal_stick();
				if(!TextUtils.isEmpty(stick)){
					holder.stick.setVisibility(View.VISIBLE);
				}else{
					holder.stick.setVisibility(View.GONE);
				}

				int value = bean.getIs_new();
				String valueStr = String.valueOf(bean.getIs_new());
				if(value == 0){
					holder.red.setVisibility(View.INVISIBLE);
				}else{
					if(valueStr.length() > 2){
						valueStr = PushManager.STR_MORE;
					}
					holder.red.setVisibility(View.VISIBLE);
					holder.red.setText(valueStr);
				}
				
				holder.title.setTag(bean);
			}
			return convertView;
			
		}
	}
