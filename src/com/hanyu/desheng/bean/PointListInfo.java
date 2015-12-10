package com.hanyu.desheng.bean;

/**
 * 
 * 
 * @param <T>
 */
public class PointListInfo {

	public int count;// 积分数量（正数为增加，负数为减少）
	public String addtime; // 时间
	public String from; // 积分来源
	public String type;// 积分类型（get会员获取synchronize线下同步convert积分商城消费gift后台赠送）
	public String info;// 积分说明
	public String theline;// 0线上1线下

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTheline() {
		return theline;
	}

	public void setTheline(String theline) {
		this.theline = theline;
	}

	// public PointListInfo() {
	//
	// }
	//
	// @Override
	// public String[] getNodes() {
	// return new String[] { "count", "addtime", "from", "type", "info",
	// "theline" };
	// }

}
