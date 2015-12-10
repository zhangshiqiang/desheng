package com.hanyu.desheng.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
	/**
	 * 获取是否第一次进入---key
	 */
	public final static String IS_FIRST = "isFirst";
	private static String CONFIG = "config";
	private static SharedPreferences sharedPreferences;

	// 写入
	public static void saveStringData(Context context, String key, String value) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putString(key, value).commit();
	}

	// 写入
	public static void saveIntData(Context context, String key, int value) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putInt(key, value).commit();
	}

	// 写入
	public static void saveBooleanData(Context context, String key,
			boolean value) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putBoolean(key, value).commit();
	}

	// 读出
	public static String getStringData(Context context, String key,
			String defValue) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		return sharedPreferences.getString(key, defValue);
	}

	// 读出
	public static int getIntData(Context context, String key, int defValue) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		return sharedPreferences.getInt(key, defValue);
	}

	// 读出
	public static boolean getBooleanData(Context context, String key,
			boolean defValue) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		return sharedPreferences.getBoolean(key, defValue);
	}
	
	public static String VSPID="vspid";
	
	// 写入
		public static void saveStringVspid(Context context,String value) {
			if (sharedPreferences == null) {
				sharedPreferences = context.getSharedPreferences(CONFIG,
						Context.MODE_PRIVATE);
			}
			sharedPreferences.edit().putString(VSPID,value).commit();
		}
		
		public static String getStringVspid(Context context) {
			if (sharedPreferences == null) {
				sharedPreferences = context.getSharedPreferences(CONFIG,
						Context.MODE_PRIVATE);
			}
			return sharedPreferences.getString(VSPID, "");
		}
		
		private static String ISMES="ismes";
		public static void setMes(Context context,boolean isMes){
			if (sharedPreferences == null) {
				sharedPreferences = context.getSharedPreferences(CONFIG,
						Context.MODE_PRIVATE);
			}
			sharedPreferences.edit().putBoolean(ISMES, isMes).commit();
		}
		
		public static boolean getMes(Context context){
			if (sharedPreferences == null) {
				sharedPreferences = context.getSharedPreferences(CONFIG,
						Context.MODE_PRIVATE);
			}
				return sharedPreferences.getBoolean(ISMES, true);
		}
		

	// 清理sp
	public static boolean ClearData(Context context) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		return sharedPreferences.edit().clear().commit();
	}
}
