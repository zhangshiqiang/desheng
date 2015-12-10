package com.hanyu.desheng.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.bean.NewsBean.Data.News;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsListPtrAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<News> list;
	private Context context;
	private DisplayImageOptions options;

	public NewsListPtrAdapter() {

	}

	public NewsListPtrAdapter(Context context, List<News> hot_lesson_list) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.list = hot_lesson_list;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.deshenglogo) // 设置图片下载期间显示的图片
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
			convertView = inflater.inflate(R.layout.news_listview, null);
			viewholder.news_listview_tv1 = (TextView) convertView
					.findViewById(R.id.news_listview_tv1);
			viewholder.news_listview_rl = (ImageView) convertView
					.findViewById(R.id.news_listview_rl);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		if (list != null && list.size() > 0) {
			viewholder.news_listview_tv1.setText(list.get(position).title);
			viewholder.news_listview_rl.setScaleType(ScaleType.FIT_XY);
			ImageLoader.getInstance().displayImage(list.get(position).thumb,
					viewholder.news_listview_rl, options);
		}
		return convertView;
	}

	class ViewHolder {
		TextView news_listview_tv1;// 课程标题
		ImageView news_listview_rl;// 背景图
	}
}
