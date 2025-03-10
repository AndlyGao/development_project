package com.chengning.fenghuovideo.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.chengning.common.base.BaseDialogFragment;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.fenghuovideo.widget.RemovePhoneBindDialog.RemovePhoneBindOkLitener;

public class DeleteArticleCommentDialog extends BaseDialogFragment {
	private RemovePhoneBindOkLitener mListener;

	public void setData(RemovePhoneBindOkLitener litener) {
		this.mListener = litener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("确认删除此内容？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.confirm();
				dismissAllowingStateLoss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.cancle();
				dismissAllowingStateLoss();
			}
		});
		
		return builder.create();
	}

	@Override
	public View configContentView() {
		return null;
	}

	@Override
	public void initView() {

	}

	@Override
	public void initData() {

	}

	@Override
	public void initListener() {

	}
}
