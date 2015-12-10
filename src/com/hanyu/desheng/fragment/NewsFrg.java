package com.hanyu.desheng.fragment;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.NewsAct;
import com.hanyu.desheng.adapter.NewsListPtrAdapter;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.bean.NewsBean;
import com.hanyu.desheng.bean.NewsBean.Data.News;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.hanyu.desheng.pulltorefresh.PullToRefreshListView;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.MyTimeUtils;
import com.hanyu.desheng.utils.MyToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class NewsFrg extends BaseFragment {
	@ViewInject(R.id.home_listview)
	private PullToRefreshListView home_listview;
	private List<News> hot_lesson_list;
	private int page = 0;
	private String page_more;
	private NewsListPtrAdapter adapter;
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
					getToppic("0");
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
		getToppic("0");
	}

	@Override
	public void setListener() {
		home_listview.getRefreshableView().setClickable(true);
		home_listview.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				News bean = (News) adapter.getItem(position);
				intent = new Intent(context, NewsAct.class);
				intent.putExtra("news_id", Integer.parseInt(bean.news_id));
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
	private void getToppic(final String page_no) {
		new HttpTask<Void, Void, String>(context) {
			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().getNewsList(page_no);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					NewsBean homeBean = GsonUtils.json2Bean(result, NewsBean.class);
					hot_lesson_list = homeBean.data.news_list;
					adapter = new NewsListPtrAdapter(context, hot_lesson_list);
					home_listview.getRefreshableView().setAdapter(adapter);
				}else {
					Toast.makeText(getActivity(), "加载数据失败，正在重新加载数据", Toast.LENGTH_SHORT).show();
					getToppic(page_no);
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
				String result = EngineManager.getUserEngine().getNewsList(page_no);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (code.equals("0")) {
							NewsBean homeBean = GsonUtils.json2Bean(result, NewsBean.class);
							hot_lesson_list.add((News) homeBean.data.news_list);
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

}
