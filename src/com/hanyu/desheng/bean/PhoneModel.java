package com.hanyu.desheng.bean;

public class PhoneModel {

	private String	imgSrc;		// ��ʾͷ��
	private String	name;			// ��ʾ������
	private String	sortLetters;	// ��ʾ����ƴ��������ĸ

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
