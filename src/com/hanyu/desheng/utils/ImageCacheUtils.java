package com.hanyu.desheng.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;


public class ImageCacheUtils {
	private static final String tag = "ImageCacheUtils";
	public static final int SUCCESS = 100;
	public static final int FAIL = 101;
	
	private LruCache<String, Bitmap> lruCache;
	private File cacheDir;
	private ExecutorService newFixedThreadPool;
	private Handler handler;
	
	public ImageCacheUtils(Context context,Handler handler) {
		int maxSize = (int) (Runtime.getRuntime().maxMemory()/8);
		this.handler = handler;
		lruCache = new LruCache<String, Bitmap>(maxSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				
				return value.getRowBytes()*value.getHeight();
			}
		};
		cacheDir = context.getCacheDir();
		
		newFixedThreadPool = Executors.newFixedThreadPool(5);
	}
	//获取图片方法,tag当前控件的唯一性标示，根据tag获取imageView，设置图片
	public Bitmap getImageBitmap(String imgUrl,int position){
		Bitmap bitmap = null;
		//1，内存
		bitmap = lruCache.get(imgUrl);
		if(bitmap!=null){
			Log.i(tag, "内存中获取到的图片");
			return bitmap;
		}
		//2,文件
		bitmap = getBitmapFromLocal(imgUrl);
		if(bitmap!=null){
			Log.i(tag, "文件中获取到的图片");
			return bitmap;
		}
		//3,网络
		getBitmapFromNet(imgUrl,position);
		Log.i(tag, "网络获取图片");
		return null;
	}
	
	private void getBitmapFromNet(String imgUrl, int position) {
		newFixedThreadPool.execute(new RunableTask(imgUrl,position));
	}
	
	class RunableTask implements Runnable{
		private String imgUrl;
		private int position;
		private Bitmap bitmap;
		
		public RunableTask(String imgUrl, int position) {
			this.imgUrl = imgUrl;
			this.position = position;
		}
		@Override
		public void run() {
			try {
				URL url = new URL(imgUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				connection.setRequestMethod("GET");
				if(connection.getResponseCode() == 200){
					InputStream inputStream = connection.getInputStream();
					bitmap = BitmapFactory.decodeStream(inputStream);
				}
				Message msg = new Message();
				msg.obj = bitmap;
				msg.what = SUCCESS;
				msg.arg1 = position;
				handler.sendMessage(msg);
				
				//文件
				writeToLocal(imgUrl,bitmap);
				//内存
				lruCache.put(imgUrl, bitmap);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
			handler.obtainMessage(FAIL).sendToTarget();
		}
	}
	private Bitmap getBitmapFromLocal(String imgUrl) {
		//读取文件的操作
		try {
			String fileName = MD5Encoder.encode(imgUrl).substring(10);
			File file = new File(cacheDir,fileName);
			Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
			lruCache.put(imgUrl, bitmap);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void writeToLocal(String imgUrl, Bitmap bitmap) {
		try {
			String fileName = MD5Encoder.encode(imgUrl).substring(10);
			File file = new File(cacheDir,fileName);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			//写入文件
			bitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
