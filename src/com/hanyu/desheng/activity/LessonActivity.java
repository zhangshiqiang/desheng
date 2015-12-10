package com.hanyu.desheng.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanyu.desheng.R;
import com.hanyu.desheng.adapter.LessonHotAdapter;
import com.hanyu.desheng.base.BaseActivity;
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
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LessonActivity extends BaseActivity {
	@ViewInject(R.id.lesson_ptr)
	private PullToRefreshListView lesson_ptr;
	private LessonHotAdapter adapter;
	private List<Lesson> hot_lesson_list;
	private String page_more;
	private int page = 1;

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.lesson;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("课程");
		context = LessonActivity.this;
		lesson_ptr.setPullLoadEnabled(true);
		lesson_ptr.setPullRefreshEnabled(true);
		lesson_ptr.getRefreshableView().setSelector(
				new ColorDrawable(Color.TRANSPARENT));
		lesson_ptr.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				lesson_ptr.setLastUpdatedLabel(MyTimeUtils.getStringDate());
				lesson_ptr.onPullDownRefreshComplete();
				if (hot_lesson_list != null) {
					hot_lesson_list.clear();
					adapter.notifyDataSetChanged();
					getToppic("0");
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				lesson_ptr.onPullUpRefreshComplete();
				// 加载更多
				if (page_more.equals("1")) {
					page++;
					getToppic(page + "");
				} else {
					lesson_ptr.setHasMoreData(false);
				}
			}
		});
		getToppic("1");
	}

	@Override
	public void setListener() {
		lesson_ptr.getRefreshableView().setOnItemClickListener(
				new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Lesson bean = (Lesson) adapter.getItem(position);
						intent = new Intent(LessonActivity.this,
								LessonDetailsActivity.class);
						intent.putExtra("lesson_id", bean.lesson_id);
						intent.putExtra("title", bean.title);
						intent.putExtra("thumb", bean.thumb);
						intent.putExtra("is_collect", bean.is_collect);
						intent.putExtra("collect_num", bean.collect_num);
						startActivity(intent);
					}
				});
	}

	/**
	 * 从网络获取图片地址
	 */
	private void getToppic(final String page_no) {
		new HttpTask<Void, Void, String>(context) {
			@Override
			protected String doInBackground(Void... params) {
				String user_id = "";
				String result = "";
				if (YangUtils.isLogin(context)) {
					user_id = SharedPreferencesUtil.getStringData(context,
							"memberid", "");
					result = EngineManager.getUserEngine().getTopPic(user_id,
							page_no);
				} else {
					result = EngineManager.getUserEngine().getTopPic("",
							page_no);
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (page_no.equals("0")) {
					hot_lesson_list.clear();
				}
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (code.equals("0")) {
							HomeBean2 homeBean = GsonUtils.json2Bean(result,
									HomeBean2.class);
							hot_lesson_list
									.addAll(homeBean.data.hot_lesson_list);
							page_more = homeBean.data.page_more;
							adapter = new LessonHotAdapter(context,
									hot_lesson_list);
							lesson_ptr.getRefreshableView().setAdapter(adapter);
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
