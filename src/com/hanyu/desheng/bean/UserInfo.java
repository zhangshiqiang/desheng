package com.hanyu.desheng.bean;

import java.io.Serializable;

import com.hanyu.desheng.base.BaseObj;

public class UserInfo extends BaseObj implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String miid; // 会员id
	public String miphone; // 账号
	public String miname;// 会员姓名
	public String milevel;// 会员等级
	public String miinter;// 会员积分
	public String miphone1;// 手机
	public String miaddtime;// 注册时间
	public String mivshopcode;// 所属店铺编号
	public String mistoreno;// 所属门店编号
	public String micardno;// 会员卡号
	public String misex;// 性别（1男，0女）
	public String mibirthday;// 生日
	public String qq;// QQ
	public String miprovince;// 会员地址省
	public String micity;// 市
	public String miarea;// 县
	public String miaddr;// 镇
	public String mirealname;// 真实姓名
	public String miweixin;// 微信号
	public String miemail;// 邮箱
	public String miprofession;// 职业
	public String mivshopname;// 所属店铺
	public String mishopid;// 所属店铺id
	public String mishopcode;// 店铺编号
	public String mistorename;// 所属门店
	public String minodename;// 所属节点
	public String openshop;// 是否开店（0是未开店 若开店返回为所开店铺的id）
	public String openid;// 微信openid 对接微信的唯一参数
	public String miuserheader;// 用户头像
    public String hx_name;
    public String mobile;
	public UserInfo() {

	}

	@Override
	public String[] getNodes() {
		return new String[] { "miid", "miphone", "miname", "milevel",
				"miinter", "miphone1", "miaddtime", "mivshopcode", "mistoreno",
				"micardno", "misex", "mibirthday", "qq", "miprovince",
				"micity", "miarea", "miaddr", "mirealname", "miweixin",
				"miemail", "miprofession", "mivshopname", "mishopid",
				"mishopcode", "mistorename", "minodename", "openshop",
				"openid", "miuserheader" };
	}

}
