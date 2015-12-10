package com.hanyu.desheng.bean;

import java.io.Serializable;
import java.util.List;

public class NewsBean implements Serializable {
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
		public List<News> news_list;// 轮播图

		public class News implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public String title;
			public String thumb;
			public String news_id;
		}

		public String page_more;// 是否有下一页 1是；0否。
	}
}
