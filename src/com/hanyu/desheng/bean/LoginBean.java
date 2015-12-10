package com.hanyu.desheng.bean;

import java.io.Serializable;

public class LoginBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 请求返回状态 
	 * 0 操作成功 
	 * 10000 某个参数验证失败，具体参数看返回内容 
	 * 10001 账号密码错误 
	 * 10002 新密码长度位数不足 
	 * 10003 操作失败，请开发者稍后再试
	 */
	public int code;

	/**
	 * 返回信息
	 */
	public String msg;

	public Data data;

	public class Data {
		public String cardno;// 会员卡号
		public String membercode;// 会员代码
		public String memberid;// 会员ID
		public String autologinurl;// 跳转链接
	}

}
