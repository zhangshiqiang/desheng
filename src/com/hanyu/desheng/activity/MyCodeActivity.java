package com.hanyu.desheng.activity;

import com.easemob.chat.EMContactManager;
import com.hanyu.desheng.ExampleApplication;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.util.MyCodeIV;
import com.hanyu.desheng.utils.DisplayUtil;
import com.hanyu.desheng.utils.SavePicPopupWindow;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.YangUtils;
import com.hanyu.desheng.zxing.CaptureActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyCodeActivity extends BaseActivity {
	protected static final int RESULT = 5555;

	@ViewInject(R.id.my_code_iv)
	private ImageView my_code_iv;
	@ViewInject(R.id.mycode_rl)
	private RelativeLayout mycode_rl;

	@ViewInject(R.id.tv_username)
	private TextView tv_username;
	@ViewInject(R.id.iv_avatar)
	private ImageView iv_avatar;

	private Bitmap bitmap;
	private Context mContext;
	private SavePicPopupWindow menuWindow; // 自定义编辑弹出框
	private String hx_username;
	private MyCodeIV asd;
	private Canvas canvas;

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.my_code;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		DisplayMetrics dm = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		DisplayUtil.init(dm);
		int avatar_size = 200;
		int avatar_left = 70;
		int avatar_top = 30;
		int avatartSize = DisplayUtil.resize(avatar_size, com.hanyu.desheng.utils.DisplayUtil.ScaleType.DENSITY);
		int avatarLeft = DisplayUtil.resize(avatar_left, com.hanyu.desheng.utils.DisplayUtil.ScaleType.DENSITY);
		int avatarTop = DisplayUtil.resize(avatar_top, com.hanyu.desheng.utils.DisplayUtil.ScaleType.DENSITY);
		
		RelativeLayout.LayoutParams avatarParams = (android.widget.RelativeLayout.LayoutParams) iv_avatar.getLayoutParams();
		avatarParams.width = avatartSize;
		avatarParams.height = avatartSize;
		avatarParams.leftMargin = avatarLeft;
		avatarParams.topMargin = avatarTop;
		iv_avatar.setLayoutParams(avatarParams);
		
		
		int user_left = 60;
		int userLeft = DisplayUtil.resize(user_left, com.hanyu.desheng.utils.DisplayUtil.ScaleType.WIDTH);
		RelativeLayout.LayoutParams userParams = (android.widget.RelativeLayout.LayoutParams) tv_username.getLayoutParams();
		userParams.leftMargin = userLeft;
		userParams.topMargin = avatarTop;
		tv_username.setLayoutParams(userParams);
		
		int code_height = 380;
		int code_width = 430;
		int code_btm = 220;
		int codeHeight = DisplayUtil.resize(code_height, com.hanyu.desheng.utils.DisplayUtil.ScaleType.HEIGHT);
		int codeWidth = DisplayUtil.resize(code_width, com.hanyu.desheng.utils.DisplayUtil.ScaleType.DENSITY);
		int codeBtm = DisplayUtil.resize(code_btm, com.hanyu.desheng.utils.DisplayUtil.ScaleType.HEIGHT);
		RelativeLayout.LayoutParams codeParams = (android.widget.RelativeLayout.LayoutParams) my_code_iv.getLayoutParams();
		codeParams.height = codeHeight;
		codeParams.width = codeWidth;
		codeParams.bottomMargin = codeBtm;
		my_code_iv.setLayoutParams(codeParams);
		

		setBack();
		setTopTitle("我的二维码");
		setRightButton("扫描", new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(mContext, CaptureActivity.class);
				startActivityForResult(intent, RESULT);
			}
		});

		// 二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// 自己的环信用户名
		hx_username = SharedPreferencesUtil.getStringData(context, "hx_name", "");
		bitmap = YangUtils.createQRImage("desheng4567" + hx_username);
		my_code_iv.setScaleType(ScaleType.FIT_XY);
		my_code_iv.setImageBitmap(bitmap);
		// }
		// }).start();
		mContext = MyCodeActivity.this;

		// asd = new MyCodeIV(mContext, bitmap);
		// canvas = new Canvas();
		// asd.draw(canvas);
		// asd.setMinimumHeight(500);
		// asd.setMinimumWidth(500);
		// asd.invalidate();
		// mycode_ll.addView(asd);
		mycode_rl.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				menuWindow = new SavePicPopupWindow(mContext, itemsOnClick);
				menuWindow.showAtLocation(findViewById(R.id.mycode_rl), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
						0);

				return false;
			}
		});

		getUserInfo();
	}

	private void getUserInfo() {
		String username = SharedPreferencesUtil.getStringData(context, "miname", "");
		tv_username.setText(username);
		String headpic = SharedPreferencesUtil.getStringData(context, "headpic", "");
		if (!TextUtils.isEmpty(headpic)) {
			ImageLoader.getInstance().displayImage(headpic, iv_avatar);
		}
	}

	@Override
	public void setListener() {

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == 5555 && arg1 == 666) {
			if (arg2 != null) {
				// 扫描二维码获取的其他用户的环信用户名
				hx_username = arg2.getExtras().getString("hx_name").toString();
				String username = SharedPreferencesUtil.getStringData(mContext, "miname", "");
				addContact(hx_username, username);
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	/**
	 * 添加contact
	 * 
	 * @param view
	 */
	public void addContact(final String hx_username, final String username) {
		if (ExampleApplication.getInstance().getUserName().equals(hx_username)) {
			String str = mContext.getResources().getString(R.string.not_add_myself);
			mContext.startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", str));
			return;
		}

		if (ExampleApplication.getInstance().getContactList().containsKey(hx_username)) {
			String strin = mContext.getResources().getString(R.string.This_user_is_already_your_friend);
			mContext.startActivity(new Intent(mContext, AlertDialog.class).putExtra("msg", strin));
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(mContext);
		String stri = mContext.getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {
				try {
					String s = username + "请求加你为好友";
					EMContactManager.getInstance().addContact(hx_username, s);
					((Activity) mContext).runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							String s1 = mContext.getResources().getString(R.string.send_successful);
							Toast.makeText(mContext, s1, 1).show();
						}
					});
				} catch (final Exception e) {
					((Activity) mContext).runOnUiThread(new Runnable() {
						@SuppressLint("ShowToast")
						public void run() {
							progressDialog.dismiss();
							String s2 = mContext.getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(mContext, s2 + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 隐藏弹出窗口
			menuWindow.dismiss();

			switch (v.getId()) {
			case R.id.save_pic:// 保存图片到本地
				Bitmap bitmapFromView = YangUtils.getBitmapFromView(mycode_rl);
				YangUtils.saveImageToGallery(mContext, bitmapFromView);
				break;
			case R.id.cancel_Btn:// 取消
				break;
			default:
				break;
			}
		}
	};

}
