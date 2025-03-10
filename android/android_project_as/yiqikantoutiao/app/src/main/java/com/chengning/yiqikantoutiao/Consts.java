package com.chengning.yiqikantoutiao;

import android.view.View;
import android.widget.ImageView;

public class Consts {

	// TODO(debug)
	public static final boolean DEBUG = true;
	
	public static final String UPDATE_APP_KEY = "yiqikantoutiao";
	public static final String USER_AGENT_PREFIX = "yiqikantoutiao";

	// leancloud
	public static final String LEANCLOUD_APP_ID = "EaTisSfykLmK2lqfCJr9yiSL-gzGzoHsz";
	public static final String LEANCLOUD_APP_KEY = "AOGDEYGl60XTTX9BDmo7DERm";
	// @杨洋，指定接收对应版本的推送。
	public static final int LEANCLOUD_DATA_VERSION = 1;

	public static final String WX_APP_ID = "wx9e9ac46ab9b4ec8b";
	public static final String WX_APP_SECRET = "44ce9dddb142851cab361e6c39d6bfed";
	public static final String QQ_APP_ID = "1106429226";
	public static final String QQ_APP_KEY = "1IxI45QiQ43aZ67v";
	public static final String WEIBO_APP_KEY = "4043778609";
	public static final String WEIBO_APP_SECRET = "c79dc7aeb618af9da64fbb3756bcc714";

	// 阿里
	public static final String ALI_APP_KEY = "24627914";
	public static final String ALI_APP_SECRET = "e751fbb3776285f858a9ed1fae7bb9b5";

	//xiaomi
	public static final String XIAOMI_APP_ID = "2882303761517618112";
	public static final String XIAOMI_APP_KEY = "5751761845112";

	public static final String WEIBO_REDIRECT_URL = "http://feng.pp78.com/weibo_cb.php";

	public static final int BIND_RESULT_CODE = 11;

	public static final String ACTION_WX_BIND_SUCESS = Consts.class.getPackage().getName() + ".action_bind_success";

	public enum ArticleType {
		ARTICLE,
		DYNAMIC
		;
	}

	/**
	 * show_type 1 1小图 2 三图 3大图视频 4一小图视频类型(嵌套wap) 5 大图广告 6 大图文章 7 一大图二小图文章 
	 */
	public static final int SHOW_TYPE_ONE_SMALL = 1;
	public static final int SHOW_TYPE_THREE_SMALL  = 2;
	public static final int SHOW_TYPE_ONE_BIG_VIDEO = 3;
	public static final int SHOW_TYPE_ONE_SMALL_VIDEO = 4;
	public static final int SHOW_TYPE_ONE_BIG = 6;
	public static final int SHOW_TYPE_ONE_BIG_TWO_SMALL = 7;
//	public static final int NEWS_TYPE_SIX_SMALL_PIC  = 32;
//	public static final int NEWS_TYPE_TWO_BIG_PIC  = 40;
	public static final int SHOW_TYPE_ONE_SMALL_AD = SHOW_TYPE_ONE_SMALL;
	public static final int SHOW_TYPE_ONE_BIG_AD = 5;
	public static final int SHOW_TYPE_THREE_SMALL_AD = SHOW_TYPE_THREE_SMALL;
	
	public static final String BASE_SCHEME = "com.chengning.yiqikantoutiao.";
	public static final String REPLY_DETAIL_SCHEME = BASE_SCHEME + "replyDetail://";

	
	/**
	 * content_type 文章，视频，wap，广告，图文  
	 */
	public static final String CONTENT_TYPE_NORMAL = "0";
	public static final String CONTENT_TYPE_VIDEO = "1";
	public static final String CONTENT_TYPE_WEB = "2";
	public static final String CONTENT_TYPE_AD = "3";
	public static final String CONTENT_TYPE_TUWEN = "4";

	public static final int EMOTION_SIZE_MIDDLE = App.getInst().getResources().getDimensionPixelSize(R.dimen.emotion_size_middle);
	
	/**
	 * 匹配网址的正则表达式
	 */
	public static final String SITE_COMPILE_STR = "\\[URL\\](.+?)\\[/URL\\]";
	/**
	 * 匹配用户名的正则表达式
	 */
	public static final String NICKNAME_COMPILE_STR = "@([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)";
	/**
	 * 匹配主题的正则表达式
	 */
	public static final String TOPIC_COMPILE_STR = "#([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)#";
	/**
	 * 匹配表情的正则表达式
	 */
	public static final String EMOTION_COMPILE_STR = "\\[(\\S+?)\\]";

	public enum ArticleCommentType {
		ARTICLE,
		REPLY
		;
	}

	public static final int TIME_EVERY_SCREEN = 6*1000;
	public static final float EXPECT_RATIO_IMG_HEIGHT = 0.4f;
	public static final double EXPECT_VARIANCE = 0.09d;
	public static final double EXPECT_AVERAGE = 0.5d;
	public static final double EXPECT_AVERAGE_ERROR_RATIO = 0.4;
	public static final double EXPECT_VARIANCE_ERROR_RATIO = 0.4;

	public static final String ENCODE_KEY = "chengning";

	public static final int TASK_SHARE_FRIENDS_QUAN = 1;
	public static final int TASK_SHARE_FRIENDS_QUN = 2;
	public static final int TASK_SHARE_FRIENDS = 3;
	public static final int TASK_SHARE_ARTICLE = 4;


}
