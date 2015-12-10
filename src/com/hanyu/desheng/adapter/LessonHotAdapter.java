package com.hanyu.desheng.adapter;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanyu.desheng.R;
import com.hanyu.desheng.bean.Lesson;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class LessonHotAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Lesson> list;
	private Context context;
	// 用来控制CheckBox的选中状况
	private static HashMap<Integer, Boolean> isSelected;

	public LessonHotAdapter() {

	}

	@SuppressLint("UseSparseArrays")
	public LessonHotAdapter(Context context, List<Lesson> hot_lesson_list) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.list = hot_lesson_list;
		isSelected = new HashMap<Integer, Boolean>();
		// 初始化数据
		initDate();
	}

	// 初始化isSelected的数据
	private void initDate() {
		for (int i = 0; i < list.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		LessonHotAdapter.isSelected = isSelected;
	}

	@Override
	public int getCount() {
		return list.size() == 0 ? 1 : list.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewholder;
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = inflater.inflate(R.layout.lesson_item, null);
			viewholder.lesson_listview_tv1 = (TextView) convertView
					.findViewById(R.id.lesson_listview_tv1);
			viewholder.lesson_listview_cb = (CheckBox) convertView
					.findViewById(R.id.lesson_listview_cb);
			viewholder.lesson_listview_rl = (ImageView) convertView
					.findViewById(R.id.lesson_listview_rl);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		viewholder.lesson_listview_tv1.setText(list.get(position).title);
		viewholder.lesson_listview_cb.setText(list.get(position).collect_num
				+ "");
		ImageLoader.getInstance().displayImage(list.get(position).thumb,
				viewholder.lesson_listview_rl);
		if (list.get(position).is_collect == 0) {
			isSelected.put(position, false);
			setIsSelected(isSelected);
		} else {
			isSelected.put(position, true);
			setIsSelected(isSelected);
		}
		// 根据isSelected来设置checkbox的选中状况
		viewholder.lesson_listview_cb.setChecked(getIsSelected().get(position));
		viewholder.lesson_listview_cb
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							viewholder.lesson_listview_cb.setText(list
									.get(position).collect_num + 1 + "");
							getcollect(list.get(position).lesson_id);
						} else {
							viewholder.lesson_listview_cb.setText(list
									.get(position).collect_num + "");
//							cancelcollect(list.get(position).lesson_id);
						}
					}
				});

		return convertView;
	}

	class ViewHolder {
		TextView lesson_listview_tv1;// 课程标题
		CheckBox lesson_listview_cb;// 课程收藏选择框和收藏数量
		ImageView lesson_listview_rl;// 背景图
	}

	/**
	 * 收藏
	 * 
	 * @param lesson_id
	 */
	protected void getcollect(final int lesson_id) {
		new HttpTask<Void, Void, String>(context) {
			@Override
			protected String doInBackground(Void... params) {
				String user_id = SharedPreferencesUtil.getStringData(context,
						"memberid", "");
				String mobile = SharedPreferencesUtil.getStringData(context,
						"miphone", "");
				String result = EngineManager.getUserEngine().Collect(user_id,
						lesson_id, mobile);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					if (result != null) {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (code.equals("0")) {
							MyToastUtils.showShortToast(context, "收藏成功");
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
	 */
//	protected void cancelcollect(final int lesson_id) {
//		new HttpTask<Void, Void, String>(context) {
//			@Override
//			protected String doInBackground(Void... params) {
//				String user_id = SharedPreferencesUtil.getStringData(context,
//						"memberid", "");
//				String mobile = SharedPreferencesUtil.getStringData(context,
//						"miphone", "");
//				String result = EngineManager.getUserEngine().CancelCollect(
//						user_id, lesson_id, mobile);
//				return result;
//			}
//
//			@Override
//			protected void onPostExecute(String result) {
//				try {
//					if (result != null) {
//						JSONObject json = new JSONObject(result);
//						String code = json.getString("code");
//						if (code.equals("0")) {
//							MyToastUtils.showShortToast(context, "取消收藏成功");
//						}
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//
//			protected void onPreExecute() {
//
//			}
//		}.executeProxy();
//	}

}
