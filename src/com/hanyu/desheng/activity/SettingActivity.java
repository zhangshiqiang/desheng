package com.hanyu.desheng.activity;

import com.easemob.chat.EMChatManager;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.HomeBean;
import com.hanyu.desheng.engine.DSUrlManager;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.util.ShowDialogUtil;
import com.hanyu.desheng.utils.DataCleanManager;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.YangUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends BaseActivity {
	@ViewInject(R.id.setting_rl1)
	private RelativeLayout setting_rl1;// 检测更新
	@ViewInject(R.id.setting_rl2)
	private RelativeLayout setting_rl2;// 关于我们
	@ViewInject(R.id.setting_rl3)
	private RelativeLayout setting_rl3;// 消息设置
	@ViewInject(R.id.setting_rl4)
	private RelativeLayout setting_rl4;// 账户安全
	@ViewInject(R.id.setting_rl5)
	private RelativeLayout setting_rl5;// 清除缓存
	@ViewInject(R.id.setting_tv1)
	private TextView setting_tv1;// 检测更新文本
	@ViewInject(R.id.setting_tv2)
	private TextView setting_tv2;// 清除缓存文本

	@ViewInject(R.id.ivMes)
	private ImageView ivMes;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_rl1:
			// TODO 检测更新
			getToppic("0");
			break;
		case R.id.setting_rl2:
			if (!YangUtils.isLogin(context)) {
				ShowDialogUtil.showIsLoginDialog(context);
			} else {
				intent = new Intent(SettingActivity.this, AboutUSActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.setting_rl3:
			intent = new Intent(SettingActivity.this, MessageSettingActivity.class);
			startActivity(intent);
			break;
		case R.id.setting_rl4:
			if (!YangUtils.isLogin(context)) {
				ShowDialogUtil.showIsLoginDialog(context);
			} else {
				intent = new Intent(SettingActivity.this, AccountSafeActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.setting_rl5:
			ImageLoader.getInstance().clearDiskCache();
			ImageLoader.getInstance().clearMemoryCache();
			DataCleanManager.clearAllCache(context);
			setting_tv2.setText("");
			break;
		case R.id.ivMes:
			if (SharedPreferencesUtil.getMes(this)) {
				ivMes.setImageResource(R.drawable.kg_normal);
				SharedPreferencesUtil.setMes(getApplicationContext(), false);
				EMChatManager.getInstance().getChatOptions().setNotifyBySoundAndVibrate(false);
			} else {
				ivMes.setImageResource(R.drawable.kg_check);
				SharedPreferencesUtil.setMes(getApplicationContext(), true);
				EMChatManager.getInstance().getChatOptions().setNotifyBySoundAndVibrate(true);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 从网络获取图片地址
	 * 
	 * @param i
	 */
	private void getToppic(final String page_no) {
		new HttpTask<Void, Void, String>(context) {
			@Override
			protected String doInBackground(Void... params) {
				String user_id = "";
				String result = "";
				if (YangUtils.isLogin(context)) {
					user_id = SharedPreferencesUtil.getStringData(context, "memberid", "");
					result = EngineManager.getUserEngine().getTopPic(user_id, page_no);
				} else {
					result = EngineManager.getUserEngine().getTopPic("", page_no);
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					HomeBean homeBean = GsonUtils.json2Bean(result, HomeBean.class);
					// 检测版本更新
					checkUpdate(homeBean);
				}
			}

			protected void onPreExecute() {

			}
		}.executeProxy();
	}

	private AlertDialog updateDialog;

	/**
	 * 检查更新
	 */
	public void checkUpdate(HomeBean homeBean) {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			String versionName = info.versionName;
			if (!versionName.equals(homeBean.data.andriod.version)) {
				setting_tv1.setText("发现新版本" + homeBean.data.andriod.version);
				showUpdateDialog(homeBean.data.andriod.version,
						new DSUrlManager().getFullUrl2(homeBean.data.andriod.apk_url));
			} else {
				Toast.makeText(this, "您已经是最新版本了！", Toast.LENGTH_SHORT).show();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	//获取apk的versionCode，即AndroidManifest.xml中定义的android:versionCode
	public int getVersionCode(Context context) {
	    int versionCode = 0;
	    try {
	        versionCode = context.getPackageManager().getPackageInfo("net.vpntunnel", 0).versionCode;
	    } catch (NameNotFoundException e) {
//	        Log.e(TAG, e.getMessage());
	    }
	 
	    return versionCode;
	}

	public void showUpdateDialog(final String versionName, final String downloadUrl) {
		Builder builder = new android.app.AlertDialog.Builder(context);
		builder.setTitle("发现新版本!");
		builder.setMessage("新版本" + versionName + ",是否更新");
		builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateDialog.dismiss();
			}

		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateDialog.dismiss();
				// UpdateManager update = new UpdateManager(context,
				// versionName, downloadUrl);
				// update.downloadApk(downloadUrl);
				Uri uri = Uri.parse(
						"http://storage3.pgyer.com/M02/05/3F/wKgBfVZSy2qAZdKjARcQTI9SmZA127.apk?auth_key=48e0c6fb48a3f0955ee3dca284557242-1448420727&attname=desheng%281%29.apk");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);

			}
		});
		updateDialog = builder.show();

	}

	@Override
	public int setLayout() {
		return R.layout.setting;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		if (SharedPreferencesUtil.getMes(this)) {
			ivMes.setImageResource(R.drawable.kg_check);
		} else {
			ivMes.setImageResource(R.drawable.kg_normal);
		}
		setBack();
		setTopTitle("设置");
		context = SettingActivity.this;
		String cachesize;
		try {
			cachesize = DataCleanManager.getTotalCacheSize(SettingActivity.this);
			if (cachesize != null) {
				setting_tv2.setText(cachesize);
			} else {
				setting_tv2.setText("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setListener() {
		setting_rl1.setOnClickListener(this);
		setting_rl2.setOnClickListener(this);
		setting_rl3.setOnClickListener(this);
		setting_rl4.setOnClickListener(this);
		setting_rl5.setOnClickListener(this);
		ivMes.setOnClickListener(this);
	}

	/**
	 * 检测更新
	 */
	// private void updateVersion(){
	// if(gUtil==null){
	// gUtil=new GeneralUtil(this);
	// }
	// Version version=new Version();
	// version.sessionId=GlobalParams.sessionId;
	// version.id="1";
	// version.sysType="1";
	// gUtil.getVersion(version,1);
	// }

}
