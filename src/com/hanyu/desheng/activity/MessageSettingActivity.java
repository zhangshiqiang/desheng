package com.hanyu.desheng.activity;

import android.os.Bundle;
import android.view.View;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;

public class MessageSettingActivity extends BaseActivity {

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.message_setting;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("消息设置");
	}

	@Override
	public void setListener() {

	}

}
