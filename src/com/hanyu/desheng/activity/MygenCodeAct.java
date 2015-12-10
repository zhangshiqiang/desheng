package com.hanyu.desheng.activity;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.utils.DisplayUtil;
import com.hanyu.desheng.utils.SavePicPopupWindow;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.YangUtils;
import com.hanyu.desheng.zxing.CaptureActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MygenCodeAct extends BaseActivity {
	@ViewInject(R.id.my_gencode_iv)
	private ImageView my_gencode_iv;
	@ViewInject(R.id.mygencode_rl)
	private RelativeLayout mygencode_rl;
	@ViewInject(R.id.tv_username)
	private TextView tv_username;
	@ViewInject(R.id.iv_avatar)
	private ImageView iv_avatar;
	private String dsqr;
	private Bitmap bitmap;
	private SavePicPopupWindow menuWindow; // 自定义编辑弹出框

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.mygencode;
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
		RelativeLayout.LayoutParams codeParams = (android.widget.RelativeLayout.LayoutParams) my_gencode_iv.getLayoutParams();
		codeParams.height = codeHeight;
		codeParams.width = codeWidth;
		codeParams.bottomMargin = codeBtm;
		my_gencode_iv.setLayoutParams(codeParams);
		
		context = MygenCodeAct.this;
		setBack();
		setTopTitle("我的推广二维码");
		setRightButton("扫描", new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(context, CaptureActivity.class);
				startActivity(intent);
			}
		});
		intent = getIntent();
		Bundle extras = intent.getExtras();
		dsqr = extras.getString("dsqr");
//		YangUtils.QR_HEIGHT = (int) (getResources().getDimension(R.dimen.image_code_height) - 30);
//		YangUtils.QR_WIDTH = (int) (getResources().getDimension(R.dimen.image_code_width) - 30);
		bitmap = YangUtils.createQRImage(dsqr);
		my_gencode_iv.setScaleType(ScaleType.FIT_XY);
		my_gencode_iv.setImageBitmap(bitmap);

		mygencode_rl.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				menuWindow = new SavePicPopupWindow(context, itemsOnClick);
				menuWindow.showAtLocation(findViewById(R.id.mygencode_ll),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				return false;
			}
		});
		
		getUserInfo();
		
	}

	private void getUserInfo() {
		String username = SharedPreferencesUtil.getStringData(context, "miname", "");
		tv_username.setText(username);
		String headpic = SharedPreferencesUtil.getStringData(context,
				"headpic", "");
		if (!TextUtils.isEmpty(headpic)) {
			ImageLoader.getInstance().displayImage(headpic, iv_avatar);
		}
	}

	@Override
	public void setListener() {

	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 隐藏弹出窗口
			menuWindow.dismiss();

			switch (v.getId()) {
			case R.id.save_pic:// 保存图片到本地
				Bitmap bitmapFromView = YangUtils.getBitmapFromView(mygencode_rl);
				YangUtils.saveImageToGallery(context, bitmapFromView);
				break;
			case R.id.cancel_Btn:// 取消
				break;
			default:
				break;
			}
		}
	};

}
