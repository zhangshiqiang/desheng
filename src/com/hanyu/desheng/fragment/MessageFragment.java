package com.hanyu.desheng.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.ChatActivity;
import com.hanyu.desheng.activity.NewGroupActivity;
import com.hanyu.desheng.adapter.ChatAllHistoryAdapter;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.db.HxUserDao;
import com.hanyu.desheng.db.InviteMessgeDao;
import com.hanyu.desheng.db.UserDao;
import com.hanyu.desheng.easemob.chatuidemo.Constant;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.hanyu.desheng.pulltorefresh.PullToRefreshListView;
import com.hanyu.desheng.ui.BadgeView;
import com.hanyu.desheng.utils.MyTimeUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MessageFragment extends BaseFragment {
	protected static final String tag = "MessageFragment";
//	@ViewInject(R.id.message_right)
//	private RelativeLayout message_right;

	@ViewInject(R.id.msg_iv)
	private ImageView msg_iv;

	@ViewInject(R.id.message_ptr)
	private PullToRefreshListView message_ptr;

	public RelativeLayout errorItem;
	public TextView errorText;

	// private MessageListPtrAdapter adapter;
	private BadgeView badge;
	@SuppressWarnings("unused")
	private PopupWindow popupWindow;

	// 聊天
	private InputMethodManager inputMethodManager;
	@SuppressWarnings("unused")
	private boolean hidden;
	private ChatAllHistoryAdapter adapter;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();
	UserDao dao;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_right:
			intent = new Intent(context, NewGroupActivity.class);
			startActivity(intent);
//			showPopupWindow(v);
			break;
		case R.id.msg_iv:
			badge.toggle(true);
			break;

		default:
			break;
		}
	}

	@Override
	public View initView(LayoutInflater inflater) {
		view = View.inflate(context, R.layout.message_fragment, null);
		ViewUtils.inject(this, view);
		// badge = new BadgeView(context, msg_iv);
		// badge.setText("84");
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		if (savedInstanceState != null
				&& savedInstanceState.getBoolean("isConflict", false))
			return;
		dao = new UserDao(getActivity());
		inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
		conversationList.addAll(loadConversationsWithRecentChat());
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
		// adapter = new MessageListPtrAdapter(context);
		message_ptr.getRefreshableView().setAdapter(adapter);
		message_ptr.setPullLoadEnabled(false);
		message_ptr.setPullRefreshEnabled(true);
		message_ptr.getRefreshableView().setSelector(R.drawable.item_back);
//		message_ptr.getRefreshableView().setSelector(
//				new ColorDrawable(Color.TRANSPARENT));
		message_ptr.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				message_ptr.setLastUpdatedLabel(MyTimeUtils.getStringDate());
				refresh();
				message_ptr.onPullDownRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				message_ptr.onPullUpRefreshComplete();
			}
		});

		final String st2 = getResources().getString(
				R.string.Cant_chat_with_yourself);
		message_ptr.getRefreshableView().setOnItemClickListener(
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						EMConversation conversation = adapter.getItem(position);
						String username = conversation.getUserName();
						if (username.equals(ExampleApplication.getInstance()
								.getUserName()))
							Toast.makeText(getActivity(), st2, 0).show();
						else {
							// 进入聊天页面
							Intent intent = new Intent(getActivity(),
									ChatActivity.class);
							if (conversation.isGroup()) {
								// it is group chat群聊
								intent.putExtra("chatType",
										ChatActivity.CHATTYPE_GROUP);
								intent.putExtra("groupId", username);
							} else {
								// it is single chat单聊
								intent.putExtra("userId", username);
								intent.putExtra("flag", 1);
								if(HxUserDao.findByHx(username)!=null)
									intent.putExtra("username", HxUserDao.findByHx(username).username);
//								if(dao.getContactList().get(username)!=null)
//								intent.putExtra("username", dao
//										.getContactList().get(username)
//										.getNick());
							}
							startActivity(intent);
						}
					}
				});
		// 注册上下文菜单
		registerForContextMenu(message_ptr.getRefreshableView());

		message_ptr.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				hideSoftKeyboard();
				return false;
			}

		});
//		checkgroup();
	}
	

	/**
	 * 判断该用户是否是店长，是则可发起群聊，否则不可发起群聊
	 */
//	private void checkgroup() {
//		String state = SharedPreferencesUtil.getStringData(context,
//				"shopstate", "");
//		if ("1".equals(state)) {
//			message_right.setVisibility(View.VISIBLE);
//		} else {
//			message_right.setVisibility(View.GONE);
//		}
//	}

	@Override
	public void setListener() {
//		message_right.setOnClickListener(this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
		getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
		// }
	}

//	private void showPopupWindow(View view) {
//		// 一个自定义的布局，作为显示的内容
//		View contentView = LayoutInflater.from(context).inflate(
//				R.layout.message_popup, null);
//		// 设置按钮的点击事件
//		TextView msg_pp_tv = (TextView) contentView
//				.findViewById(R.id.msg_pp_tv);
//
//		msg_pp_tv.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				intent = new Intent(context, NewGroupActivity.class);
//				startActivity(intent);
//				if (popupWindow.isShowing()) {
//					popupWindow.dismiss();
//				}
//			}
//		});
//
//		popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
//				LayoutParams.WRAP_CONTENT, true);
//		popupWindow.setWidth(250);
//		popupWindow.setHeight(100);
//
//		popupWindow.setTouchable(true);
//
//		popupWindow.setTouchInterceptor(new OnTouchListener() {
//
//			// 这里如果返回true的话，touch事件将被拦截
//			// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				return false;
//			}
//		});
//
//		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
//		// 我觉得这里是API的一个bug
//		popupWindow.setBackgroundDrawable(getResources().getDrawable(
//				R.drawable.xx_03));
//		// 设置好参数之后再show
//		popupWindow.showAsDropDown(view);
//	}

	/**
	 * 隐藏软键盘
	 */
	void hideSoftKeyboard() {
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_message) {
			EMConversation tobeDeleteCons = adapter
					.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 删除此会话
			EMChatManager.getInstance().deleteConversation(
					tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup());
			InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
			inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
			adapter.remove(tobeDeleteCons);
			adapter.notifyDataSetChanged();

			// 更新消息未读数
			((MainActivity) getActivity()).updateUnreadLabel();

			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	/**
	 * 获取所有会话
	 * 
	 * @param context
	 * @return +
	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		List<EMConversation> list = new ArrayList<EMConversation>();
		// 过滤掉messages seize为0的conversation
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getAllMessages().size() != 0){
				list.add(conversation);
			}
		}
		// 排序
		sortConversationByLastChatTime(list);
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(
			List<EMConversation> conversationList) {
		Collections.sort(conversationList, new Comparator<EMConversation>() {
			@Override
			public int compare(final EMConversation con1,
					final EMConversation con2) {

				EMMessage con2LastMessage = con2.getLastMessage();
				EMMessage con1LastMessage = con1.getLastMessage();
				if (con2LastMessage.getMsgTime() == con1LastMessage
						.getMsgTime()) {
					return 0;
				} else if (con2LastMessage.getMsgTime() > con1LastMessage
						.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
//		if (!hidden && !((MainActivity) getActivity()).isConflict) {
			refresh();
//		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (((MainActivity) getActivity()).isConflict) {
			outState.putBoolean("isConflict", true);
		} else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
			outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
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
			receiver = new ChatBroadCastReceiver();
		}
		getActivity().registerReceiver(receiver,
				new IntentFilter(UPDATE_CHAT_HISTORY));
	}

	private ChatBroadCastReceiver receiver;

	public static String UPDATE_CHAT_HISTORY = "com.UPDATE_CHAT_HISTORY";

	class ChatBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			refresh();
		}
	}

}
