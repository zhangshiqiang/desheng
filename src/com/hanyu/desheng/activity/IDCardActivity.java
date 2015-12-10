package com.hanyu.desheng.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SelectPicPopupWindow;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

public class IDCardActivity extends BaseActivity {
	@ViewInject(R.id.idcard_btn)
	private Button idcard_btn;
	@ViewInject(R.id.idcard_et)
	private EditText idcard_et;

	private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框

	/** 使用照相机拍照获取图片 */
	public static final int CAMERA = 0x11;
	/** 使用相册中的图片 */
	public static final int PICTURE = 2;
	private static final int PHOTO_RESULT = 0x12;// 结果

	private static String IMG_PATH = getSDPath() + java.io.File.separator
			+ "idcard";

	@SuppressWarnings("unused")
	private static ProgressDialog pd;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.idcard_btn:
			String idcard = idcard_et.getText().toString();
			if (TextUtils.isEmpty(idcard) || idcard.length() < 18) {
				MyToastUtils.showShortToast(context, "请输入正确的身份证号码");
			} else {
				menuWindow = new SelectPicPopupWindow(IDCardActivity.this,
						itemsOnClick);
				menuWindow.showAtLocation(findViewById(R.id.idcard),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}

			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.idcard;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		File path = new File(IMG_PATH);
		if (!path.exists()) {
			path.mkdirs();
		}
		context = IDCardActivity.this;
		setBack();
		setTopTitle("身份证验证");
	}

	@Override
	public void setListener() {
		idcard_btn.setOnClickListener(this);
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 隐藏弹出窗口
			menuWindow.dismiss();

			switch (v.getId()) {
			case R.id.takePhotoBtn:// 拍照
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
				startActivityForResult(intent, CAMERA);
				break;
			case R.id.pickPhotoBtn:// 相册选择图片
				intent = new Intent();
				// 如果要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, PICTURE);
				break;
			case R.id.cancelBtn:// 取消
				break;
			default:
				break;
			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CAMERA:
				startPhotoCrop(Uri.fromFile(new File(IMG_PATH, "temp.jpg")));
				break;
			case PICTURE:
				startPhotoCrop(data.getData());
				break;
			case PHOTO_RESULT:
				if (data != null) {
					setPicToView(data);
				}
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			// 取得SDCard图片路径做显示
			Bitmap photo = extras.getParcelable("data");
			@SuppressWarnings("unused")
			Drawable drawable = new BitmapDrawable(null, photo);
			// String urlpath = FileUtil.saveFile(PersonalDataActivity.this,
			// "temphead.jpg", photo);
			upLoadHeadPic(new File(IMG_PATH, "temp_cropped.jpg"));

		}
	}

	/**
	 * 调用系统图片编辑进行裁剪
	 */
	public void startPhotoCrop(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(IMG_PATH, "temp_cropped.jpg")));
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, PHOTO_RESULT);
	}

	/**
	 * 获取sd卡的路径
	 * 
	 * @return 路径的字符串
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取外存目录
		}
		return sdDir.toString();
	}

	/**
	 * 上传头像并获得返回的地址
	 * 
	 * @param file
	 */
	private void upLoadHeadPic(final File file) {
		new HttpTask<Void, Void, String>(context) {

			String idcard;

			@Override
			protected String doInBackground(Void... params) {
				idcard = idcard_et.getText().toString();
				String result = EngineManager.getUserEngine().uploadUserImage(
						file);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (code.equals("0")) {
							JSONObject object = json.getJSONObject("data");
							String upload = object.getString("imgurl");
							SharedPreferencesUtil.saveStringData(context,
									"idcard_url", upload);
							SharedPreferencesUtil.saveStringData(context,
									"idcard", idcard);

							MyToastUtils.showShortToast(context, "获取成功");

						} else {
							MyToastUtils.showShortToast(context, "获取失败");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			protected void onPreExecute() {

			}
		}.executeProxy();
	}

}
