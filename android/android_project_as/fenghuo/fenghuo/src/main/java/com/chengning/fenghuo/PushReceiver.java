package com.chengning.fenghuo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.chengning.fenghuo.activity.ArticleActivity;
import com.chengning.fenghuo.activity.MainActivity;
import com.chengning.fenghuo.activity.PhotoPageActivity;
import com.chengning.fenghuo.activity.VideoDetailActivity;
import com.chengning.fenghuo.data.bean.ChannelItemBean;
import com.chengning.fenghuo.data.bean.PushLCBean;
import com.chengning.fenghuo.util.Common;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
推送

消息推送
	@我的	消息-@我的
	赞我的	消息-赞
	评论我的	消息-评论
	私信我的（留言给我）	消息
	新增粉丝	我的

内容推送
	通知	具体内容详情页

免打扰（特定时间段不接受任何推送）
**/
public class PushReceiver extends BroadcastReceiver {
	private static final String TAG = PushReceiver.class.getSimpleName();

	public static final int TYPE_ARTICLE = 1;
	public static final int TYPE_FANS = 2;
	public static final int TYPE_AT = 3;
	public static final int TYPE_COMMENT = 4;
	public static final int TYPE_DIG = 5;
	public static final int TYPE_MSG = 6;
	public static final String IS_SHOW_BADGE = "Increment";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle bundle = intent.getExtras();
		if ("com.chengning.fenghuo.PUSH".equals(action) && bundle != null
				&& bundle.containsKey("com.avos.avoscloud.Data")) {
			if (!Common.isTrue(SettingManager.getInst().getSettingBean().getConfig_push_enable()) 
					|| Common.isMIUI()) {
				Log.i(TAG, "推送已关闭");
				return;
			}
			Log.i(TAG, "onReceive");
			try {
				Gson gson = new Gson();
	        	PushLCBean bean = gson.fromJson(bundle.getString("com.avos.avoscloud.Data"), new TypeToken<PushLCBean>(){}.getType());
				
				String alert = bean.getAlert();
				String title = bean.getTitle();
				int type = 0;
				if(bean.getExtension() != null){
					type = bean.getExtension().getType(); 
				}
				
				int nId = 100 + type;
				// 文章页
				final int tid = bean.getExtension().getTid();
				int tempTid = tid;
				if (tempTid == 0) {
					tempTid = Common.getRandomNum();
				}
				nId += tempTid;
				Log.i(PushReceiver.class.getSimpleName(), "alert: " + alert + ", title: " + title + ", type: " + type);
				
				Intent resultIntent = null;
				if(type == TYPE_ARTICLE){
					
					//是否显示桌面红点
					if (TextUtils.equals(bean.getBadge(), IS_SHOW_BADGE)) {
						Common.showBadge(context, 1);
					}
					
					if(!Common.isTrue(SettingManager.getInst().getSettingBean().getIs_push())){
						return;
					}
					
					ChannelItemBean dbean = new ChannelItemBean();
					
					int arcType = bean.getExtension().getArc_type();
	     			switch (arcType) {
	     			case 2: // 图文
						
						dbean.setTid(String.valueOf(tid));
						resultIntent = new Intent(context, PhotoPageActivity.class);
						resultIntent.putExtra("bean", dbean);
						break;
	     			case 3: //视频
	     				dbean.setTid(String.valueOf(tid));
	     				resultIntent = new Intent(context, VideoDetailActivity.class);
	     				resultIntent.putExtra("bean", dbean);
	     				break;
//	     			case 4: //自定义广告
//	     				String adUrl = bean.getTz_url();
//	     				resultIntent = new Intent(context, AdDetailActivity.class);
//	     				resultIntent.putExtra("type", AdDetailActivity.TYPE_ARTICLE);
//	     				resultIntent.putExtra("adurl", adUrl);
//	     				resultIntent.putExtra("imgurl", adUrl);
//	     				break;
	
	     			default:
	     				dbean.setTid(String.valueOf(tid));
	     				resultIntent = new Intent(context, ArticleActivity.class);
	     				resultIntent.putExtra("bean", dbean);
	     				break;
	     			}
	     			
	     			resultIntent.putExtra("push", true);
	     			resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				}else{
					Object[] rets = PushMsgManager.getInstance().filterLCPush(context, type, bean);
					if(((Boolean)rets[0]) == true){
						return;
					}else{
						resultIntent = (Intent) rets[1];
						if(resultIntent == null){
							// 首页
							resultIntent = new Intent(context, MainActivity.class);
							resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						}
					}
				}
				
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, nId, resultIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(title)
						.setContentText(alert)
						.setTicker(title);
				mBuilder.setContentIntent(pendingIntent);
				mBuilder.setAutoCancel(true);
				mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
				
				int mNotificationId = nId;
				NotificationManager mNotifyMgr = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotifyMgr.notify(mNotificationId, mBuilder.build());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
