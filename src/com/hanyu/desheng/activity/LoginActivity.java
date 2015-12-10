package com.hanyu.desheng.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.HanziToPinyin;
import com.google.gson.Gson;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.ExitApplication;
import com.hanyu.desheng.Left;
import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.HxBean;
import com.hanyu.desheng.bean.LoginBean;
import com.hanyu.desheng.bean.MobileData;
import com.hanyu.desheng.bean.PhoneBean;
import com.hanyu.desheng.bean.ShopInfo;
import com.hanyu.desheng.bean.Url;
import com.hanyu.desheng.bean.UserInfo;
import com.hanyu.desheng.db.InviteMessgeDao;
import com.hanyu.desheng.domain.InviteMessage;
import com.hanyu.desheng.domain.User;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.fragment.MainFragment;
import com.hanyu.desheng.fragment.ShopFragment;
import com.hanyu.desheng.jpush.ExampleUtil;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.XmlComonUtil;
import com.hanyu.desheng.utils.YangUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

@SuppressLint("DefaultLocale")
public class LoginActivity extends BaseActivity {
	protected static final String tag = "LoginActivity";
	protected static final int USERINFO = 0;
	@ViewInject(R.id.login_register)
	private LinearLayout login_register;// 注册
	@ViewInject(R.id.login_find_pwd)
	private TextView login_find_pwd;// 忘记密码
	@ViewInject(R.id.login_cb)
	private CheckBox login_cb;// 记住密码
	@ViewInject(R.id.login_et1)
	private EditText login_et1;// 账号
	@ViewInject(R.id.login_et2)
	private EditText login_et2;// 密码
	@ViewInject(R.id.login_btn)
	private Button login_btn;// 登录
	@ViewInject(R.id.rl_back)
	private RelativeLayout rl_back;// 后退键

	private SharedPreferences sp;
	private UserInfo userInfo;
	private ShopInfo shopinfo;
	private String openshop;// 是否开店 0否 其余代表开店的店铺id
	private String openid;// 与微信连接唯一标识
	private String mishopid;// 所属店铺id
	private ProgressDialog pd;
	private Gson gson = new Gson();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_register:
			intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.login_find_pwd:
			intent = new Intent(LoginActivity.this, LostPassWordActivity.class);
			startActivity(intent);
			break;
		case R.id.login_btn:
			String username = login_et1.getText().toString();
			String pwd = login_et2.getText().toString();
			if (TextUtils.isEmpty(username)) {
				MyToastUtils.showShortToast(context, "请输入账号");
			} else if (TextUtils.isEmpty(pwd)) {
				MyToastUtils.showShortToast(context, "请输入密码");
			} else {
				if (login_cb.isChecked()) {
					sp = getSharedPreferences("login", 0);
					Editor edit = sp.edit();
					edit.putString("username", username);
					edit.commit();
				}

				InviteMessgeDao dao = new InviteMessgeDao(this);
				List<InviteMessage> messagesList = dao.getMessagesList();
				// 调用登录接口~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				toLogin(username, pwd);
			}

			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.login;
	}

	@Override
	protected void onResume() {
		super.onResume();

		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		JPushInterface.onPause(this);
		super.onPause();
	}

	@Override
	public void init(Bundle savedInstanceState) {
		pd = new ProgressDialog(this);
		pd.setMessage("正在登陆中...");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		context = LoginActivity.this;
		if (!YangUtils.isLogin(context)) {
			rl_back.setVisibility(View.GONE);
			setTopTitle("登录");
			userInfo = new UserInfo();
			sp = getSharedPreferences("login", 0);
			Boolean checked = sp.getBoolean("isChecked", false);
			login_cb.setChecked(checked);
			String username = sp.getString("username", "");
			if (login_cb.isChecked()) {
				if (!TextUtils.isEmpty(username)) {
					login_et1.setText(username);
				}
			}
		} else {
			startActivity(new Intent(context, MainActivity.class));
			finish();
		}
	}

	@Override
	public void setListener() {
		login_find_pwd.setOnClickListener(this);
		login_btn.setOnClickListener(this);
		login_register.setOnClickListener(this);
		login_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Boolean Checked = login_cb.isChecked();
				sp = getSharedPreferences("login", 0);
				Editor edit = sp.edit();
				edit.putBoolean("isChecked", Checked);
				edit.commit();
			}
		});
	}

	/**
	 * 登录
	 * 
	 * @param username
	 *            用户名
	 * @param pwd
	 *            密码
	 */
	private void toLogin(final String username, final String pwd) {
		pd.show();
		new HttpTask<Void, Void, String>(context) {
			// 开始登录~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().toLogin(username, pwd);
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
							LoginBean bean = GsonUtils.json2Bean(result, LoginBean.class);

							SharedPreferencesUtil.saveStringData(context, "cardno", bean.data.cardno);
							SharedPreferencesUtil.saveStringData(context, "memberid", bean.data.memberid);
							SharedPreferencesUtil.saveStringData(context, "membercode", bean.data.membercode);
							SharedPreferencesUtil.saveStringData(context, "autologinurl", bean.data.autologinurl);
							LogUtil.i(tag, "登录成功" + bean.data.memberid);
							// 获取会员信息~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
							getUserInfo(bean.data.memberid, username);

						} else {
							MyToastUtils.showShortToast(context, msg);
							pd.cancel();
						}
					} else {
						pd.cancel();
					}
				} catch (JSONException e) {
					pd.cancel();
					e.printStackTrace();
					Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			protected void onPreExecute() {

			}

		}.executeProxy();
	}

	/**
	 * 获取会员信息xml流
	 * 
	 * @param memberid
	 * @param mobile
	 */
	protected void getUserInfo(final String memberid, final String mobile) {

		new HttpTask<Void, Void, InputStream>(context) {

			@Override
			protected InputStream doInBackground(Void... params) {
				InputStream result = EngineManager.getUserEngine().toGetinfo(memberid);
				return result;
			}

			@Override
			protected void onPostExecute(InputStream result) {
				if (result != null) {
					try {

						// 用xml解析工具类解析xml
						XmlComonUtil.streamText2Model(result, userInfo);
						Boolean isLoad = true;
						// 是否登录
						SharedPreferencesUtil.saveBooleanData(context, "isLoad", isLoad);
						// 会员用户名
						SharedPreferencesUtil.saveStringData(context, "miname", userInfo.miname);
						// 开店id
						SharedPreferencesUtil.saveStringData(context, "openshop", userInfo.openshop);
						// 微信的openid
						SharedPreferencesUtil.saveStringData(context, "openid", userInfo.openid);
						// 会员积分
						SharedPreferencesUtil.saveStringData(context, "miinter", userInfo.miinter);
						// 会员手机号
						SharedPreferencesUtil.saveStringData(context, "miphone", userInfo.miphone);
						// 所属店铺id
						SharedPreferencesUtil.saveStringData(context, "mishopid", userInfo.mishopid);
						// 会员所属部门
						SharedPreferencesUtil.saveStringData(context, "minodename", userInfo.minodename);
						// 会员头像
						SharedPreferencesUtil.saveStringData(context, "headpic", userInfo.miuserheader);
						Log.e(tag, "openshop：" + userInfo.openshop + "，openid：" + userInfo.openid + "，mishopid："
								+ userInfo.mishopid + "userInfo.miuserheader" + userInfo.miuserheader);
						// 判断店主的店铺状态~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
						openshop = userInfo.openshop;
						openid = userInfo.openid;
						mishopid = userInfo.mishopid;// 所属店铺id

						if (!openshop.equals("0")) {
							checkShopid(openshop);
						} else {
							if (!mishopid.equals("0")) {
								checkBelongsShopid(mishopid);
							} else {
								SharedPreferencesUtil.saveStringData(context, "shopurl", Url.shop);
							}
						}

						intent = new Intent(Left.UPDATA_CENTER);
						intent.putExtra("userinfo", userInfo);
						setResult(USERINFO, intent);
						sendBroadcast(intent);
						LogUtil.i(tag, userInfo.miphone);
						LogUtil.i(tag, userInfo.miphone1 + "phone1");
						// 获取环信信息~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
						// getHxInfo(userInfo.miphone);
						getHxInfo(login_et1.getText().toString());
					} catch (Exception e) {
						e.printStackTrace();
						pd.cancel();
					}
				} else {
					pd.cancel();
				}
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

	/**
	 * 获取环信用户信息
	 * 
	 * @param username
	 *            用户手机号
	 * @param bean
	 */
	protected void getHxInfo(final String username) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().toLoginHx(username);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					if (result != null) {
						HxBean hx = GsonUtils.json2Bean(result, HxBean.class);
						if ("0".equals(hx.code)) {
							JPushInterface.resumePush(getApplicationContext());
							SharedPreferencesUtil.saveStringData(context, "hx_name", hx.data.hx_name);
							// 开始登录环信~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
							toLoginHx(hx.data.hx_name, hx.data.hx_pwd);
							// 调用JPush API设置Alias
							mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, hx.data.hx_name));
						} else {
							pd.cancel();
							MyToastUtils.showShortToast(context, hx.msg);
						}
					}
				} catch (Exception e) {
					pd.cancel();
					e.printStackTrace();
				}
			}

			@Override
			protected void onPreExecute() {

			}

		}.executeProxy();
	}

	private void toLoginHx(final String currentUsername, final String currentPassword) {

		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

			@Override
			public void onSuccess() {
				// 登陆成功，保存用户名密码
				ExampleApplication.getInstance().setUserName(currentUsername);
				ExampleApplication.getInstance().setPassword(currentPassword);
				try {
					if (JPushInterface.isPushStopped(getApplicationContext())) {
						JPushInterface.resumePush(getApplicationContext());
					}
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					// ** manually load all local groups and
					// conversations in case we are auto login
					EMGroupManager.getInstance().loadAllGroups();// 加载个人群组
					EMChatManager.getInstance().loadAllConversations();// 加载个人会话
					// 处理好友和群组
				} catch (Exception e) {
					pd.cancel();
					e.printStackTrace();
					// 取好友或者群聊失败，不让进入主页面
					runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							// pd.dismiss();
							ExampleApplication.getInstance().logout(null);
							Toast.makeText(getApplicationContext(), R.string.login_failure_failed, 1).show();
						}
					});
					return;
				}
				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMChatManager.getInstance()
						.updateCurrentUserNick(ExampleApplication.currentUserNick.trim());
				if (!updatenick) {
					Log.e("LoginActivity", "update current user nick fail");
				}
				pd.cancel();
				// 登录完成，准备进入主界面~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				finish();
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				pd.cancel();
				runOnUiThread(new Runnable() {
					public void run() {
						// pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(ExampleApplication.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
					.toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}

	/**
	 * 检测用户自己店铺是否可用
	 * 
	 * @param openshop2
	 */
	private void checkShopid(final String openshop2) {
		new HttpTask<Void, Void, InputStream>(context) {

			@Override
			protected InputStream doInBackground(Void... params) {
				InputStream result = EngineManager.getShopEngine().toGetStoreinfo(openshop2);
				return result;
			}

			//
			@Override
			protected void onPostExecute(InputStream result) {
				try {
					if (result != null) {
						shopinfo = new ShopInfo();
						XmlComonUtil.streamText2Model(result, shopinfo);
						String state = shopinfo.vsstate;
						SharedPreferencesUtil.saveStringData(context, "shopstate", state);
						SharedPreferencesUtil.saveStringVspid(LoginActivity.this, shopinfo.vspid);
						Log.e(tag, "自己店铺的状态：" + state);
						if (state.equals("1")) {// 自己店铺可用
							Log.e("login", Url.shop2 + openshop + "&amp;OpenId=" + openid);
							SharedPreferencesUtil.saveStringData(context, "shopurl",
									Url.shop2 + openshop + "&OpenId=" + openid);
							// SharedPreferencesUtil.saveStringData(context,
							// "shopurl", Url.shop2 + openshop+ "&amp;OpenId=" +
							// openid);
						} else {// 不可用，检测上级店铺
							String vspid = shopinfo.vspid;
							checkUpShopid(vspid);
						}
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

	/**
	 * 检测所属店铺id是否可用
	 * 
	 * @param mishopid
	 */
	private void checkBelongsShopid(final String mishopid) {
		new HttpTask<Void, Void, InputStream>(context) {

			@Override
			protected InputStream doInBackground(Void... params) {
				InputStream result = EngineManager.getShopEngine().toGetStoreinfo(mishopid);
				return result;
			}

			@Override
			protected void onPostExecute(InputStream result) {
				try {
					if (result != null) {
						shopinfo = new ShopInfo();
						XmlComonUtil.streamText2Model(result, shopinfo);
						String state = shopinfo.vsstate;
						LogUtil.i(tag, "所属店铺的状态：" + state);
						if (state.equals("1")) {
							SharedPreferencesUtil.saveStringData(context, "shopurl",
									Url.shop2 + mishopid + "&amp;OpenId=" + openid);
						} else {
							String vspid = shopinfo.vspid;
							checkUpShopid(vspid);
						}
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
						shopinfo = new ShopInfo();
						XmlComonUtil.streamText2Model(result, shopinfo);
						String state = shopinfo.vsstate;
						LogUtil.i(tag, "上级店铺的状态：" + state);
						List<String> pList = new ArrayList<String>();
						pList.add(shopinfo.vsphone);
						checkPhoneNumber(gson.toJson(pList));
						if (state.equals("1")) {// 上级店铺可用，使用上级店铺ID
							SharedPreferencesUtil.saveStringData(context, "shopurl",
									Url.shop2 + vspid + "&amp;OpenId=" + openid);
						} else {
							LogUtil.i(tag, "公司店铺");
							SharedPreferencesUtil.saveStringData(context, "shopurl", Url.shop);
						}
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
								SharedPreferencesUtil.saveStringData(LoginActivity.this, "hx_kefu_name",
										lpb.get(0).hx_name);
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

	// --------------------------通过极光推送设置别名----------------------//
	private static final int MSG_SET_ALIAS = 1001;
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SET_ALIAS:
				Log.d(tag, "通过handler设置设备别名");
				JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
				break;

			default:
				Log.i(tag, "Unhandled msg - " + msg.what);
			}
		}
	};

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Log.i("JPush_alias", logs + alias);
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.i(tag, logs);
				if (ExampleUtil.isConnected(getApplicationContext())) {
					mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
				} else {
					Log.i(tag, "No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Log.e(tag, logs);
			}
		}
	};

}
