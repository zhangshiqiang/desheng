package com.hanyu.desheng.bean;

import java.io.Serializable;
import java.util.List;
/**
 * 聊天返回头像昵称
 * @author wangbin
 *
 */
public class ChatBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3454666;
 
	public int code;
	public String msg;
	public Data data;
	
	public class Data{
		public List<UserInfo> info_list;
	}
	
}
