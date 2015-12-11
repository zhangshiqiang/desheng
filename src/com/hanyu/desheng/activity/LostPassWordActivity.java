package com.hanyu.desheng.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class LostPassWordActivity extends BaseActivity {
	protected static final String tag = "LostPassWordActivity";
	protected static final int FLAG = 10;
	@ViewInject(R.id.lostpwd_btn)
	private Button lostpwd_btn;// 获取验证码
	@ViewInject(R.id.lostpwd_btn2)
	private Button lostpwd_btn2;// 下一步
	@ViewInject(R.id.lostpwd_phone)
	private EditText lostpwd_phone;// 手机号
	@ViewInject(R.id.lostpwd_code)
	private EditText lostpwd_code;// 手机号

	private String phone;
	private String authcode;// 验证码
	
	private int i = 60;
	private Timer timer;
	private MyTimerTask myTask;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lostpwd_btn:
			phone = lostpwd_phone.getText().toString();
			if (TextUtils.isEmpty(phone)) {
				MyToastUtils.showShortToast(context, "请输入手机号");
			} else if (phone.length() < 11) {
				MyToastUtils.showShortToast(context, "请输入正确的手机号");
			} else {
				getcode(phone);
			}
			lostpwd_btn.setEnabled(false);
			i = 60;
			timer = new Timer();
			myTask = new MyTimerTask();
			timer.schedule(myTask, 0, 1000);
			break;
		case R.id.lostpwd_btn2:
			phone = lostpwd_phone.getText().toString();
			authcode = lostpwd_code.getText().toString();

			if (TextUtils.isEmpty(phone)) {
				MyToastUtils.showShortToast(context, "请输入手机号");
			} else if (phone.length() < 11) {
				MyToastUtils.showShortToast(context, "请输入正确的手机号");
			} else if (TextUtils.isEmpty(authcode)) {
				MyToastUtils.showShortToast(context, "请输入验证码");
			} else {
				checkcode(phone, authcode);
			}

			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.lostpwd;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("找回密码");
		context = LostPassWordActivity.this;
		ExampleApplication.getInstance().addActivity(LostPassWordActivity.this);
	}

	@Override
	public void setListener() {
		lostpwd_btn.setOnClickListener(this);
		lostpwd_btn2.setOnClickListener(this);
	}

	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			handler.sendEmptyMessage(i--);
		}

	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				lostpwd_btn.setEnabled(true);
				lostpwd_btn.setText("重新发送");
				timer.cancel();
				myTask.cancel();
			} else {
				lostpwd_btn.setText(msg.what + "秒");
			}
		}

	};
	/**
	 * 获取验证码
	 * 
	 * @param phone
	 */
	private void getcode(final String phone) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String content = "验证码：";
				String result = EngineManager.getUserEngine().postVerCode(
						phone, FLAG, content);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					if (result != null) {
						JSONObject json = new JSONObject(result);
						int code = json.getInt("code");// 返回状态
						if (code == 0) {
							authcode = json.getString("msg");
							System.out.println("--------------"+authcode);
							MyToastUtils.showShortToast(context, "获取验证码成功");
							LogUtil.i(tag, "获取成功" + authcode);
						} else {
							MyToastUtils.showShortToast(context, "获取服务器失败");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

	/**
	 * 检测验证码
	 * 
	 * @param phone
	 * @param authcode
	 */
	private void checkcode(final String phone, final String authcode) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().checkVerCode(
						phone, FLAG, authcode);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					if (result != null) {
						JSONObject json = new JSONObject(result);
						int code = json.getInt("code");// 返回状态
						if (code == 0) {
							intent = new Intent(LostPassWordActivity.this,
									LostPassWordSecondActivity.class);
							intent.putExtra("phone", phone);
							startActivity(intent);
							LogUtil.i(tag, "验证成功");
						} else {
							MyToastUtils.showShortToast(context, "获取服务器失败");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

}
