package com.hanyu.desheng.activity;

import android.os.Bundle;
import android.view.View;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;

public class SearchOrderActivity extends BaseActivity {

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.search_order;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("搜索订单");
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub

	}

}
