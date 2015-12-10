package com.hanyu.desheng.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.ChatBean;
import com.hanyu.desheng.bean.UserInfo;
import com.hanyu.desheng.db.UserDao;
import com.hanyu.desheng.domain.User;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.ui.CircleImageView;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.XmlComonUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PerInfoAct extends BaseActivity {
	@ViewInject(R.id.dzact_riv)
	private CircleImageView dzact_riv;// 头像
	@ViewInject(R.id.dzact_tv1)
	private TextView dzact_tv1;// 昵称
	@ViewInject(R.id.dzact_tv2)
	private TextView dzact_tv2;// 手机号
	@ViewInject(R.id.dzact_tv3)
	private TextView dzact_tv3;// QQ
	@ViewInject(R.id.dzact_tv4)
	private TextView dzact_tv4;// 微信
	private UserInfo userInfo;
	private String phone;
	private FrameLayout my_order_lv_fl;

	private String hxusername;
	private Gson gson = new Gson();

	@ViewInject(R.id.btnChat)
	Button btnChat;

	UserDao dao;
	private Context mContext;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnChat:
			if ("添加好友".equals(btnChat.getText().toString())) {
				addContact(hxusername, dzact_tv1.getText().toString());
			} else {
				Intent intent = new Intent(PerInfoAct.this, ChatActivity.class);
				intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
				intent.putExtra("userId", hxusername);
				if (!TextUtils.isEmpty(dzact_tv1.getText().toString()))
					intent.putExtra("username", dzact_tv1.getText().toString());
				startActivity(intent);
			}
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.dzact;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		mContext = this;
		dao = new UserDao(this);
		my_order_lv_fl = (FrameLayout) findViewById(R.id.my_order_lv_fl);
		setBack();
		setTopTitle("好友信息");
		userInfo = new UserInfo();
		context = PerInfoAct.this;
		intent = getIntent();
		Bundle extras = intent.getExtras();
		phone = extras.getString("phone");
		hxusername = intent.getStringExtra("hxuser");

		if ("1".equals(intent.getStringExtra("flag"))) {
			btnChat.setVisibility(View.VISIBLE);
			try {
				List<String> userNames = EMChatManager.getInstance().getContactUserNames();
				if (userNames.contains(hxusername)) {
					btnChat.setText("点击聊天");
				} else {
					btnChat.setText("添加好友");
				}
			} catch (EaseMobException e) {
				e.printStackTrace();
			}
		} else {
			btnChat.setVisibility(View.GONE);
		}

//		if (hxusername == null) {
//			getUserInfo(phone);
//		} else {
			List<String> list = new ArrayList<String>();
			list.add(hxusername);
			getHxUserName(gson.toJson(list));
//		}
		btnChat.setOnClickListener(this);

	}

	@Override
	public void setListener() {

	}

	private void getHxUserName(final String uns) {

		new HttpTask<Void, Void, String>(this) {
			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().toGetHxUser(uns);
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					Log.e("111", result);
					try {
						ChatBean chatBean = GsonUtils.json2Bean(result, ChatBean.class);
						if (chatBean != null && chatBean.code == 0) {
							if (chatBean.data.info_list != null && chatBean.data.info_list.size() > 0) {
								ImageLoader.getInstance().displayImage(chatBean.data.info_list.get(0).miuserheader,
										dzact_riv);
								dzact_tv1.setText(chatBean.data.info_list.get(0).miname);
								getUserInfo(chatBean.data.info_list.get(0).mobile);
							}
						}
					} catch (Exception e) {
					}
					
				} else {
					MyToastUtils.showShortToast(getApplicationContext(), "网络错误");
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
	 */
	protected void getUserInfo(final String phone) {

		new HttpTask<Void, Void, InputStream>(context) {

			@Override
			protected InputStream doInBackground(Void... params) {
				InputStream result = EngineManager.getUserEngine().toGetinfo2(phone);
				return result;
			}

			@Override
			protected void onPostExecute(InputStream result) {
				if (result != null) {
					try {
						// 用xml解析工具类解析xml
						XmlComonUtil.streamText2Model(result, userInfo);
						// 昵称
//						dzact_tv1.setText(userInfo.miname);
						// 真实姓名
						// personal_et.setText(userInfo.mirealname);
						// email
						// personal_tv3.setText(userInfo.miemail);
						// 手机号
						dzact_tv2.setText(userInfo.miphone);
						// qq
						dzact_tv3.setText(userInfo.qq);
						// 微信
						dzact_tv4.setText(userInfo.miweixin);
						// ImageLoader.getInstance().displayImage(
						// userInfo.miuserheader, dzact_riv);

						// 保存联系人
						User user = new User();
						user.setUsername(hxusername);
						user.setAvatar(userInfo.miuserheader);
						user.setNick(userInfo.miname);
						user.setMobile(userInfo.miphone);

						// UserDao dao = new UserDao(context);
						// dao.saveContact(user);

						// 职业
						// personal_profession.setText(userInfo.miprofession);
						// 生日
						// @SuppressWarnings("deprecation")
						// Date date = new Date(userInfo.mibirthday);
						// String format = (String) DateFormat.format(
						// "yyyy-MM-dd", date);
						// personal_birthday.setText(format);
						// // 性别
						// if ("1".equals(userInfo.misex)) {
						// personal_rg.check(R.id.personal_man);
						// } else if ("0".equals(userInfo.misex)) {
						// personal_rg.check(R.id.personal_woman);
						// }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				my_order_lv_fl.setVisibility(View.GONE);
			}

			@Override
			protected void onPreExecute() {

			}

		}.executeProxy();
	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact(final String hx_username, final String username) {
		if (ExampleApplication.getInstance().getUserName().equals(hx_username)) {
			String str = mContext.getResources().getString(R.string.not_add_myself);
			mContext.startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", str));
			return;
		}

		if (ExampleApplication.getInstance().getContactList().containsKey(hx_username)) {
			String strin = mContext.getResources().getString(R.string.This_user_is_already_your_friend);
			mContext.startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", strin));
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(mContext);
		String stri = mContext.getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {

				try {
					// demo写死了个reason，实际应该让用户手动填入
					// String s =
					// mContext.getResources().getString(R.string.Add_a_friend);
					String s = username + "请求加你为好友";
					EMContactManager.getInstance().addContact(hx_username, s);
					((Activity) mContext).runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = mContext.getResources().getString(R.string.send_successful);
							Toast.makeText(mContext, s1, 1).show();
						}
					});
				} catch (final Exception e) {
					((Activity) mContext).runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = mContext.getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(mContext, s2 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

}
