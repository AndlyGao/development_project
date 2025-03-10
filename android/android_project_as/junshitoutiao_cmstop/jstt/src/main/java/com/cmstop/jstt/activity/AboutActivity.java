package com.cmstop.jstt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.chengning.common.base.BaseActivity;
import com.chengning.common.util.StatusBarUtil;
import com.cmstop.jstt.SettingManager;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.views.TitleBar;
import com.cmstop.jstt.R;

public class AboutActivity extends BaseActivity {

	private TitleBar mTitleBar;
	private View mLogo;
	private TextView mVersion;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_about);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.normalstate_bg), true);
        }
		super.onCreate(savedInstanceState);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void initViews() {
		mTitleBar = new TitleBar(getActivity(), true);
		mTitleBar.showDefaultBack();
		mTitleBar.setTitle("关于我们");
		
		mLogo = findViewById(R.id.about_logo);
		mVersion = (TextView) findViewById(R.id.textView0);
	}

	@Override
	public void initDatas() {
		mVersion.setText(new StringBuilder("军事头条 v").append(Common.getVersionName(getActivity())).toString());
	}

	@Override
	public void installListeners() {
		mLogo.setOnClickListener(new FastOnClickListener());
	}

	@Override
	public void processHandlerMessage(Message msg) {

	}
	
	private class FastOnClickListener implements OnClickListener{
		
		private long startTime = 0;
		private int count = 0;

		@Override
		public void onClick(View v) {
			if(System.currentTimeMillis() - startTime < 800){
				if(count >= 4){
					DebugActivity.launch(getActivity());
				}else{
					count++;
				}
			}else{
				count = 0;
			}
			startTime = System.currentTimeMillis();
		}
		
	}

}
