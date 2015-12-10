package com.hanyu.desheng.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.bean.ShopOrderBean.Order;
import com.hanyu.desheng.ui.MyListView;

public class OrderAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Order> order_list;
	private SonOrderAdapter adapter;
	private Context context;

	public OrderAdapter(Context context, List<Order> order_list) {
		inflater = LayoutInflater.from(context);
		this.order_list = order_list;
		this.context = context;
	}

	public void notifyData(List<Order> order_list2) {
		this.order_list = order_list2;
		notifyDataSetChanged();
	}

	public void notifyData2(List<Order> order_list3) {
		this.order_list = order_list3;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return order_list.size();
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
		ViewHoler viewHoler;
		if (convertView == null) {
			viewHoler = new ViewHoler();
			convertView = inflater.inflate(R.layout.allorder_item, null);
			viewHoler.allorder_item_lv = (MyListView) convertView
					.findViewById(R.id.allorder_item_lv);
			viewHoler.allorder_item_number = (TextView) convertView
					.findViewById(R.id.allorder_item_number);
			viewHoler.allorder_allprice = (TextView) convertView
					.findViewById(R.id.allorder_allprice);
			convertView.setTag(viewHoler);
		} else {
			viewHoler = (ViewHoler) convertView.getTag();
		}
		viewHoler.allorder_item_number.setText("订单编号："
				+ order_list.get(position).vono);
		viewHoler.allorder_allprice.setText(order_list.get(position).vototal);
		adapter = new SonOrderAdapter(context,
				order_list.get(position).son_order,
				order_list.get(position).vostate);
		viewHoler.allorder_item_lv.setAdapter(adapter);
		return convertView;
	}

	class ViewHoler {
		TextView allorder_item_number;
		TextView allorder_allprice;
		MyListView allorder_item_lv;
	}

}
