package com.hanyu.desheng.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.hanyu.desheng.MainActivity;
import com.hanyu.desheng.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

/**
 * APK更新管理类
 * 
 * @author Royal
 * 
 */
public class UpdateManager {

	private static final int NOTIFICATION_ID = 0x12;  
	// 上下文对象
	private Context mContext;
	// 下载进度条
	private ProgressBar progressBar;
	// 是否终止下载
	private boolean isInterceptDownload = false;
	// 进度条显示数值
	private int progress = 0;
	private String versionName;
	private String downloadUrl;

	/**
	 * 参数为Context(上下文activity)的构造函数
	 * 
	 * @param context
	 */
	public UpdateManager(Context context,String versionName,String downloadUrl) {
		this.mContext = context;
		this.versionName = versionName;
		this.downloadUrl = downloadUrl;
		
		notification = new Notification();  
        notification.contentView = new RemoteViews(((Activity) context).getApplication().getPackageName(), R.layout.download_view);  
        notification.contentView.setProgressBar(R.id.progressBar1, 100, 0, false);  
        notification.contentIntent = PendingIntent.getActivity(context, 0,new Intent(context, MainActivity.class), 0);  
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
	}

	/**
	 * 下载apk
	 */
	public void downloadApk(String downloadUrl) {
		// 开启另一线程下载
		Thread downLoadThread = new Thread(downApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 从服务器下载新版apk的线程
	 */
	private Runnable downApkRunnable = new Runnable() {
		@Override
		public void run() {
			if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				// 如果没有SD卡
				Builder builder = new Builder(mContext);
				builder.setTitle("提示");
				builder.setMessage("当前设备无SD卡，数据无法下载");
				builder.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.show();
				return;
			} else {
				FileOutputStream fos = null;
				try {
					// 服务器上新版apk地址
					URL url = new URL(downloadUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();
					File file = new File(
							Environment.getExternalStorageDirectory().getAbsolutePath() + "/deshengApk/");
					if (!file.exists()) {
						// 如果文件夹不存在,则创建
						file.mkdir();
					}
					// 下载服务器中新版本软件（写文件）
					String apkFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/deshengApk/"
							+ versionName;
					File ApkFile = new File(apkFile);
					fos = new FileOutputStream(ApkFile);
					int count = 0;
					byte buf[] = new byte[1024];
					manager.notify(NOTIFICATION_ID, notification);
					do {
						int numRead = is.read(buf);
						count += numRead;
						// 更新进度条
						progress = (int) (((float) count / length) * 100);
						handler.sendEmptyMessage(1);
						if (numRead <= 0) {
							// 下载完成通知安装
							handler.sendEmptyMessage(0);
							break;
						}
						fos.write(buf, 0, numRead);
						// 当点击取消时，则停止下载
					} while (!isInterceptDownload);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					if (fos!=null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	};

	/**
	 * 声明一个handler来跟进进度条
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 更新进度情况
				notification.contentView.setProgressBar(R.id.progressBar1, 100, msg.arg1, false);  
	            notification.contentView.setTextViewText(R.id.textView1, "进度" + msg.arg1 + "%");  
	            manager.notify(NOTIFICATION_ID, notification);  
				
//				progressBar.setProgress(progress);
				break;
			case 0:
//				progressBar.setVisibility(View.INVISIBLE);
				// 安装apk文件
				installApk();
				break;
			default:
				break;
			}
		};
	};
	private NotificationManager manager;
	private Notification notification;

	/**
	 * 安装apk
	 */
	private void installApk() {
		// 获取当前sdcard存储路径
		File apkfile = new File(
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/deshengApk/" + versionName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		// 安装，如果签名不一致，可能出现程序未安装提示
		i.setDataAndType(Uri.fromFile(new File(apkfile.getAbsolutePath())), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}

}
