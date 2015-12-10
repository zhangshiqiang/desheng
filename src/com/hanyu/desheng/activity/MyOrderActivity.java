package com.hanyu.desheng.activity;

import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.fragment.AllOrderFragment;
import com.hanyu.desheng.fragment.FinishFragment;
import com.hanyu.desheng.fragment.GetFragment;
import com.hanyu.desheng.fragment.MainFragment;
import com.hanyu.desheng.fragment.PayFragment;
import com.hanyu.desheng.fragment.SendFragment;
import com.hanyu.desheng.fragment.ShopFragment;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;

//import com.viewpagerindicator.TabPageIndicator;

public class MyOrderActivity extends BaseActivity {
	public static final String tag = "MyOrderActivity";
	@ViewInject(R.id.myorder_content)
	private FrameLayout myorder_content;
	@ViewInject(R.id.myorder_rg)
	private RadioGroup myorder_rg; // 按钮组
	private FragmentManager fm;
	private AllOrderFragment allorderfragment;
	private PayFragment payfragment;
	private SendFragment sendfragment;
	private GetFragment getfragment;
	private FinishFragment finishFragment;

	// private GirlFragment girlfragment;
	// private BoyFragment boyfragment;
	// private TxuFragment txufragment;
	// private SuitFragment suitfragment;
	// private ShirtFragment shirtfragment;
	// private Boolean isFirst = true;
	// private FragmentPagerAdapter mAdapter;
	// private List<Fragment> mFragments = new ArrayList<Fragment>();
	// private int currentIndex;

	// @ViewInject(R.id.my_order_sll)
	// private LinearLayout my_order_sll;
	// @ViewInject(R.id.my_order_gll)
	// private LinearLayout my_order_gll;
	// @ViewInject(R.id.my_order_gll2)
	// private LinearLayout my_order_gll2;
	// @ViewInject(R.id.indicator)
	// private UnderlinePageIndicator indicator;
	// @ViewInject(R.id.pager)
	// private ViewPager mViewPager;
	// @ViewInject(R.id.my_order_tv1)
	// private TextView my_order_tv1;
	// @ViewInject(R.id.my_order_tv2)
	// private TextView my_order_tv2;
	// @ViewInject(R.id.my_order_tv3)
	// private TextView my_order_tv3;
	// @ViewInject(R.id.my_order_tv4)
	// private TextView my_order_tv4;
	// @ViewInject(R.id.my_order_tv5)
	// private TextView my_order_tv5;

	@Override
	public int setLayout() {
		return R.layout.my_order;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		BaseActivity.isback_shop=true;
		setBack();
		setTopTitle("德升4567-我的订单");
		pageselect();
		// filter();
		// setRightIv2(R.drawable.sh_03, new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// intent = new Intent(MyOrderActivity.this,
		// SearchOrderActivity.class);
		// startActivity(intent);
		// }
		// });
		// TODO 搜索订单
		// setRightIv(R.drawable.sh_03, new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// intent = new Intent(MyOrderActivity.this,
		// SearchOrderActivity.class);
		// startActivity(intent);
		// }
		// });
		// setRightIv(R.drawable.sh_05, new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (isFirst) {
		// my_order_sll.setVisibility(View.GONE);
		// my_order_gll.setVisibility(View.VISIBLE);
		// my_order_gll2.setVisibility(View.VISIBLE);
		// isFirst = false;
		// } else if (!isFirst) {
		// my_order_sll.setVisibility(View.VISIBLE);
		// my_order_gll.setVisibility(View.GONE);
		// my_order_gll2.setVisibility(View.GONE);
		// isFirst = true;
		// }
		// }
		// });
	}

	/**
	 * 返回键
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(MainFragment.CHANGETAB);
		intent.putExtra("tab", 2);
		sendBroadcast(intent);
		finish();
	}

	// private void filter() {
	// girlfragment = new GirlFragment();
	// boyfragment = new BoyFragment();
	// txufragment = new TxuFragment();
	// suitfragment = new SuitFragment();
	// shirtfragment = new ShirtFragment();
	// mFragments.add(girlfragment);
	// mFragments.add(boyfragment);
	// mFragments.add(txufragment);
	// mFragments.add(suitfragment);
	// mFragments.add(shirtfragment);
	//
	// mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
	// @Override
	// public int getCount() {
	// return mFragments.size();
	// }
	//
	// @Override
	// public Fragment getItem(int arg0) {
	// return mFragments.get(arg0);
	// }
	// };
	// mViewPager.setAdapter(mAdapter);
	//
	// indicator.setViewPager(mViewPager);
	// indicator.setFades(false);
	// indicator.setOnPageChangeListener(new OnPageChangeListener() {
	//
	// @Override
	// public void onPageSelected(int arg0) {
	// resetTabBtn();
	// switch (arg0) {
	// case 0:
	// my_order_tv1.setPressed(true);
	// break;
	// case 1:
	// my_order_tv2.setPressed(true);
	// break;
	// case 2:
	// my_order_tv3.setPressed(true);
	// break;
	// case 3:
	// my_order_tv4.setPressed(true);
	// break;
	// case 4:
	// my_order_tv5.setPressed(true);
	// break;
	//
	// default:
	// break;
	// }
	// currentIndex = arg0;
	// }
	//
	// @Override
	// public void onPageScrolled(int arg0, float arg1, int arg2) {
	//
	// }
	//
	// @Override
	// public void onPageScrollStateChanged(int arg0) {
	//
	// }
	// });
	// }

	// protected void resetTabBtn() {
	// my_order_tv1.setPressed(false);
	// my_order_tv2.setPressed(false);
	// my_order_tv3.setPressed(false);
	// my_order_tv4.setPressed(false);
	// my_order_tv5.setPressed(false);
	// }

	@Override
	public void onClick(View v) {
		// switch (v.getId()) {
		// case R.id.my_order_tv1:
		// mViewPager.setCurrentItem(0);
		// break;
		// case R.id.my_order_tv2:
		// mViewPager.setCurrentItem(1);
		// break;
		// case R.id.my_order_tv3:
		// mViewPager.setCurrentItem(2);
		// break;
		// case R.id.my_order_tv4:
		// mViewPager.setCurrentItem(3);
		// break;
		// case R.id.my_order_tv5:
		// mViewPager.setCurrentItem(4);
		// break;
		//
		// default:
		// break;
		// }
	}

	@Override
	public void setListener() {
		// my_order_tv1.setOnClickListener(this);
		// my_order_tv2.setOnClickListener(this);
		// my_order_tv3.setOnClickListener(this);
		// my_order_tv4.setOnClickListener(this);
		// my_order_tv5.setOnClickListener(this);
	}

	/**
	 * 选择页面
	 */
	private void pageselect() {
		myorder_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				fm = getSupportFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();
				hideFragments(transaction);
				switch (checkedId) {
				case R.id.rb_all:
					if (allorderfragment == null) {
						allorderfragment = new AllOrderFragment();
						transaction.add(R.id.myorder_content, allorderfragment);
					} else {
						transaction.show(allorderfragment);
					}
					break;
				case R.id.rb_pay:
					if (payfragment == null) {
						payfragment = new PayFragment();
						transaction.add(R.id.myorder_content, payfragment);
					} else {
						transaction.show(payfragment);
					}
					break;
				case R.id.rb_send:
					if (sendfragment == null) {
						sendfragment = new SendFragment();
						transaction.add(R.id.myorder_content, sendfragment);
					} else {
						transaction.show(sendfragment);
					}
					break;
				case R.id.rb_get:
					if (getfragment == null) {
						getfragment = new GetFragment();
						transaction.add(R.id.myorder_content, getfragment);
					} else {
						transaction.show(getfragment);
					}
					break;
				case R.id.rb_finish:
					if (finishFragment == null) {
						finishFragment = new FinishFragment();
						transaction.add(R.id.myorder_content, finishFragment);
					} else {
						transaction.show(finishFragment);
					}
					break;

				default:
					break;
				}
				transaction.commit();
			}
		});
		myorder_rg.check(R.id.rb_all);
	}

	/**
	 * 隐藏所有的页面
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (allorderfragment != null) {
			transaction.hide(allorderfragment);
		}
		if (payfragment != null) {
			transaction.hide(payfragment);
		}
		if (sendfragment != null) {
			transaction.hide(sendfragment);
		}
		if (getfragment != null) {
			transaction.hide(getfragment);
		}
		if (finishFragment != null) {
			transaction.hide(finishFragment);
		}

	}

}
