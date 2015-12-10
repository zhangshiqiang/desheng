package com.hanyu.desheng.engine;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import com.lidroid.xutils.http.client.multipart.content.FileBody;

import com.hanyu.desheng.bean.RegisterBean;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.LogUtil;

/**
 * 用户业务处理类
 * 
 * 
 */
public class UserEngine extends DSUrlManager {

	private static final String tag = "UserEngine";

	/**
	 * 获取手机验证码
	 * 
	 * @param
	 * @return
	 */
	public String postVerCode(String phone, int flag, String content) {
		String str = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("flag", flag + "");
		params.put("content", content);
		str = getHttpManager().sendPost(getFullUrl(GETCODE), params);
		if (str != null) {
			LogUtil.i(tag, str);
			return str;
		}
		return "";
	}

	/**
	 * 检验验证码
	 * 
	 * @param
	 * @return
	 */
	public String checkVerCode(String phone, int flag, String code) {
		String str = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("flag", flag + "");
		params.put("code", code);
		str = getHttpManager().sendPost(getFullUrl(CHECKCODE), params);
		if (str != null) {
			LogUtil.i(tag, str);
			return str;
		}
		return "";
	}

	/**
	 * 注册
	 */
	public RegisterBean toRegister(String username, String pwd) {
		RegisterBean rb = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("pwd", pwd);
		String result = getHttpManager().sendPost(getFullUrl(REGISTER), params);
		if (result != null) {
			LogUtil.i(tag, result);
			rb = GsonUtils.json2Bean(result, RegisterBean.class);
		}
		return rb;

	}

	/**
	 * 登录
	 */
	public String toLogin(String username, String pwd) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("pwd", pwd);
		String result = getHttpManager().sendPost(getFullUrl(LOGIN), params);
		if (result != null) {
			LogUtil.i(tag, result);
			return result;
		}
		return "";
	}

	/**
	 * 获取环信登录信息
	 */
	public String toLoginHx(String username) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mobile", username);
		String result = getHttpManager().sendPost(getFullUrl2(LOGINHX), params);
		if (result != null) {
			LogUtil.i(tag, result);
			return result;
		}
		return "";
	}

	/**
	 * 获取环信用户名昵称
	 */
	public String toGetHxUser(String usernames) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("hx_name", usernames);
		String result = getHttpManager().sendPost(getFullUrl2(GET_HX_USER),
				params);
		if (result != null) {
			LogUtil.i(tag, result);
			return result;
		}
		return "";

	}

	/**
	 * 通过手机号获取环信
	 * 
	 * @param mobiles
	 * @return
	 */
	public String toGetMobil(String mobiles) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobiles);
		String result = getHttpManager().sendPost(getFullUrl2(FILTERHXMOBILE),
				params);
		if (result != null) {
			LogUtil.i(tag, result);
			return result;
		}
		return "";
	}

	/**
	 * 过滤通讯录里没有注册环信的手机号
	 */
	public String toFilterHXMobile(String mobiles) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobiles);
		String result = getHttpManager().sendPost(getFullUrl2(FILTERHXMOBILE),
				params);
		if (result != null) {
			LogUtil.i(tag, result);
			return result;
		}
		return "";
	}

	/**
	 * 修改密码
	 */
	public String changePWD(String username, String pwd, String newpwd) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("pwd", pwd);
		params.put("newpwd", newpwd);
		String result = getHttpManager()
				.sendPost(getFullUrl(CHANGEPWD), params);
		if (result != null) {
			LogUtil.i(tag, result);
			return result;
		}
		return "";
	}

	/**
	 * 获取会员信息 通过id
	 */

	public InputStream toGetinfo(String memberid) {
		HashMap<String, String> params = new HashMap<String, String>();
		// params.put("id", memberid);
		InputStream result = null;
		result = getHttpManager().sendPostI(
				getFullUrl(GETINFO) + "&id=" + memberid, params);
		// result = getHttpManager()
		// .sendPostI(
		// "http://api.wxshop.wddcn.com/a2d6c3342ac14430937707dbd68143ac/api?method=get.vip.item&id=10303",
		// params);
		if (result != null) {
			LogUtil.i(tag, result + "");
			return result;
		}
		return null;
	}
	/**
	 * 获取会员信息 通过手机号
	 */
	
	public InputStream toGetinfo2(String phone) {
		HashMap<String, String> params = new HashMap<String, String>();
		// params.put("id", memberid);
		InputStream result = null;
		result = getHttpManager().sendPostI(
				getFullUrl(GETINFO) + "&phone=" + phone, params);
		// result = getHttpManager()
		// .sendPostI(
		// "http://api.wxshop.wddcn.com/a2d6c3342ac14430937707dbd68143ac/api?method=get.vip.item&id=10303",
		// params);
		if (result != null) {
			LogUtil.i(tag, result + "");
			return result;
		}
		return null;
	}

	// public InputStream toGetinfo(String memberid) {
	// InputStream result = null;
	// result = getHttpManager().sendGetI(
	// getFullUrl(GETINFO) + "&id=" + memberid);
	// if (result != null) {
	// LogUtil.i(tag, result + "");
	// return result;
	// }
	// return null;
	// }

	/**
	 * 更新会员信息
	 * 
	 */
	public String toUpdateHeadPic(String memberid, String membercode,
			String photo, String nickname, String realname, String email,
			String sex, String phone, String qq, String weixin,
			String profession, String birthday, String identity,
			String identity_img) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("memberid", memberid);
		params.put("membercode", membercode);
		params.put("photo", photo);
		params.put("nickname", nickname);
		params.put("realname", realname);
		params.put("email", email);
		params.put("sex", sex);
		params.put("phone", phone);
		params.put("qq", qq);
		params.put("weixin", weixin);
		params.put("profession", profession);
		params.put("birthday", birthday);
		params.put("identity", identity);
		params.put("identity_img", identity_img);
		String result = getHttpManager().sendPost(getFullUrl(UPDATAINFO),
				params);
		if (result != null) {
			LogUtil.i(tag, result);
			return result;
		}
		return null;
	}

	/**
	 * 意见反馈
	 * 
	 * @param feedback
	 * @return
	 */
	public String toFeedBack(String mobile, String title, String content) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("title", title);
		params.put("content", content);
		result = getHttpManager().sendPost(getFullUrl2(FEEDBACK), params);
		if (result != null) {
			LogUtil.i("feedback", result);
			return result;
		}
		return null;
	}

	/**
	 * 获取首页信息
	 * 
	 * @param user_id
	 *            用户id（此处用于判断课程是否被收藏）如果用户未登录传空值
	 * @param page_no
	 *            第几次请求课程分页，初始值为0。
	 * @return
	 */
	public String getTopPic(String user_id, String page_no) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_id", user_id);
		params.put("page_no", page_no);
		result = getHttpManager().sendPost(getFullUrl2(HOMEPAGE), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}

	/**
	 * 获取订单列表
	 * 
	 * @param miid_id
	 *            用户id 必填
	 * @param state
	 *            请求状态订单状体，多个状态逗号(,)分隔， 0、等待付款 2、待发货 4、已经发货 6、已经收货 8、交易成功
	 *            -1、本次交易已取消
	 * @param page_no
	 *            分页数（可选参数，缺省值为1）
	 * @return
	 */
	public String getShopOrder(String miid_id, String state, int page_no) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("miid_id", miid_id);
		params.put("state", state);
		params.put("page_no", page_no+"");
		result = getHttpManager().sendPost(getFullUrl2(SHOPORDER), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}

	/**
	 * 获取课程详情
	 * 
	 * @param lesson_id
	 *            课程id
	 * @return
	 */
	public String getLessonDetails(String user_id, int lesson_id) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_id", user_id);
		params.put("lesson_id", lesson_id + "");
		result = getHttpManager().sendPost(getFullUrl2(LESSONDETAILS), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}
	/**
	 * 获取新闻列表
	 * 
	 * @param page_no
	 *            页数
	 * @return
	 */
	public String getNewsList(String page_no) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("page_no", page_no + "");
		result = getHttpManager().sendPost(getFullUrl2(NEWSLIST), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}
	/**
	 * 获取新闻详情
	 * 
	 * @param news_id
	 *            新闻id
	 * @return
	 */
	public String getNewsDetails(int news_id) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("news_id", news_id + "");
		result = getHttpManager().sendPost(getFullUrl2(NEWSINFO), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}

	/**
	 * 上传用户图像
	 * 
	 * @param user_id
	 * @param file
	 * @return
	 */
	public String uploadUserImage(File file) {
		HashMap<String, String> params = new HashMap<String, String>();

		HashMap<String, FileBody> fileMaps = null;
		if (file != null) {
			fileMaps = new HashMap<String, FileBody>();
			FileBody fileBody = new FileBody(file);
			fileMaps.put("imgFile", fileBody);
		}

		String result = getHttpManager().upLoad(getFullUrl(UPLOADPIC), params,
				fileMaps);

		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;

	}

	/**
	 * 获取最新版本
	 * 
	 * @param version
	 * @return
	 */

	// public String getVersion() {
	//
	// try {
	// HashMap<String, String> params = new HashMap<String, String>();
	// String result = getHttpManager().sendPost(getFullUrl(VERSION),
	// params);
	// if (result != null) {
	// Log.i("tag", result);
	// JSONArray jsonArr = new JSONArray(new String(result));
	// JSONObject json = jsonArr.getJSONObject(0);
	// result = json.toString();
	// }
	// return result;
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// return "";
	// }
	// }

	/**
	 * 收藏
	 * 
	 * @param user_id
	 *            用户id
	 * @return
	 */
	public String Collect(String user_id, int lesson_id,String mobile) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_id", user_id);
		params.put("lesson_id", lesson_id + "");
		params.put("mobile", mobile);
		result = getHttpManager().sendPost(getFullUrl2(COLLECT), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}

	/**
	 * 退群
	 * 
	 * @param user_id
	 *            用户id
	 * @return
	 */
	public String CancelCollect(String user_id, String groupId,String mobile) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_id", user_id);
		params.put("hx_group_id", groupId);
		params.put("mobile", mobile);
		result = getHttpManager().sendPost(getFullUrl2(CANCELCOLLECT), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}

	/**
	 * 获得店铺上级德升管理人员的环信号
	 * 
	 * @param mobile
	 * @param hx_group_id
	 * @return
	 */
	public String getTopHxUser(String mobile, String hx_group_id) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("hx_group_id", hx_group_id);
		result = getHttpManager()
				.sendPost(getFullUrl2(GET_TOP_HX_USER), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}

	// 判断用户所在的群有没有被禁言
	public String checkUserChat(final String user_hx_name,
			final String hx_group_id) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_hx_name", user_hx_name);
		params.put("hx_group_id", hx_group_id);
		result = getHttpManager()
				.sendPost(getFullUrl2(CHECK_USER_CHAT), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}

	/**
	 * 判断用户有没有管理权限
	 * 
	 * @param hx_name
	 * @param hx_group_id
	 * @return
	 */
	public String checkAdmin(final String hx_name, final String hx_group_id) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("hx_name", hx_name);
		params.put("hx_group_id", hx_group_id);
		result = getHttpManager().sendPost(getFullUrl2(CHECK_ADMIN), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;

	}

	/**
	 * 对用户实施禁言
	 * 
	 * @param user_hx_name
	 * @param hx_group_id
	 * @return
	 */
	public String tobannedUser(String user_hx_name, String hx_group_id) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_hx_name", user_hx_name);
		params.put("hx_group_id", hx_group_id);
		result = getHttpManager().sendPost(getFullUrl2(BANNER_USER), params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;

	}

	/**
	 * 对用户解除禁言
	 * 
	 * @param user_hx_name
	 * @param hx_group_id
	 * @return
	 */
	public String toCancelUser(String user_hx_name, String hx_group_id) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_hx_name", user_hx_name);
		params.put("hx_group_id", hx_group_id);
		result = getHttpManager().sendPost(getFullUrl2(CANCEL_BANNER_USER),
				params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}

	/**
	 * 更新用户积分
	 * 
	 * @return
	 */
	public String updatajifen(String phone, String inter, String remark) {
		String result = null;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("inter", inter);
		params.put("remark", remark);
		result = getHttpManager().sendPost(getFullUrl(UPDATAINTEGR),
				params);
		if (result != null) {
			LogUtil.i("tag", result);
			return result;
		}
		return null;
	}
}
