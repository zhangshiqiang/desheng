package com.hanyu.desheng;

import com.easemob.EMCallBack;
import com.hanyu.desheng.activity.FeedBackActivity;
import com.hanyu.desheng.activity.GeneralizeActivity;
import com.hanyu.desheng.activity.LoginActivity;
import com.hanyu.desheng.activity.MyCodeActivity;
import com.hanyu.desheng.activity.MyOrderActivity;
import com.hanyu.desheng.activity.PersonalActivity;
import com.hanyu.desheng.activity.SettingActivity;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.bean.UserInfo;
import com.hanyu.desheng.db.InviteMessgeDao;
import com.hanyu.desheng.db.UserDao;
import com.hanyu.desheng.ui.CircleImageView;
import com.hanyu.desheng.util.ShowDialogUtil;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.YangUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

public class Left extends BaseFragment implements View.OnClickListener {
	private static final int GERENZILIAO = 1;
	private View currentView;
	private Button menu_order, menu_spread, menu_proposal, menu_exit, menu_login, menu_set;
	private CircleImageView menu_headiv;// 用户头像
	private TextView menu_text_login;
	private UserInfo userinfo;
	UserDao userDao;

	public View getCurrentView() {
		return currentView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		currentView = inflater.inflate(R.layout.left, container, false);
		menu_order = (Button) currentView.findViewById(R.id.menu_order);
		menu_spread = (Button) currentView.findViewById(R.id.menu_spread);
		menu_proposal = (Button) currentView.findViewById(R.id.menu_proposal);
		menu_headiv = (CircleImageView) currentView.findViewById(R.id.menu_headiv);
		menu_text_login = (TextView) currentView.findViewById(R.id.menu_text_login);
		menu_exit = (Button) currentView.findViewById(R.id.menu_exit);
		menu_login = (Button) currentView.findViewById(R.id.menu_login);
		menu_set = (Button) currentView.findViewById(R.id.menu_set);
		menu_login.setOnClickListener(this);
		menu_headiv.setOnClickListener(this);
		menu_set.setOnClickListener(this);
		menu_exit.setOnClickListener(this);
		menu_proposal.setOnClickListener(this);
		menu_spread.setOnClickListener(this);
		menu_order.setOnClickListener(this);
		return currentView;
	}

	@Override
	public View initView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		userDao = new UserDao(getActivity());
		if (!YangUtils.isLogin(context)) {
			menu_exit.setVisibility(View.GONE);
			// menu_login.setVisibility(View.VISIBLE);
			menu_text_login.setText("请先登录");
		} else {
			username = SharedPreferencesUtil.getStringData(context, "miname", "");
			menu_exit.setVisibility(View.VISIBLE);
			// menu_login.setVisibility(View.GONE);
			menu_text_login.setText(username);
		}

		String headpic = SharedPreferencesUtil.getStringData(context, "headpic", "");
		if (!TextUtils.isEmpty(headpic)) {
			ImageLoader.getInstance().displayImage(headpic, menu_headiv);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == 0) {
			if (data != null) {
				userinfo = (UserInfo) data.getExtras().getSerializable("userinfo");
				menu_text_login.setText(userinfo.miname);
			}
		}
	}

	@Override
	public void setListener() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 我的订单
		case R.id.menu_order:
			if (!YangUtils.isLogin(context)) {
				ShowDialogUtil.showIsLoginDialog(context);
			} else {
				intent = new Intent(context, MyOrderActivity.class);
				startActivity(intent);
			}

			break;
		// 我的推广
		case R.id.menu_spread:
			if (!YangUtils.isLogin(context)) {
				ShowDialogUtil.showIsLoginDialog(context);
			} else {
				intent = new Intent(context, GeneralizeActivity.class);
				startActivityForResult(intent, GERENZILIAO);
			}
			break;
		// 意见反馈
		case R.id.menu_proposal:
			if (!YangUtils.isLogin(context)) {
				ShowDialogUtil.showIsLoginDialog(context);
			} else {
				intent = new Intent(context, FeedBackActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.menu_set:
			intent = new Intent(context, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_headiv:
			if (!YangUtils.isLogin(context)) {
				ShowDialogUtil.showIsLoginDialog(context);
			} else {
				intent = new Intent(context, PersonalActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.menu_login:
			intent = new Intent(context, MyCodeActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_exit:
			/**
			 * 退出环信
			 */
			ExampleApplication.getInstance().logout(new EMCallBack() {

				@Override
				public void onSuccess() {
					if (getActivity() == null) {
						return;
					}
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							JPushInterface.stopPush(getActivity().getApplicationContext());
							Boolean isLoad = false;

							userDao.deleteAll();
							InviteMessgeDao dao = new InviteMessgeDao(getActivity());
							dao.deleteAllMessage();

							SharedPreferencesUtil.saveBooleanData(context, "isLoad", isLoad);
							context.sendBroadcast(new Intent(Left.UPDATA_CENTER));
							boolean isMes = SharedPreferencesUtil.getMes(getActivity());
							SharedPreferencesUtil.ClearData(context);
							SharedPreferencesUtil.setMes(getActivity(), isMes);
							intent = new Intent(context, LoginActivity.class);
							startActivity(intent);
							getActivity().finish();
						}
					});
				}

				@Override
				public void onProgress(int progress, String status) {

				}

				@Override
				public void onError(int code, String message) {

				}
			});
			break;
		default:
			break;
		}
	}

	private MyBroadCastReceiver receiver;
	public final static String UPDATA_CENTER = "com.hanyu.desheng.updatefragment";
	private Boolean isload;
	private String username;

	private class MyBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			// 更新登录状态
			isload = SharedPreferencesUtil.getBooleanData(context, "isLoad", false);
			if (isload.booleanValue() == false) {
				menu_exit.setVisibility(View.GONE);
				// menu_login.setVisibility(View.VISIBLE);
				menu_text_login.setText("请先登录");
			} else if (isload.booleanValue() == true) {
				menu_exit.setVisibility(View.VISIBLE);
				// menu_login.setVisibility(View.GONE);
				username = SharedPreferencesUtil.getStringData(context, "miname", "");
				if (!TextUtils.isEmpty(username)) {
					menu_text_login.setText(username);
				} else {
					menu_text_login.setText("您还没有设置用户名");
				}
			}

			String headpic = SharedPreferencesUtil.getStringData(context, "headpic", "");
			if (!TextUtils.isEmpty(headpic)) {
				ImageLoader.getInstance().displayImage(headpic, menu_headiv);
			}
		}

	}

}