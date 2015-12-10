package com.hanyu.desheng.fragment;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.LessonDetailsActivity;
import com.hanyu.desheng.adapter.HomeListPtrAdapter;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.bean.HomeBean;
import com.hanyu.desheng.bean.HomeBean2;
import com.hanyu.desheng.bean.Lesson;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.hanyu.desheng.pulltorefresh.PullToRefreshListView;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.MyTimeUtils;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.YangUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class HotLessonFrg extends BaseFragment {

	@ViewInject(R.id.home_listview)
	private PullToRefreshListView home_listview;
	private List<Lesson> hot_lesson_list;
	private int page = 0;
	private String page_more;
	private HomeListPtrAdapter adapter;
	@ViewInject(R.id.my_order_lv_fl)
	private FrameLayout my_order_lv_fl;

	@Override
	public void onClick(View v) {

	}

	@Override
	public View initView(LayoutInflater inflater) {
		view = View.inflate(context, R.layout.hotlesson, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		View headView = View.inflate(context, R.layout.head_image_view, null);
		home_listview.getRefreshableView().addHeaderView(headView);
		
		home_listview.setPullLoadEnabled(true);
		home_listview.setPullRefreshEnabled(true);
		home_listview.getRefreshableView().setSelector(new ColorDrawable(Color.TRANSPARENT));
		home_listview.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				home_listview.setLastUpdatedLabel(MyTimeUtils.getStringDate());
				home_listview.onPullDownRefreshComplete();
				if (hot_lesson_list != null) {
					hot_lesson_list.clear();
					getToppic("0", 2);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				home_listview.onPullUpRefreshComplete();
				// 加载更多
				if (!TextUtils.isEmpty(page_more)) {
					if (Integer.parseInt(page_more) > 0) {
						page++;
						getToppic2(page + "");
					} else {
						home_listview.setHasMoreData(false);
					}
				} else {
					MyToastUtils.showShortToast(context, "没有更多啦！~");
				}

			}
		});
		getToppic("0", 1);

	}

	@Override
	public void setListener() {
		home_listview.getRefreshableView().setClickable(true);
		home_listview.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					Uri uri = Uri.parse("http://dian.fm/4567");
					Intent it = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(it);
					return;
				}
				Lesson bean = (Lesson) adapter.getItem(position - 1);
				intent = new Intent(context, LessonDetailsActivity.class);
				intent.putExtra("lesson_id", bean.lesson_id);
				intent.putExtra("flag", 8);
				startActivity(intent);
			}
		});
	}

	/**
	 * 从网络获取图片地址
	 * 
	 * @param i
	 */
	private void getToppic(final String page_no, final int i) {
		new HttpTask<Void, Void, String>(context) {
			@Override
			protected String doInBackground(Void... params) {
				String user_id = "";
				String result = "";
				if (YangUtils.isLogin(context)) {
					user_id = SharedPreferencesUtil.getStringData(context, "memberid", "");
					result = EngineManager.getUserEngine().getTopPic(user_id, page_no);
				} else {
					result = EngineManager.getUserEngine().getTopPic("", page_no);
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				// if (page == 0) {
				// hot_lesson_list.clear();
				// }
				if (result != null) {
					HomeBean homeBean = GsonUtils.json2Bean(result, HomeBean.class);
					hot_lesson_list = homeBean.data.hot_lesson_list;
					adapter = new HomeListPtrAdapter(context, hot_lesson_list);
					home_listview.getRefreshableView().setAdapter(adapter);
				} else {
					if (getActivity() != null) {
						Toast.makeText(getActivity(), "加载数据失败，正在重新加载数据", Toast.LENGTH_SHORT).show();
						getToppic(page_no, i);
					}
				}
				my_order_lv_fl.setVisibility(View.GONE);
			}

			protected void onPreExecute() {

			}
		}.executeProxy();
	}

	/**
	 * 加载更多
	 */
	private void getToppic2(final String page_no) {
		new HttpTask<Void, Void, String>(context) {
			@Override
			protected String doInBackground(Void... params) {
				String user_id = "";
				String result = "";
				if (YangUtils.isLogin(context)) {
					user_id = SharedPreferencesUtil.getStringData(context, "memberid", "");
					result = EngineManager.getUserEngine().getTopPic(user_id, page_no);
				} else {
					result = EngineManager.getUserEngine().getTopPic("", page_no);
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (code.equals("0")) {
							HomeBean2 homeBean = GsonUtils.json2Bean(result, HomeBean2.class);
							hot_lesson_list.add((Lesson) homeBean.data.hot_lesson_list);
							adapter.notifyDataSetChanged();
							page_more = homeBean.data.page_more;
						} else {
							MyToastUtils.showShortToast(context, "获取失败");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}

			protected void onPreExecute() {

			}
		}.executeProxy();
	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATA_LESSONLIST);
		if (receiver == null) {
			receiver = new MyBroadCastReceiver();
		}
		context.registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			context.unregisterReceiver(receiver);
		}
		receiver = null;
	}

	private MyBroadCastReceiver receiver;
	public final static String UPDATA_LESSONLIST = "com.hanyu.desheng.updatelessonlist";

	private class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (hot_lesson_list != null) {
				hot_lesson_list.clear();
				getToppic("0", 1);
				adapter.notifyDataSetChanged();
			}
		}
	}
}
