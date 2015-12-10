package com.hanyu.desheng.bean;

import java.io.Serializable;

public class TopUserBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 732657627;
	
	public String code;
	public String msg;
    public Data data;
	
	public class Data{
		public String hx_name;
	}
}
