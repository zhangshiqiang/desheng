package com.hanyu.desheng.base;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EasyUtils;
import com.easemob.util.HanziToPinyin;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.ExitApplication;
import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.MyOrderActivity;
import com.hanyu.desheng.bean.HxUserBean;
import com.hanyu.desheng.db.HxUserDao;
import com.hanyu.desheng.db.UserDao;
import com.hanyu.desheng.domain.User;
import com.hanyu.desheng.fragment.MainFragment;
import com.hanyu.desheng.utils.CommonUtils;
import com.lidroid.xutils.ViewUtils;

/**
 * 界面基类----所有activity需继承此类
 * 
 * @author
 * 
 */
public abstract class BaseActivity extends FragmentActivity implements OnClickListener {
	protected RelativeLayout back;
	private TextView title;
	private CheckBox select;
	public SharedPreferences sp;
	public Intent intent;
	public Context context;
	public static boolean isback_shop = false;
	private static final int notifiId = 11;
	protected NotificationManager notificationManager;
	UserDao userDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 手机窗口设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置填充的layout
		setContentView(setLayout());
		// 注入控件
		ViewUtils.inject(this);
		init(savedInstanceState);
		setListener();
		ExitApplication.getInstance().addActivity(this);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (userDao == null)
			userDao = new UserDao(getApplicationContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
		// onresume时，取消notification显示
		EMChatManager.getInstance().activityResumed();
		// umeng
		// MobclickAgent.onResume(this);
	}

	/**
	 * 设置界面加载的Layout
	 * 
	 * @return
	 */
	public abstract int setLayout();

	/**
	 * 初始化数据
	 */
	public abstract void init(Bundle savedInstanceState);

	/**
	 * 对view设置监听事件
	 */
	public abstract void setListener();

	/**
	 * 设置头部标题
	 * 
	 * @param str
	 */
	public void setTopTitle(String str) {
		title = (TextView) findViewById(R.id.tv_title);
		if (str != null) {
			title.setText(str);
		}

	}

	/**
	 * 返回键
	 */
	public void setBack() {

		back = (RelativeLayout) findViewById(R.id.rl_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isback_shop == true) {
					isback_shop=false;
					Intent intent = new Intent(MainFragment.CHANGETAB);
					intent.putExtra("tab", 2);
					sendBroadcast(intent);
				} else {
					hideSoftInput();
				}
				finish();
			}
		});
	}

	public void hideSoftInput() {

	}

	/**
	 * 设置右边button的名字和监听器
	 * 
	 * @param text
	 *            名字
	 * @param listener
	 *            监听器
	 */
	public void setRightButton(String text, OnClickListener listener) {

		TextView btn = (TextView) findViewById(R.id.tv_right);
		btn.setVisibility(View.VISIBLE);
		if (!TextUtils.isEmpty(text)) {
			btn.setText(text);
		}
		if (listener != null) {
			btn.setOnClickListener(listener);
		}
	}

	/**
	 * 设置选择器的图片和监听器
	 * 
	 * @param resource
	 *            图片ID
	 * @param listener
	 *            监听器
	 */
	@SuppressLint("NewApi")
	public void setRightSelector(int resource, OnCheckedChangeListener listener) {
		select = (CheckBox) findViewById(R.id.cb_right);
		select.setVisibility(View.VISIBLE);
		select.setButtonDrawable(resource);
		if (listener != null) {
			select.setOnCheckedChangeListener(listener);
		}
	}

	/**
	 * 
	 * @param resouce
	 *            图片地址1,右距10dp
	 * @param listener
	 */
	public void setRightIv(int resource, OnClickListener listener) {
		RelativeLayout rl_right = (RelativeLayout) findViewById(R.id.rl_right);
		ImageView iv = (ImageView) findViewById(R.id.ivRight);
		rl_right.setVisibility(View.VISIBLE);
		iv.setBackgroundResource(resource);
		if (listener != null) {
			rl_right.setOnClickListener(listener);
		}
	}

	/**
	 * 
	 * @param resouce
	 *            图片地址2,右距35dp
	 * @param listener
	 */
	public void setRightIv2(int resource, OnClickListener listener) {
		RelativeLayout rl_right = (RelativeLayout) findViewById(R.id.rl_right3);
		ImageView iv = (ImageView) findViewById(R.id.ivRight3);
		rl_right.setVisibility(View.VISIBLE);
		iv.setBackgroundResource(resource);
		if (listener != null) {
			rl_right.setOnClickListener(listener);
		}
	}

	/**
	 * @param text
	 *            名字
	 * @param resouce
	 *            图片地址
	 * @param listener
	 */
	public void setRightIv2(String text, int resource, OnClickListener listener) {
		RelativeLayout rl_right = (RelativeLayout) findViewById(R.id.rl_right2);
		ImageView iv = (ImageView) findViewById(R.id.ivRight2);
		TextView tv = (TextView) findViewById(R.id.tv_right2);
		rl_right.setVisibility(View.VISIBLE);
		tv.setText(text);
		iv.setBackgroundResource(resource);
		if (listener != null) {
			rl_right.setOnClickListener(listener);
		}
	}

	/**
	 * 获取上下文
	 * 
	 * @return
	 */
	public Context getContext() {
		return this;
	}

	/**
	 * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下 如果不需要，注释掉即可
	 * 
	 * @param message
	 */
	protected void notifyNewMessage(EMMessage message) {
		// 如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
		// 以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
		if (!EasyUtils.isAppRunningForeground(this)) {
			return;
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(getApplicationInfo().icon).setWhen(System.currentTimeMillis()).setAutoCancel(true);

		String ticker = CommonUtils.getMessageDigest(message, this);
		String st = getResources().getString(R.string.expression);
		if (message.getType() == Type.TXT)
			ticker = ticker.replaceAll("\\[.{2,3}\\]", st);
		HxUserBean user = new HxUserBean();
		try {
			String user_nickname = message.getStringAttribute("user_nickname");
			String user_avatar = message.getStringAttribute("user_avatar");
			String hx_username = message.getFrom();
			user.hx_username = hx_username;
			user.headpic = user_avatar;
			user.username = user_nickname;
			// user.setUsername(hx_username);
			// user.setAvatar(user_avatar);
			// user.setNick(user_nickname);
			// setUserHearder(user_nickname, user);

		} catch (EaseMobException e) {
			e.printStackTrace();
		}
		if (HxUserDao.findByHx(message.getFrom()) == null) {
			HxUserDao.insertHxUser(user);
		} else {
			HxUserDao.updateUser(user);
		}
		// if(userDao.getContactList().get(message.getFrom())==null){
		// userDao.saveContact(user);
		// }
		// 设置状态栏提示
		mBuilder.setTicker(user.username + ": " + ticker);

		// 必须设置pendingintent，否则在2.3的机器上会有bug
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId, intent, PendingIntent.FLAG_ONE_SHOT);
		mBuilder.setContentIntent(pendingIntent);

		Notification notification = mBuilder.build();
		notificationManager.notify(notifiId, notification);
		notificationManager.cancel(notifiId);

	}

	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(ExampleApplication.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
					.toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
}
