package com.hanyu.desheng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ProvisionActivity extends BaseActivity {
	@ViewInject(R.id.provision_wv)
	private WebView provision_wv;
	@ViewInject(R.id.provision_btn)
	private Button provision_btn;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.provision_btn:
			finish();
			sendBroadcast(new Intent(RegisterActivity.REGISTER));
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.provision;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("会员条款");
		provision_wv.getSettings().setDefaultTextEncodingName("UTF-8");
		provision_wv.loadUrl("http://e.aysfq.cn/hytk.html");
	}

	@Override
	public void setListener() {
		provision_btn.setOnClickListener(this);
	}

}
