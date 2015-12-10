package com.hanyu.desheng.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.hanyu.desheng.R;
import com.hanyu.desheng.util.LogUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UpdateAppService extends Service {

	private Context context;
	private Notification notification;
	private NotificationManager nManager;
	private PendingIntent pendingIntent;
	private Intent install;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		CreateInform();
		return super.onStartCommand(intent, flags, startId);
	}

	private String downloadUrl;
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		downloadUrl = intent.getStringExtra("downloadUrl");
		Log.e("1111", "downloadUrl:"+intent.getStringExtra("downloadUrl"));
		
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// 创建通知
	public void CreateInform() {
		install = new Intent(Intent.ACTION_VIEW);
//		install.setDataAndType(Uri.fromFile(new File(getSDCardPath() + "/desheng"
//				+ "/desheng.apk")), "application/vnd.android.package-archive");
		pendingIntent = PendingIntent.getActivity(context, 0, install, 0);
		// 创建一个通知
		notification = new Notification(R.drawable.down_not_icon, "开始下载德升新版本~~",
				System.currentTimeMillis());
		notification.setLatestEventInfo(context, "正在下载德升~", "点击查看详细内容",
				pendingIntent);
		nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nManager.notify(100, notification);
		new Thread(new updateRunnable()).start();
	}

	class updateRunnable implements Runnable {
		int downnum = 0;
		int downcount = 0;

		@Override
		public void run() {
			try {
				
				DownLoadApp(downloadUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void DownLoadApp(String urlString) throws Exception {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			if (urlConnection.getResponseCode() != 200) {
				Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
				stopSelf();
				return;
			}
			int length = urlConnection.getContentLength();
			InputStream inputStream = urlConnection.getInputStream();
			OutputStream outputStream = new FileOutputStream(getFile());
			byte buffer[] = new byte[1024 * 3];
			int readsize = 0;
			while((readsize = inputStream.read(buffer))!=-1){
				outputStream.write(buffer, 0, readsize);
				downnum += readsize;
				notification.icon = R.drawable.down_icon;
				if ((downcount == 0)
						|| (int) (downnum * 100 / length) - 1 > downcount) {
					downcount += 1;
					notification.setLatestEventInfo(context, "正在下载德升最新版本",
							"已下载" + (int) downnum * 100 / length + "%", null);
					nManager.notify(100, notification);
				}
				if (downnum == length) {
					notification.setLatestEventInfo(context, "已下载完成~", "点击安装",
							pendingIntent);
//					nManager.notify(100, notification);
					nManager.cancel(100);
					
				}
			}
			installApk(getFile());
			stopSelf();
			
			inputStream.close();
			outputStream.close();
		}

		// 获取文件的保存路径
		public File getFile() throws Exception {
			String SavePath = getSDCardPath() + "/desheng";
			File path = new File(SavePath);
			File file = new File(SavePath + "/desheng.apk");
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			return file;
		}

	}
	
	/**
	 * 安装apk
	 */
	private void installApk(File file) {
		// 获取当前sdcard存储路径
		File apkfile = file;
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 安装，如果签名不一致，可能出现程序未安装提示
		i.setDataAndType(Uri.fromFile(new File(apkfile.getAbsolutePath())), "application/vnd.android.package-archive");
		startActivity(i);
	}

	private String getSDCardPath() {
		File sdcardDir = null;
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}
}
