package com.hanyu.desheng.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.fragment.MainFragment;
import com.hanyu.desheng.fragment.ShopFragment;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

public class FeedBackActivity extends BaseActivity {
	@ViewInject(R.id.feedback_et1)
	private EditText feedback_et1;// 标题
	@ViewInject(R.id.feedback_et2)
	private EditText feedback_et2;// 内容
	@ViewInject(R.id.feedback_btn)
	private Button feedback_btn;// 提交
	@ViewInject(R.id.feedback_ll)
	private LinearLayout feedback_ll;// 提交

	private String title;
	private String content;
	private String mobile;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feedback_btn:
			title = feedback_et1.getText().toString();
			content = feedback_et2.getText().toString();
			if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
				MyToastUtils.showShortToast(context, "请填写标题或内容");
			} else {
				sendfeedback(mobile, title, content);
			}
			break;
		case R.id.feedback_ll:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.feedback;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		BaseActivity.isback_shop=true;
		setBack();
		setTopTitle("德升4567-意见反馈");
		context = FeedBackActivity.this;
		mobile = SharedPreferencesUtil.getStringData(context, "miphone", "");
	}

	@Override
	public void setListener() {
		feedback_btn.setOnClickListener(this);
		feedback_ll.setOnClickListener(this);
	}

	private void sendfeedback(final String mobile, final String title,
			final String content) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().toFeedBack(
						mobile, title, content);
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
							MyToastUtils.showShortToast(context,
									"感谢您的宝贵意见，我们会继续努力");
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
	/**
	 * 返回键
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(MainFragment.CHANGETAB);
		intent.putExtra("tab", 2);
		sendBroadcast(intent);
		finish();
	}


}
