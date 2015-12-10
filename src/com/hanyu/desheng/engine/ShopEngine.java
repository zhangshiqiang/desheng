package com.hanyu.desheng.engine;

import java.io.InputStream;
import java.util.HashMap;

import com.hanyu.desheng.utils.LogUtil;

/**
 * 商城业务处理类
 * 
 * 
 */
public class ShopEngine extends DSUrlManager {

	private static final String tag = "ShopEngine";

	/**
	 * 获取商城信息
	 */
	public InputStream toGetStoreinfo(String shopid) {
		InputStream result = null;
		result = getHttpManager().sendGetI(getFullUrl(STORE) + "&id=" + shopid);
		if (result != null) {
			LogUtil.i(tag, result + "");
			return result;
		}
		return null;
	}

	/**
	 * 确认收货
	 * 
	 * @param ord_no
	 *            子订单编号
	 * @param uid
	 *            该单下单会员id
	 * 
	 * @return
	 */
	public InputStream confirmGoods(String ord_no) {
		HashMap<String, String> params = new HashMap<String, String>();
		// params.put("ord_no", ord_no);
		InputStream result = null;
		result = getHttpManager().sendPostI(
				getFullUrl(DORDERS) + "&ord_no=" + ord_no, params);
		if (result != null) {
			LogUtil.i(tag, result + "");
			return result;
		}
		return null;
	}

	/**
	 * 获取积分列表
	 * 
	 * @param phone
	 *            用户手机号 必填
	 * @param page_size
	 *            每页记录数，默认为40 选填
	 * @param page_no
	 *            页数 选填
	 * @return
	 */
	public InputStream getPointList(String phone, int page_size, int page_no) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		InputStream result = null;
		result = getHttpManager().sendPostI(getFullUrl(POINTLIST), params);
		if (result != null) {
			LogUtil.i(tag, result + "");
			return result;
		}
		return null;
	}
}
