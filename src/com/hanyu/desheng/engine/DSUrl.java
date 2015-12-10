package com.hanyu.desheng.engine;

public interface DSUrl {
	public String Token = "a2d6c3342ac14430937707dbd68143ac/";
//	public String BASEURL = "http://sl.api.wxshop.wddcn.com/";
//	public String BASEURL = "http://wxshop.wddcn.com/";
	public String BASEURL2 = "http://app.4567cn.com/Api";
	public String BASEURL3 = "http://app.4567cn.com";
	public String BASEURL = "http://api.wxshop.wddcn.com/";

	// 首页
	public String HOMEPAGE = "/index.php?method=get_index_info";
	// 收藏
	public String COLLECT = "/index.php?method=set_lesson_collect";
	// 取消收藏
	public String CANCELCOLLECT = "/index.php?method=cancel_lesson_collect";
	// 课程详情
	public String LESSONDETAILS = "/index.php?method=get_lesson_info";
	// 注册
	public String REGISTER = "dzd/api?method=add.vip.user&ResultType=JSON";
	// 登录
	public String LOGIN = "dzd/api?method=check.vip.user&ResultType=JSON";
	// 修改密码
	public String CHANGEPWD = "dzd/api?method=update.vip.user.pwd&ResultType=JSON";
	// 更改个人信息
	public String UPDATAINFO = "dzd/api?method=update.vip.user.info&ResultType=JSON";
	// 获取个人信息
	public String GETINFO = "api?method=get.vip.item";
	// 获取验证码
	public String GETCODE = "dzd/api?method=get.sms.code&ResultType=JSON";
	// 检验验证码
	public String CHECKCODE = "dzd/api?method=check.sms.code&ResultType=JSON";
	// 上传头像
	public String UPLOADPIC = "dzd/api?method=upload.img&ResultType=JSON";
	// 获取订单信息
	public String SHOPORDER = "/transfer.php?method=get_member_order";
	// 获取已开店铺的信息
	public String STORE = "api?method=get.shop.item";
	// 确认收货
	public String DORDERS = "api?method=update.dorders.receive";
	// 积分列表
	public String POINTLIST = "api?method=get.vip.integral";
	// 获取环信用户信息
	public String LOGINHX = "/chat.php?method=get_hx_account";
	// 过滤通讯录里没有注册环信的手机号
	public String FILTERHXMOBILE = "/chat.php?method=filter_hx_mobile";
	//获取环信用户昵称头像
	public String GET_HX_USER="/chat.php?method=get_info_byhxname";
	//获得店铺上级德升管理人员的环信号
	public String GET_TOP_HX_USER="/silence.php?method=get_admin_hxname";
	//判断用户所在的群有没有被禁言
	public String CHECK_USER_CHAT="/silence.php?method=check_user_silence";
	//检查是否有禁言权限
	public String CHECK_ADMIN="/silence.php?method=check_admin_authority";
	//对用户禁言
	public String BANNER_USER="/silence.php?method=en_silence";
	//对用户取消禁言
	public String CANCEL_BANNER_USER="/silence.php?method=de_silence";
	//意见反馈
	public String FEEDBACK="/transfer.php?method=feed_back";
	//更新积分
	public String UPDATAINTEGR="dzd/api?method=update.vip.integral&ResultType=JSON";
	//新闻列表
	public String NEWSLIST="/index.php?method=get_news_list";
	//新闻详情
	public String NEWSINFO="/index.php?method=get_news_info";
}
