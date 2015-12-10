package com.hanyu.desheng.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.utils.MyToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class LostPassWordSecondActivity extends BaseActivity {
	@ViewInject(R.id.lostpwdsecond_btn)
	private Button lostpwdsecond_btn;
	@ViewInject(R.id.lostpwdsecond_et1)
	// 设置新密码
	private EditText lostpwdsecond_et1;
	@ViewInject(R.id.lostpwdsecond_et2)
	// 确认新密码
	private EditText lostpwdsecond_et2;
	private String phone;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lostpwdsecond_btn:
			String newpwd1 = lostpwdsecond_et1.getText().toString();
			String newpwd2 = lostpwdsecond_et2.getText().toString();
			if (TextUtils.isEmpty(newpwd1)) {
				MyToastUtils.showShortToast(context, "请输入新密码");
			} else if (TextUtils.isEmpty(newpwd2)) {
				MyToastUtils.showShortToast(context, "请确认新密码");
			} else if (newpwd1.length() < 6 || newpwd2.length() < 6) {
				MyToastUtils.showShortToast(context, "密码长度不能少于6位");
			} else if (!newpwd1.equals(newpwd2)) {
				MyToastUtils.showShortToast(context, "两次输入的密码不一样");
			} else {
				setNewPwd(phone, newpwd1);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.lostpwdsecond;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("设置新密码");
		context = LostPassWordSecondActivity.this;
		ExampleApplication.getInstance().addActivity(LostPassWordSecondActivity.this);
		intent = getIntent();
		Bundle extras = intent.getExtras();
		phone = extras.getString("phone");
	}

	@Override
	public void setListener() {
		lostpwdsecond_btn.setOnClickListener(this);
	}

	/**
	 * 设置新密码
	 * 
	 * @param phone2
	 * @param newpwd1
	 */
	private void setNewPwd(final String phone2, final String newpwd1) {
		new HttpTask<Void, Void, String>(context) {


			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().changePWD(phone2,
						null, newpwd1);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					if (result != null) {
						JSONObject object = new JSONObject(result);
						int code = object.getInt("code");
						String msg = object.getString("msg");
						if (code == 0) {
							MyToastUtils.showShortToast(context, "修改密码成功");
							ExampleApplication.getInstance().exit();
							finish();
						} else {
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

}
