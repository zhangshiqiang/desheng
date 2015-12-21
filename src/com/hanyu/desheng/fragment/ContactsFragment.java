package com.hanyu.desheng.fragment;

import java.util.List;
import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.AddFrendActivity;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.db.InviteMessgeDao;
import com.hanyu.desheng.domain.InviteMessage;
import com.hanyu.desheng.domain.InviteMessage.InviteMesageStatus;
import com.lidroid.xutils.ViewUtils;
import android.annotation.SuppressLint;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

@SuppressLint("DefaultLocale")
public class ContactsFragment extends BaseFragment {
	protected static final String tag = "ContactsFragment";
	private FrameLayout contacts_fl;
	private RelativeLayout contacts_right;// 添加好友
	private RadioGroup rg;

	private FragmentManager fm;
	private MyFriendFragment friendFragment;
	private MyGroupFragment groupFragment;
	private ImageView iv_friend_right;
	private RelativeLayout contacts_rl_back;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.contacts_right:
			iv_friend_right.setBackgroundResource(R.drawable.ss_03);
			intent = new Intent(context, AddFrendActivity.class);
			startActivity(intent);
			break;
		case R.id.contacts_rl_back:
			Intent intent = new Intent(MainFragment.CHANGETAB);
			intent.putExtra("tab", 2);
			// main_rl.setVisibility(View.GONE);
			getActivity().sendBroadcast(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public View initView(LayoutInflater inflater) {
		view = View.inflate(context, R.layout.contacts_fragment, null);
		contacts_fl = MainFragment.contacts_fl;
		contacts_right = MainFragment.contacts_right;
		rg = MainFragment.rg;
		iv_friend_right = MainFragment.iv_friend_right;
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		contacts_rl_back=MainFragment.contacts_rl_back;
		contacts_rl_back.setOnClickListener(this);
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

	@Override
	public void setListener() {
		contacts_right.setOnClickListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (newMsgReceiver != null) {
			context.unregisterReceiver(newMsgReceiver);
		}
	}

	/**
	 * 隐藏所有的页面
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (friendFragment != null) {
			transaction.hide(friendFragment);
		}
		if (groupFragment != null) {
			transaction.hide(groupFragment);
		}
	}
}
