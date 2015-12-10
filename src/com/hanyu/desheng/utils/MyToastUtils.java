package com.hanyu.desheng.utils;

import android.content.Context;
import android.widget.Toast;

import com.hanyu.desheng.engine.ConstantValue;

/**
 * Toast提示工具
 * 
 * 
 */
public class MyToastUtils {
	private static Toast mToast;  
	/**
	 * 显示时间短的吐司---调试用
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShortDebugToast(Context context, String message) {
		if (ConstantValue.IS_SHOW_DEBUG) {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 显示时间长的吐司---调试用
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLongDebugToast(Context context, String message) {
		if (ConstantValue.IS_SHOW_DEBUG) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 显示时间短的吐司
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShortToast(Context context, String message) {
		 if(mToast == null) {  
	            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);  
	        } else {  
	            mToast.setText(message);         
	            mToast.setDuration(Toast.LENGTH_SHORT);  
	        }  
	        mToast.show();  
	}

	/**
	 * 显示时间长的吐司
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLongToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

}
