package com.hanyu.desheng.adapter;

import java.io.InputStream;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.LogisticsTrackingActivity;
import com.hanyu.desheng.bean.BaseResult;
import com.hanyu.desheng.bean.ShopOrderBean.Order;
import com.hanyu.desheng.bean.ShopOrderBean.Son;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.ui.MyListView;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.XmlComonUtil;

public class SonOrderAdapter extends BaseAdapter implements OnClickListener {
	private List<Son> son_order;
	private Context context;
	private LayoutInflater inflater;
	private GoodsAdapter adapter;
	private BaseResult baseResult;
	private String vostate;

	public SonOrderAdapter(Context context, List<Son> son_order, String vostate) {
		inflater = LayoutInflater.from(context);
		this.son_order = son_order;
		this.context = context;
		this.vostate = vostate;
	}

	public SonOrderAdapter(Context context, Order order) {

	}

	@Override
	public int getCount() {
		return son_order.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void removeItem(int position) {
		son_order.remove(position);
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.son_order_item, null);
			holder.son_order_no = (TextView) convertView
					.findViewById(R.id.son_order_no);
			holder.son_order_lv = (MyListView) convertView
					.findViewById(R.id.son_order_lv);
			holder.son_order_btn1 = (Button) convertView
					.findViewById(R.id.son_order_btn1);
			holder.son_order_btn2 = (Button) convertView
					.findViewById(R.id.son_order_btn2);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.son_order_no.setText("子单编号：" + son_order.get(position).son_sn);
		if ("-1".equals(vostate) || "2".equals(vostate) || "0".equals(vostate) || "6".equals(vostate) ) {
			holder.son_order_btn1.setVisibility(View.GONE);
			holder.son_order_btn2.setVisibility(View.GONE);
		} else if ("8".equals(vostate)) {
			holder.son_order_btn1.setVisibility(View.VISIBLE);
			holder.son_order_btn2.setVisibility(View.GONE);
			holder.son_order_btn1.setTag(position);
			holder.son_order_btn1.setOnClickListener(this);
		} else if ("4".equals(vostate)) {
			holder.son_order_btn1.setVisibility(View.VISIBLE);
			holder.son_order_btn2.setVisibility(View.VISIBLE);
			holder.son_order_btn1.setTag(position);
			holder.son_order_btn1.setOnClickListener(this);
			holder.son_order_btn2.setTag(position);
			holder.son_order_btn2.setOnClickListener(this);
		}
//		if ("-1".equals(vostate) || "2".equals(vostate) || "0".equals(vostate)) {
//			holder.son_order_btn1.setVisibility(View.GONE);
//			holder.son_order_btn2.setVisibility(View.GONE);
//		} else if ("6".equals(vostate) || "8".equals(vostate)) {
//			holder.son_order_btn1.setVisibility(View.VISIBLE);
//			holder.son_order_btn2.setVisibility(View.GONE);
//			holder.son_order_btn1.setTag(position);
//			holder.son_order_btn1.setOnClickListener(this);
//		} else if ("4".equals(vostate)) {
//			holder.son_order_btn1.setVisibility(View.VISIBLE);
//			holder.son_order_btn2.setVisibility(View.VISIBLE);
//			holder.son_order_btn1.setTag(position);
//			holder.son_order_btn1.setOnClickListener(this);
//			holder.son_order_btn2.setTag(position);
//			holder.son_order_btn2.setOnClickListener(this);
//		}

		adapter = new GoodsAdapter(context, son_order.get(position).order_goods);
		holder.son_order_lv.setSelector(R.color.transparent);
		holder.son_order_lv.setAdapter(adapter);

		return convertView;
	}

	class Holder {
		TextView son_order_no;// 子订单编号
		MyListView son_order_lv;// 子订单商品列表
		Button son_order_btn1;// 查看物流
		Button son_order_btn2;// 确认收货
	}

	private int positon;

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.son_order_btn1:
			positon = (Integer) v.getTag();
			Intent intent = new Intent(context, LogisticsTrackingActivity.class);
			context.startActivity(intent);
			break;
		case R.id.son_order_btn2:
			positon = (Integer) v.getTag();
			new AlertDialog.Builder(context)
					.setTitle("确认")
					.setMessage("确定收货吗？")
					.setPositiveButton(
							"是",
							new android.content.DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String son_sn = son_order.get(positon).son_sn;
									confirmGoods(son_sn, positon);
								}
							})
					.setNegativeButton(
							"否",
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();

			break;

		default:
			break;
		}
	}

	private void confirmGoods(final String son_sn, final int positon) {
		new HttpTask<Void, Void, InputStream>(context) {

			@Override
			protected InputStream doInBackground(Void... params) {
				LogUtil.i("son_sn", son_sn);
				InputStream result = EngineManager.getShopEngine()
						.confirmGoods(son_sn);
				return result;
			}

			@Override
			protected void onPostExecute(InputStream result) {
				try {
					if (result != null) {
						baseResult = new BaseResult();
						XmlComonUtil.streamText2Model(result, baseResult);
						if (baseResult.code.equals("1")) {
							MyToastUtils.showShortToast(context, "确认收货成功");
							removeItem(positon);
						} else {
							MyToastUtils.showShortToast(context, "确认收货失败");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void onPreExecute() {
			}
		}.executeProxy();
	}
}
