package com.hanyu.desheng.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类
 * 
 * @author sto_LiHui
 * 
 */
public class SPUtils {
	/**
	 * 获取是否第一次进入---key
	 */
	public final static String IS_FIRST = "isFirst";
	/**
	 * 版本---key
	 */
	public final static String VERSION_KEY = "version";
	/**
	 * SharedPreferences---名字
	 */
	public final static String SP_NAME = "config";
	/**
	 * 获取SharedPreferences
	 * 
	 * @param context
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * 存放布尔类型
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveBoolean(Context context, String key, boolean value) {
		getSharedPreferences(context).edit().putBoolean(key, value).commit();
	}

	/**
	 * 获取布尔值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		return getSharedPreferences(context).getBoolean(key, defValue);
	}

	/**
	 * 存放String值
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveString(Context context, String key, String value) {
		getSharedPreferences(context).edit().putString(key, value).commit();
	}

	/**
	 * 获取String值
	 * 
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		return getSharedPreferences(context).getString(key, defValue);
	}
    
	/**
	 * 设置
	 * @param context
	 * @param value
	 * 0是默认既有声音又有震动，1是有声音无震动，2是有震动无声音,3无震动无声音
	 */
	public static void saveSet(Context context,int value){
		getSharedPreferences(context).edit().putInt("shakeVoice",value).commit();
	}
	
	public static int getSetInt(Context context){
		return getSharedPreferences(context).getInt("shakeVoice",0);
	}
}
