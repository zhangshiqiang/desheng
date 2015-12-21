package com.hanyu.desheng.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.util.Base64Encoder;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.hanyu.desheng.GlobalParams;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.PointListInfo;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.fragment.MainFragment;
import com.hanyu.desheng.ui.RoundedImageView;
import com.hanyu.desheng.util.ShowDialogUtil;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.YangUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class GeneralizeActivity extends BaseActivity {
	private static final String tag = "GeneralizeActivity";
	@ViewInject(R.id.generalize_lv)
	private ListView generalize_lv;
	@ViewInject(R.id.generalize_username)
	private TextView generalize_username;
	@ViewInject(R.id.generalize_miinter)
	private TextView generalize_miinter;
	@ViewInject(R.id.generalize_btn)
	private Button generalize_btn;
	@ViewInject(R.id.generalize_rl2)
	private RelativeLayout generalize_rl2;
	@ViewInject(R.id.generalize_headriv)
	private RoundedImageView generalize_headriv;
	private String phone;
	private String username;
	private String miinter;
	private List<PointListInfo> pointListInfos;
	private MyAdapter adapter;
	private String appHome = Environment.getExternalStorageDirectory().getAbsolutePath() + "/desheng";
	private String DSQR;
	private String state;
	public static String url;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.generalize_btn:
			if (!YangUtils.isLogin(context)) {
				ShowDialogUtil.showIsLoginDialog(context);
			} else {
				if ("1".equals(state)) {
					DSQR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/desheng/" + 5 + ".png";
					LogUtil.i(tag, DSQR);
					showShare();
				} else {
					MyToastUtils.showShortToast(context, "您还不是店主，目前不能使用此功能");
				}

			}
			break;
		case R.id.generalize_rl2:
			if (!YangUtils.isLogin(context)) {
				ShowDialogUtil.showIsLoginDialog(context);
			} else {
				if ("1".equals(state)) {
					intent = new Intent(context, MygenCodeAct.class);
					intent.putExtra("dsqr", url);
					startActivity(intent);
				} else {
					MyToastUtils.showShortToast(context, "您还不是店主，目前不能使用此功能");
				}

			}
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.generalize;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		BaseActivity.isback_shop = true;
		setBack();
		setTopTitle("德升4567-推广规则");
		context = GeneralizeActivity.this;
		initView();
		phone = SharedPreferencesUtil.getStringData(context, "miphone", "");
		String mobile = Base64Encoder.getInstance().encode(phone);
		LogUtil.i(tag, "mobile" + mobile);
		url = "http://app.4567cn.com/Api/login.php?invite=" + mobile;
		Bitmap bitmap = YangUtils.createQRImage(url);
		compressImage(bitmap, 5);
		getPointList(phone);

	}

	private void initView() {
		username = SharedPreferencesUtil.getStringData(context, "miname", "");
		miinter = SharedPreferencesUtil.getStringData(context, "miinter", "");
		generalize_username.setText("用户名：" + username);
		generalize_miinter.setText(miinter);
		state = SharedPreferencesUtil.getStringData(context, "shopstate", "");
		String headpic = SharedPreferencesUtil.getStringData(context, "headpic", "");
		if (!TextUtils.isEmpty(headpic)) {
			ImageLoader.getInstance().displayImage(headpic, generalize_headriv);
		}
	}

	@Override
	public void setListener() {
		generalize_btn.setOnClickListener(this);
		generalize_rl2.setOnClickListener(this);
	}

	public static String MSG = "com.hanyu.desheng.activity.GeneralizeActivity.fenxiang";
	private MyBroadCast2 receiver3;

	private class MyBroadCast2 extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.i("fenxiang", "广播分享！！！！！！！！");
			if (!YangUtils.isLogin(context)) {
				ShowDialogUtil.showIsLoginDialog(context);
			} else {
				if ("1".equals(state)) {
					DSQR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/desheng/" + 5 + ".png";
					LogUtil.i(tag, DSQR);
					showShare();
				} else {
					MyToastUtils.showShortToast(context, "您还不是店主，目前不能使用此功能");
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (receiver3 != null) {
			unregisterReceiver(receiver3);
		}
		super.onDestroy();
	}

	/**
	 * 获取积分列表
	 * 
	 * @param phone2
	 *            用户手机号
	 */
	private void getPointList(final String phone2) {
		new HttpTask<Void, Void, InputStream>(context) {

			@Override
			protected InputStream doInBackground(Void... params) {
				InputStream result = EngineManager.getShopEngine().getPointList(phone2, 0, 0);
				return result;
			}

			@Override
			protected void onPostExecute(InputStream result) {
				try {
					if (result != null) {
						pointListInfos = PullParseXML(result);
						adapter = new MyAdapter();
						generalize_lv.setAdapter(adapter);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.onPostExecute(result);
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
		}.executeProxy();
	}

	public List<PointListInfo> PullParseXML(InputStream result) {

		List<PointListInfo> list = null;
		PointListInfo pointListInfo = null;

		// 构建XmlPullParserFactory
		try {
			// 获取XmlPullParser的实例
			XmlPullParser xmlPullParser = Xml.newPullParser();
			// 设置输入流 xml文件
			xmlPullParser.setInput(result, "UTF-8");
			int eventType = xmlPullParser.getEventType();

			try {

				while (eventType != XmlPullParser.END_DOCUMENT) {
					LogUtil.i(tag, "END_DOCUMENT:" + eventType);
					String nodeName = xmlPullParser.getName();
					switch (eventType) {
					// 文档开始
					case XmlPullParser.START_DOCUMENT:
						LogUtil.i(tag, "START_DOCUMENT:" + eventType);
						list = new ArrayList<PointListInfo>();
						break;
					// 开始节点
					case XmlPullParser.START_TAG:
						LogUtil.i(tag, "START_TAG:" + eventType);
						// 判断如果其实节点为item
						if ("item".equals(nodeName)) {
							// 实例化积分列表对象
							pointListInfo = new PointListInfo();
						} else if ("count".equals(nodeName)) {
							// LogUtil.i(tag, "count" +
							// xmlPullParser.nextText());
							// 设置count
							pointListInfo.setCount(Integer.parseInt(xmlPullParser.nextText()));
						} else if ("addtime".equals(nodeName)) {
							// 设置addtime
							pointListInfo.setAddtime(xmlPullParser.nextText());
						} else if ("from".equals(nodeName)) {
							// 设置from
							pointListInfo.setFrom(xmlPullParser.nextText());
						} else if ("type".equals(nodeName)) {
							// 设置type
							pointListInfo.setType(xmlPullParser.nextText());
						} else if ("info".equals(nodeName)) {
							// 设置info
							pointListInfo.setInfo(xmlPullParser.nextText());
						} else if ("theline".equals(nodeName)) {
							// 设置theline属性
							pointListInfo.setTheline(xmlPullParser.nextText());
						}
						break;
					// 结束节点
					case XmlPullParser.END_TAG:
						if ("item".equals(nodeName)) {
							list.add(pointListInfo);
							pointListInfo = null;
						}
						break;
					default:
						break;
					}
					eventType = xmlPullParser.next();
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return list;
	}

	private class MyAdapter extends BaseAdapter {

		// private int iv[] = { R.drawable.dl_qq, R.drawable.jf_wx,
		// R.drawable.jf_xlwb };
		@Override
		public int getCount() {
			return pointListInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(GeneralizeActivity.this, R.layout.generalize_lv_item, null);
				viewHolder.gl_lv_tv1 = (TextView) convertView.findViewById(R.id.gl_lv_tv1);
				viewHolder.gl_lv_tv2 = (TextView) convertView.findViewById(R.id.gl_lv_tv2);
				viewHolder.gl_lv_tv3 = (TextView) convertView.findViewById(R.id.gl_lv_tv3);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.gl_lv_tv3.setText("来源：" + pointListInfos.get(position).from);
			viewHolder.gl_lv_tv1.setText("日期：" + pointListInfos.get(position).addtime);
			viewHolder.gl_lv_tv2.setText("积分：" + pointListInfos.get(position).count);
			return convertView;
		}

		class ViewHolder {
			TextView gl_lv_tv1;
			TextView gl_lv_tv2;
			TextView gl_lv_tv3;
		}
	}

	private void showShare() {
		GlobalParams.share = 2;
		OnekeyShare oks = new OnekeyShare();
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("DS4567");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(url);
		// text是分享文本，所有平台都需要这个字段
		oks.setText("大众创业，万众创新：我只推荐 德升DS4567，一个更专业的时装购物平台，");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(DSQR);
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(url);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("大众创业，万众创新：我只推荐 德升DS4567，一个更专业的时装购物平台，");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		oks.setSiteUrl(url);
		oks.show(context);

	}

	@SuppressLint("SdCardPath")
	private Bitmap compressImage(Bitmap image, int i) {
		File destDir = new File(appHome);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			LogUtil.i(tag, "我已生成二维码");
			stream = new FileOutputStream("/sdcard/desheng/" + i + ".png");
			// selectedspath.add("/sdcard/desheng/"+ i +".png");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bitmap.compress(format, quality, stream);
		return bitmap;
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

}
