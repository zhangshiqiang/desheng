package com.hanyu.desheng.engine;

import android.content.Context;
import android.os.AsyncTask;

import com.hanyu.desheng.utils.MyToastUtils;

/**
 * 访问网络的工具
 * 
 * @author sto_LiHui
 * 
 * @param <Params>：传递的参数信息——如用户信息封装
 * @param <Progress>：下载文件——进度提示，提示数据类型（int float）,对于当前的使用环境，设置成Void
 * @param <Result>：返回值类型——封装对象
 */
public abstract class HttpTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	private Context context;

	public HttpTask(Context context) {
		this.context = context;
	}

	/**
	 * 在执行子线程操作之前做网络判断的处理（override一个start）
	 * 
	 * @param params
	 * @return
	 */
	public final AsyncTask<Params, Progress, Result> executeProxy(
			Params... params) {
		if (NetUtil.checkNetWork(context)) {
			return super.execute(params);
		} else {
			noNetWork();
		}
		return null;
	}

	/**
	 * 没有网络时调用的方法
	 */
	public void noNetWork() {
		MyToastUtils.showShortToast(context, "网络不给力");
	}

}
