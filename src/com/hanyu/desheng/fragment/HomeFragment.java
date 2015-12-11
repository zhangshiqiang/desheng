package com.hanyu.desheng.fragment;

import java.util.ArrayList;
import java.util.List;

import com.easemob.chat.EMContactManager;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.AlertDialog;
import com.hanyu.desheng.activity.BannerAct;
import com.hanyu.desheng.activity.LessonDetailsActivity;
import com.hanyu.desheng.activity.NewsAct;
import com.hanyu.desheng.adapter.HomeListPtrAdapter;
import com.hanyu.desheng.adapter.NewsListPtrAdapter;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.bean.HomeBean;
import com.hanyu.desheng.bean.HomeBean.Data.Banner;
import com.hanyu.desheng.bean.Lesson;
import com.hanyu.desheng.bean.NewsBean;
import com.hanyu.desheng.bean.NewsBean.Data.News;
import com.hanyu.desheng.engine.DSUrlManager;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.hanyu.desheng.pulltorefresh.PullToRefreshListView;
import com.hanyu.desheng.ui.loopviewpager.LoopViewPager;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.YangUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class HomeFragment extends BaseFragment {
	protected static final String tag = "HomeFragment";
	protected static final int requestCode = 555;
	private LoopViewPager homgpager_viewpager;// 轮播图
	private LinearLayout ll_point;// 轮播点
	@ViewInject(R.id.layout_content)
	private FrameLayout layout_content;
	@ViewInject(R.id.home_listview_ll)
	private FrameLayout home_listview_ll;
	@ViewInject(R.id.home_fg)
	private FrameLayout home_fg;
	private ImageView iv_img1;
	private ImageView iv_img2;
	private RadioGroup rg;
	private RadioButton home_rb1;
	@ViewInject(R.id.list_view)
	private PullToRefreshListView list_view;

	private String hx_username;
	private MyPageAdapter myadapter;

	private List<View> views = new ArrayList<View>();
	private LayoutParams paramsL = new LayoutParams(15, 15);
	private List<Banner> banners = new ArrayList<HomeBean.Data.Banner>();

	private android.app.AlertDialog updateDialog;
	private RelativeLayout home_rl_back;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_rl_back:
			Intent intent = new Intent(MainFragment.CHANGETAB);
			intent.putExtra("tab", 2);
			// main_rl.setVisibility(View.GONE);
			getActivity().sendBroadcast(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public View initView(LayoutInflater inflater) {
		view = View.inflate(context, R.layout.fragment_home, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		home_rl_back=MainFragment.home_rl_back;
		View headView = View.inflate(getActivity(), R.layout.home_head_view, null);

		homgpager_viewpager = (LoopViewPager) headView.findViewById(R.id.homgpager_viewpager);
		ll_point = (LinearLayout) headView.findViewById(R.id.ll_point);
		rg = (RadioGroup) headView.findViewById(R.id.home_rg);
		home_rb1 = (RadioButton) rg.findViewById(R.id.home_rb1);
		if ("1".equals(MainFragment.state)) {
			home_rb1.setVisibility(View.VISIBLE);
			Lesson lesson = new Lesson();
			hotList.add(lesson);
			System.out.println("********** *********" + MainFragment.state);
		}
		// else{
		// rg.check(2);
		// }
		iv_img1 = (ImageView) headView.findViewById(R.id.iv_img1);
		iv_img2 = (ImageView) headView.findViewById(R.id.iv_img2);
		list_view.getRefreshableView().addHeaderView(headView);
		selectpage();

//		getHotData("0", 2);
		getNewsData("0");

		initListener();

	}

	private void initListener() {
		home_rl_back.setOnClickListener(this);
		homgpager_viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {
				if (views.size() != 0 && views.get(position) != null) {

					for (int i = 0; i < views.size(); i++) {
						if (i == position % views.size()) {
							views.get(i).setBackgroundResource(R.drawable.sy_09);
						} else {
							views.get(i).setBackgroundResource(R.drawable.sy_07);
						}
					}

				}
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			public void onPageScrollStateChanged(int arg0) {

			}
		});

		list_view.setPullLoadEnabled(true);
		list_view.setPullRefreshEnabled(true);
		list_view.getRefreshableView().setSelector(new ColorDrawable(Color.TRANSPARENT));

		list_view.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int checkedId = rg.getCheckedRadioButtonId();
				switch (checkedId) {
				case R.id.home_rb1:
					if (position == 1) {
						Uri uri = Uri.parse("http://dian.fm/4567");
						Intent it = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(it);
						return;
					}
					Lesson bean = (Lesson) hotAdapter.getItem(position - 1);
					intent = new Intent(context, LessonDetailsActivity.class);
					intent.putExtra("lesson_id", bean.lesson_id);
					intent.putExtra("flag", 8);
					startActivity(intent);
					break;
				case R.id.home_rb2:
					News newBean = (News) newsAdapter.getItem(position - 1);
					intent = new Intent(context, NewsAct.class);
					intent.putExtra("news_id", Integer.parseInt(newBean.news_id));
					intent.putExtra("flag", 8);
					startActivity(intent);
					break;
				}
			}
		});

		list_view.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (isRefresh) {
					return;
				}
				isRefresh = true;

				int checkedId = rg.getCheckedRadioButtonId();
				switch (checkedId) {
				case R.id.home_rb1:
					if (hotList != null) {
						hotList.clear();
						Lesson lesson = new Lesson();
						hotList.add(lesson);
						getHotData("0", 2);
					}
					break;
				case R.id.home_rb2:
					if (newsList != null) {
						newsList.clear();
						getNewsData("0");
					}
					break;
				}

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

				if (isRefresh) {
					return;
				}
				isRefresh = true;

				int checkedId = rg.getCheckedRadioButtonId();
				switch (checkedId) {
				case R.id.home_rb1:
					if (hotList != null) {
						hotPage += 1;
						getHotData(hotPage + "", 2);
					}
					break;
				case R.id.home_rb2:
					if (newsList != null) {
						newsPage += 1;
						getNewsData(newsPage + "");
					}
					break;
				}
				// list_view.onPullUpRefreshComplete();
			}
		});
	}

	public boolean isRefresh = false;

	private int hotPage = 0;
	private int newsPage = 0;

	private HomeListPtrAdapter hotAdapter;
	private NewsListPtrAdapter newsAdapter;

	private void selectpage() {
		if ("1".equals(MainFragment.state)) {
			home_rb1.setVisibility(View.VISIBLE);
			Lesson lesson = new Lesson();
			hotList.add(lesson);
			System.out.println("*******************" + MainFragment.state);
		} else {
			home_rb1.setVisibility(View.GONE);
			iv_img1.setVisibility(View.GONE);
			// hotList = null;
		}
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.home_rb1:
					if (hotList != null) {
						hotAdapter = new HomeListPtrAdapter(context, hotList);
						list_view.getRefreshableView().setAdapter(hotAdapter);
						iv_img2.setBackgroundResource(R.color.white);
						iv_img1.setBackgroundResource(R.drawable.sun4);
					}
					break;
				case R.id.home_rb2:
					if (newsList != null) {
						newsAdapter = new NewsListPtrAdapter(context, newsList);
						list_view.getRefreshableView().setAdapter(newsAdapter);
						iv_img1.setBackgroundResource(R.color.white);
						iv_img2.setBackgroundResource(R.drawable.sun4);
					}
					break;
				}
			}
		});
		if ("1".equals(MainFragment.state)) {
			rg.check(R.id.home_rb1);
		} else {
			rg.check(R.id.home_rb2);
		}
		getToppic("0");
	}

	private List<Lesson> hotList = new ArrayList<Lesson>();

	/**
	 * 热门课程
	 * 
	 * @param i
	 */
	private void getHotData(final String page_no, final int i) {

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
				isRefresh = false;
				if (result != null) {
					try {
						HomeBean homeBean = GsonUtils.json2Bean(result, HomeBean.class);
						if (hotList != null) {
							hotList.addAll(homeBean.data.hot_lesson_list);
						}
						if (hotAdapter != null) {
							hotAdapter.notifyDataSetChanged();
						} else {
							list_view.getRefreshableView().setAdapter(new HomeListPtrAdapter(getActivity(), hotList));
						}
						hotPage = Integer.parseInt(page_no);
					} catch (Exception e) {
						Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
					}
				} else {
					if (getActivity() != null) {
						Toast.makeText(getActivity(), "加载数据失败，请上拉刷新", Toast.LENGTH_SHORT).show();
						// getHotData(page_no, i);
					}
				}
				list_view.onPullDownRefreshComplete();
				list_view.onPullUpRefreshComplete();
			}

			
			protected void onPreExecute() {

			}
		}.executeProxy();
	}

	private List<News> newsList = new ArrayList<NewsBean.Data.News>();

	private void getNewsData(final String page_no) {
		new HttpTask<Void, Void, String>(context) {
			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().getNewsList(page_no);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				isRefresh = false;
				if (result != null) {
					try {
						NewsBean homeBean = GsonUtils.json2Bean(result, NewsBean.class);
						if (newsList != null) {
							newsList.addAll(homeBean.data.news_list);
							int checkedId = rg.getCheckedRadioButtonId();
							if (checkedId == R.id.home_rb2) {
								if (newsAdapter == null) {
									newsAdapter = new NewsListPtrAdapter(getActivity(), newsList);
									list_view.getRefreshableView().setAdapter(newsAdapter);
								} else {
									newsAdapter.notifyDataSetChanged();
								}
								newsPage = Integer.parseInt(page_no);
							}

						}
					} catch (Exception e) {

					}

				} else {
					Toast.makeText(getActivity(), "加载数据失败，请上拉刷新", Toast.LENGTH_SHORT).show();
					// getToppic(page_no);
				}
				list_view.onPullDownRefreshComplete();
				list_view.onPullUpRefreshComplete();
			}

			protected void onPreExecute() {

			}
		}.executeProxy();
	}

	/**
	 * 头部轮播图
	 * 
	 * @param i
	 */
	private void getToppic(final String page_no) {
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
					HomeBean homeBean = GsonUtils.json2Bean(result, HomeBean.class);
					banners = homeBean.data.banner_list;
					startViewPager();

					// 检测版本更新
					checkUpdate(homeBean);
				}
			}

			protected void onPreExecute() {

			}
		}.executeProxy();
	}

	/**
	 * 检查更新
	 */
	public void checkUpdate(HomeBean homeBean) {
		if (getActivity() == null) {
			return;
		}
		PackageManager pm = getActivity().getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getActivity().getPackageName(), 0);
			String versionName = info.versionName;
			if (!versionName.equals(homeBean.data.andriod.version)) {
				showUpdateDialog(homeBean.data.andriod.version,
						new DSUrlManager().getFullUrl3(homeBean.data.andriod.apk_url));

			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void showUpdateDialog(final String versionName, final String downloadUrl) {
		Builder builder = new android.app.AlertDialog.Builder(context);
		builder.setTitle("发现新版本!");
		builder.setMessage("新版本" + versionName + ",是否更新");
		builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateDialog.dismiss();
			}

		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateDialog.dismiss();
				// Intent updateIntent = new Intent(context,
				// UpdateAppService.class);
				// updateIntent.putExtra("downloadUrl", downloadUrl);
				// context.startService(updateIntent);

				Uri uri = Uri.parse("http://www.pgyer.com/1G7m");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

			}
		});
		updateDialog = builder.show();

	}

	@Override
	public void setListener() {

	}

	/**
	 * 启动轮播图
	 */

	private void startViewPager() {
		homgpager_viewpager.setAuto(true);
		initMyPageAdapter();
	}

	/**
	 * 初始化点
	 */
	private void initPoint() {
		views.clear();
		ll_point.removeAllViews();
		for (int i = 0; i < banners.size(); i++) {
			View view = new View(context);
			paramsL.setMargins(YangUtils.dip2px(context, 5), 0, 0, 0);
			view.setLayoutParams(paramsL);
			if (i == 0) {
				view.setBackgroundResource(R.drawable.sy_03);
			} else {
				view.setBackgroundResource(R.drawable.sy_07);
			}

			views.add(view);
			ll_point.addView(view);
		}

	}

	/**
	 * 初始化adapter
	 */
	private void initMyPageAdapter() {
		initPoint();
		if (myadapter == null) {
			myadapter = new MyPageAdapter();
			if (homgpager_viewpager != null) {
				homgpager_viewpager.setAdapter(myadapter);
			}

		} else {
			myadapter.notifyDataSetChanged();
		}

	}

	private class MyPageAdapter extends PagerAdapter {
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		@SuppressWarnings("deprecation")
		private DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.deshenglogo) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.deshenglogo) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.deshenglogo) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.build(); // 创建配置过得DisplayImageOption对象

		@Override
		public int getCount() {
			return banners.size();
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		public Object instantiateItem(ViewGroup container, final int position) {
			View view = View.inflate(context, R.layout.home_vp_item, null);

			ImageView iv_iamge = (ImageView) view.findViewById(R.id.iv_image);
			iv_iamge.setScaleType(ScaleType.FIT_XY);
			ImageLoader.getInstance().displayImage(banners.get(position).banner, iv_iamge, options);

			((ViewPager) container).addView(view);

			iv_iamge.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String link_url = banners.get(position).link_url;
					if (link_url.contains("news:")) {
						intent = new Intent(context, NewsAct.class);
						String news = link_url.replace("news:", "").toString();
						int news_id = Integer.parseInt(news);
						intent.putExtra("news_id", news_id);
						intent.putExtra("flag", 8);
						startActivity(intent);
					} else if (link_url.contains("lesson:")) {
						intent = new Intent(context, LessonDetailsActivity.class);
						String lesson = link_url.replace("lesson:", "").toString();
						int lesson_id = Integer.parseInt(lesson);
						intent.putExtra("lesson_id", lesson_id);
						intent.putExtra("flag", 8);
						startActivity(intent);
					} else if (link_url.contains("http://")) {
						intent = new Intent(context, BannerAct.class);
						intent.putExtra("link_url", link_url);
						startActivity(intent);
					}
				}
			});

			return view;
		}

		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 5555 && resultCode == 666) {
			if (data != null) {
				hx_username = data.getExtras().getString("hx_name").toString();
				String username = SharedPreferencesUtil.getStringData(context, "miname", "");
				addContact(hx_username, username);
			}
		}
	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact(final String hx_username, final String username) {
		if (ExampleApplication.getInstance().getUserName().equals(hx_username)) {
			String str = context.getResources().getString(R.string.not_add_myself);
			context.startActivity(new Intent(context, AlertDialog.class).putExtra("msg", str));
			return;
		}

		if (ExampleApplication.getInstance().getContactList().containsKey(hx_username)) {
			String strin = context.getResources().getString(R.string.This_user_is_already_your_friend);
			context.startActivity(new Intent(context, AlertDialog.class).putExtra("msg", strin));
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(context);
		String stri = context.getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {
				try {
					String s = username + "请求加你为好友";
					EMContactManager.getInstance().addContact(hx_username, s);
					((Activity) context).runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							String s1 = context.getResources().getString(R.string.send_successful);
							Toast.makeText(context, s1, 1).show();
						}
					});
				} catch (final Exception e) {
					((Activity) context).runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							String s2 = context.getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(context, s2 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	// public void onBackPressed() {
	// Intent intent = new Intent(MainFragment.CHANGETAB);
	// intent.putExtra("tab", 2);
	// // main_rl.setVisibility(View.GONE);
	// context.sendBroadcast(intent);
	// }

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	//
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	//
	//
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

}
