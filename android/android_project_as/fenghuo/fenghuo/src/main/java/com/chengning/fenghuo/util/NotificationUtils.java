package com.chengning.fenghuo.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.chengning.fenghuo.Consts;
import com.chengning.fenghuo.NotificationBroadcastReceiver;
import com.chengning.fenghuo.PushMsgManager;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.data.bean.ChatConversationBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.View;

public class NotificationUtils {
	
	 /**
	   * tag list，用来标记是否应该展示 Notification
	   * 比如已经在聊天页面了，实际就不应该再弹出 notification
	   */
	  private static List<String> notificationTagList = new LinkedList<String>();

	  /**
	   * 添加 tag 到 tag list，在 MessageHandler 弹出 notification 前会判断是否与此 tag 相等
	   * 若相等，则不弹，反之，则弹出
	   * @param tag
	   */
	  public static void addTag(String tag) {
	    if (!notificationTagList.contains(tag)) {
	      notificationTagList.add(tag);
	    }
	  }

	  /**
	   * 在 tag list 中 remove 该 tag
	   * @param tag
	   */
	  public static void removeTag(String tag) {
	    notificationTagList.remove(tag);
	  }

	  /**
	   * 判断是否应该弹出 notification
	   * 判断标准是该 tag 是否包含在 tag list 中
	   * @param tag
	   * @return
	   */
	  public static boolean isShowNotification(String tag) {
	    return !notificationTagList.contains(tag);
	  }

	  /**
	   * 显示通知
	   * @param context
	   * @param title
	   * @param content
	   * @param sound
	   * @param intent
	 * @param icon 
	   */
	  public static void showNotification(final Context context, final AVIMTypedMessage message, final ChatConversationBean mConversation) {
		  	
		  	String notificationContent = "";
			String title = mConversation.getCon_name();
			String imgUri = "";
			
			Map<String, Object> attr = null;
			switch (message.getMessageType()) {
			case AVIMMessageType.TEXT_MESSAGE_TYPE:
				notificationContent = ((AVIMTextMessage)message).getText();
				attr = ((AVIMTextMessage)message).getAttrs();
				break;
			case AVIMMessageType.IMAGE_MESSAGE_TYPE:
				notificationContent = "[图片]";
				attr = ((AVIMImageMessage)message).getAttrs();
				break;
			
			default:
				break;
			}
			final String finalNotificationContent = notificationContent;
			imgUri =  null == attr ? "" : (String)attr.get("face");
//			Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgUri);
			final int tarWidth = context.getResources().getDimensionPixelSize(R.dimen.notification_large_icon_width);
			ImageLoader.getInstance().loadImage(imgUri, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					privateShowNotification(context, message, mConversation, finalNotificationContent, null);
				}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					int w = arg2.getWidth();
					if(w < tarWidth){
						float scale = (float)tarWidth / (float)w;
						Matrix matrix = new Matrix(); 
						matrix.postScale(scale, scale);
						Bitmap b2 = Bitmap.createBitmap(arg2, 0, 0, w, arg2.getHeight(), matrix, true);
						privateShowNotification(context, message, mConversation, finalNotificationContent, b2);
					}else{
						privateShowNotification(context, message, mConversation, finalNotificationContent, arg2);
					}
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
				}
			});
	  }
	  
	  private static void privateShowNotification(Context context, AVIMTypedMessage message, ChatConversationBean mConversation,
			  String notificationContent, Bitmap bitmap){

			String title = mConversation.getCon_name();
			String sound = null;
			
			Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
			intent.putExtra(Consts.CONVERSATION_ID, mConversation.getCon_id());
			intent.putExtra(Consts.MEMBER_ID, message.getFrom());
			int notificationId = Common.handleStrToInteger(message.getFrom());
			PendingIntent contentIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		  			.setLargeIcon(bitmap)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(title).setAutoCancel(true).setContentIntent(contentIntent)
					.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
					.setContentText(notificationContent);
			NotificationManager manager =
				  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = mBuilder.build();
			
			if (sound != null && sound.trim().length() > 0) {
				notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + sound);
			}
			manager.notify(mConversation.getCon_id(), notificationId, notification);
			
			// 推送收到的消息留言+1
//			PushMsgManager.getInstance().getPushUserDataBean().setNewpm(PushMsgManager.getInstance().getPushUserDataBean().getNewpm() + 1);
	  }
}
