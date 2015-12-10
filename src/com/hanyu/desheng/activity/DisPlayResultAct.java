package com.hanyu.desheng.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class DisPlayResultAct extends BaseActivity {
	@ViewInject(R.id.displayresult_tv)
	private TextView displayresult_tv;
	private String result;

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.displayresult;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("扫描结果");
		intent =getIntent();
		Bundle extras = intent.getExtras();
		result = extras.getString("result");
		displayresult_tv.setText(result);
	}

	@Override
	public void setListener() {

	}

}
