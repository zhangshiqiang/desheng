package com.hanyu.desheng.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ResAct extends BaseActivity {
	@ViewInject(R.id.resact_wv)
	private WebView resact_wv;
	private String dsurl;

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.resact;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void init(Bundle savedInstanceState) {
		context = ResAct.this;
		setBack();
		setTopTitle("扫描结果");
		intent =getIntent();
		dsurl = intent.getExtras().getString("dsurl");
		WebSettings webSettings = resact_wv.getSettings();
		webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		resact_wv.removeJavascriptInterface("searchBoxJavaBredge_");
		resact_wv.requestFocusFromTouch();
		resact_wv.loadUrl(dsurl);
		resact_wv.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});
	}

	@Override
	public void setListener() {

	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && resact_wv.canGoBack()) {
			resact_wv.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


}
