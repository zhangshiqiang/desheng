package com.hanyu.desheng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.google.gson.Gson;
import com.hanyu.desheng.activity.AddFrendActivity;
import com.hanyu.desheng.activity.AlertDialog;
import com.hanyu.desheng.activity.BannedActivity;
import com.hanyu.desheng.activity.ChatActivity;
import com.hanyu.desheng.activity.LoginActivity;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.ChatBean;
import com.hanyu.desheng.db.InviteMessgeDao;
import com.hanyu.desheng.db.UserDao;
import com.hanyu.desheng.domain.InviteMessage;
import com.hanyu.desheng.domain.InviteMessage.InviteMesageStatus;
import com.hanyu.desheng.domain.User;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.fragment.ContactsFragment;
import com.hanyu.desheng.fragment.MainFragment;
import com.hanyu.desheng.fragment.MessageFragment;
import com.hanyu.desheng.fragment.MyFriendFragment;
import com.hanyu.desheng.fragment.ShopFragment;
import com.hanyu.desheng.util.LogUtils;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.leaf.library.slidingmenu.SlidingMenu;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

@SuppressLint("DefaultLocale")
public class MainActivity extends BaseActivity {
	private Left left_Fragment;
	FrameLayout left, right;
	public static SlidingPaneLayout slidingPaneLayout;
	protected static final String TAG = "MainActivity";
	// // 未读消息textview
	// private TextView unreadLabel;
	// // 未读通讯录textview
	// private TextView unreadAddressLable;

	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;
	public static boolean isForeground = false;

	private NewMessageBroadcastReceiver msgReceiver;

	@Override
	public void onClick(View arg0) {

	}

	/**
	 * 二维码 扫一扫，解码为环信用户名，发送添加好友请求
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 555 && resultCode == 666) {
			if (data != null) {
				String hx_username = data.getExtras().getString("hx_name").toString();
				String username = SharedPreferencesUtil.getStringData(context, "miname", "");
				addContact(hx_username, username);
			}
		} else if (requestCode == MainFragment.NEW_GROUP && resultCode == RESULT_OK) {
			// 创建新群
			// 进入群聊
			Intent intent = new Intent(this, ChatActivity.class);
			// it is group chat
			String groupId = data.getStringExtra("groupId");
			intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
			intent.putExtra("groupId", groupId);
			startActivityForResult(intent, 0);

		}

	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact(final String hx_username, final String username) {
		if (ExampleApplication.getInstance().getUserName().equals(hx_username)) {
			String str = getResources().getString(R.string.not_add_myself);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", str));
			return;
		}

		if (ExampleApplication.getInstance().getContactList().containsKey(hx_username)) {
			String strin = getResources().getString(R.string.This_user_is_already_your_friend);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", strin));
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
					String s = username + "请求加你为好友";
					EMContactManager.getInstance().addContact(hx_username, s);
					runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(R.string.send_successful);
							Toast.makeText(MainActivity.this, s1, 1).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(MainActivity.this, s2 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	@Override
	public int setLayout() {
		return R.layout.activity_main;
	}

	public void check_staudy(View view) {
		intent = new Intent(MainFragment.CHANGETAB);
		intent.putExtra("tab", 0);
		// main_rl.setVisibility(View.GONE);
		sendBroadcast(intent);
		slidingPaneLayout.closePane();
		// Toast.makeText(this, btn.getText() + "--click--", 1).show();
	}

	public void check_contents(View view) {
		intent = new Intent(MainFragment.CHANGETAB);
		intent.putExtra("tab", 1);
		// main_rl.setVisibility(View.GONE);
		sendBroadcast(intent);
		slidingPaneLayout.closePane();
		// Toast.makeText(this, btn.getText() + "--click--", 1).show();
	}

	public void check_msg(View view) {
		intent = new Intent(MainFragment.CHANGETAB);
		intent.putExtra("tab", 3);
		slidingPaneLayout.closePane();
		// main_rl.setVisibility(View.GONE);
		sendBroadcast(intent);
		// Toast.makeText(this, btn.getText() + "--click--", 1).show();
	}

	public void check_center(View view) {
		intent = new Intent(MainFragment.CHANGETAB);
		intent.putExtra("tab", 4);
		slidingPaneLayout.closePane();
		// main_rl.setVisibility(View.GONE);
		sendBroadcast(intent);
		// Toast.makeText(this, btn.getText() + "--click--", 1).show();
	}

	@Override
	public void init(Bundle savedInstanceState) {
		left_Fragment = new Left();
		EMChatManager.getInstance().getChatOptions().setUseRoster(true);

		if (savedInstanceState != null && savedInstanceState.getBoolean(ExampleApplication.ACCOUNT_REMOVED, false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			ExampleApplication.getInstance().logout(null);

			// JPushInterface.stopPush(getApplicationContext());
			startActivity(new Intent(this, LoginActivity.class));
			finish();

			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理

			// JPushInterface.stopPush(getApplicationContext());
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return;
		}

		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(ExampleApplication.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		MainFragment homeFragment = new MainFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.rightfragment, homeFragment, "HOME").commit();
		inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);
		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(
				EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		// 注册一个透传消息的BroadcastReceiver
		IntentFilter cmdMessageIntentFilter = new IntentFilter(
				EMChatManager.getInstance().getCmdMessageBroadcastAction());
		cmdMessageIntentFilter.setPriority(3);
		registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);
		// 监听好友状态
		contactListener = new MyContactListener();
		EMContactManager.getInstance().setContactListener(contactListener);
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		// 注册群聊相关的listener
		EMGroupManager.getInstance().addGroupChangeListener(new MyGroupChangeListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
		JPushInterface.init(getApplicationContext());
		registerMessageReceiver();
		// mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		//
		// // init the ListView and Adapter, nothing new
		//// initListView();
		//
		// // set a custom shadow that overlays the main content when the drawer
		// // opens
		// mDrawerLayout.setDrawerShadow(R.drawable.center_02,
		// GravityCompat.START);
		//
		// mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
		// R.drawable.center_01, R.string.drawer_open,
		// R.string.drawer_close) {
		//
		// /**
		// * Called when a drawer has settled in a completely closed state.
		// */
		// public void onDrawerClosed(View view) {
		//
		// invalidateOptionsMenu(); // creates call to
		// // onPrepareOptionsMenu()
		// }
		//
		// /** Called when a drawer has settled in a completely open state. */
		// public void onDrawerOpened(View drawerView) {
		//
		// invalidateOptionsMenu(); // creates call to
		// // onPrepareOptionsMenu()
		// }
		// };
		//
		// // Set the drawer toggle as the DrawerListener
		// mDrawerLayout.setDrawerListener(mDrawerToggle);
		//
		// // enable ActionBar app icon to behave as action to toggle nav drawer
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);
		// Note: getActionBar() Added in API level 11
		setContentView(R.layout.activity_main);
		slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.slidingPaneLayout);
		left = (FrameLayout) findViewById(R.id.leftfragment);

		right = (FrameLayout) findViewById(R.id.rightfragment);

		FragmentManager fragmentManager = this.getSupportFragmentManager();
		fragmentManager.beginTransaction().add(R.id.leftfragment, new Left(), "left").commit();

		fragmentManager.beginTransaction().add(R.id.rightfragment, new Right(), "right").commit();

		slidingPaneLayout.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				left.setScaleY(slideOffset / 2 + 0.5F);
				left.setScaleX(slideOffset / 2 + 0.5F);
				left.setAlpha(slideOffset);
				right.setScaleY(1 - slideOffset / 5);
				if(Left.menu_msgcnt.getVisibility() != View.VISIBLE){
					MainFragment.shop_head_txt.setVisibility(View.GONE);
				}else{
					MainFragment.shop_head_txt.setVisibility(View.VISIBLE);
				}
				
			}

			@Override
			public void onPanelOpened(View arg0) {
				ShopFragment.shop_head_rl.setAlpha(0.0f);
			}

			@Override
			public void onPanelClosed(View arg0) {
				ShopFragment.shop_head_rl.setAlpha(1.0f);
			}

		});
		// menu_center = (ImageButton) findViewById(R.id.menu_center);
		// menu_contens = (ImageButton) findViewById(R.id.menu_contents);
		// menu_msg = (ImageButton) findViewById(R.id.menu_msg);
		// menu_shop = (ImageButton) findViewById(R.id.menu_shop);
		// menu_staudy = (ImageButton) findViewById(R.id.menu_staudy);
		// menu_center.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(MainFragment.CHANGETAB);
		// intent.putExtra("tab", 4);
		// // main_rl.setVisibility(View.GONE);
		// sendBroadcast(intent);
		// }
		// });
		// menu_contens.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(MainFragment.CHANGETAB);
		// intent.putExtra("tab", 1);
		// // main_rl.setVisibility(View.GONE);
		// sendBroadcast(intent);
		// }
		// });
		// menu_msg.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(MainFragment.CHANGETAB);
		// intent.putExtra("tab", 3);
		// // main_rl.setVisibility(View.GONE);
		// sendBroadcast(intent);
		// }
		// });
		// menu_shop.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(MainFragment.CHANGETAB);
		// intent.putExtra("tab", 2);
		// // main_rl.setVisibility(View.GONE);
		// sendBroadcast(intent);
		// }
		// });
		// menu_staudy.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(MainFragment.CHANGETAB);
		// intent.putExtra("tab", 0);
		// // main_rl.setVisibility(View.GONE);
		// sendBroadcast(intent);
		// }
		// });

	}

	@Override
	public void setListener() {

	}

	@Override
	protected void onDestroy() {
		if (msgReceiver != null) {
			unregisterReceiver(msgReceiver);
		}
		if (ackMessageReceiver != null) {
			unregisterReceiver(ackMessageReceiver);
		}
		if (cmdMessageReceiver != null) {
			unregisterReceiver(cmdMessageReceiver);
		}
		if (mMessageReceiver != null) {
			unregisterReceiver(mMessageReceiver);
		}
		EMContactManager.getInstance().removeContactListener();
		super.onDestroy();
	}

	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		Intent intent = new Intent(MainFragment.MSG);
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			intent.putExtra("unreadMsgCountTotal", count);
			sendBroadcast(intent);
		} else if (count == 0) {
			intent.putExtra("unreadMsgCountTotal", count);
			sendBroadcast(intent);
		}
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		// 提示有新消息
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

		// // 刷新bottom bar消息未读数
		// updateUnreadAddressLable();
		// // 刷新好友页面ui
		// if (currentTabIndex == 1)
		// contactListFragment.refresh();
	}

	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

			String from = intent.getStringExtra("from");
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			// 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
			if (ChatActivity.activityInstance != null) {
				if (message.getChatType() == ChatType.GroupChat) {
					if (message.getTo().equals(ChatActivity.activityInstance.getToChatUsername()))
						return;
				} else {
					if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
						return;
				}
			}

			// 注销广播接收者，否则在ChatActivity中会收到这个广播
			abortBroadcast();

			if (message == null) {
				return;
			}
			notifyNewMessage(message);
			sendBroadcast(new Intent(MessageFragment.UPDATE_CHAT_HISTORY));
			// 刷新bottom bar消息未读数
			updateUnreadLabel();
			// if (currentTabIndex == 0) {
			// // 当前页面如果为聊天历史页面，刷新此页面
			// if (chatHistoryFragment != null) {
			// chatHistoryFragment.refresh();
			// }
			// }

		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();

			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");

			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);

				if (msg != null) {

					// 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
					if (ChatActivity.activityInstance != null) {
						if (msg.getChatType() == ChatType.Chat) {
							if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
								return;
						}
					}

					msg.isAcked = true;
				}
			}

		}
	};

	/**
	 * 透传消息BroadcastReceiver
	 */
	private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();
			EMLog.d(TAG, "收到透传消息");
			// 获取cmd message对象
			@SuppressWarnings("unused")
			String msgId = intent.getStringExtra("msgid");
			EMMessage message = intent.getParcelableExtra("message");
			// 获取消息body
			CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
			String action = cmdMsgBody.action;// 获取自定义action

			// 获取扩展属性 此处省略
			// message.getStringAttribute("");
			EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
			String st9 = getResources().getString(R.string.receive_the_passthrough);
			// Toast.makeText(MainActivity.this, st9 + action,
			// Toast.LENGTH_SHORT)
			// .show();
			if (action.equals(BannedActivity.USER_BAND_ACTION) || action.equals(BannedActivity.USER_UNBAND_ACTION)) {
				Intent forward = new Intent();
				forward.setAction(ChatActivity.BAND_MESSAGE_ACTION);
				forward.putExtra("action", action);
				sendBroadcast(forward);
			}
		}
	};

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	/**
	 * 保存邀请等msg
	 * 
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		// 未读数加1
		User user = ExampleApplication.getInstance().getContactList().get(ExampleApplication.NEW_FRIENDS_USERNAME);
		if (user.getUnreadMsgCount() == 0)
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
	}

	/**
	 * set head
	 * 
	 * @param username
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	User setUserHead(String username) {
		User user = new User();
		user.setUsername(username);
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
		return user;
	}

	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements EMConnectionListener {

		@Override
		public void onConnected() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// chatHistoryFragment.errorItem.setVisibility(View.GONE);
				}

			});
		}

		@Override
		public void onDisconnected(final int error) {
			@SuppressWarnings("unused")
			final String st1 = getResources().getString(R.string.Less_than_chat_server_connection);
			@SuppressWarnings("unused")
			final String st2 = getResources().getString(R.string.the_current_network);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						showAccountRemovedDialog();
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						showConflictDialog();
					} else {
						// chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
						// if (NetUtils.hasNetwork(MainActivity.this))
						// chatHistoryFragment.errorText.setText(st1);
						// else
						// chatHistoryFragment.errorText.setText(st2);
					}
				}

			});
		}
	}

	private Gson gson = new Gson();

	/**
	 * MyGroupChangeListener
	 */
	private class MyGroupChangeListener implements GroupChangeListener {

		@SuppressWarnings("unused")
		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

			boolean hasGroup = false;
			for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
				if (group.getGroupId().equals(groupId)) {
					hasGroup = true;
					break;
				}
			}
			if (!hasGroup)
				return;
			// 被邀请

			String st3 = getResources().getString(R.string.Invite_you_to_join_a_group_chat);
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);

			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody("欢迎加入该群"));
			// 保存邀请消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					sendBroadcast(new Intent(MessageFragment.UPDATE_CHAT_HISTORY));
					sendBroadcast(new Intent(MyFriendFragment.UPDATE_CONTACT_HISTORY));
					// 刷新ui
					// if (currentTabIndex == 0)
					// chatHistoryFragment.refresh();
					// if
					// (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName()))
					// {
					// GroupsActivity.instance.onResume();
					// }
				}
			});
			// List<String> list=new ArrayList<String>();
			// list.add(inviter);
			// getHxUserName(gson.toJson(list),msg,groupId,inviter,st3);
		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						// if (currentTabIndex == 0)
						// chatHistoryFragment.refresh();
						// if
						// (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName()))
						// {
						// GroupsActivity.instance.onResume();
						// }
					} catch (Exception e) {
						EMLog.e(TAG, "refresh exception " + e.getMessage());
					}
				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					sendBroadcast(new Intent(MessageFragment.UPDATE_CHAT_HISTORY));
					sendBroadcast(new Intent(MyFriendFragment.UPDATE_CONTACT_HISTORY));
					// if (currentTabIndex == 0)
					// chatHistoryFragment.refresh();
					// if
					// (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName()))
					// {
					// GroupsActivity.instance.onResume();
					// }
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
			// 用户申请加入群聊
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
			msg.setStatus(InviteMesageStatus.BEAPPLYED);
			notifyNewIviteMessage(msg);
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {
			String st4 = getResources().getString(R.string.Agreed_to_your_group_chat_application);
			// 加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(accepter + st4));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					sendBroadcast(new Intent(MessageFragment.UPDATE_CHAT_HISTORY));
					sendBroadcast(new Intent(MyFriendFragment.UPDATE_CONTACT_HISTORY));
					// 刷新ui
					// if (currentTabIndex == 0)
					// chatHistoryFragment.refresh();
					// if
					// (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName()))
					// {
					// GroupsActivity.instance.onResume();
					// }
				}
			});
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}

	}

	/**
	 * 当好友添加，读取好友信息，并保存
	 * 
	 * @param usernames
	 */
	private void getHxUserName(final String uns, final List<String> usernames) {
		new HttpTask<Void, Void, String>(MainActivity.this) {

			// private AlertDialog errorDialog;

			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().toGetHxUser(uns);
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					ChatBean chatBean = GsonUtils.json2Bean(result, ChatBean.class);
					if (chatBean != null && chatBean.code == 0) {
						Map<String, User> userlist = new HashMap<String, User>();
						if (chatBean.data.info_list != null && chatBean.data.info_list.size() > 0) {
							for (int i = 0; i < chatBean.data.info_list.size(); i++) {

								User user = new User();
								user.setUsername(chatBean.data.info_list.get(i).hx_name);
								user.setAvatar(chatBean.data.info_list.get(i).miuserheader);
								user.setNick(chatBean.data.info_list.get(i).miname);
								user.setMobile(chatBean.data.info_list.get(i).mobile);
								setUserHearder(chatBean.data.info_list.get(i).miname, user);
								userlist.put(chatBean.data.info_list.get(i).miname, user);
							}
						}
						// 存入db
						UserDao dao = new UserDao(MainActivity.this);
						List<User> users = new ArrayList<User>(userlist.values());
						dao.saveContactList(users);
					} else {
						getHxUserName(uns, usernames);
					}
				}
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

	/***
	 * 好友变化listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			LogUtils.e(getClass(), "当好友添加");
			getHxUserName(gson.toJson(usernameList), usernameList);

			// 保存增加的联系人
			Map<String, User> localUsers = ExampleApplication.getInstance().getContactList();
			Map<String, User> toAddUsers = new HashMap<String, User>();
			for (String username : usernameList) {
				User user = setUserHead(username);
				// 添加好友时可能会回调added方法两次
				if (!localUsers.containsKey(username)) {
					userDao.saveContact(user);
				}
				toAddUsers.put(username, user);
			}
			localUsers.putAll(toAddUsers);

			sendBroadcast(new Intent(MyFriendFragment.UPDATE_CONTACT_HISTORY));
		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			LogUtils.e(getClass(), "您被好友删除了");

			// 被删除
			Map<String, User> localUsers = ExampleApplication.getInstance().getContactList();
			for (String username : usernameList) {
				localUsers.remove(username);
				userDao.deleteContact(username);
				inviteMessgeDao.deleteMessage(username);
			}
			runOnUiThread(new Runnable() {
				@SuppressLint("ShowToast")
				public void run() {
					// 如果正在与此用户的聊天页面
					String st10 = getResources().getString(R.string.have_you_removed);
					if (ChatActivity.activityInstance != null
							&& usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
						Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, 1)
								.show();
						ChatActivity.activityInstance.finish();
					}
					updateUnreadLabel();

					sendBroadcast(new Intent(MessageFragment.UPDATE_CHAT_HISTORY));
					sendBroadcast(new Intent(MyFriendFragment.UPDATE_CONTACT_HISTORY));
				}
			});
		}

		@Override
		public void onContactInvited(String username, String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			LogUtils.e(getClass(), "请求加你为好友");
			sendBroadcast(new Intent(ContactsFragment.GET_NEW_MSG));

			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
					inviteMessgeDao.deleteMessage(username);
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason("请求添加您为好友");
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);

			sendBroadcast(new Intent(AddFrendActivity.NEW_FRIEND_REQUEST));
		}

		@Override
		public void onContactAgreed(String username) {
			LogUtils.e(getClass(), "同意了你的好友请求");

			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);

			sendBroadcast(new Intent(MyFriendFragment.UPDATE_CONTACT_HISTORY));

		}

		@Override
		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能,demo未实现
			LogUtils.e(getClass(), "拒绝了你的好友请求");
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
		isForeground = true;
		if (!isConflict || !isCurrentAccountRemoved) {
			updateUnreadLabel();
			// updateUnreadAddressLable();
			EMChatManager.getInstance().activityResumed();
		}
	}

	@Override
	protected void onPause() {
		JPushInterface.onPause(this);
		isForeground = false;
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(ExampleApplication.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		ExampleApplication.getInstance().logout(null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Boolean isLoad = false;
						SharedPreferencesUtil.saveBooleanData(MainActivity.this, "isLoad", isLoad);
						sendBroadcast(new Intent(Left.UPDATA_CENTER));
						SharedPreferencesUtil.ClearData(context);
						conflictBuilder = null;
						JPushInterface.stopPush(getApplicationContext());
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
						finish();
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		ExampleApplication.getInstance().logout(null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Boolean isLoad = false;
						SharedPreferencesUtil.saveBooleanData(MainActivity.this, "isLoad", isLoad);
						sendBroadcast(new Intent(Left.UPDATA_CENTER));
						SharedPreferencesUtil.ClearData(context);
						accountRemovedBuilder = null;
						JPushInterface.stopPush(getApplicationContext());

						startActivity(new Intent(MainActivity.this, LoginActivity.class));
						finish();
					}
				});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
			}

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(ExampleApplication.ACCOUNT_REMOVED, false)
				&& !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	private long lastPressTime = 0;

	@Override
	public void onBackPressed() {
		if (MainFragment.current_main == 4) {
			long time = System.currentTimeMillis();
			if (time - lastPressTime < 500) {
				// finish();
				Intent home = new Intent(Intent.ACTION_MAIN);
				home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				home.addCategory(Intent.CATEGORY_HOME);
				startActivity(home);
				slidingPaneLayout.closePane();
			} else {
				// Toast.makeText(this, "再按一次返回键退出程序",
				// Toast.LENGTH_SHORT).show();
				lastPressTime = System.currentTimeMillis();
				sendBroadcast(new Intent(ShopFragment.WEB_GO_BACk));
			}
			// sendBroadcast(new Intent(ShopFragment.WEB_GO_BACk));
		} else {
			// ---------------------------------------------------------------------------------------------------------------------
			Intent intent = new Intent(MainFragment.CHANGETAB);
			intent.putExtra("tab", 2);
			// main_rl.setVisibility(View.GONE);
			sendBroadcast(intent);
			// MainFragment.main_radio.check(4);

		}

	}

	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	private MyContactListener contactListener;

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
			// String messge = intent.getStringExtra(KEY_MESSAGE);
			// String extras = intent.getStringExtra(KEY_EXTRAS);
			// try {
			// StringBuilder showMsg = new StringBuilder();
			// showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
			// if (!ExampleUtil.isEmpty(extras)) {
			// showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
			// LogUtil.i("极光推送内容", showMsg.toString());
			// JSONObject json = new JSONObject(extras);
			// String lesson_id = json.optString("txt");
			// Intent intent1 = new Intent(MainActivity.this,
			// LessonDetailsActivity.class);
			// intent1.putExtra("lesson_id", Integer.parseInt(lesson_id));
			// startActivity(intent1);
			// }
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }
			// }
		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @param usernames
	 */
	@SuppressWarnings("unused")
	private void getHxUserName(final String uns, final EMMessage msg, final String groupId, final String inviter,
			final String st3) {
		new HttpTask<Void, Void, String>(context) {
			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().toGetHxUser(uns);
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					ChatBean chatBean = GsonUtils.json2Bean(result, ChatBean.class);
					if (chatBean != null && chatBean.code == 0) {
						if (chatBean.data.info_list != null && chatBean.data.info_list.size() > 0) {
							msg.setChatType(ChatType.GroupChat);
							msg.setFrom(inviter);
							msg.setTo(groupId);
							msg.setMsgId(UUID.randomUUID().toString());
							msg.addBody(new TextMessageBody(chatBean.data.info_list.get(0).miname + st3));
							// 保存邀请消息
							EMChatManager.getInstance().saveMessage(msg);
							// 提醒新消息
							EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
							runOnUiThread(new Runnable() {
								public void run() {
									updateUnreadLabel();
									sendBroadcast(new Intent(MessageFragment.UPDATE_CHAT_HISTORY));
									sendBroadcast(new Intent(MyFriendFragment.UPDATE_CONTACT_HISTORY));
									// 刷新ui
									// if (currentTabIndex == 0)
									// chatHistoryFragment.refresh();
									// if
									// (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName()))
									// {
									// GroupsActivity.instance.onResume();
									// }
								}
							});
							// HxUserBean userBean=new HxUserBean();
							// userBean.hx_username=uns.substring(2,uns.length()-2);
							// userBean.username=chatBean.data.info_list.get(0).miname;
							// userBean.headpic=chatBean.data.info_list.get(0).miuserheader;
							// HxUserDao.insertHxUser(userBean);
						}
					}
				}
			}

			@Override
			protected void onPreExecute() {
			}
		}.executeProxy();
	}

	// private void initListView() {
	// mDrawerList = (ListView) findViewById(R.id.left_drawer);
	//
	// mPlanetTitles = getResources().getStringArray(R.array.img_src_data);
	//
	// // Set the adapter for the list view
	// mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
	// mPlanetTitles));
	// // Set the list's click listener
	// mDrawerList.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// // Highlight the selected item, update the title, and close the
	// // drawer
	// mDrawerList.setItemChecked(position, true);
	// setTitle(mPlanetTitles[position]);
	// mDrawerLayout.closeDrawer(mDrawerList);
	// }
	// });
	// }

	// @Override
	// protected void onPostCreate(Bundle savedInstanceState) {
	// super.onPostCreate(savedInstanceState);
	// // Sync the toggle state after onRestoreInstanceState has occurred.
	// mDrawerToggle.syncState();
	// }
	//
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// mDrawerToggle.onConfigurationChanged(newConfig);
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Pass the event to ActionBarDrawerToggle, if it returns
	// // true, then it has handled the app icon touch event
	// if (mDrawerToggle.onOptionsItemSelected(item)) {
	// return true;
	// }
	// // Handle your other action bar items...
	//
	// return super.onOptionsItemSelected(item);
	// }

}