package com.hanyu.desheng.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.bean.ShopOrderBean.Son.Goods;
import com.hanyu.desheng.ui.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GoodsAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	@SuppressWarnings("unused")
	private Context context;
	private List<Goods> order_goods;

	public GoodsAdapter(Context context, List<Goods> order_goods) {
		inflater = LayoutInflater.from(context);
		this.order_goods = order_goods;
		this.context = context;
	}

	@Override
	public int getCount() {
		return order_goods.size();
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
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.goods_item, null);
			holder.goods_riv = (RoundedImageView) convertView
					.findViewById(R.id.goods_riv);
			holder.goods_tv = (TextView) convertView
					.findViewById(R.id.goods_tv);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.goods_tv.setText(order_goods.get(position).gititle);
		ImageLoader.getInstance().displayImage(
				order_goods.get(position).giimgs, holder.goods_riv);
		return convertView;
	}

	class Holder {
		RoundedImageView goods_riv;// 商品缩略图
		TextView goods_tv;// 商品名称
	}

}
