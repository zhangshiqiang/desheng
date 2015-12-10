package com.hanyu.desheng.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.adapter.OrderAdapter;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.bean.ShopOrderBean;
import com.hanyu.desheng.bean.ShopOrderBean.Order;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.hanyu.desheng.pulltorefresh.PullToRefreshListView;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyTimeUtils;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

public class PayFragment extends BaseFragment {
	protected static final String tag = "PayFragment";
	@ViewInject(R.id.my_order_ptr)
	private PullToRefreshListView my_order_ptr;
	@ViewInject(R.id.my_order_lv_tv)
	private TextView my_order_lv_tv;
	@ViewInject(R.id.my_order_lv_fl)
	private FrameLayout my_order_lv_fl;
	private OrderAdapter adapter;
	private ShopOrderBean bean;
	private List<Order> beans = new ArrayList<ShopOrderBean.Order>();
	private int page_no = 1;

	@Override
	public void onClick(View v) {

	}

	@Override
	public View initView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.my_order_lv, null);
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		my_order_ptr.setPullLoadEnabled(true);
		my_order_ptr.setPullRefreshEnabled(true);
		my_order_ptr.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				my_order_ptr.setLastUpdatedLabel(MyTimeUtils.getStringDate());
				my_order_ptr.onPullDownRefreshComplete();
				my_order_lv_tv.setVisibility(View.GONE);
				my_order_lv_fl.setVisibility(View.VISIBLE);
				if (bean.data.order_list != null) {
					bean.data.order_list.clear();
					getShopOrder(1, 1);
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				my_order_ptr.onPullUpRefreshComplete();
				page_no++;
				getShopOrder(page_no, 2);
			}
		});
		getShopOrder(1, 1);
	}

	@Override
	public void setListener() {

	}

	/**
	 * 获取商品订单列表
	 */
	private void getShopOrder(final int page_no, final int flag) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String miid_id = SharedPreferencesUtil.getStringData(context,
						"memberid", "");
				String result = EngineManager.getUserEngine().getShopOrder(
						miid_id, "0", page_no);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (code.equals("0")) {
							bean = GsonUtils.json2Bean(result,
									ShopOrderBean.class);
							if (bean.data.order_list.size() > 0) {
								if (adapter == null) {

									adapter = new OrderAdapter(context,
											bean.data.order_list);
									my_order_ptr.getRefreshableView()
											.setAdapter(adapter);
								} else {
									switch (flag) {
									case 1:
										adapter.notifyData(bean.data.order_list);
										break;
									case 2:
										beans.addAll(bean.data.order_list);
										adapter.notifyData2(beans);
										break;

									default:
										break;
									}
								}
							} else {
								my_order_lv_fl.setVisibility(View.GONE);
								my_order_lv_tv.setVisibility(View.VISIBLE);
							}
							LogUtil.i(tag, "获取成功");
						} else {
							String msg = json.getString("msg");
							MyToastUtils.showShortToast(context, msg);
						}
					} catch (JSONException e) {
						my_order_lv_fl.setVisibility(View.GONE);
						MyToastUtils.showShortToast(context, "获取失败");
						e.printStackTrace();
					}
				}
			}

			@Override
			protected void onPreExecute() {
			}

		}.executeProxy();
	}

}
