package com.hanyu.desheng.bean;

import java.io.Serializable;

import com.leaf.library.db.annotation.Column;
import com.leaf.library.db.annotation.Table;
@Table(name="group_banner")
public class GroupBannerBean implements Serializable{
	
	@Column
    public String username;
	@Column
    public String groupid;
	@Column
    public String isjinyan;

}
