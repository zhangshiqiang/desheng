package com.hanyu.desheng.bean;

import java.io.Serializable;

import com.leaf.library.db.annotation.Column;
import com.leaf.library.db.annotation.Table;

/**
 * 通讯录好友
 * @author wangbin
 *
 */
@Table(name="phone_friend_table")
public class PhoneBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 43364763;
	@Column
	public String mobile;
	/**
	 * 0未添加，1等待验证，2已添加
	 */
	@Column
	public int type;
	@Column
	public String hx_name;

}
