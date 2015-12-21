package com.hanyu.desheng.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanyu.desheng.ExitApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.utils.MyToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangePassWordActivity extends BaseActivity {
	@ViewInject(R.id.change_pwd_et1)
	private EditText change_pwd_et1;
	@ViewInject(R.id.change_pwd_et2)
	private EditText change_pwd_et2;
	@ViewInject(R.id.change_pwd_et3)
	private EditText change_pwd_et3;
	@ViewInject(R.id.change_pwd_et3s)
	private EditText change_pwd_et3s;
	@ViewInject(R.id.change_pwd_btn)
	private Button commit;
	private static final int USERINFO = 0;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change_pwd_btn:
			String username = change_pwd_et1.getText().toString();
			String pwd = change_pwd_et2.getText().toString();
			String newpwd = change_pwd_et3.getText().toString();
			String newpwds = change_pwd_et3s.getText().toString();
			if (TextUtils.isEmpty(username)) {
				MyToastUtils.showShortToast(context, "请输入您的账号");
			} else if (TextUtils.isEmpty(pwd)) {
				MyToastUtils.showShortToast(context, "请输入您的原密码");
			} else if (TextUtils.isEmpty(newpwd)) {
				MyToastUtils.showShortToast(context, "请输入新密码");
			} else if (!newpwd.equals(newpwds)) {
				System.out.println(newpwd + "====" + newpwds);
				MyToastUtils.showShortToast(context, "两次密码不一致");
			} else {
				Changepwd(username, pwd, newpwd);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.change_pwd;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("修改密码");
		context = ChangePassWordActivity.this;
	}

	@Override
	public void setListener() {
		commit.setOnClickListener(this);
	}

	/**
	 * 修改密码
	 * 
	 * @param username
	 *            账号
	 * @param pwd
	 *            原密码
	 * @param newpwd
	 *            新密码
	 */
	private void Changepwd(final String username, final String pwd, final String newpwd) {
		new HttpTask<Void, Void, String>(context) {

			private String md5pwd;
			private String md5newpwd;

			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().changePWD(username, pwd, newpwd);
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
							SharedPreferences settings = getSharedPreferences("config", 0);

							SharedPreferences.Editor editor = settings.edit();
							// 清除保存的账号
							editor.remove("hx_name");

							editor.commit();

							intent = new Intent(context, LoginActivity.class);
							startActivityForResult(intent, USERINFO);
							ExitApplication.getInstance().exit(context);
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
