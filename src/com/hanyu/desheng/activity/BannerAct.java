package com.hanyu.desheng.activity;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BannerAct extends BaseActivity {
	@ViewInject(R.id.resact_wv)
	private WebView resact_wv;
	private String dsurl;
	private static final String APP_CACAHE_DIRNAME = "/webcache";

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.resact;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		context = BannerAct.this;
		setBack();
		setTopTitle("详情");
		intent =getIntent();
		dsurl = intent.getExtras().getString("link_url");
		
		WebSettings s = resact_wv.getSettings();
		// 开启 DOM storage API 功能
		resact_wv.getSettings().setDomStorageEnabled(true);
		// 开启 database storage API 功能
		resact_wv.getSettings().setDatabaseEnabled(true);
		String cacheDirPath = getFilesDir().getAbsolutePath()
				+ APP_CACAHE_DIRNAME;
		resact_wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// String cacheDirPath =
		// getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
		// 设置数据库缓存路径
		resact_wv.getSettings().setDatabasePath(cacheDirPath);
		// 设置 Application Caches 缓存目录
		resact_wv.getSettings().setAppCachePath(cacheDirPath);
		// 开启 Application Caches 功能
		resact_wv.getSettings().setAppCacheEnabled(true);
		

		s.setBuiltInZoomControls(true);
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		s.setUseWideViewPort(true);
		s.setLoadWithOverviewMode(true);
		s.setSavePassword(true);
		s.setSaveFormData(true);
		s.setJavaScriptEnabled(true);
		// enable navigator.geolocation
		s.setGeolocationEnabled(true);
		s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
		// enable Web Storage: localStorage, sessionStorage
		s.setDomStorageEnabled(true);
		resact_wv.requestFocus();
		resact_wv.setScrollBarStyle(0);
		s.setBuiltInZoomControls(false);
		s.setSupportZoom(false);
		s.setDisplayZoomControls(false);
		resact_wv.removeJavascriptInterface("searchBoxJavaBredge_");
		resact_wv.getSettings().setJavaScriptEnabled(true);
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
