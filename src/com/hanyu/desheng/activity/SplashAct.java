package com.hanyu.desheng.activity;

import com.easemob.chat.EMChatManager;
import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.welcome.activity.GuideActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import cn.jpush.android.api.JPushInterface;

public class SplashAct extends BaseActivity {

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.splash;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		if (SharedPreferencesUtil.getMes(this)) {
			EMChatManager.getInstance().getChatOptions().setNotifyBySoundAndVibrate(true);
		} else {
			EMChatManager.getInstance().getChatOptions().setNotifyBySoundAndVibrate(false);
		}
	}

	@Override
	public void setListener() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		JPushInterface.onPause(this);
		super.onPause();
	}

	boolean isFirstIn = false;

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 延迟2秒
	private static final long SPLASH_DELAY_MILLIS = 2000;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	/**
	 * Handler:跳转到不同界面
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				finish();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		init();
	}

	private void init() {
		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);

		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn = preferences.getBoolean("isFirstIn", true);

		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (!isFirstIn) {
			// 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}

	}

	private void goHome() {
		Intent intent = new Intent(SplashAct.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void goGuide() {
		Intent intent = new Intent(SplashAct.this, GuideActivity.class);
		SplashAct.this.startActivity(intent);
		finish();
	}
}
