package com.hanyu.desheng.util;

import android.util.Log;

public class LogUtils {
	private static boolean isDebug = true;
	public static void e(Class clz,String msg){
		if (isDebug) {
			Log.e(clz.getSimpleName(),msg);
		}
	}

}
