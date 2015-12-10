package com.hanyu.desheng.activity;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanyu.desheng.GlobalParams;
import com.hanyu.desheng.Left;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.UserInfo;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.ui.CircleImageView;
import com.hanyu.desheng.ui.TimePopupWindow;
import com.hanyu.desheng.ui.TimePopupWindow.OnTimeSelectListener;
import com.hanyu.desheng.ui.TimePopupWindow.Type;
import com.hanyu.desheng.utils.LogUtil;
import com.hanyu.desheng.utils.MyToastUtils;
import com.hanyu.desheng.utils.SelectPicPopupWindow;
import com.hanyu.desheng.utils.SharedPreferencesUtil;
import com.hanyu.desheng.utils.XmlComonUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalActivity extends BaseActivity {
	private static final String tag = "PersonalActivity";
	@ViewInject(R.id.rl_back)
	private RelativeLayout rl_back;// 后退键
	@ViewInject(R.id.personal_rl1)
	private RelativeLayout personal_rl1;// 头像管理
	@ViewInject(R.id.personal_rl2)
	private RelativeLayout personal_rl2;// 真实姓名
	@ViewInject(R.id.personal_rl3)
	private RelativeLayout personal_rl3;// 昵称
	@ViewInject(R.id.personal_rlqq)
	private RelativeLayout personal_rlqq;// qq
	@ViewInject(R.id.personal_rlwx)
	private RelativeLayout personal_rlwx;// 微信
	@ViewInject(R.id.personal_rlph)
	private RelativeLayout personal_rlph;// 手机号
	@ViewInject(R.id.personal_rlpro)
	private RelativeLayout personal_rlpro;// 职业
	@ViewInject(R.id.personal_rl5)
	private RelativeLayout personal_rl5;// 生日
	@ViewInject(R.id.personal_rlem)
	private RelativeLayout personal_rlem;// 邮箱
	@ViewInject(R.id.personal_rl8)
	private RelativeLayout personal_rl8;// 身份证验证
	@ViewInject(R.id.personal_rl9)
	private RelativeLayout personal_rl9;// 我的二维码

	@ViewInject(R.id.personal_riv)
	private CircleImageView personal_riv;// 头像
	@ViewInject(R.id.personal_et)
	private TextView personal_et;// 真实姓名
	@ViewInject(R.id.personal_tv2)
	private TextView personal_tv2;// 昵称文本
	@ViewInject(R.id.personal_tv3)
	private TextView personal_tv3;// 邮箱
	@ViewInject(R.id.personal_phone)
	private TextView personal_phone;// 手机号
	@ViewInject(R.id.personal_qq)
	private TextView personal_qq;// QQ
	@ViewInject(R.id.personal_wx)
	private TextView personal_wx;// 微信
	@ViewInject(R.id.personal_profession)
	private TextView personal_profession;// 职业
	@ViewInject(R.id.personal_birthday)
	private TextView personal_birthday;// 生日
	@ViewInject(R.id.personal_rg)
	private RadioGroup personal_rg;// 按钮组
	@ViewInject(R.id.my_order_lv_fl)
	private FrameLayout my_order_lv_fl;

	private UserInfo userInfo;

	private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框
	/** 使用照相机拍照获取图片 */
	public static final int CAMERA = 0x11;
	/** 使用相册中的图片 */
	public static final int PICTURE = 202;
	private static final int PHOTO_RESULT = 0x12;// 结果
	private static final int REALNAME = 1;
	private static final int NICKNAME = 2;
	private static final int QQNUMBER = 3;
	private static final int WEIXIN = 4;
	private static final int PROFESS = 5;
	private static final int PHONE = 6;
	private static final int EMAIL = 7;

	private static String IMG_PATH = getSDPath() + java.io.File.separator
			+ "ocrtest";

	// private static ProgressDialog pd;
	// private String cityTxt;
	private int sex;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personal_rl1:
			menuWindow = new SelectPicPopupWindow(PersonalActivity.this,
					itemsOnClick);
			menuWindow.showAtLocation(findViewById(R.id.personal),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.personal_rl2:
			intent = new Intent(PersonalActivity.this,
					ChangeNicknameActivity.class);
			intent.putExtra("flag", 1);
			startActivityForResult(intent, REALNAME);
			break;
		case R.id.personal_rlqq:
			intent = new Intent(PersonalActivity.this,
					ChangeNicknameActivity.class);
			intent.putExtra("flag", 2);
			startActivityForResult(intent, QQNUMBER);
			break;
		case R.id.personal_rlwx:
			intent = new Intent(PersonalActivity.this,
					ChangeNicknameActivity.class);
			intent.putExtra("flag", 1);
			startActivityForResult(intent, WEIXIN);
			break;
		case R.id.personal_rlpro:
			intent = new Intent(PersonalActivity.this,
					ChangeNicknameActivity.class);
			intent.putExtra("flag", 1);
			startActivityForResult(intent, PROFESS);
			break;
		case R.id.personal_rlph:
			intent = new Intent(PersonalActivity.this,
					ChangeNicknameActivity.class);
			intent.putExtra("flag", 3);
			startActivityForResult(intent, PHONE);
			break;
		case R.id.personal_rlem:
			intent = new Intent(PersonalActivity.this,
					ChangeNicknameActivity.class);
			intent.putExtra("flag", 4);
			startActivityForResult(intent, EMAIL);
			break;
		case R.id.personal_rl3:
			intent = new Intent(PersonalActivity.this,
					ChangeNicknameActivity.class);
			intent.putExtra("flag", 1);
			startActivityForResult(intent, NICKNAME);
			break;
		case R.id.personal_rl5:
			TimePopupWindow timePopupWindow = new TimePopupWindow(context,
					Type.YEAR_MONTH_DAY);
			timePopupWindow.setRange(1950, 2050);
			timePopupWindow.showAtLocation(v, Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0, null);
			timePopupWindow.setOnTimeSelectListener(new OnTimeSelectListener() {
				@SuppressLint("SimpleDateFormat")
				@Override
				public void onTimeSelect(Date date) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					String t1 = format.format(date);
					personal_birthday.setText(t1);
				}
			});
			break;
		case R.id.personal_rl8:
			intent = new Intent(PersonalActivity.this, IDCardActivity.class);
			startActivity(intent);
			break;
		case R.id.personal_rl9:
			intent = new Intent(PersonalActivity.this, MyCodeActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_back:
			UpLoadDetails(sex);
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public int setLayout() {
		return R.layout.personal;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		setTopTitle("德升4567-个人资料");
		userInfo = new UserInfo();
		context = PersonalActivity.this;
		getsex();
		initInfo();
	}

	/**
	 * 初始化个人信息
	 */
	private void initInfo() {
		// 头像
		File path = new File(IMG_PATH);
		if (!path.exists()) {
			path.mkdirs();
		}
		String head = SharedPreferencesUtil.getStringData(context, "headpic",
				"");
		if (!TextUtils.isEmpty(head)) {
			ImageLoader.getInstance().displayImage(head, personal_riv);
		} else {
			personal_riv.setBackgroundResource(R.drawable.tx_03);
		}
		String memberid = SharedPreferencesUtil.getStringData(context,
				"memberid", "");
		LogUtil.i(tag, "我是获取会员信息中的" + memberid);
		getUserInfo(memberid);
	}

	@Override
	public void setListener() {
		personal_rl1.setOnClickListener(this);
		personal_rl2.setOnClickListener(this);
		personal_rl3.setOnClickListener(this);
		// personal_rl4.setOnClickListener(this);
		personal_rl5.setOnClickListener(this);
		personal_rl8.setOnClickListener(this);
		personal_rl9.setOnClickListener(this);
		personal_rlqq.setOnClickListener(this);
		personal_rlwx.setOnClickListener(this);
		personal_rlph.setOnClickListener(this);
		personal_rlem.setOnClickListener(this);
		rl_back.setOnClickListener(this);
		personal_rlpro.setOnClickListener(this);
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 隐藏弹出窗口
			menuWindow.dismiss();

			switch (v.getId()) {
			case R.id.takePhotoBtn:// 拍照
				// 相机
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
		if (requestCode == 2 && resultCode == 0) {
			if (data != null) {
				String nickname = data.getExtras().getString("result");
				personal_tv2.setText(nickname);
			}
		}
		if (requestCode == 1 && resultCode == 0) {
			if (data != null) {
				String realname = data.getExtras().getString("result");
				personal_et.setText(realname);
			}
		}
		if (requestCode == 3 && resultCode == 0) {
			if (data != null) {
				String qq = data.getExtras().getString("result");
				personal_qq.setText(qq);
			}
		}
		if (requestCode == 4 && resultCode == 0) {
			if (data != null) {
				String weixin = data.getExtras().getString("result");
				personal_wx.setText(weixin);
			}
		}
		if (requestCode == 5 && resultCode == 0) {
			if (data != null) {
				String pro = data.getExtras().getString("result");
				personal_profession.setText(pro);
			}
		}
		if (requestCode == 6 && resultCode == 0) {
			if (data != null) {
				String phone = data.getExtras().getString("result");
				personal_phone.setText(phone);
			}
		}
		if (requestCode == 7 && resultCode == 0) {
			if (data != null) {
				String email = data.getExtras().getString("result");
				personal_tv3.setText(email);
			}
		}
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
			Drawable drawable = new BitmapDrawable(null, photo);
			// String urlpath = FileUtil.saveFile(PersonalDataActivity.this,
			// "temphead.jpg", photo);
			personal_riv.setImageDrawable(drawable);
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
		intent.putExtra("outputX", 230);
		intent.putExtra("outputY", 230);
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

			@Override
			protected String doInBackground(Void... params) {
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
							GlobalParams.headpic = upload;
							SharedPreferencesUtil.saveStringData(context,
									"headpic", GlobalParams.headpic);
							ImageLoader.getInstance().displayImage(
									GlobalParams.headpic, personal_riv);
							sendBroadcast(new Intent(
									Left.UPDATA_CENTER));
							MyToastUtils.showShortToast(context, "更换头像成功");

						} else {
							MyToastUtils.showShortToast(context, "更换头像失败");
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

	private void getsex() {
		personal_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.personal_man:
					sex = 1;
					break;
				case R.id.personal_woman:
					sex = 0;
					break;

				default:
					break;
				}

			}
		});

		personal_rg.check(R.id.personal_man);
	}

	/**
	 * 更新个人信息
	 * 
	 * @param sex
	 */
	protected void UpLoadDetails(final int sex) {
		new HttpTask<Void, Void, String>(context) {

			private String nickname2;
			private String realname;
			private String email;
			private String phone;
			private String qq;
			private String weixin;
			private String profession;
			private String birthday;

			@Override
			protected String doInBackground(Void... params) {
				String memberid = SharedPreferencesUtil.getStringData(context,
						"memberid", "");
				LogUtil.i(tag, "我是更新会员信息中的" + memberid);
				String membercode = SharedPreferencesUtil.getStringData(
						context, "membercode", "");
				nickname2 = personal_tv2.getText().toString();
				realname = personal_et.getText().toString();
				email = personal_tv3.getText().toString();
				phone = personal_phone.getText().toString();
				qq = personal_qq.getText().toString();
				weixin = personal_wx.getText().toString();
				profession = personal_profession.getText().toString();
				birthday = personal_birthday.getText().toString();
				String identity = SharedPreferencesUtil.getStringData(context,
						"idcard", "");
				String identity_img = SharedPreferencesUtil.getStringData(
						context, "idcard_url", "");
				String headpic = SharedPreferencesUtil.getStringData(context,
						"headpic", "");
				String result = EngineManager.getUserEngine().toUpdateHeadPic(
						memberid, membercode, headpic, nickname2, realname,
						email, sex + "", phone, qq, weixin, profession,
						birthday, identity, identity_img);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					try {
						JSONObject json = new JSONObject(result);
						String code = json.getString("code");
						if (code.equals("0")) {
							SharedPreferencesUtil.saveStringData(context,
									"miname", nickname2);
							// SharedPreferencesUtil.saveStringData(context,
							// "realname", realname);
							// SharedPreferencesUtil.saveStringData(context,
							// "email", email);
							SharedPreferencesUtil.saveStringData(context,
									"miphone", phone);
							// SharedPreferencesUtil.saveStringData(context,
							// "qq",
							// qq);
							// SharedPreferencesUtil.saveStringData(context,
							// "weixin", weixin);
							// SharedPreferencesUtil.saveStringData(context,
							// "profession", profession);
							// SharedPreferencesUtil.saveStringData(context,
							// "birthday", birthday);
							// SharedPreferencesUtil.saveIntData(context, "sex",
							// sex);
							sendBroadcast(new Intent(
									Left.UPDATA_CENTER));
							// MyToastUtils.showShortToast(context, "更新成功");
						} else {
							// MyToastUtils.showShortToast(context, "更新失败");
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

	/**
	 * 获取会员信息xml流
	 * 
	 * @param memberid
	 */
	protected void getUserInfo(final String memberid) {

		new HttpTask<Void, Void, InputStream>(context) {

			@Override
			protected InputStream doInBackground(Void... params) {
				InputStream result = EngineManager.getUserEngine().toGetinfo(
						memberid);
				return result;
			}

			@Override
			protected void onPostExecute(InputStream result) {
				if (result != null) {
					try {
						// 用xml解析工具类解析xml
						XmlComonUtil.streamText2Model(result, userInfo);
						// 昵称
						personal_tv2.setText(userInfo.miname);
						// 真实姓名
						personal_et.setText(userInfo.mirealname);
						// email
						personal_tv3.setText(userInfo.miemail);
						// 手机号
						personal_phone.setText(userInfo.miphone);
						// qq
						personal_qq.setText(userInfo.qq);
						// 微信
						personal_wx.setText(userInfo.miweixin);
						// 职业
						personal_profession.setText(userInfo.miprofession);
						// 生日
						@SuppressWarnings("deprecation")
						Date date = new Date(userInfo.mibirthday);
						String format = (String) DateFormat.format(
								"yyyy-MM-dd", date);
						personal_birthday.setText(format);
						// 性别
						if ("1".equals(userInfo.misex)) {
							personal_rg.check(R.id.personal_man);
						} else if ("0".equals(userInfo.misex)) {
							personal_rg.check(R.id.personal_woman);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				my_order_lv_fl.setVisibility(View.GONE);
			}

			@Override
			protected void onPreExecute() {
				my_order_lv_fl.setVisibility(View.VISIBLE);
			}

		}.executeProxy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			UpLoadDetails(sex);
			finish();
			return true;
		}
		return false;
	}

}
