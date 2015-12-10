package com.hanyu.desheng.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AccountSafeActivity extends BaseActivity {
	protected static final int UNSINA = 22;
	protected static final int SINA = 21;
	protected static final int BQQ = 01;
	protected static final int UNBQQ = 02;
	protected static final int WEIXIN = 11;
	protected static final int UNWEIXIN = 12;
	@ViewInject(R.id.accountsafe_rl1)
	private RelativeLayout accountsafe_rl1;// 密码修改
	@ViewInject(R.id.third_qq)
	private RelativeLayout third_qq;// 密码修改
	@ViewInject(R.id.third_wx)
	private RelativeLayout third_wx;// 密码修改
	@ViewInject(R.id.third_xlwb)
	private RelativeLayout third_xlwb;// 密码修改
	@ViewInject(R.id.sina_tv)
	private TextView sina_tv;// 新浪微博绑定信息
	@ViewInject(R.id.qq_tv)
	private TextView qq_tv;// QQ绑定信息
	@ViewInject(R.id.weixin_tv)
	private TextView weixin_tv;// 微信绑定信息

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case BQQ:
				qq_tv.setText("已绑定");
				MyToastUtils.showShortToast(context, "QQ绑定成功");
				break;
			case UNBQQ:
				qq_tv.setText("未绑定");
				MyToastUtils.showShortToast(context, "解除QQ绑定成功");
				break;
			case WEIXIN:
				weixin_tv.setText("已绑定");
				MyToastUtils.showShortToast(context, "微信绑定成功");
				break;
			case UNWEIXIN:
				weixin_tv.setText("未绑定");
				MyToastUtils.showShortToast(context, "解除微信绑定成功");
				break;
			case SINA:
				sina_tv.setText("已绑定");
				MyToastUtils.showShortToast(context, "新浪微博绑定成功");
				break;
			case UNSINA:
				sina_tv.setText("未绑定");
				MyToastUtils.showShortToast(context, "解除新浪微博绑定成功");
				break;

			default:
				break;
			}
		}
	};
	private Platform weibo;
	private Platform qq;
	private Platform weixin;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accountsafe_rl1:
			intent = new Intent(AccountSafeActivity.this,
					ChangePassWordActivity.class);
			startActivity(intent);
			break;
		case R.id.third_qq:
			if ("未绑定".equals(qq_tv.getText().toString())) {
				bindqq();
			} else if ("已绑定".equals(qq_tv.getText().toString())) {
				unbindqq();
			}
			break;
		case R.id.third_wx:
			if ("未绑定".equals(weixin_tv.getText().toString())) {
				bindweixin();
			} else if ("已绑定".equals(weixin_tv.getText().toString())) {
				unbindweixin();
			}
			break;
		case R.id.third_xlwb:
			if ("未绑定".equals(sina_tv.getText().toString())) {
				bindsina();
			} else if ("已绑定".equals(sina_tv.getText().toString())) {
				unbindsina();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.accountsafe;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		context = AccountSafeActivity.this;
		setBack();
		setTopTitle("账户安全");
		ShareSDK.initSDK(context);
		inittv();
	}

	/**
	 * 初始化数据
	 */
	private void inittv() {
		weibo = ShareSDK.getPlatform(context, SinaWeibo.NAME);
		String name = weibo.getDb().getUserName().toString();
		LogUtil.i("sinaname", name);
		if (weibo.isValid()) {
			sina_tv.setText("已绑定");
		} else {
			sina_tv.setText("未绑定");
		}
		qq = ShareSDK.getPlatform(context, QQ.NAME);
		if (qq.isValid()) {
			qq_tv.setText("已绑定");
		} else {
			qq_tv.setText("未绑定");
		}
		weixin = ShareSDK.getPlatform(context, Wechat.NAME);
		if (weixin.isValid()) {
			weixin_tv.setText("已绑定");
		} else {
			weixin_tv.setText("未绑定");
		}
	}

	@Override
	public void setListener() {
		accountsafe_rl1.setOnClickListener(this);
		third_qq.setOnClickListener(this);
		third_wx.setOnClickListener(this);
		third_xlwb.setOnClickListener(this);
	}

	/**
	 * 绑定QQ
	 */
	private void bindqq() {
		qq.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				LogUtil.i("onError", "绑定失败");
			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				LogUtil.i("onComplete", "绑定成功");
				Message message = handler.obtainMessage();
				message.what = BQQ;
				handler.sendMessage(message);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				LogUtil.i("onCancel", "绑定取消");
			}
		});
		qq.authorize();
	}

	/**
	 * 解绑QQ
	 */
	private void unbindqq() {
		if (qq.isValid()) {
			qq.removeAccount();
			Message message = handler.obtainMessage();
			message.what = UNBQQ;
			handler.sendMessage(message);
		}
	}

	/**
	 * 绑定微信
	 */
	private void bindweixin() {
		weixin.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				LogUtil.i("onError", "绑定失败");
			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				LogUtil.i("onComplete", "绑定成功");
				Message message = handler.obtainMessage();
				message.what = WEIXIN;
				handler.sendMessage(message);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				LogUtil.i("onCancel", "绑定取消");
			}
		});
		weixin.authorize();
	}

	/**
	 * 解绑微信
	 */
	private void unbindweixin() {
		if (weixin.isValid()) {
			weixin.removeAccount();
			Message message = handler.obtainMessage();
			message.what = UNWEIXIN;
			handler.sendMessage(message);
		}
	}

	/**
	 * 绑定新浪微博
	 */
	private void bindsina() {
		weibo.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				LogUtil.i("onError", "绑定失败");
			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				LogUtil.i("onComplete", "绑定成功");
				Message message = handler.obtainMessage();
				message.what = SINA;
				handler.sendMessage(message);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				LogUtil.i("onCancel", "绑定取消");
			}
		});
		weibo.authorize();
	}

	/**
	 * 解绑新浪微博
	 */
	private void unbindsina() {
		if (weibo.isValid()) {
			weibo.removeAccount();
			Message message = handler.obtainMessage();
			message.what = UNSINA;
			handler.sendMessage(message);
		}
	}

}
