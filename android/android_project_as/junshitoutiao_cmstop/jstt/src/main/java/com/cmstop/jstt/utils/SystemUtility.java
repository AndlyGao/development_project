package com.cmstop.jstt.utils;

import java.io.File;

import org.apache.http.HttpHost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.cmstop.jstt.App;

public class SystemUtility {

	private static int screenWidth;

	private static int screenHeight;

	private static float density;

	public enum NetWorkType {
		none, mobile, wifi
	}

	private static void setScreenInfo() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) App.getInst().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		density = dm.density;
	}

	public static int getScreenWidth() {
		if (screenWidth == 0)
			setScreenInfo();
		return screenWidth;
	}

	public static int getScreenHeight() {
		if (screenHeight == 0)
			setScreenInfo();
		return screenHeight;
	}
	
	public static int getTitleBarHeight(Activity activity) {
		int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();  
		//statusBarHeight是上面所求的状态栏的高度  
		int titleBarHeight = contentTop - getStatusBarHeight(activity);
		
		return titleBarHeight;
	}

	public static float getDensity() {
		if (density == 0.0f)
			setScreenInfo();
		return density;
	}

	public static boolean hasSDCard() {
		boolean mHasSDcard = false;
		if (Environment.MEDIA_MOUNTED.endsWith(Environment.getExternalStorageState())) {
			mHasSDcard = true;
		} else {
			mHasSDcard = false;
		}

		return mHasSDcard;
	}

	public static String getSdcardPath() {

		if (hasSDCard())
			return Environment.getExternalStorageDirectory().getAbsolutePath();

		return "/sdcard/";
	}

	private static boolean sdcardCanWrite() {
		return android.os.Environment.getExternalStorageDirectory().canWrite();
	}

	public static boolean hasSdcardAndCanWrite() {
		return hasSDCard() && sdcardCanWrite();
	}

	/**
	 * 获取SDCARD的可用大小,单位字节
	 * 
	 * @return
	 */
	public long getSdcardtAvailableStore() {

		if (hasSdcardAndCanWrite()) {
			String path = getSdcardPath();
			if (path != null) {
				StatFs statFs = new StatFs(path);

				long blocSize = statFs.getBlockSize();

				long availaBlock = statFs.getAvailableBlocks();

				return availaBlock * blocSize;
			}
		}

		return 0;
	}

	public static NetWorkType getNetworkType() {

		ConnectivityManager connMgr = (ConnectivityManager) App.getInst().getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null) {
			switch (networkInfo.getType()) {
			case ConnectivityManager.TYPE_MOBILE:
				return NetWorkType.mobile;
			case ConnectivityManager.TYPE_WIFI:
				return NetWorkType.wifi;
			}
		}

		return NetWorkType.none;
	}

	public static HttpHost getProxy() {
		String host = Proxy.getDefaultHost();
		if (!TextUtils.isEmpty(host)) {
			int port = Proxy.getDefaultPort();
			HttpHost proxy = new HttpHost(host, port);
			return proxy;
		}
		return null;
	}

	/**
	 * mac地址
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getMacAddress() {
		WifiManager wifiManager = (WifiManager) App.getInst().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo != null && wifiInfo.getMacAddress() != null)
			return wifiInfo.getMacAddress().replace(":", "");
		else
			return "0022f420d03f";// 00117f29d23a
	}
	
	public static String getUDPIP() {
		Context context = App.getInst();
		
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi.getDhcpInfo();
        int IpAddress =dhcpInfo.ipAddress;
        int subMask = dhcpInfo.netmask;
        return transformIp((~subMask) | IpAddress);
    }
	
	private static String transformIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }
	
	public static String getIP() {
		Context context = App.getInst();
		
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return transformIp(wifi.getConnectionInfo().getIpAddress());
	}

	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (Exception e) {
		}
		return "";
	}

	public static int getVersionCode(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (Exception e) {
		}
		return 0;
	}
	
	public static String getPackage(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.packageName;
		} catch (Exception e) {
		}
		return "";
	}
	
	public static void scanPhoto(File file) {
		 Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		 Uri uri = Uri.fromFile(file);
		 intent.setData(uri);
		 App.getInst().sendBroadcast(intent);
	}
	
	public static void hideSoftInput(View paramEditText) {
		((InputMethodManager) App.getInst().getSystemService("input_method"))
				.hideSoftInputFromWindow(paramEditText.getWindowToken(), 0);
	}

	public static void showKeyBoard(final View paramEditText) {
		paramEditText.requestFocus();
		paramEditText.post(new Runnable() {
			@Override
			public void run() {
				((InputMethodManager) App.getInst().getSystemService("input_method")).showSoftInput(paramEditText, 0);
			}
		});
	}

	public static int getScreenHeight(Activity paramActivity) {
		Display display = paramActivity.getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return metrics.heightPixels;
	}

	public static int getStatusBarHeight(Activity paramActivity) {
		Rect localRect = new Rect();
		paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		return localRect.top;

	}

	// below status bar,include actionbar, above softkeyboard
	public static int getAppHeight(Activity paramActivity) {
		Rect localRect = new Rect();
		paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		return localRect.height();
	}

//	// below actionbar, above softkeyboard
//	public static int getAppContentHeight(Activity paramActivity) {
//		return SystemUtility.getScreenHeight(paramActivity) - SystemUtility.getStatusBarHeight(paramActivity)
//				- SystemUtility.getActionBarHeight(paramActivity) - SystemUtility.getKeyboardHeight(paramActivity);
//	}

//	public static int getKeyboardHeight(Activity paramActivity) {
//
//		int height = SystemUtility.getScreenHeight(paramActivity) - SystemUtility.getStatusBarHeight(paramActivity)
//				- SystemUtility.getAppHeight(paramActivity);
//		if (height == 0) {
//			height = ActivityHelper.getInstance().getIntShareData("KeyboardHeight", 400);
//		}
//		else {
//			ActivityHelper.getInstance().putIntShareData("KeyboardHeight", height);
//		}
//		
//
//		return height;
//	}

	public static boolean isKeyBoardShow(Activity paramActivity) {
		int height = SystemUtility.getScreenHeight(paramActivity) - SystemUtility.getStatusBarHeight(paramActivity)
				- SystemUtility.getAppHeight(paramActivity);
		return height != 0;
	}

}
