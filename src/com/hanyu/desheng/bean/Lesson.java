package com.hanyu.desheng.bean;

import java.io.Serializable;

public class Lesson implements Serializable {
	public int collect_num;// 收藏数
	public int is_collect;// 是否收藏 1是 0否
	public int lesson_id;// 课程id
	public String hx_group_id;// 环信群组id
	public String thumb;// 课程缩略图
	public String title;// 课程标题
}
