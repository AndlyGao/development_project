package com.chengning.fenghuo.adapter;

import java.util.List;

import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import com.chengning.common.base.BasePageListAdapter;
import com.chengning.common.base.BaseViewHolder;
import com.chengning.fenghuo.R;

public class PointHintNoCompleteTaskAdapter extends BasePageListAdapter {

	public PointHintNoCompleteTaskAdapter(Activity activity, List list) {
		super(activity, list);
	}

	@Override
	public int buildLayoutId() {
		return R.layout.item_poin_hint_no_complete_task;
	}

	@Override
	public void handleLayout(View convertView, int position, Object obj) {
		TextView content = BaseViewHolder.get(convertView, R.id.item_point_hint_no_cmp_task_tv);
		SpannableStringBuilder builder= new SpannableStringBuilder();
		builder.append(String.valueOf(position + 1)).append(".").append((SpannableStringBuilder)obj);
		content.setText(builder);
	}

}
