/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hanyu.desheng.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.easemob.chat.EMChatConfig;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.HuanXinBaseActivity;
import com.hanyu.desheng.easemob.chatuidemo.widget.photoview.PhotoView;
import com.hanyu.desheng.task.LoadLocalBigImgTask;
import com.hanyu.desheng.utils.ImageCache;
import com.hanyu.desheng.utils.SavePicPopupWindow;
import com.hanyu.desheng.utils.YangUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 下载显示大图
 * 
 */
public class ShowBigImage extends HuanXinBaseActivity {

	private ProgressDialog pd;
	private PhotoView image;
	private int default_res = R.drawable.default_image;
	private String localFilePath;
	private Bitmap bitmap;
	private boolean isDownloaded;
	private ProgressBar loadLocalPb;
	private SavePicPopupWindow menuWindow; // 自定义编辑弹出框

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_big_image);
		

		image = (PhotoView) findViewById(R.id.image);
		loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
		default_res = getIntent().getIntExtra("default_image", R.drawable.default_avatar);
		Uri uri = getIntent().getParcelableExtra("uri");
		String remotepath = getIntent().getExtras().getString("remotepath");
		String secret = getIntent().getExtras().getString("secret");
		System.err.println("show big image uri:" + uri + " remotepath:" + remotepath);

		//本地存在，直接显示本地的图片
		if (uri != null && new File(uri.getPath()).exists()) {
			System.err.println("showbigimage file exists. directly show it");
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenWidth = metrics.widthPixels;
			// int screenHeight =metrics.heightPixels;
			bitmap = ImageCache.getInstance().get(uri.getPath());
			if (bitmap == null) {
				LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
						ImageUtils.SCALE_IMAGE_HEIGHT);
				if (android.os.Build.VERSION.SDK_INT > 10) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					task.execute();
				}
			} else {
				image.setImageBitmap(bitmap);
			}
		} else if (remotepath != null) { //去服务器下载图片
			System.err.println("download remote image");
			Map<String, String> maps = new HashMap<String, String>();
			if (!TextUtils.isEmpty(secret)) {
				maps.put("share-secret", secret);
			}
			downloadImage(remotepath, maps);
		} else {
			image.setImageResource(default_res);
		}

		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		image.setOnLongClickListener(new OnLongClickListener() {
			
			private AlertDialog dialog;

			@Override
			public boolean onLongClick(View v) {
//				Builder builder = new AlertDialog.Builder(ShowBigImage.this);
//				builder.setItems(new String[]{"保存到本地"}, new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						if (which == 0) {
//							saveImageToLocal();
//						}
//					}
//				});
//				dialog = builder.show();
				Log.e("111", "Long Click22222222222222222222");
				menuWindow = new SavePicPopupWindow(ShowBigImage.this, itemsOnClick);
				menuWindow.showAtLocation(findViewById(R.id.rl_container),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

				return true;
			}
		});
	}
	
	// 为弹出窗口实现监听类
		private OnClickListener itemsOnClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 隐藏弹出窗口
				menuWindow.dismiss();

				switch (v.getId()) {
				case R.id.save_pic:// 保存图片到本地
					Bitmap bitmapFromView = YangUtils.getBitmapFromView(image);
					YangUtils.saveImageToGallery(ShowBigImage.this, bitmapFromView);
					break;
				case R.id.cancel_Btn:// 取消
					break;
				default:
					break;
				}
			}
		};
	
	private String downloadPath = Environment.getExternalStorageDirectory()+"/download";
	private void saveImageToLocal(){
		UUID uuid = UUID.randomUUID();
		File mkdir = new File(downloadPath);
		if (!mkdir.exists()) {
			mkdir.mkdirs();
		}
		File path = new File(mkdir,uuid.toString()+".png");
		Log.e("path", path.getAbsolutePath());
		FileOutputStream fos = null;
		try {
			image.setDrawingCacheEnabled(true);
//			Bitmap image1 = image.getDrawingCache();
			Bitmap image1 = Bitmap.createBitmap(image.getDrawingCache());
			image.setDrawingCacheEnabled(false);
			fos = new FileOutputStream(path);
			image1.compress(CompressFormat.PNG, 100, fos);
			fos.write(0);
			fos.flush();
			Toast.makeText(ShowBigImage.this, "图片保存在:"+path, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(ShowBigImage.this, "保存失败", Toast.LENGTH_SHORT).show();
		}finally{
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * 通过远程URL，确定下本地下载后的localurl
	 * @param remoteUrl
	 * @return
	 */
	public String getLocalFilePath(String remoteUrl){
		String localPath;
		if (remoteUrl.contains("/")){
			localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
					+ remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
		}else{
			localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteUrl;
		}
		return localPath;
	}
	
	/**
	 * 下载图片
	 * 
	 * @param remoteFilePath
	 */
	private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
		String str1 = getResources().getString(R.string.Download_the_pictures);
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage(str1);
		pd.show();
		localFilePath = getLocalFilePath(remoteFilePath);
		final HttpFileManager httpFileMgr = new HttpFileManager(this, EMChatConfig.getInstance().getStorageUrl());
		final CloudOperationCallback callback = new CloudOperationCallback() {
			public void onSuccess(String resultMsg) {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;

						bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
						if (bitmap == null) {
							image.setImageResource(default_res);
						} else {
							image.setImageBitmap(bitmap);
							ImageCache.getInstance().put(localFilePath, bitmap);
							isDownloaded = true;
						}
						if (pd != null) {
							pd.dismiss();
						}
					}
				});
			}

			public void onError(String msg) {
				Log.e("###", "offline file transfer error:" + msg);
				File file = new File(localFilePath);
				if (file.exists()&&file.isFile()) {
					file.delete();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
						image.setImageResource(default_res);
					}
				});
			}

			public void onProgress(final int progress) {
				Log.d("ease", "Progress: " + progress);
				final String str2 = getResources().getString(R.string.Download_the_pictures_new);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						pd.setMessage(str2 + progress + "%");
					}
				});
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				httpFileMgr.downloadFile(remoteFilePath, localFilePath, headers, callback);
			}
		}).start();
	}

	@Override
	public void onBackPressed() {
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}
}
