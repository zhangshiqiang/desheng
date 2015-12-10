package com.hanyu.desheng.utils;

import android.util.DisplayMetrics;

public class DisplayUtil {
	// 以下三个常量为基准参数，取决于开发界面时所使用的设备的屏幕参数，其余的设备都基于此参数进行适配
	public static final double BASE_WIDTH = 1080; // 屏幕的宽度
	public static final double BASE_HEIGHT = 1920; // 屏幕的高度
	public static final double BASE_DENSITYDPI = 440; // 屏幕的像素密度

	// 以下三个变量为适配比例，即需要适配的设备和基准设备的屏幕参数比例
	public static double widthRatio; // 宽度比例
	public static double heightRatio; // 高度比例
	public static double densityRatio; // 像素密度比例

	// 屏幕参数类型，分别表示宽度、高度和密度
	public enum ScaleType {
		WIDTH, HEIGHT, DENSITY
	}

	/** 初始化比例参数 */
	public static void init(DisplayMetrics dm) {
		widthRatio = dm.widthPixels / BASE_WIDTH;
		heightRatio = dm.heightPixels / BASE_HEIGHT;
//		densityRatio = BASE_DENSITYDPI / dm.densityDpi;
		densityRatio = dm.densityDpi / BASE_DENSITYDPI;
	}

	/** 根据比例参数进行计算，并返回适配后的值 */
	public static int resize(int size, ScaleType type) {
		int result = 0;
		switch (type) {
		case WIDTH:
			result = (int) (size * widthRatio);
			break;
		case HEIGHT:
			result = (int) (size * heightRatio);
			break;
		case DENSITY:
			result = (int) (size * densityRatio);
			break;
		}
		return result;
	}

}
