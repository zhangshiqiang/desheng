package com.hanyu.desheng.bean;

public class PhoneModel {

	private String	imgSrc;		// 显示头像
	private String	name;			// 显示的数据
	private String	sortLetters;	// 显示数据拼音的首字母

	private String mobile;
	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Override
	public String toString() {
		return mobile;
	}
	
	@Override
	public int hashCode() {
		return mobile.hashCode();
	}
	
	
}
