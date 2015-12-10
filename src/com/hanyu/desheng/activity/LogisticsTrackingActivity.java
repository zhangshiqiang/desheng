package com.hanyu.desheng.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class LogisticsTrackingActivity extends BaseActivity {
	@ViewInject(R.id.logisticstracking_lv)
	private ListView logisticstracking_lv;
	private MyAdapter adapter;

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.logisticstracking;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		context = LogisticsTrackingActivity.this;
		setBack();
		setTopTitle("物流追踪");
		adapter = new MyAdapter();
		logisticstracking_lv.setAdapter(adapter);
	}

	@Override
	public void setListener() {

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(context,
						R.layout.logisticstracking_item, null);
				holder.first_strl = (RelativeLayout) convertView
						.findViewById(R.id.first_strl);
				holder.second_strl = (RelativeLayout) convertView
						.findViewById(R.id.second_strl);
				holder.code_item_title = (TextView) convertView
						.findViewById(R.id.code_item_title);
				holder.code_item_content = (TextView) convertView
						.findViewById(R.id.code_item_content);
				holder.code_item_time = (TextView) convertView
						.findViewById(R.id.code_item_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				holder.first_strl.setVisibility(View.VISIBLE);
				holder.second_strl.setVisibility(View.GONE);
				holder.code_item_title.setTextColor(getResources().getColor(
						R.color.orange4));
				holder.code_item_content.setTextColor(getResources().getColor(
						R.color.orange4));
				holder.code_item_time.setTextColor(getResources().getColor(
						R.color.orange4));
			} else {
				holder.first_strl.setVisibility(View.GONE);
				holder.second_strl.setVisibility(View.VISIBLE);
				holder.code_item_title.setTextColor(getResources().getColor(
						R.color.gray4));
				holder.code_item_content.setTextColor(getResources().getColor(
						R.color.gray4));
				holder.code_item_time.setTextColor(getResources().getColor(
						R.color.gray4));
			}
			return convertView;
		}

		class ViewHolder {
			RelativeLayout first_strl;// 物流列表第一个点
			RelativeLayout second_strl;// 物流列表其他点
			TextView code_item_title;// 物流标题
			TextView code_item_content;// 物流内容
			TextView code_item_time;// 时间
		}

	}

}
