package com.hanyu.desheng.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.google.gson.Gson;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.BannerBean;
import com.hanyu.desheng.bean.ChatBean;
import com.hanyu.desheng.bean.HxUserBean;
import com.hanyu.desheng.db.HxUserDao;
import com.hanyu.desheng.domain.User;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.ui.CircleImageView;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.LogUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 禁言
 * 
 * @author wangbin
 *
 */
public class BannedActivity extends BaseActivity {
	@ViewInject(R.id.tvtitle)
	private TextView tvtitle;
	@ViewInject(R.id.list)
	private ListView listView;
	@ViewInject(R.id.btnSave)
	private Button btnSave;

	/** group中一开始就有的成员 */
	private List<String> exitingMembers;

	String groupId;
	private PickContactAdapter contactAdapter;

	@Override
	public void onClick(View v) {

	}

	// 图片缓存 默认 等
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.default_avatar)
			// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.default_avatar)
			// 设置图片加载/解码过程中错误时候显示的图片
			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	@Override
	public int setLayout() {
		return R.layout.activity_group_pick_contacts;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		tvtitle.setText("禁言");
		btnSave.setVisibility(View.GONE);
		groupId = getIntent().getStringExtra("groupId");

		// 获取此群组的成员列表
		EMGroup group = EMGroupManager.getInstance().getGroup(groupId);
		exitingMembers = group.getMembers();
		// if(EMChatManager.getInstance().getCurrentUser()
		// .equals(group.getOwner())){
		// exitingMembers.remove(group.getOwner());
		// }else{
		// exitingMembers.remove(ExampleApplication.getInstance().getUserName());
		// exitingMembers.remove(group.getOwner());
		// }
		final List<User> alluserList = new ArrayList<User>();
		for (User user : ExampleApplication.getInstance().getContactList().values()) {
			// if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) &
			// !user.getUsername().equals(Constant.GROUP_USERNAME))
			for (int i = 0; i < exitingMembers.size(); i++) {
				if (user.getUsername().equals(exitingMembers.get(i))
						&& !user.getUsername().equals(ExampleApplication.getInstance().getUserName())) {
					alluserList.add(user);
				}
			}

		}
		// 对list进行排序
		Collections.sort(alluserList, new Comparator<User>() {
			@Override
			public int compare(User lhs, User rhs) {
				return (lhs.getUsername().compareTo(rhs.getUsername()));

			}
		});
		// 保存是否被禁言
		isBanned = new String[exitingMembers.size()];
		contactAdapter = new PickContactAdapter(this, exitingMembers);
		listView.setAdapter(contactAdapter);

	}

	@Override
	public void setListener() {

	}

	Gson gson = new Gson();

	private String[] isBanned;

	/**
	 * adapter
	 */
	private class PickContactAdapter extends BaseAdapter {
		private List<String> users;
		private Context context;

		public PickContactAdapter(Context context, List<String> users) {
			this.users = users;
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = View.inflate(context, R.layout.row_contact_with_checkbox, null);
			// if (position > 0) {
			final String username = users.get(position);
			// 选择框checkbox
			final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
			final CircleImageView civ = (CircleImageView) view.findViewById(R.id.avatar);
			final TextView tvName = (TextView) view.findViewById(R.id.name);
			final LinearLayout ll_cb = (LinearLayout) view.findViewById(R.id.ll_cb);
			List<String> list = new ArrayList<String>();
			list.add(username);
			if (HxUserDao.findByHx(username) != null) {
				tvName.setText(HxUserDao.findByHx(username).username);
				ImageLoader.getInstance().displayImage(HxUserDao.findByHx(username).headpic, civ, options);
			} else {
				getHxUserName(gson.toJson(list), tvName, civ);
			}

			if (TextUtils.isEmpty(isBanned[position])) {
				isBanned(username, groupId, checkBox, position);
			} else {
				if ("0".equals(isBanned[position])) {
					checkBox.setChecked(false);
				} else {
					checkBox.setChecked(true);
				}
				isBanned[position] = isBanned[position];
			}

			ll_cb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (checkBox.isChecked()) {
						Log.e("111", "取消禁言");
						checkBox.setChecked(false);
						isBanned[position] = "0";
						toCancelUser(users.get(position), groupId);
					} else {
						Log.e("111", "禁言");
						checkBox.setChecked(true);
						isBanned[position] = "1";
						tobannedUser(users.get(position), groupId);
					}
				}
			});
			// }
			return view;
		}

		@Override
		public int getCount() {
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			return users.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	public static final String USER_BAND_ACTION = "USER_BAND";//禁言
	public static final String USER_UNBAND_ACTION = "USER_UNBAND";//解除禁言

	/**
	 * 发送透传禁言消息 params type:0,禁言，1,解除禁言
	 */
	private void sendBannedMessage(String username, int type) {
		EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);

		// 支持单聊和群聊，默认单聊，如果是群聊添加下面这行
		// cmdMsg.setChatType(ChatType.GroupChat);
		CmdMessageBody cmdBody = null;
		if (type == 0) {
			cmdBody = new CmdMessageBody(USER_BAND_ACTION);
		} else {
			cmdBody = new CmdMessageBody(USER_UNBAND_ACTION);
		}
		cmdMsg.setReceipt(username);
		cmdMsg.addBody(cmdBody);
		EMChatManager.getInstance().sendMessage(cmdMsg, new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.e("band", "发送成功");
			}

			@Override
			public void onProgress(int arg0, String arg1) {

			}

			@Override
			public void onError(int arg0, String arg1) {
				Log.e("band", "发送失败");
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	/**
	 * 获取用户信息
	 * 
	 * @param usernames
	 */
	private void getHxUserName(final String uns, final TextView tvName, final CircleImageView cirView) {
		LogUtil.i("tag", "环信username：" + uns);
		new HttpTask<Void, Void, String>(this) {
			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().toGetHxUser(uns);
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					ChatBean chatBean = GsonUtils.json2Bean(result, ChatBean.class);
					if (chatBean == null) {
						return;
					}
					if (chatBean.code == 0) {
						if (chatBean.data.info_list != null && chatBean.data.info_list.size() > 0) {
							tvName.setText(chatBean.data.info_list.get(0).miname);
							ImageLoader.getInstance().displayImage(chatBean.data.info_list.get(0).miuserheader, cirView,
									options);
							HxUserBean userBean = new HxUserBean();
							userBean.hx_username = uns.substring(2, uns.length() - 2);
							userBean.username = chatBean.data.info_list.get(0).miname;
							userBean.headpic = chatBean.data.info_list.get(0).miuserheader;
							HxUserDao.insertHxUser(userBean);
						}
					}
				}
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

	/**
	 * 对用户禁言
	 * 
	 * @param user_hx_name
	 * @param hx_group_id
	 */
	private void tobannedUser(final String user_hx_name, final String hx_group_id) {
		new HttpTask<Void, Void, String>(this) {

			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().tobannedUser(user_hx_name, hx_group_id);
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					BannerBean bb = GsonUtils.json2Bean(result, BannerBean.class);
					if (!TextUtils.isEmpty(bb.code)) {
						// Toast.makeText(BannedActivity.this,bb.msg,
						// Toast.LENGTH_SHORT).show();
						// 发送禁言消息
						sendBannedMessage(user_hx_name, 0);
					} else {
						Toast.makeText(BannedActivity.this, bb.msg, Toast.LENGTH_SHORT).show();
					}
				}
			}

		}.executeProxy();
	}

	/**
	 * 解除禁言
	 * 
	 * @param user_hx_name
	 * @param hx_group_id
	 */
	private void toCancelUser(final String user_hx_name, final String hx_group_id) {
		new HttpTask<Void, Void, String>(this) {

			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().toCancelUser(user_hx_name, hx_group_id);
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					BannerBean bb = GsonUtils.json2Bean(result, BannerBean.class);
					if (!TextUtils.isEmpty(bb.code)) {
						// Toast.makeText(BannedActivity.this,bb.msg,
						// Toast.LENGTH_SHORT).show();
						// 发送透传消息
						sendBannedMessage(user_hx_name, 1);
					} else {
						Toast.makeText(BannedActivity.this, bb.msg, Toast.LENGTH_SHORT).show();
					}
				}
			}

		}.executeProxy();
	}

	/**
	 * 
	 * @param username
	 * @param groupid
	 */
	private void isBanned(final String username, final String groupid, final CheckBox checkBox, final int position) {
		new HttpTask<Void, Void, String>(BannedActivity.this) {

			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().checkUserChat(username, groupid);
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					try {
						JSONObject jsonObject = new JSONObject(result);
						JSONObject jsonObject2 = jsonObject.getJSONObject("data");
						String isSilence = jsonObject2.getString("is_silence");
						if ("0".equals(isSilence)) {
							checkBox.setChecked(false);
						} else {
							checkBox.setChecked(true);
						}
						isBanned[position] = isSilence;
					} catch (JSONException e) {
						isBanned(username, groupid, checkBox, position);
						e.printStackTrace();
					}
				}
			}

		}.executeProxy();

	}

	public void remove(EMGroup group, List<String> exitingMembers) {
		if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
			exitingMembers.remove(group.getOwner());
		}
		for (int i = 0; i < exitingMembers.size(); i++) {

		}
		for (int i = 0; i < exitingMembers.size(); i++) {
			if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
				exitingMembers.remove(group.getOwner());
			} else {
				exitingMembers.remove(ExampleApplication.getInstance().getUserName());
				exitingMembers.remove(group.getOwner());
			}
		}
	}

	public void back(View view) {
		finish();
	}

	public void jinyan(String username, String groupId) {
		EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);

		// 支持单聊和群聊，默认单聊，如果是群聊添加下面这行
		cmdMsg.setChatType(ChatType.GroupChat);

		String action = "action1";// action可以自定义，在广播接收时可以收到
		CmdMessageBody cmdBody = new CmdMessageBody(action);
		String toUsername = username;// 发送给某个人
		cmdMsg.setReceipt(toUsername);
		cmdMsg.setAttribute("jinyan", "a");// 支持自定义扩展
		cmdMsg.addBody(cmdBody);
		EMChatManager.getInstance().sendMessage(cmdMsg, new EMCallBack() {

			@Override
			public void onError(int arg0, String arg1) {

			}

			@Override
			public void onProgress(int arg0, String arg1) {

			}

			@Override
			public void onSuccess() {

			}
		});

	}
}
