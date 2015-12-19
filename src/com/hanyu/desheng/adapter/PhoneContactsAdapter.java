package com.hanyu.desheng.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.util.Base64Encoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.AlertDialog;
import com.hanyu.desheng.activity.InviteFriendActivity;
import com.hanyu.desheng.bean.PhoneModel;
import com.hanyu.desheng.db.PhoneDao;
import com.hanyu.desheng.ui.silent.handle.ImageLoader;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.SharedPreferencesUtil;

/**
 * 手机通讯录
 * 
 * @author
 */
@SuppressLint({ "DefaultLocale", "ShowToast" })
public class PhoneContactsAdapter extends BaseAdapter implements SectionIndexer {
	private List<PhoneModel> list = null;
	private InviteFriendActivity mContext;
	public ImageLoader imageLoader;
	public static HashMap<PhoneModel, Boolean> map_NumberSelected = null;
	// 当前的位置，这个很重要，Button的状态记录就靠它了
	@SuppressWarnings("unused")
	private int currentPosition = 0;
	@SuppressWarnings("unused")
	private Button currentBtn;

	// 这个数组参数是记录每个position上的Button的请求是否成功
	// private boolean[] ORDER_SUCCESS;

	@SuppressLint("UseSparseArrays")
	public PhoneContactsAdapter(InviteFriendActivity mContext, List<PhoneModel> list) {
		this.mContext = mContext;
		this.list = list;
		imageLoader = new ImageLoader(mContext);
		map_NumberSelected = new HashMap<PhoneModel, Boolean>();
		// ORDER_SUCCESS = new boolean[list.size()];//
		// int[listSize.get(0).get(0)];
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<PhoneModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup arg2) {
		final ViewHolder viewHolder;
		final PhoneModel mContent = list.get(position);

		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.phone_contacts_item, null);
			viewHolder.tvHead = (ImageView) view.findViewById(R.id.phone_contacts_iv);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.phone_contacts_tv2);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.phone_contacts_tv);
			viewHolder.phone_contacts_tv3 = (TextView) view.findViewById(R.id.phone_contacts_tv3);
			viewHolder.phone_contacts_tv4 = (TextView) view.findViewById(R.id.phone_contacts_tv4);
			viewHolder.phone_contacts_btn = (Button) view.findViewById(R.id.phone_contacts_btn);
			viewHolder.checked = (CheckBox) view.findViewById(R.id.T_selectAll_item);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		// int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		// if (position == getPositionForSection(section)) {
		// viewHolder.tvLetter.setVisibility(View.VISIBLE);
		// viewHolder.tvLetter.setText(mContent.getSortLetters());
		// } else {
		viewHolder.tvLetter.setVisibility(View.GONE);
		// }

		// imageLoader.DisplayImage(this.list.get(position).getImgSrc(),
		// viewHolder.tvHead);
		final int pos = position;
		try {
			viewHolder.tvTitle.setText(list.get(pos).getName());
			// viewHolder.tvTitle.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线

			if (PhoneDao.findByMobole(list.get(pos).getMobile()) == null) {
				viewHolder.phone_contacts_btn.setText("邀请");
			} else {
				viewHolder.phone_contacts_btn.setText("添加");
			}
		} catch (Exception e) {
			viewHolder.tvTitle.setText("");
			viewHolder.phone_contacts_btn.setVisibility(View.GONE);
		}

		viewHolder.phone_contacts_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (PhoneDao.findByMobole(list.get(pos).getMobile()) == null) {
					getcode(list.get(pos).getMobile());
				} else {
					addContact(PhoneDao.findByMobole(list.get(pos).getMobile()), list.get(pos).getName());
				}

			}
		});

		viewHolder.checked.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (!map_NumberSelected.containsKey(list.get(pos))) {
						map_NumberSelected.put(list.get(pos), isChecked);
					}
				} else {
					map_NumberSelected.remove(list.get(pos));
				}

				// mContext.showDialog();
				LogUtil.i("===", map_NumberSelected.containsKey(list.get(pos)) + "");
			}
		});
		if (map_NumberSelected.containsKey(list.get(pos))) {
			viewHolder.checked.setChecked(true);
		} else {
			viewHolder.checked.setChecked(false);
		}
		// viewHolder.checked.setChecked(map_NumberSelected.get(list.get(pos)));
		// 判断保存的position位置上的Butoon是否已预约成功
		// if (ORDER_SUCCESS[position]) {
		// viewHolder.phone_contacts_btn.setVisibility(View.GONE);
		// viewHolder.phone_contacts_tv4.setVisibility(View.VISIBLE);
		// // 这里不设的话返回来时还会可点击,因为向下拉时，Holder里的btn被设置成可点击了
		// // holder.btn_order.setEnabled(false);
		// // viewHolder.phone_contacts_btn.setClickable(false);
		// } else {
		// //
		// 这里要写上默认的显示内容，要不然后面的子控件会出现显示已预订的情况。因为不设置的话btn还是Holder中的btn，而此时holder中的btn是已经设置了已预约的
		// viewHolder.phone_contacts_btn.setVisibility(View.VISIBLE);
		// viewHolder.phone_contacts_tv4.setVisibility(View.GONE);
		// // 要设置为可点击，不然是Holder里的状态
		// // holder.btn_order.setEnabled(true);
		// // viewHolder.phone_contacts_btn.setClickable(true);
		// // 这个也要放这里判断
		// viewHolder.phone_contacts_btn.setOnClickListener(new btnListener(
		// position, viewHolder.phone_contacts_btn));
		//
		// }
		// sort(list);
		return view;

	}

	@SuppressWarnings("unchecked")
	private void sort(List<PhoneModel> list) {
		Collections.sort(list, new Comparator<PhoneModel>() {

			@Override
			public int compare(PhoneModel lhs, PhoneModel rhs) {
				if (PhoneDao.findByMobole(lhs.getMobile()) == null && PhoneDao.findByMobole(rhs.getMobile()) == null) {
					return 0;
				} else if (PhoneDao.findByMobole(lhs.getMobile()) != null
						&& PhoneDao.findByMobole(rhs.getMobile()) != null) {
					return 2;
				} else {
					return 1;
				}

			}
		});
	}

	public static void setMap_NumberSelected(HashMap<PhoneModel, Boolean> map_NumberSelected) {
		PhoneContactsAdapter.map_NumberSelected = map_NumberSelected;
	}

	public static HashMap<PhoneModel, Boolean> getMap_NumberSelected() {
		return map_NumberSelected;
	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact(final String hx_username, final String username) {
		if (ExampleApplication.getInstance().getUserName().equals(hx_username)) {
			String str = mContext.getResources().getString(R.string.not_add_myself);
			mContext.startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", str));
			return;
		}

		if (ExampleApplication.getInstance().getContactList().containsKey(hx_username)) {
			String strin = mContext.getResources().getString(R.string.This_user_is_already_your_friend);
			mContext.startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", strin));
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(mContext);
		String stri = mContext.getResources().getString(R.string.Is_sending_a_request);
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
					((Activity) mContext).runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = mContext.getResources().getString(R.string.send_successful);
							Toast.makeText(mContext, s1, 1).show();
						}
					});
				} catch (final Exception e) {
					((Activity) mContext).runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = mContext.getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(mContext, s2 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 要传入当前点击的Button，用于设置currentBtn为当前点击的Button，这样点击后的setText就不会错位了。
	 */
	class btnListener implements OnClickListener {
		private int position;
		private Button Btn;

		public btnListener(int position, Button currentBtn) {
			this.position = position;
			this.Btn = currentBtn;
		}

		@Override
		public void onClick(View v) {
			currentPosition = position;
			currentBtn = Btn;
			// Toast.makeText(mContext, "点击第 " + (position + 1) + "个Button", 0)
			// .show();
			// ORDER_SUCCESS[currentPosition] = true;
			notifyDataSetChanged();
		}
	}

	public static class ViewHolder {
		ImageView tvHead;
		TextView tvLetter;
		TextView tvTitle;
		@SuppressWarnings({ "unused" })
		TextView phone_contacts_tv3;
		@SuppressWarnings("unused")
		TextView phone_contacts_tv4;
		Button phone_contacts_btn;
		public CheckBox checked;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	/**
	 * 向好友发短信
	 * 
	 * @param friphone
	 * 
	 * @param phone
	 */
	public void getcode(String friphone) {
		String phone = SharedPreferencesUtil.getStringData(mContext, "miphone", "");
		String mobile = Base64Encoder.getInstance().encode(phone);
		// LogUtil.i(tag, "mobile" + mobile);
		// String uName = SharedPreferencesUtil.getStringData(mContext,
		// "miname", "");
		String url = "大众创业，万众创新：我只推荐 德升DS4567，一个更专业的时装购物平台"
				+ "，链接地址：http://app.4567cn.com/Api/login.php?invite=MTU1MzczMzI5ODE=" + mobile;
		Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
		sendIntent.setData(Uri.parse("smsto:" + friphone));
		sendIntent.putExtra("sms_body", url);
		mContext.startActivity(sendIntent);
		// new HttpTask<Void, Void, String>(mContext) {
		//
		// @Override
		// protected String doInBackground(Void... params) {
		// String mobile = Base64Encoder.getInstance().encode(phone);
		// // LogUtil.i(tag, "mobile" + mobile);
		// String uName = SharedPreferencesUtil.getStringData(mContext,
		// "miname", "");
		// String url = uName
		// + "邀请你加入：http://e.aysfq.cn/Api/login.php?invite="
		// + mobile;
		// String result = EngineManager.getUserEngine().postVerCode(
		// phone, 5, url);
		// return result;
		// }
		//
		// @Override
		// protected void onPostExecute(String result) {
		// try {
		// if (result != null) {
		// JSONObject json = new JSONObject(result);
		// int code = json.getInt("code");// 返回状态
		// if (code == 0) {
		// // authcode = json.getString("msg");
		// MyToastUtils.showShortToast(mContext, "邀请成功，请耐心等待");
		// // LogUtil.i(tag, "获取成功" + authcode);
		// } else {
		// MyToastUtils.showShortToast(mContext, "获取服务器失败");
		// }
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// @Override
		// protected void onPreExecute() {
		// }
		//
		// }.executeProxy();
	}
}
