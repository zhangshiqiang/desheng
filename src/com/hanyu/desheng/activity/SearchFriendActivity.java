package com.hanyu.desheng.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.easemob.chat.EMContactManager;
import com.google.gson.Gson;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.ChatBean;
import com.hanyu.desheng.bean.MobileData;
import com.hanyu.desheng.bean.PhoneBean;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.ui.CircleImageView;
import com.hanyu.desheng.ui.ClearEditText;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.MyToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class SearchFriendActivity extends BaseActivity {
	@ViewInject(R.id.search_friend_et)
	private ClearEditText search_friend_et;// 搜索框
	@ViewInject(R.id.search_friend_search)
	private TextView search_friend_search;// 搜索按钮
	@ViewInject(R.id.search_friend_lv)
	private ListView lv_search;// 搜索按钮
	private Gson gson = new Gson();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_friend_search:
			if (!TextUtils.isEmpty(search_friend_et.getText().toString())) {
				List<String> phonelist = new ArrayList<String>();
				phonelist.add(search_friend_et.getText().toString());
				checkPhoneNumber(gson.toJson(phonelist));
			}
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.search_friend;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		context = SearchFriendActivity.this;
		setBack();
		setTopTitle("搜索好友");

		search_friend_et
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEND
								|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

							if (!TextUtils.isEmpty(search_friend_et.getText()
									.toString())) {
								List<String> phonelist = new ArrayList<String>();
								phonelist.add(search_friend_et.getText()
										.toString());
								checkPhoneNumber(gson.toJson(phonelist));
							}

							return true;
						}
						return false;
					}
				});
	}

	@Override
	public void setListener() {
		search_friend_search.setOnClickListener(this);
	}

	/**
	 * 通过手机号查找好友
	 * 
	 * @param mobiles
	 */
	private void checkPhoneNumber(final String mobiles) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().toFilterHXMobile(
						mobiles);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				Log.e("result", result);
				if (result != null) {
					try {
						MobileData md = gson.fromJson(result, MobileData.class);
						if ("0".equals(md.code)) {
							List<PhoneBean> lpb = md.data.hx_mobile;
							if (lpb != null && lpb.size() > 0) {
								MyAdapter myAdapter = new MyAdapter(lpb);
								lv_search.setAdapter(myAdapter);
							}else{
								Toast.makeText(SearchFriendActivity.this, "没有搜索到该用户", Toast.LENGTH_SHORT).show();
							}
						} else {
							String msg = md.msg;
							MyToastUtils.showShortToast(context, msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				super.onPostExecute(result);
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
		}.executeProxy();
	}

	/**
	 * 适配器
	 * 
	 * @author wangbin
	 * 
	 */
	private class MyAdapter extends BaseAdapter {
		List<PhoneBean> list;

		public MyAdapter(List<PhoneBean> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = View.inflate(context, R.layout.phone_contacts_item,
					null);
			TextView tvTitle = (TextView) convertView
					.findViewById(R.id.phone_contacts_tv2);
			CircleImageView tvHead = (CircleImageView) convertView
					.findViewById(R.id.phone_contacts_iv);
			Button phone_contacts_btn = (Button) convertView
					.findViewById(R.id.phone_contacts_btn);
			List<String> chatList = new ArrayList<String>();
			chatList.add(list.get(position).hx_name);
			getHxUserN(gson.toJson(chatList), tvTitle, tvHead);
			phone_contacts_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					addContact(list.get(position).hx_name, "");
				}
			});
			return convertView;
		}
	}

	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.default_avatar)
			// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.default_avatar)
			// 设置图片加载/解码过程中错误时候显示的图片
			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	/**
	 * 获取用户信息
	 * 
	 * @param usernames
	 */
	private void getHxUserN(final String uns, final TextView tvName,
			final ImageView cirView) {
		new HttpTask<Void, Void, String>(context) {
			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().toGetHxUser(uns);
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					ChatBean chatBean = GsonUtils.json2Bean(result,
							ChatBean.class);
					if (chatBean != null && chatBean.code == 0) {
						if (chatBean.data.info_list != null
								&& chatBean.data.info_list.size() > 0) {
							if (tvName != null)
								tvName.setText(chatBean.data.info_list.get(0).miname);
							ImageLoader
									.getInstance()
									.displayImage(
											chatBean.data.info_list.get(0).miuserheader,
											cirView, options);
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

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact(final String hx_username, final String username) {
		if (ExampleApplication.getInstance().getUserName().equals(hx_username)) {
			String str = getResources().getString(R.string.not_add_myself);
			startActivity(new Intent(context, AlertDialog.class).putExtra(
					"msg", str));
			return;
		}

		if (ExampleApplication.getInstance().getContactList()
				.containsKey(hx_username)) {
			String strin = getResources().getString(
					R.string.This_user_is_already_your_friend);
			startActivity(new Intent(context, AlertDialog.class).putExtra(
					"msg", strin));
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
					// demo写死了个reason，实际应该让用户手动填入
					// String s =
					// mContext.getResources().getString(R.string.Add_a_friend);
					String s = username + "请求加你为好友";
					EMContactManager.getInstance().addContact(hx_username, s);
					runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(
									R.string.send_successful);
							Toast.makeText(context, s1, 1).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(
									R.string.Request_add_buddy_failure);
							Toast.makeText(context, s2 + e.getMessage(), 1)
									.show();
						}
					});
				}
			}
		}).start();
	}

}
