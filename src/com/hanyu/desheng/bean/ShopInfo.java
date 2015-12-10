package com.hanyu.desheng.bean;

import java.io.Serializable;

import com.hanyu.desheng.base.BaseObj;

public class ShopInfo extends BaseObj implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String vsid;// 店铺ID
	public String vsname;// 店铺名称
	public String vsuser; // 姓名
	public String vsphone; // 电话
	public String vsbank; // 银行
	public String vsbankcard; // 卡号
	public String vsholder; // 持卡人
	public String vsnotice; // 公告
	public String vsdate; // 申请时间
	public String vsstate; // 状态  1代表正常  0和2都不正常
	public String vsweixin; // 微信帐号
	public String vsbankarea; // 开户行
	public String vspercent; // 自销抽成
	public String vspercent2; // 二级抽成
	public String vspercent3; // 三级抽成
	public String vspercent4; // 四级抽成
	public String vosendtype; // 发货方式
	public String vsagent; // 代理 1为是
	public String vssendself; // 自主发货
	public String vspid; // 上级店铺ID
	public String vssmoke; // 是否启用下级抽成 1为是

	public ShopInfo() {

	}

	@Override
	public String[] getNodes() {
		return new String[] { "vsid", "vsname", "vsuser", "vsphone", "vsbank",
				"vsbankcard", "vsholder", "vsnotice", "vsdate", "vsstate",
				"vsweixin", "vsbankarea", "vspercent", "vspercent2",
				"vspercent3", "vspercent4", "vosendtype", "vsagent",
				"vssendself", "vspid", "vssmoke" };
	}

}
