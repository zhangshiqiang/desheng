/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hanyu.desheng.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.google.gson.Gson;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.HuanXinBaseActivity;
import com.hanyu.desheng.bean.ChatBean;
import com.hanyu.desheng.bean.CheckAdminBean;
import com.hanyu.desheng.bean.HxUserBean;
import com.hanyu.desheng.bean.ShopInfo;
import com.hanyu.desheng.bean.TopUserBean;
import com.hanyu.desheng.db.HxUserDao;
import com.hanyu.desheng.db.UserDao;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.fragment.HotLessonFrg;
import com.hanyu.desheng.ui.CircleImageView;
import com.hanyu.desheng.ui.ExpandGridView;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.XmlComonUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 测试群组id 98550744432509368
 * 
 * @author wangbin
 * 
 */
public class GroupDetailsActivity extends HuanXinBaseActivity implements OnClickListener {
	private static final String TAG = "GroupDetailsActivity";
	private static final int REQUEST_CODE_ADD_USER = 0;
	private static final int REQUEST_CODE_EXIT = 1;
	private static final int REQUEST_CODE_EXIT_DELETE = 2;
	private static final int REQUEST_CODE_CLEAR_ALL_HISTORY = 3;
	private static final int REQUEST_CODE_ADD_TO_BALCKLIST = 4;
	private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;

	String longClickUsername = null;

	private ExpandGridView userGridview;
	private String groupId;
	private ProgressBar loadingPB;
	private Button exitBtn;
	private Button deleteBtn;
	private EMGroup group;
	private GridAdapter adapter;
	@SuppressWarnings("unused")
	private int referenceWidth;
	@SuppressWarnings("unused")
	private int referenceHeight;
	private ProgressDialog progressDialog;
	private FrameLayout my_order_lv_fl;

	private RelativeLayout rl_switch_block_groupmsg;
	/**
	 * 屏蔽群消息imageView
	 */
	private ImageView iv_switch_block_groupmsg;
	/**
	 * 关闭屏蔽群消息imageview
	 */
	private ImageView iv_switch_unblock_groupmsg;

	public static GroupDetailsActivity instance;

	String st = "";
	// 清空所有聊天记录
	private RelativeLayout clearAllHistory;
	private RelativeLayout blacklistLayout;
	private RelativeLayout changeGroupNameLayout;
	private RelativeLayout rlBanned;

	@SuppressWarnings("unused")
	private UserDao dao;

	TextView tvMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_details);

		instance = this;
		st = getResources().getString(R.string.people);
		clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
		tvMore = (TextView) findViewById(R.id.tvMore);
		userGridview = (ExpandGridView) findViewById(R.id.gridview);
		loadingPB = (ProgressBar) findViewById(R.id.progressBar);
		exitBtn = (Button) findViewById(R.id.btn_exit_grp);
		deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);
		blacklistLayout = (RelativeLayout) findViewById(R.id.rl_blacklist);
		changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);
		rlBanned = (RelativeLayout) findViewById(R.id.rlBanned);
		my_order_lv_fl = (FrameLayout) findViewById(R.id.my_order_lv_fl);

		rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);

		iv_switch_block_groupmsg = (ImageView) findViewById(R.id.iv_switch_block_groupmsg);
		iv_switch_unblock_groupmsg = (ImageView) findViewById(R.id.iv_switch_unblock_groupmsg);

		rl_switch_block_groupmsg.setOnClickListener(this);

		Drawable referenceDrawable = getResources().getDrawable(R.drawable.smiley_add_btn);
		referenceWidth = referenceDrawable.getIntrinsicWidth();
		referenceHeight = referenceDrawable.getIntrinsicHeight();
		dao = new UserDao(this);
		// 获取传过来的groupid
		groupId = getIntent().getStringExtra("groupId");
		group = EMGroupManager.getInstance().getGroup(groupId);
		List<String> ms = group.getMembers();
		if (group.getOwner() == null || "".equals(group.getOwner())
				|| !group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
			exitBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.GONE);
			blacklistLayout.setVisibility(View.GONE);
			changeGroupNameLayout.setVisibility(View.GONE);
		}

		updateGroup();

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// List<String> groupMebers = group.getMembers();
		// if (groupMebers != null && groupMebers.size() > 0) {
		// if (groupMebers.size() > 16) {
		// tvMore.setVisibility(View.VISIBLE);
		// adapter = new GridAdapter(GroupDetailsActivity.this,
		// R.layout.grid, groupMebers.subList(0, 16));
		//
		// } else {
		// tvMore.setVisibility(View.GONE);
		// adapter = new GridAdapter(GroupDetailsActivity.this,
		// R.layout.grid, groupMebers);
		// }
		// Message message = handler.obtainMessage();
		// message.obj = groupMebers;
		// handler.sendMessage(message);
		// }
		//
		// }
		// }).start();
		// 如果自己是群主，显示解散按钮
		if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
			exitBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.VISIBLE);
			rl_switch_block_groupmsg.setVisibility(View.GONE);
			rlBanned.setVisibility(View.VISIBLE);
		} else {
			checkAdmin(ExampleApplication.getInstance().getUserName(), groupId);
		}

		/**
		 * 跳转到禁言
		 */
		rlBanned.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GroupDetailsActivity.this, BannedActivity.class);
				intent.putExtra("groupId", groupId);
				startActivity(intent);
			}
		});

		tvMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GroupDetailsActivity.this, GroupMoreActivity.class);
				intent.putExtra("groupId", groupId);
				// startActivity(intent);
				startActivityForResult(intent, MORE_GROUP_MEMBER);
			}
		});
		((TextView) findViewById(R.id.group_name))
				.setText(group.getGroupName() + "(" + group.getAffiliationsCount() + st);

		clearAllHistory.setOnClickListener(this);
		blacklistLayout.setOnClickListener(this);
		changeGroupNameLayout.setOnClickListener(this);

	}

	private static final int MORE_GROUP_MEMBER = 118;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// List<String> membes=(List<String>) msg.obj;
			userGridview.setAdapter(adapter);
			// 保证每次进详情看到的都是最新的group
			// updateGroup();
		}

	};

	// 是否是管理员
	private String have_authority;

	/**
	 * 检查是否有权限
	 * 
	 * @param hx_name
	 * @param hx_group_id
	 */
	private void checkAdmin(final String hx_name, final String hx_group_id) {
		new HttpTask<Void, Void, String>(this) {

			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().checkAdmin(hx_name, hx_group_id);
			}

			@Override
			protected void onPostExecute(String result) {
				LogUtil.i("tag", hx_name + ":" + hx_group_id);
				if (result != null) {
					try {
						JSONObject jobj = new JSONObject(result);
						if ("0".equals(jobj.getString("code"))) {
							CheckAdminBean cb = GsonUtils.json2Bean(result, CheckAdminBean.class);
							have_authority = cb.data.have_authority;
							if ("1".equals(cb.data.have_authority)) {
								rlBanned.setVisibility(View.VISIBLE);
							} else {
								rlBanned.setVisibility(View.GONE);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

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
		new HttpTask<Void, Void, InputStream>(this) {

			@Override
			protected InputStream doInBackground(Void... params) {
				InputStream result = EngineManager.getShopEngine().toGetStoreinfo(vspid);
				return result;
			}

			@SuppressWarnings("unused")
			@Override
			protected void onPostExecute(InputStream result) {
				try {
					if (result != null) {
						ShopInfo shopinfo = new ShopInfo();
						XmlComonUtil.streamText2Model(result, shopinfo);
						String state = shopinfo.vsstate;
						if (!TextUtils.isEmpty(shopinfo.vsphone)) {
							// getTopHxUser(shopinfo.vsphone, hx_group_id)
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

	@SuppressWarnings("unused")
	private void getTopHxUser(final String mobile, final String hx_group_id) {
		new HttpTask<Void, Void, String>(this) {

			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().getTopHxUser(mobile, hx_group_id);
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					TopUserBean tb = GsonUtils.json2Bean(result, TopUserBean.class);

				}
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String st1 = getResources().getString(R.string.being_added);
		String st2 = getResources().getString(R.string.is_quit_the_group_chat);
		String st3 = getResources().getString(R.string.chatting_is_dissolution);
		String st4 = getResources().getString(R.string.are_empty_group_of_news);
		String st5 = getResources().getString(R.string.is_modify_the_group_name);
		final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
		final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);
		String st8 = getResources().getString(R.string.Are_moving_to_blacklist);
		final String st9 = getResources().getString(R.string.failed_to_move_into);

		final String stsuccess = getResources().getString(R.string.Move_into_blacklist_success);
		if (resultCode == RESULT_OK) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(GroupDetailsActivity.this);
				progressDialog.setMessage(st1);
				progressDialog.setCanceledOnTouchOutside(false);
			}
			switch (requestCode) {
			case REQUEST_CODE_ADD_USER:// 添加群成员
				final String[] newmembers = data.getStringArrayExtra("newmembers");
				progressDialog.setMessage(st1);
				progressDialog.show();
				addMembersToGroup(newmembers);
				break;
			case REQUEST_CODE_EXIT: // 退出群
				progressDialog.setMessage(st2);
				progressDialog.show();
				progressDialog.setCancelable(false);
				progressDialog.setCanceledOnTouchOutside(false);
				exitGrop();
				break;
			case REQUEST_CODE_EXIT_DELETE: // 解散群
				progressDialog.setMessage(st3);
				progressDialog.show();
				deleteGrop();
				break;
			case REQUEST_CODE_CLEAR_ALL_HISTORY:
				// 清空此群聊的聊天记录
				progressDialog.setMessage(st4);
				progressDialog.show();
				clearGroupHistory();
				break;

			case REQUEST_CODE_EDIT_GROUPNAME: // 修改群名称
				final String returnData = data.getStringExtra("data");
				if (!TextUtils.isEmpty(returnData)) {
					progressDialog.setMessage(st5);
					progressDialog.show();

					new Thread(new Runnable() {
						public void run() {
							try {
								EMGroupManager.getInstance().changeGroupName(groupId, returnData);
								runOnUiThread(new Runnable() {
									@SuppressLint("ShowToast")
									public void run() {
										((TextView) findViewById(R.id.group_name))
												.setText(returnData + "(" + group.getAffiliationsCount() + st);
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st6, 0).show();
									}
								});

							} catch (EaseMobException e) {
								e.printStackTrace();
								runOnUiThread(new Runnable() {
									@SuppressLint("ShowToast")
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st7, 0).show();
									}
								});
							}
						}
					}).start();
				}
				break;
			case REQUEST_CODE_ADD_TO_BALCKLIST:
				progressDialog.setMessage(st8);
				progressDialog.show();
				new Thread(new Runnable() {
					public void run() {
						try {
							EMGroupManager.getInstance().blockUser(groupId, longClickUsername);
							runOnUiThread(new Runnable() {
								@SuppressLint("ShowToast")
								public void run() {
									adapter.notifyDataSetChanged();
									progressDialog.dismiss();
									Toast.makeText(getApplicationContext(), stsuccess, 0).show();
								}
							});
						} catch (EaseMobException e) {
							runOnUiThread(new Runnable() {
								@SuppressLint("ShowToast")
								public void run() {
									progressDialog.dismiss();
									Toast.makeText(getApplicationContext(), st9, 0).show();
								}
							});
						}
					}
				}).start();

				break;
			default:
				break;
			}
		}

		if (requestCode == MORE_GROUP_MEMBER && resultCode == 119) {
			Log.e("111", "成员改变");
			updateGroup();
		}
	}

	/**
	 * 点击退出群组按钮
	 * 
	 * @param view
	 */
	public void exitGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);

	}

	private Gson gson = new Gson();

	/**
	 * 点击解散群组按钮
	 * 
	 * @param view
	 */
	public void exitDeleteGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast",
				getString(R.string.dissolution_group_hint)), REQUEST_CODE_EXIT_DELETE);

	}

	/**
	 * 清空群聊天记录
	 */
	public void clearGroupHistory() {

		EMChatManager.getInstance().clearConversation(group.getGroupId());
		progressDialog.dismiss();
		// adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));

	}

	/**
	 * 退出群组
	 * 
	 * @param groupId
	 */
	private void exitGrop() {
		cancelcollect(groupId);
		// @SuppressWarnings("unused")
		// String st1 = getResources().getString(
		// R.string.Exit_the_group_chat_failure);
		// new Thread(new Runnable() {
		// public void run() {
		// try {
		// EMGroupManager.getInstance().exitFromGroup(groupId);
		// runOnUiThread(new Runnable() {
		// public void run() {
		// progressDialog.dismiss();
		// setResult(RESULT_OK);
		// finish();
		// if (ChatActivity.activityInstance != null)
		// ChatActivity.activityInstance.finish();
		// // 退群时调取接口退出课程
		// cancelcollect(groupId);
		// }
		// });
		// } catch (final Exception e) {
		// runOnUiThread(new Runnable() {
		// @SuppressLint("ShowToast")
		// public void run() {
		// progressDialog.dismiss();
		// Toast.makeText(getApplicationContext(),
		// "退出群聊失败: " + e.getMessage(), 1).show();
		// }
		// });
		// }
		// }
		// }).start();
	}

	/**
	 * 解散群组
	 * 
	 * @param groupId
	 */
	private void deleteGrop() {
		final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
		new Thread(new Runnable() {
			public void run() {
				try {
					EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							if (ChatActivity.activityInstance != null)
								ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), st5 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 增加群成员
	 * 
	 * @param newmembers
	 */
	private void addMembersToGroup(final String[] newmembers) {
		final String st6 = getResources().getString(R.string.Add_group_members_fail);
		new Thread(new Runnable() {

			public void run() {
				try {
					// 创建者调用add方法
					if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
						EMGroupManager.getInstance().addUsersToGroup(groupId, newmembers);
					} else {
						// 一般成员调用invite方法
						EMGroupManager.getInstance().inviteUser(groupId, newmembers, null);
					}
					runOnUiThread(new Runnable() {
						public void run() {
							// adapter.notifyDataSetChanged();
							notifyGroupList();

							((TextView) findViewById(R.id.group_name))
									.setText(group.getGroupName() + "(" + group.getAffiliationsCount() + st);
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), st6 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	private void notifyGroupList() {
		groupMebers = group.getMembers();
		if (groupMebers.size() > 16) {
			groupMebers = groupMebers.subList(0, 16);
			tvMore.setVisibility(View.VISIBLE);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		String st6 = getResources().getString(R.string.Is_unblock);
		final String st7 = getResources().getString(R.string.remove_group_of);
		switch (v.getId()) {
		case R.id.rl_switch_block_groupmsg: // 屏蔽群组
			if (iv_switch_block_groupmsg.getVisibility() == View.VISIBLE) {
				try {
					EMGroupManager.getInstance().unblockGroupMessage(groupId);
					iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
					iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
					MyToastUtils.showShortToast(GroupDetailsActivity.this, "已取消屏蔽该群");
				} catch (EaseMobException e) {
					e.printStackTrace();
				}
				// System.out.println("change to unblock group msg");
				// if (progressDialog == null) {
				// progressDialog = new ProgressDialog(
				// GroupDetailsActivity.this);
				// progressDialog.setCanceledOnTouchOutside(false);
				// }
				// progressDialog.setMessage(st6);
				// progressDialog.show();
				// new Thread(new Runnable() {
				// public void run() {
				// try {
				// EMGroupManager.getInstance().unblockGroupMessage(
				// groupId);
				// runOnUiThread(new Runnable() {
				// public void run() {
				// iv_switch_block_groupmsg
				// .setVisibility(View.INVISIBLE);
				// iv_switch_unblock_groupmsg
				// .setVisibility(View.VISIBLE);
				// progressDialog.dismiss();
				// }
				// });
				// } catch (Exception e) {
				// e.printStackTrace();
				// runOnUiThread(new Runnable() {
				// @SuppressLint("ShowToast")
				// public void run() {
				// progressDialog.dismiss();
				// Toast.makeText(getApplicationContext(),
				// st7, 1).show();
				// }
				// });
				//
				// }
				// }
				// }).start();

			} else {

				try {
					EMGroupManager.getInstance().blockGroupMessage(groupId);
					iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
					iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
					MyToastUtils.showShortToast(GroupDetailsActivity.this, "已屏蔽该群");
				} catch (EaseMobException e) {
					e.printStackTrace();
				}
				// String st8 = getResources()
				// .getString(R.string.group_is_blocked);
				// final String st9 = getResources().getString(
				// R.string.group_of_shielding);
				// System.out.println("change to block group msg");
				// if (progressDialog == null) {
				// progressDialog = new ProgressDialog(
				// GroupDetailsActivity.this);
				// progressDialog.setCanceledOnTouchOutside(false);
				// }
				// progressDialog.setMessage(st8);
				// progressDialog.show();
				// new Thread(new Runnable() {
				// public void run() {
				// try {
				// EMGroupManager.getInstance().blockGroupMessage(
				// groupId);
				// runOnUiThread(new Runnable() {
				// public void run() {
				// iv_switch_block_groupmsg
				// .setVisibility(View.VISIBLE);
				// iv_switch_unblock_groupmsg
				// .setVisibility(View.INVISIBLE);
				// progressDialog.dismiss();
				// }
				// });
				// } catch (Exception e) {
				// e.printStackTrace();
				// runOnUiThread(new Runnable() {
				// @SuppressLint("ShowToast")
				// public void run() {
				// progressDialog.dismiss();
				// Toast.makeText(getApplicationContext(),
				// st9, 1).show();
				// }
				// });
				// }
				//
				// }
				// }).start();
			}
			break;

		case R.id.clear_all_history: // 清空聊天记录
			String st9 = getResources().getString(R.string.sure_to_empty_this);
			Intent intent = new Intent(GroupDetailsActivity.this, AlertDialog.class);
			intent.putExtra("cancel", true);
			intent.putExtra("titleIsCancel", true);
			intent.putExtra("msg", st9);
			startActivityForResult(intent, REQUEST_CODE_CLEAR_ALL_HISTORY);
			break;

		case R.id.rl_blacklist: // 黑名单列表
			startActivity(
					new Intent(GroupDetailsActivity.this, GroupBlacklistActivity.class).putExtra("groupId", groupId));
			break;

		case R.id.rl_change_group_name:
			startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", group.getGroupName()),
					REQUEST_CODE_EDIT_GROUPNAME);
			break;

		default:
			break;
		}

	}

	// 图片缓存 默认 等
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.default_avatar)
			// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.default_avatar)
			// 设置图片加载/解码过程中错误时候显示的图片
			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	/**
	 * 群组成员gridadapter
	 * 
	 * @author admin_new
	 * 
	 */
	private class GridAdapter extends ArrayAdapter<String> {

		private int res;
		public boolean isInDeleteMode;
		@SuppressWarnings("unused")
		private List<String> objects;

		public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
			res = textViewResourceId;
			isInDeleteMode = false;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(res, null);
				holder.imageView = (CircleImageView) convertView.findViewById(R.id.iv_avatar);
				holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
				holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final LinearLayout button = (LinearLayout) convertView.findViewById(R.id.button_avatar);
			// 最后一个item，减人按钮
			if (position == getCount() - 1) {
				holder.textView.setText("");
				// 设置成删除按钮
				holder.imageView.setImageResource(R.drawable.smiley_minus_btn);
				// button.setCompoundDrawablesWithIntrinsicBounds(0,
				// R.drawable.smiley_minus_btn, 0, 0);
				// 如果不是创建者或者没有相应权限，不提供加减人按钮
				if (!group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())
						&& have_authority != null && !have_authority.equals("1")) {
					// if current user is not group admin, hide add/remove btn
					convertView.setVisibility(View.INVISIBLE);
				} else { // 显示删除按钮
					if (isInDeleteMode) {
						// 正处于删除模式下，隐藏删除按钮
						convertView.setVisibility(View.INVISIBLE);
					} else {
						// 正常模式
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
					}
					final String st10 = getResources().getString(R.string.The_delete_button_is_clicked);
					// button.setOnClickListener(new OnClickListener() {
					// @Override
					// public void onClick(View v) {
					// EMLog.d(TAG, st10);
					// isInDeleteMode = true;
					// notifyDataSetChanged();
					// }
					// });
					holder.imageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, st10);
							isInDeleteMode = true;
							notifyDataSetChanged();
						}
					});
				}
			} else if (position == getCount() - 2) { // 添加群组成员按钮
				holder.textView.setText("");
				holder.imageView.setImageResource(R.drawable.smiley_add_btn);
				// button.setCompoundDrawablesWithIntrinsicBounds(0,
				// R.drawable.smiley_add_btn, 0, 0);
				// 如果不是创建者或者没有相应权限
				// if (!group.isAllowInvites()
				// && !group.getOwner().equals(
				// EMChatManager.getInstance().getCurrentUser())) {
				// // if current user is not group admin, hide add/remove btn
				// convertView.setVisibility(View.INVISIBLE);
				// } else {
				// 不是创建者并且不允许成员邀请，隐藏添加按钮
				if (group != null && !group.isAllowInvites()
						&& !group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())
						&& have_authority!=null && !have_authority.equals("1")) {
					convertView.setVisibility(View.INVISIBLE);
				} else {
					// 正处于删除模式下,隐藏添加按钮
					if (isInDeleteMode) {
						convertView.setVisibility(View.INVISIBLE);
					} else {
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
					}
					final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, st11);
							// 进入选人页面
							startActivityForResult(
									(new Intent(GroupDetailsActivity.this, GroupPickContactsActivity.class)
											.putExtra("groupId", groupId)),
									REQUEST_CODE_ADD_USER);
						}
					});
					holder.imageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, st11);
							// 进入选人页面
							startActivityForResult(
									(new Intent(GroupDetailsActivity.this, GroupPickContactsActivity.class)
											.putExtra("groupId", groupId)),
									REQUEST_CODE_ADD_USER);
						}
					});
				}
			} else { // 普通item，显示群组成员
				// final String username = getItem(position);
				final String username = groupMebers.get(position);
				convertView.setVisibility(View.VISIBLE);
				button.setVisibility(View.VISIBLE);
				// Drawable avatar =
				// getResources().getDrawable(R.drawable.default_avatar);
				// avatar.setBounds(0, 0, referenceWidth, referenceHeight);
				// button.setCompoundDrawables(null, avatar, null, null);
				final List<String> list = new ArrayList<String>();
				list.add(username);
				if (HxUserDao.findByHx(username) != null) {
					holder.textView.setText(HxUserDao.findByHx(username).username);
					ImageLoader.getInstance().displayImage(HxUserDao.findByHx(username).headpic, holder.imageView,
							options);
				} else {
					getHxUserName(gson.toJson(list), holder.textView, holder.imageView);
				}

				holder.imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						Intent intent = new Intent(GroupDetailsActivity.this, PerInfoAct.class);
						// intent.putExtra("chatType",
						// ChatActivity.CHATTYPE_SINGLE);
						Log.e("1111", "username:" + username);
						intent.putExtra("hxuser", username);
						intent.putExtra("flag", "1");
						startActivity(intent);
						// Intent intent=new Intent(GroupDetailsActivity.this
						// ,PerInfoAct.class);
						// intent.putExtra("hxuser",username);

					}
				});

				// if(dao.getContactList().get(username)!=null)
				// holder.textView.setText(dao.getContactList().get(username).getNick());
				// UserUtils.setUserAvatar(getContext(), username,
				// holder.imageView);
				// demo群组成员的头像都用默认头像，需由开发者自己去设置头像
				if (isInDeleteMode) {
					// 如果是删除模式下，显示减人图标
					convertView.findViewById(R.id.badge_delete).setVisibility(View.VISIBLE);
				} else {
					convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
				}
				final String st12 = getResources().getString(R.string.not_delete_myself);
				final String st13 = getResources().getString(R.string.Are_removed);
				final String st14 = getResources().getString(R.string.Delete_failed);
				@SuppressWarnings("unused")
				final String st15 = getResources().getString(R.string.confirm_the_members);
				button.setOnClickListener(new OnClickListener() {
					@SuppressLint("ShowToast")
					@Override
					public void onClick(View v) {
						if (isInDeleteMode) {
							// 如果是删除自己，return
							if (EMChatManager.getInstance().getCurrentUser().equals(username)) {
								startActivity(
										new Intent(GroupDetailsActivity.this, AlertDialog.class).putExtra("msg", st12));
								return;
							}
							if (!NetUtils.hasNetwork(getApplicationContext())) {
								Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), 0)
										.show();
								return;
							}
							EMLog.d("group", "remove user from group:" + username);
							deleteMembersFromGroup(username);
						} else {
							// 正常情况下点击user，可以进入用户详情或者聊天页面等等
							// startActivity(new
							// Intent(GroupDetailsActivity.this,
							// ChatActivity.class).putExtra("userId",
							// user.getUsername()));

						}
					}

					/**
					 * 删除群成员
					 * 
					 * @param username
					 */
					protected void deleteMembersFromGroup(final String username) {
						final ProgressDialog deleteDialog = new ProgressDialog(GroupDetailsActivity.this);
						deleteDialog.setMessage(st13);
						deleteDialog.setCanceledOnTouchOutside(false);
						deleteDialog.show();
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									// 删除被选中的成员
									EMGroupManager.getInstance().removeUserFromGroup(groupId, username);
									isInDeleteMode = false;
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											groupMebers = EMGroupManager.getInstance().getGroup(groupId).getMembers();
											if (groupMebers.size() > 16) {
												groupMebers = groupMebers.subList(0, 16);
												tvMore.setVisibility(View.VISIBLE);
											}

											deleteDialog.dismiss();
											adapter.notifyDataSetChanged();
											((TextView) findViewById(R.id.group_name)).setText(
													group.getGroupName() + "(" + group.getAffiliationsCount() + st);
										}
									});
								} catch (final Exception e) {
									deleteDialog.dismiss();
									runOnUiThread(new Runnable() {
										@SuppressLint("ShowToast")
										public void run() {
											Toast.makeText(getApplicationContext(), st14 + e.getMessage(), 1).show();
										}
									});
								}

							}
						}).start();
					}
				});

				button.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// if(EMChatManager.getInstance().getCurrentUser().equals(username))
						// return true;
						// if
						// (group.getOwner().equals(EMChatManager.getInstance().getCurrentUser()))
						// {
						// Intent intent = new Intent(GroupDetailsActivity.this,
						// AlertDialog.class);
						// intent.putExtra("msg", st15);
						// intent.putExtra("cancel", true);
						// startActivityForResult(intent,
						// REQUEST_CODE_ADD_TO_BALCKLIST);
						// longClickUsername = username;
						// }
						return false;
					}
				});
			}
			return convertView;
		}

		@Override
		public int getCount() {
			// try{
			// return super.getCount() + 2;
			// }catch(Exception e){
			// return super.getCount();
			// }
			return groupMebers.size() + 2;
		}
	}

	private List<String> groupMebers;

	protected void updateGroup() {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMGroup returnGroup = EMGroupManager.getInstance().getGroupFromServer(groupId);
					// 更新本地数据
					EMGroupManager.getInstance().createOrUpdateLocalGroup(returnGroup);
					runOnUiThread(new Runnable() {

						public void run() {
							((TextView) findViewById(R.id.group_name))
									.setText(group.getGroupName() + "(" + group.getAffiliationsCount() + "人)");

							groupMebers = group.getMembers();
							if (groupMebers != null && groupMebers.size() > 0) {
								if (groupMebers.size() > 16) {
									// adapter = new
									// GridAdapter(GroupDetailsActivity.this,
									// R.layout.grid, groupMebers.subList(0,
									// 16));
									groupMebers = groupMebers.subList(0, 16);
									adapter = new GridAdapter(GroupDetailsActivity.this, R.layout.grid, groupMebers);
									tvMore.setVisibility(View.VISIBLE);

								} else {
									tvMore.setVisibility(View.GONE);
									adapter = new GridAdapter(GroupDetailsActivity.this, R.layout.grid, groupMebers);
								}
								Message message = handler.obtainMessage();
								message.obj = groupMebers;
								handler.sendMessage(message);
							}

							loadingPB.setVisibility(View.INVISIBLE);
							// adapter.notifyDataSetChanged();
							if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
								// 显示解散按钮
								exitBtn.setVisibility(View.GONE);
								deleteBtn.setVisibility(View.VISIBLE);
							} else {
								// 显示退出按钮
								exitBtn.setVisibility(View.VISIBLE);
								deleteBtn.setVisibility(View.GONE);

							}

							// update block
							System.out.println("group msg is blocked:" + group.getMsgBlocked());
							if (group.getMsgBlocked()) {
								iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
								iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
							} else {
								iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
								iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
							}
						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							loadingPB.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}).start();
	}

	public void back(View view) {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}

	private static class ViewHolder {
		CircleImageView imageView;
		TextView textView;
		@SuppressWarnings("unused")
		ImageView badgeDeleteView;
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
				Log.e("111", result);
				if (result != null) {
					try {
						ChatBean chatBean = GsonUtils.json2Bean(result, ChatBean.class);
						if (chatBean != null && chatBean.code == 0) {
							if (chatBean.data.info_list != null && chatBean.data.info_list.size() > 0) {
								tvName.setText(chatBean.data.info_list.get(0).miname);
								ImageLoader.getInstance().displayImage(chatBean.data.info_list.get(0).miuserheader,
										cirView, options);
								HxUserBean userBean = new HxUserBean();
								userBean.hx_username = uns.substring(2, uns.length() - 2);
								userBean.username = chatBean.data.info_list.get(0).miname;
								userBean.headpic = chatBean.data.info_list.get(0).miuserheader;
								HxUserDao.insertHxUser(userBean);
							}
						}
					} catch (Exception e) {
					}
				}
				my_order_lv_fl.setVisibility(View.GONE);
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

	private void getHxUserName(final String uns) {

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
					if (chatBean != null && chatBean.code == 0) {
						if (chatBean.data.info_list != null && chatBean.data.info_list.size() > 0) {
							Intent intent = new Intent(GroupDetailsActivity.this, PerInfoAct.class);
							intent.putExtra("phone", chatBean.data.info_list.get(0).mobile);
							startActivity(intent);
						}
					}
				} else {
					MyToastUtils.showShortToast(getApplicationContext(), "网络错误");
				}
				my_order_lv_fl.setVisibility(View.GONE);
			}

			@Override
			protected void onPreExecute() {
				// my_order_lv_fl.setVisibility(View.VISIBLE);
			}

		}.executeProxy();

	}

	/**
	 * 取消收藏
	 * 
	 * @param lesson_id
	 * @param holder
	 * @param position
	 */
	protected void cancelcollect(final String groupId) {
		new HttpTask<Void, Void, String>(GroupDetailsActivity.this) {

			@Override
			protected String doInBackground(Void... params) {
				String user_id = SharedPreferencesUtil.getStringData(GroupDetailsActivity.this, "memberid", "");
				String mobile = SharedPreferencesUtil.getStringData(GroupDetailsActivity.this, "miphone", "");
				String result = EngineManager.getUserEngine().CancelCollect(user_id, groupId, mobile);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					if (result != null) {
						Log.e("111", "exitGroup:" + result);
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (!TextUtils.isEmpty(code)) {

							sendBroadcast(new Intent(HotLessonFrg.UPDATA_LESSONLIST));
							@SuppressWarnings("unused")
							String st1 = getResources().getString(R.string.Exit_the_group_chat_failure);
							new Thread(new Runnable() {
								public void run() {
									try {
										EMGroupManager.getInstance().exitFromGroup(groupId);
										runOnUiThread(new Runnable() {
											public void run() {
												progressDialog.dismiss();
												setResult(RESULT_OK);
												finish();
												if (ChatActivity.activityInstance != null)
													ChatActivity.activityInstance.finish();
												// 退群时调取接口退出课程
												// cancelcollect(groupId);
											}
										});
									} catch (final Exception e) {
										runOnUiThread(new Runnable() {
											@SuppressLint("ShowToast")
											public void run() {
												progressDialog.dismiss();
												Toast.makeText(getApplicationContext(), "退出群聊失败: " + e.getMessage(), 1)
														.show();
											}
										});
									}
								}
							}).start();
						} else {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									progressDialog.dismiss();
									Toast.makeText(getApplicationContext(), "退出群聊失败: ", 1).show();
								}
							});
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			protected void onPreExecute() {

			}
		}.executeProxy();
	}

}
