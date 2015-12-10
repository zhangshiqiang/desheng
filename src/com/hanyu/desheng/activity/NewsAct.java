package com.hanyu.desheng.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.util.LogUtils;
import com.hanyu.desheng.utils.MyToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsAct extends BaseActivity {
	protected static final String tag = "LessonDetailsActivity";
	@ViewInject(R.id.news_details_rl)
	private ImageView news_details_rl;
	@ViewInject(R.id.news_details_tv)
	private TextView news_details_tv;
	@ViewInject(R.id.news_details_wv)
	private WebView news_details_wv;
	@ViewInject(R.id.rl_back)
	private RelativeLayout rl_back;
	private int news_id;
	private int flag;

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.news_details;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("新闻详情");
		context = NewsAct.this;
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		news_id = bundle.getInt("news_id");
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
		getLessonDetails(news_id);

		news_details_wv.getSettings().setJavaScriptEnabled(true);
	}

	@Override
	public void setListener() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		news_details_wv.destroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		news_details_wv.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		news_details_wv.onResume();  
	}

	private void getLessonDetails(final int news_id) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().getNewsDetails(news_id);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						LogUtils.e(getClass(), "result:"+result);
						if (code.equals("0")) {
							JSONObject data = json.getJSONObject("data");
							String lesson_info = data.getString("lesson_info");
							String title = data.getString("title");
							String thumb = data.getString("thumb");
							news_details_tv.setText(title);
							ImageLoader.getInstance().displayImage(thumb, news_details_rl);
							news_details_rl.setScaleType(ScaleType.FIT_XY);
							Spanned fromHtml = Html.fromHtml(lesson_info);
							news_details_wv.loadDataWithBaseURL(null, fromHtml.toString(), "text/html", "UTF-8", null);
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

}
