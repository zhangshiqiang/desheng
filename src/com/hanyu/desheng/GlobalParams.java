package com.hanyu.desheng;

import android.graphics.Bitmap;

/**
 * 全局变量 用于判断 登录 网络状态等 如有创建虚注释清楚
 * 
 * 
 */
public class GlobalParams {

	/**
	 * 是否登录
	 */
	public static boolean isLoad = true;
	/**
	 * 是否是wap连接
	 */
	public static boolean isWap = false;
	/**
	 * 屏幕宽
	 */
	public static int screenWidth;
	/**
	 * 屏幕高
	 */
	public static int screenHeight;

	/**
	 * sessionId
	 */
	public static String sessionId = "";
	/**
	 * 用户memberid
	 */
	public static int memberid;
	/**
	 * 用户名
	 */
	public static String mname;
	/**
	 * 头像
	 */
	public static String headpic;
	/**
	 * 头像
	 */
	public static Bitmap head;
	/**
	 * 手机号
	 */
	public static String mobile = "";
	/**
	 * 纬度 31.1806063083
	 */
	public static Double latitude;
	/**
	 * 经度 121.1852003612
	 */
	public static Double longitude;
	/**
	 * 时间差
	 */
	public static long timeDelay = 0;
	
	public static int shakeRing=0;
	/**
	 * 分享状态
	 */
	public static int share;
	
	/**
	 * 状态栏
	 */
	public static int statusHeight;

}
