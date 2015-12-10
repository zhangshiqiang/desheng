package com.hanyu.desheng.activity;

import java.util.List;

import com.easemob.chat.EMContactManager;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.adapter.NewFriendsMsgAdapter;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.db.InviteMessgeDao;
import com.hanyu.desheng.db.UserDao;
import com.hanyu.desheng.domain.InviteMessage;
import com.hanyu.desheng.util.LogUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.zxing.CaptureActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AddFrendActivity extends BaseActivity {
	@ViewInject(R.id.addfriend_lv)
	private ListView addfriend_lv;
	@ViewInject(R.id.addfriend_tv)
	private RelativeLayout addfriend_tv;// 手机联系人
	@ViewInject(R.id.addfriend_search)
	private RelativeLayout addfriend_search;// 搜索好友
	@ViewInject(R.id.addfriend_scanner)
	private RelativeLayout addfriend_scanner;// 扫一扫
	protected static final int RESULT = 5555;
	@SuppressWarnings("unused")
	private UserDao dao;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addfriend_tv:
			intent = new Intent(AddFrendActivity.this,
					InviteFriendActivity.class);
			startActivity(intent);
			break;
		case R.id.addfriend_search:
			intent = new Intent(AddFrendActivity.this,
					SearchFriendActivity.class);
			startActivity(intent);
			break;
		case R.id.addfriend_scanner:
			intent = new Intent(AddFrendActivity.this, CaptureActivity.class);
			intent.putExtra("flag", 1);
			startActivityForResult(intent, RESULT);
			break;

		default:
			break;
		}
	}
	
	@Override
	public int setLayout() {
		return R.layout.addfriend;
	}
	
	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("新增好友");
		dao = new UserDao(this);
		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();
		
		for (InviteMessage inviteMessage : msgs) {
			LogUtils.e(getClass(), "新消息:"+inviteMessage);
		}
		// 设置adapter
		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
		addfriend_lv.setAdapter(adapter);
		try {
			ExampleApplication.getInstance().getContactList()
					.get(ExampleApplication.NEW_FRIENDS_USERNAME)
					.setUnreadMsgCount(0);
		} catch (Exception e) {
		}

		// MyAdapter adapter = new MyAdapter();
		// addfriend_lv.setAdapter(adapter);
		
		IntentFilter filter = new IntentFilter(NEW_FRIEND_REQUEST);
		newFriendRecevier = new NewFriendRecevier();
		registerReceiver(newFriendRecevier, filter);
	}
	
	public static final String NEW_FRIEND_REQUEST ="new_friend_request_action";
	private NewFriendRecevier newFriendRecevier;
	private class NewFriendRecevier extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtils.e(getClass(), "有新的消息了````````````````");
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (newFriendRecevier != null) {
			unregisterReceiver(newFriendRecevier);
		}
	}
	
	

	@Override
	public void setListener() {
		addfriend_tv.setOnClickListener(this);
		addfriend_search.setOnClickListener(this);
		addfriend_scanner.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == 5555 && arg1 == 666) {
			if (arg2 != null) {
				// 扫描二维码获取的其他用户的环信用户名
				String hx_username = arg2.getExtras().getString("hx_name")
						.toString();
				String username = SharedPreferencesUtil.getStringData(
						AddFrendActivity.this, "miname", "");
				addContact(hx_username, username);
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact(final String hx_username, final String username) {
		if (ExampleApplication.getInstance().getUserName().equals(hx_username)) {
			String str = getResources().getString(R.string.not_add_myself);
			startActivity(new Intent(AddFrendActivity.this, AlertDialog.class)
					.putExtra("msg", str));
			return;
		}

		if (ExampleApplication.getInstance().getContactList()
				.containsKey(hx_username)) {
			String strin = getResources().getString(
					R.string.This_user_is_already_your_friend);
			startActivity(new Intent(AddFrendActivity.this, AlertDialog.class)
					.putExtra("msg", strin));
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {
				try {
					String s = "请求加你为好友";
					EMContactManager.getInstance().addContact(hx_username, s);
					runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(
									R.string.send_successful);
							Toast.makeText(AddFrendActivity.this, s1, 1).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(
									R.string.Request_add_buddy_failure);
							Toast.makeText(AddFrendActivity.this,
									s2 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}
}
