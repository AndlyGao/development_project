package com.chengning.yiqikantoutiao;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVInstallation;
import com.chengning.yiqikantoutiao.activity.ArticleActivity;
import com.chengning.yiqikantoutiao.activity.MainActivity;
import com.chengning.yiqikantoutiao.activity.PhotoPageActivity;
import com.chengning.yiqikantoutiao.activity.VideoDetailActivity;
import com.chengning.yiqikantoutiao.data.bean.ChannelItemBean;
import com.chengning.yiqikantoutiao.data.bean.XiaomiPushBean;
import com.chengning.yiqikantoutiao.util.Common;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 XiaomiPushMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * 3、DemoMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、DemoMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、DemoMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、DemoMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、DemoMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 *
 * @author 
 */
public class XiaomiPushMessageReceiver extends PushMessageReceiver {

	private final String NOTIFY_HEAD = "[小米]";
	
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
    	//TODO 接受到的透传消息
    	if (!Common.isTrue(SettingManager.getInst().getSettingBean().getConfig_push_enable())
    			|| !Common.isMIUI()) {
			return;
		}
    	handleMessageClick(context,message);
    }
    
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
    	//TODO 点击通知消息
//    	handleMessageClick(context,message);
    }
    
    /**
     * 处理通知点击
     * @param context
     * @param message
     */
    private void handleMessageClick(Context context, MiPushMessage message) {
    	try {
	    	 Gson gson = new Gson();
	     	XiaomiPushBean bean = gson.fromJson(message.getContent(), new TypeToken<XiaomiPushBean>(){}.getType());
	     	if (bean != null) {
	     		
	     		String alert = bean.getAlert();
	    		String title = bean.getTitle();
	    		int type = 0;
	    		type = bean.getType();
	    		
	    		int nId = 100 + type;
	    		final String tid = bean.getTid();
				
				int tempTid = 0;
				if (!TextUtils.isEmpty(tid) && TextUtils.isDigitsOnly(tid)) {
					tempTid = Integer.valueOf(tid);
				}
				if (tempTid == 0) {
					tempTid = Common.getRandomNum();
				}
				nId += tempTid;
				
	     		Intent intent;
	 			if(type == PushReceiver.TYPE_ARTICLE){
	     			
	     			if(!Common.isTrue(SettingManager.getInst().getSettingBean().getIs_push())){
	     				return;
	     			}
	     			//是否显示桌面红点
					if (TextUtils.equals(bean.getBadge(), PushReceiver.IS_SHOW_BADGE)) {
						Common.showBadge(context, 1);
					}
					
					ChannelItemBean dbean = new ChannelItemBean();
	     			int arcType = bean.getArc_type();
	     			switch (arcType) {
	     			case 2: // 图文
						
						dbean.setTid(String.valueOf(tid));
						intent = new Intent(context, PhotoPageActivity.class);
						intent.putExtra("bean", dbean);
						break;
	     			case 3: //视频
	     				dbean.setTid(String.valueOf(tid));
	     				intent = new Intent(context, VideoDetailActivity.class);
	     				intent.putExtra("bean", dbean);
	     				break;
//	     			case 4: //自定义广告
//	     				String adUrl = bean.getTz_url();
//	     				intent = new Intent(context, AdDetailActivity.class);
//	     				intent.putExtra("type", AdDetailActivity.TYPE_ARTICLE);
//	     				intent.putExtra("adurl", adUrl);
//	     				intent.putExtra("imgurl", adUrl);
//	     				break;
	
	     			default:
	     				dbean.setTid(String.valueOf(tid));
	     				intent = new Intent(context, ArticleActivity.class);
	     				intent.putExtra("bean", dbean);
	     				break;
	     			}
	     			
	     			intent.putExtra("push", true);
	     			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
	     			
	     		}else{

					// 首页
					intent = new Intent(context, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

	     		}
	 			PendingIntent pendingIntent = PendingIntent.getActivity(
						context, nId, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
	
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context)
						.setSmallIcon(R.mipmap.ic_launcher)
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
	     	} 
     	}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {

    }
	
	@Override
	public void onReceiveRegisterResult(Context arg0, MiPushCommandMessage message) {
		String command = message.getCommand();
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
            	MiPushClient.setAlias(arg0, AVInstallation.getCurrentInstallation().getInstallationId(), null);
            }
        } 
		super.onReceiveRegisterResult(arg0, message);
	}
	
}
