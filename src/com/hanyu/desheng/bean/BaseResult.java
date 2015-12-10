package com.hanyu.desheng.bean;

import java.io.Serializable;

import com.hanyu.desheng.base.BaseObj;

/**
 * 
 * 
 * @param <T>
 */
public class BaseResult extends BaseObj implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 请求返回状态
	 */
	public String code;

	/**
	 * 返回信息
	 */
	public String msg;

	public BaseResult() {

	}

	@Override
	public String[] getNodes() {
		return new String[] { "code", "msg" };
	}

}
