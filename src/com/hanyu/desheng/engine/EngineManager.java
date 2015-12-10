package com.hanyu.desheng.engine;

/**
 * 业务类对象获取工具类
 */
public class EngineManager {

	private static UserEngine userEngine;
	private static ShopEngine shopEngine;

	// private static MineEngine mineEngine;
	// private static ScanEngine scanEngine;

	/**
	 * 获取用户业务操作对象
	 * 
	 * @return
	 */
	public static UserEngine getUserEngine() {
		if (userEngine == null) {
			userEngine = new UserEngine();
		}
		return userEngine;
	}

	/**
	 * 获取商店业务操作对象
	 * 
	 * @return
	 */
	public static ShopEngine getShopEngine() {
		if (shopEngine == null) {
			shopEngine = new ShopEngine();
		}
		return shopEngine;
	}

}
