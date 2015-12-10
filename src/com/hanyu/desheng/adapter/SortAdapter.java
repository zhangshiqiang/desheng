package com.hanyu.desheng.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.ui.RoundedImageView;
import com.hanyu.desheng.ui.silent.handle.ImageLoader;
import com.hanyu.desheng.domain.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 
 * @author
 */
public class SortAdapter extends BaseAdapter implements SectionIndexer {
	private List<User> list = null;
	private Context mContext;
	public ImageLoader imageLoader;
	// 图片缓存 默认 等
	@SuppressWarnings("unused")
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.default_avatar)
			// 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.default_avatar)
			// 设置图片加载/解码过程中错误时候显示的图片
			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	public SortAdapter(Context mContext, List<User> list) {
		this.mContext = mContext;
		this.list = list;
		imageLoader = new ImageLoader(mContext);
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<User> list) {
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

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final User mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.phone_item_adapter, null);
			viewHolder.tvHead = (RoundedImageView) view
					.findViewById(R.id.head_img);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		if (list != null && list.size() > 0) {
			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);

			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				viewHolder.tvLetter.setVisibility(View.VISIBLE);
				viewHolder.tvLetter.setText(mContent.getHeader());
			} else {
				viewHolder.tvLetter.setVisibility(View.GONE);
			}

			// imageLoader.DisplayImage(this.list.get(position).getImgSrc(),
			// viewHolder.tvHead);
			viewHolder.tvTitle.setTextColor(Color.GREEN);
			viewHolder.tvTitle.setText(this.list.get(position).getUsername());
			// viewHolder.tvTitle.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
		}
		return view;

	}

	final static class ViewHolder {
		RoundedImageView tvHead;
		TextView tvLetter;
		TextView tvTitle;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getHeader().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getHeader();
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
}
