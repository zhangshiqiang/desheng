package com.hanyu.desheng.bean;

import java.io.Serializable;

public class RegisterBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int code;

	public String msg;

	public class Data implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public String cardno;// 会员卡号，

		public int memberid;// 会员ID，

		public String membercode; // 会员代码
	}

}
