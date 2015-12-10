package com.hanyu.desheng.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.ActionMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.ui.ClearEditText;
import com.hanyu.desheng.utils.MyToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ChangeNicknameActivity extends BaseActivity {
	protected static final int NICKNAME = 0;
	@ViewInject(R.id.nickname_cet)
	private ClearEditText nickname_cet;
	private int flag;

	@Override
	public void onClick(View v) {
		
	}
	
	@Override
	public void hideSoftInput() {
		if (inputManager != null) {
			inputManager.hideSoftInputFromInputMethod(nickname_cet.getWindowToken(), 0);
			inputManager.hideSoftInputFromWindow(nickname_cet.getWindowToken(), 0);
		}
	}

	@Override
	public int setLayout() {
		return R.layout.nickname;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		context = ChangeNicknameActivity.this;
		setBack();
		setTopTitle("请填写信息");
		setRightButton("保存", new OnClickListener() {
			@Override
			public void onClick(View v) {
				String result = nickname_cet.getText().toString();
				intent = new Intent();
				if (flag == 4) {
					if (!result.contains("@")) {
						MyToastUtils.showShortToast(context, "请输入正确的邮箱地址");
					} else {
						if (inputManager != null) {
							inputManager.hideSoftInputFromInputMethod(nickname_cet.getWindowToken(), 0);
							inputManager.hideSoftInputFromWindow(nickname_cet.getWindowToken(), 0);
						}
						intent.putExtra("result", result);
						setResult(NICKNAME, intent);
						finish();
					}
				} else {
					if (result.contains("\"\"")) {
						result.replaceAll("\"\"", "“");
					}
					intent.putExtra("result", result);
					setResult(NICKNAME, intent);
					finish();
					if (inputManager != null) {
						inputManager.hideSoftInputFromInputMethod(nickname_cet.getWindowToken(), 0);
						inputManager.hideSoftInputFromWindow(nickname_cet.getWindowToken(), 0);
					}
				}
			}
		});
		intent = getIntent();
		Bundle extras = intent.getExtras();
		flag = extras.getInt("flag");
		switch (flag) {
		case 1:
			nickname_cet.setInputType(0x00000001);// text
			break;
		case 2:
			nickname_cet.setInputType(0x00000002);// number
			break;
		case 3:
			nickname_cet.setInputType(0x00000003);// phone
			break;
		case 4:
			nickname_cet.setInputType(0x00000021);// 邮箱地址
			break;

		default:
			break;
		}
		
		if (flag == 1) {
			InputFilter[] filters = {new InputFilter.LengthFilter(11)};
			nickname_cet.setFilters(filters);
		}

	}

	private InputMethodManager inputManager;
	@Override
	public void setListener() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {
				inputManager = (InputMethodManager) nickname_cet
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(nickname_cet, 0);
			}
		},

		998);
	}
	
	@Override
	public void onActionModeFinished(ActionMode mode) {
		super.onActionModeFinished(mode);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (inputManager != null) {
			inputManager.hideSoftInputFromInputMethod(nickname_cet.getWindowToken(), 0);
			inputManager.hideSoftInputFromWindow(nickname_cet.getWindowToken(), 0);
		}
	}

}
