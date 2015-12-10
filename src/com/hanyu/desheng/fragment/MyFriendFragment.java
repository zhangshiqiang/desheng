package com.hanyu.desheng.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.HanziToPinyin;
import com.google.gson.Gson;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.ChatActivity;
import com.hanyu.desheng.activity.LoginActivity;
import com.hanyu.desheng.adapter.ContactAdapter;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.bean.ChatBean;
import com.hanyu.desheng.db.InviteMessgeDao;
import com.hanyu.desheng.db.UserDao;
import com.hanyu.desheng.domain.User;
import com.hanyu.desheng.easemob.chatuidemo.Constant;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.hanyu.desheng.pulltorefresh.PullToRefreshListView;
import com.hanyu.desheng.ui.ClearEditText;
import com.hanyu.desheng.ui.Sidebar;
import com.hanyu.desheng.util.LogUtils;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MyFriendFragment extends BaseFragment {
	@ViewInject(R.id.country_lvcountry)
	private PullToRefreshListView listView;
	@ViewInject(R.id.sidrbar)
	private Sidebar sidebar;
	@ViewInject(R.id.dialog)
	private TextView dialog;
	@ViewInject(R.id.contacts_fm_et)
	private ClearEditText contacts_fm_et;// 搜索框
	// private SortAdapter adapter;
	// private InputMethodManager inputMethodManager;
	private ContactAdapter adapter;

	private List<User> contactList;
	private List<String> blackList;
	@SuppressWarnings("unused")
	private boolean hidden;
	@ViewInject(R.id.tvSearch)
	private TextView tvSearch;
	@ViewInject(R.id.lv_search)
	private ListView lv_search;
	@ViewInject(R.id.tv_hint)
	private TextView tv_hint;

	// private static final String[] DATA = Cheeses.sCheeseStrings;
	// /**
	// * 汉字转换成拼音的类
	// */
	// private CharacterParser characterParser;
	// // private List<PhoneModel> SourceDateList;
	// /**
	// * 根据拼音来排列ListView里面的数据类
	// */
	// private PinyinComparator pinyinComparator;

	@Override
	public void onClick(View v) {

	}

	@Override
	public View initView(LayoutInflater inflater) {
		view = View.inflate(context, R.layout.friend_fm, null);
		ViewUtils.inject(this, view);

		try {
			processContactsAndGroups();
		} catch (EaseMobException e) {
			e.printStackTrace();
		}

		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		listView.setVisibility(View.INVISIBLE);

		// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
		if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
			return;

		sidebar.setListView(listView.getRefreshableView());
		// 黑名单列表
		try {
			blackList = EMContactManager.getInstance().getBlackListUsernames();
		} catch (Exception e) {
			Toast.makeText(context, "登陆失效，请重新登陆", Toast.LENGTH_SHORT).show();
			if (getActivity() != null) {
				Intent intent2 = new Intent(getActivity(), LoginActivity.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getActivity().startActivity(intent2);
				getActivity().finish();
			}
			return;
		}
		contactList = new ArrayList<User>();

		// 获取设置contactlist
		// getContactList(0);

		// getAllContactList();

		// 设置adapter
		// clearListUser(contactList);
		// adapter = new ContactAdapter(getActivity(), R.layout.row_contact,
		// contactList);
		// listView.getRefreshableView().setAdapter(adapter);
		listView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra("userId", adapter.getItem(position).getUsername());
				intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
				intent.putExtra("username", adapter.getItem(position).getNick());
				intent.putExtra("phone", adapter.getItem(position).getMobile());
				intent.putExtra("flag", 3);
				getActivity().startActivity(intent);
			}
		});

		listView.setPullRefreshEnabled(true);
		listView.setPullLoadEnabled(false);
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				SimpleDateFormat fromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				listView.setLastUpdatedLabel(fromat.format(new Date()));
				page = 0;
				try {
					processContactsAndGroups();
				} catch (EaseMobException e) {
					e.printStackTrace();
				}

				listView.onPullDownRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				listView.onPullUpRefreshComplete();

			}
		});
		contacts_fm_et.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// adapter.getFilter().filter(s);
				filterData(s.toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {

			}
		});

		registerForContextMenu(listView.getRefreshableView());
	}

	private List<User> userInfoList = new ArrayList<User>();
	List<String> usernames;

	private void setOrNotifyAdapter() {
		LogUtils.e(getClass(), "size:" + contactList.size());
		if (adapter == null) {
			adapter = new ContactAdapter(getActivity(), R.layout.row_contact, contactList);
			listView.getRefreshableView().setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void setListener() {

	}

	/**
	 * 
	 * @param s
	 */
	private void filterData(String s) {
		getContactList(0);
		clearListUser(contactList);
		List<User> conLists = new ArrayList<User>();
		if (contactList.size() > 0) {
			for (int i = 0; i < contactList.size(); i++) {
				if (contactList.get(i).getNick().startsWith(s)) {
					conLists.add(contactList.get(i));
				}
			}
		}
		contactList.clear();
		contactList.addAll(conLists);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// 长按前两个不弹menu
		// if (((AdapterContextMenuInfo) menuInfo).position > 1) {
		getActivity().getMenuInflater().inflate(R.menu.context_contact_list, menu);
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
			User tobeDeleteUser = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 删除此联系人
			deleteContact(tobeDeleteUser);
			// 删除相关的邀请消息
			InviteMessgeDao dao = new InviteMessgeDao(getActivity());
			dao.deleteMessage(tobeDeleteUser.getUsername());
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 删除联系人
	 * 
	 * @param toDeleteUser
	 */
	public void deleteContact(final User tobeDeleteUser) {
		String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.Delete_failed);
		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
					// 删除db和内存中此用户的数据
					UserDao dao = new UserDao(getActivity());
					dao.deleteContact(tobeDeleteUser.getUsername());
					ExampleApplication.getInstance().getContactList().remove(tobeDeleteUser.getUsername());
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							contactList.remove(tobeDeleteUser);
							// adapter.remove(tobeDeleteUser);
							adapter.notifyDataSetChanged();
						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st2 + e.getMessage(), 1).show();
						}
					});

				}

			}
		}).start();

	}

	/**
	 * 把user移入到黑名单
	 */
	@SuppressWarnings("unused")
	private void moveToBlacklist(final String username) {
		final ProgressDialog pd = new ProgressDialog(getActivity());
		String st1 = getResources().getString(R.string.Is_moved_into_blacklist);
		final String st2 = getResources().getString(R.string.Move_into_blacklist_success);
		final String st3 = getResources().getString(R.string.Move_into_blacklist_failure);
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					// 加入到黑名单
					EMContactManager.getInstance().addUserToBlackList(username, false);
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st2, 0).show();
							refresh();
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st3, 0).show();
						}
					});
				}
			}
		}).start();

	}

	// private void initViews() {
	// sortListView.setOnItemClickListener(new OnItemClickListener() {
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1,
	// int position, long arg3) {
	// Intent intent = new Intent(getActivity(), ChatActivity.class);
	// intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
	// intent.putExtra("userId", "ceshi2");
	// intent.putExtra("username", contactList.get(position)
	// .getUsername());
	// getActivity().startActivity(intent);
	// }
	// });
	// // 实例化汉字转拼音类
	// characterParser = CharacterParser.getInstance();
	//
	// pinyinComparator = new PinyinComparator();
	//
	// sideBar.setTextView(dialog);
	//
	// // 设置右侧触摸监听
	// sideBar.setOnTouchingLetterChangedListener(new
	// OnTouchingLetterChangedListener() {
	//
	// @Override
	// public void onTouchingLetterChanged(String s) {
	// // 该字母首次出现的位置
	// int position = adapter.getPositionForSection(s.charAt(0));
	// if (position != -1) {
	// sortListView.setSelection(position);
	// }
	//
	// }
	// });
	//
	// // SourceDateList = filledData(
	// // getResources().getStringArray(R.array.date), getResources()
	// // .getStringArray(R.array.img_src_data));
	//
	// // 根据a-z进行排序源数据
	// // Collections.sort(SourceDateList, pinyinComparator);
	//
	// sortListView.setAdapter(adapter);
	// }

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	// private List<PhoneModel> filledData(String[] date, String[] imgData) {
	// List<PhoneModel> mSortList = new ArrayList<PhoneModel>();
	//
	// for (int i = 0; i < date.length; i++) {
	// PhoneModel sortModel = new PhoneModel();
	// sortModel.setImgSrc(imgData[i]);
	// sortModel.setName(date[i]);
	// // 汉字转换成拼音
	// String pinyin = characterParser.getSelling(date[i]);
	// String sortString = pinyin.substring(0, 1).toUpperCase();
	//
	// // 正则表达式，判断首字母是否是英文字母
	// if (sortString.matches("[A-Z]")) {
	// sortModel.setSortLetters(sortString.toUpperCase());
	// } else {
	// sortModel.setSortLetters("#");
	// }
	//
	// mSortList.add(sortModel);
	// }
	// return mSortList;
	// }
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	// private void filterData(String filterStr) {
	// List<PhoneModel> filterDateList = new ArrayList<PhoneModel>();
	//
	// if (TextUtils.isEmpty(filterStr)) {
	// filterDateList = SourceDateList;
	// } else {
	// filterDateList.clear();
	// for (PhoneModel sortModel : SourceDateList) {
	// String name = sortModel.getName();
	// if (name.indexOf(filterStr.toString()) != -1
	// || characterParser.getSelling(name).startsWith(
	// filterStr.toString())) {
	// filterDateList.add(sortModel);
	// }
	// }
	// }
	//
	// // 根据a-z进行排序
	// Collections.sort(filterDateList, pinyinComparator);
	// adapter.updateListView(filterDateList);
	// }
	// 刷新ui
	public void refresh() {
		new Thread() {
			public void run() {
				page = 0;
				getContactList(0);
			};
		}.start();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	private int page = 0;

	/**
	 * 获取联系人列表，并过滤掉黑名单和排序
	 */
	// 0,加载首页，1，加载更多
	private void getContactList(int type) {
		if (type == 0) {
			if (contactList != null) {
				contactList.clear();
			}else{
				return;
			}
		}
		String state = SharedPreferencesUtil.getStringData(context, "shopstate", "");
		UserDao userDao = new UserDao(context);
		final Map<String, User> users = userDao.getContactList();
		
		if (users==null || users.isEmpty() && type == 1) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getActivity(), "没有更多好友了", Toast.LENGTH_SHORT).show();
				}
			});
			return;
		}

		Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, User> entry = iterator.next();
//			if (!entry.getKey().equals(ExampleApplication.NEW_FRIENDS_USERNAME)
//					&& !entry.getKey().equals(ExampleApplication.GROUP_USERNAME) && !blackList.contains(entry.getKey()))
//				contactList.add(entry.getValue());
			if (!entry.getKey().equals(ExampleApplication.GROUP_USERNAME) && !blackList.contains(entry.getKey())){
				contactList.add(entry.getValue());
			}
		}
		// 排序
		Collections.sort(contactList, new Comparator<User>() {

			@Override
			public int compare(User lhs, User rhs) {

				return lhs.getHeader().compareToIgnoreCase(rhs.getHeader());
			}
		});

		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// 加入"申请与通知"和"群聊"
					if (users.get(ExampleApplication.GROUP_USERNAME) != null)
						contactList.add(0, users.get(ExampleApplication.GROUP_USERNAME));
					// 把"申请与通知"添加到首位
					if (users.get(ExampleApplication.NEW_FRIENDS_USERNAME) != null)
						contactList.add(0, users.get(ExampleApplication.NEW_FRIENDS_USERNAME));

					setOrNotifyAdapter();
				}
			});
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (((MainActivity) getActivity()).isConflict) {
			outState.putBoolean("isConflict", true);
		} else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
			outState.putBoolean(ExampleApplication.ACCOUNT_REMOVED, true);
		}

	}

	private void clearListUser(List<User> contactList) {
		for (int i = 0; i < contactList.size(); i++) {
			if (Constant.NEW_FRIENDS_USERNAME.equals(contactList.get(i).getUsername())) {
				contactList.remove(i);
			}
		}
		for (int i = 0; i < contactList.size(); i++) {
			if (Constant.GROUP_USERNAME.equals(contactList.get(i).getUsername())) {
				contactList.remove(i);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (receiver == null) {
			receiver = new ContactBroadCastReceiver();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_CONTACT_HISTORY);
		getActivity().registerReceiver(receiver, filter);
	}

	private ContactBroadCastReceiver receiver;

	public static String UPDATE_CONTACT_HISTORY = "com.UPDATE_CONTACT_HISTORY";

	class ContactBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			page = 0;
			// getAllContactList();
			try {
				processContactsAndGroups();
			} catch (EaseMobException e) {
				e.printStackTrace();
			}
		}
	}

	private Gson gson = new Gson();

	/**
	 * 获取成员群组列表
	 * 
	 * @throws EaseMobException
	 */
	private void processContactsAndGroups() throws EaseMobException {
		// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定

		new Thread() {
			@Override
			public void run() {
				try {
					List<String> usernames = EMContactManager.getInstance().getContactUserNames();
					for (String string : usernames) {
						LogUtils.e(getClass(), "hxUsername:"+string);
					}
					getHxUserName(gson.toJson(usernames), usernames);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						processContactsAndGroups();
					} catch (EaseMobException e1) {
						e1.printStackTrace();
					}
				}
			}
		}.start();

		// System.out.println("----------------" + usernames.toString());
		// EMLog.d("roster", "contacts size: " + usernames.size());

	}

	/**
	 * 获取用户信息
	 * 
	 * @param usernames
	 */
	private void getHxUserName(final String uns, final List<String> usernames) {
		new HttpTask<Void, Void, String>(getActivity()) {

			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().toGetHxUser(uns);
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
//					Log.e("result", result);
					final ChatBean chatBean = GsonUtils.json2Bean(result, ChatBean.class);
					if (chatBean != null && chatBean.code == 0) {
						new Thread() {
							@Override
							public void run() {
								Map<String, User> userlist = new HashMap<String, User>();
								if (chatBean.data.info_list != null && chatBean.data.info_list.size() > 0) {
									Log.e("11111", "size=====:"+chatBean.data.info_list.size());
									for (int i = 0; i < chatBean.data.info_list.size(); i++) {
										
										LogUtils.e(getClass(), chatBean.data.info_list.get(i).mobile);
										User user = new User();
										user.setUsername(chatBean.data.info_list.get(i).hx_name);
										user.setAvatar(chatBean.data.info_list.get(i).miuserheader);
										user.setNick(chatBean.data.info_list.get(i).miname);
										user.setMobile(chatBean.data.info_list.get(i).mobile);
										setUserHearder(chatBean.data.info_list.get(i).miname, user);
//										userlist.put(chatBean.data.info_list.get(i).miname, user);
										userlist.put(chatBean.data.info_list.get(i).hx_name, user);
									}
								}
								// 存入db
								UserDao dao = new UserDao(getActivity());
								List<User> users = new ArrayList<User>(userlist.values());
								dao.saveContactList(users);

//								Map<String, User> contactList2 = dao.getContactList();
//								for (User user : contactList2.values()) {
//									Log.e("saveDao", user.getNick());
//								}

								// 获取黑名单列表
								List<String> blackList;
								try {
									blackList = EMContactManager.getInstance().getBlackListUsernamesFromServer();
									// 保存黑名单
									EMContactManager.getInstance().saveBlackList(blackList);

									// 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
									EMGroupManager.getInstance().getGroupsFromServer();
								} catch (EaseMobException e) {
									e.printStackTrace();
								}
								if (getActivity() != null) {
									getActivity().runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											listView.setVisibility(View.VISIBLE);
											tv_hint.setVisibility(View.GONE);
											refresh();
										}
									});
								}
							}
						}.start();
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
}
