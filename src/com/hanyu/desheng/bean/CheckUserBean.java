package com.hanyu.desheng.bean;

import java.io.Serializable;

public class CheckUserBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2356464;
	
	public String code;
	
	public Data data;
    
	public String msg;
	
	public class Data{
		public String is_silence;
	}
}
