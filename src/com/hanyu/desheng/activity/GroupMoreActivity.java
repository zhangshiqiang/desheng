package com.hanyu.desheng.activity;

import java.util.ArrayList;
import java.util.List;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.ChatBean;
import com.hanyu.desheng.bean.HxUserBean;
import com.hanyu.desheng.bean.UserInfo;
import com.hanyu.desheng.db.HxUserDao;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.hanyu.desheng.pulltorefresh.PullToRefreshListView;
import com.hanyu.desheng.ui.CircleImageView;
import com.hanyu.desheng.ui.ClearEditText;
import com.hanyu.desheng.utils.GsonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 群组更多成员
 * 
 * @author wangbin
 * 
 */
public class GroupMoreActivity extends BaseActivity {
	@ViewInject(R.id.rl_back)
	private RelativeLayout rl_back;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.invitefriend_cet)
	private ClearEditText invitefriend_cet;
	@ViewInject(R.id.invitefriend_lv)
	private PullToRefreshListView invitefriend_lv;

	private String groupId;
	List<String> list = new ArrayList<String>();

	private EMGroup group;

	int page = 0;
	int pagesize = 16;
	int pagecount;

	private MyAdapter adapter;

	private List<String> memberList = new ArrayList<String>();

	@Override
	public int setLayout() {
		return R.layout.activity_group_more;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		tv_title.setText("群组成员");
		groupId = getIntent().getStringExtra("groupId");
		group = EMGroupManager.getInstance().getGroup(groupId);

		invitefriend_lv.setPullLoadEnabled(true);
		invitefriend_lv.setPullRefreshEnabled(true);

		invitefriend_lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				invitefriend_lv.onPullDownRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				page++;
				if (page >= pagecount) {
					// 没有数据
					Toast.makeText(GroupMoreActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
					invitefriend_lv.onPullUpRefreshComplete();
					return;
				}
//				setData();
				getUserInfoByPage();
				invitefriend_lv.onPullUpRefreshComplete();
			}
		});
		List<String> llist = group.getMembers();
		if (llist != null && llist.size() > 0) {
			for (int i = 0; i < llist.size(); i++) {
				list.add(llist.get(i));
			}
		}
		if (list != null && list.size() > pagesize) {
			if (list.size() % pagesize == 0) {
				pagecount = list.size() / pagesize;
			} else {
				pagecount = list.size() / pagesize + 1;
			}
		} else {
			invitefriend_lv.setPullLoadEnabled(false);
		}
		
		
		getUserInfoByPage();
		
		
		
		invitefriend_lv.getRefreshableView().setAdapter(adapter);
	}

	private void getUserInfoByPage() {
		if (list != null) {
			List<String> loadingList = null;
			if (list.size() <= pagesize) {
				// 不足16人
				loadingList = list.subList(page * pagesize, list.size());
			} else {
				if (page == pagecount - 1) {
					loadingList = list.subList(page * pagesize, list.size());
				} else {
					try {
						loadingList = list.subList(page * pagesize, (page + 1) * pagesize);
					} catch (Exception e) {
						loadingList = list.subList(page * pagesize, list.size());
					}
				}
			}
			memberList.addAll(loadingList);
			getHxUserName(gson.toJson(loadingList));
			
			setOrNotifyAdapter();
		}
	}

	private void setOrNotifyAdapter() {
		if (adapter == null) {
			adapter = new MyAdapter(memberList);
		}else{
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void setListener() {
		rl_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			finish();
			break;
		}
	}

	class MyAdapter extends BaseAdapter {
		List<String> members;
		
		private MyAdapter(List<String> members) {
			this.members = members;
		}

		@Override
		public int getCount() {
			return members == null ? 0 : members.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(GroupMoreActivity.this, R.layout.phone_contacts_item, null);
				viewHolder = new ViewHolder();
				viewHolder.phone_contacts_iv = (CircleImageView) convertView.findViewById(R.id.phone_contacts_iv);
				viewHolder.phone_contacts_tv2 = (TextView) convertView.findViewById(R.id.phone_contacts_tv2);
				viewHolder.phone_contacts_tv = (TextView) convertView.findViewById(R.id.phone_contacts_tv);
				viewHolder.phone_contacts_btn = (Button) convertView.findViewById(R.id.phone_contacts_btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();

			}

			viewHolder.phone_contacts_tv2.setText("");
			viewHolder.phone_contacts_iv.setImageResource(R.drawable.tx_03);

			viewHolder.phone_contacts_tv.setVisibility(View.GONE);
			viewHolder.phone_contacts_btn.setVisibility(View.GONE);
			if (HxUserDao.findByHx(members.get(position)) != null) {
				HxUserBean userBean = HxUserDao.findByHx(members.get(position));
				if (userBean != null) {
					ImageLoader.getInstance().displayImage(userBean.headpic, viewHolder.phone_contacts_iv, options);
					viewHolder.phone_contacts_tv2.setText(userBean.username);
				}
			}
//			else {
//				final List<String> list = new ArrayList<String>();
//				list.add(members.get(position));
//				getHxUserName(gson.toJson(list), viewHolder.phone_contacts_tv2, viewHolder.phone_contacts_iv);
//			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(GroupMoreActivity.this, PerInfoAct.class);
					// intent.putExtra("chatType",
					// ChatActivity.CHATTYPE_SINGLE);
					intent.putExtra("hxuser", members.get(position));
					intent.putExtra("flag", "1");

					// if (HxUserDao.findByHx(members.get(position)) != null) {
					// intent.putExtra(
					// "username",
					// HxUserDao.findByHx(members.get(position)).username);
					// }
					startActivity(intent);
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				private Dialog dialog;

				@Override
				public boolean onLongClick(View v) {
					if (!EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
						return true;
					}
					if (group.getOwner().equals(members.get(position))) {
						Toast.makeText(GroupMoreActivity.this, "不能删除自己", Toast.LENGTH_SHORT).show();
						return true;
					}

					Builder builder = new AlertDialog.Builder(GroupMoreActivity.this);
					builder.setItems(new String[] { "从本群移除" }, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							String username = members.get(position);
							try {
								EMGroupManager.getInstance().removeUserFromGroup(groupId, username);
								synchronized (object) {
									members.remove(position);
								}
								adapter.notifyDataSetChanged();
								setResult(119);
							} catch (EaseMobException e) {
								e.printStackTrace();
								Toast.makeText(context, "移除失败", Toast.LENGTH_SHORT).show();
							}
						}

					});
					dialog = builder.show();
					return true;
				}
			});

			return convertView;
		}

		class ViewHolder {
			CircleImageView phone_contacts_iv;
			TextView phone_contacts_tv2;
			TextView phone_contacts_tv;
			Button phone_contacts_btn;
		}

	}
	
	private Object object;

	private Gson gson = new Gson();

	// 图片缓存 默认 等
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.default_avatar)
			// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.default_avatar)
			// 设置图片加载/解码过程中错误时候显示的图片
			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	/**
	 * 获取用户信息
	 * 
	 * @param usernames
	 */
	private void getHxUserName(final String uns, final TextView tvName, final CircleImageView cirView) {
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
							if (tvName != null)
								tvName.setText(chatBean.data.info_list.get(0).miname);
							ImageLoader.getInstance().displayImage(chatBean.data.info_list.get(0).miuserheader, cirView,
									options);
							HxUserBean userBean = new HxUserBean();
							userBean.hx_username = uns.substring(2, uns.length() - 2);
							userBean.username = chatBean.data.info_list.get(0).miname;
							userBean.headpic = chatBean.data.info_list.get(0).miuserheader;
							HxUserDao.insertHxUser(userBean);
							adapter.notifyDataSetChanged();
						}
					}
				}
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

	private void getHxUserName(final String uns) {
		new HttpTask<Void, Void, String>(this) {
			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().toGetHxUser(uns);
			}

			@Override
			protected void onPostExecute(String result) {
				Log.e("uns", uns);
				if (result != null) {
					ChatBean chatBean = GsonUtils.json2Bean(result, ChatBean.class);
					if (chatBean != null && chatBean.code == 0) {
						if (chatBean.data.info_list != null && chatBean.data.info_list.size() > 0) {
							for (UserInfo info : chatBean.data.info_list) {
								HxUserBean userBean = new HxUserBean();
								userBean.hx_username = info.hx_name;
								userBean.username = info.miname;
								userBean.headpic = info.miuserheader;
								HxUserDao.insertHxUser(userBean);
							}
							adapter.notifyDataSetChanged();
						}
					} else {
						Toast.makeText(GroupMoreActivity.this, "获取群成员信息失败，正在重新请求", Toast.LENGTH_SHORT).show();
						getHxUserName(uns);
					}
				}
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

}
