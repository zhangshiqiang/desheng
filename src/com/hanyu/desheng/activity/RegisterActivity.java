package com.hanyu.desheng.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.RegisterBean;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class RegisterActivity extends BaseActivity {
	protected static final String tag = null;
	protected static final int FLAG = 2;
	@ViewInject(R.id.register_tv)
	private TextView register_tv;
	@ViewInject(R.id.register_btn)
	private Button register_btn;
	@ViewInject(R.id.register_btn1)
	private Button register_btn1;
	@ViewInject(R.id.register_cb)
	private CheckBox register_cb;
	@ViewInject(R.id.regidter_et)
	private EditText regidter_et;
	@ViewInject(R.id.register_et_pwd)
	private EditText register_et_pwd;
	@ViewInject(R.id.enter_et_pwd)
	private EditText enter_et_pwd;
	@ViewInject(R.id.register_code_et)
	private EditText register_code_et;
	private String phone;
	private String authcode;// 验证码
	private String pwd, pwds;

	private int i = 60;
	private Timer timer;
	private MyTimerTask myTask;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_tv:
			intent = new Intent(RegisterActivity.this, ProvisionActivity.class);
			startActivity(intent);
			break;
		case R.id.register_btn:
			phone = regidter_et.getText().toString();
			if (TextUtils.isEmpty(phone)) {
				MyToastUtils.showShortToast(context, "请输入手机号");
			} else if (phone.length() < 11) {
				MyToastUtils.showShortToast(context, "请输入正确的手机号");
			} else {
				getcode(phone);
			}
			register_btn.setEnabled(false);
			i = 60;
			timer = new Timer();
			myTask = new MyTimerTask();
			timer.schedule(myTask, 0, 1000);
			break;
		case R.id.register_btn1:
			phone = regidter_et.getText().toString();
			pwd = register_et_pwd.getText().toString();
			pwds = enter_et_pwd.getText().toString();
			authcode = register_code_et.getText().toString();
			if (TextUtils.isEmpty(phone)) {
				MyToastUtils.showShortToast(context, "请输入手机号");
			} else if (phone.length() < 11) {
				MyToastUtils.showShortToast(context, "请输入正确的手机号");
			} else if (TextUtils.isEmpty(authcode)) {
				MyToastUtils.showShortToast(context, "请输入验证码");
			} else if (TextUtils.isEmpty(pwd)) {
				MyToastUtils.showShortToast(context, "请输入密码");
			} else if (pwd.length() < 6) {
				MyToastUtils.showShortToast(context, "密码长度太短，为了安全，请你重新设置不低于6位数的密码");
			} else if (pwd != pwds) {
				MyToastUtils.showShortToast(context, "两次输入密码不一致");
			} else if (!register_cb.isChecked()) {
				MyToastUtils.showShortToast(context, "请选中条款");
			} else {
				checkcode(phone, authcode);
			}
			break;

		default:
			break;
		}
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
				register_btn.setEnabled(true);
				register_btn.setText("重新发送");
				timer.cancel();
				myTask.cancel();
			} else {
				register_btn.setText(msg.what + "秒");
			}
		}

	};

	/**
	 * 注册
	 * 
	 * @param phone
	 * @param pwd
	 */
	private void register(final String phone, final String pwd) {
		new HttpTask<Void, Void, RegisterBean>(context) {

			@Override
			protected RegisterBean doInBackground(Void... params) {
				RegisterBean result = EngineManager.getUserEngine().toRegister(phone, pwd);
				return result;
			}

			@Override
			protected void onPostExecute(RegisterBean result) {
				if (result != null) {
					if (result.code == 0) {
						MyToastUtils.showShortToast(context, "注册成功");
						finish();
					} else {
						MyToastUtils.showShortToast(context, result.msg);
					}
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
				String result = EngineManager.getUserEngine().checkVerCode(phone, FLAG, authcode);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					if (result != null) {
						JSONObject json = new JSONObject(result);
						int code = json.getInt("code");// 返回状态
						if (code == 0) {
							register(phone, pwd);
							LogUtil.i(tag, "验证成功");
						} else {
							String msg = json.optString("msg");
							MyToastUtils.showShortToast(context, msg);
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
	 * 获取验证码
	 * 
	 * @param phone
	 */
	private void getcode(final String phone) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String content = "验证码：";
				String result = EngineManager.getUserEngine().postVerCode(phone, FLAG, content);
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
							MyToastUtils.showShortToast(context, "获取验证码成功");
							LogUtil.i(tag, "获取成功" + authcode);
						} else {
							String msg = json.optString("msg");
							MyToastUtils.showShortToast(context, msg);
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

	@Override
	public int setLayout() {
		return R.layout.register;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("注册");
		context = RegisterActivity.this;
	}

	@Override
	public void setListener() {
		register_tv.setOnClickListener(this);
		register_btn.setOnClickListener(this);
		register_btn1.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		registerMessageReceiver();
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mMessageReceiver);
		mMessageReceiver = null;
		super.onDestroy();
	}

	private MyBroadCastReceiver mMessageReceiver;
	public static final String REGISTER = "com.hanyu.desheng.register";

	public void registerMessageReceiver() {
		if (mMessageReceiver == null) {
			mMessageReceiver = new MyBroadCastReceiver();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(REGISTER);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			register_cb.setChecked(true);
		}
	}
}
