package com.hanyu.desheng.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.MeasureSpec;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Android----工具类
 * 
 * @author yang
 * 
 */
public class YangUtils {
	public static int QR_WIDTH = 400;
	public static int QR_HEIGHT = 400;

	/**
	 * 获得手机唯一IMEI
	 */
	public static String getPhoneIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String IMEI = telephonyManager.getDeviceId();
		return IMEI;
	}

	/**
	 * 判断是否登录
	 * 
	 * @return
	 */
	public static boolean isLogin(Context context) {
		SharedPreferences sp = context.getSharedPreferences("config", 0);
		String hx_name = sp.getString("hx_name", "");
		if (TextUtils.isEmpty(hx_name)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取手机屏幕宽
	 * 
	 * @param activity
	 */
	public static int getScreenWidth(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// int width = metrics.widthPixels;// 屏幕的宽
		// int height = metrics.heightPixels;// 屏幕的高
		return metrics.widthPixels;
	}

	/**
	 * 获取手机屏幕高
	 * 
	 * @param activity
	 */
	public static int getScreenHeight(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// int width = metrics.widthPixels;// 屏幕的宽
		// int height = metrics.heightPixels;// 屏幕的高
		return metrics.heightPixels;

	}

	/**
	 * 获取当前屏幕旋转角度
	 * 
	 * @param activity
	 * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
	 */
	public static int getScreenRotationOnPhone(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		// final Display display = ((WindowManager)
		// context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			return 0;

		case Surface.ROTATION_90:
			return 90;

		case Surface.ROTATION_180:
			return 180;

		case Surface.ROTATION_270:
			return -90;
		}
		return 0;
	}

	/**
	 * 判断当前是否有可用的网络以及网络类型 0：无网络 1：WIFI 2：CMWAP 3：CMNET
	 * 
	 * @param context
	 * @return
	 */
	public static int isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return 0;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						NetworkInfo netWorkInfo = info[i];
						if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							return 1;
						} else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
							String extraInfo = netWorkInfo.getExtraInfo();
							if ("cmwap".equalsIgnoreCase(extraInfo)
									|| "cmwap:gsm".equalsIgnoreCase(extraInfo)) {
								return 2;
							}
							return 3;
						}
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 将指定byte数组转换成16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * 
	 * @param context
	 *            上下文
	 * @param dpValue
	 *            dp值
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 * 
	 * @param context
	 *            上下文
	 * @param pxValue
	 *            像素值
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 生成0-9的随机数
	 * 
	 * @param count
	 *            生成几位数（6位）
	 * @return random 随机数
	 */
	@SuppressWarnings("unused")
	public static String getRandomNumber(int count) {
		Random rdm = new Random(System.currentTimeMillis());
		String random = "";
		for (int i = 0; i < count; i++) {
			String str = String.valueOf((int) (Math.random() * 10 - 1));
			random = random + str;
		}
		return random;
	}

	/***
	 * 获取MAC地址
	 * 
	 * @return
	 */
	public static String getMacAddress(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo.getMacAddress() != null) {
			return wifiInfo.getMacAddress();
		} else {
			return "";
		}
	}

	/**
	 * 获取运行时间
	 * 
	 * @return 运行时间(单位/s)
	 */
	public static long getRunTimes() {
		long ut = SystemClock.elapsedRealtime() / 1000;
		if (ut == 0) {
			ut = 1;
		}
		return ut;
	}

	/**
	 * 获取当前版本号
	 * 
	 * @return
	 */
	public static String getVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			return "";
		}
	}

	// 要转换的地址或字符串,可以是中文
	public static Bitmap createQRImage(String url) {
		Bitmap bitmap = null;
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static void saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(),
				"desheng");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			MyToastUtils.showShortToast(context, "保存失败");
			e.printStackTrace();
		} catch (IOException e) {
			MyToastUtils.showShortToast(context, "保存失败");
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
			MyToastUtils.showShortToast(context, "保存成功");
		} catch (FileNotFoundException e) {
			MyToastUtils.showShortToast(context, "保存失败");
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.fromFile(new File(file.getPath()))));
	}

	// private Bitmap getimage(String srcPath,int i) {
	// BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
	// newOpts.inJustDecodeBounds = true;
	// Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
	// // 方法1 Android获得屏幕的宽和高
	// WindowManager windowManager =getActivity().getWindowManager();
	// Display display = windowManager.getDefaultDisplay();
	// int screenWidth = screenWidth = display.getWidth();
	// int screenHeight = screenHeight = display.getHeight();
	// newOpts.inJustDecodeBounds = false;
	// int w = newOpts.outWidth;
	// int h = newOpts.outHeight;
	// // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
	// float hh = screenHeight;// 这里设置高度为800f
	// float ww = screenWidth;// 这里设置宽度为480f
	// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	// int be = 1;// be=1表示不缩放
	// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
	// be = (int) (newOpts.outWidth / ww);
	// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
	// be = (int) (newOpts.outHeight / hh);
	// }
	// if (be <= 0)
	// be = 1;
	// newOpts.inSampleSize = be;// 设置缩放比例
	// // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	// bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
	// return compressImage(bitmap,i);// 压缩好比例大小后再进行质量压缩
	// }

	public static Bitmap getBitmapFromView(View contentLayout) {
		contentLayout.setDrawingCacheEnabled(true);
//		contentLayout.measure(
//				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//		contentLayout.layout(0, 0, contentLayout.getMeasuredWidth(), contentLayout.getMeasuredHeight());
		contentLayout.buildDrawingCache();
		Bitmap bmp = contentLayout.getDrawingCache();
		return bmp;
	}
}
