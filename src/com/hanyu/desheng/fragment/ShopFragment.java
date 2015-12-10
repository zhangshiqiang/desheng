package com.hanyu.desheng.fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.GlobalParams;
import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.ChatActivity;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.bean.MobileData;
import com.hanyu.desheng.bean.PhoneBean;
import com.hanyu.desheng.bean.ShopInfo;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.ui.CircleImageView;
import com.hanyu.desheng.util.LogUtils;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.XmlComonUtil;
import com.hanyu.desheng.utils.YangUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 一、15968857736 密码 123456 已开店，进入自己店铺 —（DS00000017，即，15968857736 的店）
 * 二、13922241262 密码 123456 已开店，禁用了，进入上级店铺—（DS00000017， 即，15968857736 的店）
 * 三、18626488598 密码 123456 已开店也禁用，上级也禁用，进入公司—（DS00004906， 即，13067888879
 * 账号的店铺，需可设置修改指定店） 四、18988859111 密码 123456 未开店，进入所属店铺
 * —（DS00000017，即，15968857736 的店） 五、18370573339 密码 123456 未开店，所属店铺禁用，进入上级店铺
 * —（DS00000017，即，15968857736 的店） 六、15690780606 密码 123456 未开店，所属店铺及上级也禁用，
 * 进入公司—（DS00004906，即，13067888879 账号的店铺，需可设置修改指定店）
 * 
 * @author yang
 * 
 */
@SuppressLint({ "SetJavaScriptEnabled", "SdCardPath" })
public class ShopFragment extends BaseFragment {
	@ViewInject(R.id.shop_webview)
	public static WebView shop_webview;
	private PopupWindow popupWindow;
	private RelativeLayout rlback;// webview后退键
	public static CircleImageView shop_head_img;
	private RelativeLayout shop_tv_right;// 分享当前链接
	private TextView share;
	private TextView copy;
	private TextView scan;
	private static final String APP_CACAHE_DIRNAME = "/webcache";
	protected static final String tag = "ShopFragment";

	public static ShopFragment instance;
	@SuppressWarnings("unused")
	private String chat_url = "http://wxkf.wddcn.com/client/chat.ashx?sid=17871&mid=429&utype=dzd&uid=5495667&ciid_siid=0_0&url=http://dzd.4567cn.com/vshop/detail.html?sid=17871%26gid=585956509%26iid=55621";
	private String chat_url1 = "http://wxkf.wddcn.com/client/chat.ashx?";

	public ShopFragment() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shop_rl_back:
			if (shop_webview.canGoBack()) {
				shop_webview.goBack();
			} else {
				// getActivity().finish();
			}
			break;
		case R.id.shop_head_img:
			MainActivity.slidingPaneLayout.openPane();
			break;
		case R.id.shop_tv_right:
			if (popupWindow == null) {
				popupWindow = new PopupWindow(getActivity());
				View view = View.inflate(context, R.layout.shop_popup, null);
				popupWindow.setContentView(view);
				popupWindow.setOutsideTouchable(true);
				share = (TextView) view.findViewById(R.id.shop_fm_menu_item1);
				copy = (TextView) view.findViewById(R.id.shop_fm_menu_item2);
				scan = (TextView) view.findViewById(R.id.home_fm_menu_item3);
				popupWindow.setWidth(YangUtils.getScreenWidth(getActivity()) / 3 - 20);
				popupWindow.setHeight(YangUtils.getScreenHeight(getActivity()) / 5);
				popupWindow.showAsDropDown(v);
				share.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						GlobalParams.share = 1;
						popupWindow.dismiss();
						ShareSDK.initSDK(context);
						final OnekeyShare oks = new OnekeyShare();
						// 关闭sso授权
						oks.disableSSOWhenAuthorize();
						oks.setSilent(false);
						oks.setDialogMode();
						// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
						oks.setTitle("DS4567");
						// text是分享文本，所有平台都需要这个字段
						oks.setText(shop_webview.getUrl());
						// 启动分享GUI
						oks.show(context);
					}
				});
				copy.setOnClickListener(new OnClickListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(View v) {
						popupWindow.dismiss();
						ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
						cmb.setText(shop_webview.getUrl());
						MyToastUtils.showShortToast(context, "当前网址已复制到剪切板");
					}
				});
				scan.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						popupWindow.dismiss();
						shop_webview.reload();
					}
				});
			} else {
				popupWindow.dismiss();
				popupWindow = null;
			}

			break;
		}
	}

	@Override
	public View initView(LayoutInflater inflater) {
		view = View.inflate(context, R.layout.shop_fragment, null);
		rlback = MainFragment.rlback;
		shop_head_img = MainFragment.shop_head_img;
		shop_tv_right = MainFragment.shop_tv_right;
		ViewUtils.inject(this, view);
		instance = this;
		return view;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void initData(Bundle savedInstanceState) {
		WebSettings s = shop_webview.getSettings();
		// 开启 DOM storage API 功能
		shop_webview.getSettings().setDomStorageEnabled(true);
		// 开启 database storage API 功能
		shop_webview.getSettings().setDatabaseEnabled(true);
		String cacheDirPath = context.getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
		shop_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// String cacheDirPath =
		// getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
		// 设置数据库缓存路径
		shop_webview.getSettings().setDatabasePath(cacheDirPath);
		// 设置 Application Caches 缓存目录
		shop_webview.getSettings().setAppCachePath(cacheDirPath);
		// 开启 Application Caches 功能
		shop_webview.getSettings().setAppCacheEnabled(true);

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
		shop_webview.requestFocus();
		shop_webview.setScrollBarStyle(0);
		s.setBuiltInZoomControls(false);
		s.setSupportZoom(false);
		s.setDisplayZoomControls(false);
		shop_webview.removeJavascriptInterface("searchBoxJavaBredge_");
		shop_webview.getSettings().setJavaScriptEnabled(true);
		// shop_webview.loadUrl("http://www.baidu.com");
		shop_webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.i("tag", "请求地址：" + url);
				// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				// Uri uri=Uri.parse(url);
				if (url.contains(chat_url1)) {
					if (!"".equals(SharedPreferencesUtil.getStringData(getActivity(), "hx_kefu_name", ""))) {
						Intent intent = new Intent(getActivity(), ChatActivity.class);
						intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
						intent.putExtra("userId",
								SharedPreferencesUtil.getStringData(getActivity(), "hx_kefu_name", ""));
						intent.putExtra("username", "店长");
						intent.putExtra("flag", 2);
						startActivity(intent);
					} else {
						checkUpShopid(SharedPreferencesUtil.getStringVspid(getActivity()));
					}

					return true;
				}

				if (url.startsWith("tel:")) {

					if (ExampleApplication.chatmap.containsKey(url.substring(4, url.length()))) {
						String phone = url.substring(4, url.length());
						SharedPreferencesUtil.saveStringData(context, "dzphone", phone);
						Intent intent = new Intent(getActivity(), ChatActivity.class);
						intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
						intent.putExtra("userId", ExampleApplication.chatmap.get(url.substring(4, url.length())));
						intent.putExtra("username", "店长");
						intent.putExtra("flag", 2);
						startActivity(intent);
					} else {
						List<String> mobiles = new ArrayList<String>();
						mobiles.add(url.substring(4, url.length()));
						checkPhoneNumber(gson.toJson(mobiles));
					}
					return true;
				}

				view.loadUrl(url);
				if (shop_webview.getUrl() != null) {
					if (shop_webview.getUrl().contains("http://dzd.4567cn.com/vshop/index.html")) {
						rlback.setVisibility(View.GONE);
						shop_head_img.setVisibility(View.VISIBLE);
					} else {
						shop_head_img.setVisibility(View.GONE);
						rlback.setVisibility(View.VISIBLE);
					}
				}
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (shop_webview.getUrl() != null) {
					if (shop_webview.getUrl().contains("http://dzd.4567cn.com/vshop/index.html")) {
						shop_head_img.setVisibility(View.VISIBLE);
						rlback.setVisibility(View.GONE);
					} else {
						shop_head_img.setVisibility(View.GONE);
						rlback.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (shop_webview.getUrl() != null) {
					if (shop_webview.getUrl().contains("http://dzd.4567cn.com/vshop/index.html")) {
						shop_head_img.setVisibility(View.VISIBLE);
						rlback.setVisibility(View.GONE);
					} else {
						shop_head_img.setVisibility(View.GONE);
						rlback.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		String autologinurl = SharedPreferencesUtil.getStringData(context, "autologinurl", "");
		String shopurl = SharedPreferencesUtil.getStringData(context, "shopurl", "");

		Log.e("1111", autologinurl + shopurl);
		// 首页Url
		indexUrl = autologinurl + shopurl;
		// shop_webview.loadUrl(shopurl);
		LogUtils.e(getClass(), autologinurl + shopurl);
		shop_webview.loadUrl(autologinurl + shopurl);
		Log.e(tag, autologinurl + shopurl);
		shop_webview.setOnTouchListener(new OnTouchListener() {
			private float lastY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					// Intent intent = new Intent(MainFragment.CHANGETAB);
					// intent.putExtra("tab", 1);
					// context.sendBroadcast(intent);
					// lastY = event.getY();
					break;
				// case MotionEvent.ACTION_MOVE:
				//
				// float disY = event.getY() - lastY;
				//
				// // 垂直方向滑动
				// if (Math.abs(disY) > 10) {
				// shop_gotop.bringToFront();
				// Intent intent = new Intent(MainFragment.CHANGETAB);
				// intent.putExtra("tab", 0);
				// context.sendBroadcast(intent);
				// }
				//
				// break;

				default:
					break;
				}

				return false;
			}
		});

		String headpic = SharedPreferencesUtil.getStringData(context, "headpic", "");
		if (!TextUtils.isEmpty(headpic)) {
			ImageLoader.getInstance().displayImage(headpic, shop_head_img);
		}
	}

	@Override
	public void setListener() {
		shop_head_img.setOnClickListener(this);
		rlback.setOnClickListener(this);
		shop_tv_right.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		if (receiver == null) {
			receiver = new MyBroadCast();
		}
		getActivity().registerReceiver(receiver, new IntentFilter(WEB_GO_BACk));
		super.onStart();
	}

	@Override
	public void onDestroyView() {
		shop_webview.clearCache(true);
		shop_webview.stopLoading();
		shop_webview.destroy();
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
		}
		super.onDestroy();
	}

	public static String WEB_GO_BACk = "com.web.go.back";
	private MyBroadCast receiver;

	private long lastPressTime = 0;

	private class MyBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (shop_webview.canGoBack()) {
				shop_webview.goBack();
			} else {
				long time = System.currentTimeMillis();
				if (time - lastPressTime < 500) {
					getActivity().finish();
				} else {
					Toast.makeText(context, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
					lastPressTime = System.currentTimeMillis();
				}
			}
		}
	}

	private Gson gson = new Gson();

	private String indexUrl;

	/**
	 * 过滤通讯录里没有注册环信的手机号
	 * 
	 * @param mobiles
	 *            手机号数组
	 */
	private void checkPhoneNumber(final String mobiles) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().toFilterHXMobile(mobiles);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					try {
						MobileData md = gson.fromJson(result, MobileData.class);
						if ("0".equals(md.code)) {
							List<PhoneBean> lpb = md.data.hx_mobile;
							if (lpb != null && lpb.size() > 0) {
								SharedPreferencesUtil.saveStringData(getActivity(), "hx_kefu_name", lpb.get(0).hx_name);
								if (!ExampleApplication.chatmap.containsKey(lpb.get(0).mobile))
									SharedPreferencesUtil.saveStringData(context, "dzphone", lpb.get(0).mobile);
								ExampleApplication.chatmap.put(lpb.get(0).mobile, lpb.get(0).hx_name);
								Intent intent = new Intent(getActivity(), ChatActivity.class);
								intent.putExtra("userId", lpb.get(0).hx_name);
								intent.putExtra("username", "店长");
								intent.putExtra("flag", 2);
								getActivity().startActivity(intent);
							}
						} else {
							String msg = md.msg;
							MyToastUtils.showShortToast(context, msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				super.onPostExecute(result);
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
		}.executeProxy();
	}

	/**
	 * 检查上级店铺id是否可用
	 * 
	 * @param vspid
	 */
	protected void checkUpShopid(final String vspid) {
		new HttpTask<Void, Void, InputStream>(context) {

			@Override
			protected InputStream doInBackground(Void... params) {
				InputStream result = EngineManager.getShopEngine().toGetStoreinfo(vspid);
				return result;
			}

			@Override
			protected void onPostExecute(InputStream result) {
				try {
					if (result != null) {
						ShopInfo shopinfo = new ShopInfo();
						XmlComonUtil.streamText2Model(result, shopinfo);
						String state = shopinfo.vsstate;
						LogUtil.i(tag, "上级店铺的状态：" + state);
						List<String> pList = new ArrayList<String>();
						pList.add(shopinfo.vsphone);
						checkPhoneNumber(gson.toJson(pList));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void onPreExecute() {
			}
		}.executeProxy();
	}

}
