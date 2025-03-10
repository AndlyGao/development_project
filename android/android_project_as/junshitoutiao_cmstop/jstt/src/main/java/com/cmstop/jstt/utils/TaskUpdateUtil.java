package com.cmstop.jstt.utils;


import org.json.JSONException;

import android.support.v4.app.FragmentActivity;

import com.chengning.common.app.ActivityInfo.ActivityState;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.IBaseActivity;
import com.cmstop.jstt.App;
import com.cmstop.jstt.Const.PointActionType;
import com.cmstop.jstt.beans.data.DirectoratePointBean;
import com.cmstop.jstt.beans.data.LoginBean;
import com.cmstop.jstt.views.UpgradeDialog;
import com.cmstop.jstt.views.UpgradeDialog.DialogOnClickListener;
import com.google.gson.Gson;

public class TaskUpdateUtil {
	

	/**
	 * 显示积分提示
	 * 
	 * @param json
	 * @param mContext
	 * @throws JSONException
	 */
	public static void showHints(FragmentActivity mContext, String data,
			PointActionType action) throws JSONException {
		Gson gson = new Gson();
		DirectoratePointBean task = gson.fromJson(data,
				DirectoratePointBean.class);
		if(task != null){
			showUpdateDialog(mContext, task, action);
		}
	}
	//每日登陆使用
	public static void showHints(FragmentActivity mContext,
			DirectoratePointBean mData, PointActionType action) throws JSONException {
		if (!Common.isListEmpty(mData.getCredits_rule())) {
			showUpdateDialog(mContext, mData, action);

		}
	}

	public static void showUpdateDialog(FragmentActivity context,
			DirectoratePointBean bean, PointActionType action) {
		if(context instanceof IBaseActivity && ((IBaseActivity)context).getActivityInfo().getActivityState() == ActivityState.SaveInstanceStated){
			return;
		}
		if (!Common.isListEmpty(bean.getCredits_rule())) {
			UpgradeDialog dialog = new UpgradeDialog();
			dialog.setData(action, bean.getCredits_rule(),
					new DialogOnClickListener() {

						@Override
						public void onConfirmClick() {

						}
					});
			dialog.showAllowingStateLoss((BaseFragmentActivity) context, context.getSupportFragmentManager(),
					UpgradeDialog.class.getSimpleName());
		}
	}
	public static DirectoratePointBean convertLoginToPoint(LoginBean bean){
		DirectoratePointBean mBean = new DirectoratePointBean();
		if(bean.getCredits_rule() != null){
			mBean.setCredits_rule(bean.getCredits_rule());			
		}
		return mBean;
	}
	//文章页登录后显示每日登录
	public static void LoginShowUpdate(BaseFragmentActivity context){
		if(App.getInst().isLogin()){
			if (!Common.isListEmpty(App.getInst().getLoginBean().getCredits_rule())) {
				DirectoratePointBean pointBean = TaskUpdateUtil.convertLoginToPoint(App.getInst().getLoginBean());
				try {
					if (pointBean != null) {
						TaskUpdateUtil.showHints(context,pointBean, PointActionType.LOGIN);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 手动制空credits_rules
				LoginBean mLoginBean = new LoginBean();
				mLoginBean = App.getInst().getLoginBean();
				mLoginBean.setCredits_rule(null);
				App.getInst().saveLoginBean(mLoginBean);
			}
		}
	}
	
}
