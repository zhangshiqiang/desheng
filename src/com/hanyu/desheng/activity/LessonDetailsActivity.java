package com.hanyu.desheng.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.fragment.HotLessonFrg;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LessonDetailsActivity extends BaseActivity {
	protected static final String tag = "LessonDetailsActivity";
	@ViewInject(R.id.lesson_details_rl)
	private ImageView lesson_details_rl;
	@ViewInject(R.id.lesson_details_tv)
	private TextView lesson_details_tv;
	@ViewInject(R.id.lesson_details_ll)
	private LinearLayout lesson_details_ll;// 收藏范围
	@ViewInject(R.id.lesson_details_iv)
	private ImageView lesson_details_iv;// 收藏心
	@ViewInject(R.id.lesson_details_tv2)
	private TextView lesson_details_tv2;// 收藏文字
	@ViewInject(R.id.lesson_details_wv)
	private WebView lesson_details_wv;
	@ViewInject(R.id.rl_back)
	private RelativeLayout rl_back;
	private int lesson_id;
	private int flag;
	private String collect_num;

	private String hx_group_id;
	private String is_collect;

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.lesson_details;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setTopTitle("课程详情");
		context = LessonDetailsActivity.this;
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		lesson_id = bundle.getInt("lesson_id");
		flag = bundle.getInt("flag");
		if (flag == 9) {
			rl_back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					intent = new Intent(context, MainActivity.class);
					startActivity(intent);
					finish();
				}
			});
		} else if (flag == 8) {
			setBack();
		}
		getLessonDetails(lesson_id);

		lesson_details_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("加入该群".equals(lesson_details_tv2.getText().toString())) {
					getcollect(lesson_id);
				} else if ("进入该群".equals(lesson_details_tv2.getText()
						.toString())) {
					Intent intent = new Intent(context, ChatActivity.class);
					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					intent.putExtra("groupId", hx_group_id);
					startActivity(intent);
				}
			}
		});

	}

	@Override
	public void setListener() {

	}

	/**
	 * 获取课程详情
	 * 
	 * @param lesson_id
	 *            课程id
	 */
	private void getLessonDetails(final int lesson_id) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String user_id = SharedPreferencesUtil.getStringData(context,
						"memberid", "");
				String result = EngineManager.getUserEngine().getLessonDetails(
						user_id, lesson_id);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (code.equals("0")) {
							LogUtil.i(tag, "获取课程详情成功");
							JSONObject data = json.getJSONObject("data");
							String lesson_info = data.getString("lesson_info");
							collect_num = data.getString("collect_num");
							is_collect = data.getString("is_collect");
							String title = data.getString("title");
							String thumb = data.getString("thumb");
							hx_group_id = data.getString("hx_group_id");
							lesson_details_tv.setText(title);
							ImageLoader.getInstance().displayImage(thumb,
									lesson_details_rl);
							lesson_details_rl.setScaleType(ScaleType.FIT_XY);

							if ("1".equals(is_collect)) {
								lesson_details_iv
										.setBackgroundResource(R.drawable.xin_hong);
								lesson_details_tv2.setText("进入该群");
							} else {
								lesson_details_iv
										.setBackgroundResource(R.drawable.xin_bai);
								lesson_details_tv2.setText("加入该群");
							}
							Spanned fromHtml = Html.fromHtml(lesson_info);
							lesson_details_wv.getSettings()
									.setLoadsImagesAutomatically(true);
							lesson_details_wv.loadDataWithBaseURL(null,
									fromHtml.toString(), "text/html", "UTF-8",
									null);
						} else {
							String msg = json.getString("msg");
							MyToastUtils.showShortToast(context, msg);
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

	/**
	 * 收藏
	 * 
	 * @param lesson_id
	 */
	protected void getcollect(final int lesson_id) {
		new HttpTask<Void, Void, String>(context) {
			@Override
			protected String doInBackground(Void... params) {
				String user_id = SharedPreferencesUtil.getStringData(context,
						"memberid", "");
				String mobile = SharedPreferencesUtil.getStringData(context,
						"miphone", "");
				String result = EngineManager.getUserEngine().Collect(user_id,
						lesson_id, mobile);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					if (result != null) {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (code.equals("0")) {
							lesson_details_iv
									.setBackgroundResource(R.drawable.xin_hong);
							lesson_details_tv2.setText("进入该群");
							MyToastUtils.showShortToast(context, "已加入该群");
							intent = new Intent(HotLessonFrg.UPDATA_LESSONLIST);
							sendBroadcast(intent);
						} else {
							String msg = json.getString("msg");
							MyToastUtils.showShortToast(context, msg);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			protected void onPreExecute() {

			}
		}.executeProxy();
	}

	/**
	 * 取消收藏
	 * 
	 * @param lesson_id
	 */
	// protected void cancelcollect(final int lesson_id) {
	// new HttpTask<Void, Void, String>(context) {
	// @Override
	// protected String doInBackground(Void... params) {
	// String user_id = SharedPreferencesUtil.getStringData(context,
	// "memberid", "");
	// String mobile = SharedPreferencesUtil.getStringData(context,
	// "miphone", "");
	// String result = EngineManager.getUserEngine().CancelCollect(
	// user_id, lesson_id, mobile);
	// return result;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// try {
	// if (result != null) {
	// JSONObject json = new JSONObject(result);
	// String code = json.getString("code");
	// if (code.equals("0")) {
	// MyToastUtils.showShortToast(context, "已退出该群");
	// intent = new Intent(HomeFragment.UPDATA_LESSONLIST);
	// sendBroadcast(intent);
	// } else {
	// String msg = json.getString("msg");
	// MyToastUtils.showShortToast(context, msg);
	// }
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// protected void onPreExecute() {
	//
	// }
	// }.executeProxy();
	// }

}
