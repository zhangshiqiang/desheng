package com.hanyu.desheng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class OrderDetailsActivity extends BaseActivity {
	@ViewInject(R.id.orderdetails_btn1)
	private Button orderdetails_btn1;// 物流追踪

	@ViewInject(R.id.orderdetails_btn2)
	private Button orderdetails_btn2;// 确认收货

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.orderdetails_btn1:
			intent = new Intent(OrderDetailsActivity.this, LogisticsTrackingActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.orderdetails;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("订单详情");
	}

	@Override
	public void setListener() {
		orderdetails_btn1.setOnClickListener(this);
		orderdetails_btn2.setOnClickListener(this);
	}

}
