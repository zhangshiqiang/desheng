package com.hanyu.desheng.engine;


import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hanyu.desheng.GlobalParams;

/**
 * 判断网络
 * 
 * @author sto_LiHui
 * 
 */
public class NetUtil {
	// ①判断网络类型
	// ②编写网络访问的工具

	public static boolean checkNetWork(Context context) {
		// ①判断网络类型
		// 判断WIFI
		// 判断APN
		// 如果都不可以用，没有网络

		// ②处理wap方式
		// 如果APN（wap，net），如果是wap形式，获取到代理信息（10.0.0.172），如果是net没有ip和端口

		boolean isWifi = isWifiConnection(context);
		boolean isAPN = isAPNConnection(context);

		if (!isWifi && !isAPN) {
			return false;
		}

		if (isAPN) {
			// 获取代理的ip信息
			@SuppressWarnings("deprecation")
			String ip = android.net.Proxy.getDefaultHost();// 获取代理的ip信息
			if (StringUtils.isNotBlank(ip)) {
				// wap连接
				GlobalParams.isWap = true;
			}
		}

		return true;
	}

	/**
	 * 判断手机APN接入连接网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAPNConnection(Context context) {
		if (context == null) {
			return true;
		}
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);// 获取到APN连接的描述信息

		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 判断wifi连接网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnection(Context context) {
		if (context != null) {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);// 获取到wifi连接的描述信息
			
			if (networkInfo != null) {
				return networkInfo.isConnected();
			}
			
		}
		return false;
	}

}
