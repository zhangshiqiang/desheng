package com.hanyu.desheng.utils;

import android.util.Log;

public class LogUtil {
	private static boolean isOpen = true;
	public static void i(String tag,String msg){

		if(isOpen){
			Log.i(tag, msg);
		}
	}
}
