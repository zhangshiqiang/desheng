package com.hanyu.desheng.engine;

/**
 * 鼎充请求URL管理接口----所有接口地址在此接口配置
 * 
 * 
 */
public class DSUrlManager implements DSUrl {
	/**
	 * 网络访问管理类
	 */
	private HttpManager manager;

	/**
	 * 获取 网络访问管理类
	 */
	public HttpManager getHttpManager() {
		if (manager == null) {
			manager = new HttpManager();
		}
		return manager;
	}

	/**
	 * 获得订单完整的访问URL
	 * 
	 * @param url
	 * @return
	 */
	public String getFullUrl(String url) {
		return BASEURL + Token + url;
	}

	/**
	 * 获得订单完整的访问URL
	 * 
	 * @param url
	 * @return
	 */
//	public String getFullUrl3(String url) {
//		return BASEURL3 + Token + url;
//	}

	/**
	 * 获得app其他完整的访问URL
	 * 
	 * @param url
	 * @return
	 */
	public String getFullUrl2(String url) {
		return BASEURL2 + url;
	}
	public String getFullUrl3(String url) {
		return BASEURL3 + url;
	}

	/**
	 * 获得完整的下载路径
	 * 
	 * @param download_path
	 * @return
	 */
	public static String getDownloadUrl(String url) {
		return url;
	}

}
