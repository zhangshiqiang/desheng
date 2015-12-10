package com.hanyu.desheng.bean;

import java.io.Serializable;

public class CheckAdminBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 32525728;

	public String code;
	public String msg;
	public Data data;
	public class Data{
		public String have_authority;
	}
}
