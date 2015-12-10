package com.hanyu.desheng.activity;

import java.io.InputStream;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.UserInfo;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.ui.CircleImageView;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.XmlComonUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DZAct extends BaseActivity {
	@ViewInject(R.id.dzact_riv)
	private CircleImageView dzact_riv;// 头像
	@ViewInject(R.id.dzact_tv1)
	private TextView dzact_tv1;// 昵称
	@ViewInject(R.id.dzact_tv2)
	private TextView dzact_tv2;// 手机号
	@ViewInject(R.id.dzact_tv3)
	private TextView dzact_tv3;// QQ
	@ViewInject(R.id.dzact_tv4)
	private TextView dzact_tv4;// 微信
	private UserInfo userInfo;
	private String phone;
	@ViewInject(R.id.my_order_lv_fl)
	private FrameLayout my_order_lv_fl;

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.dzact;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setBack();
		setTopTitle("店长信息");
		userInfo = new UserInfo();
		context = DZAct.this;
		phone = SharedPreferencesUtil.getStringData(context, "dzphone", "");
		getUserInfo(phone);
	}

	@Override
	public void setListener() {

	}

	/**
	 * 获取会员信息xml流
	 * 
	 * @param memberid
	 */
	protected void getUserInfo(final String phone) {

		new HttpTask<Void, Void, InputStream>(context) {

			@Override
			protected InputStream doInBackground(Void... params) {
				InputStream result = EngineManager.getUserEngine().toGetinfo2(
						phone);
				return result;
			}

			@Override
			protected void onPostExecute(InputStream result) {
				if (result != null) {
					try {
						// 用xml解析工具类解析xml
						XmlComonUtil.streamText2Model(result, userInfo);
						// 昵称
						dzact_tv1.setText(userInfo.miname);
						// 真实姓名
						// personal_et.setText(userInfo.mirealname);
						// email
						// personal_tv3.setText(userInfo.miemail);
						// 手机号
						dzact_tv2.setText(userInfo.miphone);
						// qq
						dzact_tv3.setText(userInfo.qq);
						// 微信
						dzact_tv4.setText(userInfo.miweixin);
						ImageLoader.getInstance().displayImage(
								userInfo.miuserheader, dzact_riv);

						// 职业
						// personal_profession.setText(userInfo.miprofession);
						// 生日
						// @SuppressWarnings("deprecation")
						// Date date = new Date(userInfo.mibirthday);
						// String format = (String) DateFormat.format(
						// "yyyy-MM-dd", date);
						// personal_birthday.setText(format);
						// // 性别
						// if ("1".equals(userInfo.misex)) {
						// personal_rg.check(R.id.personal_man);
						// } else if ("0".equals(userInfo.misex)) {
						// personal_rg.check(R.id.personal_woman);
						// }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				my_order_lv_fl.setVisibility(View.GONE);
			}

			@Override
			protected void onPreExecute() {
				my_order_lv_fl.setVisibility(View.VISIBLE);
			}

		}.executeProxy();
	}

}
