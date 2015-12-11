package com.hanyu.desheng.fragment;

import java.io.InputStream;
import java.util.List;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.Left;
import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.AddFrendActivity;
import com.hanyu.desheng.activity.NewGroupActivity;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.bean.UserInfo;
import com.hanyu.desheng.db.InviteMessgeDao;
import com.hanyu.desheng.db.UserDao;
import com.hanyu.desheng.domain.InviteMessage;
import com.hanyu.desheng.domain.InviteMessage.InviteMesageStatus;
import com.hanyu.desheng.domain.User;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.ui.CircleImageView;
import com.hanyu.desheng.util.ShowDialogUtil;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.XmlComonUtil;
import com.hanyu.desheng.utils.YangUtils;
import com.hanyu.desheng.zxing.CaptureActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.readystatesoftware.viewbadger.BadgeView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainFragment extends BaseFragment {
	public static final String tag = "MainFragment";

	private FragmentManager fm;
	@ViewInject(R.id.layout_content)
	private FrameLayout layout_content;

	// 主页
	private HomeFragment homefragment;
	private ContactsFragment contactsfragment;
	private MessageFragment messagefragment;
	private ShopFragment shopfragment;

	public static int current_main;
	@ViewInject(R.id.main_radio)
	public RadioGroup main_radio;
	@ViewInject(R.id.main_rl)
	private RelativeLayout main_rl;
	@ViewInject(R.id.main_ll)
	private LinearLayout main_ll;
	@ViewInject(R.id.home_btn)
	private Button home_btn;// 显示badgetview的不可见button
	private UserInfo userinfo;
	private BadgeView badge;

	@ViewInject(R.id.btnContact)
	private Button btnContact;
	private BadgeView contarctBadge;
	UserDao dao;
	//标题返回
	@ViewInject(R.id.shop_rl_back)
	public static RelativeLayout rlback;// webview后退键
	@ViewInject(R.id.shop_head_img)
	public static CircleImageView shop_head_img;
	@ViewInject(R.id.contacts_rl_back)
	public static RelativeLayout contacts_rl_back;
	@ViewInject(R.id.home_rl_back)
	public static RelativeLayout home_rl_back;
	@ViewInject(R.id.msg_rl_back)
	public static RelativeLayout msg_rl_back;
	// 标题控件
	@ViewInject(R.id.title_shop)
	private View vtitle_shop;
	@ViewInject(R.id.title_home)
	private View vtitle_home;
	@ViewInject(R.id.title_contacts)
	private View vtitle_contacts;
	@ViewInject(R.id.title_msg)
	private View vtitle_msg;
	@ViewInject(R.id.rb_shopping)
	private RadioButton rb_shops;
	@ViewInject(R.id.shop_tv_right)
	public static RelativeLayout shop_tv_right;
	private PopupWindow popupWindow;
	private LinearLayout groupchat;
	private TextView addfri;

	public static String state;
	public static final int NEW_GROUP = 131;
	protected static final int requestCode = 555;
	private TextView scan;
	@ViewInject(R.id.home_tv_right)
	private RelativeLayout home_tv_right;

	@ViewInject(R.id.message_right)
	public static RelativeLayout message_right;
	@ViewInject(R.id.contacts_right)
	public static RelativeLayout contacts_right;// 添加好友
	@ViewInject(R.id.rg_contact)
	public static RadioGroup rg;
	private MyFriendFragment friendFragment;
	private MyGroupFragment groupFragment;
	@ViewInject(R.id.iv_friend_right)
	public static ImageView iv_friend_right;
	@ViewInject(R.id.contacts_fl)
	public static FrameLayout contacts_fl;

	@Override
	public View initView(LayoutInflater inflater) {
		view = View.inflate(context, R.layout.frag_home, null);
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		dao = new UserDao(getActivity());
		userinfo = new UserInfo();
		badge = new BadgeView(context, home_btn);
		badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		badge.setTextSize(10);
		badge.setTextColor(context.getResources().getColor(R.color.white));
		state = SharedPreferencesUtil.getStringData(context, "shopstate", "");
		contarctBadge = new BadgeView(context, btnContact);
		contarctBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		contarctBadge.setTextSize(10);
		contarctBadge.setTextColor(context.getResources().getColor(R.color.white));

		selectpage();
	}

	/**
	 * 选择页面
	 */
	private void selectpage() {
		main_radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				fm = getChildFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();
				hideFragments(transaction);
				switch (checkedId) {
				case R.id.rb_homepage:
					current_main = 0;
					if (homefragment == null) {
						homefragment = new HomeFragment();
						transaction.add(R.id.layout_content, homefragment);
					} else {
						transaction.show(homefragment);
					}
					vtitle_contacts.setVisibility(View.GONE);
					vtitle_msg.setVisibility(View.GONE);
					vtitle_shop.setVisibility(View.GONE);
					vtitle_home.setVisibility(View.VISIBLE);
					rb_shops.setVisibility(View.VISIBLE);
					break;
				case R.id.rb_contacts:
					contarctBadge.hide();
					current_main = 1;
					if (!YangUtils.isLogin(context)) {
						ShowDialogUtil.showIsLoginDialog(context);
					} else {
						if (contactsfragment == null) {
							contactsfragment = new ContactsFragment();
							transaction.add(R.id.layout_content, contactsfragment);
							// contactss();
						} else {
							transaction.show(contactsfragment);
						}
						vtitle_home.setVisibility(View.GONE);
						vtitle_msg.setVisibility(View.GONE);
						vtitle_shop.setVisibility(View.GONE);
						vtitle_contacts.setVisibility(View.VISIBLE);
						rb_shops.setVisibility(View.VISIBLE);
					}

					break;
				case R.id.rb_message:
					badge.hide();
					// intent = new Intent(MainFragment.CHANGETAB);
					// intent.putExtra("unreadMsgCountTotal", "2");
					// context.sendBroadcast(intent);
					//暂不删除
					current_main = 2;
					if (!YangUtils.isLogin(context)) {
						ShowDialogUtil.showIsLoginDialog(context);
					} else {
						if (messagefragment == null) {
							messagefragment = new MessageFragment();
							transaction.add(R.id.layout_content, messagefragment);
						} else {
							transaction.show(messagefragment);
						}
						checkgroup();
						vtitle_home.setVisibility(View.GONE);
						vtitle_contacts.setVisibility(View.GONE);
						vtitle_shop.setVisibility(View.GONE);
						vtitle_msg.setVisibility(View.VISIBLE);
						rb_shops.setVisibility(View.VISIBLE);
					}
					break;
				case R.id.rb_shopping:
					current_main = 4;
					if (!YangUtils.isLogin(context)) {
						ShowDialogUtil.showIsLoginDialog(context);
					} else {
						if (shopfragment == null) {
							shopfragment = new ShopFragment();
							transaction.add(R.id.layout_content, shopfragment);
						} else {
							transaction.show(shopfragment);
						}
						vtitle_home.setVisibility(View.GONE);
						vtitle_msg.setVisibility(View.GONE);
						vtitle_contacts.setVisibility(View.GONE);
						vtitle_shop.setVisibility(View.VISIBLE);
						rb_shops.setVisibility(View.INVISIBLE);
					}
					break;
				}
				transaction.commit();
			}
		});
		main_radio.check(R.id.rb_shopping);
	}

	public void contactss() {

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				fm = getFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();
				hideFragments(transaction);

				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromInputMethod(group.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(group.getWindowToken(), 0);

				switch (checkedId) {
				case R.id.rb_contact:
					if (friendFragment == null) {
						friendFragment = new MyFriendFragment();
						transaction.add(R.id.contacts_fl, friendFragment);
					} else {
						transaction.show(friendFragment);
					}
					break;
				case R.id.rb_group:
					if (groupFragment == null) {
						groupFragment = new MyGroupFragment();
						transaction.add(R.id.contacts_fl, groupFragment);
					} else {
						transaction.show(groupFragment);
					}
					break;
				}
				transaction.commit();
			}
		});
		rg.check(R.id.rb_contact);

		hasNewMsg();
		newMsgBroad();

	}

	private void hasNewMsg() {
		InviteMessgeDao dao = new InviteMessgeDao(context);
		List<InviteMessage> msgs = dao.getMessagesList();
		boolean hasNewMsgs = false;
		for (InviteMessage msg : msgs) {
			if (msg.getStatus() == InviteMesageStatus.BEAPPLYED || msg.getStatus() == InviteMesageStatus.BEINVITEED) {
				hasNewMsgs = true;
				break;
			}
		}
		if (hasNewMsgs) {
			iv_friend_right.setBackgroundResource(R.drawable.ss_03_new);
		} else {
			iv_friend_right.setBackgroundResource(R.drawable.ss_03);
		}
	}

	public static final String GET_NEW_MSG = "action_new_msg";
	private NewMsgReceiver newMsgReceiver;

	private void newMsgBroad() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GET_NEW_MSG);
		newMsgReceiver = new NewMsgReceiver();
		context.registerReceiver(newMsgReceiver, filter);
	}

	class NewMsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("111", "收到新好友请求。。。");
			iv_friend_right.setBackgroundResource(R.drawable.ss_03_new);
		}

	}

	/**
	 * 隐藏所有的页面
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (homefragment != null) {
			transaction.hide(homefragment);
		}
		if (contactsfragment != null) {
			transaction.hide(contactsfragment);
		}
		if (messagefragment != null) {
			transaction.hide(messagefragment);
		}
		if (shopfragment != null) {
			transaction.hide(shopfragment);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.home_tv_right:
			popupWindow = new PopupWindow(getActivity());
			View view = View.inflate(context, R.layout.home_popup, null);
			popupWindow.setContentView(view);
			popupWindow.setOutsideTouchable(true);
			groupchat = (LinearLayout) view.findViewById(R.id.home_fm_menu_item1);
			addfri = (TextView) view.findViewById(R.id.home_fm_menu_item2);
			scan = (TextView) view.findViewById(R.id.home_fm_menu_item3);
			popupWindow.setWidth(YangUtils.getScreenWidth(getActivity()) / 2 - 80);
			popupWindow.setHeight(YangUtils.getScreenHeight(getActivity()) / 4 - 10);
			popupWindow.showAsDropDown(v);

			groupchat.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if ("1".equals(state)) {
						popupWindow.dismiss();
						intent = new Intent(context, NewGroupActivity.class);
						getActivity().startActivityForResult(intent, NEW_GROUP);
						// context.startActivity(intent);
					} else {
						MyToastUtils.showShortToast(context, "您还不是店主，不能使用该功能");
					}
				}
			});

			addfri.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					intent = new Intent(context, AddFrendActivity.class);
					context.startActivity(intent);
				}
			});
			scan.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
					intent = new Intent(context, CaptureActivity.class);
					getActivity().startActivityForResult(intent, requestCode);
				}
			});

			break;
		case R.id.message_right:
			intent = new Intent(context, NewGroupActivity.class);
			startActivity(intent);
			// showPopupWindow(v);
			break;
		}
	}

	/**
	 * 判断该用户是否是店长，是则可发起群聊，否则不可发起群聊
	 */
	private void checkgroup() {
		String state = SharedPreferencesUtil.getStringData(context, "shopstate", "");
		if ("1".equals(state)) {
			message_right.setVisibility(View.VISIBLE);
		} else {
			message_right.setVisibility(View.GONE);
		}
	}

	@Override
	public void setListener() {
		home_tv_right.setOnClickListener(this);
		message_right.setOnClickListener(this);
	}

	/**
	 * 获取会员信息
	 * 
	 * @param memberid
	 */
	protected void getUserInfo(final String memberid) {
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
						XmlComonUtil.streamText2Model(result, userinfo);
						SharedPreferencesUtil.saveStringData(context, "miname", userinfo.miname);
						LogUtil.i(tag, "会员昵称" + userinfo.miname);
						intent = new Intent(Left.UPDATA_CENTER);
						context.sendBroadcast(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

	@Override
	public void onResume() {
		super.onResume();
		// User user =
		// ExampleApplication.getInstance().getContactList().get(ExampleApplication.NEW_FRIENDS_USERNAME);
		// if (user != null) {
		// if (user.getUnreadMsgCount() > 0) {
		// contarctBadge.setVisibility(View.VISIBLE);
		// contarctBadge.show();
		// if (user.getUnreadMsgCount() < 99) {
		// contarctBadge.setText(user.getUnreadMsgCount() + "");
		// } else if (user.getUnreadMsgCount() > 99) {
		// contarctBadge.setText("99");
		// }
		// } else {
		// contarctBadge.setVisibility(View.GONE);
		// }
		// } else {
		// contarctBadge.setVisibility(View.GONE);
		// }
	}

	@Override
	public void onStart() {
		if (receiver == null) {
			receiver = new MyBroadCast();
		}
		getActivity().registerReceiver(receiver, new IntentFilter(CHANGETAB));
		if (receiver2 == null) {
			receiver2 = new MyBroadCast2();
		}
		getActivity().registerReceiver(receiver2, new IntentFilter(MSG));
		super.onStart();
	}

	@Override
	public void onDestroy() {
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
		}
		if (receiver2 != null) {
			getActivity().unregisterReceiver(receiver2);
		}
		super.onDestroy();
	}

	// 菜单按钮选择进入 商城/商学院/通讯录/消息
	public static String CHANGETAB = "com.hanyu.desheng.mainfragment.changetab";
	private MyBroadCast receiver;

	public class MyBroadCast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			Bundle extras = intent.getExtras();
			int tab = extras.getInt("tab");
			// if (tab == 1) {
			// main_rl.setVisibility(View.GONE);
			// } else if (tab == 2) {
			// main_rl.setVisibility(View.VISIBLE);
			// }
			RadioButton childRb = (RadioButton) main_radio.getChildAt(tab);
			childRb.setChecked(true);
			// main_rl.setVisibility(View.VISIBLE);
		}

	}

	public static String MSG = "com.hanyu.desheng.mainfragment.unreadmsg";
	private MyBroadCast2 receiver2;

	private class MyBroadCast2 extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.i("unreadMsgCountTotal", "广播啊！！！！！！！！！！！");
			Bundle extras = intent.getExtras();
			int unreadMsgCountTotal = extras.getInt("unreadMsgCountTotal");
			if (unreadMsgCountTotal > 0) {
				badge.setVisibility(View.VISIBLE);
				badge.show();
				LogUtil.i("unreadMsgCountTotal", unreadMsgCountTotal + "");
				if (unreadMsgCountTotal < 99) {
					badge.setText(unreadMsgCountTotal + "");
				} else if (unreadMsgCountTotal > 99) {
					badge.setText("99");
				}
			} else {
				badge.setVisibility(View.GONE);
			}
			if (ExampleApplication.getInstance().getContactList() == null) {
				return;
			}

			User user = ExampleApplication.getInstance().getContactList().get(ExampleApplication.NEW_FRIENDS_USERNAME);
			if (user != null) {
				if (user.getUnreadMsgCount() > 0) {
					contarctBadge.setVisibility(View.VISIBLE);
					contarctBadge.show();
					if (user.getUnreadMsgCount() < 99) {
						contarctBadge.setText(user.getUnreadMsgCount() + "");
					} else if (user.getUnreadMsgCount() > 99) {
						contarctBadge.setText("99");
					}
				} else {
					contarctBadge.setVisibility(View.GONE);
				}
			} else {
				contarctBadge.setVisibility(View.GONE);
			}
		}
	}
}