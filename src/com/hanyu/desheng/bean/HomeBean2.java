package com.hanyu.desheng.bean;

import java.io.Serializable;
import java.util.List;

public class HomeBean2 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int code;

	public String msg;

	public Data data;

	public class Data implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public List<Lesson> hot_lesson_list;// 课程列表

		public String page_more;// 是否有下一页 1是；0否。
	}
}
