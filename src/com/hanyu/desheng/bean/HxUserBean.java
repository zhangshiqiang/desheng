package com.hanyu.desheng.bean;

import java.io.Serializable;

import com.leaf.library.db.annotation.Column;
import com.leaf.library.db.annotation.Table;
/**
 * 
 * @author wangbin
 *
 */
@Table(name="hx_user_table")
public class HxUserBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Column
	public String hx_username;
    @Column
	public String hx_password;
    @Column
	public String username;
    @Column
	public String headpic;
    
}
