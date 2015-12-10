package com.hanyu.desheng.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.bean.PhoneModel;
import com.hanyu.desheng.ui.silent.handle.ImageLoader;

/**
 * 群聊
 * @author
 */
@SuppressLint("DefaultLocale")
public class GroupChatAdapter extends BaseAdapter implements SectionIndexer {
	private List<PhoneModel> list = null;
	private Context mContext;
	public ImageLoader imageLoader;

	@SuppressLint("UseSparseArrays")
	private Map<Integer, Boolean> checkStatusMap = new HashMap<Integer, Boolean>();

	public GroupChatAdapter(Context mContext, List<PhoneModel> list) {
		this.mContext = mContext;
		this.list = list;
		imageLoader = new ImageLoader(mContext);
		int position = 0;
		for (int i = 0; i < list.size(); i++) {
			checkStatusMap.put(position++, false);
		}
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

	public View getView(final int position, View view, ViewGroup arg2) {
		final ViewHolder viewHolder;
		final PhoneModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.startgroupchat_item, null);
			viewHolder.tvHead = (ImageView) view
					.findViewById(R.id.groupchat_img);
			viewHolder.tvTitle = (TextView) view
					.findViewById(R.id.groupchat_title);
			viewHolder.tvLetter = (TextView) view
					.findViewById(R.id.groupchat_tv);
			viewHolder.groupchat_cb = (CheckBox) view
					.findViewById(R.id.groupchat_cb);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}

		imageLoader.DisplayImage(this.list.get(position).getImgSrc(),
				viewHolder.tvHead);

		viewHolder.tvTitle.setText(this.list.get(position).getName());
		// viewHolder.tvTitle.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线

		viewHolder.groupchat_cb
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						isChecked = viewHolder.groupchat_cb.isChecked();
						// save CheckBox status
						checkStatusMap.put(position, isChecked);
					}
				});
		viewHolder.groupchat_cb.setChecked(checkStatusMap.get(position));
		return view;

	}

	final static class ViewHolder {
		ImageView tvHead;
		TextView tvLetter;
		TextView tvTitle;
		CheckBox groupchat_cb;
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
}
