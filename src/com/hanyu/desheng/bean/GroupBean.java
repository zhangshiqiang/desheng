package com.hanyu.desheng.bean;

import java.io.Serializable;

import com.leaf.library.db.annotation.Column;
import com.leaf.library.db.annotation.Table;

@Table(name="group_table")
public class GroupBean implements Serializable{
	
	@Column
	public String groupid;
	@Column
	public String hxusername;

}
