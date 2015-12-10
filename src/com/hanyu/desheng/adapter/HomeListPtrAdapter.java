package com.hanyu.desheng.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.ChatActivity;
import com.hanyu.desheng.bean.Lesson;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeListPtrAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Lesson> list;
	private Context context;
	private DisplayImageOptions options;
	@SuppressLint("UseSparseArrays")
	private static HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

	public HomeListPtrAdapter() {

	}

	@SuppressWarnings("deprecation")
	@SuppressLint("UseSparseArrays")
	public HomeListPtrAdapter(Context context, List<Lesson> hot_lesson_list) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.list = hot_lesson_list;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.deshenglogo) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.deshenglogo) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.deshenglogo) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.build(); // 创建配置过得DisplayImageOption对象
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = inflater.inflate(R.layout.home_listview, null);
			viewholder.home_listview_tv1 = (TextView) convertView.findViewById(R.id.home_listview_tv1);
			viewholder.home_listview_tv = (TextView) convertView.findViewById(R.id.home_listview_tv);
			viewholder.home_listview_ll = (LinearLayout) convertView.findViewById(R.id.home_listview_ll);
			viewholder.home_listview_rl = (ImageView) convertView.findViewById(R.id.home_listview_rl);
			viewholder.home_listview_iv = (ImageView) convertView.findViewById(R.id.home_listview_iv);
			viewholder.rl_head = (RelativeLayout) convertView.findViewById(R.id.rl_head);

			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		if (list != null && list.size() > 0) {
			if (position == 0) {
				viewholder.home_listview_rl.setBackgroundResource(R.drawable.head_bg3);
				viewholder.home_listview_rl.setImageResource(R.drawable.head_bg3);
				viewholder.rl_head.setVisibility(View.GONE);
			} else {
				viewholder.rl_head.setVisibility(View.VISIBLE);
				viewholder.home_listview_tv1.setText(list.get(position).title);
				viewholder.home_listview_rl.setScaleType(ScaleType.FIT_XY);
				ImageLoader.getInstance().displayImage(list.get(position).thumb, viewholder.home_listview_rl, options);
				int collect = list.get(position).is_collect;
				if (collect == 1) {
					viewholder.home_listview_iv.setBackgroundResource(R.drawable.xun2);
				} else {
					viewholder.home_listview_iv.setBackgroundResource(R.drawable.xun);
				}
				viewholder.home_listview_iv.setTag(position);
				viewholder.home_listview_ll.setTag(position);
				addListener(viewholder);// 添加事件响应
			}
		}
		return convertView;
	}

	class ViewHolder {
		TextView home_listview_tv1;// 课程标题
		TextView home_listview_tv;// 进群提示
		// CheckBox home_listview_cb;// 课程收藏选择框和收藏数量
		ImageView home_listview_rl;// 背景图
		ImageView home_listview_iv;// 心
		LinearLayout home_listview_ll;// 收藏背景范围
		RelativeLayout rl_head;
	}

	/**
	 * 收藏
	 * 
	 * @param lesson_id
	 * @param holder
	 * @param position
	 */
	protected void getcollect(final int lesson_id, final int position, final ViewHolder holder) {
		new HttpTask<Void, Void, String>(context) {
			// private Integer count;

			@Override
			protected String doInBackground(Void... params) {
				// count = Integer.valueOf(holder.home_listview_tv.getText()
				// .toString());
				String user_id = SharedPreferencesUtil.getStringData(context, "memberid", "");
				String mobile = SharedPreferencesUtil.getStringData(context, "miphone", "");
				String result = EngineManager.getUserEngine().Collect(user_id, lesson_id, mobile);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					if (result != null) {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (code.equals("0")) {
							MyToastUtils.showShortToast(context, "已进入该群");
							// map.put(position, count + 1);
							// holder.home_listview_tv.setText("进入该群");
							list.get(position).is_collect = 1;
							holder.home_listview_iv.setBackgroundResource(R.drawable.xun2);
						} else {
							String msg = json.getString("msg");
							MyToastUtils.showShortToast(context, msg);
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

	/**
	 * 取消收藏
	 * 
	 * @param lesson_id
	 * @param holder
	 * @param position
	 */
	// protected void cancelcollect(final int lesson_id, final int position,
	// final ViewHolder holder) {
	// new HttpTask<Void, Void, String>(context) {
	// private Integer count;
	//
	// @Override
	// protected String doInBackground(Void... params) {
	// count = Integer.valueOf(holder.home_listview_tv.getText()
	// .toString());
	// String user_id = SharedPreferencesUtil.getStringData(context,
	// "memberid", "");
	// String mobile = SharedPreferencesUtil.getStringData(context,
	// "miphone", "");
	// String result = EngineManager.getUserEngine().CancelCollect(
	// user_id, lesson_id, mobile);
	// return result;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// try {
	// if (result != null) {
	// JSONObject json = new JSONObject(result);
	// String code = json.getString("code");
	// if (code.equals("0")) {
	// MyToastUtils.showShortToast(context, "已退出该群");
	// map.put(position, count - 1);
	// holder.home_listview_tv.setText(count - 1 + "");
	// list.get(position).is_collect = 0;
	// holder.home_listview_iv
	// .setBackgroundResource(R.drawable.xin_bai);
	// } else {
	// String msg = json.getString("msg");
	// MyToastUtils.showShortToast(context, msg);
	// }
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// protected void onPreExecute() {
	//
	// }
	// }.executeProxy();
	// }

	private void addListener(final ViewHolder holder) {
		holder.home_listview_ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				switch (v.getId()) {
				case R.id.home_listview_ll:
					String str = SharedPreferencesUtil.getStringData(context, "minodename", "");
					if (str.equals(list.get(position).title)) {
						if (list.get(position).is_collect == 0) {
							getcollect(list.get(position).lesson_id, position, holder);
						} else if (list.get(position).is_collect == 1) {
							Intent intent = new Intent(context, ChatActivity.class);
							intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
							intent.putExtra("groupId", list.get(position).hx_group_id);
							context.startActivity(intent);
							// cancelcollect(list.get(position).lesson_id,
							// position,
							// holder);
						}
					} else {
						MyToastUtils.showShortToast(context, "您是" + str + "成员，暂无法进入本部门");
					}
					break;

				default:
					break;
				}
			}
		});

	}
}
