package com.hanyu.desheng;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

import com.easemob.EMCallBack;
import com.hanyu.desheng.bean.GroupBean;
import com.hanyu.desheng.bean.HxUserBean;
import com.hanyu.desheng.bean.PhoneBean;
import com.hanyu.desheng.domain.User;
import com.hanyu.desheng.easemob.chatuidemo.DemoHXSDKHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * For developer startup JPush SDK
 * 
 * 一般建议在自定义 Application 类里初始化。也可以在主 Activity 里。
 */
public class ExampleApplication extends Application {
	private static final String TAG = "JPush";
	private List<Activity> activityList = new LinkedList<Activity>();
	private static ExampleApplication instance;

	public static Context applicationContext;
	// login user name
	public final String PREF_USERNAME = "username";

	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String ACCOUNT_REMOVED = "account_removed";
	/**
	 * 数据库版本
	 */
	public static int VERSION = 33;
	public static Class<?>[] MODELS = { PhoneBean.class, HxUserBean.class,
			GroupBean.class};
	public static String DB_NAME = "desheng_.db";

	public static Map<String,String> chatmap=new HashMap<String, String>();
	@Override
	public void onCreate() {
		Log.d(TAG, "[ExampleApplication] onCreate");
		super.onCreate();
		JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
		initImageLoader(this);

		applicationContext = this;
		instance = this;
		/**
		 * this function will initialize the HuanXin SDK
		 * 
		 * @return boolean true if caller can continue to call HuanXin related
		 *         APIs after calling onInit, otherwise false.
		 * 
		 *         环信初始化SDK帮助函数
		 *         返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
		 * 
		 *         for example: 例子：
		 * 
		 *         public class DemoHXSDKHelper extends HXSDKHelper
		 * 
		 *         HXHelper = new DemoHXSDKHelper();
		 *         if(HXHelper.onInit(context)){ // do HuanXin related work }
		 */
		hxSDKHelper.onInit(applicationContext);
	}

	// 单例模式中获取唯一的MyApplication实例
	public static ExampleApplication getInstance() {
		if (null == instance) {
			instance = new ExampleApplication();
		}
		return instance;
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPoolSize(5)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 1)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		return hxSDKHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 * 
	 * @param user
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 * 
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(emCallBack);
	}
}
