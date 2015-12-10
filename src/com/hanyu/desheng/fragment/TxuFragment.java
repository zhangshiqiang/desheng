//package com.hanyu.desheng.fragment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ListView;
//
//import com.hanyu.desheng.R;
//import com.hanyu.desheng.activity.OrderDetailsActivity;
//import com.hanyu.desheng.adapter.OrderAdapter;
//import com.hanyu.desheng.base.BaseFragment;
//import com.hanyu.desheng.pulltorefresh.PullToRefreshBase;
//import com.hanyu.desheng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
//import com.hanyu.desheng.pulltorefresh.PullToRefreshListView;
//import com.hanyu.desheng.utils.MyTimeUtils;
//import com.lidroid.xutils.view.annotation.ViewInject;
//
//public class TxuFragment extends BaseFragment {
//	@ViewInject(R.id.my_order_ptr)
//	private PullToRefreshListView my_order_ptr;
//	private OrderAdapter adapter;
//	
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public View initView(LayoutInflater inflater) {
//		View view = inflater.inflate(R.layout.my_order_lv, null);
//		return view;
//	}
//
//	@Override
//	public void initData(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		my_order_ptr.setPullLoadEnabled(true);
//		my_order_ptr.setPullRefreshEnabled(true);
//		my_order_ptr.setOnRefreshListener(new OnRefreshListener<ListView>() {
//			@Override
//			public void onPullDownToRefresh(
//					PullToRefreshBase<ListView> refreshView) {
//				// TODO Auto-generated method stub
//				my_order_ptr.setLastUpdatedLabel(MyTimeUtils.getStringDate());
//				my_order_ptr.onPullDownRefreshComplete();
//			}
//			@Override
//			public void onPullUpToRefresh(
//					PullToRefreshBase<ListView> refreshView) {
//				// TODO Auto-generated method stub
//				my_order_ptr.onPullUpRefreshComplete();
//			}
//		});
//		adapter = new OrderAdapter(context);
//		my_order_ptr.getRefreshableView().setAdapter(adapter);
//		my_order_ptr.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				intent = new Intent(context, OrderDetailsActivity.class);
//				startActivity(intent);
//				
//			}
//		});
//	}
//
//	@Override
//	public void setListener() {
//		// TODO Auto-generated method stub
//
//	}
//
//}
