package com.hanyu.desheng.bean;

import java.io.Serializable;
import java.util.List;

public class HomeBean implements Serializable {
	public static final long serialVersionUID = 1L;
	public int code;
	public String msg;
	public Data data;
	

	public class Data implements Serializable {
		public List<Banner> banner_list;// 轮播图

		public class Banner implements Serializable {
			public static final long serialVersionUID = 1L;
			public String banner;
			public String link_url;
			
		}

		public List<Lesson> hot_lesson_list;// 课程列表

//		public class Lesson implements Serializable {
//			public int collect_num;// 收藏数
//			public int is_collect;// 是否收藏 1是 0否
//			public int lesson_id;// 课程id
//			public String hx_group_id;// 环信群组id
//			public String thumb;// 课程缩略图
//			public String title;// 课程标题
//			
//
//		}

		public String page_more;// 是否有下一页 1是；0否。

		// 版本
		public Version andriod;
	}

	public class Version {
		public String version;
		public String apk_url;
	}
}
